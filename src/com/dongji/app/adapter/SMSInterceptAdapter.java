package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.SmsContent;

public class SMSInterceptAdapter extends BaseAdapter {

	Context context;
	
	List<SmsContent> list = new ArrayList<SmsContent>();
	
	OnClickListener onClickListener;
	
	public SMSInterceptAdapter(Context context,List<SmsContent> list,OnClickListener onClickListener){
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
		convertView = LayoutInflater.from(context).inflate(R.layout.interceptsmslistitem, null);
		
		ViewHolder viewHolder = new ViewHolder();
		
		viewHolder.sms_img = (ImageView) convertView.findViewById(R.id.sms_img);
		viewHolder.name = (TextView) convertView.findViewById(R.id.name);
		viewHolder.number = (TextView) convertView.findViewById(R.id.number);
		viewHolder.date = (TextView) convertView.findViewById(R.id.date);
		viewHolder.area = (TextView) convertView.findViewById(R.id.area);
		viewHolder.sms_content = (TextView) convertView.findViewById(R.id.sms_content);
		viewHolder.resume = (Button) convertView.findViewById(R.id.resume);
		viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
		
		SmsContent smsContent = list.get(position);
		
		byte[] photoicon = smsContent.getPhoto();
		
		if ( photoicon != null) {
			
			ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
			Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
			viewHolder.sms_img.setImageBitmap(contactPhoto);
			
		} else {
			
			viewHolder.sms_img.setImageResource(R.drawable.default_contact);
		}
		
		if ( smsContent.getSms_name() != null) {
			
			viewHolder.name.setText(smsContent.getSms_name());
			
		} 
		
		viewHolder.number.setText(smsContent.getSms_number());
		viewHolder.area.setText(MainActivity.CheckNumberArea(smsContent.getSms_number()));
		viewHolder.date.setText(getDistanceTime(smsContent.getSystemTime()));
		viewHolder.sms_content.setText(smsContent.getSms_body());
		
		viewHolder.resume.setTag(smsContent.getId()+","+smsContent.getSubject()+"|"+smsContent.getThread_id());
		viewHolder.delete.setTag(smsContent.getId()+","+smsContent.getSubject()+"|"+smsContent.getThread_id());
		
		viewHolder.resume.setOnClickListener(onClickListener);
		viewHolder.delete.setOnClickListener(onClickListener);
		
		return convertView;
	}
	
	class ViewHolder{
		ImageView sms_img;
		TextView name;
		TextView number;
		TextView area;
		TextView date;
		TextView sms_content;
		
		Button resume;
		Button delete;
	
	}
	
	private String getDistanceTime(long time2) {
		Date now = new Date(); 
       String rs="";     
		SimpleDateFormat format =   new SimpleDateFormat( "MM/dd hh:mm" );
		String d = format.format(time2);
		Date date = null;
		  try {
		   date = format.parse(d);
		  } catch (ParseException e1) {
		   e1.printStackTrace();
		  }  
		  if (now.getDate()-date.getDate()==0) {
		   DateFormat df2 = new SimpleDateFormat("HH:mm");
		      rs = df2.format(time2);
		  } else if (now.getDate()-date.getDate()==1) {

			  DateFormat df2 = new SimpleDateFormat("HH:mm");
			  rs="昨天  ";
		 
		  } else {
			  
		   DateFormat df2 = new SimpleDateFormat("MM/dd");
		   rs=df2.format(time2);
		}
		  return rs;
	}

}
