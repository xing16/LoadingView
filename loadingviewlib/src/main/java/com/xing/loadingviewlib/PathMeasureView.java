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

public class PathMeasureView extends View {

    private Paint paint;

    private Paint circlePaint;

    private Paint arrowPaint;

    private int mWidth;

    private int mHeight;

    private Path path;

    private PathMeasure pathMeasure;

    private float[] pos;

    private float[] tan;

    private Path arrowPath;

    private float curValue;


    private Path destPath;

    public PathMeasureView(Context context) {
        this(context, null);
    }

    public PathMeasureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(4);
        paint.setStyle(Paint.Style.STROKE);


        circlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        circlePaint.setColor(Color.BLUE);
        circlePaint.setStrokeWidth(2);
        circlePaint.setStyle(Paint.Style.FILL);

        arrowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        arrowPaint.setColor(Color.BLUE);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPath = new Path();

        pos = new float[2];
        tan = new float[2];


    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;

        path = new Path();
        path.moveTo(20, 20);
        path.lineTo(200, 20);
        path.lineTo(200, 400);
        destPath = new Path();
        pathMeasure = new PathMeasure();

        startAnimation();
    }


    public void startAnimation() {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setDuration(3000);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curValue = (float) animation.getAnimatedValue();
//                pathMeasure.getPosTan(curValue, pos, tan);  // 将 path 路径中各个点的坐标值赋值到 pos 中
//                Log.i("Path", "pos = " + Arrays.toString(pos));
//                Log.i("Path", "tan = " + Arrays.toString(tan));   //
                invalidate();
            }
        });
        valueAnimator.start();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        destPath.reset();
        destPath.lineTo(0, 0);
        pathMeasure.setPath(path, true);
        Log.e("debug", "PathMeasure.getLength() = " + pathMeasure.getLength());
        pathMeasure.getSegment(0, pathMeasure.getLength() * curValue, destPath, true);
        canvas.drawPath(destPath, paint);  // 绘制线段路径
//        Path arrowPath = new Path();
//        arrowPath.moveTo(pos[0], pos[1] - 25);
//        arrowPath.lineTo(pos[0] + 25, pos[1]);
//        arrowPath.lineTo(pos[0], pos[1] + 25);
//        canvas.drawPath(arrowPath, arrowPaint);  // 绘制箭头标志
//        canvas.drawCircle(pos[0], pos[1], 10, circlePaint);
    }
}
