package com.dongji.app.adapter;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dongji.app.addressbook.NewMessageActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.SystemSettingActivity;
import com.dongji.app.entity.MmsContent;
import com.dongji.app.entity.MmsSmsContent;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.tool.ExpressionUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;
import com.google.android.mms.pdu.MultimediaMessagePdu;

public class MessageDetailChatAdapter extends BaseAdapter{
	
	boolean isMultiConversation ; //是否为群发会话
	
	private ContentResolver mContentResolver;
	
	private LayoutInflater mInflater;
	private Context context;
	private OnClickListener mClickListener;
	private OnLongClickListener mLongClickListener;
	List<MmsSmsContent> sList;
	
	private final Uri CONTENT_URI_PART = Uri.parse("content://mms/part"); //彩信附件表

	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			 
  	     SharedPreferences sf = context.getSharedPreferences(SystemSettingActivity.SF_NAME, 0);
		    	
		  int mode = sf.getInt(SystemSettingActivity.SF_KEY_SMS_REMIND, 1);
		     
			switch (msg.what) {
			case NewMessageActivity.SMS_TYPE_SENT:
				 if(mode == 0)
			       {
//			    	   Toast.makeText(context, "发送成功", Toast.LENGTH_SHORT).show();  
			       }
				break;
				
			case NewMessageActivity.SMS_TYPE_FAILED:
				 if(mode == 0)
			       {
//			    	   Toast.makeText(context, "发送失败", Toast.LENGTH_SHORT).show();  
			       }
			break;

			default:
				break;
			}
			 notifyDataSetChanged();
		};
	};
	
	public MessageDetailChatAdapter(Context context,boolean isMultiConversation,ArrayList<MmsSmsContent> sList,OnClickListener mClickListener,OnLongClickListener mLongClickListener) {
		this.context = context;
		this.isMultiConversation = isMultiConversation;
		
		this.mContentResolver = context.getContentResolver();
		mInflater = LayoutInflater.from(context);
		this.sList=sList;
		
		this.mClickListener = mClickListener;
		this.mLongClickListener = mLongClickListener;
	}
	
	public void addMessage(int position , SmsContent msg){
		try {
			this.sList.add(position,msg);
			notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void setDate(List<MmsSmsContent> msgList)
	{
		this.sList = msgList;
		notifyDataSetChanged();
	}
	
//	
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
	public MmsSmsContent getItem(int position) {
		return sList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		
		MmsSmsContent msgInfo = getItem(position);
		
		if(convertView==null){
			convertView=mInflater.inflate(R.layout.message_detail_item, null);
			holder=new ViewHolder(convertView);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		
		holder.setData(msgInfo,convertView,position);
		
		return convertView;
	}
	
	private class ViewHolder{
		TextView receive_text;
		TextView send_text;
		TextView today_date;
		TextView today_time;
		TextView total_time;
		LinearLayout all_line_Layout;
		TextView receive_time;
		TextView receive_date;
		TextView send_state;
		
		
		RelativeLayout receiveLayout;
		LinearLayout sendLayout;
		
		TextView tv_timing;
		
		ImageView img_timing;
		
		ImageView img_mms_receive;
		ImageView img_mms_send;
		
		
		public ViewHolder(View convertView){
			
			send_state=(TextView) convertView.findViewById(R.id.send_state);
			receive_text=(TextView) convertView.findViewById(R.id.receive_context);
			send_text=(TextView) convertView.findViewById(R.id.send_context);
			
			today_date=(TextView)convertView.findViewById(R.id.message_today_date);
			today_time=(TextView)convertView.findViewById(R.id.message_today_time);
			total_time=(TextView)convertView.findViewById(R.id.ago_time);
			receive_date=(TextView)convertView.findViewById(R.id.receive_message_today_date);
			receive_time=(TextView)convertView.findViewById(R.id.receive_message_today_time);
			all_line_Layout=(LinearLayout)convertView.findViewById(R.id.all_line);
			
			receiveLayout=(RelativeLayout)convertView.findViewById(R.id.receive_layout);
			sendLayout=(LinearLayout)convertView.findViewById(R.id.send_layout);
			
			tv_timing = (TextView)convertView.findViewById(R.id.tv_timing);
			
			img_timing = (ImageView)convertView.findViewById(R.id.img_timing);
			
			img_mms_receive =(ImageView) convertView.findViewById(R.id.img_mms_receive); 
			
			img_mms_send = (ImageView) convertView.findViewById(R.id.img_mms_send); 
		}
		
		public void setData(MmsSmsContent message, View convertView, int position) {

			
			if(message instanceof SmsContent) // ====================== 短信   ==================================
			{
				SmsContent msg = (SmsContent)message;
				String str = msg.getSms_body(); // 消息具体内容

				String date = TimeTool.getTimeStrYYMMDDNoToday(msg.getDate());
				String time = TimeTool.getTimeStrhhmm(msg.getDate());

				if(isMultiConversation) //群发短信 给每条短信加上 联系人的名字
				{
					if(msg.getSms_name()==null)
					{
						String info []  = PhoneNumberTool.getContactInfo(context, msg.getSms_number());
						String  contactName = info[0];
						if(contactName==null)
						{
							str ="(To: " +msg.getSms_number() + ")\n"+str;
							msg.setSms_name(msg.getSms_number());
						}else{
							str ="(To: " +contactName + ")\n"+str +"";
							msg.setSms_name(contactName);
						}
					}else{
						str ="(To: " +msg.getSms_name() + ")\n"+str;
					}
				}
				
				if(str==null)
				{
					str ="";
				}
				
				// 过滤并替换表情
				SpannableString spannableString =  dealWithEmotionAndPhoneNumber(str);

				if (msg.getSms_type() == NewMessageActivity.SMS_TYPE_INBOX) // 接收到的短信
				{
					sendLayout.setVisibility(View.GONE);
					receiveLayout.setVisibility(View.VISIBLE);
					receiveLayout.setOnClickListener(mClickListener);
					receiveLayout.setOnLongClickListener(mLongClickListener);
					receiveLayout.setTag(message);
					
					tv_timing.setVisibility(View.GONE);
					
					img_mms_receive.setVisibility(View.GONE);
					
					if (" ".equals(date)) {
						receive_date.setVisibility(View.GONE);
					} else {
						receive_date.setVisibility(View.VISIBLE);
						receive_date.setText(date);
					}

					receive_time.setText(time);

					receive_text.setMovementMethod(LinkMovementMethod.getInstance());
					receive_text.setText(spannableString);
					receive_text.setOnLongClickListener(mLongClickListener);
					receive_text.setTag(message);

				} else { // 发出去的短信
					
					receiveLayout.setVisibility(View.GONE);
					sendLayout.setVisibility(View.VISIBLE);
					sendLayout.setOnClickListener(mClickListener);
					sendLayout.setOnLongClickListener(mLongClickListener);
					sendLayout.setTag(message);
					
					img_mms_send.setVisibility(View.GONE);
					
					//定时短信
					if(msg.getDate()>System.currentTimeMillis()) 
					{
						tv_timing.setVisibility(View.VISIBLE);
						tv_timing.setText("将于"+TimeTool.getTimeStrYYMMDDHHMM(msg.getDate())+"发送");
						img_timing.setVisibility(View.VISIBLE);
						img_timing.setTag(msg);
						img_timing.setOnClickListener(mClickListener);
						
						send_state.setVisibility(View.GONE);
						today_date.setVisibility(View.GONE);
						today_time.setVisibility(View.GONE);
						
					}else{ //正常发出去的短信
						
						tv_timing.setVisibility(View.GONE);
						img_timing.setVisibility(View.GONE);
						
						if (" ".equals(date)) {
							today_date.setVisibility(View.GONE);
						} else {
							today_date.setVisibility(View.VISIBLE);
							today_date.setText(date);
						}

						send_state.setVisibility(View.VISIBLE);
						
						today_time.setVisibility(View.VISIBLE);
						today_time.setText(time);
					}

					send_text.setMovementMethod(LinkMovementMethod.getInstance());
					send_text.setText(spannableString);
					send_text.setOnLongClickListener(mLongClickListener);
					send_text.setTag(message);
					
					
				}

				
				send_state.setText("");

				int status = msg.getStatus();
				
				switch (status) {
				case NewMessageActivity.SEND_SUCCESS:
					send_state.setText("");
					break;

				case NewMessageActivity.SEND_PENDING:
					send_state.setText("正在发送");
					break;
					
				default:
					break;
				}

				if (msg.getSms_type() == NewMessageActivity.SMS_TYPE_FAILED) {
					send_state.setTextColor(Color.RED);
					send_state.setText("发送失败");
				} else {
					send_state.setTextColor(context.getResources().getColor(R.color.text_color_base));
				}
				
				
			}else{  // ====================== 彩信   ==================================
				
				final MmsContent mms = (MmsContent)message;
				
//				System.out.println("  mms.getDate()  ---> " + mms.getDate());
				
				String date = TimeTool.getTimeStrYYMMDDNoToday(mms.getDate());
				String time = TimeTool.getTimeStrhhmm(mms.getDate());
				
				if (mms.getMsg_box() == NewMessageActivity.SMS_TYPE_INBOX) // 收件箱
				{
					receiveLayout.setVisibility(View.VISIBLE);
					receiveLayout.setOnClickListener(mClickListener);
					receiveLayout.setOnLongClickListener(mLongClickListener);
					receiveLayout.setTag(message);
					
					img_mms_receive.setVisibility(View.VISIBLE);
					sendLayout.setVisibility(View.GONE);
					
					int msg_box = mms.getMsg_box();
					
//					System.out.println(" mms id --->" + mms.getId());
//					System.out.println(" mms msg_box  ---> " + msg_box); 
					
					int mms_st = mms.getSt();
//					System.out.println("彩信下载状态 --->" + mms_state);
					
					if (" ".equals(date)) {
						receive_date.setVisibility(View.GONE);
					} else {
						receive_date.setVisibility(View.VISIBLE);
						receive_date.setText(date);
					}
					
					receive_time.setText(time);
					
					switch (mms_st) {
					case 0: //已完成
						
						if(mms.getPart_pic_id()!=null && mms.getPart_text_id()!=null)
						{
							if(mms.getPart_bmp()!=null && !mms.getPart_bmp().isRecycled())
							{
								img_mms_receive.setImageBitmap(mms.getPart_bmp());
							}else{
								mms.setPart_pic_id(null);
							}
							
							if(mms.getContent()!=null)
							{
								receive_text.setText(dealWithEmotionAndPhoneNumber(mms.getContent()));
							}else{
								receive_text.setText("");
							}
							
						}else{
							
							showMmsPart(img_mms_receive, receive_text, mms);
						}
						
						if(!mms.isHavePicPart())
						{
							img_mms_receive.setVisibility(View.GONE);
						}
						
						break;
						
					case 132:
					case 129: //正在下载
						img_mms_receive.setImageResource(R.drawable.default_contact);
						receive_text.setText("正在下载...\n"+"("+ (mms.getM_size()/1024 + 1) +"KB)");
						break;

					case 135:
					case 130:
						img_mms_receive.setImageResource(R.drawable.default_contact);
						receive_text.setText("点击下载\n有效期: "+TimeTool.getTimeStrYYMMDDhhmmNoTodayInSecond(mms.getExp()));
						img_mms_receive.setTag(mms);
						break;
						
					default:
						break;
					}
					
					receive_text.setOnLongClickListener(mLongClickListener);
					receive_text.setTag(message);
					
				} else { // 发件箱

                    int msg_box = mms.getMsg_box();
//					System.out.println(" msg_box  ---> " + msg_box);
					
					receiveLayout.setVisibility(View.GONE);
					sendLayout.setVisibility(View.VISIBLE);
					sendLayout.setOnClickListener(mClickListener);
					sendLayout.setOnLongClickListener(mLongClickListener);
					sendLayout.setTag(message);
//					
					tv_timing.setVisibility(View.GONE);
					img_timing.setVisibility(View.GONE);
					
					if (" ".equals(date)) {
						today_date.setVisibility(View.GONE);
					} else {
						today_date.setVisibility(View.VISIBLE);
						today_date.setText(date);
					}

					send_state.setVisibility(View.VISIBLE);
					
					today_time.setVisibility(View.VISIBLE);
					today_time.setText(time);
					
					img_mms_send.setVisibility(View.VISIBLE);
					
					if(mms.getPart_pic_id()!=null && mms.getPart_text_id()!=null)
					{
						if(mms.getPart_bmp()!=null && !mms.getPart_bmp().isRecycled())
						{
							img_mms_send.setImageBitmap(mms.getPart_bmp());
						}else{
							mms.setPart_pic_id(null);
						}
						
						if(mms.getContent()!=null)
						{
							send_text.setText(dealWithEmotionAndPhoneNumber(mms.getContent()));
						}else{
							send_text.setText("");
						}
						
					}else{
						showMmsPart(img_mms_send, send_text, mms);
					}
					
					
//					System.out.println(" ========= ******************  ===================");
//					System.out.println(" mms.getMsg_box()  --->" + mms.getMsg_box());
//					System.out.println(" mms.getSt()  --->" + mms.getSt());
//					System.out.println(" mms.getRetr_st()  --->" + mms.getRetr_st());
//					System.out.println(" mms.getResp_st()  --->" + mms.getResp_st());
//					System.out.println(" mms.getD_rpt()  --->" + mms.getD_rpt());
					
					switch (mms.getMsg_box()) {
						
                    case 2: //发送成功
                    	send_state.setTextColor(context.getResources().getColor(R.color.text_color_base));
                    	send_state.setText(""); 
						break;
						
                    case 4:
                    	
                    	if(mms.getResp_st() == 130)
                    	{
                    		send_state.setTextColor(Color.RED);
                        	send_state.setText("发送失败");
                    	}else{
                    		send_state.setTextColor(context.getResources().getColor(R.color.text_color_base));
                            send_state.setText("正在发送");
                    	}
                        break;
             
						
                    case 5:
//                    	send_state.setTextColor(Color.RED);
//                    	send_state.setText("发送失败");
                    	break;
                    	

					default:
						break;
					}
				}
			}
			
			//mark
			// 时间分割线
			if (position != sList.size() - 1) {
				
				long cur_time = message.getDate();
				long pre_time = getItem(position + 1).getDate();
				
				if (checkDay(cur_time, pre_time)) {
					all_line_Layout.setVisibility(View.VISIBLE);
					total_time.setText(TimeTool.getTimeStrYYMMDDHHMM(cur_time) + " 之前的会话  ");
				} else {
					all_line_Layout.setVisibility(View.GONE);
				}
			} else {
				all_line_Layout.setVisibility(View.GONE);
			}
			
		}
	}
	
	//智能返回时间  
  	 String[] getTimeStrs(long time)
  	{
  		String [] s_t = new String[3];
  		Calendar car = Calendar.getInstance();
  		Date date = new Date(time);
  		car.setTime(date);
  		
  		Date d = new Date(System.currentTimeMillis());
  		Calendar car1 = Calendar.getInstance();
  		car1.setTime(d);
  		
  		s_t [0] = TimeTool.getTimeStrYYMMDDNoToday(time);
  		
  		int hour = car.get(Calendar.HOUR_OF_DAY);
		int min = car.get(Calendar.MINUTE);
		
		String hourStr = String.valueOf(hour);
		if(Integer.valueOf(hour)<=9)
		{
			hourStr="0"+hour;
		}
		
		String minuteStr = String.valueOf(min);
		if(Integer.valueOf(min)<=9)
		{
			minuteStr="0"+min;
		}
		
		s_t [1] = hourStr+":"+minuteStr;
		
  		return s_t;
  	}
  	 
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
  	 
  	 public void refresh(final long id,final int type)
  	 {
  		 //mark
//  		 System.out.println("  refresh  --- > id " + id );
//  		 new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				
//				int size = sList.size();
//				
//				System.out.println(" size --->" + size);
//				
//				for(int i = 0 ; i<size ; i++)
//				{
//					SmsContent sms = sList.get(i);
//					
//					if(sms.getId()==id)
//		  			 {
//		  				sms.setStatus(NewMessageLayout.SEND_SUCCESS);
//		  				sms.setSms_type(type);
//				         
//				        Message msg = handler.obtainMessage();
//				        msg.what = type;
//				        
//		  				handler.sendMessage(msg);
//		  				break;
//		  			 }
//				}
//			}
//		}).start();
  	 }
  	 
  	 
  	public void refreshTimming(final long id)
 	 {
 		 System.out.println("  Timming refresh  --- > id " + id );
 		 
 		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				int size = sList.size();
				
				System.out.println(" size --->" + size);
				
				for(int i = 0 ; i<size ; i++)
				{
					if( sList.get(i) instanceof SmsContent)
					{
						SmsContent sms = (SmsContent)sList.get(i);
						
						if(sms.getId()==id)
			  			 {
			  				sms.setStatus(NewMessageActivity.SEND_PENDING);
			  				sms.setSms_type(0);
					        Message msg = handler.obtainMessage();
					        msg.what = -1;
					        
			  				handler.sendMessage(msg);
			  				break;
			  			 }
					}
				}
			}
		}).start();
 	 }
  	
  	public void refreshTimmingTime(final long id,final long date)
	 {
		 System.out.println("  Timming refresh  --- > id " + id );
		 
		 new Thread(new Runnable() {
			
			@Override
			public void run() {
				
				int size = sList.size();
				
				System.out.println(" size --->" + size);
				
				for(int i = 0 ; i<size ; i++)
				{
					if( sList.get(i) instanceof SmsContent)
					{
						SmsContent sms = (SmsContent)sList.get(i);
						
						if(sms.getId()==id)
			  			 {
			  				sms.setSms_type(0);
			  				sms.setDate(date);
					        Message msg = handler.obtainMessage();
					        msg.what = -1;
					        
			  				handler.sendMessage(msg);
			  				break;
			  			 }
					}
				}
			}
		}).start();
	 }
  	 
  	private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN =
        Pattern.compile("[\\+]?[0-9]+([\\-]?[0-9]+)");
	private static final String PHONE_NUMBER_EXPRESSION = "[\\+]?[0-9]+([\\-]?[0-9]+)"; //[\\+]?[0-9.-]+
	private static final int MIN_NUMBER_LENGTH = 5;   // 号码最小长度限制
  	
  	private void invokeString(TextView mTextView, String str) {
  		String[] arr=str.split(PHONE_NUMBER_EXPRESSION);
  		if(arr==null || arr.length==0) {
  			if(str.length()>=MIN_NUMBER_LENGTH && GLOBAL_PHONE_NUMBER_PATTERN.matcher(str).matches()) {
  				SpannableString ss=new SpannableString(str);
  				ss.setSpan(new MyClickableSpan(str), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  				mTextView.setText(ss);
  	  			mTextView.setMovementMethod(LinkMovementMethod.getInstance());
  			}else {
  				mTextView.setText(str);
  			}
  		}else {
  			int start=0;
  			int num=0;
  			int begin=0;
  			int end=0;
  			String temp="";
  			SpannableString ss=new SpannableString(str);
  			for(int i=0;i<arr.length;i++) {
  				num=str.indexOf(arr[i], start);
  				begin=num+arr[i].length();
  				if(i<arr.length-1) {
  					end=str.indexOf(arr[i+1], begin);
  					temp=str.substring(begin, end);
  					start=begin;
  				}else {
  					temp=str.substring(begin, str.length());
  				}
  				if(temp.length()<MIN_NUMBER_LENGTH || !GLOBAL_PHONE_NUMBER_PATTERN.matcher(temp).matches()) {
  					continue;
  				}
  				ss.setSpan(new MyClickableSpan(temp), begin, begin+temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  			}
  			mTextView.setText(ss);
  			mTextView.setMovementMethod(LinkMovementMethod.getInstance());
  		}
  	}
  	
  	
  	private class MyClickableSpan extends ClickableSpan {
  		private String str;
  		
		public MyClickableSpan(String temp) {
			str=temp;
		}

		@Override
		public void onClick(View widget) {
			showCallPhoneDialog(str);
		}
  		
  	}
  	
  	private Dialog mCallPhoneDialog;
  	private TextView mPhoneNumberTextView;
  	private void initDialog() {
  		mCallPhoneDialog=new Dialog(context, R.style.dialog_about);
  		View mContentView=LayoutInflater.from(context).inflate(R.layout.layout_call_phone, null);
  		mPhoneNumberTextView=(TextView)mContentView.findViewById(R.id.phonenumbertextview);
  		Button mOkButton=(Button)mContentView.findViewById(R.id.okbutton);
  		Button mCancelButton=(Button)mContentView.findViewById(R.id.cancelbutton);
  		mOkButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String str=mPhoneNumberTextView.getText().toString();
				mCallPhoneDialog.dismiss();
				Intent intent=new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+str.replace("-", "")));
				context.startActivity(intent);
			}
		});
  		mCancelButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mCallPhoneDialog.dismiss();
			}
		});
  		mCallPhoneDialog.setContentView(mContentView);
  		mCallPhoneDialog.setCanceledOnTouchOutside(true);
  	}
  	
  	private StringBuilder sb=new StringBuilder();
  	private void showCallPhoneDialog(String str) {
  		if(mCallPhoneDialog==null) {
  			initDialog();
  		}
  		sb.delete(0, sb.length());
  		sb.append(str);
  		if(str.length()==11) {
  			sb.insert(3, " ");
  			sb.insert(8, " ");
  		}
  		mPhoneNumberTextView.setText(sb.toString());
  		if(!mCallPhoneDialog.isShowing()) {
  			mCallPhoneDialog.show();
  		}
  	}
  	
  //读取文本附件
  private String getMmsText(String _id){ 
        Uri partURI = Uri.parse("content://mms/part/" + _id ); 
        InputStream is = null; 
        StringBuilder sb = new StringBuilder();
        
        String msg = "无主题";
        
        try { 
            is = mContentResolver.openInputStream(partURI); 
            if(is!=null){
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));
                String temp = reader.readLine();
                while (temp != null) {
                    sb.append(temp);
                    temp=reader.readLine();//在网上看到很多把InputStream转成string的文章，没有这关键的一句，几乎千遍一律的复制粘贴，该处如果不加上这句的话是会内存溢出的
                }
            }
            
            msg = sb.toString();
            
        }catch (IOException e) { 
            e.printStackTrace();  
//            Log.v(TAG, "读取附件异常"+e.getMessage());
        }finally{ 
            if (is != null){ 
                try { 
                    is.close(); 
                }catch (IOException e){
//                    Log.v(TAG, "读取附件异常"+e.getMessage());
                }
            } 
        }
        return msg;
    }

  
  //读取图片附件
  private Bitmap getMmsImage(String _id){ 
      Uri partURI = Uri.parse("content://mms/part/" + _id ); 
      ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
      InputStream is = null; 
      Bitmap bitmap=null;
      try { 
          is = mContentResolver.openInputStream(partURI); 
          
          
          byte[] buffer = new byte[256];  
          int len = -1;
          while ((len = is.read(buffer)) != -1) {
              baos.write(buffer, 0, len);
          }
          
          BitmapFactory.Options options = new BitmapFactory.Options(); 
          options.inJustDecodeBounds = true;
          
         BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length,options);
          
         int sample_size = options.outWidth / (480/2) ;
         
         options.inSampleSize = sample_size ;
         options.inJustDecodeBounds = false;
          
         bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length,options);
         
      }catch (IOException e) { 
          e.printStackTrace();  
      }finally{ 
          if (is != null){ 
              try { 
                  is.close(); 
              }catch (IOException e){
              }
          } 
      }
      return bitmap;
  }
  
  private static boolean copyImage(Context context, String path, Uri dataUri) {
  	InputStream input = null;
      OutputStream output = null;

      try {
      	
      	BitmapFactory.Options ops = new BitmapFactory.Options();
      	ops.inJustDecodeBounds = true;
      	
      	BitmapFactory.decodeFile(path,ops);
      	

      	int sample_size = ops.outWidth / 480 ;
      	 
      	ops.inSampleSize = sample_size ;
      	ops.inJustDecodeBounds = false;
      	
          System.out.println(" ******************  path :" + path  + " sample_size  :" + sample_size );
      	
          output = context.getContentResolver().openOutputStream(dataUri);
          
          Bitmap bitmap = BitmapFactory.decodeFile(path, ops);
          
          if(path.contains("jpg"))
          {
          	 bitmap.compress(CompressFormat.JPEG, 30, output);
          }else if(path.contains("png")){
          	 bitmap.compress(CompressFormat.PNG, 30, output);
          }else {
          	 bitmap.compress(CompressFormat.JPEG, 30, output);
          }
          
          output.flush();  
          output.close();
          
          if(bitmap!=null)
          {
          	bitmap.recycle();
          }
          
          return true;
          
      } catch (FileNotFoundException e) {
          Log.e("", "failed to found file?", e);
      } catch (IOException e) {
          Log.e("", "write failed..", e);
      } finally {
          try {
              if (input != null)
                  input.close();
              if (output != null)
                  output.close();
          } catch (IOException e) {
              Log.e("", "close failed...");
          }
      }
  	return false;
  }
  
  void showMmsPart(ImageView img,TextView tv ,MmsContent mms)
  {
	  //根据彩信ID查询彩信的附件
      String selectionPart = new String("mid="+ mms.getId()); //part表中的mid外键为pdu表中的_id
      Cursor cPart = mContentResolver.query(CONTENT_URI_PART, null,selectionPart,null, null);            
      String bodyStr="";
      String[] coloumns = null; 
      String[] values = null; 
      
      boolean isHavePhoto = false;
      boolean isHaveText = false;
      
      while(cPart.moveToNext()){ 
          coloumns = cPart.getColumnNames(); 
          
          if(values == null) 
              values = new String[coloumns.length]; 
          for(int i=0; i< cPart.getColumnCount(); i++){ 
              values[i] = cPart.getString(i); 
          } 
          
          if( !isHavePhoto && (values[3].equals("image/jpeg") || values[3].equals("image/jpg") || values[3].equals("image/bmp") || values[3].equals("image/png"))){  //判断附件类型
        	  Bitmap bitmap = getMmsImage(values[0]);
        	  
        	  if(bitmap!=null)
        	  {
        		  img.setImageBitmap(bitmap); //该处只会显示一张图片，如果有需求的朋友可以根据自己的需求将ImageView换成Gallery，修改一下方法
                  img.setVisibility(View.VISIBLE);
                  isHavePhoto = true;
                  
                  mms.setPart_bmp(bitmap);
                  mms.setPart_pic_id(values[0]);
        	  }
              
          }else if(values[3].equals("text/plain") && !isHaveText){
              /**该处详细描述一下
              *发现一个版本问题，之前用的2.1版本的多台手机测试通过，结果用1.6的G2报异常
              *经过调试发现，1.6版本part表中根本就没有"text"列，也就是values[13],所以就
              *报错了，好像在1.6版本（我只测过G2，嘿嘿），就算是文本信息也是以一个附件形
              *式存在_date里面也就是values[12]里面，与图片类似，但在2.1里面却不是这样存
              *的，文本信息被存在了"text"这个字段中，且"_date"为null*/
//              if(values[12]!=null){//所以该处需判断一下，如果_date为null，可直接设置内容为"text"
//            	  
//                  bodyStr = getMmsText(values[0]);
//                  isHaveText = true;
//                  mms.setPart_text_id(values[0]);
//                  mms.setPart_text(bodyStr);
////                  
//                  System.out.println("   mms  text  ----> " + bodyStr );
//              }else{
            	  
        	  if(!"".equals(values[13]) && values[13]!=null )
        	  {
                  bodyStr = values[13];
                  isHaveText = true;
                  mms.setPart_text_id(values[0]);
                  mms.setPart_text(bodyStr);
        	  }
        	  
          }
      }
      cPart.close();
      
      mms.setHavePicPart(isHavePhoto);
      mms.setHaveTextPart(isHaveText);
      
      
      String content = "";
      
      //群发会话,查联系人姓名
      checkMultiAndSetContactName(mms);
      
      if(mms.getContact_name()!=null)
      {
    	  content = "(To: " +mms.getContact_name() + ")\n" ;
      }
      
      if(!"无主题".equals(mms.getSubject()) && !"".equals(mms.getSubject()))
      {
    	  content = content + "<主题: "+mms.getSubject()+">\n" ;
    	  mms.setPart_text_id("-1");
      }
      
      content = content + bodyStr ;
      
      mms.setContent(content);
      
      SpannableString spannableString = dealWithEmotionAndPhoneNumber(content);
      tv.setText(spannableString);
      
//      System.out.println(" mms.getContent() ---> " + mms.getContent());
  }
  
  //回收资源
  public void recyle()
  {
	  for(MmsSmsContent msg:sList)
	  {
		  if(msg instanceof MmsContent)
		  {
			  MmsContent mms = (MmsContent)msg;
			  if(mms.getPart_bmp()!=null && !mms.getPart_bmp().isRecycled())
			  {
				  mms.getPart_bmp().recycle();
			  }
		  }
	  }
  }
  
  
  /**
   *  如果为群发彩信  则查询彩信号码   对应的    联系人名字 
   * @param mms
   */
  void checkMultiAndSetContactName(MmsContent mms)
  {
	  
	  if(isMultiConversation) //群发彩信 
		{
		  
		  if(mms.getContact_name()==null)
			{

			    String address = "";  //号码
			    
				Cursor address_cursor = context.getContentResolver().query(Uri.parse("content://mms/"+mms.getId()+"/addr"), new String [] {"address"}, null, null, null);
				while(address_cursor.moveToNext())
				{
					if(!address_cursor.getString(address_cursor.getColumnIndex("address")).equals("insert-address-token"))
					{
						address = address_cursor.getString(address_cursor.getColumnIndex("address"));
						break;
					}
				}
				address_cursor.close();
				
				String  contactName="";
				
				if(address!= null)
				{
					String info []  = PhoneNumberTool.getContactInfo(context, address);
					contactName = info[0];
				}
				
				
				if(contactName==null) //没有联系人信息 ，则设置为号码
				{
					contactName = address;
				}else{
					mms.setAddress(address);
				}
				mms.setContact_name(contactName);
			}
		}
  }
  
  /**
   * 
   * 处理文本中包含的表情符号  和   电话号码
   * 
   * @param str
   * @return
   */
  SpannableString dealWithEmotionAndPhoneNumber(String str)
  {
	    // 过滤并替换表情
		SpannableString spannableString = ExpressionUtil.getExpressionString(context, str);
		
		// 过滤号码 , 可点击
		String temp=spannableString.toString();
  		String[] arr=temp.split(PHONE_NUMBER_EXPRESSION);
  		if(arr==null || arr.length==0) {
  			if(temp.length()>=MIN_NUMBER_LENGTH && GLOBAL_PHONE_NUMBER_PATTERN.matcher(temp).matches()) {
  				spannableString.setSpan(new MyClickableSpan(temp), 0, temp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  			}
  		}else {
  			int start=0;
  			int num=0;
  			int begin=0;
  			int end=0;
  			String temp1="";
  			for(int i=0;i<arr.length;i++) {
  				num=temp.indexOf(arr[i], start);
  				begin=num+arr[i].length();
  				if(i<arr.length-1) {
  					end=temp.indexOf(arr[i+1], begin);
  					temp1=temp.substring(begin, end);
  					start=begin;
  				}else {
  					temp1=temp.substring(begin, temp.length());
  				}
  				if(temp1.length()<MIN_NUMBER_LENGTH || !GLOBAL_PHONE_NUMBER_PATTERN.matcher(temp1).matches()) {
  					continue;
  				}
  				spannableString.setSpan(new MyClickableSpan(temp1), begin, begin+temp1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
  			}
  		}
		
  		return spannableString;
  }
}
