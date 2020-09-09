package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.studyyoun.camera.flutter_custom_camera_pugin.R;
import com.studyyoun.camera.flutter_custom_camera_pugin.camera.CameraContas;
import com.studyyoun.camera.flutter_custom_camera_pugin.camera.CameraOpenActivity;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter.CameraImageShowPresenter;


public class CameraExampShowActivity extends Activity {

	private FinishActivityRecivier mFinishActivityRecivier;
	private int mCropWidth;
	private int mCropHeight;
	private boolean mIsCorpBoolean;
	private long preClickTime = 0;
	private ImageView mShowImageView;
	private String mImageUrl;
	private boolean mImageLoading = false;
	private LinearLayout mLoadingLayout;
	private String mSource;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestNoTitle();
		setContentView(R.layout.camera_examp_show_activity_layout);


		mLoadingLayout = findViewById(R.id.ll_image_loading_view);
		mShowImageView = findViewById(R.id.iv_photo_select);
		mImageUrl = getIntent().getStringExtra("imageUrl");

		mCropHeight = getIntent().getIntExtra("mCropHeight", 500);
		mCropWidth = getIntent().getIntExtra("mCropWidth", 500);
		mIsCorpBoolean = getIntent().getBooleanExtra("mICrop", false);
		
		mSource = getIntent().getStringExtra("source");

		new Handler(Looper.myLooper()).postDelayed(new Runnable() {
			@Override
			public void run() {
				loadingImage();
			}
		}, 1000);


		findViewById(R.id.ll_base_back).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				int code = 101 ;
				if(mSource!=null&&mSource.equals("camera")){
					code = 102 ;
				}
				//发送取消操作广播
				Intent lIntent = new Intent(CameraContas.actionFinishCamera);
				lIntent.putExtra("code", code);
				CameraExampShowActivity.this.sendBroadcast(lIntent);


				CameraExampShowActivity.this.finish();
			}
		});

		findViewById(R.id.ll_base_next).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				long currentTime = System.currentTimeMillis();
				long flagTime = currentTime - preClickTime;
				if (flagTime < 2000 || !mImageLoading) {
					Toast.makeText(CameraExampShowActivity.this, "图片正在加载中", Toast.LENGTH_SHORT).show();
				} else {
					if (mIsCorpBoolean) {
						final Intent lIntent = new Intent(CameraExampShowActivity.this, CameraExampCorpActivity.class);
						lIntent.putExtra("imageUrl", mImageUrl);
						lIntent.putExtra("mCropHeight", mCropHeight);
						lIntent.putExtra("mCropWidth", mCropWidth);
						CameraExampShowActivity.this.startActivity(lIntent);
					} else {

						Toast.makeText(CameraExampShowActivity.this, "已保存 " + mImageUrl, Toast.LENGTH_LONG).show();
						Intent lIntent = new Intent("cameraactivityfinish");
						lIntent.putExtra("filePath", mImageUrl);
						lIntent.putExtra("code", 100);
						CameraExampShowActivity.this.sendBroadcast(lIntent);
						CameraExampShowActivity.this.finish();
					}
				}


			}
		});

		mFinishActivityRecivier = new FinishActivityRecivier();
		registerReceiver(mFinishActivityRecivier, new IntentFilter("cameraactivityfinish"));


	}

	private void loadingImage() {
		CameraImageShowPresenter.getInstance().showImage(mShowImageView, mImageUrl, new CameraImageShowPresenter.OnCamerImageShowCallBack() {
			@Override
			public void onFinish(int flag, String message) {
				mImageLoading = true;
				mLoadingLayout.setVisibility(View.GONE);
			}
		});
	}

	private void requestNoTitle() {
		//去除标题栏
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//去除状态栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mFinishActivityRecivier);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
			int code = 101 ;
			if(mSource!=null&&mSource.equals("camera")){
				code = 102 ;
			}
			//发送取消操作广播
			Intent lIntent = new Intent(CameraContas.actionFinishCamera);
			lIntent.putExtra("code", code);
			CameraExampShowActivity.this.sendBroadcast(lIntent);
			finish();
			//不执行父类点击事件
			return true;
		}
		//继续执行父类其他点击事件
		return super.onKeyUp(keyCode, event);
	}


	class FinishActivityRecivier extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			CameraExampShowActivity.this.finish();
		}
	}
}
