package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.studyyoun.camera.flutter_custom_camera_pugin.R;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraSaveCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter.CameraImageSavePresenter;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter.CameraImageShowPresenter;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.view.CropLayout;


public class CameraExampCorpActivity extends Activity {
    private String mUrl;
    private CropLayout mCropLayout;
    private int mCropHeight;
    private int mCropWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mUrl = getIntent().getStringExtra("imageUrl");
        mCropHeight =getIntent().getIntExtra("mCropHeight",500);
        mCropWidth =getIntent().getIntExtra("mCropWidth", 500);
        setContentView(R.layout.camera_examp_crop_activity_layout);
        mCropLayout = findViewById(R.id.cropLayout);
        mCropLayout.setCropHeight(mCropHeight);
        mCropLayout.setCropWidth(mCropWidth);
        commonDelayFunction();
    }


    protected void commonDelayFunction() {
        findViewById(R.id.tv_crop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //保存裁剪内容
                saveCropImageFunction();
            }
        });
        findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        CameraImageShowPresenter.getInstance().showImage(mCropLayout.getImageView(),mUrl);

        mCropLayout.start();
    }

    private void saveCropImageFunction() {
        CameraImageSavePresenter.getInstance().saveImage(this, mCropLayout.cropBitmap(), new CameraSaveCallBack() {
            @Override
            public void cameraFaile(int errCode, String message) {
                Toast.makeText(CameraExampCorpActivity.this,"保存异常 ",Toast.LENGTH_LONG).show();
            }

            @Override
            public void cameraSuccess(String mFilePath) {
                Toast.makeText(CameraExampCorpActivity.this,"已保存 "+mFilePath,Toast.LENGTH_LONG).show();
                Intent lIntent = new Intent("cameraactivityfinish");
                lIntent.putExtra("filePath",mFilePath);
                lIntent.putExtra("code",100);
                CameraExampCorpActivity.this.sendBroadcast(lIntent);
                CameraExampCorpActivity.this.finish();
            }

            @Override
            public void cameraPermisExit() {

            }
        });

    }
}
