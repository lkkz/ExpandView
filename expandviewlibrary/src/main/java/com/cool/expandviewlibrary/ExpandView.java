package com.cool.expandviewlibrary;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by cool on 2017/8/3.
 */

public class ExpandView extends FrameLayout implements AnimView.OnExpandAnimEndListener, AnimView.OnPackupAnimEndListener {

    private int mAnimOrientation;
    private int mAnimDuration;//动画时长
    private float mCenterX;//动画开始圆心x坐标
    private float mCenterY;//动画开始圆心y坐标
    private final static int UPLEFT = 1;//左上
    private final static int UPRIGHT = 2;//右上
    private final static int LEFTBOTTOM = 3;//左下
    private final static int RIGHTBOTTOM = 4;//右下
    private final static int CENTER = 5;//中间
    private final static int ANIM_DURATION_DEFAULT = 500;//动画默认时长
    private float mStartRadius;//开始执行时的圆半径
    private int mAnimViewWidth;
    private int mAnimViewHight;
    private boolean isAnimating = false;//是否在动画中

    private AnimView animView;

    public ExpandView(@NonNull Context context) {
        this(context, null);
    }

    public ExpandView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ExpandView);
        mAnimOrientation = ta.getInt(R.styleable.ExpandView_anim_orientation,UPLEFT);
        mAnimDuration = ta.getInt(R.styleable.ExpandView_anim_duration,ANIM_DURATION_DEFAULT);
        mCenterX = ta.getDimension(R.styleable.ExpandView_centerX,-1);
        mCenterY = ta.getDimension(R.styleable.ExpandView_centerY,-1);
        ta.recycle();
        init();
    }

    private void init() {
        mStartRadius = dp2px(5);
    }

    /**
     * 做展开动画
     */
    public void doExpandAnim() {
        if(isAnimating){
            return;
        }
        isAnimating = true;
        setVisibility(VISIBLE);

        int childCount = getChildCount();
        if(childCount >1){
            throw new IllegalArgumentException("ExpandView只能有一个子View");
        }
        if(childCount <=0){
            return;
        }
        setChildViewVisibility(INVISIBLE);

        if(animView == null) {
            animView = new AnimView(getContext());
        }
        animView.setOnExpandAnimEndListener(this);
        View view = addAnimView(childCount);
        animView.doExpandAnim(view);
    }

    /**
     * 做收起动画
     */
    public void doPackupAnim() {
        if(isAnimating){
            return;
        }
        isAnimating = true;
        setVisibility(VISIBLE);

        int childCount = getChildCount();
        setChildViewVisibility(GONE);

        if(childCount <=0){
            return;
        }
        if(animView == null) {
            animView = new AnimView(getContext());
        }
        animView.setOnPackupAnimEndListener(this);

        View view = addAnimView(childCount);
        animView.doPackupAnim(view);
    }

    /**
     * 获取子view的宽高并添加动画animview
     * @param childCount 孩子个数
     * @return 返回第一个孩子
     */
    @NonNull
    private View addAnimView(int childCount) {
        View view = getChildAt(0);
        mAnimViewWidth = view.getMeasuredWidth();
        mAnimViewHight = view.getHeight();
        MarginLayoutParams l = (MarginLayoutParams) view.getLayoutParams();
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(mAnimViewWidth,mAnimViewHight);
        layoutParams.leftMargin = l.leftMargin;
        layoutParams.topMargin = l.topMargin;
        layoutParams.rightMargin = l.rightMargin;
        layoutParams.bottomMargin = l.bottomMargin;
        addView(animView, childCount, layoutParams);
        initAnimView(animView);
        return view;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new MarginLayoutParams(lp);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int childCount = getChildCount();
        int expectWidth = 0;
        int expectHeight = 0;

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
            measureChildWithMargins(childView,widthMeasureSpec,0,heightMeasureSpec,0);
            int childMeasuredWidth = childView.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            int childMeasuredHeight = childView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            expectWidth = childMeasuredWidth;
            expectHeight = childMeasuredHeight;
        }

        if(widthMode == MeasureSpec.EXACTLY){
            expectWidth = widthSize;
        }else {
            expectWidth = MeasureSpec.makeMeasureSpec(expectWidth,MeasureSpec.EXACTLY);
        }

        if(heightMode == MeasureSpec.EXACTLY){
            expectHeight = heightSize;
        }else {
            expectHeight = MeasureSpec.makeMeasureSpec(expectHeight,MeasureSpec.EXACTLY);
        }

        setMeasuredDimension(expectWidth,expectHeight);
    }

    /**
     * 设置子view显示或隐藏
     * @param visibility 显示或隐藏
     */
    private void setChildViewVisibility(int visibility) {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if(childView instanceof AnimView){
                removeView(childView);
            }
            childView.setVisibility(visibility);
        }
    }

    /**
     * 设置动画view的一些属性
     * @param animView 动画view
     */
    private void initAnimView(AnimView animView){
        animView.setDuration(mAnimDuration);
        animView.setCenterPosition(calculateCirclePoint());
        animView.setStartRadius(mStartRadius);
    }

    /**
     * 计算动画起点圆心
     * @return 起点圆心
     */
    private PointF  calculateCirclePoint(){
        PointF circlePoint = new PointF();
        if(mCenterX != -1 && mCenterY != -1){
            circlePoint.set(mCenterX,mCenterY);
            return circlePoint;
        }
        switch (mAnimOrientation) {
            case UPLEFT:
                mCenterX = mStartRadius;
                mCenterY = mStartRadius;
            break;
            case UPRIGHT:
                mCenterX = mAnimViewWidth -mStartRadius;
                mCenterY = mStartRadius;
            break;
            case LEFTBOTTOM:
                mCenterX = mStartRadius;
                mCenterY = mAnimViewHight - mStartRadius;
            break;
            case RIGHTBOTTOM:
                mCenterX = mAnimViewWidth - mStartRadius;
                mCenterY = mAnimViewHight - mStartRadius;
            break;
            case CENTER:
                mCenterX = mAnimViewWidth /2;
                mCenterY = mAnimViewHight /2;
            break;
        }
        circlePoint.set(mCenterX,mCenterY);
        return circlePoint;
    }

    @Override
    public void onExpandAnimEnd() {
        isAnimating = false;
        if (animView != null) {
            removeView(animView);
        }
        setChildViewVisibility(VISIBLE);

    }

    @Override
    public void onPackupAnimEnd() {
        isAnimating = false;
        if (animView != null) {
            removeView(animView);
        }
        setVisibility(INVISIBLE);
    }

    private int dp2px(int dp){
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
