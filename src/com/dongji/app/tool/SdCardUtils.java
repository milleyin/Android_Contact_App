package com.dongji.app.tool;

import android.os.Environment;

/**
 * 
 * @author Administrator
 *
 */
public class SdCardUtils {

	// 检测sd卡是否可用
	public static boolean checkSDState() {
		String state = Environment.getExternalStorageState();
		
		if (state.equals(Environment.MEDIA_MOUNTED)&& Environment.getExternalStorageDirectory().canWrite()) {
			return true;
		} else {
			return false;
		}
	}
}
