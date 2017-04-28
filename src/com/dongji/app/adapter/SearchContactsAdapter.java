package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.EnContact;
import com.dongji.app.ui.LetterParser;

public class SearchContactsAdapter extends BaseAdapter implements Filterable {
	
	MainActivity mainActivity;
	
	private LayoutInflater inflater;
	// 保存当前搜索出来的联系人
	private List<ContactBean> contactinfoList ;
	// 保存所有联系人
	private List<ContactBean> oldInfoList;
	// LetterParser提供汉子转换为拼音
	private LetterParser letterParser;
	
	private List<ContactBean> contactall;

	// 名字列表，号码列表
	private List<String> nameList;
	private List<String> phoneList;
	// 将名字转换为数字，比如“王大猫”--->"wdm"--->936
	private List<String> nameToNumList;

	
	View content_layout;
	int original_x;
	int l_r;
	int l_r_dis;
	int target_dis = 60;
	int margin_bottom;
	public View menu;
	
	String number;
	
	private String filterNum;
	ArrayList<ContactBean> result;
	Map<ContactBean,Integer> map;
	
//	ArrayList<String> nick_list;
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);

			final AbsoluteLayout.LayoutParams ablp = new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.FILL_PARENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0, 0);
			
			switch (msg.what) {
			case 0:
				lp.setMargins(0, 0, 0, margin_bottom);

				menu.setLayoutParams(lp);
				menu.postInvalidate();

				break;

			case 1:
				ablp.x = l_r_dis;
			    content_layout.setLayoutParams(ablp);
				content_layout.postInvalidate();

				System.out.println(" update --->");
				break;

			case 2:
				ablp.x = -l_r_dis;
				content_layout.setLayoutParams(ablp);
				content_layout.postInvalidate();
				break;
				
		    case 3:
				
				if(l_r==1)  //打电话
				{
					mainActivity.triggerCall(number);
					content_layout.postDelayed(new Runnable() {
						public void run() {
							ablp.x = 0;
							content_layout.setLayoutParams(ablp);
							content_layout.postInvalidate();
						}
					},1000);
					
				}else{ //发短信
					mainActivity.triggerSms(number);
					
					content_layout.postDelayed(new Runnable() {
						public void run() {
							ablp.x = 0;
							content_layout.setLayoutParams(ablp);
							content_layout.postInvalidate();
						}
					},1000);
				}
				break;
				
		    case 4: //联想功能，刷新
	          contactinfoList = result;
	          
			   if (result.size() > 0) {
				   notifyDataSetChanged();
				} 
				else {
					notifyDataSetInvalidated();
				}
			   
			   System.out.println(" result.size()  --->" + result.size());
			   
				if( result.size() == 0 ){
					
					if( mainActivity.ln_association_tips.getVisibility() == View.GONE)
					{
						mainActivity.ln_association_tips.setVisibility(View.VISIBLE);
						mainActivity.lv_contact_association.setVisibility(View.GONE);
					}
					
				}else{
					
						mainActivity.ln_association_tips.setVisibility(View.GONE);
						mainActivity.lv_contact_association.setVisibility(View.VISIBLE);
						
				}
		    	break;
		    	
			default:
				break;
			}

		};
	};

	public SearchContactsAdapter(MainActivity mainActivity , List<ContactBean> contactinfoList ) {
		this.mainActivity = mainActivity;
		inflater = LayoutInflater.from(mainActivity);
		letterParser = new LetterParser();
		
		this.contactall = contactinfoList;
		
		System.out.println("  MainActivity.EN_CONTACTS.size()  ----- > "+MainActivity.EN_CONTACTS.size());
		
		//过滤加密联系人
		if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
		{
			List<ContactBean> cc = new ArrayList<ContactBean>();
			
			for(ContactBean cb:contactinfoList)
			{
				boolean isMath = false;
			
					for(EnContact ec:MainActivity.EN_CONTACTS)
					{
//							System.out.println(" 加密的id:  " +ec.getContactId() + "   联系人的id ：  " + String.valueOf(cb.getContact_id()) );
						if(ec.getContactId().equals(String.valueOf(cb.getContact_id())))
						{
							isMath = true;
							break;
						}
					}
					
					if(!isMath)
					{
						cc.add(cb);
					}
				}
			this.contactinfoList = cc;
			
		}else{
			this.contactinfoList = contactinfoList;
		}
		
//		this.contactinfoList = contactinfoList;
		oldInfoList = this.contactinfoList;
		initSomeList();
		
		target_dis = mainActivity.getResources().getDimensionPixelSize(R.dimen.target_dis);
	}

	private void initSomeList() {
		
		nameList = new ArrayList<String>();
		
		phoneList = new ArrayList<String>();
		
		nameToNumList = new ArrayList<String>();
		
		for (ContactBean map : contactinfoList) {
			
			if (map.getNick() != null)
				nameList.add(map.getNick());
			else
				nameList.add(map.getNumber());
			
			phoneList.add(map.getNumber());
		}
		
		initNameToNumList();
	}

	private void initNameToNumList() {
		if (nameToNumList != null) {
			nameToNumList.clear();
		}
		for (String name : nameList) {
			String num = getNameNum(name);
			if (null != num) {
				nameToNumList.add(num);
			} else {
				nameToNumList.add(name);
			}
		}
	}

	private String getNameNum(String name) {
		
		if (name != null && name.length() != 0) {
			int len = name.length();
			char[] nums = new char[len];
			for (int i = 0; i < len; i++) {
				String tmp = name.substring(i);
				nums[i] = getOneNumFromAlpha(letterParser.getFirstAlpha(tmp));
			}
			return new String(nums);
		}
		return null;
	}

	private char getOneNumFromAlpha(char firstAlpha) {
		switch (firstAlpha) {
		case 'a':
		case 'b':
		case 'c':
			return '2';
		case 'd':
		case 'e':
		case 'f':
			return '3';
		case 'g':
		case 'h':
		case 'i':
			return '4';
		case 'j':
		case 'k':
		case 'l':
			return '5';
		case 'm':
		case 'n':
		case 'o':
			return '6';
		case 'p':
		case 'q':
		case 'r':
		case 's':
			return '7';
		case 't':
		case 'u':
		case 'v':
			return '8';
		case 'w':
		case 'x':
		case 'y':
		case 'z':
			return '9';
		default:
			return '0';
		}
	}

	@Override
	public int getCount() {
		return contactinfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return contactinfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	class ViewHolder {
		ImageView ivAvatar;
		TextView tvNick;
		TextView tvItemNumAddr;
		TextView area;
		
		LinearLayout content_layout;
		LinearLayout main_layout;
		ProgressBar progressBar;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		String name = "";
		String phone = "";
		String formattedNumber = "";
		String contactId = null;
		
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.contactitems, null);
			holder = new ViewHolder();
//			holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
			holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
			holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
			holder.tvItemNumAddr = (TextView) convertView.findViewById(R.id.number);
			holder.area = (TextView) convertView.findViewById(R.id.area);
//			holder.othernumber_tips = (Button) convertView.findViewById(R.id.othernumber_tips);
//			holder.othernumberlayout = (LinearLayout)convertView.findViewById(R.id.othernumberlayout);
			holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
			
//			holder.menu_call = (Button)convertView.findViewById(R.id.menu_call);
//			
//			holder.menu_sms_detail = (Button)convertView.findViewById(R.id.menu_sms_detail);
	//
//			holder.menu_contact_detail = (Button)convertView.findViewById(R.id.menu_contact_detail);
//			
//			holder.menu_delete = (Button)convertView.findViewById(R.id.menu_delete);
//			
//			holder.menu_remind = (Button)convertView.findViewById(R.id.menu_remind);
//			
//			holder.menu_add_to = (Button)convertView.findViewById(R.id.menu_add_to);
//			
			holder.content_layout =  (LinearLayout) convertView.findViewById(R.id.content_layout);
			holder.main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
//			
//			holder.menu = (LinearLayout)convertView.findViewById(R.id.menu);
			convertView.setTag(holder);
			} else {
			holder = (ViewHolder) convertView.getTag();
			}
		
		final ContactBean map = contactinfoList.get(position);
		
		holder.progressBar.setVisibility(View.GONE);
		
		final LinearLayout content_layout = holder.content_layout;
		LinearLayout main_layout = holder.main_layout;
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						original_x = (int) event.getX();
						break;

					case MotionEvent.ACTION_UP:
						int destain = (int) event.getX() - original_x;

						if (destain > 60) // 向右滑
						{
							l_r = 1;
							scrollLeftOrRight(content_layout);
							number = map.getNumber();
						} else if (destain < -60) // 向左滑
						{
							l_r = 2;
							scrollLeftOrRight(content_layout);
							number = map.getNumber();
						} else { // 点击事件,回调,打电话
							mainActivity.triggetAssocsionCall(map.getNumber());
						}

						break;

					default:
						break;
					}

					return true;
				}
			});
		
		name = map.getNick();
		phone = map.getNumber();
		
		contactId = map.getContact_id().toString();
		holder.tvNick.setText(name);
		
//		System.out.println("nick list size ---- > " + nick_list.size());
//		
//		boolean isSetColor = false;
//		if (nick_list.size() > 0 ) {
//			
//			for (int i=0;i<nick_list.size();i++) {
//				
//				if (nick_list.get(i).equals(name)) {
//					
//					isSetColor = true;
//					break;
//					
//				}
//				
//			}
//			
//			if (isSetColor) {
//				holder.tvNick.setText(Html.fromHtml("<font color='#3d8eba'>" + name+ "</font>"));
//			} else {
//				holder.tvNick.setText(name);
//			}
//			
//			
//		} else {
//			holder.tvNick.setText(name);
//		}
		
		
//		System.out.println(" phone  --->" + phone);
		if(phone !=null)
		{
			if (null == filterNum) {
				holder.tvItemNumAddr.setText(phone);
			} else {
				
				StringBuffer sb = new StringBuffer();
				
				for (int p = 0;p < phone.length(); p++) {
					
					boolean b = false;
					
					for(int i =0 ;i < filterNum.length();i++) {
						
						if ((phone.charAt(p)+"").equals((filterNum.charAt(i)+""))) {
							b =  true;
						} 
					}
					
					if(b)
					{
						sb.append("<font color='#3d8eba'>" + (phone.charAt(p)+"")+ "</font>");
					}else{
						sb.append(phone.charAt(p)+"");
					}
				}
				
				holder.tvItemNumAddr.setText(Html.fromHtml(sb.toString()));
			}
		}
	
//		viewHolder.tvItemNumAddr.setText(phone);

		holder.tvItemNumAddr.setTag(contactId);
		
		String photo_id = map.getPhoto_id();
		
		if (photo_id != null) {
			Cursor photo = mainActivity.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
					"ContactsContract.Data._ID = " + photo_id, null, null);
			if (photo.moveToNext()) {
				byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
				ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
				Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
				if (contactPhoto == null)
					holder.ivAvatar.setImageResource(R.drawable.default_contact);
				else
					holder.ivAvatar.setImageBitmap(contactPhoto);
			}
			photo.close();
		} else {
			holder.ivAvatar.setImageResource(R.drawable.default_contact);
		} 
			
		holder.area.setText(MainActivity.CheckNumberArea(phone));

		return convertView;
	}
	
	

	/***
	 * 下面是给ListView添加过滤方法，可以按照数字检索号码，数字检索名字。
	 */
	@Override
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
//				System.out.println("===publishResults===");
//				contactinfoList = (ArrayList<ContactBean>) results.values;
//				
//				if (results.count > 0) {
//					notifyDataSetChanged();
//				} else {
//					notifyDataSetInvalidated();
//				}
//				
//				if( results.count == 0 || constraint.equals("")){
//					mainActivity.ln_association_tips.setVisibility(View.VISIBLE);
//					mainActivity.lv_contact_association.setVisibility(View.GONE);
//				}else{
//					mainActivity.ln_association_tips.setVisibility(View.GONE);
//					mainActivity.lv_contact_association.setVisibility(View.VISIBLE);
//				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence s) {
				
//				System.out.println("s   -------> "+s.toString());
//				
//				filterNum = s.toString();
//				
//				System.out.println("===performFiltering===");
				FilterResults results = new FilterResults();
//				
//				ArrayList<ContactBean> result = new ArrayList<ContactBean>();
//				
//				
//				System.out.println("oldInfoList  size   ----- > "+oldInfoList.size());
//				
//				if (oldInfoList != null && oldInfoList.size() != 0) {
//					
//					if (letterParser.isNumeric(s.toString()) && phoneList != null) {
//						
//						for (int i = 0; i < phoneList.size(); i++) {
//							
//							if (nameToNumList.get(i)!=null && nameToNumList.get(i).contains(s)) {
//								
//								result.add(oldInfoList.get(i));
//								
//							} else if (letterParser.numberMatch(phoneList.get(i), s.toString())) {
//								
//								result.add(oldInfoList.get(i));
//							}
//						}
//					}
//				}
//				
//				System.out.println("result  size  -------> "+result.size());
//				results.values = result;
//				results.count = result.size();
				
				return results;
			}
		};
		return filter;
	}
	
	
	public void resetData(){
		  List<ContactBean> cc = new ArrayList<ContactBean>();
			
		  System.out.println("  MainActivity.EN_CONTACTS.size()  ----- > "+MainActivity.EN_CONTACTS.size());
		  
			//过滤加密联系人
			if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
			{
				for(ContactBean cb:contactall)
				{
					boolean isMath = false;
				
						for(EnContact ec:MainActivity.EN_CONTACTS)
						{
//								System.out.println(" 加密的id:  " +ec.getContactId() + "   联系人的id ：  " + String.valueOf(cb.getContact_id()) );
							if(ec.getContactId().equals(String.valueOf(cb.getContact_id())))
							{
								isMath = true;
								break;
							}
						
//							break;
						}
						
						if(!isMath)
						{
							cc.add(cb);
						}
					}
				this.contactinfoList = cc;
				
			}else{
				this.contactinfoList = contactall;
			}
			
//			this.contactinfoList = contactinfoList;
			oldInfoList = this.contactinfoList;
			initSomeList();
	}
	
	
	public void filter(String s)
	{
		filterNum = s;
		
		System.out.println("===performFiltering===");
		
		result = new ArrayList<ContactBean>();
		
		map = new HashMap<ContactBean,Integer>();
		
		System.out.println("oldInfoList  size   ----- > "+oldInfoList.size());
		
//		if(contactinfoList.size()>0)
//		{
//            if (letterParser.isNumeric(s.toString()) && phoneList != null) {
//				
//				for (int i = 0; i < phoneList.size(); i++) {
//					
//					if (nameToNumList.get(i)!=null && nameToNumList.get(i).contains(s)) {
//						
////						nick_list.add(oldInfoList.get(i).getNick());
//						result.add(oldInfoList.get(i));
//					} else if (letterParser.numberMatch(phoneList.get(i), s.toString())) {
//						result.add(oldInfoList.get(i));
//					}
//					
//					
//					
//				}
//			}
//		}else 
		if (oldInfoList != null && oldInfoList.size() != 0) {
			
			if (letterParser.isNumeric(s.toString()) && phoneList != null) {
				
				for (int i = 0; i < phoneList.size(); i++) {
					
					if (nameToNumList.get(i)!=null && nameToNumList.get(i).contains(s)) {
//						result.add(oldInfoList.get(i));
						
						map.put(oldInfoList.get(i), getContainsCount(nameToNumList.get(i)));
						
//						temp_result.add(oldInfoList.get(i));
//						count.add(getContainsCount(nameToNumList.get(i)));
					} else if (letterParser.numberMatch(phoneList.get(i), s.toString())) {
//						result.add(oldInfoList.get(i));
//						temp_result.add(oldInfoList.get(i));
//						count.add(getContainsCount(phoneList.get(i)));
						map.put(oldInfoList.get(i), getContainsCount(phoneList.get(i)));
					}
				}
			}
		}
		
//		if (temp_result.size() > 0) {
//			
//			for (int i = 0; i < count.size() - 1; i++) {
//				for (int j = 1; j < count.size() - i; j++) {
//					ContactBean a;
//					if ((count.get(j - 1)).compareTo(count.get(j)) > 0) { // 比较两个整数的大小
//
//						a = temp_result.get(j - 1);
//						result.set((j - 1), temp_result.get(j));
//						result.set(j, a);
//					}
//				}
//			}
//		}
		
		Map<ContactBean,Integer> new_map = sortMap(map,"desc");
		
		Iterator<Map.Entry<ContactBean,Integer>> it = new_map.entrySet().iterator();
		while (it.hasNext()) {
		   Map.Entry<ContactBean,Integer> entry = it.next();
		   ContactBean bean = entry.getKey();
		   
		   result.add(bean);
		}

		
		
		handler.sendEmptyMessage(4);
	}
	
	private int getContainsCount(String str){
		
		int num = 0;
		
		for (int f = 0; f < filterNum.length(); f++) {
			
			if ( str !=null) {
				
				for (int n = 0; n < str.length();n++) {
					
//					System.out.println("  name num list ----- > " + (nameToNumList.get(i).charAt(n)+""));
					
					if ((str.charAt(n)+"").equals(filterNum.charAt(f)+"")) {
						++num;
					}
					
				}
				
			}
		}
		
		return num;
		
	}
	
	/**
	   * @param Map<String,float> map 排序的MAP 
	   * @param String sequence 排序方式 desc 、asc
	   * @throws Exception
	   * @return String
	   */
	public static Map sortMap(Map map,String sequence) {
	      
	      Object[] keyArray = (Object[]) map.keySet().toArray();
	      Object[] valueArray = (Object[]) map.values().toArray();
	      int keyArrayLength = keyArray.length;
	      Integer tmp = null;
	      ContactBean bean;
	      /*
	       * Bubble sort 
	       */
	      for (int i = 0; i < keyArrayLength; i++)
	      {
	          for (int j = 0; j < keyArrayLength-i-1; j++)
	          {
	              float value1 = (Integer) valueArray[j];
	              float value2 = (Integer) valueArray[j+1];
	              
	              if(sequence.equals("desc")){
	               //降序
	               if (value2 > value1)
	               {
	                   tmp = (Integer) valueArray[j+1];
	                   valueArray[j+1] = valueArray[j];
	                   valueArray[j] = tmp;

	                   bean = (ContactBean) keyArray[j+1];
	                   keyArray[j+1] = keyArray[j];
	                   keyArray[j] = bean;
	               }
	              }else if("asc".equals(sequence)){
	               if (value2 < value1)
	               {
	                   tmp = (Integer) valueArray[j+1];
	                   valueArray[j+1] = valueArray[j];
	                   valueArray[j] = tmp;

	                   bean = (ContactBean) keyArray[j+1];
	                   keyArray[j+1] = keyArray[j];
	                   keyArray[j] = bean;
	               }
	              }else {
	               if (value2 < value1)
	               {
	                   tmp = (Integer) valueArray[j+1];
	                   valueArray[j+1] = valueArray[j];
	                   valueArray[j] = tmp;

	                   bean = (ContactBean) keyArray[j+1];
	                   keyArray[j+1] = keyArray[j];
	                   keyArray[j] = bean;
	               }
	              }
	          }
	      }

	      Map mapReturn = new LinkedHashMap();
	      for (int i = 0; i < keyArrayLength; i++)
	      {
	          mapReturn.put(keyArray[i], valueArray[i]);
	      }
	      return mapReturn;

	}
	
	private void scrollLeftOrRight(View v) {
		l_r_dis = 0;
		content_layout = v;

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (l_r_dis < target_dis) {
					l_r_dis += 3;
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(l_r);
				}
				
				try {
					Thread.sleep(400);
				} catch (Exception e) {
				}
			
				handler.sendEmptyMessage(3);
			}
		}).start();
	}
	
	public char[] digit2Char(int digit) {
		char[] cs = null;
		switch (digit) {
		case 0:
			cs = new char[] {};
			break;
		case 1:
			break;
		case 2:
			cs = new char[] { 'a', 'b', 'c' };
			break;
		case 3:
			cs = new char[] { 'd', 'e', 'f' };
			break;
		case 4:
			cs = new char[] { 'g', 'h', 'i' };
			break;
		case 5:
			cs = new char[] { 'j', 'k', 'l' };
			break;
		case 6:
			cs = new char[] { 'm', 'n', 'o' };
			break;
		case 7:
			cs = new char[] { 'p', 'q', 'r', 's' };
			break;
		case 8:
			cs = new char[] { 't', 'u', 'v' };
			break;
		case 9:
			cs = new char[] { 'w', 'x', 'y', 'z' };
			break;
		}
		return cs;
	}
	

}