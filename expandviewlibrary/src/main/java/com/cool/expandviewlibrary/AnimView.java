package com.cool.expandviewlibrary;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by cool on 2017/8/3.
 */

public class AnimView extends View {

    private Bitmap mBackgroundBitmap;
    private Paint mPaint;
    private float mEndRadius;
    private float mStartRadius;
    private PointF mCirclePoint;//封装圆心坐标
    private float mCurrentRadius = mStartRadius;
    private int width;
    private int height;
    private long mDuration;

    public AnimView(Context context) {
        this(context, null);
    }

    public AnimView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AnimView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mDuration = 500;
        mCirclePoint = new PointF(0,0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawCircle(mCirclePoint.x, mCirclePoint.y, mCurrentRadius, mPaint);
    }

    /**
     * 展开动画
     *
     * @param backgroundAnimView
     */
    public void doExpandAnim(View backgroundAnimView) {
        createBackgroundBitmap(backgroundAnimView);
        if(mBackgroundBitmap == null){
            return;
        }
        startExpandAnim();
    }

    /**
     * 收起动画
     */
    public void doPackupAnim(View backgroundAnimView) {
        createBackgroundBitmap(backgroundAnimView);
        if(mBackgroundBitmap == null){
            return;
        }
        startPackupAnim();
    }

    private void createBackgroundBitmap(View backgroundAnimView) {
        width = backgroundAnimView.getWidth();
        height = backgroundAnimView.getHeight();
        if(width <=0 || height <= 0){
            return;
        }
        mBackgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        backgroundAnimView.draw(new Canvas(mBackgroundBitmap));

        mEndRadius = (float) Math.sqrt(width * width + height * height);
        BitmapShader bitmapShader = new BitmapShader(mBackgroundBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mPaint.setShader(bitmapShader);
    }

    private void startPackupAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mEndRadius, 0);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentRadius = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.setDuration(mDuration);
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onPackupAnimEndListener != null) {
                    onPackupAnimEndListener.onPackupAnimEnd();
                }
                mBackgroundBitmap.recycle();
                mBackgroundBitmap = null;
            }
        });
        valueAnimator.start();
    }

    private void startExpandAnim() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(mStartRadius, mEndRadius);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mCurrentRadius = (float) valueAnimator.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (onExpandAnimEndListener != null) {
                    onExpandAnimEndListener.onExpandAnimEnd();
                }
                mBackgroundBitmap.recycle();
                mBackgroundBitmap = null;
            }
        });
        valueAnimator.setDuration(mDuration);
        valueAnimator.start();
    }

    /**
     * 设置动画时长
     * @param duration 时长
     */
    public void setDuration(long duration){
        this.mDuration = duration;
    }

    /**
     * 设置圆心坐标
     * @param point 圆心坐标
     */
    public void setCenterPosition(PointF point){
        this.mCirclePoint = point;
    }

    /**
     * 设置开始是圆半径
     * @param startRadius 圆半径
     */
    public void setStartRadius(float startRadius){
        this.mStartRadius = startRadius;
    }

    private OnExpandAnimEndListener onExpandAnimEndListener;

    public void setOnExpandAnimEndListener(OnExpandAnimEndListener listener) {
        this.onExpandAnimEndListener = listener;
    }

    public interface OnExpandAnimEndListener {
        void onExpandAnimEnd();
    }

    private OnPackupAnimEndListener onPackupAnimEndListener;

    public void setOnPackupAnimEndListener(OnPackupAnimEndListener listener){
        this.onPackupAnimEndListener = listener;
    }

    public interface OnPackupAnimEndListener{
        void onPackupAnimEnd();
    }
}
