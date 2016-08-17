package net.cloudapp.eggfry.frypan;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;

import java.util.ArrayList;

public class MultiPlayActivity extends AppCompatActivity {
    private static final int CHANNEL_NUM = 60;      // 채널 갯수

    private int selectedChannel = 1;

    private String username;
    private int channel;

    private int progressStatus = 0;
    private Handler handler = new Handler();

    private ArrayList<Integer> channelList = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;

    private ProgressDialog loadingDialog;

    private SocketService mService;
    private Intent intent;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { // 서비스와 연결되었을 때
            SocketService.mBinder binder = (SocketService.mBinder) service;
            mService = binder.getService();
            mService.registerCallback(mCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) { // 서비스와 떨어졌을 때
            mService=null;
        }
    };

    private SocketService.ICallback mCallback = new SocketService.ICallback() { // SocketService는 recvData 함수를 호출해서 Activity 작업 하기
        public void recvData(String message) {
            String[] messages = message.split(" ");
            if(message.equals("Connection Fail")) { // 연결 안됨
                loadingDialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(MultiPlayActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("네트워크 문제로 연결할 수 없습니다.");
                alert.show();
                stopService(intent);
            } else if(message.equals("Room Connected")) { // 방에 연결
                loadingDialog.dismiss();
                Intent it = new Intent(MultiPlayActivity.this, WaitingRoomActivity.class);
                it.putExtra("username", username);
                it.putExtra("channel", channel);
                startActivity(it);
            } else if(message.equals("Server Full")) { // 서버 동접 인원수 다 참
                loadingDialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(MultiPlayActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("서버가 가득 찼습니다. 잠시 후 시도해주세요.");
                alert.show();
                stopService(intent);
            } else if(message.equals("Room Full")) { // 같은 방에 들어갈 인원 다 참
                loadingDialog.dismiss();
                AlertDialog.Builder alert = new AlertDialog.Builder(MultiPlayActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                    }
                });
                alert.setMessage("채널이 가득 찼습니다. 다른 방으로 시도해주세요.");
                alert.show();
                stopService(intent);
            }
            else if(messages[0].equals("Username")) { // Username 설정
                username = messages[1];
            } else if(messages[0].equals("Channel")) { // Channel 설정
                channel = Integer.parseInt(messages[1]);
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_play);

        // 1 ~ CHANNEL_NUM까지 배열에 담음
        for(int i=1; i<=CHANNEL_NUM; i++) {
            channelList.add(i);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, channelList);

    }

    public void onRandomBtnClicked(View v) {
        // 로딩 빙글빙글

        loadingDialog = new ProgressDialog(MultiPlayActivity.this);
        loadingDialog.setMessage("적절한 방을 찾는 중입니다...");
        loadingDialog.setCancelable(true);
        setSocketServiceConnection("70");

        loadingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.myServiceFunc("Cancel");
                dialog.dismiss();
                stopService(intent);
            }
        });
        loadingDialog.show();
    }

    public void onSelectBtnClicked(View v) {

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(numberPicker);
        builder.setMessage("채널을 선택해주세요");
        builder.setNeutralButton("입장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                 선택한 채널(selectedChannel이 비었으면 접속, 4명 다 찼으면 Toast 띄워줌)
                setSocketServiceConnection(
                        String.valueOf(numberPicker.getValue()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Cancel 누르면 NullPointerException
                mService.myServiceFunc("Cancel");
                dialogInterface.dismiss();
                stopService(intent);
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    public void setSocketServiceConnection(String channel) {
        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        String username = sp.getString("id", "");

        intent = new Intent(this, SocketService.class);
        intent.putExtra("username", username);
        intent.putExtra("channel", channel);

        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }
}
