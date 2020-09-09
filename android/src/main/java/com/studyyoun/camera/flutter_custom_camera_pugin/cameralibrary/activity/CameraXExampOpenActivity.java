package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.studyyoun.camera.flutter_custom_camera_pugin.R;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraXUtils;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraPhotoGraphCallback;

import static com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraConfig.LOGTAG;


/**
 * 1、打开自定义相机
 * 2、前后镜头切换
 * 3、打开相册
 */
public class CameraXExampOpenActivity extends Activity {

    private CameraXUtils mCameraUtils;
    private LinearLayout mBackLayout;
    private Uri mImageUri;
    private String mMImagePath;
    private String mKey;
    private String mFilePath;
    private FrameLayout mRootView;
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private FinishActivityRecivier mFinishActivityRecivier;
    private int mCropWidth;
    private int mCropHeight;
    private boolean mMICrop;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNoTitle();
        setContentView(getCommonLayoutId());
        mContext = this;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        Intent lIntent = getIntent();
        mCropWidth = lIntent.getIntExtra("cropWidth",500);
        mCropHeight = lIntent.getIntExtra("cropHeight",500);
        mMICrop = lIntent.getBooleanExtra("mICrop",false);
        commonInitView();
        commonFunction();
        commonDelayFunction();
    }

    protected void requestNoTitle() {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    protected int getCommonLayoutId() {
        return R.layout.camera_examp_open_activity_layout;
    }


    protected void commonInitView() {
        mBackLayout = findViewById(R.id.ll_base_back);
        mRootView = findViewById(R.id.fr_root_view);
    }


    protected void commonFunction() {
        //初始化自定义相机
        mCameraUtils = CameraXUtils.getInstance().setXContinuous(true).initCamerView(this, mRootView, mDisplayMetrics.widthPixels, mDisplayMetrics.heightPixels, mCameraCallBack);

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCameraUtils != null) {
            mCameraUtils.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraUtils.destore();
        unregisterReceiver(mFinishActivityRecivier);
    }


    protected void commonDelayFunction() {


        mFinishActivityRecivier = new FinishActivityRecivier();
        registerReceiver(mFinishActivityRecivier, new IntentFilter("cameraactivityfinish"));

        findViewById(R.id.aliyun_record_bg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraXUtils.getInstance().onCameraClick();
            }
        });
        findViewById(R.id.camera_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraXUtils.getInstance().changeCameraClick();
            }
        });

        findViewById(R.id.tv_photo_album).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "打开相册");
                CameraXUtils.getInstance().openCapTureGroupFunction(CameraXExampOpenActivity.this);
            }
        });
        findViewById(R.id.camera_flash).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "打开闪光灯");
                CameraXUtils.getInstance().openCameraFlashFunction();
            }
        });

        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraXExampOpenActivity.this.finish();
            }
        });

    }

    private CameraCallBack mCameraCallBack = new CameraCallBack() {
        @Override
        public void cameraFaile(int errCode, String message) {
            Log.d(LOGTAG, errCode + " + errCode + " + message + "  message");
            Intent lIntent = new Intent("cameraactivityfinish");
            lIntent.putExtra("code",102);
            CameraXExampOpenActivity.this.sendBroadcast(lIntent);
        }

        @Override
        public void cameraSuccess(String mFilePath) {
            Log.d(LOGTAG, "success  " + mFilePath);
            showImageFunction(mFilePath);
        }

        @Override
        public void cameraPermisExit() {
            CameraXExampOpenActivity.this.finish();
        }
    };

    private CameraPhotoGraphCallback mCameraPhotoGraphCallback = new CameraPhotoGraphCallback() {
        @Override
        public void onSuccess(String s) {
            Log.d(LOGTAG, "相册路径处理 " + s);
            showImageFunction(s);
        }

        @Override
        public void onFaile(String s) {
            //异常
            Log.e(LOGTAG, "相册选取图片失败" + s);
            Intent lIntent = new Intent("cameraactivityfinish");
            lIntent.putExtra("code",101);
            CameraXExampOpenActivity.this.sendBroadcast(lIntent);
        }
    };

    private void showImageFunction(String mFilePath) {
        this.mFilePath = mFilePath;
        //加载显示图片
        final Intent lIntent = new Intent(CameraXExampOpenActivity.this, CameraExampShowActivity.class);
        lIntent.putExtra("imageUrl", mFilePath);
        lIntent.putExtra("mCropHeight", mCropHeight);
        lIntent.putExtra("mCropWidth", mCropWidth);
        lIntent.putExtra("mICrop", mMICrop);
        Log.d(LOGTAG, "imageUrl " + mFilePath);
        CameraXExampOpenActivity.this.startActivity(lIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //权限回调
        mCameraUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mCameraUtils.onActivityResult(requestCode, resultCode, data, mContext, mCameraPhotoGraphCallback);
        }
    }

    class FinishActivityRecivier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            CameraXExampOpenActivity.this.finish();
        }
    }
}
