package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.Display;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

public class CropLayout extends FrameLayout {
    private int mCropWidth = 500;//设置裁剪宽度
    private int mCropHeight = 500;//设置裁剪高度

    private ZoomImageView mZoomImageView;
    private CropFloatView mCropView;

    public CropLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mZoomImageView = new ZoomImageView(context);
        mCropView = new CropFloatView(context);
        ViewGroup.LayoutParams lp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        this.addView(mZoomImageView, lp);
        this.addView(mCropView, lp);
    }

    public ZoomImageView getImageView() {
        return mZoomImageView;
    }


    public Bitmap cropBitmap() {
        return mZoomImageView.cropBitmap();
    }

    public void setCropWidth(int mCropWidth) {
        this.mCropWidth = mCropWidth;
        mCropView.setCropWidth(mCropWidth);
        mZoomImageView.setCropWidth(mCropWidth);
    }

    public void setCropHeight(int mCropHeight) {
        this.mCropHeight = mCropHeight;
        mCropView.setCropHeight(mCropHeight);
        mZoomImageView.setCropHeight(mCropHeight);
    }

    public void start() {
        int height = getScreenHeight(getContext());
        int width = getScreenWidth(getContext());
        int mHOffset = (width - mCropWidth) / 2;
        int mVOffset = (height - mCropHeight) / 2;
        mZoomImageView.setHOffset(mHOffset);
        mZoomImageView.setVOffset(mVOffset);
        mCropView.setHOffset(mHOffset);
        mCropView.setVOffset(mVOffset);
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
}
