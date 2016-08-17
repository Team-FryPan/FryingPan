package net.cloudapp.eggfry.frypan;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

/**
 * Created by user on 2016-08-07.
 */
public class SocketService extends Service{

    private String username, channel; // 유저의 이름과 채널

    private boolean isConnected = false; // 서버와 연결되었는지
    private boolean isStarted = false; // 게임이 시작되었는지

    public static GameManager gameManager; // 게임을 컨트롤하는 클래스

    // 기본 생성자
    public SocketService() {}

    private Socket mSocket; {
        try {
            String address = "http://eggfry.cloudapp.net:6600/";
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

    // Web Socket으로부터 Message 받기
    private Emitter.Listener onDisconnected = new Emitter.Listener() {
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

    // Activity와 연결되었을 때
    @Override
    public IBinder onBind(Intent intent) {
        username = intent.getStringExtra("username");
        channel = intent.getStringExtra("channel");

        System.out.println(username);
        System.out.println(channel);

        mSocket.connect();
        mSocket.emit("fromClient", "Login " + username + " " + channel); // emit 두번째 인자에 메세지를 담음
        mSocket.on("toClient", onNewMessage); // on으로 메세지를 받음
        mSocket.on("disconnected", onDisconnected);

        Handler mHandler = new Handler(Looper.getMainLooper());
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isConnected) { // 연결이 안되었을 때
                    mSocket.close();
                    mCallback.recvData("Connection Fail");
                }
            }
        }, 3000);

        gameManager = new GameManager(); // 게임매니저 선언
        return mBinder;
    }

    @Override
    public void onDestroy() {
        mSocket.close();
    }

    public interface ICallback { // Activity로부터 함수를 호출받을 수 있는 Callback
        public void recvData(String string);
    }

    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback=cb;
    }

    public void myServiceFunc(String message) { // Activity로부터 메세지를 받음
        switch (message) {
            case "Cancel": // Activity에서 중간에 Cancel을 눌렀을 때
                mSocket.emit("fromClient", "Cancel"); // SocketServer에 Cancel을 보냄
                mSocket.close();
                break;
        }

    }

    public void proccessResponse(String response) { // SocketServer로부터 명령을 받음
        if(response.equals("Server Connection")) { // Server와 연결되었을 때
            isConnected = true; // 연결됨
        }
        String[] messages = response.split(" ");
        switch (messages[0]) {
            case "Login" : // 처음에 채널을 선택하거나 랜덤으로 방에 들어갔을 때
                this.username = messages[1];
                this.channel = messages[2];

                mCallback.recvData("Username "+username);
                mCallback.recvData("Channel "+channel);
                mCallback.recvData("Room Connected");

                break;

            case "Set" : // 게임이 시작되었을 때 (자신의 정보를 모두 저장)
                isStarted = true;
                if(messages[1].equals(username)) {
                    gameManager.setNickNum(Integer.parseInt(messages[2]));
                    gameManager.setUserNum(Integer.parseInt(messages[3]));
                }
                gameManager.startTimer();
                mCallback.recvData("Set");

                break;

            case "Report" : // 점수를 보고
                for(int i=1;i<5;i++) {
                    gameManager.setScore(i-1, Integer.parseInt(messages[i]));
                }
                break;

            case "Result" : // 게임이 끝났을 때 결과 보고(점수로 표현, 진 사람은 -1)
                for(int i=0;i<4;i++) {
                    gameManager.setScore(i, Integer.parseInt(messages[i*2+2]));
                    // Result 처리
                }
                break;

            case "Send" : // 누가 누구에게 공격
                if(gameManager.getNickNum() == Integer.parseInt(messages[2])) { // 자신이 공격당하면
                    gameManager.setAttackCount(Integer.parseInt(messages[3])); // attackCount만큼 후라이팬 놀이 수행
                } else { // 기본적으로
                    gameManager.setAttackCount(4); // attackCount는 4
                }
            case "Error" :
                if(messages[1].equals("1001")) { // 서버 동접 수용 인원 다 참
                    mCallback.recvData("Server Full");
                } else if(messages[1].equals("1002")) { // 방 인원 다 참
                    mCallback.recvData("Room Full");
                }
        }
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

}
