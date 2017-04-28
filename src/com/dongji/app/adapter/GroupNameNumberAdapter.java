package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;

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

public class GroupNameNumberAdapter extends BaseAdapter {

	Context context;
	List<String> list = new ArrayList<String>();
	
	public GroupNameNumberAdapter(Context context,ArrayList<String> list){
		
		this.context = context;
		this.list = list;
		
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
		convertView = LayoutInflater.from(context).inflate(R.layout.group_name_number, null);
		ViewHolder viewHolder = new ViewHolder();
		String  nameNumber = list.get(position);
	
		
		String names[]=nameNumber.split(":");
		if(names!=null)
		{
			viewHolder.number = (TextView) convertView.findViewById(R.id.group_number);
			viewHolder.name = (TextView) convertView.findViewById(R.id.group_name);
			
			if(!names[0].equals(" "))
			{
				viewHolder.name.setText(names[0]+" : ");
			}
			
			if(names.length==2)
			viewHolder.number.setText(names[1]);
		}
		
		
		
		return convertView;
	}
	
	class ViewHolder{
		TextView name;
		TextView number;
	}

}
