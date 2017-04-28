package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import android.R.string;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.GroupInfo;
import com.dongji.app.tool.PhoneNumberTool;

public class GroupInfoAdapter extends BaseAdapter{
	Context c;
	ArrayList<GroupInfo> groupInfos;
	
	ContentResolver mContentResolver;
    public GroupInfoAdapter(Context c,ArrayList<GroupInfo> groupInfos)
    {
    	this.c=c;
    	this.groupInfos=groupInfos;
    	
    	this.mContentResolver = c.getContentResolver(); 
    }
	@Override
	public int getCount() {
		return groupInfos.size();
	}

	@Override
	public Object getItem(int position) {
		return groupInfos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		convertView=LayoutInflater.from(c).inflate(R.layout.show_contact_info, null);
		
		ImageView contact_img = (ImageView)convertView.findViewById(R.id.contact_img);
		
		TextView phoneName=(TextView)convertView.findViewById(R.id.phone_name_show);
		TextView phoneNumber=(TextView)convertView.findViewById(R.id.phone_number_show);
		
		final GroupInfo groupInfo=groupInfos.get(position);
		phoneName.setText(groupInfo.getPhone_name());
		
		if(groupInfo.getPhone_number()!=null)
		{
			phoneNumber.setText(groupInfo.getPhone_number());
		}else{
			
			Cursor phones = mContentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + groupInfo.getPerson_id(), null,
					null);

	        if (phones.moveToNext()) { // 第一个号码
		    String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
		     groupInfo.setPhone_number(phone);
	        }
	        phones.close();
	        
	        phoneNumber.setText(groupInfo.getPhone_number());
		}
	
		
		if(groupInfo.getArea()== null )
		{
			String area = MainActivity.CheckNumberArea(groupInfo.getPhone_number());
			((TextView)convertView.findViewById(R.id.tv_area)).setText(area);
			groupInfo.setArea(area);
			
		}else{
			
			((TextView)convertView.findViewById(R.id.tv_area)).setText(groupInfo.getArea());
		}
			
		
		//头像
				String photo_id = groupInfo.getPhoto_id();
				if (photo_id != null) {
					Cursor photo = mContentResolver.query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
							"ContactsContract.Data._ID = " + photo_id, null, null);
					if (photo.moveToNext()) {
						byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
						ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
						Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
						if (contactPhoto == null){
							contact_img.setImageResource(R.drawable.default_contact);
						}
						else{
							contact_img.setImageBitmap(contactPhoto);
						}
					}
					photo.close();
				} else {
					contact_img.setImageResource(R.drawable.default_contact);
				}
		
		return convertView;
	}
	

}
