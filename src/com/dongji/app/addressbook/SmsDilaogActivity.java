package com.dongji.app.addressbook;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.gsm.SmsManager;
import android.text.format.DateFormat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.entity.ContactBean;
import com.dongji.app.service.MessagetIntercepteNotifationUtil;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.entity.Threads;
import com.dongji.app.tool.AddBlackWhite;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;

/**
 * 
 *  短信弹窗
 * 
 * @author Administrator
 *
 */
public class SmsDilaogActivity extends Activity implements OnClickListener {

	ImageView contact_img;
	TextView contact_name;
	TextView contact_phone;
	TextView area;
	TextView time;
	TextView sms_message;
	LinearLayout exit;
	Button delete;
	Button blacklist;
	Button reply;
	Button quick_reply;
	
	LinearLayout quick_reply_layout;
	LinearLayout tips_layout;
	EditText quick_reply_content;
	TextView tips_txt;
	Button quick_reply_btn;
	Button btn_yes;
	
	Bundle bundle = null;
	
	ContactBean contactBean = null;

	AddBlackWhite addBlackWhite;
	
	public static final Uri SMS_URI = Uri.parse("content://sms/");
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addBlackWhite = new AddBlackWhite(this);
		
		bundle = getIntent().getExtras();

		setContentView(R.layout.activity_smsdilaog);

		contactBean = new ContactBean();
		
		initView();
		bundleValue(bundle);
	}

	
	private void initView() {
		contact_img = (ImageView) findViewById(R.id.contact_img);
		contact_name = (TextView) findViewById(R.id.contact_name);
		contact_phone = (TextView) findViewById(R.id.contact_phone);
		area = (TextView) findViewById(R.id.area);
		time = (TextView) findViewById(R.id.time);
		sms_message = (TextView) findViewById(R.id.sms_message);
		exit = (LinearLayout) findViewById(R.id.exit);
		delete = (Button) findViewById(R.id.delete);
		blacklist = (Button) findViewById(R.id.blacklist);
		reply = (Button) findViewById(R.id.reply);
		quick_reply = (Button) findViewById(R.id.quick_reply);
		
		quick_reply_layout = (LinearLayout) findViewById(R.id.quick_reply_layout);
		tips_layout = (LinearLayout) findViewById(R.id.tips_layout);
		quick_reply_content = (EditText) findViewById(R.id.quick_reply_content);
		quick_reply_btn = (Button) findViewById(R.id.quick_reply_btn);
		btn_yes = (Button) findViewById(R.id.btn_yes);
		tips_txt = (TextView) findViewById(R.id.tips);
		
		exit.setOnClickListener(this);
		delete.setOnClickListener(this);
		blacklist.setOnClickListener(this);
		reply.setOnClickListener(this);
		quick_reply.setOnClickListener(this);
		quick_reply_btn.setOnClickListener(this);
		btn_yes.setOnClickListener(this);
	}

	
	private void bundleValue(Bundle bundle) {
		String phone = bundle.getString("address");
		String content = bundle.getString("content");
		Long date = bundle.getLong("date");
		
		contact_phone.setText(phone);
		
//		area.setText(getResources().getString(R.string.area));
		area.setText(MainActivity.CheckNumberArea(phone));
		
		boolean is24 =  DateFormat.is24HourFormat(this); 
		
		SimpleDateFormat df = null;
		
		if (is24) {
			df = new SimpleDateFormat("HH:mm");
		} else {
			df = new SimpleDateFormat("hh:mm");
		}
		
		
		time.setText(TimeTool.getTimeStrhhmm(date));
		
		sms_message.setText(content);
		
		Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.NUMBER},null, null, null);
		Long contact_id = null;
		if (phones.moveToFirst()) {
			do{
				String number = PhoneNumberTool.cleanse( phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				if(number.equals(PhoneNumberTool.cleanse(phone))){
					contact_id = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
				}
			}while(phones.moveToNext()&&!phones.isAfterLast());
			
		}
		phones.close();
		
		contactBean.setNumber(PhoneNumberTool.cleanse(phone));
		
		if(contact_id != null){
			
			contactBean.setContact_id(contact_id);
			Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.DISPLAY_NAME,ContactsContract.Contacts.PHOTO_ID}, ContactsContract.Contacts._ID+"="+contact_id, null,ContactsContract.Contacts._ID+" desc");
			if(cursor.moveToFirst()){
				String display_name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
				String photo_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
				if(display_name != null){
					contact_name.setText(display_name);
				}
				contactBean.setNick(display_name);
				if(photo_id != null){
					Cursor photo = getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
							"ContactsContract.Data._ID = " + photo_id, null, null);
					if (photo.moveToNext()) {
						byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
						
						contactBean.setPhoto(photoicon);
						
						ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
						Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
						if (contactPhoto == null)
							contact_img.setImageResource(R.drawable.default_contact);
						else
							contact_img.setImageBitmap(contactPhoto);
					}
					photo.close();
				} else {
					contact_img.setImageResource(R.drawable.default_contact);
				}
			}
			cursor.close();
		}else{
			contact_name.setText("");
			contact_img.setImageResource(R.drawable.default_contact);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.exit:
			SmsDilaogActivity.this.finish();
			break;
		case R.id.delete:
			String phone = bundle.getString("address");
			
			System.out.println(" delete phone -=---- > " + phone);
			
			Uri uri = Uri.parse("content://sms/");
			Cursor cursor = getContentResolver().query(uri, new String[]{"thread_id","_id"}, " address = '"+phone+"'", null, " date desc");
			
			if (cursor.getCount() > 0) {
				
				if(cursor.moveToFirst()){
					Long thread_id = cursor.getLong(cursor.getColumnIndex("thread_id"));
					Long id = cursor.getLong(cursor.getColumnIndex("_id"));
					int count = getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id),"_id =" + id, null);
					
					if (count > 0) {
						
						Toast.makeText(this, "信息删除成功", Toast.LENGTH_SHORT).show();
						SmsDilaogActivity.this.finish();
						
					} else {
						Toast.makeText(this, "信息删除失败", Toast.LENGTH_SHORT).show();
					}
					
	//				Cursor isDelete = getContentResolver().query(uri,null, " _id = "+id, null, null);
	//				if(isDelete.getCount() == 0)
	//					SmsDilaogActivity.this.finish();
	//				isDelete.close();
				}
				
			} else {
				
				SmsDilaogActivity.this.finish();
				
			}
			cursor.close();
			
			NotificationManager nm = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE); 
			nm.cancel(MessagetIntercepteNotifationUtil.Notification_ID);
			
			break;
		case R.id.blacklist:
			
//			Cursor contact = db.query(blackList.table, null, blackList.CONTACT_ID+"='"+contactBean.getContact_id()+"'", null, null, null, null);
//			if(contact.getCount() == 0){
//				db = blackList.getWritableDatabase();
//				
//				ContentValues blackvalues = new ContentValues();
//				
//				if(contactBean.getContact_id() == null){
//					
//					blackvalues.put(blackList.CONTACT_ID, "");
//					blackvalues.put(blackList.CONTACT_NAME, contactBean.getNumber());
//					blackvalues.put(blackList.CONTACT_NUMBER, contactBean.getNumber());
//					blackvalues.put(blackList.CONTACT_PHOTO, "");
//					
//				}else{
//				
//					blackvalues.put(blackList.CONTACT_ID, contactBean.getContact_id());
//					blackvalues.put(blackList.CONTACT_NAME, contactBean.getNick());
//					blackvalues.put(blackList.CONTACT_NUMBER, contactBean.getNumber());
//					blackvalues.put(blackList.CONTACT_PHOTO, contactBean.getPhoto());
//					
//					MainActivity.BLACKLIST.add(contactBean.getContact_id());
//					
//				}
//				
//				db.insert(blackList.table, blackList._ID, blackvalues);
//			}
//			MyDatabaseUtil myDatabaseUtil = DButil.getInstance(this);
//			Cursor contact = myDatabaseUtil.queryBlack(contactBean.getNumber());
//			
//			if(contact.getCount() == 0){
//				
//				Cursor white = myDatabaseUtil.queryWhite(contactBean.getNumber());
//				
//				if (white.getCount() > 0) {
//					
//					myDatabaseUtil.deleteWhite(contactBean.getNumber());
//					
//				}
//				
//				white.close();
//				
//				myDatabaseUtil.insertBlack(contactBean.getNumber());
//									
//			} else {
//				
//				Toast.makeText(this, "黑名单中存在该联系人！", Toast.LENGTH_SHORT).show();
//			}
//			contact.close();
			
			int count = addBlackWhite.saveBlack(contactBean.getNumber());
			if (count  > 0 ) {

				Toast.makeText(this, "黑名单中存在该联系人！", Toast.LENGTH_SHORT).show();
			}
			
			SmsDilaogActivity.this.finish();
			
			break;
		case R.id.reply:
			
			String thread_id = NewMessageActivity.queryThreadIdByNumber(getApplicationContext(),contact_phone.getText().toString());
			Intent intent = new Intent(getApplicationContext(), NewMessageActivity.class);
			intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
			intent.putExtra(NewMessageActivity.DATA_NUMBER, contact_phone.getText().toString());

			startActivity(intent);
			
			this.finish();
			
			break;
		case R.id.quick_reply:
			TranslateAnimation animation = new TranslateAnimation(0, 0, 150, 0);
			quick_reply_layout.setAnimation(animation);
			
			if (quick_reply_layout.getVisibility() == View.GONE) 
				quick_reply_layout.setVisibility(View.VISIBLE);
			else 
				quick_reply_layout.setVisibility(View.GONE);
			
			
			break;
		case R.id.quick_reply_btn:
			String strContent = quick_reply_content.getText().toString();
			
			if ( !strContent.equals("")){
//				String strNo =  bundle.getString("address");
//	            SmsManager smsManager = SmsManager.getDefault();
//	            PendingIntent sentIntent = PendingIntent.getBroadcast(SmsDilaogActivity.this, 0, new Intent(), 0);
//	            //如果字数超过70,需拆分成多条短信发送
//	            if (strContent.length() > 70) {
//	                List<String> msgs = smsManager.divideMessage(strContent);
//	                for (String msg : msgs) {
//	                    smsManager.sendTextMessage(strNo, null, msg, sentIntent, null);                        
//	                }
//	            } else {
//	                smsManager.sendTextMessage(strNo, null, strContent, sentIntent, null);
//	            }
//	//            Toast.makeText(SmsDilaogActivity.this, "短信发送完成", Toast.LENGTH_LONG).show();
//	
//				ContentValues values = new ContentValues();
//				values.put("address", strNo);
//				values.put("body",strContent);
//				values.put("status",-1);
//				getContentResolver().insert(Uri.parse("content://sms/sent"),values);
//				
//				tips_txt.setText("回复成功");
				
				send(strContent);
				
//				Toast.makeText(this, "信息发送成功", Toast.LENGTH_SHORT).show();
				
				quick_reply_layout.setVisibility(View.GONE);
				SmsDilaogActivity.this.finish();
	//			TranslateAnimation animation2 = new TranslateAnimation(0, 0, 0, 150);
	//			tips_layout.setAnimation(animation2);
	//			tips_layout.setVisibility(View.VISIBLE);
				
				NotificationManager nm1 = (NotificationManager) getApplication().getSystemService(NOTIFICATION_SERVICE); 
				nm1.cancel(MessagetIntercepteNotifationUtil.Notification_ID);
				
			} else {
				
				Toast.makeText(this, "回复内容不能为空，请重新输入！", Toast.LENGTH_SHORT).show();
				
			}
			break;
		case R.id.btn_yes:
			SmsDilaogActivity.this.finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		bundle = intent.getExtras();
		bundleValue(bundle);
	}
	
	
	void send(String content)
	{
		String number =  bundle.getString("address");
		
		List<String> addr = new ArrayList<String>();
		addr.add(number);

		long thread_id = Threads.getOrCreateThreadId(this, addr);
		
		sendSMS(addr, content, Long.valueOf(thread_id));
		
	}
	
	
	/**
	 * 发送短信
	 * @param phone 号码列表
	 * @param body 短息内容
	 * @param threadId  会话id
	 */
	public void sendSMS(List<String> phone,String body,long threadId){
		
		    final SmsManager smsManager = SmsManager.getDefault();
    		
    		Intent sentIntent = new Intent(NewMessageActivity.SENT_SMS_ACTION);  
    	    Intent deliverIntent = new Intent(NewMessageActivity.DELIVERED_SMS_ACTION);  
    	    
    	    
            for(final String pno:phone ){
                
                List<String> texts = smsManager.divideMessage(body); 
                
		        //逐条发送短信  
				for(final String text:texts)  
				{
				   int id = writeSmsToDataBase(pno, text, threadId);
				   
				   sentIntent.putExtra("_id", id);
				   final PendingIntent  sentPI = PendingIntent.getBroadcast(this, id, sentIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				   
				   deliverIntent.putExtra("_id", id);
				   final PendingIntent deliverPI = PendingIntent.getBroadcast(this, id, deliverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				   
				   new Thread(new Runnable() {
					
					@Override
					public void run() {
						smsManager.sendTextMessage(pno, null, text, sentPI, deliverPI);
					}
				}).start();
				   
//				   System.out.println(" 发送 ： ============ pno :" + pno + " text :" + text );
				   
				}         
           }   
       }
	
	private int writeSmsToDataBase(String phoneNumber, String smsContent,long threadId)  
    {  
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", smsContent);  
        values.put("type", 0); 
        values.put("read", 1);
        values.put("status", NewMessageActivity.SEND_PENDING );
        values.put("thread_id", threadId);
        Uri uri =  getContentResolver().insert(SMS_URI, values);
        
        Cursor c = getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        
        c.moveToNext();
        
        int id = c.getInt(c.getColumnIndex("_id"));
        
        c.close();

        return id;
    }  
	
	
}
