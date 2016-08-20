package net.cloudapp.eggfry.frypan;

import android.content.Context;
import android.media.MediaPlayer;


/**
 * Created by user on 2016-08-12.
 */
public class BackGroundMusicManager {

    MediaPlayer mediaPlayer; // 음악 플레이어

    public BackGroundMusicManager(Context context, int id) { // 음악 플레이어 생성
        // 안드로이드 4.4에서 테스트 시 null 값 나옴
        mediaPlayer = MediaPlayer.create(context, id);

        mediaPlayer.setLooping(true);
    }

    public void play() { // 음악 재생
        mediaPlayer.start();
    }

    public void stop() { // 음악 정지
        mediaPlayer.pause();
    }

}
