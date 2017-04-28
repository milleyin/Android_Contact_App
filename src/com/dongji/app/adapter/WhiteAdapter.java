package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;

public class WhiteAdapter extends BaseAdapter {

	Context context;
	List<ContactBean> list = new ArrayList<ContactBean>();
	OnClickListener onClickListener;
	
	public WhiteAdapter(Context context,List<ContactBean> list,OnClickListener onClickListener){
		
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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.intercept_blackwhitelistitem, null);
		
		ViewHolder viewHolder = new ViewHolder();
		
		viewHolder.contactitem_avatar_iv = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		viewHolder.contactitem_nick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		viewHolder.number = (TextView) convertView.findViewById(R.id.number);
		viewHolder.area = (TextView) convertView.findViewById(R.id.area);
		viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
		
		ContactBean contactBean = list.get(position);
		
		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Cursor photo = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },"ContactsContract.Data._ID = " + photo_id, null, null);
			if (photo.moveToNext()) {
				byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
				if(photoicon != null){
					ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
					Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
					if (contactPhoto == null)
						viewHolder.contactitem_avatar_iv.setImageResource(R.drawable.default_contact);
					else
						viewHolder.contactitem_avatar_iv.setImageBitmap(contactPhoto);
				}else{
					viewHolder.contactitem_avatar_iv.setImageResource(R.drawable.default_contact);
				}
					
			}
			photo.close();
		}
		
		viewHolder.contactitem_nick.setText(contactBean.getNick());
		viewHolder.number.setText(contactBean.getNumber());
		viewHolder.area.setText(MainActivity.CheckNumberArea(contactBean.getNumber()));
		viewHolder.delete.setTag(contactBean.getNick()+","+2+","+contactBean.getContact_id()+","+contactBean.getNumber());
		viewHolder.delete.setOnClickListener(onClickListener);
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView contactitem_avatar_iv;
		TextView contactitem_nick;
		TextView number;
		TextView area;
		Button delete;
	}
	
}
