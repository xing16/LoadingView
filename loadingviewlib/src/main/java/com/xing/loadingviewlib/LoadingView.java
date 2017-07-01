package com.xing.loadingviewlib;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/6/24.
 */

public class LoadingView extends View {

    private final int DEFAULT_COLOR = Color.BLACK;  // 默认圆弧颜色

    private final int DEFAULT_STROKE_WIDTH = dp2Px(2);  // 默认圆弧宽度

    private final boolean DEFAULT_IS_SHOW_RESULT = false;  // 默认不显示加载结果

    private final int DEFAULT_VIEW_WIDTH = dp2Px(50);  // 控件默认宽度

    private final int DEFAULT_VIEW_HEIGHT = dp2Px(50);  // 控件默认高度

    private int color;  // 圆弧颜色

    private int strokeWidth;   // 圆弧宽度

    private boolean isShowResult;   // 是否显示加载结果状态

    private Paint paint;  // 画笔

    private int mWidth;  // 控件宽度

    private int mHeight;  // 控件高度

    private int radius;   // 圆弧所在圆的半径

    private int halfStrokeWidth;  // 画笔宽度的一半


    private int rotateDelta = 4;

    private int curAngle = 0;

    private int minAngle = -90;

    private int startAngle = -90;  // 上方顶点

    private int endAngle = 0;

    private RectF rectF;

    private StateEnum stateEnum = StateEnum.LOADING;

    private Path successPath;

    private Path rightFailPath;

    private Path leftFailPath;

    private ValueAnimator successAnimator;

    private ValueAnimator rightFailAnimator;

    private ValueAnimator leftFailAnimator;

    private PathMeasure pathMeasure;

    private float successValue;

    private float rightFailValue;

    private float leftFailValue;

    private Path destPath;

    private AnimatorSet animatorSet;

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = null;
        try {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
            color = typedArray.getColor(R.styleable.LoadingView_color, DEFAULT_COLOR);
            strokeWidth = (int) typedArray.getDimension(R.styleable.LoadingView_storkeWidth, DEFAULT_STROKE_WIDTH);
            isShowResult = typedArray.getBoolean(R.styleable.LoadingView_isShowResult, DEFAULT_IS_SHOW_RESULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (typedArray != null) {
                typedArray.recycle();
            }
        }
        paint = createPaint(color, strokeWidth, Paint.Style.STROKE);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.i("debug", "getMeasureWidth() = " + getMeasuredWidth());
        Log.i("debug", "getMeasureHeight() = " + getMeasuredHeight());

        radius = Math.min(mWidth, mHeight) / 2;
        halfStrokeWidth = strokeWidth / 2;

        rectF = new RectF(halfStrokeWidth - radius, halfStrokeWidth - radius,
                radius - halfStrokeWidth, radius - halfStrokeWidth);
        // success path
        successPath = new Path();
        successPath.moveTo(-radius * 2 / 3f, 0f);
        successPath.lineTo(-radius / 8f, radius / 2f);
        successPath.lineTo(radius / 2, -radius / 3);
        // fail path ,right top  to left bottom
        rightFailPath = new Path();
        rightFailPath.moveTo(radius / 3f, -radius / 3f);
        rightFailPath.lineTo(-radius / 3f, radius / 3f);

        // fail path, left top to right bottom
        leftFailPath = new Path();
        leftFailPath.moveTo(-radius / 3f, -radius / 3f);
        leftFailPath.lineTo(radius / 3f, radius / 3f);

        pathMeasure = new PathMeasure();

        destPath = new Path();

        initSuccessAnimator();
        initFailAnimator();
    }

    private void initSuccessAnimator() {
//        pathMeasure.setPath(successPath, false);
        successAnimator = ValueAnimator.ofFloat(0, 1f);
        successAnimator.setDuration(1000);
        successAnimator.setInterpolator(new LinearInterpolator());
        successAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                successValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
    }


    private void initFailAnimator() {
//        pathMeasure.setPath(rightFailPath, false);
        rightFailAnimator = ValueAnimator.ofFloat(0, 1f);
        rightFailAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                rightFailValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

//        pathMeasure.setPath(leftFailPath, false);
        leftFailAnimator = ValueAnimator.ofFloat(0, 1f);
        leftFailAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                leftFailValue = (float) animation.getAnimatedValue();
                invalidate();
            }
        });

        animatorSet = new AnimatorSet();
        animatorSet.play(leftFailAnimator).after(rightFailAnimator);
        animatorSet.setDuration(500);
        animatorSet.setInterpolator(new LinearInterpolator());


    }


    /**
     * 测量控件的宽高，当测量模式不是精确模式时，设置默认宽高
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST || widthMode == MeasureSpec.UNSPECIFIED) {
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_VIEW_WIDTH, MeasureSpec.EXACTLY);
        }
        if (heightMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.UNSPECIFIED) {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_VIEW_HEIGHT, MeasureSpec.EXACTLY);

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        destPath.reset();
        destPath.lineTo(0, 0);   // destPath
        if (stateEnum == StateEnum.LOADING) {
            if (endAngle >= 300 || startAngle > minAngle) {
                startAngle += 6;
                if (endAngle > 20) {
                    endAngle -= 6;
                }
            }
            if (startAngle > minAngle + 300) {
                minAngle = startAngle;
                endAngle = 20;
            }
            canvas.rotate(curAngle += rotateDelta, 0, 0);//旋转rotateDelta=4的弧长
            canvas.drawArc(rectF, startAngle, endAngle, false, paint);
            // endAngle += 6 放在 drawArc()后面，是防止刚进入时，突兀的显示了一段圆弧
            if (startAngle == minAngle) {
                endAngle += 6;
            }
            invalidate();
        }
        if (isShowResult) {
            if (stateEnum == StateEnum.LOAD_SUCCESS) {
                pathMeasure.setPath(successPath, false);
                canvas.drawCircle(0, 0, radius - halfStrokeWidth, paint);
                pathMeasure.getSegment(0, successValue * pathMeasure.getLength(), destPath, true);
                canvas.drawPath(destPath, paint);
            } else if (stateEnum == StateEnum.LOAD_FAILED) {
                canvas.drawCircle(0, 0, radius - halfStrokeWidth, paint);
                pathMeasure.setPath(rightFailPath, false);
                pathMeasure.getSegment(0, rightFailValue * pathMeasure.getLength(), destPath, true);
                if (rightFailValue == 1) {
                    pathMeasure.setPath(leftFailPath, false);
                    pathMeasure.nextContour();
                    pathMeasure.getSegment(0, leftFailValue * pathMeasure.getLength(), destPath, true);
                }
                canvas.drawPath(destPath, paint);
            }
        }
        canvas.restore();

    }


    public void updateState(StateEnum stateEnum) {
        this.stateEnum = stateEnum;
        if (stateEnum == StateEnum.LOAD_SUCCESS) {
            successAnimator.start();
        } else if (stateEnum == StateEnum.LOAD_FAILED) {
            animatorSet.start();
        }
    }


    public enum StateEnum {
        LOADING,  // 正在加载
        LOAD_SUCCESS,    // 加载成功，显示对号
        LOAD_FAILED     // 加载失败，显示叉号
    }


    /**
     * 创建画笔
     *
     * @param color       画笔颜色
     * @param strokeWidth 画笔宽度
     * @param style       画笔样式
     * @return
     */
    private Paint createPaint(int color, int strokeWidth, Paint.Style style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth);
        paint.setStyle(style);
        return paint;
    }


    /**
     * dp 转换成 px
     *
     * @param dpValue
     * @return
     */
    private int dp2Px(int dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }

}
