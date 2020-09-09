package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.lang.ref.WeakReference;

public class CameraImageShowPresenter {
    
    public interface OnCamerImageShowCallBack{
        void onFinish(int flag, String message);
    }
    

    private OnCamerImageShowCallBack mOnCamerImageShowCallBack;
    private ImageView mImageView;
    private Context mContext;
    private static CameraImageShowPresenter mCameraImageShowPresenter;
    private CameraImageShowPresenter(){

    }
    public static  CameraImageShowPresenter getInstance(){
        if (mCameraImageShowPresenter==null) {
            mCameraImageShowPresenter=new CameraImageShowPresenter();
        }
        return mCameraImageShowPresenter;
    }

    public void showImage(ImageView imageView,String locationImagePath){
        showImage(imageView,locationImagePath,null);
    }
    public void showImage(ImageView imageView,String locationImagePath,OnCamerImageShowCallBack imageShowCallBack){
        this.mImageView = imageView;
        this.mContext = imageView.getContext();
        this.mOnCamerImageShowCallBack = imageShowCallBack;
        CameraImageShowAsyncTask lCameraImageShowAsyncTask = new CameraImageShowAsyncTask(this, mContext, imageView,imageShowCallBack);
        lCameraImageShowAsyncTask.execute(locationImagePath);
    }

    static class CameraImageShowAsyncTask extends AsyncTask<String, Void, Bitmap> {

        private WeakReference<CameraImageShowPresenter> ref;
        private float maxWidth;
        private float maxHeight;
        private OnCamerImageShowCallBack mOnCamerImageShowCallBack;

        CameraImageShowAsyncTask(CameraImageShowPresenter activity, Context context, ImageView imageView, OnCamerImageShowCallBack imageShowCallBack) {
            ref = new WeakReference<>(activity);
            maxWidth = imageView.getMeasuredWidth();
            maxHeight =imageView.getMeasuredHeight();
            mOnCamerImageShowCallBack = imageShowCallBack;
        }

        @Override
        protected Bitmap doInBackground(String... thumbnailPaths) {
            Bitmap bmp = null;
            try {
                if (ref != null) {
                    CameraImageShowPresenter publishActivity = ref.get();
                    if (publishActivity != null) {
                        String path = thumbnailPaths[0];
                        if (TextUtils.isEmpty(path)) {
                            return null;
                        }
                        File thumbnail = new File(path);
                        if (!thumbnail.exists()) {
                            return null;
                        }
                        BitmapFactory.Options opt = new BitmapFactory.Options();
                        opt.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(path, opt);
                        float bw = opt.outWidth;
                        float bh = opt.outHeight;
                        float scale;
                        if (bw > bh) {
                            scale = bw / maxWidth;
                        } else {
                            scale = bh / maxHeight;
                        }
                        boolean needScaleAfterDecode = scale != 1;
                        opt.inJustDecodeBounds = false;
                        bmp = BitmapFactory.decodeFile(path, opt);
                        if (needScaleAfterDecode) {
                            bmp = publishActivity.scaleBitmap(bmp, scale);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("errMessage" ,""+ e.getMessage());
            }

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null && ref != null && ref.get() != null) {
                ref.get().initThumbnail(bitmap);
            }
            if (mOnCamerImageShowCallBack != null) {
                mOnCamerImageShowCallBack.onFinish(0,"success");
            }
        }
    }

    private void initThumbnail(Bitmap thumbnail) {
        if (thumbnail != null&&mImageView!=null) {
            mImageView.setImageBitmap(thumbnail);
        }else {

        }
    }

    private Bitmap scaleBitmap(Bitmap bmp, float scale) {
        Matrix mi = new Matrix();
        mi.setScale(1 / scale, 1 / scale);
        Bitmap temp = bmp;
        bmp = Bitmap.createBitmap(temp, 0, 0, temp.getWidth(), temp.getHeight(), mi, false);
        temp.recycle();
        return bmp;
    }
}
