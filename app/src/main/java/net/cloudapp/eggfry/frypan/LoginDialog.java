package net.cloudapp.eggfry.frypan;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by swj on 16. 8. 7..
 */
public class LoginDialog extends Dialog {
    public LoginDialog(Context context) {
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

        setContentView(R.layout.dialog_login);

        Button loginBtn = (Button)findViewById(R.id.login_btn);
        Button registerBtn = (Button)findViewById(R.id.register_btn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Handler handler = new Handler();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                EditText ed_id = (EditText)findViewById(R.id.ed_id);
                                EditText ed_pwd = (EditText)findViewById(R.id.ed_pwd);

                                if("".equals(ed_id.getText().toString()) || "".equals(ed_pwd.getText().toString())) {
                                    Toast.makeText(getContext().getApplicationContext()
                                            , "가입을 원하는 ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                                } else {

                                }
                            }
                        });
                    }
                }).start();
            }
        });

    }
}
