package net.cloudapp.eggfry.frypan;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by swj on 16. 8. 7..
 */
public class LoginDialog extends Dialog {

    public HttpResponse httpResponse;
    public Activity activity;

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

        ((Button)findViewById(R.id.login_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditText ed_id = (EditText) findViewById(R.id.ed_id);
                        EditText ed_pwd = (EditText) findViewById(R.id.ed_pwd);

                        if ("".equals(ed_id.getText().toString()) || "".equals(ed_pwd.getText().toString())) {
                            Toast.makeText(getContext(), "ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                        webconnect("CheckUser.php", ed_id.getText().toString(), ed_pwd.getText().toString());
                    }
                });
            }
        });

        ((Button)findViewById(R.id.register_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        EditText ed_id = (EditText)findViewById(R.id.ed_id);
                        EditText ed_pwd = (EditText)findViewById(R.id.ed_pwd);

                        if("".equals(ed_id.getText().toString()) || "".equals(ed_pwd.getText().toString())) {
                            Toast.makeText(getContext(), "가입을 원하는 ID와 비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
                        }
                        webconnect("InsertUser.php", ed_id.getText().toString(), ed_pwd.getText().toString());
                    }
                });
            }
        });


    }

    private void webconnect(String fileName, String input_id, String input_pw) {
        String address = "http://mascorewebserver.hol.es";
        String username = input_id;
        String password = input_pw;

        String deviceName= Build.DEVICE;
        String version=String.valueOf(android.os.Build.VERSION.SDK_INT);

        Map<String, String> map = new HashMap<String, String>();
        map.put("fileName", fileName);
        map.put("UserName", username);
        map.put("Password", password);
        map.put("version", version);
        map.put("model", deviceName);

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.httpResponse = this.httpResponse;
        String requestAddress = httpRequest.makeUrl(address, map);
        httpRequest.getRequest(requestAddress);
    }
}
