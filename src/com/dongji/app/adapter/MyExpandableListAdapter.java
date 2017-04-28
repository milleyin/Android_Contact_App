package com.dongji.app.adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.R;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Context mContext;
	LayoutInflater inflaters;
	private String[] groups = {
			"People Names", "Friends Names", "Cat Names"
	};
	private String[][] children = {
			{ "1", "2", "4", "5"},
			{ "6", "̷7", "8" },
			{ "9", "10", "11", "12" }			
	};
	
	public MyExpandableListAdapter(Context context) {
		mContext = context;
		inflaters=LayoutInflater.from(mContext);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return children[groupPosition][childPosition];
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		convertView=inflaters.inflate(R.layout.expand_child, null);
		holder.tv_app_name = (TextView) convertView.findViewById(R.id.child_name);
		holder.tv_app_name.setText(getChild(groupPosition, childPosition).toString());
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return children[groupPosition].length;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	@Override
	public int getGroupCount() {
		return groups.length;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ViewHolder holder = new ViewHolder();
		convertView=inflaters.inflate(R.layout.expand_group, null);
		holder.img_icon = (ImageView) convertView.findViewById(R.id.img_tip_icon);
		holder.tv_app_name = (TextView) convertView.findViewById(R.id.group_name);
		holder.img_right_icon = (ImageView)convertView.findViewById(R.id.img_right_icon);
		holder.img_right_icon.setImageResource(R.drawable.up_img);
		if(!isExpanded)
		{
			holder.img_right_icon.setImageResource(R.drawable.down_img);
		}
		 holder.img_icon.setImageResource(R.drawable.added_item_icon);
		 holder.tv_app_name.setText(groups[groupPosition]);
		return convertView;
	}
	class ViewHolder {
		ImageView img_icon;
		TextView tv_app_name;
		ImageView img_right_icon;
		RelativeLayout rl_tool_manage;
	}
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	

}
