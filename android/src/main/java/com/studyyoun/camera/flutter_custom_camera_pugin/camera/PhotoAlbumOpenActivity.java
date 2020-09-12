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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.studyyoun.camera.flutter_custom_camera_pugin.R;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraXUtils;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.activity.CameraExampShowActivity;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraPhotoGraphCallback;

import static com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraConfig.LOGTAG;


/**
 * 打开相册
 */
public class PhotoAlbumOpenActivity extends Activity {

    private CameraXUtils mCameraUtils;

    private Uri mImageUri;
    private String mMImagePath;
    private String mKey;
    private String mFilePath;
    private Context mContext;
    private DisplayMetrics mDisplayMetrics;
    private FinishActivityRecivier mFinishActivityRecivier;
    private int mCropWidth;
    private int mCropHeight;
    private boolean mMICrop;
    private CameraConfigOptions mCameraConfigOptions ;
    
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
        
        mCameraConfigOptions = (CameraConfigOptions) getIntent().getSerializableExtra("cameraConfigOptions");
        
        
        mCameraUtils = CameraXUtils.getInstance();
        CameraXUtils.getInstance().openCapTureGroupFunction(PhotoAlbumOpenActivity.this);
    
    
        mFinishActivityRecivier = new FinishActivityRecivier();
        registerReceiver(mFinishActivityRecivier, new IntentFilter("cameraactivityfinish"));
    }

    protected void requestNoTitle() {
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去除状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    protected int getCommonLayoutId() {
        return R.layout.photo_album_activity_layout;
    }



    @Override
    protected void onStart() {
        super.onStart();
     
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCameraUtils.destore();
        unregisterReceiver(mFinishActivityRecivier);
    }


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
            PhotoAlbumOpenActivity.this.sendBroadcast(lIntent);
        }
    };

    private void showImageFunction(String mFilePath) {
        this.mFilePath = mFilePath;
        //加载显示图片
        final Intent lIntent = new Intent(PhotoAlbumOpenActivity.this, CameraExampShowActivity.class);
        lIntent.putExtra("imageUrl", mFilePath);
        lIntent.putExtra("mCropHeight", mCropHeight);
        lIntent.putExtra("mCropWidth", mCropWidth);
        lIntent.putExtra("mICrop", mMICrop);
        lIntent.putExtra("cameraConfigOptions",mCameraConfigOptions);
        Log.d(LOGTAG, "imageUrl " + mFilePath);
        
        ///发一个广播
        Intent intentRecivier=new Intent();
        intentRecivier.setAction("cameraRecivierAction");
        intentRecivier.putExtra("imageUrl", mFilePath);
        intentRecivier.putExtra("mCropHeight", mCropHeight);
        intentRecivier.putExtra("mCropWidth", mCropWidth);
        intentRecivier.putExtra("mICrop", mMICrop);
        PhotoAlbumOpenActivity.this.sendBroadcast(intentRecivier);
    
        PhotoAlbumOpenActivity.this.startActivity(lIntent);
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
        }else {
            //发送取消操作广播
            Intent lIntent = new Intent(CameraContas.actionFinishCamera);
            lIntent.putExtra("code",101);
            PhotoAlbumOpenActivity.this.sendBroadcast(lIntent);
            finish();
        }
    }

    class FinishActivityRecivier extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            PhotoAlbumOpenActivity.this.finish();
        }
    }
}
