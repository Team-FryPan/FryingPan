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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

//-- 요구 사항 및 수정 필요한 부분
//-- Ctrl-F로 '//--' ㄱㄱ

public class MultiPlayActivity extends AppCompatActivity {
    private static final int CHANNEL_NUM = 60;      // 채널 갯수

    private int selectedChannel = 1;

    private String username;
    private int channel;

    private SoundManager soundManager;
    private int progressStatus = 0;
    private Handler handler = new Handler();

    private ArrayList<Integer> channelList = new ArrayList<>();

    private ProgressDialog loadingDialog;

    private SocketService mService;
    public static Intent socketIntent;

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
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageButton b1 = (ImageButton)findViewById(R.id.random_btn);
                            ImageButton b2 = (ImageButton)findViewById(R.id.select_btn);

                            b1.setBackgroundResource(R.drawable.egg_random);
                            b2.setBackgroundResource(R.drawable.egg_select);
                        }
                    });
                }
            }).start();

            String[] messages = message.split(" ");
            if(message.equals("Connection Fail")) { // 연결 안됨
                if(loadingDialog != null)
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
                unbindService(mConnection);
                stopService(socketIntent);

            } else if(message.equals("Room Connected")) { // 방에 연결
                if(loadingDialog != null)
                    loadingDialog.dismiss();
                Intent it = new Intent(MultiPlayActivity.this, WaitingRoomActivity.class);
                it.putExtra("username", username);
                it.putExtra("channel", channel);
                it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                startActivity(it);
            } else if(message.equals("Server Full")) { // 서버 동접 인원수 다 참
                if(loadingDialog != null)
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
                unbindService(mConnection);
                stopService(socketIntent);
            } else if(message.equals("Room Full")) { // 같은 방에 들어갈 인원 다 참
                if(loadingDialog != null)
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
                unbindService(mConnection);
                stopService(socketIntent);

            } else if(messages[0].equals("Username")) { // Username 설정
                username = messages[1];

            } else if(messages[0].equals("Channel")) { // Channel 설정
                channel = Integer.parseInt(messages[1]);

            } else if(messages[0].equals("Set")) { // 게임 시작
                BusProvider.getInstance().post(new PushEvent("Set"));
            } else if(messages[0].equals("Ready")) { // 레디
                BusProvider.getInstance().post(new PushEvent(message));
            } else if(messages[0].equals("Cancel")) { // 취소 신호
                BusProvider.getInstance().post(new PushEvent(message));
            } else if(messages[0].equals("Select")) { // 공격 대상 핑 맞추기
                BusProvider.getInstance().post(new PushEvent("Select"));
            } else if(messages[0].equals("Number")) { // 공격 횟수 핑 맞추기
                BusProvider.getInstance().post(new PushEvent("Number"));
            } else if(messages[0].equals("Report")) { // 점수 보고
                BusProvider.getInstance().post(new PushEvent(message));
            } else if(messages[0].equals("Result")) { // 게임 종료
                BusProvider.getInstance().post(new PushEvent("Result"));
            } else if(messages[0].equals("Send")) { // Send 핑 맞춰주기
                BusProvider.getInstance().post(new PushEvent("Send"));
            } else if(messages[0].equals("Defend")) { // Defend 핑 맞추기
                BusProvider.getInstance().post(new PushEvent(message));
            } else if(messages[0].equals("GameStart")) {
                BusProvider.getInstance().post(new PushEvent("GameStart"));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_play);

        ImageView bg = (ImageView)findViewById(R.id.bg_multi_play);
        Glide.with(this).load(R.drawable.bg_select).into(bg);

        setSound();
        // 1 ~ CHANNEL_NUM까지 배열에 담음
        for(int i=1; i<=CHANNEL_NUM; i++) {
            channelList.add(i);
        }

        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, channelList);

        BusProvider.getInstance().register(this);

    }

    //
    // 버튼 처리
    //

    public void onRandomBtnClicked(View v) {

        final ImageButton b = (ImageButton)findViewById(R.id.random_btn);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.setBackgroundResource(R.drawable.egg2);
            }
        }, 400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.setBackgroundResource(R.drawable.eggfry);
            }
        }, 800);

        soundManager.playSound("click");
        soundManager.loadSound("click", R.raw.buttonclicked);

        // 로딩 빙글빙글
        loadingDialog = new ProgressDialog(MultiPlayActivity.this);
        loadingDialog.setMessage("적절한 방을 찾는 중입니다...");
        loadingDialog.setCancelable(true);
        setSocketServiceConnection("70");

        loadingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                b.setBackgroundResource(R.drawable.egg_random);
                unbindService(mConnection);
                stopService(socketIntent);
            }
        });
        loadingDialog.show();

    }

    public void onSelectBtnClicked(View v) {

        final ImageButton b = (ImageButton)findViewById(R.id.select_btn);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.setBackgroundResource(R.drawable.egg2);
            }
        }, 400);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                b.setBackgroundResource(R.drawable.eggfry);
            }
        }, 800);

        soundManager.playSound("click");
        soundManager.loadSound("click", R.raw.buttonclicked);

        final NumberPicker numberPicker = new NumberPicker(this);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(60);

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(numberPicker);
        builder.setMessage("채널을 선택해주세요");
        builder.setNeutralButton("입장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                setSocketServiceConnection(
                        String.valueOf(numberPicker.getValue()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                b.setBackgroundResource(R.drawable.egg_select);
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }

    // 소켓
    public void setSocketServiceConnection(String channel) {
        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        String username = sp.getString("id", "");

        socketIntent = new Intent(this, SocketService.class);
        socketIntent.putExtra("username", username);
        socketIntent.putExtra("channel", channel);

        bindService(socketIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    // 음향
    public void setSound() {
        soundManager = new SoundManager(this);

        soundManager.loadSound("click", R.raw.buttonclicked);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        String[] array = mPushEvent.getString().split(" ");
        if(mPushEvent.getString().equals("Destroy")) {
        } else if(mPushEvent.getString().equals("ReadyButton")) {
            mService.myServiceFunc("Ready");
        } else if(mPushEvent.getString().equals("CancelButton")) {
            mService.myServiceFunc("Cancel");
        } else if(mPushEvent.getString().equals("ReadyRequest")) {
            mService.myServiceFunc("ReadyRequest");
        } else if(mPushEvent.getString().equals("LoseButton")) {
            mService.myServiceFunc("Lose");
        } else if(array[0].equals("SendButton")) {
            mService.myServiceFunc(mPushEvent.getString());
        } else if(array[0].equals("DefendButton")) {
            mService.myServiceFunc(mPushEvent.getString());
        } else if(array[0].equals("SelectButton")) {
            mService.myServiceFunc(mPushEvent.getString());
        } else if(array[0].equals("NumberButton")) {
            mService.myServiceFunc(mPushEvent.getString());
        } else if(array[0].equals("GameStartButton")) {
            mService.myServiceFunc(mPushEvent.getString());
        }
    }
}
