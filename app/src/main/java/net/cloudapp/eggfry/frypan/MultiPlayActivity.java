package net.cloudapp.eggfry.frypan;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;

public class MultiPlayActivity extends AppCompatActivity {
    private static final int CHANNEL_NUM = 60;      // 채널 갯수

    private int selectedChannel = 1;

    private int progressStatus = 0;
    private Handler handler = new Handler();

    private ArrayList<Integer> channelList = new ArrayList<>();
    private ArrayAdapter<Integer> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_play);

        // 1 ~ CHANNEL_NUM까지 배열에 담음
        for(int i=1; i<=CHANNEL_NUM; i++) {
            channelList.add(i);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, channelList);



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

        // 여기서 실제 작업(채널 선정), 작업 끝나면 loadingDialog.dismiss();


    }

    public void onSelectBtnClicked(View v) {
        final Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedChannel = (Integer) spinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }


        });



        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(spinner);
        builder.setMessage("채널을 선택해주세요");
        builder.setNeutralButton("입장", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // 선택한 채널(selectedChannel이 비었으면 접속, 4명 다 찼으면 Toast 띄워줌)
                Log.d("asdf", String.valueOf(selectedChannel));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.create().show();
    }
}
