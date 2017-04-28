package com.dongji.app.sqllite;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * 归属地数据库
 * @author Administrator
 *
 */
public class PhoneDatabaseUtil {

	private SQLiteDatabase mSQLiteDatabase = null;
	
    private String table_phone_reg = "phone_reg" ;
    
    public static String PHONE_ID = "phone_id";
    public static String KEY_PID = "pid";
    public static String KEY_CID = "cid";
    public static String KEY_OID = "oid";
    
    public static String KEY_NAME = "name";
    
    private String table_provinces = "provinces"; //省份表 
    
    private String table_cities = "cities";  //城市表
    
    private String table_operators = "operators";  //运营商表
    
    private String cities = "北京上海天津重庆";
    
    
    private String table_fixedline = "fixedline";
    private String CREACODE= "creacode"; //固话区号
    private String ATTRIBUTION = "attribution"; //区号对应城市名
    
    
	public PhoneDatabaseUtil(String path)
	{
		this.mSQLiteDatabase = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY|SQLiteDatabase.NO_LOCALIZED_COLLATORS);
	}
	
	
	public void close()
	{
		mSQLiteDatabase.close();
	}
	
	
    public String fetch(String num)
    {
    	
//    	System.out.println(" num --->" + num);
    	
    	String name = null;
    	
    	Cursor mCursor=mSQLiteDatabase.query(table_phone_reg, new String [] {PHONE_ID,KEY_PID,KEY_CID,KEY_OID}, PHONE_ID+" = "+ num, null, null, null, null);
    	if(mCursor.moveToNext())
    	{
    		int pid = mCursor.getInt(mCursor.getColumnIndex(KEY_PID));
    		int cid = mCursor.getInt(mCursor.getColumnIndex(KEY_CID)); 
    		int oid = mCursor.getInt(mCursor.getColumnIndex(KEY_OID));
    		
    		//省份
    		Cursor pc = mSQLiteDatabase.query(table_provinces, new String [] {KEY_NAME}, KEY_PID+" = "+ pid, null, null, null, null);
    		pc.moveToNext();
    		String p_name = pc.getString(0);
    		
    		//城市
    		Cursor cc = mSQLiteDatabase.query(table_cities, new String [] {KEY_NAME}, KEY_CID+" = "+ cid, null, null, null, null);
    		cc.moveToNext();
    		String c_name = cc.getString(0);
    		
    		//运营商
    		Cursor oc = mSQLiteDatabase.query(table_operators, new String [] {KEY_NAME}, KEY_OID+" = "+ oid, null, null, null, null);
    		oc.moveToNext();
    		String o_name = oc.getString(0);
    		
    		
//    		System.out.println(" 归属地 --->" + p_name+"省"+c_name+"市"+o_name);
    		
    		pc.close();
    		cc.close();
    		oc.close();
    		
    		name =  p_name+" "+c_name+" "+o_name;
    	}
    	
    	mCursor.close();
    	
    	if(name ==null)
    	{
    		String key_1 = num.substring(0, 3);
    		String key_2 = num.substring(0, 4);
    		
//    		System.out.println(" str_num ---> " + num + " key_1 --->" + key_1 + "  key_2  --->" + key_2);
    		
    		Cursor fix_cur = mSQLiteDatabase.query(table_fixedline, new String [] {ATTRIBUTION}, CREACODE+" = '"+ key_1 + "' OR "+ CREACODE +" = '"+key_2 + "'", null, null, null, null);

    		if(fix_cur.moveToNext())
    		{
    			name = fix_cur.getString(fix_cur.getColumnIndex(ATTRIBUTION));
    		}else{
    			name = "未知归属地";
    		}
    		
    		fix_cur.close();
    	}
    	
    	return name;
    }   
    
	
	/**
	 * 转义符
	 *
	 */
	public String tranSQLSting(String str)
	{
		String [] parts=str.split("'");
		String str_tran ="";
		for(int i =0;i<parts.length;i++)
		{
			if(i==0)
			{
				str_tran+=parts[i];
			}else{
				str_tran+="''"+parts[i];
			}
		}
		return str_tran;
	}

}

