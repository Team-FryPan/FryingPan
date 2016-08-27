package net.cloudapp.eggfry.frypan;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * Created by swj on 16. 8. 25..
 */

// 가로 세로 1:1 고정 레이아웃
public class SquareLayout extends FrameLayout {
    // 기준 방향
    public static boolean STANDARD_WIDTH = true;        // default
    public static boolean STANDARD_HEIGHT = false;

    private boolean standardSide;

    public SquareLayout(Context context, boolean standardSide) {
        super(context);

        this.standardSide = standardSide;

        initView();
    }


    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.standardSide = STANDARD_WIDTH;
        initView();
        getAttrs(attrs);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        if(standardSide)        // 가로 기준
            setMeasuredDimension(width, width);
        else                    // 세로 기준
            setMeasuredDimension(height, height);
    }

    // View 초기 설정
    public void initView() {
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li = (LayoutInflater) getContext().getSystemService(infService);
        View v = li.inflate(R.layout.square_layout, this, false);
        addView(v);
    }

    // 레이아웃 속성 받아와서 적용
    public void getAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SquareLayout);

        this.standardSide = !typedArray.getBoolean(R.styleable.SquareLayout_standardHeight, false);

        typedArray.recycle();

    }
}
