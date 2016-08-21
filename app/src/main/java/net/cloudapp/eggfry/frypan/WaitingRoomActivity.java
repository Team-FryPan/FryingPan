package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.squareup.otto.Subscribe;

public class WaitingRoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_room);


        // 툴바(액션바) 설정
        Intent recvIntent = getIntent();
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(recvIntent.getIntExtra("channel", 0) + "번 채널");
        toolbar.setTitleTextColor(Color.WHITE);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            Log.d("WaitingRoom", "액션바");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 준비 버튼
        ((ToggleButton)findViewById(R.id.ready_btn)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    buttonView.setBackgroundColor(Color.GRAY);
                    BusProvider.getInstance().post(new PushEvent("ReadyButton"));
                } else {
                    buttonView.setBackgroundColor(Color.parseColor("#FF7700"));
                    BusProvider.getInstance().post(new PushEvent("CancelButton"));
                }
            }
        });

        BusProvider.getInstance().register(this);
        BusProvider.getInstance().post(new PushEvent("ReadyRequest"));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
        BusProvider.getInstance().post(new PushEvent("CancelButton"));
        BusProvider.getInstance().post(new PushEvent("Destroy"));
    }

    // 툴바 아이템 선택 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        final String[] messages = mPushEvent.getString().split(" ");
        if(mPushEvent.getString().equals("Set")) {
            Intent it = new Intent(this, GameActivity.class);
            it.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(it);
            BusProvider.getInstance().unregister(this);
        } else if(messages[0].equals("Ready")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView imageView = (ImageView)findViewById(getResources().getIdentifier("icon_"+messages[1], "id", getPackageName()));
                            imageView.setAlpha(1.0f);
                        }
                    });
                }
            }).start();

        } else if(messages[0].equals("Cancel")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            ImageView imageView = (ImageView)findViewById(getResources().getIdentifier("icon_"+messages[1], "id", getPackageName()));
                            imageView.setAlpha(0.3f);
                        }
                    });
                }
            }).start();
        }
    }
}
