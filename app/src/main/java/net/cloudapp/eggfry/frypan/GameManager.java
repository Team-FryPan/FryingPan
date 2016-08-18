package net.cloudapp.eggfry.frypan;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016-08-13.
 */
public class GameManager {

    private String[] arr_nickname = {"키위", "레몬", "사과", "수박"};
    private int[] score = new int[4]; // nickNum번째 score이 자신의 score
    private int userNum = -1; // 유저의 고유번호(0번부터 999번)
    private int nickNum = -1; // arr_nickname에서 nickNum번째 nickname이 자신의 nickname
    private int attackCount = -1; // 자신이 이 턴에 후라이팬놀이를 수행할 횟수

    private Timer timer = new Timer(); // 타이머(60초마다 SpeedUp)
    private int speedLevel = 1; // (speedLevel-1)*0.1+1을 계속 곱해줘서 스피드를 맞춤
    private long timeCount=0; // 게임이 시작되면 timeCount를 100millis마다 작동

    public int[] getScore() {
        return score;
    }

    public int getUserNum() {
        return userNum;
    }

    public int getNickNum() {
        return nickNum;
    }

    public long getTimeCount() {
        return timeCount;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public void setScore(int index, int score) {
        this.score[index] = score;
    }

    public void setUserNum(int userNum) {
        this.userNum = userNum;
    }

    public void setNickNum(int nickNum) {
        this.nickNum = nickNum;
    }

    public void setAttackCount(int attackCount) {
        this.attackCount = attackCount;
    }

    public void startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeCount++;
                if(timeCount%600==0) {
                    speedLevel++;
                }
            }
        };
        timer.schedule(task, 1000, 100);
    }
}
