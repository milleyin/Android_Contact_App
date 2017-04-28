package com.dongji.app.adapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.text.ClipboardManager;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.addressbook.AddContactActivity;
import com.dongji.app.addressbook.NewMessageActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.tool.ExpressionUtil;
import com.dongji.app.tool.TimeTool;

public class ContactHistorySmsDetailAdapter extends BaseAdapter{
	
	private LayoutInflater mInflater;
//	private final ArrayList<MessageInfo> msgs;
	private Context context;
	AddContactActivity addContactLayout;
	
	ArrayList<SmsContent> sList;
	String from_state="2";
	
    List<CheckBox> cbs ;
	
    boolean[] itemStatus = new boolean[20];
	
	boolean isEditMode = false; //是否为多选模式
	
	Dialog popup_text;
	View conView;
//	Button messageCopy;
//	Button messageDelete;
//	Button messageRemind;
//	Button messageForward;
//	Button messageFavorite;
	
	String thread_id ; //当前的短信会话id
	
	public ContactHistorySmsDetailAdapter(Context context,AddContactActivity addContactLayout,ArrayList<SmsContent> sList) {
		this.context = context;
		this.addContactLayout = addContactLayout;
		mInflater = LayoutInflater.from(context);
		this.sList=sList;
		
        itemStatus = new boolean[sList.size()];
		
		cbs = new ArrayList<CheckBox>();
		
		for(int i = 0;i<sList.size();i++)
		{
			cbs.add(null);
		}
		
		if(sList.size()>0)
		{
			thread_id = String.valueOf(sList.get(0).getThread_id());
			
			System.out.println(" ContactHistorySmsDetailAdapter:  thread_id ---> " + thread_id);
		}
		
	}
	
	public void addMessage(SmsContent msg){
		this.sList.add(msg);
		notifyDataSetChanged();
	}
	
	public void addMessages(List<SmsContent> msgList){
		this.sList.addAll(msgList);
		notifyDataSetChanged();
	}
	
	public void clear(){
		this.sList.clear();
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return sList.size();
	}

	@Override
	public SmsContent getItem(int position) {
		return sList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		SmsContent msgInfo = getItem(position);
		msgInfo.setPosition(position); //设置位置
//		if(convertView==null){
			convertView=mInflater.inflate(R.layout.contact_history_sms_list_item, null);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
//		}else{
//			holder=(ViewHolder) convertView.getTag();
//		}
		holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkBoxEdit);
		holder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
		
		
		if(cbs.get(position)!=null)
		{
			cbs.remove(position);
		}
		cbs.add(position, holder.checkBox);
		if(cbs.size()>sList.size())
		{
			cbs.remove(cbs.size()-1);
		}
		
		
		if(isEditMode)
		{
			holder.checkBox.setVisibility(View.VISIBLE);
			
			if (itemStatus[position] == true) {
				holder.checkBox.setChecked(true);
			} else {
				holder.checkBox.setChecked(false);
			}
		}else  {
			holder.checkBox.setVisibility(View.GONE);
		}
		
		holder.setData(msgInfo,convertView,position);
		return convertView;
	}
	
	private class ViewHolder{
		TextView text;
		TextView total_time;
		
		CheckBox checkBox;
		
		LinearLayout messgaeLayout;
		
		public ViewHolder(View convertView){
			text=(TextView) convertView.findViewById(R.id.team_singlechat_id_listiteam_message);
			text.setOnLongClickListener(new MyLongClickListener());
			messgaeLayout=(LinearLayout)convertView.findViewById(R.id.all_line);
			total_time=(TextView)convertView.findViewById(R.id.ago_time);
		}
		
		public void setData(SmsContent msg,View convertView,int position){
			
			messgaeLayout.setVisibility(View.GONE);
			
			text.setTag(msg);
			
			String date = TimeTool.getTimeStrYYMMDDNoToday(msg.getDate());
			String time = TimeTool.getTimeStrhhmm(msg.getDate());
			
			if(msg.getSms_type() == NewMessageActivity.SMS_TYPE_INBOX){	
																			
				text.setBackgroundResource(R.drawable.balloon_l);
				
				((LinearLayout)convertView.findViewById(R.id.l_tv_layout)).setVisibility(View.GONE);
				((TextView)convertView.findViewById(R.id.today_date_r)).setText(date);
				((TextView)convertView.findViewById(R.id.today_time_r)).setText(time);
				
			}else{
				text.setBackgroundResource(R.drawable.balloon_r);
				
				((LinearLayout)convertView.findViewById(R.id.r_tv_layout)).setVisibility(View.GONE);
				((TextView)convertView.findViewById(R.id.today_date_l)).setText(date);
				((TextView)convertView.findViewById(R.id.today_time_l)).setText(time);
			}
			
			if(position!=sList.size()-1)
			   {
				    long cur_time = msg.getDate();
				    
				    SmsContent msgInfo = getItem(position+1);
				    long pre_time = msgInfo.getDate();
				    
				    if(checkDay(cur_time, pre_time))
				    {
				    	messgaeLayout.setVisibility(View.VISIBLE);
				    	total_time.setText(TimeTool.getTimeStrYYMMDDHHMM(cur_time) + " 之前的会话  ");
				    }
			   }
			
			String str = msg.getSms_body();					
			SpannableString spannableString = ExpressionUtil.getExpressionString(context, str);
			
			try {
				text.setText(spannableString);
			} catch (Exception e) {
			}
		}
	}
	
    public void selectALL(boolean isSelectAll) {
		
		for (int i = 0; i < itemStatus.length; i++) {
			itemStatus[i] = isSelectAll;
		}
	}
    
	//获取所有被选中的  通话记录的id
		public long [] getSelectedCallogId()
		{
			int [] positon =  getSelectedItemIndexes();
			
			
			if(positon.length==0)
			{
				return null;
			}
			
			long [] callog_ids = new long [positon.length];
			
			for(int i =0;i<positon.length;i++)
			{
				callog_ids[i] = sList.get(positon[i]).getId();
			}
			
			return callog_ids;
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
			
//			this.notifyDataSetChanged();
			
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
	
	class MyLongClickListener implements OnLongClickListener
	{

		@Override
		public boolean onLongClick(View v) {
			
			SmsContent sms = (SmsContent)v.getTag();
			
			conView=	mInflater.inflate(R.layout.drop_down_message, null);
			Button messageCopy=(Button)conView.findViewById(R.id.message_copy);
			Button messageDelete=(Button)conView.findViewById(R.id.message_delete);
			Button messageForward=(Button)conView.findViewById(R.id.message_forward);
			Button messageRemind=(Button)conView.findViewById(R.id.message_remind);
			Button messageFavorite=(Button)conView.findViewById(R.id.message_favorite);
			
			
			messageCopy.setOnClickListener(new MyClickListener(sms));
			messageDelete.setOnClickListener(new MyClickListener(sms));
			messageForward.setOnClickListener(new MyClickListener(sms));
			messageRemind.setOnClickListener(new MyClickListener(sms));
			messageFavorite.setOnClickListener(new MyClickListener(sms));
			
			popup_text = new Dialog(context,R.style.theme_myDialog);
			popup_text.setContentView(conView);
			popup_text.setCanceledOnTouchOutside(true);
			popup_text.show();
			
//			popup_text.setBackgroundDrawable(new BitmapDrawable());  //按返回键  以及点击  区域外 消失  (神奇的语句)
//			popup_text.showAsDropDown(v);
			return false;
		}
		
	}
	
	class MyClickListener implements OnClickListener
	{

		SmsContent sms;
		
		public MyClickListener(SmsContent sms)
		{
			this.sms = sms;
		}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.message_copy:
                
//				IClipboard clip = IClipboard.Stub.asInterface(context.ServiceManager.getService("clipboard"));
//				
//				clip.getClipboardText().toString();//获得复制的内容
//				
//				clip.setClipboardText(v.getTag());
				
				ClipboardManager cm =(ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
				cm.setText(sms.getSms_body());
				
				Toast.makeText(context, "已复制进粘贴板", Toast.LENGTH_SHORT).show();
				
				break;

			case R.id.message_delete:
				

				final Dialog dialog = new Dialog(context,R.style.theme_myDialog_activity);
				
				dialog.setContentView(R.layout.mydialog);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();
				
				TextView tv_tips = (TextView) dialog.findViewById(R.id.tv_tips);
				Button btn_top_tips_yes = (Button) dialog.findViewById(R.id.btn_top_tips_yes);
				Button btn_top_tips_no = (Button) dialog.findViewById(R.id.btn_top_tips_no);
				
				tv_tips.setText("确定删除选中的短信?");
				
				btn_top_tips_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						int position = sms.getPosition();
						
						int count = context.getContentResolver().delete(Uri.parse("content://sms/conversations/" + sms.getThread_id()),"_id =" + sms.getId(), null);
				        
						System.out.println("  count   --->" + count);
						
				        sList.remove(position);
				        itemStatus = new boolean [sList.size()];
				        notifyDataSetChanged();
						
						addContactLayout.updateSmsNumAfterDelete();
						
						dialog.dismiss();
					}
				});
				
				btn_top_tips_no.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				
				break;
				
			case R.id.message_forward:
				addContactLayout.smsForward(sms.getSms_body());
				break;
				
			case R.id.message_remind:
				addContactLayout.addRemind(sms.getSms_body());
				break;
				
			case R.id.message_favorite:
				
				addContactLayout.addCollectMsg(sms);
				
				break;
				
			default:
				break;
			}
			
			popup_text.dismiss();
		}
	}
    
    //智能返回时间  
//   	 String[] getTimeStrs(long time)
//   	{
//   		 String [] s_t = new String[2];
//   		Calendar car = Calendar.getInstance();
//   		Date date = new Date(time);
//   		car.setTime(date);
//   		int year = car.get(Calendar.YEAR);
//   		int month = car.get(Calendar.MONTH)+1;
//   		int day = car.get(Calendar.DATE);
//   		
//   		Date d = new Date(System.currentTimeMillis());
//   		Calendar car1 = Calendar.getInstance();
//   		car1.setTime(d);
//   		int cur_year = car1.get(Calendar.YEAR);
//   		int cur_month = car1.get(Calendar.MONTH)+1;
//   		int cur_day = car1.get(Calendar.DATE);
//   		
//   		String s_date  = "";
//   		
////   	    System.out.println(" time " + time );
////   	    System.out.println(" year " + year  + " cur_year " + cur_year);
//   		if(year==cur_year &&  month==cur_month && day==cur_day)
//   		{
//   			s_date = " ";
//   		}
//   		else if(year==cur_year &&  month==cur_month && day+1==cur_day)
//   		{
//   			s_date =  "昨天";
//   		}else  if(year==cur_year){
//   			s_date =  month+"月"+day;
//   		}else{
//   			s_date =  year+"年"+month+"月"+day;
//   		}
//   		
//   		s_t [0] = TimeTool.getTimeStrYYMMDDNoToday(time);
//   		
//   		int hour = car.get(Calendar.HOUR_OF_DAY);
//		int min = car.get(Calendar.MINUTE);
//		
//		String hourStr = String.valueOf(hour);
//		if(Integer.valueOf(hour)<=9)
//		{
//			hourStr="0"+hour;
//		}
//		
//		String minuteStr = String.valueOf(min);
//		if(Integer.valueOf(min)<=9)
//		{
//			minuteStr="0"+min;
//		}
//		
//		s_t [1] = hourStr+":"+minuteStr;
//   		
//   		return s_t;
//   	}
   	 
   	 boolean checkDay(long cur,long pre)
  	 {
  		Calendar car_cur = Calendar.getInstance();
  		Date date = new Date(cur);
  		car_cur.setTime(date);
  		int year = car_cur.get(Calendar.YEAR);
  		int month = car_cur.get(Calendar.MONTH)+1;
  		int day = car_cur.get(Calendar.DATE);
  		
  		
  		Calendar car_pre = Calendar.getInstance();
  		Date date_pre = new Date(pre);
  		car_pre.setTime(date_pre);
  		int year_pre = car_pre.get(Calendar.YEAR);
  		int month_pre = car_pre.get(Calendar.MONTH)+1;
  		int day_pre = car_pre.get(Calendar.DATE);
  		
  		if(year == year_pre && month == month_pre && day == day_pre)
  		{
  			return false;
  		}
  		return true;
  	 }

	public String getThread_id() {
		return thread_id;
	}
   	 
}
