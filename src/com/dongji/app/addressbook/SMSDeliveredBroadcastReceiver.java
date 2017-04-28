package com.dongji.app.addressbook;

import com.dongji.app.tool.PhoneNumberTool;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.Toast;

/**
 * 
 * 短信 接收状态的广播接收
 * @author Administrator
 * 
 *
 */
public class SMSDeliveredBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		
	   int id = intent.getIntExtra("_id", -1);
		
	   System.out.println("  SMSDeliveredBroadcastReceiver ---> id" +  id);
	   
	   SharedPreferences sf = context.getSharedPreferences(SystemSettingActivity.SF_NAME, 0);
	   int mode = sf.getInt(SystemSettingActivity.SF_KEY_SMS_REMIND, 1);
	   
	   String str = "";
	   Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/"), null, "_id = " + id , null, null);
	   
	   if(cursor.moveToNext())
	   {
		   String address = cursor.getString(cursor.getColumnIndex("address"));
		   
		   String info [] = PhoneNumberTool.getContactInfo(context, address);
		   String name = info [0];
		   
		   if(name ==null)
		   {
			   str = address + "已成功接收";
		   }else{
			   str = name + "已成功接收";
		   }
		   
	   }else{
		   str = "对方已成功接收";
	   }
	   
	   cursor.close();
	   
	   if(mode == 0)
	   {
		   Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	   }
	   
	}
	
}
