package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements
        ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener,
        ViewTreeObserver.OnGlobalLayoutListener {

    private int mCropWidth;//设置裁剪宽度
    private int mCropHeight;//设置裁剪高度


    private int mOffset = 0;
    private int mVOffset = 0;

    private float SCALE_MAX = 4.0f;
    private float SCALE_MID = 2.0f;
    private float SCALE_MIN = 1.0f;

    private float mScale = 1.0f;
    private boolean isFirst = true;

    private final float[] mMatrixValues = new float[9];

    private ScaleGestureDetector mScaleGestureDetector = null;
    private Matrix mScaleMatrix = new Matrix();

    private GestureDetector mGestureDetector;
    private boolean isAutoScale;

    private boolean isInit;

    private float mLastX;
    private float mLastY;

    private boolean isCanDrag;
    private int lastPointerCount;

    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setScaleType(ImageView.ScaleType.MATRIX);
        mGestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {
                    @Override
                    public boolean onDoubleTap(MotionEvent e) {
                        if (isAutoScale)
                            return true;
                        float x = e.getX();
                        float y = e.getY();
                        if (getScale() < SCALE_MID) {
                            postDelayed(new ScaleRunnable(SCALE_MID, x, y), 16);
                            isAutoScale = true;
                        } else {
                            postDelayed(new ScaleRunnable(mScale, x, y), 16);
                            isAutoScale = true;
                        }
                        return true;
                    }
                });
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(this);
    }


    private class ScaleRunnable implements Runnable {
        static final float BIGGER = 1.07f;
        static final float SMALLER = 0.93f;
        private float mTargetScale;
        private float mScale;
        private float x;
        private float y;

        ScaleRunnable(float targetScale, float x, float y) {
            this.mTargetScale = targetScale;
            this.x = x;
            this.y = y;
            if (getScale() < mTargetScale) {
                mScale = BIGGER;
            } else {
                mScale = SMALLER;
            }

        }

        @Override
        public void run() {
            mScaleMatrix.postScale(mScale, mScale, x, y);
            checkBorder();
            setImageMatrix(mScaleMatrix);

            final float currentScale = getScale();
            if (((mScale > 1f) && (currentScale < mTargetScale)) || ((mScale < 1f) && (mTargetScale < currentScale))) {
                postDelayed(this, 16);
            } else {
                final float deltaScale = mTargetScale / currentScale;
                mScaleMatrix.postScale(deltaScale, deltaScale, x, y);
                checkBorder();
                setImageMatrix(mScaleMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scale = getScale();
        float scaleFactor = detector.getScaleFactor();

        if (getDrawable() == null)
            return true;
        if ((scale < SCALE_MAX && scaleFactor > SCALE_MIN)
                || (scale > mScale && scaleFactor < SCALE_MIN)) {
            if (scaleFactor * scale < SCALE_MIN) {
                scaleFactor = SCALE_MIN / scale;
            }
            if (scaleFactor * scale > SCALE_MAX) {
                scaleFactor = SCALE_MAX / scale;
            }

            mScaleMatrix.postScale(scaleFactor, scaleFactor,
                    detector.getFocusX(), detector.getFocusY());
            checkBorder();
            setImageMatrix(mScaleMatrix);
        }
        return true;

    }

    private RectF getMatrixRectF() {
        Matrix matrix = mScaleMatrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d) {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            matrix.mapRect(rect);
        }
        return rect;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event))
            return true;
        mScaleGestureDetector.onTouchEvent(event);

        float x = 0, y = 0;
        final int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }
        x = x / pointerCount;
        y = y / pointerCount;

        if (pointerCount != lastPointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }

        lastPointerCount = pointerCount;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isCanDrag(dx, dy);
                }
                if (isCanDrag) {
                    if (getDrawable() != null) {

                        RectF rectF = getMatrixRectF();
                        if (rectF.width() <= getWidth() - mOffset * 2) {
                            dx = 0;
                        }
                        if (rectF.height() <= getHeight() - mVOffset * 2) {
                            dy = 0;
                        }
                        mScaleMatrix.postTranslate(dx, dy);
                        checkBorder();
                        setImageMatrix(mScaleMatrix);
                    }
                }
                mLastX = x;
                mLastY = y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                lastPointerCount = 0;
                break;
        }

        return true;
    }

    public final float getScale() {
        mScaleMatrix.getValues(mMatrixValues);
        return mMatrixValues[Matrix.MSCALE_X];
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mScaleMatrix = null;
        getViewTreeObserver().removeGlobalOnLayoutListener(this);
    }

    @Override
    protected boolean setFrame(int l, int t, int r, int b) {
        if (isInit) return false;
        boolean change = super.setFrame(l, t, r, b);
        Drawable drawable = getDrawable();
        if (drawable == null) return false;
        int boundWidth = drawable.getBounds().width();
        int boundHeight = drawable.getBounds().height();
        if (boundWidth > mCropWidth || boundHeight > mCropHeight) return false;
        int width = getWidth();
        int height = getHeight();
        mScale = (float) width / boundWidth;
        isInit = true;
        postDelayed(new ScaleRunnable(mScale, width / 2, height / 2), 50);
        isAutoScale = false;
        return change;
    }


    @Override
    public void onGlobalLayout() {
        if (isFirst) {
            Drawable d = getDrawable();
            if (d == null)
                return;
            //mVOffset = (getHeight() - (getWidth() - 2 * mOffset)) / 2;

            int width = getWidth();
            int height = getHeight();

            int dw = d.getIntrinsicWidth();
            int dh = d.getIntrinsicHeight();
            float scale = 1.0f;
            if (dw < getWidth() - mOffset * 2
                    && dh > getHeight() - mVOffset * 2) {
                scale = (getWidth() * 1.0f - mOffset * 2) / dw;
            }

            if (dh < getHeight() - mVOffset * 2
                    && dw > getWidth() - mOffset * 2) {
                scale = (getHeight() * 1.0f - mVOffset * 2) / dh;
            }

            if (dw < getWidth() - mOffset * 2
                    && dh < getHeight() - mVOffset * 2) {
                float scaleW = (getWidth() * 1.0f - mOffset * 2)
                        / dw;
                float scaleH = (getHeight() * 1.0f - mVOffset * 2) / dh;
                scale = Math.max(scaleW, scaleH);
            }

            float sw = (float) (width - 2 * mOffset) / (float) dw;
            float sh = (float) (height - 2 * mVOffset) / (float) dh;

            SCALE_MIN = Math.max(sw, sh);
            if (SCALE_MIN >= 1.0F) SCALE_MIN = 1.0F;

            mScale = scale;
            SCALE_MID = mScale * 2;
            SCALE_MAX = mScale * 4;
            mScaleMatrix.postTranslate((width - dw) / 2, (height - dh) / 2);
            mScaleMatrix.postScale(scale, scale, getWidth() / 2,
                    getHeight() / 2);
            setImageMatrix(mScaleMatrix);
            isFirst = false;
        }

    }

    public Bitmap cropBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        draw(canvas);
        return Bitmap.createBitmap(bitmap, mOffset,
                mVOffset, getWidth() - 2 * mOffset,
                getWidth() - 2 * mOffset);
    }

    private void checkBorder() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.width() + 0.01 >= width - 2 * mOffset) {
            if (rect.left > mOffset) {
                deltaX = -rect.left + mOffset;
            }
            if (rect.right < width - mOffset) {
                deltaX = width - mOffset - rect.right;
            }
        }
        if (rect.height() + 0.01 >= height - 2 * mVOffset) {
            if (rect.top > mVOffset) {
                deltaY = -rect.top + mVOffset;
            }
            if (rect.bottom < height - mVOffset) {
                deltaY = height - mVOffset - rect.bottom;

            }
        }
        mScaleMatrix.postTranslate(deltaX, deltaY);

    }

    private boolean isCanDrag(float dx, float dy) {
        return Math.sqrt((dx * dx) + (dy * dy)) >= 0;
    }

    public void setHOffset(int hOffset) {
        this.mOffset = hOffset;
    }

    public void setVOffset(int vOffset) {
        this.mVOffset = vOffset;
    }

    public void setCropWidth(int mCropWidth) {
        this.mCropWidth = mCropWidth;
    }

    public void setCropHeight(int mCropHeight) {
        this.mCropHeight = mCropHeight;
    }
}
