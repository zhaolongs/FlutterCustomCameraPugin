package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by haibin
 * on 2016/12/1.
 */
@SuppressWarnings("ALL")
public class CropDrawable extends Drawable {
    private Context mContext;
    private int offset = 50;

    private Paint mCornerPaint = new Paint();
    private Paint mLinePaint = new Paint();
    private Paint mNineLinePaint = new Paint();

    private int mCropWidth = 800, mCropHeight = 800;

    private static final int RADIUS = 20;

    public int mLeft, mRight, mTop, mBottom;

    public CropDrawable(Context mContext) {
        this.mContext = mContext;
        initPaint();
    }

    private void initPaint() {
        mLinePaint.setColor(Color.WHITE);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStrokeWidth(2);
        mLinePaint.setStyle(Paint.Style.STROKE);

        mNineLinePaint.setColor(Color.WHITE);
        mNineLinePaint.setAntiAlias(true);
        mNineLinePaint.setStrokeWidth(1);
        mNineLinePaint.setStyle(Paint.Style.STROKE);

        mCornerPaint.setColor(Color.WHITE);
        mCornerPaint.setAntiAlias(true);
        mCornerPaint.setStrokeWidth(8);
        mCornerPaint.setStyle(Paint.Style.FILL);
    }
    /**
     * 获得屏幕的宽度
     *
     * @param context context
     * @return width
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }

    /**
     * 获得屏幕的高度
     *
     * @param context context
     * @return height
     */
    public static int getScreenHeight(Context context) {
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getHeight();
    }
    @Override
    public void draw(Canvas canvas) {
        int width = getScreenWidth(mContext);
        int height =getScreenHeight(mContext);
        mLeft = (width - mCropWidth) / 2;
        mTop = (height - mCropHeight) / 2;
        mRight = (width + mCropWidth) / 2;
        mBottom = (height + mCropHeight) / 2;
        Rect rect = new Rect(mLeft, mTop, mRight, mBottom);
        canvas.drawRect(rect, mLinePaint);
        //左上
        canvas.drawLine(mLeft, mTop, mLeft, mTop + 50, mCornerPaint);
        canvas.drawLine(mLeft - 4, mTop, mLeft + 50, mTop, mCornerPaint);

        //右上
        canvas.drawLine(mRight, mTop, mRight, mTop + 50, mCornerPaint);
        canvas.drawLine(mRight - 50, mTop, mRight + 4, mTop, mCornerPaint);

        //左下
        canvas.drawLine(mLeft, mBottom, mLeft + 50, mBottom, mCornerPaint);
        canvas.drawLine(mLeft, mBottom - 50, mLeft, mBottom + 4, mCornerPaint);

        //右下
        canvas.drawLine(mRight, mBottom, mRight, mBottom - 50, mCornerPaint);
        canvas.drawLine(mRight - 50, mBottom, mRight + 4, mBottom, mCornerPaint);

        int index = canvas.save();
        canvas.clipRect(rect);
        //画九宫格
        int vAvg = mCropWidth / 3;
        int hAvg = mCropHeight / 3;
        canvas.drawLine(mLeft + vAvg, mTop, mLeft + vAvg, mBottom, mNineLinePaint);
        canvas.drawLine(mLeft + vAvg * 2, mTop, mLeft + vAvg * 2, mBottom, mNineLinePaint);

        canvas.drawLine(mLeft, mTop + hAvg, mRight, mTop + hAvg, mNineLinePaint);
        canvas.drawLine(mLeft, mTop + hAvg * 2, mRight, mTop + hAvg * 2, mNineLinePaint);

        canvas.restoreToCount(index);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    public void offset(int x, int y) {
        getBounds().offset(x, y);
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(new Rect(mLeft, mTop, mRight, mBottom));
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.UNKNOWN;
    }

    public void setRegion(Rect rect) {
        int width = getScreenWidth(mContext);
        int height = getScreenHeight(mContext);
        rect.set((width - mCropWidth) / 2, (height - mCropHeight) / 2, (width + mCropWidth) / 2, (height + mCropHeight) / 2);
    }

    public int getLeft() {
        return mLeft;
    }


    public int getRight() {
        return mRight;
    }


    public int getTop() {
        return mCropHeight;
    }


    public int getBottom() {
        return mBottom;
    }

    public void setCropWidth(int mCropWidth) {
        this.mCropWidth = mCropWidth;
    }

    public void setCropHeight(int mCropHeight) {
        this.mCropHeight = mCropHeight;
    }
}
