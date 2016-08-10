package net.cloudapp.eggfry.frypan;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

//private void onLoginBtnClicked(View v) {
//        EditText et_username, et_channel;
//        String username, channel;
//        Button ok;
//private SocketService mService;
//
//private ServiceConnection mConnection = new ServiceConnection() {
//@Override
//public void onServiceConnected(ComponentName name, IBinder service) {
//        SocketService.mBinder binder = (SocketService.mBinder) service;
//        mService = binder.getService();
//        mService.registerCallback(mCallback);
//        }
//
//@Override
//public void onServiceDisconnected(ComponentName name) {
//        mService=null;
//        }
//        };
//
//private SocketService.ICallback mCallback = new SocketService.ICallback() {
//public void recvData() {}
//        };
//
//@Override
//protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        et_username=(EditText)findViewById(R.id.editText);
//        et_channel=(EditText)findViewById(R.id.editText2);
//
//        ok=(Button)findViewById(R.id.button);
//        }
//
//public void onButtonClicked(View view) {
//        username=et_username.getText().toString();
//        channel=et_channel.getText().toString();
//
//        Intent it = new Intent(this, SocketService.class);
//        it.putExtra("username", username);
//        it.putExtra("channel", channel);
//        bindService(it, mConnection, Context.BIND_AUTO_CREATE);
//        }
//
//        // 호출은 mService.myServiceFunc();
//        }

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences("login_info", MODE_PRIVATE);
        // if 기존 로그인 정보 X
        if("".equals(sp.getString("id", ""))
                || "".equals(sp.getString("pwd", ""))) {
            LoginDialog loginDialog = new LoginDialog(this);
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

    // ID 중복 체크(현재 구현 X)
//    private boolean checkID(String input_id) {
//        String address = "http://mascorewebserver.hol.es";
//        String fileName = "InsertUser.php";
//        String username="ABCDcd";
//        String password="a1b1";
//
//        String deviceName= Build.DEVICE;
//        String version=String.valueOf(android.os.Build.VERSION.SDK_INT);
//
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("fileName", fileName);
//        map.put("UserName", username);
//        map.put("Password", password);
//        map.put("version", version);
//        map.put("model", deviceName);
//
//        HttpRequest httpRequest = new HttpRequest();
//        String requestAddress = httpRequest.makeUrl(address, map);
//        httpRequest.getRequest(requestAddress);
//        return true;
//    }
}
