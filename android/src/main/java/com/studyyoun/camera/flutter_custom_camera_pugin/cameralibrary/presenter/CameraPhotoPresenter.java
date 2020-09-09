package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;


import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraContact;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraPhotoGraphCallback;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.utils.CameraPhotoFromPhotoAlbum;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class CameraPhotoPresenter implements CameraContact.PhotoPresenter {
    //相册
    private Uri mImageUri;
    private String mMImagePath;

    @Override
    public void openCapTureGroupFunction(Context context) {
        //同样new一个file用于存放照片
        File imageFile = new File(Environment
                .getExternalStorageDirectory(), "outputImage.jpg");
        if (imageFile.exists()) {
            imageFile.delete();
        }
        try {
            imageFile.createNewFile();
        } catch (IOException e) {
            // TODO Auto-generated catch block
        }
        //转换成Uri
        mImageUri = Uri.fromFile(imageFile);
        mMImagePath = imageFile.getPath();
        //开启选择呢绒界面
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        //设置可以缩放
        intent.putExtra("scale", true);
        //设置可以裁剪
        intent.putExtra("crop", false);
        intent.setType("image/*");
        //设置输出位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //开始选择
        ((Activity) context).startActivityForResult(intent, 12);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data, Context context, CameraPhotoGraphCallback callback) {

        if (resultCode == RESULT_OK) {
            if (requestCode == 12) {
                try {
                    String photh = CameraPhotoFromPhotoAlbum.getRealPathFromUri(context, data.getData());
                    if (callback != null) {
                        callback.onSuccess(photh);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (callback != null) {
                        callback.onFaile(e.getMessage());
                    }
                }

            }
        }
    }
}
