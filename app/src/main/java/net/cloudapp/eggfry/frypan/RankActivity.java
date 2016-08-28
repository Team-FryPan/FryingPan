package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

//-- 요구 사항 및 수정 필요한 부분
//-- Ctrl-F로 '//--' ㄱㄱ

public class RankActivity extends AppCompatActivity implements HttpResponse{

    public HttpResponse httpResponse;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        userName = sp.getString("id", "");
        int finalScore = getIntent().getIntExtra("score", -1);

        ImageView bgView = (ImageView)findViewById(R.id.bg_rank);
        Glide.with(this).load(R.drawable.bg_select).asBitmap().into(bgView);



        webconnect(userName, finalScore);
    }

    public void onBackButtonClicked(View view) {
        stopService(MultiPlayActivity.socketIntent);
        Intent intent = new Intent(this, MultiPlayActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private void webconnect(String UserName, int finalScore) {
        String address = "http://mascorewebserver.hol.es/UpdateUser.php?UserName="+UserName+"&Rating="+finalScore;
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.httpResponse = this;
        httpRequest.getRequest(address);
    }

    private void webconnect_getRank(String UserName) {
        String address = "http://mascorewebserver.hol.es/GetRank.php?UserName="+UserName;
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.httpResponse = this;
        httpRequest.getRequest(address);
    }
    // 받아온 문자열 처리
    public void processFinish(String output) {
        final String[] messages = output.split("\n");
        switch(messages[3]) {
            case "Success":
                if(messages[0].contains("UpdateUser.php")) {
                    webconnect_getRank(userName);
                } else if(messages[0].contains("GetRank.php")) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView tv1 = (TextView) findViewById(R.id.text_username);
                            TextView tv2 = (TextView) findViewById(R.id.text_rating);
                            TextView tv3 = (TextView) findViewById(R.id.text_rank);

                            tv1.setText("이름 : " + userName);
                            tv2.setText("레이팅 : " + messages[4]);
                            tv3.setText("랭크 : " + messages[5]);
                        }
                    });
                }
                break;
            case "TryAgain":
                Toast.makeText(this, "어플리케이션을 다시 설치해보세요.", Toast.LENGTH_SHORT).show();
                break;
            case "Fail":
                Toast.makeText(this, "네트워크를 확인해보세요.", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
