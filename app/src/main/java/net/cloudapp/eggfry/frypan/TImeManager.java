package net.cloudapp.eggfry.frypan;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by swj on 16. 8. 26..
 */
public abstract class TimeManager {
    private static final int EXPECT_TIMING = 400;               // 성공 기준
    private static final int TIMING_INTERVAL = 50;              // 점수 체크 타이밍 간격(Perfect, Great, Good)

    public static final int PERFECT = 0;
    public static final int GREAT = 1;
    public static final int GOOD = 2;
    public static final int FAIL = 3;

    public static int timingNum = 0;                            // Count Timing 0 ~ 3

    public static long beatStartMillis, beatEndMillis;

    public static int checkScore() {
        long timing = beatStartMillis - beatEndMillis;

        Log.d("Timing", String.valueOf(timing));

        if(timing <= EXPECT_TIMING + TIMING_INTERVAL
                && timing >= EXPECT_TIMING - TIMING_INTERVAL )
            return PERFECT;

        else if(timing <= EXPECT_TIMING + 2*TIMING_INTERVAL
                && timing >= EXPECT_TIMING - 2*TIMING_INTERVAL )
            return GREAT;

        else if(timing <= EXPECT_TIMING + 3*TIMING_INTERVAL
                && timing >= EXPECT_TIMING - 3*TIMING_INTERVAL )
            return GOOD;

        else
            return FAIL;
    }
}
