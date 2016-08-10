package net.cloudapp.eggfry.frypan;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by user on 2016-08-07.
 */
public class SocketService extends Service{

    // 기본 변수
    private String address;
    private String username, channel;

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

    // Activity로부터 Message 받기
    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println((String)args[0]);
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

    public void myServiceFunc() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    // mCallback.recvData();
}
