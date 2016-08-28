package net.cloudapp.eggfry.frypan;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

//-- 요구 사항 및 수정 필요한 부분
//-- Ctrl-F로 '//--' ㄱㄱ

/**
 * Created by user on 2016-08-20.
 */
public class ResultDialog extends Dialog {

    private int finalScore;
    public ResultDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        setContentView(R.layout.dialog_result);

        int[] score = SocketService.gameManager.getScore();
        int nickNum = SocketService.gameManager.getNickNum();
        String[] nickname = SocketService.gameManager.getArr_nickname();
        int rank = SocketService.gameManager.getRank(score, nickNum);
        finalScore = (3-rank)*5;

        for(int i=0;i<4;i++) {
            int tempRank = SocketService.gameManager.getRank(score, nickNum);
            TextView tv1 = (TextView)findViewById(getContext().getResources().getIdentifier("text_"+(tempRank-1)+"_"+"0", "id", getContext().getPackageName()));
            tv1.setText(nickname[i]);
            TextView tv2 = (TextView)findViewById(getContext().getResources().getIdentifier("text_"+(tempRank-1)+"_"+"1", "id", getContext().getPackageName()));
            tv2.setText(String.valueOf(score[i]));
        }
    }



}

