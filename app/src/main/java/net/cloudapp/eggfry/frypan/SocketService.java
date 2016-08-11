package net.cloudapp.eggfry.frypan;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016-08-07.
 */
public class SocketService extends Service{

    // 기본 변수
    private String address="http://eggfry.cloudapp.net:6600/";
    private String[] arr_nickname = {"포도", "딸기", "사과", "수박"};
    private int[] score = new int[4];
    private String username, channel;
    private int userNum = -1;
    private int nickNum = -1;
    private int attackCount = -1;

    private boolean isConnected = false;
    private boolean isStarted = false;

    private Timer timer = new Timer();
    private long timeCount=0;

    // 기본 생성자
    public SocketService() {
    }

    private Socket mSocket; {
        try {
            mSocket = IO.socket(address + "?username=" + username + "&channel=" + channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Web Socket으로부터 Message 받기
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println((String)args[0]);
                    proccessResponse((String)args[0]);
                }
            }).start();
        }
    };

    public class mBinder extends Binder {
        SocketService getService() {
            return SocketService.this;
        }
    }

    private final IBinder mBinder = new mBinder();

    @Override
    public IBinder onBind(Intent intent) {
        username = intent.getStringExtra("username");
        channel=intent.getStringExtra("channel");
        System.out.println(username);
        System.out.println(channel);

        mSocket.connect();
        mSocket.emit("fromClient", "Login " + username + " " + channel);
        mSocket.on("toClient", onNewMessage);

        return mBinder;
    }

    public interface ICallback {
        public void recvData();
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback=cb;
    }

    public void myServiceFunc(String message) {
        switch (message) {
            case "Cancel":
                mSocket.emit("fromClient", "Cancel");
                break;
        }

    }

    public void proccessResponse(String response) {
        if(response.equals("Server Connection")) {
            isConnected = true;
        }
        String[] messages = response.split(" ");
        switch (messages[0]) {
            case "Login" :
                this.username = messages[1];
                this.channel = messages[2];
                break;

            case "Set" :
                isStarted = true;
                if(messages[1].equals(username)) {
                    this.nickNum = Integer.parseInt(messages[2]);
                    this.userNum = Integer.parseInt(messages[3]);
                }

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        timeCount++;
                    }
                };
                timer.schedule(task, 1000, 100);

                break;

            case "Report" :
                for(int i=1;i<5;i++) {
                    score[i-1] = Integer.parseInt(messages[i]);
                }
                break;

            case "Result" :
                for(int i=0;i<4;i++) {
                    score[i] = Integer.parseInt(messages[i*2+2]);
                    // Result 처리
                }
                break;

            case "Send" :
                if(nickNum == Integer.parseInt(messages[2])) {
                    attackCount = Integer.parseInt(messages[3]);
                } else {
                    attackCount = 4;
                }

        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // mCallback.recvData();
}
