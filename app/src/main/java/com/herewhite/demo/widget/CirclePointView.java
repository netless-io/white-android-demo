package com.herewhite.demo.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.herewhite.demo.R;

public class CirclePointView extends View {

    private static final int DEFAULT_COLOR = Color.BLACK;
    private int mCircleColor;
    private Paint mDrawPoint;

    private int mWidth;
    private int mHeight;
    private boolean mIsSolid; //是否实心圆。
    private float mStrikeWidth;

    public CirclePointView(Context context) {
        this(context, null);
    }

    public CirclePointView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CirclePointView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) { // 获取在xml中配置的属性。
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CirclePointView);
            mCircleColor = array.getColor(R.styleable.CirclePointView_pointColor, DEFAULT_COLOR);
            mIsSolid = array.getBoolean(R.styleable.CirclePointView_isSolid, true);
            mStrikeWidth = array.getDimension(R.styleable.CirclePointView_strikeWidth, 1);
            array.recycle();
        }
        mDrawPoint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDrawPoint.setStyle(mIsSolid ? Paint.Style.FILL : Paint.Style.STROKE);
        if (!mIsSolid) {
            mDrawPoint.setStrokeWidth(mStrikeWidth);
        }
        mDrawPoint.setColor(mCircleColor);
        mDrawPoint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);// 获取圆、圆环的宽度
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        final int widMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int defaultWidth = (int) getResources().getDisplayMetrics().density * 4;
        if (mWidth == 0 || widMode != MeasureSpec.EXACTLY) {
            mWidth = defaultWidth;
        }
        if (mHeight == 0 || heightMode != MeasureSpec.EXACTLY) {
            mHeight = defaultWidth;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int value = ((mWidth > mHeight ? mHeight : mWidth)) >> 1;
        if (mIsSolid || mStrikeWidth >= value) { // 画实心圆。
            canvas.drawCircle(mWidth >>> 1, mHeight >>> 1, value, mDrawPoint);
            return;
        }
        value =  (value - (int)(mStrikeWidth / 2)); // 空心圆。
        canvas.drawCircle(mWidth >>> 1, mHeight >>> 1, value, mDrawPoint);
    }

    /**
     * 画实心圆。
     */
    public void setSolidCircle() {
        mIsSolid = true;
        mDrawPoint.setStyle(Paint.Style.FILL);
        invalidate();
    }

    /**
     * 画空心圆，
     *
     * @param strikeWidth 圆环宽度。
     */
    public void setEmptyCircleWid(float strikeWidth) {
        if (strikeWidth == 0) {
            return;
        }
        mIsSolid = false;
        mDrawPoint.setStyle(Paint.Style.STROKE);
        mStrikeWidth = strikeWidth;
        mDrawPoint.setStrokeWidth(strikeWidth);
        invalidate();
    }

    /**
     * 设置圆的颜色。
     *
     * @param circleColor 颜色。
     */
    public void setCircleColor(int circleColor) {
        mCircleColor = circleColor;
        mDrawPoint.setColor(circleColor);
        invalidate();
    }
}
