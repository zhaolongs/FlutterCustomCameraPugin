package com.studyyoun.camera.flutter_custom_camera_pugin.camera;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.studyyoun.camera.flutter_custom_camera_pugin.R;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraXUtils;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.activity.CameraExampShowActivity;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraPhotoGraphCallback;
import static com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraConfig.LOGTAG;


/**
 * 1、打开自定义相机
 * 2、前后镜头切换
 * 3、打开相册
 */
public class CameraOpenActivity extends Activity {

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
    
    private CameraConfigOptions mCameraConfigOptions ;
    private ImageView mCameraFlashImageView;
    private LinearLayout mPhotoAlbumLayout;
    private ImageView mCameraChangeImageView;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNoTitle();
        setContentView(getCommonLayoutId());
        mContext = this;
        mDisplayMetrics = mContext.getResources().getDisplayMetrics();
        
        mCameraConfigOptions = (CameraConfigOptions) getIntent().getSerializableExtra("cameraConfigOptions");

        Intent lIntent = getIntent();
        mCropWidth = lIntent.getIntExtra("cropWidth",500);
        mCropHeight = lIntent.getIntExtra("cropHeight",500);

        mMICrop = lIntent.getBooleanExtra("mICrop",false);
        commonInitView();
        commonFunction();
        commonDelayFunction();
        
        commonOptions();
    }
    
    //根据配置设置页面
    private void commonOptions() {
    
        if (mCameraConfigOptions.isShowFlashButtonCamera) {
            mCameraFlashImageView.setVisibility(View.VISIBLE);
        }else{
            mCameraFlashImageView.setVisibility(View.GONE);
        }
    
        if (mCameraConfigOptions.isShowPhotoAlbum) {
            mPhotoAlbumLayout.setVisibility(View.VISIBLE);
        }else{
            mPhotoAlbumLayout.setVisibility(View.GONE);
        }
        if (mCameraConfigOptions.isShowSelectCamera) {
            mCameraChangeImageView.setVisibility(View.VISIBLE);
        }else{
            mCameraChangeImageView.setVisibility(View.GONE);
        }
    
        
        
    }
    
    protected void requestNoTitle() {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    protected int getCommonLayoutId() {
        return R.layout.camera_open_activity_layout;
    }


    protected void commonInitView() {
        mBackLayout = findViewById(R.id.ll_base_back);
        mRootView = findViewById(R.id.fr_root_view);
    
        mCameraFlashImageView = findViewById(R.id.camera_flash);
        mPhotoAlbumLayout = findViewById(R.id.tv_photo_album);
    
        mCameraChangeImageView = findViewById(R.id.camera_change);
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
                try {
                    CameraXUtils.getInstance().onCameraClick();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mCameraChangeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraXUtils.getInstance().changeCameraClick();
            }
        });
    
        mPhotoAlbumLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "打开相册");
                CameraXUtils.getInstance().openCapTureGroupFunction(CameraOpenActivity.this);
            }
        });
        mCameraFlashImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOGTAG, "打开闪光灯");
				CameraXUtils.getInstance().openCameraFlashFunction();
            }
        });

        mBackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送取消操作广播
                Intent lIntent = new Intent(CameraContas.actionFinishCamera);
                lIntent.putExtra("code", 102);
                CameraOpenActivity.this.sendBroadcast(lIntent);
                CameraOpenActivity.this.finish();
            }
        });

    }

    private CameraCallBack mCameraCallBack = new CameraCallBack() {
        @Override
        public void cameraFaile(int errCode, String message) {
            Log.d(LOGTAG, errCode + " + errCode + " + message + "  message");
            //发送取消操作广播
            Intent lIntent = new Intent(CameraContas.actionFinishCamera);
            lIntent.putExtra("code", 102);
            CameraOpenActivity.this.sendBroadcast(lIntent);
        }

        @Override
        public void cameraSuccess(String mFilePath) {
            Log.d(LOGTAG, "success  " + mFilePath);
            showImageFunction(mFilePath,"camera");
        }

        @Override
        public void cameraPermisExit() {
            CameraOpenActivity.this.finish();
        }
    };

    private CameraPhotoGraphCallback mCameraPhotoGraphCallback = new CameraPhotoGraphCallback() {
        @Override
        public void onSuccess(String s) {
            Log.d(LOGTAG, "相册路径处理 " + s);
            showImageFunction(s, "photo");
        }

        @Override
        public void onFaile(String s) {
            //异常
            Log.e(LOGTAG, "相册选取图片失败" + s);
            Intent lIntent = new Intent("cameraactivityfinish");
            lIntent.putExtra("code",101);
            CameraOpenActivity.this.sendBroadcast(lIntent);
        }
    };
    
    /**
     *
     * @param mFilePath
     * @param source 来源 相机 相册
     */
    private void showImageFunction(String mFilePath, String source) {
        this.mFilePath = mFilePath;
        //加载显示图片
        final Intent lIntent = new Intent(CameraOpenActivity.this, CameraExampShowActivity.class);
        lIntent.putExtra("imageUrl", mFilePath);
        lIntent.putExtra("mCropHeight", mCropHeight);
        lIntent.putExtra("mCropWidth", mCropWidth);
        lIntent.putExtra("mICrop", mMICrop);
        lIntent.putExtra("source", source);
        Log.d(LOGTAG, "imageUrl " + mFilePath);
        
        ///发一个广播
        Intent intentRecivier=new Intent();
        intentRecivier.setAction("cameraRecivierAction");
        intentRecivier.putExtra("imageUrl", mFilePath);
        intentRecivier.putExtra("mCropHeight", mCropHeight);
        intentRecivier.putExtra("mCropWidth", mCropWidth);
        intentRecivier.putExtra("mICrop", mMICrop);
        CameraOpenActivity.this.sendBroadcast(intentRecivier);
    
        CameraOpenActivity.this.startActivity(lIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //权限回调
        mCameraUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mCameraUtils.onActivityResult(requestCode, resultCode, data, mContext, mCameraPhotoGraphCallback);
        }else{
            //发送取消操作广播
            Intent lIntent = new Intent(CameraContas.actionFinishCamera);
            lIntent.putExtra("code", 102);
            CameraOpenActivity.this.sendBroadcast(lIntent);
        }
    }
    
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            //发送取消操作广播
            Intent lIntent = new Intent(CameraContas.actionFinishCamera);
            lIntent.putExtra("code", 102);
            CameraOpenActivity.this.sendBroadcast(lIntent);
            
            finish();
            //不执行父类点击事件
            return true;
        }
        //继续执行父类其他点击事件
        return super.onKeyUp(keyCode, event);
    }
    class FinishActivityRecivier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            CameraOpenActivity.this.finish();
        }
    }
}
