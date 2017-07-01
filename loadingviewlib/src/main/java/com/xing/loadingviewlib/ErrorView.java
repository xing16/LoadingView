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

public class ErrorView extends View {

    private Paint paint;


    private Path path;

    private Path destPath;


    private int mWidth;

    private int mHeight;

    private float curValue;


    private PathMeasure pathMeasure;
    private Path originPath;


    public ErrorView(Context context) {
        this(context, null);
    }

    public ErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        originPath = new Path();

//        path = new Path();
//        path.moveTo(100, 300);
//        path.lineTo(140, 340);
//        path.lineTo(220, 240);

//        originPath.addPath(path);
        originPath.addCircle(220, 240, 50, Path.Direction.CW);
        originPath.addCircle(300, 400, 60, Path.Direction.CW);


        destPath = new Path();


        pathMeasure = new PathMeasure(originPath, false);


    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, pathMeasure.getLength());
        valueAnimator.setDuration(1000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curValue = (float) animation.getAnimatedValue();
                Log.i("error", "curValue = " + curValue);
                postInvalidate();
            }
        });
        valueAnimator.start();

    }

    @Override
    protected void onDraw(Canvas canvas) {
//        canvas.drawPath(originPath, paint);
        destPath.reset();   // 清除上次绘制的路径
        destPath.lineTo(0, 0);
        pathMeasure.getSegment(0, curValue, destPath, true);
//        Log.i("error", "res =  " + pathMeasure.nextContour());
        Log.i("error", "destPath = " + destPath);
        canvas.drawPath(destPath, paint);
    }


}
