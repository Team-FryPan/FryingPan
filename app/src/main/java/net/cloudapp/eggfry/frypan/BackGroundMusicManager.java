package net.cloudapp.eggfry.frypan;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 2016-08-22.
 */
public class BackGroundMusicManager {
    MediaPlayer mediaPlayer;

    public BackGroundMusicManager(final Context context, final int id) {
        try {
            final Timer timer = new Timer(); // 타이머를 이용한 지속적인 변수 할당(버그 수정)
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    if (mediaPlayer != null) {
                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                        mediaPlayer.setLooping(true);
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        timer.cancel();
                    }
                    mediaPlayer = MediaPlayer.create(context, id);
                }
            }, 0, 100);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
