package com.dongji.app.service;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.CallLog;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.sqllite.ContactLauncherDBHelper;
import com.dongji.app.tool.PhoneNumberTool;

public class ChangHeatReceiver extends BroadcastReceiver {

	private static final String ACTION_DATE_CHANGED = Intent.ACTION_DATE_CHANGED;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		if(ACTION_DATE_CHANGED.equals(action)){
			
			ContactLauncherDBHelper dbHelper = new ContactLauncherDBHelper(context);
			
			Cursor calllog = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{CallLog.Calls.CACHED_NAME}, " date between "+System.currentTimeMillis()+" and "+(System.currentTimeMillis()-(24*60*3600*1000)), null, CallLog.Calls.DATE+" desc");
			
			String name = "";
			
			if(calllog.moveToFirst()){
				
				do{
					
					name = calllog.getString(calllog.getColumnIndex(CallLog.Calls.CACHED_NAME))+",";
					
					
				}while(calllog.moveToNext());
			}
			
			calllog.close();

			Cursor sms = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"address"}," date between "+System.currentTimeMillis()+" and "+(System.currentTimeMillis()-(24*60*3600*1000)) , null, " date desc");
			
			String phone = "";
			
			if(sms.moveToFirst()){
				
				do{
					phone = PhoneNumberTool.cleanse(sms.getString(sms.getColumnIndex("address"))) +",";
					
				}while(sms.moveToNext());
			}
			
			sms.close();
			
			SQLiteDatabase db  = dbHelper.getReadableDatabase();
			
			Cursor heat = db.query(dbHelper.heat_table, new String[]{dbHelper.HEAT,dbHelper.CONTACTNAME,dbHelper.NUMBER}, null, null, null, null, null);
			
			if(heat.moveToFirst()){
				
				do{
					
					db = dbHelper.getWritableDatabase();
					
					ContentValues values = new ContentValues();
					
					int value = heat.getInt(heat.getColumnIndex(dbHelper.HEAT));
					
					String heat_name = heat.getString(heat.getColumnIndex(dbHelper.CONTACTNAME));
					String number = heat.getString(heat.getColumnIndex(dbHelper.NUMBER));
					
					if(number.contains(phone) || heat_name.contains(name)){
						
						values.put(dbHelper.HEAT, value + 1);
						
					}else if(value > 0 && (!number.contains(phone)||!heat_name.contains(name))){
						values.put(dbHelper.HEAT, value - 1);
					}
					
					db.update(dbHelper.heat_table, values, dbHelper.CONTACTNAME+"='"+heat_name+"'", null);
					
				}while(heat.moveToNext());
				
			}
			
			db.close();
			
		}
		
	}

}
