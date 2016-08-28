package net.cloudapp.eggfry.frypan;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class GameActivity extends AppCompatActivity {
    private SoundManager soundManager;
    private static final int DURATION = 1336;
    private static final int DURATION_SMALL = 334;

    private Handler handler = new Handler();

    List<SquareLayout> defBeats = new ArrayList<>();                // 방어 시 누를 버튼
    SquareLayout[] countViews;                                      // 상단 카운트 뷰
    HashMap<String, Animation> animations = new HashMap<>();

    RelativeLayout defendContainer;
    LinearLayout attackContainer, attackPlayers, attackNums;

    int[] numImgIds = {R.drawable.num_1, R.drawable.num_2, R.drawable.num_3, R.drawable.num_4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        ImageView bg = (ImageView) findViewById(R.id.game_bg);
        attackPlayers = (LinearLayout) findViewById(R.id.attack_players);
        attackNums = (LinearLayout) findViewById(R.id.attack_nums);

        defendContainer = (RelativeLayout) findViewById(R.id.defend_container);
        attackContainer = (LinearLayout) findViewById(R.id.attack_container);

        BusProvider.getInstance().register(this);

        // Resources Set
        setSound();

        setDefBeats();
        setCountViews();
        setAnims();


        for (int i = 0; i < attackPlayers.getChildCount(); i++) {
            attackPlayers.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TimeManager.timingNum == 2) {
                        int attackTo = (attackPlayers.indexOfChild(v) + SocketService.gameManager.getNickNum() + 1) % 4;     // 공격 대상
                        Glide.with(GameActivity.this)                                                                        // 선택한 과일로 countView 변경
                                .load(SocketService.gameManager.getArr_drawableId()[attackTo]).asBitmap()
                                .into(new SquareLayoutTarget(GameActivity.this, countViews[2]));
                        onFruitClicked(attackTo);
                    } else {
                        fryPanFailed();
                    }

                }
            });
        }

        for (int i = 0; i < attackNums.getChildCount(); i++) {
            attackNums.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TimeManager.timingNum == 3) {
                        int attackNum = attackNums.indexOfChild(v) + 1;     // 공격 대상
                        Log.d("NumsClicked", "timingNum = " + String.valueOf(TimeManager.timingNum) + ", attackNum = " + attackNum);
                        Glide.with(GameActivity.this)                                               // countView 숫자로 변경
                                .load(numImgIds[attackNum - 1]).asBitmap()
                                .into(new SquareLayoutTarget(GameActivity.this, countViews[3]));
                        onCountClicked(attackNum);
                    } else {
                        fryPanFailed();
                    }
                }
            });
        }


        // Fruit Image Set
        int[] fruitContainerIds = {R.id.bottom_img, R.id.right_img, R.id.up_img, R.id.left_img};

        int nickNum = SocketService.gameManager.getNickNum();
        for (int i = 0; i < 4; i++) {                                // 자신부터 nickNum 순서 차례대로
            ImageView imageView = (ImageView) findViewById(fruitContainerIds[nickNum % 4]);
            int imgId = SocketService.gameManager.getArr_drawableId()[nickNum % 4];

            Glide.with(this)
                    .load(imgId).asBitmap()
                    .into(imageView);
            if (i != 0) {
                SquareLayout fruitView = (SquareLayout)((ViewGroup) attackPlayers.getChildAt(i-1)).getChildAt(0);
                Glide.with(this).load(imgId).asBitmap()
                        .into(new SquareLayoutTarget(this, fruitView));
            }
            nickNum++;
        }

        Glide.with(this).load(R.drawable.in_game_bg).asBitmap().into(bg);       // BG Load

        BusProvider.getInstance().post(new PushEvent("GameStartButton"));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        BusProvider.getInstance().unregister(this);
    }

    /**
     * 공격
     */
    private void attack() {
        // 서버에 공격 정보 전송
        Log.d("Attack", "Attack!!");

        defendContainer.setVisibility(View.GONE);
        attackContainer.setVisibility(View.VISIBLE);

        for (int i = 0; i < 4; i++) {
            countViews[i].setBackgroundResource(R.drawable.circle);
            countViews[i].setAlpha((float) 0.5);
        }

        startTurn();
        playDrumSound(); // 드럼 재생
    }

    /**
     * 공격 받을 시 방어
     * (서버에서 공격 신호를 받았을 때 실행)
     *
     * @param num 방어 횟수
     */
    private void defend(int num) {
        defendContainer.setVisibility(View.VISIBLE);
        attackContainer.setVisibility(View.GONE);


        for (int i = 0; i < 4 - num; i++) {
            countViews[i].setBackgroundResource(R.drawable.circle);
            countViews[i].setAlpha((float) 0.5);
        }
        for (int i = 4 - num; i < 4; i++) {
            Glide.with(this)
                    .load(R.drawable.kiwi).asBitmap()
                    .into(new SquareLayoutTarget(this, countViews[i]));
            countViews[i].setAlpha((float) 0.5);
        }

        for (SquareLayout defBeat : defBeats) {
            defBeat.setVisibility(View.GONE);
            defBeat.setLeft((int) (Math.random() * (defendContainer.getMeasuredWidth() - 300)));
            defBeat.setTop((int) (Math.random() * (defendContainer.getMeasuredHeight() - 300)));
            defBeat.setRight(defBeat.getLeft() + 300);
            defBeat.setBottom(defBeat.getTop() + 300);
        }


        startTurn(num);
        playDrumSound();
    }

    /**
     * 방어 시 배경 눌렸을 때(실패 처리)
     *
     * @param v defendContainer
     */
    public void onDefendLayoutClicked(View v) {
        int count = 0;
        for (int i = 0; i < defendContainer.getChildCount(); i++) {
            if (defendContainer.getChildAt(i).getVisibility() == View.VISIBLE) {
                count++;
            }
        }
        if (count > 0) {
            // 실패 처리
            fryPanFailed();
        }
    }

    /**
     * 공격 / 방어 실패
     */
    public void fryPanFailed() {
        // 인디안밥

        BusProvider.getInstance().post(new PushEvent("LoseButton"));
    }


    public void setCountView(int[] imageIds) {

    }

    public void onFruitClicked(int fruitNum) { // 어떤 과일이 선택되었을 때
        BusProvider.getInstance().post(new PushEvent("SelectButton " + fruitNum)); // 선택된 과일 보내기
    }

    public void onCountClicked(int countNum) { // 어떤 갯수가 선택되었을 때
        BusProvider.getInstance().post(new PushEvent("NumberButton " + countNum)); // 선택된 갯수 보내기
    }

    public void onDefendClicked(int num) { // num번째 defend 버튼이 클릭되었을 때
        BusProvider.getInstance().post(new PushEvent("DefendButton " + SocketService.gameManager.getNickNum() + " " + num));
    }

    // 숫자 -> 문자
    public String numberToString(int number) {
        String number_string = "";
        switch (number) {
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

    public void setDefBeats() {
        defBeats.add(new SquareLayout(this, SquareLayout.STANDARD_WIDTH));
        defBeats.add(new SquareLayout(this, SquareLayout.STANDARD_WIDTH));
        defBeats.add(new SquareLayout(this, SquareLayout.STANDARD_WIDTH));
        defBeats.add(new SquareLayout(this, SquareLayout.STANDARD_WIDTH));


        for (final SquareLayout defBeat : defBeats) {
            defendContainer.addView(defBeat);
            ViewGroup.LayoutParams params = defBeat.getLayoutParams();
            params.height = 300;
            params.width = 300;

            defBeat.setLayoutParams(params);

            // 비트 터치 시
            defBeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // true, false
                            TimeManager.beatEndMillis = System.currentTimeMillis();

                            int result = TimeManager.checkScore();
                            Log.d("Touch", String.valueOf(result));
                            int score = SocketService.gameManager.getScore()[SocketService.gameManager.getNickNum()];   // 현재 점수

                            switch (result) {
                                case TimeManager.PERFECT:
                                    // 55점
                                    score += 20;
                                case TimeManager.GREAT:
                                    // 35점
                                    score += 15;
                                case TimeManager.GOOD:
                                    // 20점
                                    score += 20;

                                    //Tasks
                                    v.clearAnimation();
                                    int index = defBeats.indexOf(v);
                                    v.setVisibility(View.GONE);
                                    countViews[index].setAlpha(1);

                                    if(index == 3) {                    // 마지막 비트
                                        attack();
                                    }

                                    break;
                                case TimeManager.FAIL:
                                    fryPanFailed();
                                    break;
                            }
                        }
                    });


                }
            });

            // Image Load
            Glide.with(this).load(R.drawable.beat1).asBitmap().into(new SquareLayoutTarget(this, defBeats.get(0)));
            Glide.with(this).load(R.drawable.beat2).asBitmap().into(new SquareLayoutTarget(this, defBeats.get(1)));
            Glide.with(this).load(R.drawable.beat3).asBitmap().into(new SquareLayoutTarget(this, defBeats.get(2)));
            Glide.with(this).load(R.drawable.beat4).asBitmap().into(new SquareLayoutTarget(this, defBeats.get(3)));
        }

    }

    private void setCountViews() {
        countViews = new SquareLayout[]{
                (SquareLayout) findViewById(R.id.count1),
                (SquareLayout) findViewById(R.id.count2),
                (SquareLayout) findViewById(R.id.count3),
                (SquareLayout) findViewById(R.id.count4)
        };

        // Image Load

        Glide.with(this).load(R.drawable.ting).asBitmap().into(new SquareLayoutTarget(this, countViews[0]));
        Glide.with(this).load(R.drawable.ting).asBitmap().into(new SquareLayoutTarget(this, countViews[1]));
        Glide.with(this).load(R.drawable.ting).asBitmap().into(new SquareLayoutTarget(this, countViews[2]));
        Glide.with(this).load(R.drawable.ting).asBitmap().into(new SquareLayoutTarget(this, countViews[3]));
    }

    private void setAnims() {
        // 추가
        animations.put("count_in", AnimationUtils.loadAnimation(this, R.anim.count_in));
        animations.put("beat_anim", AnimationUtils.loadAnimation(this, R.anim.beat_anim));

        // Listener
        animations.get("beat_anim").setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 비트 생성 시 millis
                TimeManager.beatStartMillis = System.currentTimeMillis();
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 터치 못하고 애니메이션 끝남
                fryPanFailed();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    // 턴 시작
    public void startTurn() {
        TimeManager.timingNum = 0;

        final Handler handler = new Handler();

        final Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if(TimeManager.timingNum < 4) {
                    countViews[TimeManager.timingNum].setAlpha(1);
                    Log.d("Timer", String.valueOf(TimeManager.timingNum));
                    TimeManager.timingNum++;
                    handler.postDelayed(this, 2000);
                } else if (TimeManager.timingNum >= 4) {
                    this.cancel();
                    timer.cancel();
                    handler.removeCallbacks(this);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attackContainer.setVisibility(View.GONE);
                        }
                    });
                    return;
                }
            }
        };

        timer.schedule(task, 2000);
    }

    public void startTurn(final int num) {
        TimeManager.timingNum = 0;
        final Timer timer = new Timer();

        final Handler handler = new Handler();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(TimeManager.timingNum < 4) {
                            countViews[TimeManager.timingNum].setAlpha(1);
                            TimeManager.timingNum++;
                        } else {
                            attack();
                            timer.cancel();
                            return ;
                        }

                        if(TimeManager.timingNum >= num) {
                            SquareLayout defBeat = defBeats.get(TimeManager.timingNum - num);
                            defBeat.setVisibility(View.VISIBLE);
                            defBeat.startAnimation(animations.get("beat_anim"));
                        }
                    }
                });
                handler.postDelayed(this, 0);
            }
        };


        if(num == 0) {
            defBeats.get(0).setVisibility(View.VISIBLE);
            defBeats.get(0).startAnimation(animations.get("beat_anim"));
        }

        timer.schedule(task, 2000);
    }


    // Sounds
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
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation anim = animations.get("count_in");
                anim.setDuration(DURATION / 4);

                int offset = 0;
                for (SquareLayout countView : countViews) {
                    Glide.with(GameActivity.this).load(R.drawable.ting).asBitmap()          // 이미지
                            .into(new SquareLayoutTarget(GameActivity.this, countView));
                    anim.setStartOffset(offset);
                    offset += DURATION / 4;

                    countView.startAnimation(anim);
                }
                soundManager.playSound("start1");
            }
        }, 0);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation anim = animations.get("count_in");
                anim.setDuration(DURATION / 4);

                int offset = 0;
                for (SquareLayout countView : countViews) {
                    Glide.with(GameActivity.this).load(R.drawable.tang).asBitmap()          // 이미지
                            .into(new SquareLayoutTarget(GameActivity.this, countView));
                    anim.setStartOffset(offset);
                    offset += DURATION / 4;

                    countView.startAnimation(anim);
                }
                soundManager.playSound("start2");
            }
        }, DURATION);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation anim = animations.get("count_in");
                anim.setDuration(DURATION / 4);

                int offset = 0;
                for (SquareLayout countView : countViews) {
                    Glide.with(GameActivity.this).load(R.drawable.tang).asBitmap()          // 이미지
                            .into(new SquareLayoutTarget(GameActivity.this, countView));
                    anim.setStartOffset(offset);
                    offset += DURATION / 4;

                    countView.startAnimation(anim);
                }
                soundManager.playSound("start3");
            }
        }, DURATION * 2);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                soundManager.playSound("start4");
            }
        }, DURATION * 3);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, DURATION * 4);
    }


    public void playDrumSound() {
        soundManager.playSound("drum");
        soundManager.loadSound("drum");
    }

    /**
     * 공격시 재생 소리(과일 선택했을 때) -- 먼저 실행
     * 누를 때마다 소리남
     *
     * @param fruit 과일 이름
     */
    public void playAttackSound(String fruit) {
        soundManager.playSound(fruit);
        soundManager.loadSound(fruit);
    }

    /**
     * 공격시 재생 소리(횟수 클릭했을 때) -- 나중에 실행
     * 누를 때마다 소리남
     *
     * @param number
     */
    public void playAttackSound(int number) {

        String number_string = numberToString(number);
        soundManager.playSound(number_string);
        soundManager.loadSound(number_string);

        handler.postDelayed(new Runnable() { // Send 명령 보내기
            @Override
            public void run() {
                BusProvider.getInstance().post(new PushEvent("SendButton"));
            }
        }, DURATION_SMALL);
    }

    /**
     * 방어시 재생 소리(누를 때마다 소리남)
     *
     * @param fruit  과일 이름
     * @param number 수행 번째(제로 베이스)
     */
    public void playDefendSound(String fruit, int number) {
        soundManager.playSound(fruit);
        soundManager.loadSound(fruit);

//        if (SocketService.gameManager.getIsMyTurn() && number == 3) {
//            attack();
//        }
    }

    // 서버에서 메세지를 받았을 때
    @Subscribe
    public void FinishLoad(PushEvent mPushEvent) {
        String[] message = mPushEvent.getString().split(" ");
        Log.d("NickNum", message[0]);
        switch (message[0]) {
            case "GameStart":
//                initialPlaySound();
                Log.d("Socket", "NickNum : " + String.valueOf(SocketService.gameManager.getNickNum()));
                if (SocketService.gameManager.getNickNum() == 0) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            attack();
                        }
                    });
                }
                break;

            case "Send": // 핑을 맞춰주기
                if (SocketService.gameManager.getIsMyTurn()) { // 자기 턴이면
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            defend(SocketService.gameManager.getAttackCount()); // 공격받은 횟수만큼 방어
                        }
                    });
                }
                break;

            case "Select": // 공격 대상을 선택한다면(서버에서 받아옴)
                playAttackSound(SocketService.gameManager.getArr_nickname()[SocketService.gameManager.getAttackTarget()]); // 사운드 재생

                break;
            case "Number": // 공격 횟수를 선택한다면(서버에서 받아옴)
                playAttackSound(SocketService.gameManager.getAttackCount()); // 사운드 재생
                if(SocketService.gameManager.getAttackTarget()
                        == SocketService.gameManager.getNickNum()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            defend(SocketService.gameManager.getAttackCount());
                        }
                    });
                }
                break;

            case "Defend":
                playDefendSound(message[1], Integer.parseInt(message[2]));
                break;

            case "Time":
                break;

            case "Report": // 점수를 보내줌
                break;

            case "Result": // 누군가가 졌을 때
                Intent it = new Intent(this, ResultActivity.class);
                startActivity(it);
                break;
        }
    }

    /////////////
    // Classes //
    /////////////

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
}



