package net.cloudapp.eggfry.frypan;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by user on 2016-08-12.
 */
public class SoundManager {

    private SoundPool soundPool; // Sound가 로딩될 풀
    private HashMap<String, Integer> sounds = new HashMap<>(); // 파일명과 resId로 HashMap
    private boolean isEnabled = true; // 소리 재생 가능인지

    public SoundManager(Context context, int resId1, int resId2) { // 기본적으로 많이 쓰이는 소리 세팅
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        sounds.put("click", soundPool.load(context, resId1, 1));
        sounds.put("press", soundPool.load(context, resId2, 1));
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() { // 자주 쓰이는 소리가 아니라면
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                if(sampleId != sounds.get("click") && sampleId != sounds.get("press")) {
                    soundPool.play(sampleId, 100, 100, 1, 0, 1f);
                }
            }
        });
    }
    public void playSoundWithoutLoad(String string) { // 자주 쓰이는 소리 재생
        if(isEnabled) {
            int sound = sounds.get(string);
            soundPool.play(sound, 100, 100, 1, 0, 1f);
        }
    }

    public void playSound(Context context, int resId) {
        if(isEnabled) {
            int sound = soundPool.load(context, resId, 1);
        }
    }

    public void enableSound(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
}
