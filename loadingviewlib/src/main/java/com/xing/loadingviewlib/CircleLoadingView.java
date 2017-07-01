package com.xing.loadingviewlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/6/25.
 */

public class CircleLoadingView extends View {

    private Path path;

    private Paint paint;

    private int mWidth;

    private int mHeight;

    private PathMeasure pathMeasure;

    private float curValue;
    private Path loadedPath;
    private float length;

    private Path destPath;


    public CircleLoadingView(Context context) {
        this(context, null);
    }

    public CircleLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        path = new Path();

        loadedPath = new Path();

        destPath = new Path();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        path.addCircle(mWidth / 2, mHeight / 2, 60, Path.Direction.CW);
        pathMeasure = new PathMeasure(path, false);
        length = pathMeasure.getLength();

        initAnimator();

    }

    private void initAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curValue = (float) animation.getAnimatedValue();
                Log.e("circle", "curValue = " + curValue);
                postInvalidate();
            }
        });
        valueAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawPath(path, paint);
        destPath.reset();   // 清除上次绘制的路径
        destPath.lineTo(0, 0);
        canvas.rotate(360.0f * curValue - 45.0f, mWidth / 2, mHeight / 2);
        float stop = curValue * length;
        float start = (float) (stop - ((0.5 - Math.abs(curValue - 0.5)) * length));
//        if (curValue < length * 0.6) {
//            stop = (float) (curValue + start);
//        } else {
//
//            stop = (float) (curValue * 0.5 + start);
//        }
        Log.i("circle", "start = " + start + ", stop = " + stop);

        pathMeasure.getSegment(start, stop, destPath, true);  // 注意：stop 值需要大于 start 值，stop > start
        canvas.drawPath(destPath, paint);
    }
}
