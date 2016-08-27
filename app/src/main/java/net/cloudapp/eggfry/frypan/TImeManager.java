package net.cloudapp.eggfry.frypan;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by swj on 16. 8. 26..
 */
public abstract class TimeManager {

    public static int timing = 500;

    public static int btnCreatedTime;
    public static int btnClickedTime;

    public static Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // Task
            // 시간, 타이밍
            Timer timer = new Timer();
            long startTime = System.currentTimeMillis();

            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                }
            };
            timer.schedule(task, 1000, 100);

        }
    };

    public static void start() {
        Thread thread = new Thread(runnable);

    }

    public static void reset() {

    }

    public static void onSuccess() {

    }

    public static void onFail() {

    }
}

