package com.dongji.app.tool;

import java.io.File;
import java.security.MessageDigest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * 工具类   定义一些常量  和 常用的方法 
 * @author 
 *
 */
public class ToolUnit {
	
	//sd上的存储根目录
	public static String BASE_PATH = Environment.getExternalStorageDirectory().getPath()+"/c4catpad" ; 
	
	
	//版本更新的文件的存储路径
	public static String UPDATE_PATH = BASE_PATH+ "/update";
	
	
	
	
	
	/**
	 * 检测 SD卡是否加载  可用
	 * @return 
	 */
	public static boolean checkSDCard(Context context)
	{
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED) && Environment.getExternalStorageDirectory().canWrite()) {
			return true;
		} else {
//			Toast.makeText(context, R.string.sdcard_error, Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	
	/**
	 * 检测网络状态
	 * @param context
	 * @return
	 */
	public static boolean checkNetWork(Context context)
	{
		 ConnectivityManager manager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);       
		 NetworkInfo networkinfo = manager.getActiveNetworkInfo();       
		 if (networkinfo == null || !networkinfo.isAvailable()) {  
//			  Toast.makeText(context, R.string.internet_error, Toast.LENGTH_SHORT).show();
		      return false;     
		 }     
		 return true;     
	}
	/**
	* 返回当前系统版本
	* @return
	*/
	public static String GetSystemVersion()
	{
	return android.os.Build.VERSION.RELEASE;
	}
	
    /**
     * MD5加密
     * @param in
     * @return out
     */
    public static String MD5(String in) { 
        MessageDigest md5 = null; 
        try { 
            md5 = MessageDigest.getInstance("MD5"); 
        } catch (Exception e) { 
            e.printStackTrace(); 
            return ""; 
        } 
  
        char[] charArray = in.toCharArray(); 
        byte[] byteArray = new byte[charArray.length]; 
  
        for (int i = 0; i < charArray.length; i++) { 
            byteArray[i] = (byte) charArray[i]; 
        }
        
        byte[] md5Bytes = md5.digest(byteArray); 
  
        StringBuffer hexValue = new StringBuffer(); 
        for (int i = 0; i < md5Bytes.length; i++) { 
            int val = ((int) md5Bytes[i]) & 0xff; 
            if (val < 16) { 
                hexValue.append("0"); 
            } 
            hexValue.append(Integer.toHexString(val)); 
        } 
        return hexValue.toString(); 
    } 
    
}
