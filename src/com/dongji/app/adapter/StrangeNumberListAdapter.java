package com.dongji.app.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dongji.app.addressbook.R;
import com.dongji.app.entity.StrangeNumberCalllogBean;

public class StrangeNumberListAdapter extends BaseAdapter {

	Context context;
	
	List<StrangeNumberCalllogBean> callogs ;
	
	public StrangeNumberListAdapter(Context context,List<StrangeNumberCalllogBean> callogs)
	{
		this.context = context;
		this.callogs = callogs;
	}
	
	
	@Override
	public int getCount() {
		return callogs.size();
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
		
		convertView = LayoutInflater.from(context).inflate(R.layout.strange_number_list_item, null);
		
		ViewHolder vh = new ViewHolder();
		vh.tv_date = (TextView)convertView.findViewById(R.id.tv_date);
		vh.tv_duration = (TextView)convertView.findViewById(R.id.tv_duration);
		vh.tv_type = (TextView)convertView.findViewById(R.id.tv_type);
		
		StrangeNumberCalllogBean s = callogs.get(position);
		
		vh.tv_date.setText(s.getDate());
		vh.tv_duration.setText(s.getDuration());
		vh.tv_type.setText(s.getType());
		
		return convertView;
	}

	class ViewHolder {
		
		TextView tv_date;
	    TextView tv_duration;
	    TextView tv_type;
	}
}
