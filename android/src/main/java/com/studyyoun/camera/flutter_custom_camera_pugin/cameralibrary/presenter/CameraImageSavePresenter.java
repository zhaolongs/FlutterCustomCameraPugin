package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;


import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraSaveCallBack;

import java.io.File;
import java.io.FileOutputStream;

public class CameraImageSavePresenter {

    private Context mContext;
    private static CameraImageSavePresenter mCameraImageShowPresenter;
    public static final String IMAGE_TYPE = ".jpeg";
    private CameraSaveCallBack mCameraSaveCallBack;
    private CameraImageSavePresenter(){

    }
    public static CameraImageSavePresenter getInstance(){
        if (mCameraImageShowPresenter==null) {
            mCameraImageShowPresenter=new CameraImageSavePresenter();
        }
        return mCameraImageShowPresenter;
    }
    public static String getPhotoFileName() {
        return Environment.getExternalStorageDirectory().getPath() + File.separator + System.currentTimeMillis() + IMAGE_TYPE;
    }

    public void saveImage(Context context,Bitmap bitmap,CameraSaveCallBack callBack){
        this.mContext =context;
        this.mCameraSaveCallBack=callBack;
        CameraImageShowAsyncTask lCameraImageShowAsyncTask = new CameraImageShowAsyncTask( mContext,  this.mCameraSaveCallBack);
        lCameraImageShowAsyncTask.execute(bitmap);
    }


    static class CameraImageShowAsyncTask extends AsyncTask<Bitmap, Void, String> {

       private CameraSaveCallBack mCameraSaveCallBack;
        CameraImageShowAsyncTask( Context context,CameraSaveCallBack callBack) {
           mCameraSaveCallBack = callBack;
        }

        @Override
        protected String doInBackground(Bitmap... bitmaps) {
            FileOutputStream outStream = null;
            String fileName = getPhotoFileName();
            Bitmap lBitmap = bitmaps[0];
            try {
                outStream = new FileOutputStream(fileName);
                // 把数据写入文件，100表示不压缩
                lBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                return fileName;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (outStream != null) {
                        // 记得要关闭流！
                        outStream.close();
                    }
                    if (lBitmap != null) {
                        lBitmap.recycle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        protected void onPostExecute(String path) {
            super.onPostExecute(path);
            if (mCameraSaveCallBack != null) {
                if (path==null){
                    mCameraSaveCallBack.cameraFaile(-2,"异常 ");
                }else {
                    mCameraSaveCallBack.cameraSuccess(path);
                }
            }

        }
    }

}
