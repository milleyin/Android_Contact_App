package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import com.dongji.app.adapter.ContactsAdapter.ViewHolder;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.GroupInfo;
import com.dongji.app.tool.PhoneNumberTool;

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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EditGroupModeAdapter extends BaseAdapter {
	
	MainActivity context;
	
	ArrayList<GroupInfo> groupInfos;
	long groupid;
	
	boolean[] itemStatus ; //对应每个位置的CheckBox状态
	
	boolean isEditMode = false; 
	
	LayoutInflater layoutInflater;
	ContentResolver mContentResolver;
	
	public EditGroupModeAdapter(MainActivity context,ArrayList<GroupInfo> groupInfos,long groupid)
	{
		this.context = context;
		this.groupInfos=groupInfos;
    	this.groupid=groupid;
    	
    	this.layoutInflater = LayoutInflater.from(context);
    	this.mContentResolver = context.getContentResolver();
    	
    	itemStatus = new boolean[groupInfos.size()];
    	
    	int size = groupInfos.size();
    	
    	for(int i = 0 ; i<size ; i++)
    	{
    		GroupInfo g = groupInfos.get(i);
    		
    		if( g.getGroup_id() == groupid )
    		{
    			itemStatus[i] = true;
    		}
    	}
	}

	
	@Override
	public int getCount() {
		return groupInfos.size();
	}
	

	@Override
	public Object getItem(int position) {
		return groupInfos.get(position);
	}

	
	public void toggle(int position){
		if(itemStatus[position] == true){
			itemStatus[position] = false;
		}else{
			itemStatus[position] = true;
		}
		this.notifyDataSetChanged();
	}
	
	
	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	public int[] getSelectedItemIndexes() {

		if (itemStatus == null || itemStatus.length == 0) {
			return new int[0];
		} else {
			int size = itemStatus.length;
			int counter = 0;
			for (int i = 0; i < size; i++) {
				if (itemStatus[i] == true)
					++counter;
			}
			int[] selectedIndexes = new int[counter];
			int index = 0;
			for (int i = 0; i < size; i++) {
				if (itemStatus[i] == true)
					selectedIndexes[index++] = i;
			}
			return selectedIndexes;
		}
	};

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		
//		convertView = LayoutInflater.from(context).inflate(R.layout.item_edit, null);
//		
//		ViewHolder viewHolder = new ViewHolder();
//		viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxEdit);
		
		
//		ViewHolderEdit viewHolder;
//		
//		if (convertView == null) {
//			if (inflater == null) {
//				inflater = (LayoutInflater) mActivity.getSystemService(LAYOUT_INFLATER_SERVICE);
//			}
//			
//			convertView = inflater.inflate(R.layout.item_edit, null);
//			viewHolder = new ViewHolderEdit();
//			viewHolder.artistText = (TextView) convertView
//					.findViewById(R.id.artistTextEdit);
//			viewHolder.titleText = (TextView) convertView
//					.findViewById(R.id.titleTextEdit);
//			viewHolder.durationText = (TextView) convertView
//					.findViewById(R.id.durationTextEdit);
//			viewHolder.checkBox = (CheckBox) convertView
//					.findViewById(R.id.checkBoxEdit);
//			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolderEdit) convertView.getTag();
//		}
//		viewHolder.titleText.setText(mySongCollection[position].title);
//		viewHolder.artistText.setText(mySongCollection[position].artist);
//		viewHolder.durationText.setText(mySongCollection[position].duration);
		
		
//		if(isEditMode)
//		{
//			viewHolder.checkBox.setVisibility(View.VISIBLE);
//			viewHolder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
//			
//			if (itemStatus[position] == true) {
//				viewHolder.checkBox.setChecked(true);
//			} else {
//				viewHolder.checkBox.setChecked(false);
//			}
//		}else  {
//			viewHolder.checkBox.setVisibility(View.GONE);
//		}
		
		final ViewHolder holder;
//		if (convertView == null) {
		convertView = layoutInflater.inflate(R.layout.edit_group_contact_info, null);
		holder = new ViewHolder();
		holder.phoneName=(TextView)convertView.findViewById(R.id.phone_name_show);
		holder.phoneNumber=(TextView)convertView.findViewById(R.id.phone_number_show);
		holder.editCheckBox =(CheckBox)convertView.findViewById(R.id.remove_group_box);
		holder.area = (TextView)convertView.findViewById(R.id.area);
		
		holder.contact_img = (ImageView) convertView.findViewById(R.id.contact_img);
//		convertView.setTag(holder);
//		} else {
//		holder = (ViewHolder) convertView.getTag();
//		}
		
		holder.editCheckBox.setChecked(false);
		
		
		holder.editCheckBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
		
		final GroupInfo groupInfo=groupInfos.get(position);
		holder.phoneName.setText(groupInfo.getPhone_name());
		
		if(groupInfo.getPhone_number()!=null)
		{
			holder.phoneNumber.setText(groupInfo.getPhone_number());
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
	        
	        holder.phoneNumber.setText(groupInfo.getPhone_number());
		}
		
		if(MainActivity.phoneDatabaseUtil!=null && groupInfo.getArea()==null)
		{
			try {
				String newNum = PhoneNumberTool.cleanse(groupInfo.getPhone_number()); 
				if(newNum.length()>7)
				{
					String num = newNum.substring(0, 7);
					String area = MainActivity.phoneDatabaseUtil.fetch(num);
					holder.area.setText(area);
					groupInfo.setArea(area);
				}else{
					holder.area.setText("未知归属地");
					groupInfo.setArea("未知归属地");
				}
			} catch (Exception e) {
				e.printStackTrace();
				holder.area.setText("未知归属地");
				groupInfo.setArea("未知归属地");
			}
		}else{
			holder.area.setText("未知归属地");
			groupInfo.setArea("未知归属地");
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
					holder.contact_img.setImageResource(R.drawable.default_contact);
				}
				else{
					holder.contact_img.setImageBitmap(contactPhoto);
				}
			}
			photo.close();
		} else {
			holder.contact_img.setImageResource(R.drawable.default_contact);
		}
    	  
		if (itemStatus[position] == true) {
			holder.editCheckBox.setChecked(true);
		} else {
			holder.editCheckBox.setChecked(false);
		}
		
		return convertView;
	}
	
	
	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
		
		for(int i =0;i<itemStatus.length;i++) 
		{
			itemStatus[i] = false;
		}
		
		this.notifyDataSetChanged();
	}


	public boolean isEditMode() {
		return isEditMode;
	}

	
	class ViewHolder {
		TextView phoneName;
		TextView phoneNumber;
		
		TextView area;
		
		ImageView contact_img;
		
		CheckBox editCheckBox;
	}

	
	class MyCheckBoxChangedListener implements OnCheckedChangeListener {
		int position;

		MyCheckBoxChangedListener(int position) {
			this.position = position;
		}

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			System.out.println("" + position + "Checked?:" + isChecked);
			if (isChecked)
				itemStatus[position] = true;
			else
				itemStatus[position] = false;
		}
	}

	public boolean[] getItemStatus() {
		return itemStatus;
	}

	public long getGroupid() {
		return groupid;
	}
	
}