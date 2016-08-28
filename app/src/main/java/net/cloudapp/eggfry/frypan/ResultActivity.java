package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.otto.Subscribe;

import java.util.Timer;
import java.util.TimerTask;

public class ResultActivity extends AppCompatActivity {

    private FrameLayout ll;
    private Handler handler = new Handler();
    private TextView tv, tv2;
    private int time=0;
    private boolean isEnd = false;
    private int count=0;

    public int getRank(int[] score, int nickNum) {
        int rank=1;
        for(int i=0;i<4;i++) {
            if(score[i]>score[nickNum]) {
                rank++;
            }
        }
        return rank;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        BusProvider.getInstance().register(this);

        ImageView bg = (ImageView)findViewById(R.id.back);
        Glide.with(this).load(R.drawable.back).into(bg);

        ll = (FrameLayout)findViewById(R.id.result_layout);
        tv = (TextView)findViewById(R.id.text_score);
        tv2 = (TextView)findViewById(R.id.text_countdown);

        if(SocketService.gameManager.getScore()[SocketService.gameManager.getNickNum()] == -1) {
            isEnd = true;
            Toast.makeText(this, "패배했습니다. 추가 점수를 획득할 수 없습니다.", Toast.LENGTH_SHORT).show();
        }
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                time++;
                if(time==50) {
                    sendScore();
                    timer.cancel();
                    isEnd = true;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv2.setText(((float) time / 10.0) + "초");
                    }
                });
            }
        }, 2000, 100);
    }
    public void sendScore() {
        BusProvider.getInstance().post(new PushEvent("IndianButton " + count));
    }
    public void gotoRankActivity() {
        Intent it = new Intent(this, RankActivity.class);
        int score[] = SocketService.gameManager.getScore();
        int nickNum = SocketService.gameManager.getNickNum();
        int rank = SocketService.gameManager.getRank(score, nickNum);
        int finalScore = (3-rank)*5;
        it.putExtra("score", finalScore);
        startActivity(it);
    }

    public void onFinished() { // 인디언밥 끝났을 때
        ResultDialog resultDialog = new ResultDialog(this);
        resultDialog.setCanceledOnTouchOutside(false);
        resultDialog.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && !isEnd) {
            tv.setText("점수 : " + count++);
        }
        return true;
    }

    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        if(mPushEvent.getString().equals("IndianReport")) {
            onFinished();
        }
    }
}
