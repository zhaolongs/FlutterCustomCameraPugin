package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.callback;

import android.hardware.SensorEvent;

/**
 * Create by alv1 on 2019/6/13
 */
public interface CameraDarkCallBack {
    void onLineDark(long pCameraLight);

    void onLineNoDark(long pCameraLight);

    void onDarkList(long[] pDarkList, long pCameraLight);

    void onSensorChanged(SensorEvent pEvent, float pLux);
}
