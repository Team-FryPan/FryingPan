package net.cloudapp.eggfry.frypan;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GameActivity extends AppCompatActivity {
    private SoundManager soundManager;

    public void setSound() {
        soundManager = new SoundManager(this);

        soundManager.loadSound("click", R.raw.buttonclicked);
        soundManager.loadSound("apple", R.raw.game_apple);
        soundManager.loadSound("press", R.raw.game_buttonpressed);
        soundManager.loadSound("four", R.raw.game_kiwi);
        soundManager.loadSound("lemon", R.raw.game_lemon);
        soundManager.loadSound("one", R.raw.game_one);
        soundManager.loadSound("three", R.raw.game_three);
        soundManager.loadSound("two", R.raw.game_two);
        soundManager.loadSound("watermelon", R.raw.game_watermelon);
        soundManager.loadSound("start1", R.raw.start_1);
        soundManager.loadSound("start2", R.raw.start_2);
        soundManager.loadSound("start3", R.raw.start_3);
        soundManager.loadSound("start4", R.raw.start_4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ImageView bg = (ImageView)findViewById(R.id.game_bg);
        ImageView count1 = (ImageView)findViewById(R.id.count1);
        ImageView count2 = (ImageView)findViewById(R.id.count2);
        ImageView count3 = (ImageView)findViewById(R.id.count3);
        ImageView count4 = (ImageView)findViewById(R.id.count4);

        // Image Initialization
        Glide.with(this).load(R.drawable.in_game_bg).into(bg);
        Glide.with(this).load(R.drawable.ting).into(count1);
        Glide.with(this).load(R.drawable.ting).into(count2);
        Glide.with(this).load(R.drawable.tang).into(count3);
        Glide.with(this).load(R.drawable.tang).into(count4);

    }
}
