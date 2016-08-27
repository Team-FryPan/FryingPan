package net.cloudapp.eggfry.frypan;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016-08-13.
 */
public class GameManager {

    private final String[] arr_nickname = {"키위", "레몬", "사과", "수박"};
    private int[] score = new int[4];                                       // nickNum번째 score이 자신의 score
    private int userNum = -1;                                               // 유저의 고유번호(0번부터 999번)
    private int nickNum = -1;                                               // arr_nickname에서 nickNum번째 nickname이 자신의 nickname
    private int attackCount = -1;                                           // 자신이 이 턴에 후라이팬놀이를 수행할 횟수
    private int attackTarget = -1;                                          // 공격당하는 대상
    private boolean isMyTurn = false;                                      // 자신의 턴인지 체크

    private int[] arr_drawableId =
            {R.drawable.kiwi, R.drawable.lemon, R.drawable.apple, R.drawable.watermelon};

    private Timer timer = new Timer();                                      // 타이머(60초마다 SpeedUp)
    private int speedLevel = 1;                                             // (speedLevel-1)*0.1+1을 계속 곱해줘서 스피드를 맞춤
    private long timeCount=0;                                               // 게임이 시작되면 timeCount를 100millis마다 작동

    // Getter
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

    public int[] getArr_drawableId() {
        return arr_drawableId;
    }

    public boolean getIsMyTurn() {
        return isMyTurn;
    }

    public String getMyNickName() {
        return arr_nickname[userNum];
    }

    public int getAttackTarget() {
        return attackTarget;
    }


    // Setter
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

    public String[] getArr_nickname() {
        return arr_nickname;
    }

    public void setIsMyTurn(boolean bool) {
        isMyTurn = bool;
    }

    public void setAttackTarget(int target) {
        attackTarget = target;
    }

    public void startTimer() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                timeCount++;
                if(timeCount%10 == 0) {
                    BusProvider.getInstance().post(new PushEvent("Time " + timeCount/10));
                }
            }
        };
        timer.schedule(task, 1000, 100);
    }

    public int getRank(int[] score, int nickNum) {
        int rank=1;
        for(int i=0;i<4;i++) {
            if(score[i]>score[nickNum]) {
                rank++;
            }
        }
        return rank;
    }
}
