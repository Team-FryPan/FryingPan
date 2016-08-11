package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements HttpResponse{

    private LoginDialog loginDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);

        // if 기존 로그인 정보 X
        if("".equals(sp.getString("id", ""))
                || "".equals(sp.getString("pwd", ""))) {
            loginDialog = new LoginDialog(this);
            loginDialog.httpResponse = this;
            loginDialog.activity = this;
            loginDialog.setCanceledOnTouchOutside(false);
            loginDialog.show();

        }
    }

    public void onPlayBtnClicked(View v) {
        Intent it = new Intent(this, MultiPlayActivity.class);

        startActivity(it);
    }

    public void onHowToBtnClicked(View v) {
        Intent it = new Intent(this, HowToActivity.class);

        startActivity(it);
    }

    public void onDevInfoBtnClicked(View v) {
        Intent it = new Intent(this, DevInfoActivity.class);
        startActivity(it);
    }

    public void onExitBtnClicked(View v) {
        finish();
    }


    public void processFinish(String output) {
        String[] messages = output.split("\n");
        switch(messages[3]) {
            case "Success" :
                if (messages[0].indexOf("InsertUser.php")!=-1 || messages[0].indexOf("CheckUser.php")!=-1) {
                    if (messages[0].indexOf("InsertUser.php")!=-1) {
                        Toast.makeText(this, "가입이 완료되었습니다. ", Toast.LENGTH_SHORT).show();
                    } else if (messages[0].indexOf("CheckUser.php")!=-1) {
                        Toast.makeText(this, "로그인 성공했습니다. ", Toast.LENGTH_SHORT).show();
                    }
                    loginDialog.dismiss();
                    SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("id", messages[1]);
                    editor.putString("pwd", messages[2]);
                    editor.commit();
                }

                break;
            case "TryAgain" :
                if(messages[0].indexOf("InsertUser.php")!=-1) {
                    Toast.makeText(this, "중복된 ID입니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(messages[0].indexOf("CheckUser.php")!=-1) {
                    Toast.makeText(this, "아이디 혹은 비밀번호를 확인해주세요. ", Toast.LENGTH_SHORT).show();
                }

                break;
            case "Fail" :
                Toast.makeText(this, "오류입니다. 네트워크를 확인해주세요. ", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
