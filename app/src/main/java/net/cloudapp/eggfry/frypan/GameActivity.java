package net.cloudapp.eggfry.frypan;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.squareup.otto.Subscribe;

import java.util.HashMap;



public class GameActivity extends AppCompatActivity {
    private SoundManager soundManager;
    private static final int INITIALTIME = 3000;
    private static final int DURATION = 1336;
    private static final int DURATION_SMALL = 334;

    ImageView[] def_beats;                  // 방어 시 누를 버튼
    RelativeLayout defendContainer;
    HashMap<String, Animation> animations = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setSound();

        BusProvider.getInstance().register(this);

        animations.put("in", AnimationUtils.loadAnimation(this, R.anim.in));

        defendContainer = (RelativeLayout)findViewById(R.id.defend_container);

        def_beats = new ImageView[]{
                new ImageView(this),
                new ImageView(this),
                new ImageView(this),
                new ImageView(this)
        };

        ImageView bg = (ImageView)findViewById(R.id.game_bg);

        SquareLayout count1 = (SquareLayout) findViewById(R.id.count1);
        SquareLayout count2 = (SquareLayout) findViewById(R.id.count2);
        SquareLayout count3 = (SquareLayout) findViewById(R.id.count3);
        SquareLayout count4 = (SquareLayout) findViewById(R.id.count4);

        for (ImageView def_beat : def_beats) {
            def_beat.setMaxWidth(20);
            def_beat.setMaxHeight(20);

            def_beat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // true, false
                }
            });

            def_beat.setBackgroundResource(R.drawable.circle);      // 이미지 대신 임시로
            // 이미지 있으면
            Glide.with(this).load(R.drawable.beat1).asBitmap().into(def_beat);
        }

        // Image Initialization
        Glide.with(this).load(R.drawable.in_game_bg).asBitmap().into(bg);
        Glide.with(this).load(R.drawable.ting).asBitmap().into(new SquareLayoutTarget(this, count1));
        Glide.with(this).load(R.drawable.ting).asBitmap().into(new SquareLayoutTarget(this, count2));
        Glide.with(this).load(R.drawable.tang).asBitmap().into(new SquareLayoutTarget(this, count3));
        Glide.with(this).load(R.drawable.tang).asBitmap().into(new SquareLayoutTarget(this, count4));

        Glide.with(this).load(R.drawable.beat1).asBitmap().into(def_beats[0]);
        Glide.with(this).load(R.drawable.beat2).asBitmap().into(def_beats[1]);
        Glide.with(this).load(R.drawable.beat3).asBitmap().into(def_beats[2]);
        Glide.with(this).load(R.drawable.beat4).asBitmap().into(def_beats[3]);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }


    /**
     * 공격
     */
    public void attack() {
        // 서버에 공격 정보 전송

    }

    /**
     * 공격 받을 시 방어
     * (서버에서 공격 신호를 받았을 때 실행)
     * @param num 방어 횟수
     */
    public void defend(int num) {

    }

    /**
     *  방어 시 배경 눌렸을 때(실패 처리)
     * @param v defendContainer
     */
    public void onDefendLayoutClicked(View v) {
        if(defendContainer.getChildCount() > 0) {
            // 실패 처리
            defendFailed();
        }
    }

    /**
     * 방어 실패
     */
    public void defendFailed() {

    }


    public void setCountView(int[] imageIds) {

    }


    // 애니메이션 테스트
    public void onUpImgClicked(View v) {

        int interval = 100;
        int[] squareLayoutIds = {R.id.count1, R.id.count2, R.id.count3,R.id.count4};

        int i = 1;

        for (int viewId : squareLayoutIds) {
            Animation animation = animations.get("in");
            animation.setStartOffset(i * interval);

            SquareLayout a = (SquareLayout)findViewById(squareLayoutIds[i-1]);
            a.setBackgroundResource(R.drawable.circle);
            Glide.with(this).load(R.drawable.lemon).asBitmap()
                    .into(new SquareLayoutTarget(this, (SquareLayout)findViewById(squareLayoutIds[i-1])));
            a.startAnimation(animation);
            i++;
        }
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

    // 숫자 -> 문자
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

    // 서버에서 메세지를 받았을 때
    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        String[] message = mPushEvent.getString().split(" ");
        switch (message[0]) {
            case "Send":
                Log.d("GameActivity", String.valueOf(SocketService.gameManager.getAttackCount()));
                defend(SocketService.gameManager.getAttackCount());
                break;
            case "Time":
                break;
        }
        if(message[0].equals("Send")) {

        } else if(message[0].equals("Time")) {

        }
    }
}




// Glide를 SquareLayout에 적용시키기 위한 클래스
class SquareLayoutTarget extends ViewGroupTarget<Bitmap> {
    private Context context;

    public SquareLayoutTarget(Context context, FrameLayout frameLayout) {

        super(frameLayout);

        this.context = context;
    }

    /**
     * Sets the {@link Bitmap} on the view using
     * {@link ImageView#setImageBitmap(Bitmap)}.
     *
     * @param resource The bitmap to display.
     */

    @Override
    protected void setResource(Bitmap resource) {
        ImageView imageView = (ImageView) ((ViewGroup) view.getChildAt(0)).getChildAt(0);
        Drawable drawable = new BitmapDrawable(resource);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(drawable);
        }
    }

}

abstract class ViewGroupTarget<T> extends ViewTarget<ViewGroup, T> implements GlideAnimation.ViewAdapter {

    public ViewGroupTarget(ViewGroup view) {
        super(view);
    }

    @Override
    public Drawable getCurrentDrawable() {
        return view.getBackground();
    }

    @Override
    public void setDrawable(Drawable drawable) {
        view.setBackgroundDrawable(drawable);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        view.setBackgroundDrawable(placeholder);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        view.setBackgroundDrawable(errorDrawable);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        view.setBackgroundDrawable(placeholder);
    }

    @Override
    public void onResourceReady(T resource, GlideAnimation<? super T> glideAnimation) {
        this.setResource(resource);
    }

    protected abstract void setResource(T resource);
}