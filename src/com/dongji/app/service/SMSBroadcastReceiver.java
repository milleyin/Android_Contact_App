package com.dongji.app.service;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsMessage;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.entity.KeywordEntity;
import com.dongji.app.entity.Threads;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.google.android.mms.pdu.GenericPdu;
import com.google.android.mms.pdu.PduParser;
import com.google.android.mms.pdu.RetrieveConf;

/**
 * 
 * 新消息 监听
 * 
 * @author Administrator
 *
 */
public class SMSBroadcastReceiver extends BroadcastReceiver {

	public static final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED"; 
	public static final String GSM_SMS_RECEIVED_ACTION = "android.provider.Telephony.GSM_SMS_RECEIVED";
	
	//彩信
	public static final String MMS_RECEIVED_ACTION = "android.provider.Telephony.WAP_PUSH_RECEIVED";
	
	public static final String TAG = "ImiChatSMSReceiver";

	SharedPreferences sms_checkbox_state = null;
	
	SharedPreferences interceptmode = null;
	
	SharedPreferences encryption = null;
	
	SQLiteDatabase db = null;
	
	MyDatabaseUtil myDatabaseUtil;
	
//	SMSInterceptDBHelper smsInterceptDBHelper;
	
	String STATUSBAR_POINT = "statusbar_point";
	String POP_POINT = "pop_point";
	String SOUND_POINT = "sound_point";
	String VIBRATE_POINT = "vibrate_point";
	String LIGHTSCREEN_POINT = "lightscreen_point";
	String PREF_NAME = "com.dongji.app.addressbook.encryption";
	String ISENCRYPTION = "isEncryption";
	
	boolean statusbar = false;
	boolean pop = false;
	boolean sound = false;
	boolean vibrate = false;
	boolean sms = false;
	boolean isEncryption = false;
	
	
	@Override
	public void onReceive(final Context context, Intent intent) {
		  
		
		if(intent.getAction().equals("android.provider.Telephony.WAP_PUSH_RECEIVED")) //彩信
		{
			
		}
		
		else{  //短信
			
			SmsMessage[] messages = getMessagesFromIntent(intent);
			
			for (SmsMessage message : messages) {
				
				String number = message.getDisplayOriginatingAddress();
				String n = PhoneNumberTool.cleanse(number);
				List<String> addr = new ArrayList<String>();
				addr.add(n);
				long thread_id = Threads.getOrCreateThreadId(context, addr);
				
				 ContentValues values = new ContentValues();
			     values.put("address", message.getDisplayOriginatingAddress());
			     values.put("body", message.getDisplayMessageBody());  
			     values.put("type", 1); 
			     values.put("read", 0);
			     values.put("status", -1);
			     values.put("date", System.currentTimeMillis());
			     values.put("thread_id", thread_id);
			     Uri uri =   context.getContentResolver().insert(Uri.parse("content://sms/"), values);

//			     System.out.println("插入短信  --->" + uri.toString() +" thread_id :" + thread_id   +"    number: " + number + "    body: " + message.getDisplayMessageBody());
			     
			}
			
			abortBroadcast(); //屏蔽系统广播
			
		}
		 
		 
	}
	
	//A large message might be broken into many, which is why it is an array of objects.
	public final SmsMessage[] getMessagesFromIntent(Intent intent) {
		Object[] messages = (Object[]) intent.getSerializableExtra("pdus");
		
		byte[][] pduObjs = new byte[messages.length][];
		for (int i = 0; i < messages.length; i++) {
			pduObjs[i] = (byte[]) messages[i];
		}
		
		byte[][] pdus = new byte[pduObjs.length][];
		int pduCount = pdus.length;
		
		SmsMessage[] msgs = new SmsMessage[pduCount];
		for (int i = 0; i < pduCount; i++) {
			pdus[i] = pduObjs[i];
			msgs[i] = SmsMessage.createFromPdu(pdus[i]);
		}
		
		return msgs;
	}
	
}
