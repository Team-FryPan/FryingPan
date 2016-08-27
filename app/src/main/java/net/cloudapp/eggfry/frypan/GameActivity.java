package net.cloudapp.eggfry.frypan;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GameActivity extends AppCompatActivity {
    private SoundManager soundManager;
    private static final int INITIALTIME = 3000;
    private static final int DURATION = 1336;
    private static final int DURATION_SMALL = 334;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setSound();

        BusProvider.getInstance().register(this);

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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }



    public void onUpImgClicked(View v) {
        // 애니메이션 테스트(지워도 상관 X)

        ImageView count1 = (ImageView)findViewById(R.id.count1);
        ImageView count2 = (ImageView)findViewById(R.id.count2);
        ImageView count3 = (ImageView)findViewById(R.id.count3);
        ImageView count4 = (ImageView)findViewById(R.id.count4);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.in);

        count1.startAnimation(animation);
        count2.startAnimation(animation);
        count3.startAnimation(animation);
        count4.startAnimation(animation);
    }

    public void onLeftImgClicked(View v) {

    }

    public void onRightImgClicked(View v) {

    }





    public float speedCalculator(int speed) {
        return (float)((100-speed)/100);
    }

    public void setSound() {
        soundManager = new SoundManager(this);

        soundManager.loadSound("click", R.raw.buttonclicked);
        soundManager.loadSound("press", R.raw.game_buttonpressed);
        soundManager.loadSound("apple", R.raw.game_apple);
        soundManager.loadSound("kiwi", R.raw.game_kiwi);
        soundManager.loadSound("lemon", R.raw.game_lemon);
        soundManager.loadSound("watermelon", R.raw.game_watermelon);
        soundManager.loadSound("one", R.raw.game_one);
        soundManager.loadSound("two", R.raw.game_two);
        soundManager.loadSound("three", R.raw.game_three);
        soundManager.loadSound("four", R.raw.game_four);
        soundManager.loadSound("start1", R.raw.start_1);
        soundManager.loadSound("start2", R.raw.start_2);
        soundManager.loadSound("start3", R.raw.start_3);
        soundManager.loadSound("start4", R.raw.start_4);
        soundManager.loadSound("drum", R.raw.drumbit);
    }
    public void initialPlaySound() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                System.out.println("Start");
            }
        }, INITIALTIME);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound("start1");
                System.out.println("1");
            }
        }, INITIALTIME + DURATION);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound("start2");
                System.out.println("2");
            }
        }, INITIALTIME + DURATION * 2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound("start3");
                System.out.println("3");
            }
        }, INITIALTIME + DURATION * 3);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound("start4");
                System.out.println("4");
            }
        }, INITIALTIME + DURATION * 4);
    }
    public String numberToString(int number) {
        String number_string = "";
        switch(number) {
            case 1:
                number_string = "one";
                break;
            case 2:
                number_string = "two";
                break;
            case 3:
                number_string = "three";
                break;
            case 4:
                number_string = "four";
                break;
            default:
                number_string = "";
                break;
        }
        return number_string;
    }

    /**
     *
     * @param initialTime 초기 딜레이 시간
     * @param speed 스피드(0부터 1까지의 값)
     * @param fruit 과일 이름
     * @param number 수행 갯수
     */
    public void playSound(int initialTime, float speed, final String fruit, int number) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound(fruit);
                soundManager.loadSound(fruit);
            }
        }, initialTime + (int)(DURATION_SMALL * 2 * speed));
        final String number_string = numberToString(number);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound(number_string);
                soundManager.loadSound(number_string);
                System.out.println(number_string);
            }
        }, initialTime + (int)(DURATION_SMALL * 3 * speed));
        for(int i=3-number;i<4;i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundManager.playSound(fruit);
                    soundManager.loadSound(fruit);
                }
            }, initialTime +  (int)(DURATION_SMALL * (4+i) * speed));
        }
        for(int i=0;i<2;i++) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    soundManager.playSound("drum");
                    soundManager.loadSound("drum");
                }
            }, initialTime +  (int)(DURATION * i * speed));
        }
    }
}
