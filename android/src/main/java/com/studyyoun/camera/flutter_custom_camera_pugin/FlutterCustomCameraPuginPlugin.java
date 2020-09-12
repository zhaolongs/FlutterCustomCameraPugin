package com.studyyoun.camera.flutter_custom_camera_pugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.studyyoun.camera.flutter_custom_camera_pugin.camera.CameraConfigOptions;
import com.studyyoun.camera.flutter_custom_camera_pugin.camera.CameraOpenActivity;
import com.studyyoun.camera.flutter_custom_camera_pugin.camera.PhotoAlbumOpenActivity;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.CameraXUtils;
import com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.utils.BeanClassUtils;

import java.util.HashMap;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.StandardMessageCodec;

/**
 * FlutterCustomCameraPuginPlugin
 */
public class FlutterCustomCameraPuginPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
	
	private static final String openCameraMethodName = "openCamera";
	private BasicMessageChannel mMessageChannel;
	private Context mContext;
	private CameraFinishRecivier mCameraFinishRecivier;
	private BasicMessageChannel.Reply mReply;
	private Activity mActivity;
	
	
	/**
	 * 在 1.12 之后 Android 开始使用新的插件 API ，基于的旧的 PluginRegistry.Registrar 不会立即被弃用，但官方建议迁移到基于的新API FlutterPlugin ，
	 * 另外新版本官方建议插件直接使用 Androidx 支持，官方提供的插件也已经全面升级到 Androidx。
	 *
	 * @param flutterPluginBinding
	 */
	@Override
	public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
		
		
		mContext = flutterPluginBinding.getApplicationContext();
		
		mCameraFinishRecivier = new CameraFinishRecivier();
		IntentFilter filter = new IntentFilter();
		filter.addAction("cameraactivityfinish");
		mContext.registerReceiver(mCameraFinishRecivier, filter);
		
		//消息接收监听
		//BasicMessageChannel （主要是传递字符串和一些半结构体的数据）
		//创建通
		mMessageChannel = new BasicMessageChannel<Object>(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "flutter_and_native_custom_100", StandardMessageCodec.INSTANCE);
		// 接收消息监听
		mMessageChannel.setMessageHandler(new BasicMessageChannel.MessageHandler<Object>() {
			@Override
			public void onMessage(Object o, BasicMessageChannel.Reply<Object> reply) {
				//处理消息
				try {
					mReply = reply;
					messageHandlerControllerFunction((Map<Object, Object>) o, reply);
				} catch (Exception e) {
					e.printStackTrace();
					Map<String, Object> resultMap = new HashMap<>();
					resultMap.put("message", "异常 " + e.getMessage());
					resultMap.put("code", 502);
					//回调 此方法只能使用一次
					reply.reply(resultMap);
				}
			}
		});
		
	}
	
	/**
	 * @param arguments 参数
	 * @param reply     回调依赖
	 */
	private void messageHandlerControllerFunction(Map<Object, Object> arguments, BasicMessageChannel.Reply<Object> reply) {
		
		//方法名标识
		String lMethod = (String) arguments.get("method");
		
		//测试 reply.reply()方法 发消息给Flutter
		if (lMethod.equals("test")) {
			Toast.makeText(mContext, "flutter 调用到了 android test", Toast.LENGTH_SHORT).show();
			//回调Flutter
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("message", "reply.reply 返回给flutter的数据");
			resultMap.put("code", 200);
			//回调 此方法只能使用一次
			reply.reply(resultMap);
		} else if (lMethod.equals(openCameraMethodName)) {
			
			//拍照
			CameraConfigOptions lCameraConfigOptions = getCameraConfigOptions(arguments);
			
			///打开自定义相机
			Intent lIntent = new Intent(mContext, CameraOpenActivity.class);
			lIntent.putExtra("cameraConfigOptions", lCameraConfigOptions);
			lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(lIntent);
		} else if (lMethod.equals("openPhotoAlbum")) {
			///打开系统相册
			Intent lIntent = new Intent(mContext, PhotoAlbumOpenActivity.class);
			lIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			CameraConfigOptions lCameraConfigOptions = getCameraConfigOptions(arguments);
			lIntent.putExtra("cameraConfigOptions", lCameraConfigOptions);
			mContext.startActivity(lIntent);
			
			
		} else {
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("message", "未知方法");
			resultMap.put("code", 501);
			//回调 此方法只能使用一次
			reply.reply(resultMap);
		}
	}
	
	private CameraConfigOptions getCameraConfigOptions(Map<Object, Object> arguments) {
		Map<String, Object> lObjectMap = (Map<String, Object>) arguments.get("options");
		
		CameraConfigOptions lCameraConfigOptions = new CameraConfigOptions();
		lCameraConfigOptions.isShowSelectCamera = (boolean) lObjectMap.get("isShowSelectCamera");
		lCameraConfigOptions.isShowPhotoAlbum = (boolean) lObjectMap.get("isShowPhotoAlbum");
		lCameraConfigOptions.isShowFlashButtonCamera = (boolean) lObjectMap.get("isShowFlashButtonCamera");
		lCameraConfigOptions.isPreviewImage = (boolean) lObjectMap.get("isPreviewImage");
		lCameraConfigOptions.isCropImage = (boolean) lObjectMap.get("isCropImage");
		return lCameraConfigOptions;
	}
	
	// This static function is optional and equivalent to onAttachedToEngine. It supports the old
	// pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
	// plugin registration via this function while apps migrate to use the new Android APIs
	// post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
	//
	// It is encouraged to share logic between onAttachedToEngine and registerWith to keep
	// them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
	// depending on the user's project. onAttachedToEngine or registerWith must both be defined
	// in the same class.
	public static void registerWith(Registrar registrar) {
		
		final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_custom_camera_pugin");
		channel.setMethodCallHandler(new FlutterCustomCameraPuginPlugin());
		
		Log.e("Custom", "flutter Custom registerWith");
	}
	
	@Override
	public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
		if (call.method.equals("getPlatformVersion")) {
			result.success("Android " + android.os.Build.VERSION.RELEASE);
		} else {
			result.notImplemented();
		}
	}
	
	
	@Override
	public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
		mMessageChannel.setMessageHandler(null);
		binding.getApplicationContext().unregisterReceiver(mCameraFinishRecivier);
		
	}
	
	@Override
	public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
		mActivity = binding.getActivity();
	}
	
	@Override
	public void onDetachedFromActivityForConfigChanges() {
	
	}
	
	@Override
	public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
	
	}
	
	@Override
	public void onDetachedFromActivity() {
	
	}
	
	
	class CameraFinishRecivier extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			Message lMessage = Message.obtain();
			lMessage.what = 100;
			lMessage.obj = intent;
			mHandler.sendMessage(lMessage);
		}
	}
	
	
	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			switch (msg.what) {
				case 100:
					Intent lIntent = (Intent) msg.obj;
					int code = 200;
					String message = "操作成功";
					
					String lImageUrl = lIntent.getStringExtra("filePath");
					
					int intentCode = lIntent.getIntExtra("code", 0);
					
					
					if (intentCode == 101) {
						message = "相册选择取消";
						code = 201;
					} else if (intentCode == 102) {
						message = "相机拍照取消";
						code = 201;
					}
					
					Map<String, Object> camMap = new HashMap<>();
					
					
					if (lImageUrl != null && lImageUrl.trim().length() != 0) {
						camMap.put("lImageUrl", lImageUrl);
					}
					
					
					//构建参数
					Map<String, Object> resultMap = new HashMap<>();
					resultMap.put("data", camMap);
					resultMap.put("message", message);
					resultMap.put("code", code);
					resultMap.put("method", openCameraMethodName);
					//向 Flutter 中发送消息
					//参数 二可以再次接收到 Flutter 中的回调
					//也可以直接使用 mMessageChannel.send(resultMap）
					mMessageChannel.send(resultMap, new BasicMessageChannel.Reply<Object>() {
						@Override
						public void reply(Object o) {
							Log.d("mMessageChannel", "mMessageChannel send 回调 " + o);
						}
					});
					//回调 此方法只能使用一次
					mReply.reply(resultMap);
					break;
			}
			
		}
	};
}
