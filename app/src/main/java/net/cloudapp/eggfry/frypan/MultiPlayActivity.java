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
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;

import java.util.ArrayList;

public class MultiPlayActivity extends AppCompatActivity {
    private static final int CHANNEL_NUM = 60;      // 채널 갯수

    private int selectedChannel = 1;

    private int progressStatus = 0;
    private Handler handler = new Handler();

    private ArrayList<Integer> channelList = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;

    private SocketService mService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SocketService.mBinder binder = (SocketService.mBinder) service;
            mService = binder.getService();
            mService.registerCallback(mCallback);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService=null;
        }
    };

    private SocketService.ICallback mCallback = new SocketService.ICallback() {
        public void recvData() {

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
        ProgressDialog loadingDialog;

        loadingDialog = new ProgressDialog(MultiPlayActivity.this);
        loadingDialog.setMessage("적절한 방을 찾는 중입니다...");
        loadingDialog.setCancelable(true);
        setSocketServiceConnection("70");

        loadingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mService.myServiceFunc("Cancel");
                dialog.dismiss();
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
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    public void setSocketServiceConnection(String channel) {
        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        String username = sp.getString("id", "");

        Intent it = new Intent(this, SocketService.class);
        it.putExtra("username", username);
        it.putExtra("channel", channel);

        bindService(it, mConnection, Context.BIND_AUTO_CREATE);
    }
}
