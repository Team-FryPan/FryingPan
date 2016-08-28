package net.cloudapp.eggfry.frypan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class MainActivity extends AppCompatActivity implements HttpResponse{

    private LoginDialog loginDialog;

    public static final String VERSION = "0.6";

    private BackGroundMusicManager bgmManager;
    public static SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 배경 이미지
        ImageView bgView = (ImageView)findViewById(R.id.main_bg);
        Glide.with(this).load(R.drawable.bg).into(bgView);

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
        setSound();

    }


    // 버튼 처리
    public void onPlayBtnClicked(View v) {
        soundManager.playSound("click");
        soundManager.loadSound("click", R.raw.buttonclicked);
        Intent it = new Intent(this, MultiPlayActivity.class);
        startActivity(it);
    }

    public void onHowToBtnClicked(View v) {
        soundManager.playSound("click");
        soundManager.loadSound("click", R.raw.buttonclicked);
        showHowtoDialog();
    }

    public void onDevInfoBtnClicked(View v) {
        soundManager.playSound("click");
        soundManager.loadSound("click", R.raw.buttonclicked);
        showDevinfoDialog();
    }

    public void onExitBtnClicked(View v) {
        soundManager.playSound("click");
        soundManager.loadSound("click", R.raw.buttonclicked);
        bgmManager.stop();
        finish();
    }

    public void setSound() {
        soundManager = new SoundManager(this);
        soundManager.loadSound("click", R.raw.buttonclicked);

        bgmManager = new BackGroundMusicManager(this, R.raw.opening);
    }

    public void showDevinfoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this
        builder.setTitle("게임 정보")        // 제목 설정
                .setMessage("버전 : "+VERSION+"\n총괄 : 이지호\n개발자 : 이지호\n개발자 : 신원준\n디자인 : 강지훈")        // 메세지 설정
                .setCancelable(true)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    public void showHowtoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this
        builder.setTitle("설명")        // 제목 설정
                .setMessage("흔히 즐겨하는 후라이팬 놀이입니다. \n팅팅탱탱 박자에 맞춰서 버튼을 누르면 됩니다.\n(단, 한번 틀리면 패배하니 주의하세요)\n자, 그럼 친구 혹은 다른 유저들과 리듬에 몸을 맡겨보세요!!")        // 메세지 설정
                .setCancelable(true)        // 뒤로 버튼 클릭시 취소 가능 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    // 확인 버튼 클릭시 설정
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기
    }

    // 로그인 http 통신 처리
    public void processFinish(String output) {
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
