package com.dongji.app.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.SmsDilaogActivity;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.MmsContent;
import com.dongji.app.entity.MmsSmsContent;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.tool.PhoneNumberTool;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;

/**
 * 
 * 
 * 新消息的监听  (暂时只有对新短信的监听 )
 * 
 * 使用Service是为了确保消息监听一定会被开启
 * 
 * @author Administrator
 *
 */
public class MyService extends Service {

	public static int Notification_ID_BASE = 110;  
	
	boolean statusbar = false;
	boolean pop = false;
	boolean sound = false;
	boolean vibrate = false;
	boolean sms = false;
	boolean isEncryption = false;
	
	public static final String TAG = "ImiChatSMSReceiver";

	SharedPreferences sms_checkbox_state = null;
	
	SharedPreferences interceptmode = null;
	
	SharedPreferences encryption = null;
	
	String STATUSBAR_POINT = "statusbar_point";
	String POP_POINT = "pop_point";
	String SOUND_POINT = "sound_point";
	String VIBRATE_POINT = "vibrate_point";
	String LIGHTSCREEN_POINT = "lightscreen_point";
	String PREF_NAME = "com.dongji.app.addressbook.encryption";
	String ISENCRYPTION = "isEncryption";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Uri THREADS_URI  = Uri.parse("content://mms-sms/conversations?simple=true");  
		
		SmsProviderObserver smsProviderObserver = new SmsProviderObserver(new Handler());
		getContentResolver().registerContentObserver(THREADS_URI,true, smsProviderObserver);
		
		System.out.println("消息  监听 已设置");
	}
	
	public class SmsProviderObserver extends ContentObserver {

		public SmsProviderObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			
			MessagetIntercepteNotifationUtil util = new MessagetIntercepteNotifationUtil(MyService.this);
			util.update();
		}
	}	
}
