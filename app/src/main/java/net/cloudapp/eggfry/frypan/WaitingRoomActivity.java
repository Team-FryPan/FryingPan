package net.cloudapp.eggfry.frypan;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class WaitingRoomActivity extends AppCompatActivity {

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
        public void recvData(String str) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = new Intent(this, SocketService.class);
        bindService(intent, mConnection, Context.BIND_NOT_FOREGROUND);
    }
}
