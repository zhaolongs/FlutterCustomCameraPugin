package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Region;
import android.view.View;

/**
 * Created by haibin
 * on 2016/12/2.
 */

public class CropFloatView extends View {
    private int mCropWidth;//设置裁剪宽度
    private int mCropHeight;//设置裁剪高度

    private int mHOffset;//水平偏移量
    private int mVOffset;//垂直偏移量

    private CropDrawable mCropDrawable;
    private Rect mFloatRect = new Rect();
    private boolean isCrop;

    public CropFloatView(Context context) {
        super(context);
        mCropDrawable = new CropDrawable(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        cropDrawable();
        canvas.save();
        canvas.clipRect(mFloatRect, Region.Op.DIFFERENCE);
        canvas.drawColor(Color.parseColor("#a0000000"));
        canvas.restore();
        mCropDrawable.draw(canvas);
    }

    public CropDrawable getCropDrawable() {
        return mCropDrawable;
    }

    private void cropDrawable() {
        if (isCrop) return;
        mCropDrawable.setRegion(mFloatRect);
        isCrop = true;
    }

    public void setCropWidth(int mCropWidth) {
        this.mCropWidth = mCropWidth;
        mCropDrawable.setCropWidth(mCropWidth);
    }

    public void setCropHeight(int mCropHeight) {
        this.mCropHeight = mCropHeight;
        mCropDrawable.setCropHeight(mCropHeight);
    }

    public void setHOffset(int mHOffset) {
        this.mHOffset = mHOffset;
    }

    public void setVOffset(int mVOffset) {
        this.mVOffset = mVOffset;
    }
}
