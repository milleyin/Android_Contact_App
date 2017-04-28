package com.dongji.app.service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import com.dongji.app.adapter.EncryptionContentProvider;
import com.dongji.app.adapter.EncryptionDBHepler;
import com.dongji.app.addressbook.NewMessageActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.SmsDilaogActivity;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.KeywordEntity;
import com.dongji.app.entity.MmsContent;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.PhoneNumberTool;


/**
 * 
 * 消息刷新单元 (目前只支持对短信消息的监听，暂不支持彩信)
 * 
 * 通过查询系统消息数据库，找出最新一条消息,判断是否为新接收到的短信
 * 
 * 如果是新接收到的短信，则根据设置的规则，判断是否被拦截   以及  是否显示通知栏或短信弹窗
 * 
 * @author Administrator
 *
 */

public class MessagetIntercepteNotifationUtil {
	
	Context mContext;
	
	//消息通知的ID
    public static int Notification_ID = 110;  
	
    public static int THREAD_ID = -1;
    
	boolean statusbar = false;
	boolean pop = false; 
	boolean sound = false;
	boolean vibrate = false;
	boolean led = false;
	boolean isEncryption = false;
	
	public static final String TAG = "ImiChatSMSReceiver";

	SharedPreferences sms_checkbox_state = null;
	
	SharedPreferences interceptmode = null;
	
	SharedPreferences encryption = null;
	
	SQLiteDatabase db = null;

	MyDatabaseUtil myDatabaseUtil;
	
	String STATUSBAR_POINT = "statusbar_point";
	String POP_POINT = "pop_point";
	String SOUND_POINT = "sound_point";
	String VIBRATE_POINT = "vibrate_point";
	String LIGHTSCREEN_POINT = "lightscreen_point";
	String PREF_NAME = "com.dongji.app.addressbook.encryption";
	String ISENCRYPTION = "isEncryption";
	
	
	
	public MessagetIntercepteNotifationUtil(Context mContext) {
		super();
		this.mContext = mContext;
	}

	
	void update()
	{
		    getSetting(); 
		
			//查出最新的 未读的 短信
			Cursor sms_cur = mContext.getContentResolver().query(Uri.parse("content://sms/"), null , " type = 1 AND read = 0 AND seen!=1 ", null, " date DESC");
			final SmsContent smsContent = new SmsContent();
			if(sms_cur.moveToNext())
			{
				smsContent.setId(sms_cur.getLong(sms_cur.getColumnIndex("_id")));
				smsContent.setThread_id(sms_cur.getInt(sms_cur.getColumnIndex("thread_id")));
				smsContent.setDate(sms_cur.getLong(sms_cur.getColumnIndex("date"))); //短信的时间单位为 ： 毫秒
				smsContent.setSms_number(sms_cur.getString(sms_cur.getColumnIndex("address")));
				smsContent.setSms_body(sms_cur.getString(sms_cur.getColumnIndex("body")));
				smsContent.setSms_type(sms_cur.getInt(sms_cur.getColumnIndex("type")));
				smsContent.setStatus(sms_cur.getInt(sms_cur.getColumnIndex("status")));
			}
			sms_cur.close();
			
			boolean smsUpdate = false;
			
			if(System.currentTimeMillis() - smsContent.getDate()  < 1000)  // 1秒内则为新短信
			{
				smsUpdate = true;
			}
			
			
			if(smsUpdate)
			{
				  ContentValues cv = new ContentValues();
				  cv.put("seen", 1);
				  int update_num = mContext.getContentResolver().update(Uri.parse("content://sms/"), cv, "_id = " +  smsContent.getId(), null);
				  System.out.println("  update_num  --->  " + update_num);
					
				  handleSms(smsContent);
//				  System.out.println(" *******************  收到新短信 ***********************");
			}
			
	}
	
	void updateMMS()
	{
		//查出最新接收到  未读的 彩信
		 Cursor mms_cur = mContext.getContentResolver().query(Uri.parse("content://mms/"), null , " msg_box = 1 AND read = 0 ", null, " date DESC ");
		 final  MmsContent mmsContent = new MmsContent();
		 
		 if(mms_cur.moveToNext()){
			 
				//彩信的主题
				String subject = mms_cur.getString(mms_cur.getColumnIndex("sub"));
				
				try {
					if(subject!=null)
					{
						subject =  new  String(subject.getBytes("ISO8859_1"),"utf-8");
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} 
				
				if(subject==null)
				{
					mmsContent.setSubject("(无主题)");
				}else{
					mmsContent.setSubject(subject);
				}
				mmsContent.setId(mms_cur.getLong(mms_cur.getColumnIndex("_id")));
				mmsContent.setThread_id(mms_cur.getInt(mms_cur.getColumnIndex("thread_id")));
				mmsContent.setDate(Long.valueOf(mms_cur.getLong(mms_cur.getColumnIndex("date"))+"000")); //彩信的时间单位为 ： 秒
				mmsContent.setMsg_box(mms_cur.getInt(mms_cur.getColumnIndex("msg_box")));
				mmsContent.setSt(mms_cur.getInt(mms_cur.getColumnIndex("st")));
		}
		mms_cur.close();
		
		handleMms(mmsContent);
		
//		System.out.println(" *******************  收到新彩信   ***********************");
	}
	
	void getSetting()
	{
		sms_checkbox_state = mContext.getSharedPreferences("com.dongji.app.addressbook.dialingsetting", 0);
		interceptmode = mContext.getSharedPreferences("interceptmode", 0);
		
		statusbar = sms_checkbox_state.getBoolean(STATUSBAR_POINT, true);
		pop = sms_checkbox_state.getBoolean(POP_POINT, true);
		sound = sms_checkbox_state.getBoolean(SOUND_POINT, false);
		vibrate = sms_checkbox_state.getBoolean(VIBRATE_POINT, false);
		led = sms_checkbox_state.getBoolean(LIGHTSCREEN_POINT, true);

		encryption = mContext.getSharedPreferences(PREF_NAME,0);
		isEncryption = encryption.getBoolean(ISENCRYPTION, false);
		
	}
	
	private void handleMms(MmsContent mms) {
		
		getSetting();		
		
		//获取该彩信的号码
		String address = "";
		Cursor address_cursor = mContext.getContentResolver().query(Uri.parse("content://mms/"+mms.getId()+"/addr"), new String [] {"address"}, null, null, null);
		if(address_cursor.moveToNext())
		{
			address = address_cursor.getString(address_cursor.getColumnIndex("address"));
		}
		address_cursor.close();
		
		NotificationManager nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE); 
		Notification baseNF = new Notification();
		PendingIntent pd;
		
		Intent smsIntent = new Intent(mContext,Context.class);  
		pd = PendingIntent.getActivity(mContext, 0, smsIntent, 0); 
		
		baseNF.icon = R.drawable.default_contact;
		String name = (PhoneNumberTool.getContactInfo(mContext,address)) [0];
		baseNF.tickerText = address+" : 新彩信 ";
		baseNF.flags |= Notification.FLAG_AUTO_CANCEL; 
		String title = null;

		baseNF.setLatestEventInfo(mContext, address, "新彩信", pd);
		
		if(statusbar){
			if (!"".equals(name)) {
				title = name;
			} else {
				title = address;
			}
			baseNF.setLatestEventInfo(mContext, title, "新彩信", pd);
			
			THREAD_ID = (int)mms.getThread_id();
		}
		
		if(sound){
			baseNF.defaults |= Notification.DEFAULT_SOUND;
			baseNF.setLatestEventInfo(mContext, address, "新彩信", pd);
		}
		if(vibrate){
			baseNF.defaults |= Notification.DEFAULT_VIBRATE;
			baseNF.setLatestEventInfo(mContext, address,"新彩信", pd);
		}
		
		if(led){
			baseNF.ledARGB = 0xff00ff00;  
			baseNF.ledOnMS = 300;  
			baseNF.ledOffMS = 1000;  
			baseNF.flags |= Notification.FLAG_SHOW_LIGHTS; 
		}
		
		nm.notify(Notification_ID, baseNF);
		
		//弹窗
//		if(pop){
//			Intent popintent = new Intent(mContext, SmsDilaogActivity.class);
//			Bundle bundle = new Bundle();
//			bundle.putString("address", address);
//			bundle.putString("content", "新彩信");
//			bundle.putLong("date", mms.getDate());
//			popintent.putExtras(bundle);
//			popintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mContext.startActivity(popintent);
//			System.out.println("弹窗！！！！！！！！！！！！！！！！！！！！！");
//		}
		
	}
	
	private void handleSms(SmsContent mSmsContent) {
		
		String address = mSmsContent.getSms_number();
		String body = mSmsContent.getSms_body();
		
		//是否被拦截了
		boolean isBlock =  checkIsBlock(body,address);
		if(isBlock)
		{
			saveInterceptSMS(mContext, mSmsContent);
		}
		
		//查询是否为加密联系人
		boolean isMath = false;
		
		if(isEncryption)
		{
			String NUMBER = PhoneNumberTool.cleanse(address);
			
			List<EnContact> EN_CONTACTS = new ArrayList<EnContact>();
			
			Cursor cr = mContext.getContentResolver().query(EncryptionContentProvider.URIS,null, null, null, null);

			List<String> cotacts_ids = new ArrayList<String>();


			while (cr.moveToNext()) {
				String contactid = cr.getString(cr.getColumnIndex(EncryptionDBHepler.CONTACT_ID));
				cotacts_ids.add(contactid);

			}
			cr.close();

			for (String contactId : cotacts_ids) {
				
				EnContact enContact = new EnContact();
				enContact.setContactId(contactId);
				
				Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+ contactId, null, null);
				
				while (phones.moveToNext()) {
					// 遍历所有的电话号码
					String phoneNumber = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					enContact.getNumbers().add(phoneNumber);
//					System.out.println("phoneNumber ---> " + phoneNumber);
				}
				
				phones.close();
				EN_CONTACTS.add(enContact);
				
				
			}	
			
			for(EnContact ec:EN_CONTACTS)
			{
				for(String number:ec.getNumbers())
				{
					number = PhoneNumberTool.cleanse(number);
					if(number.equals(NUMBER))
					{
						isMath = true;
						break;
					}
				}
			}
		}
		
		
		//没有被拦截 ， 并且  非加密内容 
		if(!isMath && !isBlock) {
		
			NotificationManager nm = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE); 
			Notification baseNF = new Notification();
			
			String title = null;
			
			System.out.println(" newMessageActivity thread id ====== > " + NewMessageActivity.state);
			
			if(statusbar && NewMessageActivity.state != NewMessageActivity.STATE_DETAIL){
				
				String thread_id = String.valueOf(mSmsContent.getThread_id());
				
				Intent smsIntent = new Intent(mContext,NewMessageActivity.class);  
				smsIntent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
				smsIntent.putExtra(NewMessageActivity.DATA_NUMBER, address);
				
				PendingIntent pd = PendingIntent.getActivity(mContext, 0, smsIntent, 0); 
				
				baseNF.icon = R.drawable.default_contact;
				baseNF.flags |= Notification.FLAG_AUTO_CANCEL; 
				
				String name = (PhoneNumberTool.getContactInfo(mContext,address)) [0];
				
				if (name!=null) {
					title = name;
					baseNF.tickerText = name+"  "+address+"  "+ body;
				} else {
					baseNF.tickerText = address+"  "+ body;
					title = address;
				}
				baseNF.setLatestEventInfo(mContext, title, body, pd);
				
				THREAD_ID = (int)mSmsContent.getThread_id();
			}
			
			if(sound){
				baseNF.defaults |= Notification.DEFAULT_SOUND;
//				baseNF.setLatestEventInfo(mContext, address, body, pd);
			}
			if(vibrate){
				baseNF.defaults |= Notification.DEFAULT_VIBRATE;
//				baseNF.setLatestEventInfo(mContext, address,body, pd);
			}
			
			if(led){
				baseNF.ledARGB = 0xff00ff00;  
				baseNF.ledOnMS = 300;  
				baseNF.ledOffMS = 1000;  
				baseNF.flags |= Notification.FLAG_SHOW_LIGHTS; 
//				baseNF.setLatestEventInfo(mContext, address, body, pd);
				
			}
			
			nm.notify(Notification_ID, baseNF);
			
			//弹窗
			if(pop){
				Intent popintent = new Intent(mContext, SmsDilaogActivity.class);
				Bundle bundle = new Bundle();
				bundle.putString("address", address);
				bundle.putString("content", body);
				bundle.putLong("date", mSmsContent.getDate());
				popintent.putExtras(bundle);
				popintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				mContext.startActivity(popintent);
			}
			
	 }
}
	
	
	private boolean searchBlackList(Context context,String address){
		boolean isBlack = false;
		Cursor cursor = DButil.getInstance(context).queryBlack(address);
		if(cursor.getCount() > 0){
			isBlack = true;
		}
		cursor.close();
		
		return isBlack;
	}
	
	private boolean searchWhiteList(Context context,String address){
		boolean isWhite = false;
		Cursor cursor = DButil.getInstance(context).queryWhite(address);
		if(cursor.getCount() > 0){
			isWhite = true;
		}
		cursor.close();
		
		return isWhite;
	}
	
	private boolean searchKeyword(Context context,String body){
		boolean isKeyword = false;
		myDatabaseUtil = DButil.getInstance(context);
		
		List<KeywordEntity> keyList = myDatabaseUtil.queryKeyWord();
		
		if (keyList.size() > 0) {
			
			for (int i = 0; i < keyList.size(); i++) {
			
				KeywordEntity keywordEntity = keyList.get(i);
				
				String content = keywordEntity.getContent();
				
				if(body.contains(content)){
					isKeyword = true;
					break;
				}
			}
		}
		
		return isKeyword;
	}
	
	private boolean strangerIntercept(Context context,String address){
		boolean isStranger = true;
		
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
		if (phones.moveToFirst()) {
			do{
				String phonenumber = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				if(phonenumber.equals(PhoneNumberTool.cleanse(address))){
					isStranger = false;
					break;
				}
			}while(phones.moveToNext());
		}
		phones.close();
		
		return isStranger;
	}
	
	 private void saveInterceptSMS(Context context,SmsContent sms){
			
		   String body = sms.getSms_body();
		   String address = sms.getSms_number();
		   
		    //从系统数据库中删除短信
		    int delet_num = mContext.getContentResolver().delete(Uri.parse("content://sms/"), "_id = " + sms.getId(), null);
		   
		    System.out.println(" delet_num  ---> " + delet_num);
			
			String add =  PhoneNumberTool.cleanse(address);
			
			String contact_id = "";
			byte[] photoicon = null;
			String display_name = null;
			Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
			
			if (cursor.getCount() > 0) {
				
				if ( cursor.moveToFirst()) {
					
					do {
						
						String phone = PhoneNumberTool.cleanse(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
						
						if (add.equals(phone)) {
							
							contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
							
							display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
							
							String photo_id = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
							
							Cursor photo = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },"ContactsContract.Data._ID = " + photo_id, null, null);
							
							if (photo.moveToNext()) {
								
								photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
							}
							
							photo.close();
						}
						
					} while (cursor.moveToNext());
				}
				
			}
			
			cursor.close();
			
			SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm");
			myDatabaseUtil.addSmsIntercept(contact_id, address, body, df.format(new Date(System.currentTimeMillis())), photoicon, display_name);
			
		}
	 
	 
	 /**
	  * 判断一条信息 是否 应该被拦截
	  * @param key
	  * @param address
	  * @return
	  */
	 boolean checkIsBlock (String key , String address)
	 {
		 boolean result = false;

		 int mode = interceptmode.getInt("mode", 1);
			
			if(mode == 1){//拦截黑名单、关键字信息
				
				boolean isBlack = searchBlackList(mContext, address);   
				boolean isKeyword = searchKeyword(mContext,key); 
				
				if(isBlack || isKeyword){
					result = true;
				}
				
			}else if(mode == 2){//拦截陌生号码
				
				boolean isStranger = strangerIntercept(mContext, address); 
				
				if(isStranger){
					result = true;
				}
				
			}else if(mode == 3){//拦截白名单以外人员的信息
				boolean isWhite = searchWhiteList(mContext, address);   
				
				if(!isWhite){
					result = true;
				}
				
			}else if(mode == 4){//只拦截黑名单模式
				boolean isBlack = searchBlackList(mContext, address);  
				
				if(isBlack){
					result = true;
				}
			}
		 
		 return result;
		 
	 }
}
