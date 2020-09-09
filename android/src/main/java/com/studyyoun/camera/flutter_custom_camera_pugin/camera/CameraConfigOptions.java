package com.studyyoun.camera.flutter_custom_camera_pugin.camera;

/*
 * 创建人： Created by  on 2020/9/9.
 * 创建时间：Created by  on 2020/9/9.
 * 页面说明：
 * 可关注公众号：我的大前端生涯   获取最新技术分享
 * 可关注网易云课堂：https://study.163.com/instructor/1021406098.htm
 * 可关注博客：https://blog.csdn.net/zl18603543572
 */

import java.io.Serializable;

public  class CameraConfigOptions implements Serializable {
	
	///默认自定义相册是否显示 相册切换
	public boolean isShowPhotoAlbum = true;
	
	///默认自定义相册是否显示 前后镜头切换
	public boolean isShowSelectCamera = true;
	
	///默认自定义相册是否显示 闪光灯开关按钮
	public boolean isShowFlashButtonCamera = true;
	
	
	public boolean isShowPhotoAlbum() {
		return isShowPhotoAlbum;
	}
	
	public void setShowPhotoAlbum(boolean showPhotoAlbum) {
		isShowPhotoAlbum = showPhotoAlbum;
	}
	
	public boolean isShowSelectCamera() {
		return isShowSelectCamera;
	}
	
	public void setShowSelectCamera(boolean showSelectCamera) {
		isShowSelectCamera = showSelectCamera;
	}
	
	public boolean isShowFlashButtonCamera() {
		return isShowFlashButtonCamera;
	}
	
	public void setShowFlashButtonCamera(boolean showFlashButtonCamera) {
		isShowFlashButtonCamera = showFlashButtonCamera;
	}
}
