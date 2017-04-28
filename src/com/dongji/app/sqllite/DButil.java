package com.dongji.app.sqllite;

import android.content.Context;

/**
 * 
 * 数据库单例
 * @author Administrator
 *
 */

public class DButil {

	public static MyDatabaseUtil myDatabaseUtil;
	
	public static MyDatabaseUtil getInstance(Context context)
	{
		if(myDatabaseUtil==null)
		{
			try {
				
				myDatabaseUtil = new MyDatabaseUtil(context);
				myDatabaseUtil.open();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return myDatabaseUtil;
	}
	
	public static void close()
	{
		try {
			if(myDatabaseUtil != null)
			{
				myDatabaseUtil.close();
				myDatabaseUtil = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
