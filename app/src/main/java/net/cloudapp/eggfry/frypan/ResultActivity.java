package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class ResultActivity extends AppCompatActivity {


    public int getRank(int[] score, int nickNum) {
        int rank=1;
        for(int i=0;i<4;i++) {
            if(score[i]>score[nickNum]) {
                rank++;
            }
        }
        return rank;
    }

    int count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
    }

    public void onTouch(View view) {
        System.out.println(count++);
    }

    public void onResultButtonClicked(View view) {
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
}
