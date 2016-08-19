package net.cloudapp.eggfry.frypan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity implements HttpResponse{

    private LoginDialog loginDialog;
    public static BackGroundMusicManager bgmManager;
    public static SoundManager soundManager;
    public void setSound() {
        soundManager = new SoundManager(this);

        soundManager.loadSound("click", R.raw.buttonclicked); 

        bgmManager = new BackGroundMusicManager(this, R.raw.opening);
        bgmManager.play();
    }

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

        // 소리 세팅
        bgmManager = new BackGroundMusicManager(this, R.raw.opening);
        soundManager = new SoundManager(this);
        setSound();

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() { // 3초 뒤 BGM 재생
            @Override
            public void run() {
                bgmManager.play();
            }
        }, 3000);




    }

    public void onPlayBtnClicked(View v) {
        soundManager.playSound("click");
        Intent it = new Intent(this, MultiPlayActivity.class);
        startActivity(it);
    }

    public void onHowToBtnClicked(View v) {
        soundManager.playSound("click");
        Intent it = new Intent(this, HowToActivity.class);
        startActivity(it);
    }

    public void onDevInfoBtnClicked(View v) {
        soundManager.playSound("click");
        Intent it = new Intent(this, DevInfoActivity.class);
        startActivity(it);
    }

    public void onExitBtnClicked(View v) {
        soundManager.playSound("click");
        bgmManager.stop();
        finish();
    }

    public void onSoundToggleClicked(View v) { // SoundToggle이 클릭되었을 때
        if(((ToggleButton)v).isChecked()) {
            bgmManager.play();
            soundManager.enableSound(true);
        } else {
            bgmManager.stop();
            soundManager.enableSound(false);
        }
    }


    public void processFinish(String output) { // 로그인 http 통신 처리
        String[] messages = output.split("\n");
        switch(messages[3]) {
            case "Success" : // 성공 메세지
                if (messages[0].contains("InsertUser.php") || messages[0].contains("CheckUser.php")) {
                    if (messages[0].contains("InsertUser.php")) {
                        Toast.makeText(this, "가입이 완료되었습니다. ", Toast.LENGTH_SHORT).show();
                    } else if (messages[0].contains("CheckUser.php")) {
                        Toast.makeText(this, "로그인 성공했습니다. ", Toast.LENGTH_SHORT).show();
                    }
                    loginDialog.dismiss();
                    SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("id", messages[1]);
                    editor.putString("pwd", messages[2]);
                    editor.apply();
                }

                break;
            case "TryAgain" : // 실패 메세지
                if(messages[0].contains("InsertUser.php")) {
                    Toast.makeText(this, "중복된 ID입니다. 다시 입력해주세요.", Toast.LENGTH_SHORT).show();
                } else if(messages[0].contains("CheckUser.php")) {
                    Toast.makeText(this, "아이디 혹은 비밀번호를 확인해주세요. ", Toast.LENGTH_SHORT).show();
                }

                break;
            case "Fail" : // 오류 메세지
                Toast.makeText(this, "오류입니다. 네트워크를 확인해주세요. ", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
