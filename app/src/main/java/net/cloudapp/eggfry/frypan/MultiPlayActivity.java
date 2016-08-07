package net.cloudapp.eggfry.frypan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MultiPlayActivity extends AppCompatActivity {
    private int progressStatus = 0;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_play);
    }

    public void onRandomBtnClicked(View v) {
        // 로딩 빙글빙글
        ProgressDialog loadingDialog;

        loadingDialog = new ProgressDialog(MultiPlayActivity.this);
        loadingDialog.setMessage("적절한 방을 찾는 중입니다...");
        loadingDialog.setCancelable(true);
        loadingDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        loadingDialog.show();

        // 여기서 실제 작업, 작업 끝나면 loadingDialog.dismiss();





    }

    public void onSelectBtnClicked(View v) {

    }
}
