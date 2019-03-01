package com.mykar.framework.utils;

import android.content.Context;

import com.mykar.framework.ApplicationHolder;

import org.litepal.LitePalApplication;

public class DensityUtil {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(float dpValue) {
		Context context = LitePalApplication.getContext();
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(float pxValue) {
		Context context = LitePalApplication.getContext();
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
}
