package com.neu.beautydemo.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lancer on 2016/11/29.
 */

public class LineView extends View {
    private static final String X_KEY = "Xpos";
    private final static String Y_KEY = "Ypos";

    private List<Map<String, Integer>> mListPoint = new ArrayList<Map<String,Integer>>();

    int[][] X = new int[7][3];
    int[][] Y = new int[7][3];//接下来，建立一个类

    public LineView(Context context) {
        super(context);
    }
    public LineView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public LineView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(40);
        mPaint.setStrokeWidth(5);
        mPaint.setAntiAlias(true);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
//        canvas.drawPaint(mPaint);
//        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));

        for (int i=0; i<X.length; i++)
        {
            for (int j=0; j<3; j++){
                if (j > 0)
                {
                    canvas.drawLine(X[i][j-1], Y[i][j-1], X[i][j], Y[i][j], mPaint);
//                canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                }
            }
        }
    }

    public void setLinePoint(int startX,int startY,int stopX,int stopY)
    {
        X[0][0] = startX;Y[0][0] = startY;
        X[0][1] = stopX;Y[0][1] = startY;
        X[0][2] = stopX;Y[0][2] = stopY;
//        invalidate();//??
    }
    public void setLinePoint1(int startX,int startY,int stopX,int stopY)
    {
        X[1][0] = startX;Y[1][0] = startY;
        X[1][1] = stopX;Y[1][1] = startY;
        X[1][2] = stopX;Y[1][2] = stopY;
//        invalidate();//??
    }
    public void setLinePoint2(int startX,int startY,int stopX,int stopY)
    {
        X[2][0] = startX;Y[2][0] = startY;
        X[2][1] = stopX;Y[2][1] = startY;
        X[2][2] = stopX;Y[2][2] = stopY;
//        invalidate();//??
    }
    public void setLinePoint3(int startX,int startY,int stopX,int stopY)
    {
        X[3][0] = startX;Y[3][0] = startY;
        X[3][1] = stopX;Y[3][1] = startY;
        X[3][2] = stopX;Y[3][2] = stopY;
//        invalidate();//??
    }
    public void setLinePoint4(int startX,int startY,int stopX,int stopY)
    {
        X[4][0] = startX;Y[4][0] = startY;
        X[4][1] = stopX;Y[4][1] = startY;
        X[4][2] = stopX;Y[4][2] = stopY;
//        invalidate();//??
    }
    public void setLinePoint5(int startX,int startY,int stopX,int stopY)
    {
        X[5][0] = startX;Y[5][0] = startY;
        X[5][1] = stopX;Y[5][1] = startY;
        X[5][2] = stopX;Y[5][2] = stopY;
//        invalidate();//??
    }
    public void setLinePoint6(int startX,int startY,int stopX,int stopY)
    {
        X[6][0] = startX;Y[6][0] = startY;
        X[6][1] = stopX;Y[6][1] = startY;
        X[6][2] = stopX;Y[6][2] = stopY;
//        invalidate();//??
    }

}
