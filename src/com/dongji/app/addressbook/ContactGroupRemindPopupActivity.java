package com.dongji.app.addressbook;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Data;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;

/**
 * 
 * 联系人分组提醒弹窗
 * 
 * @author Administrator
 *
 */
public class ContactGroupRemindPopupActivity extends Activity implements OnClickListener {

	ImageView btn_close;
	
	TextView tv_top_title;
	TextView tv_name;
	
	ImageView img_head;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_remind_group);
		
		btn_close = (ImageView)findViewById(R.id.btn_close); 
		btn_close.setOnClickListener(this);
		
		tv_top_title = (TextView)findViewById(R.id.tv_top_title);
		tv_name = (TextView)findViewById(R.id.tv_name);
		
		img_head = (ImageView)findViewById(R.id.img_head);
		
		
		int group_remind_id = getIntent().getIntExtra(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID, -1);
		
		if(group_remind_id==-1)
		{
			finish();
		}else{
			
			Cursor cursor = DButil.getInstance(this).queryGroupRmind(group_remind_id);
			cursor.moveToNext();
			int group_id = cursor.getInt(cursor.getColumnIndex(MyDatabaseUtil.CONTACT_GROUP_ID));
			long time_gap = cursor.getLong(cursor.getColumnIndex(MyDatabaseUtil.CONTACT_GROUP_TIME_GAP));
			cursor.close();
			
			
			SharedPreferences sf = getSharedPreferences(SystemSettingActivity.SF_NAME, 0);;
			
			String name = sf.getString(SystemSettingActivity.SF_KEY_SECRETARY_NAME, "小秘书"); //秘书的名字
			String title = "小秘书 "+name+" 提醒您";
			tv_top_title.setText(Html.fromHtml(title.replace(name, "<font color='#3d8eba'>" + name+ "</font>")));
			
			int sex = sf.getInt(SystemSettingActivity.SF_KEY_SECRETARY_SEX, 1); //秘书性别:  1:女 ;   0:男
			
			if(sex == 0)
			{
				img_head.setImageResource(R.drawable.male_secretary);
			}
			
			//分组的名称
			Cursor group_cursor = getContentResolver().query(ContactsContract.Groups.CONTENT_URI, null, "_id = " + group_id, null, null);
			
			group_cursor.moveToNext();
			String group_name = group_cursor.getString(group_cursor.getColumnIndex("title")); 
			group_cursor.close();
			
			Cursor c = DButil.getInstance(this).queryGroupRmind(group_remind_id);
			c.moveToNext();
			
//			int group_id = c.getInt(c.getColumnIndex(MyDatabaseUtil.CONTACT_GROUP_ID));
			
			String[] groups= new String[]{GroupMembership.CONTACT_ID};
			String where= GroupMembership.GROUP_ROW_ID + "="+ group_id +" AND " + Data.MIMETYPE + " = " + " '" + GroupMembership.CONTENT_ITEM_TYPE + "'";
			Cursor groupCursor= getContentResolver().query(Data.CONTENT_URI, groups, where, null, null);
			   
//			System.out.println(" 分组有多少个联系人  : ------>" + groupCursor.getCount());

			int column_index = groupCursor.getColumnIndex(GroupMembership.CONTACT_ID);
			
			StringBuffer sb = new StringBuffer();
			
			while(groupCursor.moveToNext())
			{
				long contact_id = groupCursor.getLong(column_index);
				
				Cursor cur = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String [] { ContactsContract.Contacts.DISPLAY_NAME }, ContactsContract.Contacts._ID + " = " + contact_id , null,null);
				
				cur.moveToNext();
				
				String display_name = cur.getString(0);
				cur.close();
				
				if(display_name!=null)
				{
					sb.append(display_name+", ");
				}
			}
			
			c.close();
			
			long day = time_gap/(24L*60L*60L*1000L);
			
			String day_str = "";
			if(day<30)
			{
				day_str = day+"天";
			}else{
				day_str = day/30L + "月";
			}
			
			
			String content = "<font color='#3d8eba'>" + group_name+ "</font> 分组下" + "<font color='#3d8eba'>"+sb.toString()+"</font>已经有 <font color='#3d8eba'>" + day_str+"</font>没有联系了,马上约他们去聚聚吧!";
			
			tv_name.setText(Html.fromHtml(content));
		}
	}

	@Override
	public void onClick(View v) {
		finish();
	}
}
