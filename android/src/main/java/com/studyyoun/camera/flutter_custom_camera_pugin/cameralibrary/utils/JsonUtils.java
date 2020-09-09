package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.utils;

/*
 * 创建人： Created by  on 2020/9/9.
 * 创建时间：Created by  on 2020/9/9.
 * 页面说明：
 * 可关注公众号：我的大前端生涯   获取最新技术分享
 * 可关注网易云课堂：https://study.163.com/instructor/1021406098.htm
 * 可关注博客：https://blog.csdn.net/zl18603543572
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JsonUtils {
	private static final JsonUtils ourInstance = new JsonUtils();
	
	static JsonUtils getInstance() {
		return ourInstance;
	}
	
	private JsonUtils() {
	}


	/**
	 * @param content json字符串
	 * @return 如果转换失败返回null,
	 */
	Map<String, Object> jsonToMap(String content) {
		content = content.trim();
		Map<String, Object> result = new HashMap<>();
		try {
			if (content.charAt(0) == '[') {
				JSONArray jsonArray = new JSONArray(content);
				for (int i = 0; i < jsonArray.length(); i++) {
					Object value = jsonArray.get(i);
					if (value instanceof JSONArray || value instanceof JSONObject) {
						result.put(i + "", jsonToMap(value.toString().trim()));
					} else {
						result.put(i + "", jsonArray.getString(i));
					}
				}
			} else if (content.charAt(0) == '{') {
				JSONObject jsonObject = new JSONObject(content);
				Iterator<String> iterator = jsonObject.keys();
				while (iterator.hasNext()) {
					String key = iterator.next();
					Object value = jsonObject.get(key);
					if (value instanceof JSONArray || value instanceof JSONObject) {
						result.put(key, jsonToMap(value.toString().trim()));
					} else {
						result.put(key, value.toString().trim());
					}
				}
			} else {
				Log.e("异常", "json2Map: 字符串格式错误");
			}
		} catch (JSONException e) {
			Log.e("异常", "json2Map: ", e);
			result = null;
		}
		return result;
	}

}
