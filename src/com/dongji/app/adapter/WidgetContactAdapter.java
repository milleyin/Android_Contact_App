package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.R;
import com.dongji.app.entity.WidgetContact;

public class WidgetContactAdapter extends BaseAdapter {

	Context context;
	List<WidgetContact> list;
	OnClickListener onClickListener;
	
	public WidgetContactAdapter( Context context,List<WidgetContact> list,OnClickListener onClickListener) {

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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.widget_contact_list_item, null);
		
		ViewHolder holder = new ViewHolder();
		
		holder.contactImg = (ImageView) convertView.findViewById(R.id.contact_img);
		holder.contactName = (TextView) convertView.findViewById(R.id.contact_name);
		holder.contactPhone = (TextView) convertView.findViewById(R.id.contact_phone);
		holder.changeContact = (ImageView) convertView.findViewById(R.id.change_widget_contact);
		holder.deleteContact = (ImageView) convertView.findViewById(R.id.delete_widget_contact);
		holder.addNewContact = (LinearLayout) convertView.findViewById(R.id.add_new_contact);
		holder.show_contact_data = (LinearLayout) convertView.findViewById(R.id.show_contact_data);
		
		WidgetContact w = list.get(position);
		
		String pt = String.valueOf(position);
		
		if (w.getNumber() != null) {
		
			byte[] photoicon = w.getPhotoicon();
			if(photoicon != null){
				w.setPhotoicon(photoicon);
				
				ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
				Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
				if (contactPhoto == null)
					holder.contactImg.setImageResource(R.drawable.default_contact);
				else
					holder.contactImg.setImageBitmap(contactPhoto);
			}else{
				holder.contactImg.setImageResource(R.drawable.default_contact);
			}
			
			holder.contactName.setText(w.getName());
			holder.contactPhone.setText(w.getNumber());
			
			holder.addNewContact.setVisibility(View.GONE);
			
			holder.changeContact.setOnClickListener(onClickListener);
			holder.changeContact.setTag(w.getNumber());
			holder.deleteContact.setOnClickListener(onClickListener);
			holder.deleteContact.setTag(pt+","+w.getNumber());
			
		} else {
			
			holder.show_contact_data.setVisibility(View.GONE);
			
			holder.addNewContact.setVisibility(View.VISIBLE);
			holder.addNewContact.setOnClickListener(onClickListener);
			holder.addNewContact.setTag(pt);
			
		}
		return convertView;
	}
	
	class ViewHolder {
		ImageView contactImg;
		TextView contactName;
		TextView contactPhone;
		ImageView changeContact;
		ImageView deleteContact;
		LinearLayout show_contact_data;
		LinearLayout addNewContact;
	}

}
