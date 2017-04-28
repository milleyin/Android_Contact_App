package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.tool.PhoneNumberTool;

public class PhoneInterceptAdapter extends BaseAdapter {

	Context context;
	
	List<CallLogInfo> list = new ArrayList<CallLogInfo>();
	
	OnClickListener onClickListener;
	
	public PhoneInterceptAdapter(Context context,List<CallLogInfo> list,OnClickListener onClickListener){
		
		this.context = context;
		this.list = list;
		this.onClickListener = onClickListener;
		
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		convertView = LayoutInflater.from(context).inflate(R.layout.interceptphonelistitem, null);
		
		ViewHolder viewHolder = new ViewHolder();
		
		viewHolder.contactImage = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		viewHolder.name = (TextView) convertView.findViewById(R.id.contactitem_nick);
		viewHolder.number = (TextView) convertView.findViewById(R.id.number);
		viewHolder.date = (TextView) convertView.findViewById(R.id.date);
		viewHolder.area = (TextView) convertView.findViewById(R.id.area);
		viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
		
		CallLogInfo calllog = list.get(position);
		
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null, null, null);
		if(phones.moveToFirst()){
			do{
				String phoneString = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				if(calllog.getmCaller_number().equals(phoneString)){
					
					String photo_id = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
					if (photo_id != null) {
						Cursor photo = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
								"ContactsContract.Data._ID = " + photo_id, null, null);
						if (photo.moveToNext()) {
							byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
							ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
							Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
							if (contactPhoto == null)
								viewHolder.contactImage.setImageResource(R.drawable.default_contact);
							else
								viewHolder.contactImage.setImageBitmap(contactPhoto);
						}
						photo.close();
					} else {
						viewHolder.contactImage.setImageResource(R.drawable.default_contact);
					}
					
				}
			}while(phones.moveToNext());
		}
		phones.close();
		
		viewHolder.name.setText(calllog.getmCaller_name());
		viewHolder.number.setText(calllog.getmCaller_number());
		viewHolder.date.setText(calllog.getmCall_date());
		viewHolder.area.setText(MainActivity.CheckNumberArea(calllog.getmCaller_number()));
		
		viewHolder.delete.setTag(calllog.getId()+","+calllog.getmCall_type());
		viewHolder.delete.setOnClickListener(onClickListener);
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView contactImage;
		TextView name;
		TextView number;
		TextView date;
		TextView area;
		
		Button delete;
	}

}
