package com.xing.loadingviewlib;

import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

/**
 * Created by Administrator on 2017/6/24.
 */

public class LinePathView extends View {


    private ValueAnimator valueAnimator;

    private Path path;

    private int mWidth;

    private int mHeight;

    private int radius;

    private Paint paint;

    private PointF curPointF = new PointF();

    private PointF pointF = new PointF();

    public LinePathView(Context context) {
        this(context, null);
    }

    public LinePathView(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        radius = Math.min(mWidth, mHeight) / 2;
        path = new Path();
        path.moveTo(-radius * 2 / 3, 0);
        initAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(mWidth / 2, mHeight / 2);
        path.lineTo(curPointF.x, curPointF.y);
        canvas.drawPath(path, paint);
//
//        canvas.drawCircle(curPointF.x, curPointF.y, 10, paint);
        canvas.restore();


    }

    private void initAnimator() {
        valueAnimator = ValueAnimator.ofObject(new PointEvaluator(), new PointF(-radius * 2 / 3, 0),
                new PointF(-radius / 8, radius / 2), new PointF(radius * 2 / 3, -radius * 1 / 2));
        valueAnimator.setDuration(30000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                curPointF = (PointF) animation.getAnimatedValue();
                Log.i("aa", "curPointF = " + curPointF);
                invalidate();
            }
        });
        valueAnimator.start();
    }

    private void initPointAnimator() {
        ValueAnimator valueAnimator = ValueAnimator.ofObject(new PointEvaluator(),
                new PointF(20, 20), new PointF(300, 300), new PointF(300, 40));
        valueAnimator.setDuration(2000);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {


            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pointF = (PointF) animation.getAnimatedValue();
                invalidate();
            }
        });
        valueAnimator.start();

    }


    static class PointEvaluator implements TypeEvaluator<PointF> {

        private PointF curValue;

        @Override
        public PointF evaluate(float fraction, PointF startValue, PointF endValue) {
            Log.i("debug", "fraction = " + fraction);

            float curX = (endValue.x - startValue.x) * fraction + startValue.x;
            float curY = (endValue.y - startValue.y) * fraction + startValue.y;
            if (curValue == null) {
                curValue = new PointF(curX, curY);
            } else {
                curValue.set(curX, curY);
            }
            Log.i("debug", "curX = " + curX);
            Log.i("debug", "curY = " + curY);
            return curValue;
        }
    }


}
