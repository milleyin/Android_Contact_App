package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.tool.PhoneNumberTool;
import com.umeng.common.net.m;

public class PopRemindNumberListAdapter extends BaseAdapter {

	Context context;
	
	public CheckBox cb;
	
	public int select_positon=-1;
	
	private List<CallLogInfo> list = new ArrayList<CallLogInfo>();
	
	public PopRemindNumberListAdapter(Context context,List<CallLogInfo> list)
	{
		this.context = context;
		this.list = list;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView=LayoutInflater.from(context).inflate(R.layout.remind_number_pick_lsit_item, null);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.cb = (CheckBox)convertView.findViewById(R.id.cb);
		viewHolder.contactitem_avatar_iv = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		viewHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
		viewHolder.tv_number = (TextView)convertView.findViewById(R.id.tv_number);
		viewHolder.tv_area = (TextView)convertView.findViewById(R.id.tv_area);
		viewHolder.tv_last_call_date = (TextView)convertView.findViewById(R.id.tv_last_call_date);
		
		viewHolder.cb.setTag(position);
		
//		if(position == select_positon)
//		{
//			viewHolder.cb.setChecked(true);
//			cb = viewHolder.cb;
//		}
		
		 CallLogInfo callInfo = (CallLogInfo) list.get(position);
		 viewHolder.tv_name.setText(callInfo.getmCaller_name());
		 viewHolder.tv_number.setText(callInfo.getmCaller_number());
		 
		 Cursor cursor = context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts.PHOTO_ID}, ContactsContract.Contacts._ID + " = " + callInfo.getId(), null,null);
		 
		 if ( cursor.moveToFirst()) {
			 
			 String photo_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID));
			 
				if (photo_id != null) {
					Cursor photo = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
							"ContactsContract.Data._ID = " + photo_id, null, null);
					if (photo.moveToNext()) {
						byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
						ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
						Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
						if (contactPhoto == null){
							viewHolder.contactitem_avatar_iv.setImageResource(R.drawable.default_contact);
						}
						else{
							viewHolder.contactitem_avatar_iv.setImageBitmap(contactPhoto);
						}
					}
					photo.close();
				} else {
					viewHolder.contactitem_avatar_iv.setImageResource(R.drawable.default_contact);
				}
			 
		 }
		 
		 cursor.close();
		 
		 callInfo.setmCaller_area(MainActivity.CheckNumberArea(callInfo.getmCaller_number()));
		 
		 viewHolder.tv_area.setText(callInfo.getmCaller_area());
		
		 
		 if (!callInfo.getmCaller_number().replace(" ", "").equals("")) {
		 
			 Cursor c = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER+" = " + callInfo.getmCaller_number() , null, CallLog.Calls.DATE +" DESC");
		
			 if(c.moveToNext())
			 {
				   Date date = new Date(Long.parseLong(c.getString(c.getColumnIndex(CallLog.Calls.DATE))));
					
					SimpleDateFormat time_sfd = new SimpleDateFormat("yyyy-MM-dd");
					String time = time_sfd.format(date);
					viewHolder.tv_last_call_date.setText(time);
			 }else{
				    viewHolder.tv_last_call_date.setText("无" );
			 }
			 c.close();
			 
		 } else {
			 
			 viewHolder.tv_last_call_date.setText("无" );
			 
		 }
		 
		return convertView;
	}
	
	class ViewHolder {
		CheckBox cb;
		
		ImageView contactitem_avatar_iv;
		TextView tv_name;
		TextView tv_number;
		TextView tv_area;
		TextView tv_last_call_date;
	}
}
