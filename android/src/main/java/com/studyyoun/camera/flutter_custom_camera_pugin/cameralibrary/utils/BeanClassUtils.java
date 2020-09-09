package com.studyyoun.camera.flutter_custom_camera_pugin.cameralibrary.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
/*
 * 创建人： Created by  on 2020/9/9.
 * 创建时间：Created by  on 2020/9/9.
 * 页面说明：
 * 可关注公众号：我的大前端生涯   获取最新技术分享
 * 可关注网易云课堂：https://study.163.com/instructor/1021406098.htm
 * 可关注博客：https://blog.csdn.net/zl18603543572
 */


/**
 * 工具类将map转成对应对象的方法，入参是map和目标实体类，我们的实体类中是标准的set、get方法，
 * 要map转成对应的实体类对象需要map中的字段和我们实体类的属性字段匹配。获取到map中的字段，然后拼装成标准的set方法，
 * 利用反射获取到我们目标实体类对象中的这个set方法，并把相应的参数放进去调用。
 *
 * 可能存在的问题：1、需要map中字段和实体类对象的字段匹配，并且实体类中get\set方法要标准；
 * 2、利用反射效率会比较低。
 */

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@SuppressWarnings("all")
public class BeanClassUtils {

	/**
	 * 用于将map转成对象
	 *
	 * @param map
	 * @param object
	 * @return
	 * @throws Exception
	 */
	public static <T> T mapToObject(Map<String, Object> map, T object) {

		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			String fieldName = fields[i].getName();
			String key = toUpperCaseFirstOne(fieldName);
			String mapValue = (map.get(key) == null ? "" : map.get(key).toString());
			try {
				object.getClass().getMethod("set" + key, String.class).invoke(object, mapValue);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return object;
	}


	//首字母转小写
	public static String toLowerCaseFirstOne(String s) {
		if (Character.isLowerCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
	}


	//首字母转大写
	public static String toUpperCaseFirstOne(String s) {
		if (Character.isUpperCase(s.charAt(0)))
			return s;
		else
			return (new StringBuilder()).append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).toString();
	}

}
