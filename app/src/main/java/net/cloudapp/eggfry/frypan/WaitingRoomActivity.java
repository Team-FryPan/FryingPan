package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class WaitingRoomActivity extends AppCompatActivity {

    public static boolean isActive = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        isActive = true;

        Intent recvIntent = getIntent();

        // 툴바 설정
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(recvIntent.getStringExtra("channel") + "번 채널");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().post(new PushEvent("Destroy"));
        isActive = false;
    }

    // 준비 버튼 처리
    public void onReadyBtnClicked(View v) {

    }
}
