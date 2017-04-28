package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.NewMessageActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.addressbook.ConversationsListLayout;
import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.MmsContent;
import com.dongji.app.entity.MmsSmsContent;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.entity.ConversationBean;
import com.dongji.app.tool.ExpressionUtil;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;

public class ConversationAdapter extends BaseAdapter {

	final String MMS_TYPE = "application/vnd.wap.multipart.related"; //彩信在数据库中  ct_t 列所对应的值
	
	Context context;
	ContentResolver mContentResolver;
	
	ConversationsListLayout smsListLayout;
	
	//会话缓存
	List<ConversationBean> cache_threads = new ArrayList<ConversationBean>(); 
	
	List<ConversationBean> source_list = new ArrayList<ConversationBean>();
	List<ConversationBean> list = new ArrayList<ConversationBean>();
	
//	List<SmsContent> temp_list ;
	OnClickListener onClickListener;

//	List<CheckBox> cbs ;
	
    boolean[] itemStatus ;
	
	boolean isEditMode = false; //是否为多选模式
	
	Uri sms_uri = Uri.parse(ConversationsListLayout.SMS_URI_ALL);
	Uri canonical_addresses_uri = Uri.parse("content://mms-sms/canonical-addresses");
	
	String[]  sms_projection = new String[]{"_id","thread_id","address","person","date","read","status","type","subject","body"};
	
	boolean is2Top = false;
	
	final String[] PROJECTION = {"transport_type",
			 "_id",
			 "thread_id",
			 "address",
			 "body",
			 "read",
			 "type",
			 "status",
			 "locked",
			 "error_code",
			 "sub",
			 "sub_cs",
			 "date",
			 "date_sent",
			 "read",
			 "m_type",
			 "msg_box",
			 "d_rpt",
			 "rr",
			 "ct_t",
			 "st",
			 "exp",
			 "err_type",
			 "locked",
			 "sim_id",
			 "service_center"};
	
	private Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {

			switch (msg.what) {
			case 0:
				itemStatus = new boolean[list.size()];
				
				notifyDataSetChanged();
				break;
				
			case 1:
				
				itemStatus = new boolean[list.size()];
					
				smsListLayout.view.post(new Runnable() {
						
					@Override
					public void run() {
							
						notifyDataSetChanged();
						if(is2Top)
						{
							smsListLayout.lv_sms.setSelection(0);
						}
					}
				});
				
				break;

			default:
				break;
			}
			
		};
	};
	
	public ConversationAdapter(Context context, ConversationsListLayout smsListLayout,
			OnClickListener onClickListener) {
		
		this.context = context;
		this.mContentResolver = context.getContentResolver();
		
		this.smsListLayout = smsListLayout;
	    refresh(false);
		
		this.onClickListener = onClickListener;
		
		
		itemStatus = new boolean[this.list.size()];
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
		return 0;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {

		ConversationBean conversatBean = list.get(position);
		
		ViewHolder holder;
		
		
		if(view==null)
		{
			holder = new ViewHolder();
			view = LayoutInflater.from(context).inflate(R.layout.sms_list_item,null);
			
			holder.readImage = (ImageView) view.findViewById(R.id.read);
			holder.personImage = (ImageView) view.findViewById(R.id.sms_image);
			holder.tvNick = (TextView) view.findViewById(R.id.sms_username);
			holder.number = (TextView) view.findViewById(R.id.sms_num);
			holder.content = (TextView) view.findViewById(R.id.sms_content);
			holder.time = (TextView) view.findViewById(R.id.sms_time);

			holder.menu  = (LinearLayout) view.findViewById(R.id.menu);
			
			holder.menu_call = (Button) view.findViewById(R.id.menu_call);
			holder.menu_sms_detail = (Button) view.findViewById(R.id.menu_sms_detail);
			holder.menu_delete = (Button) view.findViewById(R.id.menu_delete);
			
			holder.checkBox = (CheckBox) view.findViewById(R.id.checkBoxEdit);
			
			holder.img_timing = (ImageView) view.findViewById(R.id.img_timing);
			
			view.setTag(holder);
		}else{
			holder = (ViewHolder)view.getTag();
		}
		
		holder.readImage.setVisibility(View.GONE);
		holder.img_timing.setVisibility(View.GONE);
		
		
		holder.checkBox.setOnCheckedChangeListener(new MyCheckBoxChangedListener(position));
		
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
		
		
		if(conversatBean.getMmsSmsContent() == null)
		{
//			System.out.println(" conversatBean.getMmsSmsContent()  --->   " + conversatBean.getAddress().get(0));
			
//			System.out.println(" Recipient_ids  ---> " + c.getString(recipient_ids_column));
				
//			System.out.println(" smsThreadBean.getSnippet_cs() --->" + smsThreadBean.getSnippet_cs());
//			System.out.println(" Message_count  ---> " + smsThreadBean.getMessage_count() );
//			
//				if(smsThreadBean.getSnippet_cs() == 0) //最新的消息为,短信类型
//				{
//					
//				}else{  //最新的消息为,彩信类型
//					SmsContent smsContent = new SmsContent();
//					smsContent.setSms_body("");
//					smsThreadBean.setSmContent(smsContent);
//				}
			
//			String body =null;
			if(conversatBean.isHaveDraft()) //有草稿
			{
				
			}else{
				
				//查出最新短信
				
				Cursor sms_cur = mContentResolver.query(Uri.parse("content://sms/conversations/"+conversatBean.getThread_id()), null, null, null, " date DESC");
				
				SmsContent smsContent = new SmsContent();
				
				if(sms_cur.moveToNext())
				{
					smsContent.setId(sms_cur.getLong(sms_cur.getColumnIndex("_id")));
					smsContent.setDate(sms_cur.getLong(sms_cur.getColumnIndex("date"))); //短信的时间单位为 ： 毫秒
					smsContent.setSms_number(sms_cur.getString(sms_cur.getColumnIndex("address")));
					smsContent.setSms_body(sms_cur.getString(sms_cur.getColumnIndex("body")));
					smsContent.setSms_type(sms_cur.getInt(sms_cur.getColumnIndex("type")));
					smsContent.setStatus(sms_cur.getInt(sms_cur.getColumnIndex("status")));
					smsContent.setRead(sms_cur.getInt(sms_cur.getColumnIndex("read")));
				}
				
				sms_cur.close();
				
				
				//查出最新的彩信
				Cursor mms_cur = mContentResolver.query(Uri.parse("content://mms/"), null , "thread_id = " + conversatBean.getThread_id(), null, " date DESC");
				
				MmsContent mmsContent = new MmsContent();
				if(mms_cur.moveToNext())
				{
					//彩信的主题
					String subject = mms_cur.getString(mms_cur.getColumnIndex("sub"));
					
					try {
						if(subject!=null)
						{
							subject =  new  String(subject.getBytes("ISO8859_1"),"utf-8");
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} 
					
//					int sbu_cs  = cursor.getInt(cursor.getColumnIndex("sub_cs"));
//					System.out.println(" subject ---> " + subject + " : sbu_cs ---> " + sbu_cs);
					
					if(subject==null)
					{
						mmsContent.setSubject("(无主题)");
					}else{
						mmsContent.setSubject(subject);
					}
					
					mmsContent.setDate(Long.valueOf(mms_cur.getLong(mms_cur.getColumnIndex("date"))+"000")); //彩信的时间单位为 ： 秒
//					mmsContent.setDate(mms_cur.getLong(mms_cur.getColumnIndex("date")));
					mmsContent.setMsg_box(mms_cur.getInt(mms_cur.getColumnIndex("msg_box")));
					mmsContent.setRead(mms_cur.getInt(mms_cur.getColumnIndex("read")));
				}
			
				mms_cur.close();
//				
				
				if(smsContent.getDate() > mmsContent.getDate())
				{
					conversatBean.setMmsSmsContent(smsContent);
				}else{
					conversatBean.setMmsSmsContent(mmsContent);
				}
				
			}
			
		}
		
		//有缓存,且没失效
		if(conversatBean.getContacts_str() != null && !conversatBean.isNeedRequeryContacts())
		{
			holder.tvNick.setText(conversatBean.getContacts_str());
			
			if(conversatBean.getPhoto_id()!=null)
			{
				holder.personImage.setImageBitmap(getPhoto(conversatBean.getPhoto_id()));
			}
			else{
				holder.personImage.setImageResource(R.drawable.default_contact);
			}
			
		}else{
			
			List<String> address_list = conversatBean.getAddress();
			
			int size = address_list.size();
				
			if (size == 1) // 非群发
			{
				String n = address_list.get(0);

				String photo_id = null;
				String display_name = null;

				String[] data = PhoneNumberTool.getContactInfo(context, n);
				if (data != null) {
					display_name = data[0];
					photo_id = data[1];
				}

				if (display_name == null) {
					holder.tvNick.setText(n);
					
					conversatBean.setContacts_str(n);
					conversatBean.setPhoto_id(null);
					holder.personImage.setImageResource(R.drawable.default_contact);
					
				} else {

					holder.tvNick.setText(display_name);
					conversatBean.setContacts_str(display_name);

					if (photo_id == null) {
						holder.personImage.setImageResource(R.drawable.default_contact);
						conversatBean.setPhoto_id(null);
					} else {
						Bitmap photo_bitmap = getPhoto(photo_id);

						if (photo_bitmap != null) {
							holder.personImage.setImageBitmap(photo_bitmap);
							conversatBean.setPhoto_id(photo_id);
						} else {
							holder.personImage.setImageResource(R.drawable.default_contact);
							conversatBean.setPhoto_id(null);
						}
					}
				}
				
				conversatBean.setNeedRequeryContacts(false);

			} else { // 群发

				String names = "";

				for (int i = 0; i < size; i++) {
					String nn = address_list.get(i);
					String date [] = PhoneNumberTool.getContactInfo(context, nn);

					if (date[0] != null) {
						names += date[0] + ",";
					} else {
						names += nn + ",";
					}
				}

				names = names.substring(0, names.length() - 1);

				holder.tvNick.setText(names);
				holder.personImage.setImageResource(R.drawable.default_contact);
				
				conversatBean.setContacts_str(names);
				conversatBean.setPhoto_id(null);
				conversatBean.setNeedRequeryContacts(false);
			}
		}
		
		//分短信 和 彩信
		int type = -1;
		String body  = "";
		boolean isUnRead = false;
		
			if(conversatBean.getMmsSmsContent() instanceof SmsContent )
			{
				SmsContent smsContent = (SmsContent) conversatBean.getMmsSmsContent();
				body = smsContent.getSms_body();
				type = smsContent.getSms_type();
				
				if(smsContent.getDate()<System.currentTimeMillis())
				{
					holder.time.setText(TimeTool.getTimeStrYYMMDDhhmmNoToday(smsContent.getDate()));
					
				}else{ //定时的短信
					holder.time.setText(TimeTool.getTimeGap(smsContent.getDate())+"后");
					holder.img_timing.setVisibility(View.VISIBLE);
				}
				
				if(smsContent.getRead() == 0 && type != NewMessageActivity.SMS_TYPE_DRAFT)
				{
					isUnRead  = true;
				}
				
			}else{
				
				MmsContent mmsContent = (MmsContent) conversatBean.getMmsSmsContent();
				body = mmsContent.getSubject();
				type = mmsContent.getMsg_box();
				
				holder.time.setText(TimeTool.getTimeStrYYMMDDhhmmNoToday(mmsContent.getDate()));
				
				if(mmsContent.getRead() == 0)
				{
					isUnRead  = true;
				}
			}
					
		if ( isUnRead ) { //未读
			
				holder.readImage.setVisibility(View.VISIBLE);
				holder.readImage.setImageResource(R.drawable.unread);
				
		} else if ( type == NewMessageActivity.SMS_TYPE_FAILED ) { //发送失败
			
			holder.readImage.setVisibility(View.VISIBLE);
			holder.readImage.setImageResource(R.drawable.failed);
//			System.out.println(" position: "  +  position + " 发送失败 ");
		}else{ //无
			holder.readImage.setVisibility(View.GONE);
		}
		
		if(body==null || "".equals(body))
		{
			body ="(无主题)";
		}
		
		holder.content.setText(ExpressionUtil.getExpressionString(context,body.toString()));

		int count = conversatBean.getMessage_count();
		
		if (count > 0) {
			holder.number.setText("(" + count + ")");
		} else {
			holder.number.setText("");
		}

		
		if(type == NewMessageActivity.SMS_TYPE_DRAFT)
		{
			String str = holder.number.getText().toString();
			holder.number.setText(str+"草稿");
		}
		
		int size = conversatBean.getAddress().size();
		if(size>1) //群发短信隐藏打电话的按钮
		{
			((View)holder.menu_call.getParent()).setVisibility(View.GONE);
		}else{
			((View)holder.menu_call.getParent()).setVisibility(View.VISIBLE);
		}
		
		holder.menu_call.setTag(conversatBean.getAddress().get(0));
		holder.menu_call.setOnClickListener(onClickListener);
		holder.menu_sms_detail.setTag(String.valueOf(conversatBean.getThread_id()));
		holder.menu_sms_detail.setOnClickListener(onClickListener);
		holder.menu_delete.setTag(String.valueOf(conversatBean.getThread_id()));
		holder.menu_delete.setOnClickListener(onClickListener);
		
//		view.setTag(thread_id+":"+address+":"+smsContent.getSms_name()+":"+smsContent.getId());

		return view;
	}

	
	class ViewHolder {
		ImageView readImage;
		ImageView personImage;
		TextView tvNick;
		TextView number;
		TextView content;
		TextView time;

		LinearLayout menu;
		
		Button menu_call;
		Button menu_sms_detail;
		Button menu_delete;
		
		CheckBox checkBox;
		
		ImageView img_timing;
	}
	
	//获取所有被选中的会话的 Threadid
	public long [] getSelectedSmsThreadId()
	{
		int [] positon =  getSelectedItemIndexes();
		
		
		long [] therad_ids = new long [positon.length];
		
		for(int i =0;i<positon.length;i++)
		{
			therad_ids[i] = list.get(positon[i]).getThread_id();
		}
		
		return therad_ids;
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
		
//		this.notifyDataSetChanged();
		
//		if(isEditMode)
//		{
//			for(CheckBox cb:cbs)
//			{
//				if(cb!=null)
//				{
//					cb.setVisibility(View.VISIBLE);
//					cb.setChecked(false);
//				}
//			}
//		}else{
//			for(CheckBox cb:cbs)
//			{
//				if(cb!=null)
//				{
//					cb.setVisibility(View.GONE);
//					cb.setChecked(false);
//				}
//			}
//		}
		
		notifyDataSetChanged();
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
			
//			System.out.println("" + position + "Checked?:" + isChecked);
			
			if (isChecked)
				itemStatus[position] = true;
			else
				itemStatus[position] = false;
			
			smsListLayout.updateDeleteNum(getSelectedItemIndexes().length);
		}
	}

	/**
	 * 获取会话列表
	 * @return
	 */
	private List<ConversationBean> queryConversations(){
		
    	List<ConversationBean> list = null;
    	Cursor c = null;
    	
    	long start = System.currentTimeMillis();
    	
    	try{
    		
    		String [] projection = new String [] {"_id" , "date" , "message_count" , "recipient_ids" , "snippet" , "snippet_cs" , "read" ,"type" , "error" , "has_attachment" };
    		
    		SharedPreferences sf = context.getSharedPreferences(ConversationsListLayout.SF_NAME, 0);
    		int sort = sf.getInt(ConversationsListLayout.SF_KEY_SMS_SORT, 1); 
    		
    		String sortOrder = null;
    		if(sort == 0){
    			sortOrder = "message_count desc";  //有些机型和版本，此种排序无效 , 故采用手动排序的方式 ,见下面的代码
    		}else{
    			sortOrder = "date desc";
    		}
    		
    		c = mContentResolver.query(Uri.parse("content://mms-sms/conversations?simple=true"), projection, null , null, sortOrder );
    		
    		list = new ArrayList<ConversationBean>();
    		
    		int id_column = c.getColumnIndex("_id");
    		int date_column = c.getColumnIndex("date");
    		int message_count_column = c.getColumnIndex("message_count");
    		int recipient_ids_column = c.getColumnIndex("recipient_ids");
    		int snippet_column = c.getColumnIndex("snippet"); 
    		int snippet_cs_column = c.getColumnIndex("snippet_cs");
    		int read_column = c.getColumnIndex("read");
    		int type_column = c.getColumnIndex("type");
    		
//    		String [] columns = c.getColumnNames();
//    		
//    		for(String css : columns)
//    		{
//    			System.out.println("  css ---> " + css);
//    		}
    		
    		System.out.println(" 一个有多少个有效会话  --->" +  c.getCount());
    		
    		while(c.moveToNext()){
    			
    			boolean isAvailable = false;
    			boolean isHaveDraft = false;
    			
    			//判断是否为有效会话
                if(c.getInt(message_count_column)>0){ 
    				isAvailable = true;
    			}else{
    			}
                
                ConversationBean conversationBean = new ConversationBean();
                
              //查询是否有草稿
//				Cursor sms_cursor = mContentResolver.query(Uri.parse("content://mms-sms/draft"), null , "thread_id = " + c.getLong(id_column) +" AND type =" + NewMessageLayout.SMS_TYPE_DRAFT, null, "date desc limit 1");
                Cursor sms_cursor = mContentResolver.query(Uri.parse("content://sms/draft"), null , "thread_id = " + c.getLong(id_column), null, null );
  				if(sms_cursor.moveToNext())
  				{
  					isAvailable = true;
  					isHaveDraft = true;
  					
  					SmsContent draft_sms = new SmsContent();
  					draft_sms.setId(sms_cursor.getLong(sms_cursor.getColumnIndex("_id")));
  					draft_sms.setSms_body("[草稿] "+sms_cursor.getString(sms_cursor.getColumnIndex("body")));
  					draft_sms.setDate(sms_cursor.getLong(sms_cursor.getColumnIndex("date")));
  					draft_sms.setSms_type(sms_cursor.getInt(sms_cursor.getColumnIndex("type")));
  					conversationBean.setMmsSmsContent(draft_sms);
  				}
  				sms_cursor.close();
                
    			if(isAvailable)
    			{
      				conversationBean.setThread_id(c.getLong(id_column));
      				conversationBean.setDate(c.getLong(date_column));
      				conversationBean.setMessage_count(c.getInt(message_count_column));
      				String recipient_ids = c.getString(recipient_ids_column);
      				conversationBean.setRecipient_ids(recipient_ids);
      				conversationBean.setRecipient_ids_array(recipient_ids.split(" "));
      				
      				conversationBean.setSnippet(c.getString(snippet_column));
      				conversationBean.setSnippet_cs(c.getInt(snippet_cs_column));
      				
      				conversationBean.setRead(c.getInt(read_column));
      				conversationBean.setType(c.getInt(type_column));
      				
      				conversationBean.setHaveDraft(isHaveDraft);
      				
    				list.add(conversationBean);
      				
    				//先尝试从缓存中获取会话号码信息
    				ConversationBean cache_conversation = getNumberFromCache(c.getLong(id_column));
    				
    				if(cache_conversation!=null)
    				{
    					conversationBean.setAddress(cache_conversation.getAddress());
    					conversationBean.setContacts_str(cache_conversation.getContacts_str());
    					conversationBean.setPhoto_id(cache_conversation.getPhoto_id());
    				}else{
    					
    					//查询此会话下的所有号码
    					String sql_str = "";
    					int total = conversationBean.getRecipient_ids_array().length;
    					
    					for (int i = 0; i < total; i++) {
    						sql_str += "_id =? OR ";
    					}

    					sql_str = sql_str.substring(0, sql_str.length() - 4); //去除最后一个 OR

    					Cursor address_cursor = mContentResolver.query(canonical_addresses_uri,new String[] { "address" }, sql_str,conversationBean.getRecipient_ids_array(), null);

    					List<String> address_list = new ArrayList<String>();

    					while (address_cursor.moveToNext()) {
    						String address = address_cursor.getString(address_cursor.getColumnIndex("address"));
    						address_list.add(PhoneNumberTool.cleanse(address));
    					}
    					address_cursor.close();
    					conversationBean.setAddress(address_list);

    				}
					
					if(conversationBean.getMessage_count()==0) //空会话(从来没真正发送过信息)，但存在草稿
					{
						conversationBean.setMessage_count(1);
					}
					
    			}
    		}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}finally{
    		if(c != null)
    			c.close();
    	}
    	
    	cache_threads = list; //缓存
    	
    	SharedPreferences sf = context.getSharedPreferences(ConversationsListLayout.SF_NAME, 0);
		int sort = sf.getInt(ConversationsListLayout.SF_KEY_SMS_SORT, 1); 
		
		if(sort == 0){ //热度排序，手动排序
			
			if(list.size()>0)
			{
				List<ConversationBean> result = new ArrayList<ConversationBean>();
				
				int size = list.size();
				
				//找出最小的
				int min_index = -1;
				
				
				for(int i = 0; i < size ; i++)
				{
					if(i==0)
					{
						min_index = 0;
					}else{
						if(list.get(i).getMessage_count() <= list.get(min_index).getMessage_count())
						{
							min_index = i;
						}
					}
				}
				
				
				System.out.println("  min_index  --->" + min_index);
				
				
				result.add(0,list.get(min_index));
				
				for (int i = 0; i < list.size(); i++) {
					
					if(i!=min_index)
					{
                        ConversationBean source_c = list.get(i);
						
					    int index = -1;
					
						for (int j = 0; j < result.size(); j++) {
							
							ConversationBean re_c = result.get(j);
							
							if (source_c.getMessage_count() > re_c.getMessage_count()) {
								index = j;
								break;
							}
								
							if (j == result.size() - 1) {
								index = j;
							}
						}
						System.out.println("  index  ---> " + index);
						result.add(index, source_c);
					}
				}
				
//				System.out.println(" 热度排序耗时  --->" + (start - System.currentTimeMillis()));
				
				return result;
				
			}else{
				
				return list;
			}
			
		}else{
			
//			System.out.println(" 时间排序耗时  --->" + (start - System.currentTimeMillis()));
			return list;
		}
    }
	
	//从缓存中获取会话的电话号码
	ConversationBean getNumberFromCache(long thread_id)
	{
		for(ConversationBean smsThread:cache_threads)
		{
			if(smsThread.getThread_id() == thread_id)
			{
				return smsThread;
			}
		}
		
		return null;
	}
	
	
	//过滤: 加密的联系人
	public void filter()
	{
		
		List<ConversationBean> temp = new ArrayList<ConversationBean>();
		
		for(ConversationBean smsThread:source_list)
		{
			List<String> addresses = smsThread.getAddress(); //会话内的所有的号码
			boolean isMath = false;
			
			for(String address:addresses)
			{
				isMath = MainActivity.checkIsEnContactByNumber(address);
				
				if(isMath)
				{
					break;
				}
			}
			
			if(!isMath){
				temp.add(smsThread);
			}
			
		}
		
		this.list  = temp;
	}

	public static Object obj = new Object();

	/**
	 * 
	 * @param is2Top 是否置顶显示
	 */
	public void refresh(boolean is2Top)
	{
        new Thread(new Runnable() {
			
			@Override
			public void run() {

				synchronized (obj) {
					
					source_list = queryConversations();
					filter();
						
					handler.sendEmptyMessage(1);
				}
				
//				try {
//					Thread.sleep(500);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
			
			}
		}).start();
	}
	
	public void refreshSort() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {

				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				source_list = queryConversations();
				filter();
					
				handler.sendEmptyMessage(1);
			
			}
		}).start();
	}
	
	
	public void updateAfterChangeEncryption() {
		filter();
		notifyDataSetChanged();
	}
  	
  	
  	private Bitmap getPhoto(String photo_id)
	{
		Bitmap contactPhoto =null;
		Cursor cursor3 = context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { "data15" },"ContactsContract.Data._ID=" + photo_id,
						null, null);
		if (cursor3.moveToFirst()) {
			byte[] photoicon = cursor3.getBlob(0);
			ByteArrayInputStream inputStream = new ByteArrayInputStream(
					photoicon);
			contactPhoto = BitmapFactory.decodeStream(inputStream);
		}
		cursor3.close();
		
		return contactPhoto;
	}
  	
  	
  	public void reQueryContactName()
  	{
  		for(ConversationBean cb:source_list)
  		{
  			cb.setNeedRequeryContacts(true);
  		}
  		notifyDataSetChanged();
  	}
  	
}
