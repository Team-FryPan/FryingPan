package net.cloudapp.eggfry.frypan;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by user on 2016-08-12.
 */
public class SoundManager {

    private Context context;
    private SoundPool soundPool; // Sound가 로딩될 풀
    private HashMap<String, Integer> sounds = new HashMap<>(); // 파일명과 resId로 HashMap
    private HashMap<String, Integer> maps = new HashMap<>(); // 파일명과 resId로 HasnMap(원본 resId)

    public SoundManager(Context context) { // 기본적으로 많이 쓰이는 소리 세팅
        soundPool = new SoundPool(6, AudioManager.STREAM_MUSIC, 0);
        this.context = context;
    }
    public void loadSound(String name) {
        sounds.put(name, soundPool.load(context, maps.get(name), 1));
    }
    public void loadSound(String name, int resid) {
        maps.put(name, resid);
        sounds.put(name, soundPool.load(context, resid, 1));
    }

    public void playSound(String string) { // 자주 쓰이는 소리 재생
        int sound = sounds.get(string);
        soundPool.play(sound, 100, 100, 1, 0, 1f);
    }
}
