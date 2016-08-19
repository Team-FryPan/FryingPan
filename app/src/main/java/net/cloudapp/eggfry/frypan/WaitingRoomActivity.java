package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.squareup.otto.Subscribe;

public class WaitingRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        BusProvider.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        BusProvider.getInstance().post(new PushEvent("Destroy"));
    }

    // 준비 버튼 처리
    public void onReadyBtnClicked(View v) {
        BusProvider.getInstance().post(new PushEvent("Ready"));
    }

    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        if(mPushEvent.getString().equals("Set")) {
            Intent it = new Intent(this, GameActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(it);
            BusProvider.getInstance().unregister(this);
        }
    }
}
