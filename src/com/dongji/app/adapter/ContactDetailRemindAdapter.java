package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.AddContactActivity;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.RemindsActivity;
import com.dongji.app.entity.RemindBean;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;

/**
 * 
 * 最近通话 列表 对应的Adapter
 * 
 * @author Administrator
 * 
 */
public class ContactDetailRemindAdapter  extends BaseAdapter {
	
    Context context;
    
    String contactId;
    
    AddContactActivity addContactLayout;
    
    RemindsActivity remindLayout;
    
    OnClickListener onClickListener;
    
	private List<RemindBean> list = new ArrayList<RemindBean>();

	public View menu;
	int margin_bottom;
	int original_x;

	List<CheckBox> cbs ;
	
    boolean[] itemStatus = new boolean[20];
	
	boolean isEditMode = false; //是否为多选模式
	
	public static  String SF_NAME = "systemsetting";
	public static String SF_KEY_COLLOG_SORT = "collog_sort";
	SharedPreferences sf ;
	int sort = 1;
	MyDatabaseUtil myDatabaseUtil;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);

			switch (msg.what) {
			case 0:
				lp.setMargins(0, 0, 0, margin_bottom);

				menu.setLayoutParams(lp);
				menu.postInvalidate();

				break;
				
			default:
				break;
			}

		};
	};
	
	public ContactDetailRemindAdapter(Context context,AddContactActivity addContactLayout, String contactId , OnClickListener onClickListener)
	{
		this.context = context;
		this.addContactLayout = addContactLayout;
		this.onClickListener = onClickListener;
		
		this.contactId = contactId;
		
		this.list = getReminds(contactId);
		
		itemStatus = new boolean[list.size()];
		cbs = new ArrayList<CheckBox>();
		
		for(int i = 0;i<list.size();i++)
		{
			cbs.add(null);
		}
	}
	
	public ContactDetailRemindAdapter(Context context,RemindsActivity remindLayout, String contactId , OnClickListener onClickListener)
	{
		
		this.context = context;
		this.remindLayout = remindLayout;
		this.contactId = contactId;
		this.list = getReminds(contactId);
		this.onClickListener = onClickListener;
		
		itemStatus = new boolean[list.size()];
		cbs = new ArrayList<CheckBox>();
		for(int i = 0;i<list.size();i++)
		{
			cbs.add(null);
		}
		
	}
	
	/**
	 * 查询和当前联系人有关的所有提醒
	 * @param contactId
	 * @return
	 */
	private List<RemindBean> getReminds(String contactId)
	{
		List<RemindBean> l = new ArrayList<RemindBean>();
		Cursor c = DButil.getInstance(context).queryRemindByContactId(contactId);
		
		while (c.moveToNext()) {
			
			RemindBean rb = new RemindBean();
			
			rb.setId(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_ID)));
			
			rb.setContent(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTENT)));
			
			rb.setContacts(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_CONTACT)));
			rb.setParticipants(c.getString(c.getColumnIndex(MyDatabaseUtil.REMIND_PARTICIPANT)));
			
			rb.setStart_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_START)));
			rb.setEnd_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REMIND_END)));
			
			rb.setRemind_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TYPE)));
			rb.setRemind_num(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_NUM)));
			rb.setRemind_time(c.getInt(c.getColumnIndex(MyDatabaseUtil.REMIND_TIME)));
			
			rb.setRepeat_type(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_TYPE)));
			rb.setRepeat_fre(c.getInt(c.getColumnIndex(MyDatabaseUtil.REPEAT_FREQ)));
			
			rb.setRepeat_condition(c.getString(c.getColumnIndex(MyDatabaseUtil.REPEAT_CONDITION)));
			
			rb.setRepeat_start_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_START_TIME)));
			rb.setRepeat_end_time(c.getLong(c.getColumnIndex(MyDatabaseUtil.REPEAT_END_TIME)));
			
			
			l.add(rb);
		}
		
		c.close();
		
		return l;
	}
	
	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		final ViewHolder viewHolder = new ViewHolder();
		
		RemindBean rb = list.get(position);
		
		convertView = LayoutInflater.from(context).inflate(R.layout.remind_list_item, null);
		
		viewHolder.tv_event_time = (TextView) convertView.findViewById(R.id.tv_event_time);
		viewHolder.tv_remind_num = (TextView) convertView.findViewById(R.id.tv_remind_num);
		
		viewHolder.tv_repeat_type = (TextView) convertView.findViewById(R.id.tv_repeat_type);
		
		viewHolder.tv_repeat_freq = (TextView) convertView.findViewById(R.id.tv_repeat_freq);
		viewHolder.t_repeat_codition = (TextView) convertView.findViewById(R.id.t_repeat_codition); 
		viewHolder.tv_repeat_time = (TextView) convertView.findViewById(R.id.tv_repeat_time);  
		
		viewHolder.tv_remind_time = (TextView) convertView.findViewById(R.id.tv_remind_time);
		
		viewHolder.tv_partners = (TextView) convertView.findViewById(R.id.tv_partners);
		
		viewHolder.content = (TextView) convertView.findViewById(R.id.content);
		
		viewHolder.menu_edit_remind = (Button)convertView.findViewById(R.id.menu_edit_remind);
		viewHolder.menu_edit_remind.setTag(rb.getId()+","+contactId);
		viewHolder.menu_edit_remind.setOnClickListener(onClickListener);
		
		viewHolder.menu_delete_remind = (Button)convertView.findViewById(R.id.menu_delete_remind);
		viewHolder.menu_delete_remind.setTag(rb.getId()+":"+position);
		viewHolder.menu_delete_remind.setOnClickListener(onClickListener);
		
		viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxEdit);
		viewHolder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
		
		//替换更新存储的checkBoxs 索引
		if(cbs.get(position)!=null)
		{
			cbs.remove(position);
		}
		
		cbs.add(position, viewHolder.checkBox);
		
		if(cbs.size()>list.size())
		{
			cbs.remove(cbs.size()-1);
		}
		
		if(isEditMode)
		{
			viewHolder.checkBox.setVisibility(View.VISIBLE);
			
			if (itemStatus[position] == true) {
				viewHolder.checkBox.setChecked(true);
			} else {
				viewHolder.checkBox.setChecked(false);
			}
		}else {
			viewHolder.checkBox.setVisibility(View.GONE);
		}
				
		
		LinearLayout main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_UP:
						if(!isEditMode)
						{
							popUpMenu(v);
						}
						break;

					default:
						break;
						
					}
					return false;
				}
		});
		
		
		viewHolder.tv_event_time.setText(TimeTool.getTimeStrYYMMDDHHMM(rb.getStart_time())+"~" + TimeTool.getTimeStrYYMMDDHHMM(rb.getEnd_time()));
		String remind_unti = "";
		
		int remind_type = rb.getRemind_type();
		switch (remind_type) {
		case MyDatabaseUtil.REMIND_TYPE_MIN:
			remind_unti="分钟";
			break;
		case MyDatabaseUtil.REMIND_TYPE_HOUR:
			remind_unti="小时";
			break;
		case MyDatabaseUtil.REMIND_TYPE_DAY:
			remind_unti="天";
			break;
		case MyDatabaseUtil.REMIND_TYPE_WEEK:
			remind_unti="星期";
			break;
			
		default:
			break;
		}
		viewHolder.tv_remind_num.setText("提醒:"+rb.getRemind_num() + " " +remind_unti);
		
		String repeat_unti = "";
		int repeat_type = rb.getRepeat_type();
		
		switch (repeat_type) {
		case MyDatabaseUtil.REPEAT_TYPE_ONE:
			repeat_unti="一次性";
			break;
		case MyDatabaseUtil.REPEAT_TYPE_DAY:
			repeat_unti="天";
			break;
		case MyDatabaseUtil.REPEAT_TYPE_WEEK:
			repeat_unti="周";
			break;
		case MyDatabaseUtil.REPEAT_TYPE_MONTH:
			repeat_unti="月";
			break;
			
		case MyDatabaseUtil.REPEAT_TYPE_YEAR:
			repeat_unti="年";
			break;	
		default:
			break;
		}
		int repeat_freq = rb.getRepeat_fre();
		
		if(repeat_unti.equals("一次性"))
		{
			viewHolder.tv_repeat_type.setText("重复： "+repeat_unti);
			viewHolder.tv_repeat_freq.setText("重复频率: 无");
			viewHolder.tv_repeat_time.setVisibility(View.GONE);
		}else{
			viewHolder.tv_repeat_type.setText("重复： 每"+repeat_unti);
			viewHolder.tv_repeat_freq.setText("重复频率: "+repeat_freq+repeat_unti );
			viewHolder.tv_repeat_time.setText("重复开始时间:" + TimeTool.getTimeStrYYMMDD(rb.getRepeat_start_time())+"  重复结束时间:" + TimeTool.getTimeStrYYMMDD(rb.getRepeat_end_time()));
		}
	
		
		String repeat_condition = rb.getRepeat_condition();
		
		if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_WEEK)
		{
			StringBuffer sb = new StringBuffer();
			
			if(repeat_condition.contains("1"))
			{
				sb.append("周一 ");
			}
			
			if(repeat_condition.contains("2"))
			{
				sb.append("周二 ");
			}
			
			if(repeat_condition.contains("3"))
			{
				sb.append("周三 ");
			}
			
			if(repeat_condition.contains("4"))
			{
				sb.append("周四 ");
			}
			
			if(repeat_condition.contains("5"))
			{
				sb.append("周五 ");
			}
			
			if(repeat_condition.contains("6"))
			{
				sb.append("周六 ");
			}
			
			if(repeat_condition.contains("7"))
			{
				sb.append("周日 ");
			}
			
			viewHolder.t_repeat_codition.setText("重复时间: "+sb.toString());
		}else if(repeat_type == MyDatabaseUtil.REPEAT_TYPE_MONTH){
			if(repeat_condition.equals("1")){
				Calendar car = Calendar.getInstance();
				Date date = new Date(rb.getStart_time());
				car.setTime(date);
				int day = car.get(Calendar.DATE);
				viewHolder.t_repeat_codition.setText("重复时间: 每月 的 第"+day+"天");
			}else{
				Calendar car = Calendar.getInstance();
				Date date = new Date(rb.getStart_time());
				car.setTime(date);
		        int week_of_month = car.get(Calendar.WEEK_OF_MONTH);
		        int day_of_week = car.get(Calendar.DAY_OF_WEEK);
		        
		        String dd ="";
		        
		        switch (day_of_week) {
				case 1:
					dd="日";
					break;
                case 2:
                	dd="一";
					break;
					
                case 3:
                	dd="二";
                    break;
                case 4:
                	dd="三";
                    break;
                case 5:
                	dd="四";
                    break;

                case 6:
                	dd="五";
                    break;
                case 7:
                	dd="六";
                    break;
				default:
					break;
				}
		        viewHolder.t_repeat_codition.setText("重复时间: 每月 的 第"+week_of_month+"个星期的周"+dd);
			}
		}else{
			viewHolder.t_repeat_codition.setVisibility(View.GONE);
		}
		
		viewHolder.tv_remind_time.setText("提醒次数: " + rb.getRemind_time() + "次");
		
		viewHolder.tv_partners.setMovementMethod(LinkMovementMethod.getInstance());  

		int [] p_ids;
		String [] p_ss; //参与人的名字数组
		List<String[]> phone_list = new ArrayList<String[]>();
		String partner_names = ""; //全部参与人
		String partner_str = rb.getParticipants();
		
		if(partner_str!=null && !partner_str.equals("")){
			String [] partner_ss = partner_str.split(";");
			
			p_ids = new int[partner_ss.length];
			p_ss = new String[partner_ss.length];
			
			StringBuffer sb_name = new StringBuffer();
			
			for(int i =0;i<partner_ss.length;i++)
			{
				String [] c_s = partner_ss[i].split(":");
				p_ids[i] = Integer.valueOf(c_s[0].replace("#", ""));
				p_ss[i] = c_s[1];
				sb_name.append(c_s[1]+"   ");
				
				String [] phons = c_s[2].split(","); //电话号码
				phone_list.add(phons);
			}
			partner_names = sb_name.toString();
			
			SpannableString spannableString = new SpannableString("参与人： "+partner_names);
			
			int index = 4;
			for(int j =0;j<partner_ss.length;j++)
			{
				spannableString.setSpan(new MyclickSpan(p_ids[j],p_ss[j],phone_list.get(j)), index, index+p_ss[j].length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				//设置颜色 
//				spannableString.setSpan(new ForegroundColorSpan(Color.RED), index, index+p_ss[j].length()+3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				index=index + p_ss[j].length()+3;
				
				System.out.println("index -->" + index);
			}
			
			viewHolder.tv_partners.setText(spannableString);
		}else{
			viewHolder.tv_partners.setText("参与人： 无");
		}
		
		viewHolder.content.setText(rb.getContent());
		
		return convertView;
}
	
	private void popUpMenu(View view) {
//		System.out.println(" onItemLongClick ");
		if (menu != null && menu != view.findViewById(R.id.menu)) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
		}

		menu = view.findViewById(R.id.menu);
		final int height = menu.getHeight();
		margin_bottom = ((LinearLayout.LayoutParams) menu.getLayoutParams()).bottomMargin;


		new Thread(new Runnable() {

			@Override
			public void run() {

				if (margin_bottom < -5) {
					while (margin_bottom < -5) {
						margin_bottom += 5;
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				} else {

					while (margin_bottom > -height) {
						margin_bottom -= 5;
						try {
							Thread.sleep(10);
						} catch (Exception e) {
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}

			}
		}).start();
	}
	
	
	class ViewHolder {
		TextView tv_event_time; //时间的开始 到 结束
		TextView tv_remind_num; //提醒数值
		
		TextView tv_repeat_type; //重复类型
		TextView tv_repeat_freq; //重复频率
		
		TextView t_repeat_codition;
		TextView tv_repeat_time;  //重复的 开始 和 结束
		
		TextView tv_partners;  //参与人
		TextView content;  //事件内容
		 
		TextView tv_remind_time; //提醒次数
		
		Button menu_edit_remind;
		Button menu_delete_remind;
		
		CheckBox checkBox;
	}
	
	
	
	public void  remove(int position)
	{
		list.remove(position);
		itemStatus = new boolean[list.size()];
		notifyDataSetChanged();
	}
	
	
	//获取所有被选中的  通话记录的id
	public long [] getSelectedId()
	{
		int [] positon =  getSelectedItemIndexes();
		
		
		if(positon.length==0)
		{
			return null;
		}
		
		long [] callog_ids = new long [positon.length];
		
		for(int i =0;i<positon.length;i++)
		{
			callog_ids[i] = list.get(positon[i]).getId();
		}
		
		return callog_ids;
	}
	
	public void selectALL(boolean isSelectAll) {
		
		for (int i = 0; i < itemStatus.length; i++) {
			itemStatus[i] = isSelectAll;
		}
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
	
	public void setEditMode(boolean isEditMode) {
		this.isEditMode = isEditMode;
		
		//重置所有状态
		for(int i =0;i<itemStatus.length;i++) 
		{
			itemStatus[i] = false;
		}
		
		if(isEditMode)
		{
			for(CheckBox cb:cbs)
			{
				if(cb!=null)
				{
					cb.setVisibility(View.VISIBLE);
					cb.setChecked(false);
				}
			}
		}else{
			for(CheckBox cb:cbs)
			{
				if(cb!=null)
				{
					cb.setVisibility(View.GONE);
					cb.setChecked(false);
				}
			}
		}
	}


	public boolean isEditMode() {
		return isEditMode;
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
			addContactLayout.updateDeleteNum(getSelectedItemIndexes().length);
		}
	}
	
	
	class MyclickSpan extends ClickableSpan{

		int contactId;
		String name;
		String[] phone_list;
		
		
		public MyclickSpan(int contactId, String name, String[] phone_list) {
			this.contactId = contactId;
			this.name = name;
			this.phone_list = phone_list;
		}

		 @Override
		    public void updateDrawState(TextPaint ds) {
//		        ds.setColor(ds.linkColor);
		        ds.setUnderlineText(false);
		    }

		@Override
		public void onClick(View widget) {
			
			System.out.println(" name ---> " + name);
			if(remindLayout == null)
				addContactLayout.showPartnerDetail(contactId,name,phone_list);
			else
				remindLayout.showPartnerDetail(0,contactId,name,phone_list);
		} 
	}
}
