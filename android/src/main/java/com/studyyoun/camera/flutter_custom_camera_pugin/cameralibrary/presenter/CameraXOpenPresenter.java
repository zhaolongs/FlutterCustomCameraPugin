package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.presenter;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.widget.FrameLayout;

import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraContact;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback.CameraDarkCallBack;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.utils.LightSensorUtil;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.view.CameraPreview;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class CameraXOpenPresenter implements CameraContact.CameraPresenter {
	
	private Camera mCamera;
	private Context mContext;
	private FrameLayout mFrameLayout;
	private CameraCallBack mCallBack;
	
	private int mPicWidth;
	private int mPicHeight;
	
	private boolean openOrClose;
	
	private int mTakePictureCount = 0;
	//是否默认连续拍照
	private boolean mIsContinuous = false;
	//切换摄像头
	//当前选用的摄像头，1后置 0前置
	private int cameraPosition = 1;
	/**
	 * 图片种类
	 */
	private static final String IMAGE_TYPE = ".jpeg";
	private boolean mIsShotPreview = false;
	private SensorManager mSenosrManager;
	
	
	public CameraXOpenPresenter() {
		initPicSelectFunction();
	}
	
	public void init(Context context, FrameLayout frameLayout, int picWidth, int picHeight, CameraCallBack callBack) {
		this.mCallBack = callBack;
		this.mContext = context;
		this.mFrameLayout = frameLayout;
		this.mPicWidth = picWidth;
		this.mPicHeight = picHeight;
	}
	
	
	@Override
	public void stop() {
		if (mCamera != null) {
			mFrameLayout.removeAllViews();
			mCamera.stopPreview();//停掉原来摄像头的预览
			mCamera.release();//释放资源
			mCamera = null;//取消原来摄像头
		}
	}
	
	@Override
	public void start() {
		openCamer(0);
	}
	
	@Override
	public void reCameraClick() {
		if (mCamera != null) {
			mCamera.startPreview();
		}
	}
	
	@Override
	public void onCameraClick() {
		//camera资源是典型的C/S架构的服务，每次使用完毕需注意回收release()
		if (mTakePictureCount == 0) {
			this.mTakePictureCount++;
			mCamera.takePicture(null, null, mPictureCallback);
		} else {
			mCamera.startPreview();
			this.mTakePictureCount = 0;
		}
	}
	
	public void onFouseCameraClick() {
	
	}
	
	@Override
	public void openCameraFlashFunction() {
		openOrClose = !openOrClose;
		Camera.Parameters lParameters = mCamera.getParameters();
		if (openOrClose) {
			lParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		} else {
			lParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		}
		mCamera.setParameters(lParameters);
	}
	
	private String[] permissions = new String[3];
	
	private void initPicSelectFunction() {
		permissions[0] = Manifest.permission.READ_EXTERNAL_STORAGE;
		permissions[1] = Manifest.permission.WRITE_EXTERNAL_STORAGE;
		permissions[2] = Manifest.permission.CAMERA;
	}
	
	private void openCamer(int i) {
		try {
			stop();
			//初始化 Camera对
			mCamera = Camera.open(i);
			//自动连续对焦
			Camera.Parameters parameters = mCamera.getParameters();
			if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				// 连续对焦模式
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
			parameters.setPreviewFrameRate(60);
			if (mIsShotPreview) {
				//当下一幅预览图像可用时调用一次onPreviewFrame
				mCamera.setOneShotPreviewCallback(mPreviewCallback);
			} else {
				mCamera.setPreviewCallback(mPreviewCallback);
			}
			//图片的格式
			parameters.setPictureFormat(ImageFormat.JPEG);
			//获取Camera所支持的图片尺寸
			List<Camera.Size> pictureSizes = mCamera.getParameters().getSupportedPictureSizes();
			List<Camera.Size> previewSizes = mCamera.getParameters().getSupportedPreviewSizes();
			//校验用户要获取的图片的尺寸
			if (pictureSizes != null && pictureSizes.size() > 0) {
				
				Collections.sort(pictureSizes, new Comparator<Camera.Size>() {
					public int compare(Camera.Size h1, Camera.Size h2) {
						if (h1.width > h2.width) {
							return 1;
						} else if (h1.width == h2.width) {
							return 0;
						} else {
							return -1;
						}
					}
				});
				
				if (pictureSizes.contains(mPicWidth)) {
					for (Camera.Size lPictureSize : pictureSizes) {
						if (lPictureSize.width == mPicWidth) {
							mPicHeight = lPictureSize.height;
							break;
						}
					}
				} else {
					//取出最大的
					Camera.Size lSize = pictureSizes.get(pictureSizes.size() - 1);
					if (mPicWidth > lSize.width) {
						mPicWidth = lSize.width;
						mPicHeight = lSize.height;
					} else {
						for (Camera.Size lPictureSize : pictureSizes) {
							if (lPictureSize.width >= mPicWidth) {
								mPicWidth = lPictureSize.width;
								mPicHeight = lPictureSize.height;
								break;
							}
						}
					}
				}
			}
			if (openOrClose) {
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
			} else {
				parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
			}
			
			parameters.setPictureSize(mPicWidth, mPicHeight);
			try {
				mCamera.setParameters(parameters);
			} catch (Exception e) {
				try {
					parameters.setPictureSize(1080,1920 );
					mCamera.setParameters(parameters);
				} catch (Exception ignored) {
				}
			}
			
			
			CameraPreview mPreview = new CameraPreview(mContext, mCamera);
			mFrameLayout.addView(mPreview);
			
		} catch (RuntimeException e) {
			// Barcode Scanner has seen crashes in the wild of this variety:
			// java.?lang.?RuntimeException: Fail to connect to camera service
			Log.w("camera", "Unexpected error initializing camera", e);
			displayFrameworkBugMessageAndExit("打开相机失败，可能是相关权限拍照和录像未打开，请尝试打开再重试。");
		}
	}
	
	private void displayFrameworkBugMessageAndExit(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage(message);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showInstalledAppDetails(mContext.getApplicationContext(), mContext.getPackageName());
			}
		});
		builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				dialog.dismiss();
				if (mCallBack != null) {
					mCallBack.cameraFaile(-1, "内存异常");
				}
			}
		});
		builder.show();
	}
	
	private static final String SCHEME = "package";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.1及之前版本)
	 */
	private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
	/**
	 * 调用系统InstalledAppDetails界面所需的Extra名称(用于Android 2.2)
	 */
	private static final String APP_PKG_NAME_22 = "pkg";
	/**
	 * InstalledAppDetails所在包名
	 */
	private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
	/**
	 * InstalledAppDetails类名
	 */
	private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
	
	/**
	 * 调用系统InstalledAppDetails界面显示已安装应用程序的详细信息。
	 * 对于Android 2.3（Api Level9）以上，使用SDK提供的接口； 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）。
	 *
	 * @param context
	 * @param packageName 应用程序的包名
	 */
	
	public static void showInstalledAppDetails(Context context, String packageName) {
		Intent intent = new Intent();
		final int apiLevel = Build.VERSION.SDK_INT;
		if (apiLevel >= 9) { // 2.3（ApiLevel 9）以上，使用SDK提供的接口
			intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
			Uri uri = Uri.fromParts(SCHEME, packageName, null);
			intent.setData(uri);
		} else { // 2.3以下，使用非公开的接口（查看InstalledAppDetails源码）
			// 2.2和2.1中，InstalledAppDetails使用的APP_PKG_NAME不同。
			final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
					: APP_PKG_NAME_21);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setClassName(APP_DETAILS_PACKAGE_NAME,
					APP_DETAILS_CLASS_NAME);
			intent.putExtra(appPkgName, packageName);
		}
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}
	
	
	//获取照片中的接口回调
	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			
			if (mIsContinuous) {
				mTakePictureCount = 0;
				mCamera.startPreview();
			}
			CameraImageShowAsyncTask lAsyncTask = new CameraImageShowAsyncTask();
			lAsyncTask.execute(data);
		}
	};
	
	private Camera.PreviewCallback mPreviewCallback = new Camera.PreviewCallback() {
		@Override
		public void onPreviewFrame(byte[] data, Camera camera) {
			
			previewDarkFuntion(camera, data);
			
		}
	};
	
	private CameraDarkCallBack mCameraDarkCallBack;
	//上次记录的时间戳
	private long lastRecordTime = System.currentTimeMillis();
	//上次记录的索引
	private int darkIndex = 0;
	//一个历史记录的数组，255是代表亮度最大值
	private long[] darkList = new long[]{255, 255, 255, 255};
	//扫描间隔
	private int waitScanTime = 300;
	//亮度低的阀值
	private int darkValue = 60;
	
	private void previewDarkFuntion(Camera camera, byte[] data) {
		if (mCameraDarkCallBack != null) {
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastRecordTime < waitScanTime) {
				return;
			}
			lastRecordTime = currentTime;
			
			int width = camera.getParameters().getPreviewSize().width;
			int height = camera.getParameters().getPreviewSize().height;
			//像素点的总亮度
			long pixelLightCount = 0L;
			//像素点的总数
			long pixeCount = width * height;
			//采集步长，因为没有必要每个像素点都采集，可以跨一段采集一个，减少计算负担，必须大于等于1。
			int step = 10;
			//data.length - allCount * 1.5f的目的是判断图像格式是不是YUV420格式，只有是这种格式才相等
			//因为int整形与float浮点直接比较会出问题，所以这么比
			if (Math.abs(data.length - pixeCount * 1.5f) < 0.00001f) {
				for (int i = 0; i < pixeCount; i += step) {
					//如果直接加是不行的，因为data[i]记录的是色值并不是数值，byte的范围是+127到—128，
					// 而亮度FFFFFF是11111111是-127，所以这里需要先转为无符号unsigned long参考Byte.toUnsignedLong()
					pixelLightCount += ((long) data[i]) & 0xffL;
				}
				//平均亮度
				long cameraLight = pixelLightCount / (pixeCount / step);
				//更新历史记录
				int lightSize = darkList.length;
				darkList[darkIndex = darkIndex % lightSize] = cameraLight;
				darkIndex++;
				boolean isDarkEnv = true;
				//判断在时间范围waitScanTime * lightSize内是不是亮度过暗
				for (int i = 0; i < lightSize; i++) {
					if (darkList[i] > darkValue) {
						isDarkEnv = false;
					}
				}
				
				if (isDarkEnv) {
					//亮度过暗回调
					mCameraDarkCallBack.onLineDark(cameraLight);
				} else {
					mCameraDarkCallBack.onLineNoDark(cameraLight);
				}
				mCameraDarkCallBack.onDarkList(darkList, cameraLight);
			}
		}
	}
	
	
	@Override
	public void destore() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.setPreviewCallback(null);
			mCamera.release();
			mCamera = null;
		}
		if (mSenosrManager != null) {
			LightSensorUtil.unregisterLightSensor(mSenosrManager, lightSensorListener);
		}
		
	}
	
	public void changeCameraClick() {
		int cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		for (int i = 0; i < cameraCount; i++) {
			//得到每一个摄像头的信息
			Camera.getCameraInfo(i, cameraInfo);
			if (cameraPosition == 1) {
				//现在是后置，变更为前置
				//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					//重新打开
					reStartCamera(i);
					cameraPosition = 0;
					break;
				}
			} else {
				//现在是前置， 变更为后置
				//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					reStartCamera(i);
					cameraPosition = 1;
					break;
				}
			}
		}
	}
	
	public void setContinuous(boolean continuous) {
		this.mIsContinuous = continuous;
	}
	
	class CameraImageShowAsyncTask extends AsyncTask<byte[], Void, String> {
		
		@Override
		protected String doInBackground(byte[]... bytes) {
			byte[] data = bytes[0];
			Bitmap lBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
			// 取得图片旋转角度
			int angle = 90;
			if (cameraPosition == 1) {
				angle = 90;
			} else {
				angle = -90;
			}
			String lS;
			Bitmap returnBm = null;
			// 根据旋转角度，生成旋转矩阵
			Matrix matrix = new Matrix();
			matrix.postRotate(angle);
			try {
				// 将原始图片按照旋转矩阵进行旋转，并得到新的图片
				returnBm = Bitmap.createBitmap(lBitmap, 0, 0, lBitmap.getWidth(), lBitmap.getHeight(), matrix, true);
				lS = savePhotoToSD(returnBm, mContext);
				
			} catch (OutOfMemoryError e) {
				returnBm = null;
				lS = null;
			}
			return lS;
		}
		
		@Override
		protected void onPostExecute(String string) {
			super.onPostExecute(string);
			if (string != null) {
				if (mCallBack != null) {
					mCallBack.cameraSuccess(string);
				}
			} else {
				if (mCallBack != null) {
					displayFrameworkBugMessageAndExit("需要访问您的储存权限");
					//mCallBack.cameraFaile(-1, "内存异常");
				}
			}
		}
	}
	
	
	//重新打开预览
	private void reStartCamera(int i) {
		openCamer(i);
	}
	
	/**
	 * 使用当前系统时间作为上传图片的名称
	 *
	 * @return 存储的根路径+图片名称
	 */
	private static String getPhotoFileName(Context context) {
		return Environment.getExternalStorageDirectory().getPath() + File.separator + System.currentTimeMillis() + IMAGE_TYPE;
	}
	
	/**
	 * 保存Bitmap图片在SD卡中
	 * 如果没有SD卡则存在手机中
	 *
	 * @param mbitmap 需要保存的Bitmap图片
	 * @return 保存成功时返回图片的路径，失败时返回null
	 */
	public String savePhotoToSD(Bitmap mbitmap, Context context) {
		FileOutputStream outStream = null;
		String fileName = null;
		try {
			fileName = getPhotoFileName(context);
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			outStream = new FileOutputStream(fileName);
			// 把数据写入文件，100表示不压缩
			mbitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
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
				if (mbitmap != null) {
					mbitmap.recycle();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	private SensorEventListener lightSensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
				//光线强度
				float lux = event.values[0];
				mCameraDarkCallBack.onSensorChanged(event, lux);
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		
		}
	};
	
	/**
	 * 光线亮度回调
	 *
	 * @param pCameraDarkCallBack
	 */
	public void setCameraDarkCallBack(CameraDarkCallBack pCameraDarkCallBack) {
		this.mCameraDarkCallBack = pCameraDarkCallBack;
		mSenosrManager = LightSensorUtil.getSenosrManager(mContext);
		LightSensorUtil.registerLightSensor(mSenosrManager, lightSensorListener);
	}
	
	/**
	 * 设置是否使用 可用图像使用
	 *
	 * @param flag
	 */
	public void setShotPreview(boolean flag) {
		this.mIsShotPreview = flag;
	}
	
}
