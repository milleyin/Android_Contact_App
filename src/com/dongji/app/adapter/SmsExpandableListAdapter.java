package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.GroupInfo;

public class SmsExpandableListAdapter extends BaseExpandableListAdapter {
	
	private Context mContext;
	LayoutInflater inflaters;
	ArrayList<GroupInfo> groupInfos;

    static	ArrayList<ArrayList<GroupInfo>>childInfo;

    private List<GroupInfo> selected_info = new ArrayList<GroupInfo>(); //被选中的
	
	
	public SmsExpandableListAdapter(Context context,ArrayList<GroupInfo> groupInfos,ArrayList<ArrayList<GroupInfo>>childInfo) {
		mContext = context;
		inflaters=LayoutInflater.from(mContext);
		this.groupInfos=groupInfos;
		this.childInfo = childInfo;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		
//		int group_id = groupInfos.get(groupPosition).getGroup_id();
		final GroupInfo groupInfo = childInfo.get(groupPosition).get(childPosition);
		
	    final	ViewHolder holder = new ViewHolder();
		convertView=inflaters.inflate(R.layout.sms_expand_child, null);
		holder.tv_app_name = (TextView) convertView.findViewById(R.id.child_name);
		holder.tv_app_number = (TextView) convertView.findViewById(R.id.child_number);
		holder.tv_app_ownership = (TextView) convertView.findViewById(R.id.child_ownership);
		
		
		holder.check_box_child = (CheckBox) convertView.findViewById(R.id.sms_child_check);
		holder.check_box_child.setTag("groupPosition"+",childPosition"); 
//		holder.check_box_child.setChecked(childInfo.get(groupPosition).get(childPosition).isIs_child());
		holder.tv_app_name.setText(groupInfo.getPhone_name());
		
		
		String number = groupInfo.getPhone_number();
		
		if(number ==null)
		{
			Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER},
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + groupInfo.getPerson_id(), null, null);
			
			if (phones.moveToNext()) { //第一个号码
					String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
					groupInfo.setPhone_number(phone);
					holder.tv_app_number.setText(phone);
				}else{
					groupInfo.setPhone_number("");
				}
			phones.close();
		}else{
			holder.tv_app_number.setText(number);
		}
		
		
		
		if(groupInfo.getArea()!=null)
		{
			holder.tv_app_ownership.setText(groupInfo.getArea());
		}else{
			String area = MainActivity.CheckNumberArea(groupInfo.getPhone_number());
			holder.tv_app_ownership.setText(area);
			groupInfo.setArea(area);
		}
		
		
		
		holder.check_box_child.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Boolean isChecked = holder.check_box_child.isChecked();
				if (isChecked) {
//					setChildStatesSelect(groupPosition,childPosition);
					romoveSelectedInfo(groupInfo);
					addSelectedInfo(groupInfo);
					groupInfo.setChecked(true);
//					groupInfo.SET
				} else {
//					setChildStatesFalse(groupPosition,childPosition);
					romoveSelectedInfo(groupInfo);
					groupInfo.setChecked(false);
				}
			}
		});
		
		if(groupInfo.isChecked())
		{
			holder.check_box_child.setChecked(true);
		}else{
			holder.check_box_child.setChecked(false);
		}
		
		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return childInfo.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return groupPosition;
	}

	@Override
	public int getGroupCount() {
		return groupInfos.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		
	final	ViewHolder holder = new ViewHolder();
	
		convertView=inflaters.inflate(R.layout.sms_expand_group, null);
		holder.check_box = (CheckBox) convertView.findViewById(R.id.sms_is_check);
		holder.tv_app_name = (TextView) convertView.findViewById(R.id.sms_group_name);
		
		holder.img_right_icon = (ImageView)convertView.findViewById(R.id.sms_img_right_icon);
		holder.img_right_icon.setImageResource(R.drawable.up_img);
		
		if(!isExpanded)
		{
			holder.img_right_icon.setImageResource(R.drawable.down_img);
		}
		
		String gTitle=null;
		if(groupInfos.size()>0)
		{
			gTitle= groupInfos.get(groupPosition).getGroup_name();
		}
		
					 if (gTitle.contains("Group:")) {
			             gTitle = gTitle.substring(gTitle.indexOf("Group:") + 6).trim();
			}
			if (gTitle.contains("Favorite_")) {
			             gTitle = "Favorites";
			}
			if (gTitle.contains("Starred in Android") || gTitle.contains("My Contacts")) {
			             
			}
			
		 holder.tv_app_name.setText(gTitle + " (" + childInfo.get(groupPosition).size() + ")");
		 holder.check_box.setChecked(groupInfos.get(groupPosition).isChecked());
		
		 holder.check_box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
//					setGroupStatesSelect((groupPosition));
					groupInfos.get(groupPosition).setChecked(true);
					for(GroupInfo g:childInfo.get(groupPosition))
					{
						g.setChecked(true);
						romoveSelectedInfo(g);
						addSelectedInfo(g);
					}
					notifyDataSetChanged();
				} else {
					groupInfos.get(groupPosition).setChecked(false);
//					setGroupStatesFalse(groupPosition);
					for(GroupInfo g:childInfo.get(groupPosition))
					{
						g.setChecked(false);
						romoveSelectedInfo(g);
					}
					notifyDataSetChanged();
				}
			}
		});
		 
//		 holder.check_box.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				Boolean isChecked = holder.check_box.isChecked();
//				if (isChecked) {
////					setGroupStatesSelect((groupPosition));
//					for(GroupInfo g:childInfo.get(groupPosition))
//					{
//						g.setChecked(true);
////						romoveSelectedInfo(g);
////						addSelectedInfo(g);
//					}
//					notifyDataSetChanged();
//				} else {
////					setGroupStatesFalse(groupPosition);
//					for(GroupInfo g:childInfo.get(groupPosition))
//					{
//						g.setChecked(false);
//					}
//					notifyDataSetChanged();
//				}
//			}
//		});
		 
//		 holder.check_box.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){
//
//	            @Override
//	            public void onCheckedChanged(CompoundButton buttonView,
//	                    boolean isChecked) {
//	                    if(isChecked)
//	                    {
//	                    	holder.check_box.setChecked(true);
//	                    	changChildStates(groupPosition);
//	                    	
//	                        Toast.makeText(mContext, "你选中了:", Toast.LENGTH_SHORT).show();
//	                    }else
//	                    {
//	                        Toast.makeText(mContext, "你取消了:", Toast.LENGTH_SHORT).show();
//	                    }
//	            }
//	            
//	        });
		 
		return convertView;
	}
	
	void addSelectedInfo(GroupInfo g)
	{
		selected_info.add(g);
	}
	
	void romoveSelectedInfo(GroupInfo g)
	{
		selected_info.remove(g);
	}
	
	 private void setGroupStatesSelect(int groupPosition) {
			for (int i = 0; i < childInfo.get(groupPosition).size(); i++) {
				childInfo.get(groupPosition).get(i).setIs_child(true);
				groupInfos.get(groupPosition).setIs_group(true);
				notifyDataSetChanged();
			}
		}
	 
	 private void setChildStatesSelect(int groupPosition,int childPosition) {
		
				childInfo.get(groupPosition).get(childPosition).setIs_child(true);
				notifyDataSetChanged();
			
		}
	 private void setGroupStatesFalse(int groupPosition) {
			for (int i = 0; i < childInfo.get(groupPosition).size(); i++) {
				childInfo.get(groupPosition).get(i).setIs_child(false);
				groupInfos.get(groupPosition).setIs_group(false);
				notifyDataSetChanged();
			}
		}
	 private void setChildStatesFalse(int groupPosition,int childPosition) {
		    childInfo.get(groupPosition).get(childPosition).setIs_child(false);
			notifyDataSetChanged();
		}
//	 
	 
	 public static ArrayList<ArrayList<GroupInfo>> getChildGroupInfo()
	 {
		 return childInfo;
	 }
	 
	class ViewHolder {
		CheckBox check_box;
		CheckBox check_box_child;
		TextView tv_app_name;
		TextView tv_app_number;
		TextView tv_app_ownership;
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

	public List<GroupInfo> getSelected_info() {
		return selected_info;
	}
	
	public void clear()
	{
		for(GroupInfo g:selected_info)
		{
			g.setChecked(false);
		}
		selected_info.clear();
		notifyDataSetChanged();
	}
}

