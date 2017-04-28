package com.dongji.app.addressbook;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.dongji.app.entity.SmsContent;
import com.dongji.app.entity.Threads;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.TimeTool;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.util.Log;

/**
 * 
 * 闹钟服务:  提醒，  联系人分组提醒 ， 定时短信
 * 
 * @author Administrator
 *
 */
public class AlarmReceiver extends BroadcastReceiver
{
	public static String ALARM_TYPE ="type";
	
	public static final int ALARM_REMIND = 0; //提醒
	public static final int ALARM_GROUP_REMIND = 1; //联系人分组提醒
	public static final int ALARM_SMS = 2; //定时短信
	
  @Override
	public void onReceive(final Context context, final Intent intent) {
		SharedPreferences sf = context.getSharedPreferences(
				SystemSettingActivity.SF_NAME, 0);

		int type = intent.getIntExtra(ALARM_TYPE, 0);

		switch (type) {
		case ALARM_REMIND:
			int close_remind_all = sf.getInt(SystemSettingActivity.SF_KEY_REMIND_ALL, 1);

			if (close_remind_all == 1) {
				Intent alaramIntent = new Intent(context,RemindPopActivity.class);
				alaramIntent.putExtra(MyDatabaseUtil.REMIND_ID,intent.getIntExtra(MyDatabaseUtil.REMIND_ID, -1));
				alaramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(alaramIntent);

			} else { // 关闭了全部提醒

				long remind_id = intent.getIntExtra(MyDatabaseUtil.REMIND_ID,-1);

				System.out.println("  触发提醒  ---->" + remind_id);
				
				Cursor c = DButil.getInstance(context).queryRemind(remind_id);

				if (c.moveToNext()) {
					long start_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START));
					int remind_type = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE));
					int remind_num = c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM));
					int repeat_type = c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE));
					String repeat_condition = c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION));
					int repeat_freq = c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ));
					long repeat_start_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME));
					long repeat_end_time = c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME));
					String time_filter = c.getString(c.getColumnIndex(MyDatabaseUtil.TIME_FILTER));

					long next_time = TimeTool.getNextTime(start_time,remind_type, remind_num, repeat_type,repeat_condition, repeat_freq, repeat_start_time,
							repeat_end_time, time_filter);

					// 保留
					if (next_time != -1) {
						Intent it = new Intent(context, AlarmReceiver.class);
						it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);
						PendingIntent pit = PendingIntent.getBroadcast(context,(int) remind_id, it, 0);
						AlarmManager amr = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
						// amr.cancel(pit);//先取消 ？
						amr.set(AlarmManager.RTC_WAKEUP, next_time, pit);

					} else {

						// 没有下一次的提醒了
						Intent it = new Intent(context, AlarmReceiver.class);
						it.putExtra(MyDatabaseUtil.REMIND_ID, remind_id);
						PendingIntent pit = PendingIntent.getBroadcast(context,(int) remind_id, it, 0);
						AlarmManager amr = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
						amr.cancel(pit);// 先取消 ？
					}
				}

				c.close();
			}

			break;

		case ALARM_GROUP_REMIND: 

			int close_group_remind = sf.getInt(SystemSettingActivity.SF_KEY_REMIND_GROUP, 1);
			
			if(close_group_remind ==1)
			{
				Intent alaramIntent = new Intent(context,ContactGroupRemindPopupActivity.class);
				alaramIntent.putExtra(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID,intent.getIntExtra(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID, -1));
				alaramIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(alaramIntent);
			}
			
			break;

		case ALARM_SMS:
			
			long timeId= intent.getIntExtra("_id", -1);
			
			System.out.println("  AlarmReceiver  ---> timeId " + timeId );
			
			if(timeId!=-1)
			{
				new Thread(new SMSTimingThread(context, timeId)).start();
			}
			
			break;

		default:
			break;
		}
	}
  
   class SMSTimingThread implements Runnable{

	   long timeId;
	   Context context;
	   ContentResolver contentResolver;
	   
	   public SMSTimingThread(Context context, long timeId)
	   {
		   this.context = context;
		   this.timeId = timeId;
		   contentResolver = context.getContentResolver();
		  
	   }
	   
	   public void send()
	   {
			SmsManager smsManager = SmsManager.getDefault();
			Intent sentIntent = new Intent(NewMessageActivity.SENT_SMS_ACTION); 
			PendingIntent sentPI ;
			
			Intent deliverIntent = new Intent(NewMessageActivity.DELIVERED_SMS_ACTION);  
    	    PendingIntent deliverPI; 

		   Cursor smsCursor = contentResolver.query(NewMessageActivity.SMS_URI, null, "_id = " + timeId, null, null);
		   
		   if(smsCursor.moveToNext())
		   {
			  int id = smsCursor.getInt(smsCursor.getColumnIndex("_id"));
			  String address = smsCursor.getString(smsCursor.getColumnIndex("address")); //号码
			  String body = smsCursor.getString(smsCursor.getColumnIndex("body")); //短信内容
			  
			  List<String> texts=smsManager.divideMessage(body); 
			  
			  //逐条发送短信  
					for(String text:texts)  
					{
					sentIntent.putExtra("_id", id);
					sentPI = PendingIntent.getBroadcast(context, id, sentIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					
					deliverIntent.putExtra("_id", id);
					deliverPI = PendingIntent.getBroadcast(context, id, deliverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
					
				    smsManager.sendTextMessage(address, null, text, sentPI, deliverPI);
				    
					} 
					
			  updateAfterSend(id);
		   }
		   
		   smsCursor.close();
	   }
   
   private void updateAfterSend(int id)
   {
	   ContentValues cv = new ContentValues();
	   cv.put("status", NewMessageActivity.SEND_PENDING);
	   contentResolver.update(NewMessageActivity.SMS_URI, cv, "_id = " + id, null);

//	   if(MainActivity.cur_newMessageLayout!=null)
//	   {
//		   MainActivity.cur_newMessageLayout.updateAfterTimmingSend(id);
//	   }
   }
	   
	@Override
	public void run() {
		 send();
	 }
   }
}