package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.List;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.MissedMessageEntity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class MissedMessageAdapter extends BaseAdapter {

	MainActivity mainActivity;
	
	OnClickListener onClickListener;
	
	List<MissedMessageEntity> list = new ArrayList<MissedMessageEntity>();
	
	public MissedMessageAdapter(MainActivity mainActivity,List<MissedMessageEntity> list,OnClickListener onClickListener){
		this.mainActivity = mainActivity;
		this.onClickListener = onClickListener;
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
		ViewHolder viewHolder=null;
		if(convertView==null) {
			convertView = LayoutInflater.from(mainActivity).inflate(R.layout.missedmessageitem, null);
			viewHolder = new ViewHolder();
			viewHolder.from = (TextView) convertView.findViewById(R.id.from);
			viewHolder.time = (TextView) convertView.findViewById(R.id.time);
			viewHolder.delete = (Button) convertView.findViewById(R.id.delete);
			viewHolder.play = (Button) convertView.findViewById(R.id.play);
			viewHolder.call = (Button) convertView.findViewById(R.id.call);
			convertView.setTag(viewHolder);
		}else {
			viewHolder=(ViewHolder)convertView.getTag();
		}
		 
		MissedMessageEntity missedMessage = list.get(position);
		
		viewHolder.from.setText("来自 "+missedMessage.getNumber()+" 的留言");
		viewHolder.time.setText(missedMessage.getDate());
		
		viewHolder.delete.setTag(missedMessage.getId()+","+missedMessage.getDir());
		viewHolder.delete.setOnClickListener(onClickListener);
		viewHolder.play.setTag(missedMessage.getDir());
		viewHolder.play.setOnClickListener(onClickListener);
		viewHolder.call.setTag(missedMessage.getNumber());
		viewHolder.call.setOnClickListener(onClickListener);
		
		return convertView;
	}

	class ViewHolder{
		TextView from;
		TextView time;
		Button delete;
		Button play;
		Button call;
	}
	
}
