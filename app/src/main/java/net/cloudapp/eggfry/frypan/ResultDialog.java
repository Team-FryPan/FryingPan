package net.cloudapp.eggfry.frypan;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by user on 2016-08-20.
 */
public class ResultDialog extends Dialog {

    public ResultDialog(Context context) {
        super(context);
    }

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

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.dialog_result);

        int[] score = SocketService.gameManager.getScore();
        int nickNum = SocketService.gameManager.getNickNum();
        String[] nickname = SocketService.gameManager.getArr_nickname();
        String[] str_rank = new String[]{"1st", "2nd", "3rd", "4th"};
        int rank = getRank(score, nickNum);
        int finalScore = (3-rank)*5;

        ListView listView = (ListView)findViewById(R.id.result_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getOwnerActivity().getApplication(), R.layout.support_simple_spinner_dropdown_item);
        for(int i=0;i<4;i++) {
            if(score[i]==-1) {
                score[i]=0;
            }
            arrayAdapter.add(str_rank[i]+" "+nickname[i]+" "+score[i]);
        }
        listView.setAdapter(arrayAdapter);
    }

//    public void processFinish(String output) {
//        switch(output) {
//            case "Success" :
//                break;
//            case "TryAgain" :
//                Toast.makeText(this, )
//                break;
//            case ""
//        }
//    }
}
