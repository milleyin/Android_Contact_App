package com.dongji.app.addressbook;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

/**
 * 
 * 短信 发送状态的广播接收
 * @author Administrator
 * 
 *
 */
public class SMSSentBroadcastReceiver extends BroadcastReceiver {

	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		if(intent.getAction().equals(NewMessageActivity.SENT_SMS_ACTION))
		{
			 int id = intent.getIntExtra("_id", -1);
			 
			 SharedPreferences sf = context.getSharedPreferences(SystemSettingActivity.SF_NAME, 0);
			 int mode = sf.getInt(SystemSettingActivity.SF_KEY_SMS_REMIND, 1);
			 
			//判断短信是否发送成功  
		     switch (getResultCode()) {  
		     
		     case Activity.RESULT_OK:  
		      
		       System.out.println(" BroadcastReceiver  id  -->  " + id);
//		       
		       Cursor c =  context.getContentResolver().query(Uri.parse("content://sms/"), null, "_id = " + id, null , null );
		       int type = -1;
		       if(c.moveToNext())
		       {
		    	   type  = c.getInt(c.getColumnIndex("type"));
		       }
//		       
//		       int status = c.getInt(c.getColumnIndex("status"));
		       System.out.println(" type  ---> " + type);
		       c.close();
//		       
		       //更新发送状态
		       if(type != NewMessageActivity.SMS_TYPE_SENT)
		       {
		    	   ContentValues cv = new ContentValues();
			       
			       cv.put("status", NewMessageActivity.SEND_SUCCESS);
			       cv.put("type", NewMessageActivity.SMS_TYPE_SENT);
			       
			       context.getContentResolver().update(Uri.parse("content://sms/"), cv, "_id = " + id , null);
			       
			       if(mode ==0)
			       {
			    	   Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show(); 
			       }
			       
			       //刷新adapter
//			       if(MainActivity.cur_newMessageLayout != null)
//			       {
//				       MainActivity.cur_newMessageLayout.updateAfterSend(id, NewMessageLayout.SMS_TYPE_SENT);
//			       }
		       }
		       
		    	   
		       break;  
		       
		       default:  
		    	
		    	Cursor f_c =  context.getContentResolver().query(Uri.parse("content://sms/"), null, "_id = " + id, null , null );
		    	
		    	int f_type = -1;
		    	if(f_c.moveToNext())
		    	{
		    		f_type = f_c.getInt(f_c.getColumnIndex("type"));
		    	}
		    	
			    f_c.close();
		    	   
			   if(f_type != NewMessageActivity.SMS_TYPE_FAILED)
			   {
				   ContentValues cv_f = new ContentValues();
			       
			       cv_f.put("status", NewMessageActivity.SEND_SUCCESS);
			       cv_f.put("type", NewMessageActivity.SMS_TYPE_FAILED);
				       
				   int f = context.getContentResolver().update(Uri.parse("content://sms/"), cv_f, "_id = " + id, null);
				   System.out.println(" 发送失败    i ---> " + f);
				       
				   if(mode ==0)
			       {
					   Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();  
			       }
			       
				   //刷新adapter
//				   if(MainActivity.cur_newMessageLayout != null)
//			       {
//				       MainActivity.cur_newMessageLayout.updateAfterSend(id, NewMessageLayout.SMS_TYPE_FAILED);
//			       }
				   
			   }
			   
		       break;  
		       } 
		}
		
	}
}
