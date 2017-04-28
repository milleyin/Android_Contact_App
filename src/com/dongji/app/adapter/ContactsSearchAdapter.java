package com.dongji.app.adapter;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.provider.ContactsContract;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dongji.app.addressbook.ContactLayout;
import com.dongji.app.addressbook.MainActivity;
import com.dongji.app.addressbook.R;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.EnContact;
import com.dongji.app.sqllite.ContactLauncherDBHelper;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.ui.LetterParser;

public class ContactsSearchAdapter extends BaseAdapter  implements SectionIndexer,Filterable {

	Context mContext;
	private String[] mNicks;
	
	ContactLayout contactLayout;
	
	// 保存当前搜索出来的联系人
    private List<ContactBean> contactinfoList;
    
 // 保存所有联系人
 	private List<ContactBean> oldInfoList;
 	// LetterParser提供汉子转换为拼音
 	private LetterParser letterParser;
		
	OnClickListener onClickListener;
	
//	List<ContactBean> contacts = new ArrayList<ContactBean>();
	
	private String filterNum;
	ArrayList<ContactBean> result;
	
	public View menu;
	int margin_bottom;
	int original_x;

	View content_layout;
	int target_dis = 60;
	int l_r_dis;
	int l_r;
	
	int height;
	
	String number ;
	
	LinearLayout ln_otherlayout;
	int otherlayout_margin_bottom;
	int target_height;
	
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

//				System.out.println(" margin_bottom ---> " + margin_bottom);
				if(margin_bottom <= -height)
				{
					menu.setVisibility(View.GONE);
				}
				break;

			case 1:
				ablp.x = l_r_dis;
			    content_layout.setLayoutParams(ablp);
				content_layout.postInvalidate();

//				System.out.println(" update --->");
				break;

			case 2:
				ablp.x = -l_r_dis;
			    content_layout.setLayoutParams(ablp);
				content_layout.postInvalidate();
				break;
				
		    case 3:
				
				if(l_r==1)  //打电话
				{
					contactLayout.triggerCall(number);
					System.out.println("  打电话    ");
					
					content_layout.postDelayed(new Runnable() {
						public void run() {
							ablp.x = 0;
							content_layout.setLayoutParams(ablp);
							content_layout.postInvalidate();
						}
					},1000);
				}else{ //发短信
					contactLayout.triggerSms(number);
					System.out.println(number);
					
					content_layout.postDelayed(new Runnable() {
						public void run() {
							ablp.x = 0;
							content_layout.setLayoutParams(ablp);
							content_layout.postInvalidate();
						}
					},1000);
				}
				
				break;

		    case 4:
		    	lp.setMargins(0, 0, 0, otherlayout_margin_bottom);

				ln_otherlayout.setLayoutParams(lp);
				ln_otherlayout.postInvalidate();
		    	break;
		    	
		    case 5:
		    	lp.setMargins(0, 0, 0, otherlayout_margin_bottom);

				ln_otherlayout.setLayoutParams(lp);
				ln_otherlayout.postInvalidate();
				if(otherlayout_margin_bottom<= -target_height)
				{
					ln_otherlayout.setVisibility(View.GONE);
				}
		    	break;
		    	
		    	
		    case 6: //联想功能，刷新
		          contactinfoList = result;
		          
				   if (result.size() > 0) {
						notifyDataSetChanged();
					} 
					else {
						notifyDataSetInvalidated();
					}
					
					if(result.size() !=0 && contactLayout.lv_search.getVisibility() == View.GONE )
					{
						contactLayout.lv_search.setVisibility(View.VISIBLE);
//						contactLayout.lvcontact.setVisibility(View.GONE);
					}
					
			     break;
			    	
			default:
				break;
			}

		};
	};
	
	public ContactsSearchAdapter(Context context,ContactLayout contactLayout, List<ContactBean> contacts,OnClickListener onClickListener)
	{
		this.mContext = context;
		this.contactLayout = contactLayout;
		
		this.height = context.getResources().getDimensionPixelSize(R.dimen.menu_height);
		
		
		//过滤加密联系人
		if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
		{
			List<ContactBean> cc = new ArrayList<ContactBean>();
			
			for(ContactBean cb:contacts)
			{
				boolean isMath = false;
			
					for(EnContact ec:MainActivity.EN_CONTACTS)
					{
//						System.out.println(" 加密的id:  " +ec.getContactId() + "   联系人的id ：  " + String.valueOf(cb.getContact_id()) );
						if(ec.getContactId().equals(String.valueOf(cb.getContact_id())))
						{
							isMath = true;
							break;
						}
					
//						break;
					}
					
					if(!isMath)
					{
						cc.add(cb);
					}
				}
			this.oldInfoList = cc;
			
		}else{
			this.oldInfoList = contacts;
		}
		
		letterParser = new LetterParser();
		
		this.contactinfoList = this.oldInfoList ;
		this.onClickListener = onClickListener;
		
		target_dis = context.getResources().getDimensionPixelSize(R.dimen.target_dis);
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		
		final ViewHolder holder;
		if (convertView == null) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.contactitems, null);
		holder = new ViewHolder();
		holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
		holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.area = (TextView) convertView.findViewById(R.id.area);
		holder.othernumber_tips = (ImageButton) convertView.findViewById(R.id.othernumber_tips);
		holder.othernumberlayout = (LinearLayout)convertView.findViewById(R.id.othernumberlayout);
		holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		
		holder.menu_call = (Button)convertView.findViewById(R.id.menu_call);
		
		holder.menu_sms_detail = (Button)convertView.findViewById(R.id.menu_sms_detail);

		holder.menu_contact_detail = (Button)convertView.findViewById(R.id.menu_contact_detail);
		
		holder.menu_delete = (Button)convertView.findViewById(R.id.menu_delete);
		
		holder.menu_remind = (Button)convertView.findViewById(R.id.menu_remind);
		
		holder.menu_add_to = (Button)convertView.findViewById(R.id.menu_add_to);
		
		holder. content_layout =  (LinearLayout) convertView.findViewById(R.id.content_layout);
		holder.main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
		
		holder.menu = (LinearLayout)convertView.findViewById(R.id.menu);
		
		holder.img_remind_tips = (ImageView)convertView.findViewById(R.id.img_remind_tips);
		convertView.setTag(holder);
		} else {
		holder = (ViewHolder) convertView.getTag();
		}

		holder.menu.setVisibility(View.GONE);//隐藏
		holder.img_remind_tips.setVisibility(View.GONE);//隐藏
		
//		convertView = LayoutInflater.from(mContext).inflate(R.layout.contactitems, null);

		final ContactBean contactBean = contactinfoList.get(position);
		
		final LinearLayout content_layout = holder.content_layout;
		LinearLayout main_layout = holder.main_layout;
		
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						original_x = (int) event.getX();
//						System.out.println(" original_x --->  " + original_x);
						break;

					case MotionEvent.ACTION_UP:
						int destain = (int) event.getX() - original_x;
//						System.out.println(" destain ---> " + destain);

						if (destain > 60) // 向右滑
						{
							l_r = 1;
							scrollLeftOrRight(content_layout);
//							System.out.println("  //向右滑 ");
							number = contactBean.getNumber();
						} else if (destain < -60) // 向左滑
						{
							l_r = 2;
							scrollLeftOrRight(content_layout);
							number = contactBean.getNumber();
						} else { // 点击事件
							popUpMenu(holder.menu);
						}

						break;

					default:
						break;
					}

					return false;
				}
			});
		
		
		holder.menu_call.setTag(contactBean.getNumber());
		holder.menu_call.setOnClickListener(onClickListener);
		
		holder.menu_sms_detail.setTag(contactBean.getNumber());
		holder.menu_sms_detail.setOnClickListener(onClickListener);

		holder.menu_contact_detail.setTag(String.valueOf(contactBean.getContact_id()));
		holder.menu_contact_detail.setOnClickListener(onClickListener);
		
		//包含两个位置，一个父位置，一个子位置。  如果子位置为-1，说明删除的是父类，则删除整个联系人, 如果子位置不为-1，则说明删除的是具体的某一个号码.
		holder.menu_delete.setTag(position+":-1"+","+String.valueOf(contactBean.getContact_id())+","+contactBean.getNick()); 
		holder.menu_delete.setOnClickListener(onClickListener);
		
		holder.menu_remind.setTag(String.valueOf(contactBean.getContact_id()));
		holder.menu_remind.setOnClickListener(onClickListener);
		
		holder.menu_add_to.setTag(contactBean.getNumber());
		holder.menu_add_to.setOnClickListener(onClickListener);

		
		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Cursor photo = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
					"ContactsContract.Data._ID = " + photo_id, null, null);
			if (photo.moveToNext()) {
				byte[] photoicon = photo.getBlob(photo.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
				ByteArrayInputStream inputStream = new ByteArrayInputStream(photoicon);
				Bitmap contactPhoto = BitmapFactory.decodeStream(inputStream);
				if (contactPhoto == null){
					holder.ivAvatar.setImageResource(R.drawable.default_contact);
				}
				else{
					holder.ivAvatar.setImageBitmap(contactPhoto);
				}
			}
			photo.close();
		} else {
			holder.ivAvatar.setImageResource(R.drawable.default_contact);
		}
		
		String number = contactBean.getNumber();
		if(number ==null)
		{
			Cursor phones = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
					new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER},
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactBean.getContact_id(), null, null);
			
			if (phones.moveToNext()) { //第一个号码
					String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)); 
					contactBean.setNumber(phone);
					//其他的号码
					while (phones.moveToNext()) { 
						String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						contactBean.getNumberlist().add(ph);
					}
				}
			phones.close();
		}
		
		if (null == filterNum) {
			holder.number.setText(contactBean.getNumber());
		} else {
			try {
				holder.number.setText(Html.fromHtml(contactBean.getNumber().replace(filterNum, "<font color='#3d8eba'>" + filterNum+ "</font>")));
			} catch (Exception e) {
//				e.printStackTrace();
				holder.number.setText(contactBean.getNumber());
			}
		}
		
//		holder.number.setText(contactBean.getNumber());
		holder.area.setText(MainActivity.CheckNumberArea(contactBean.getNumber()));
		
		String nick = contactBean.getNick();
		
		ContactLauncherDBHelper dbHelpler = new ContactLauncherDBHelper(mContext);
		SQLiteDatabase db = dbHelpler.getReadableDatabase();
		
		Cursor heat = db.query(dbHelpler.heat_table, new String[]{dbHelpler.HEAT}, dbHelpler.CONTACT_ID+"='"+contactBean.getContact_id()+"'",null, null, null, null);
		
		if(heat.getCount() > 0){
			
			if(heat.moveToFirst()){
				
				int progress = heat.getInt(heat.getColumnIndex(dbHelpler.HEAT));
				holder.progressBar.setProgress(progress);
			}
		}
		heat.close();
		
		db.close();
		
		holder.tvNick.setText(nick);
		int size = contactBean.getNumberlist().size() ;
		
		holder.othernumberlayout.setVisibility(View.GONE);
				
		//是否显示提醒标识
		boolean isShowRemindIcon = false;
		Cursor c = DButil.getInstance(mContext).queryRemindByContactId(contactBean.getContact_id().toString());
		if(c.getCount()>0)
		{
			holder.img_remind_tips.setVisibility(View.VISIBLE);
			isShowRemindIcon = true;
		}
				
		if( size > 0){
			holder.othernumber_tips.setVisibility(View.VISIBLE);
//			holder.othernumber_tips.setText("展");
			holder.othernumberlayout.removeAllViews();
			for(int i =0;i<size ; i++)
			{
				holder.othernumberlayout.addView(getItemView(contactBean, position, i,isShowRemindIcon));
			}
			
			//隐藏
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			lp.bottomMargin = - size * mContext.getResources().getDimensionPixelSize(R.dimen.contact_item_heigth);
			holder.othernumberlayout.setLayoutParams(lp);
			
			
			holder.othernumber_tips.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if(holder.othernumberlayout.getVisibility()==View.GONE)
					{
						showOtherNumberLayout(holder.othernumberlayout); //展开
//						holder.othernumber_tips.setText("缩");
					}else{
						dismissOtherNumberLayout(holder.othernumberlayout); //隐藏
//						holder.othernumber_tips.setText("展");
					}
				}
			});
		}else{
			holder.othernumber_tips.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	private void showOtherNumberLayout(LinearLayout ln)
	{
		ln.setVisibility(View.VISIBLE);
		ln_otherlayout = ln;
		otherlayout_margin_bottom = ((LinearLayout.LayoutParams) ln.getLayoutParams()).bottomMargin;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (otherlayout_margin_bottom < 0) {
					otherlayout_margin_bottom += 5;
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(4);
				}
			}
		}).start();
		
	}
	
	private void dismissOtherNumberLayout(LinearLayout ln)
	{
		target_height = ln.getHeight();
		ln_otherlayout = ln;
		
		otherlayout_margin_bottom = ((LinearLayout.LayoutParams) ln.getLayoutParams()).bottomMargin;
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (otherlayout_margin_bottom > -target_height) {
					otherlayout_margin_bottom -= 5;
					try {
						Thread.sleep(10);
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(5);
				}
			}
		}).start();
	}
	
	private View getItemView(final ContactBean contactBean , final int parentPosition,final int childPosition,boolean isShowRemindIcon)
	{
		View convertView = LayoutInflater.from(mContext).inflate(R.layout.contactitems, null);

//		final ContactBean contactBean = contacts.get(position);
		
		final ViewHolder holder = new ViewHolder();

		holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
		holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.area = (TextView) convertView.findViewById(R.id.area);
		holder.othernumber_tips = (ImageButton) convertView.findViewById(R.id.othernumber_tips);
		holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		holder.menu = (LinearLayout) convertView.findViewById(R.id.menu);
		holder.img_remind_tips = (ImageView) convertView.findViewById(R.id.img_remind_tips);
		
		if(isShowRemindIcon)
		{
			holder.img_remind_tips.setVisibility(View.VISIBLE);
		}
		
		final LinearLayout content_layout = (LinearLayout) convertView.findViewById(R.id.content_layout);
		LinearLayout main_layout = (LinearLayout) convertView.findViewById(R.id.main_layout);
		main_layout.setClickable(true);
		main_layout.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						original_x = (int) event.getX();
//						System.out.println(" original_x --->  " + original_x);
						break;

					case MotionEvent.ACTION_UP:
						int destain = (int) event.getX() - original_x;
//						System.out.println(" destain ---> " + destain);

						if (destain > 60) // 向右滑
						{
							l_r = 1;
							scrollLeftOrRight(content_layout);
//							System.out.println("  //向右滑 ");
							number = contactBean.getNumberlist().get(childPosition);
						} else if (destain < -60) // 向左滑
						{
							l_r = 2;
							scrollLeftOrRight(content_layout);
							number = contactBean.getNumberlist().get(childPosition);
						} else { // 点击事件
							popUpMenu(holder.menu);
						}

						break;

					default:
						break;
					}

					return false;
				}
			});
		
		
		holder.menu_call = (Button)convertView.findViewById(R.id.menu_call);
		holder.menu_call.setTag(contactBean.getNumberlist().get(childPosition));
		holder.menu_call.setOnClickListener(onClickListener);
		
		holder.menu_sms_detail = (Button)convertView.findViewById(R.id.menu_sms_detail);
//		holder.menu_sms_detail.setTag("");
		holder.menu_sms_detail.setOnClickListener(onClickListener);

		holder.menu_contact_detail = (Button)convertView.findViewById(R.id.menu_contact_detail);
		holder.menu_contact_detail.setTag(String.valueOf(contactBean.getContact_id()));
		holder.menu_contact_detail.setOnClickListener(onClickListener);
		
		holder.menu_delete = (Button)convertView.findViewById(R.id.menu_delete);
		//包含两个位置，一个父位置，一个子位置。  如果子位置为-1，说明删除的是父类，则删除整个联系人, 如果子位置不为-1，则说明删除的是具体的某一个号码.
		holder.menu_delete.setTag(parentPosition+":"+childPosition+","+String.valueOf(contactBean.getContact_id())+","+contactBean.getNick());
		holder.menu_delete.setOnClickListener(onClickListener);

		holder.menu_remind = (Button)convertView.findViewById(R.id.menu_remind);
//		holder.menu_remind.setTag(contactBean.getNumberlist().get(childPosition));
		holder.menu_remind.setOnClickListener(onClickListener);
		
		holder.menu_add_to = (Button)convertView.findViewById(R.id.menu_add_to);
		holder.menu_add_to.setTag(contactBean.getNumberlist().get(childPosition));
		holder.menu_add_to.setOnClickListener(onClickListener);
		
		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Cursor photo = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { ContactsContract.Contacts.Data.DATA15 },
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
		
		if (null == filterNum) {
			holder.number.setText(contactBean.getNumberlist().get(childPosition));
		} else {
			holder.number.setText(Html.fromHtml(contactBean.getNumberlist().get(childPosition).replace(filterNum, "<font color='#3d8eba'>" + filterNum+ "</font>")));
		}
		
//		holder.number.setText(contactBean.getNumberlist().get(childPosition));
		holder.area.setText(MainActivity.CheckNumberArea(contactBean.getNumberlist().get(childPosition)));
		
		String nick = contactBean.getNick();
		
		
		ContactLauncherDBHelper dbHelpler = new ContactLauncherDBHelper(mContext);
		SQLiteDatabase db = dbHelpler.getReadableDatabase();
		
		Cursor heat = db.query(dbHelpler.heat_table, new String[]{dbHelpler.HEAT}, dbHelpler.CONTACT_ID+"='"+contactBean.getContact_id()+"'",null, null, null, null);
		if(heat.getCount() > 0){
			
			if(heat.moveToFirst()){
				
				int progress = heat.getInt(heat.getColumnIndex(dbHelpler.HEAT));
				holder.progressBar.setProgress(progress);
			}
			
		}
		
		heat.close();
		
		db.close();
		
		
//		String nickName = mNicks[position];
//		String catalog = converterToFirstSpell(nickName).substring(0, 1);
//		if (position == 0) {
//			holder.tvCatalog.setText(catalog);
//		} else {
//			String lastCatalog = converterToFirstSpell(mNicks[position - 1]).substring(0, 1);
//			if (catalog.equals(lastCatalog)) {
//			} else {
//				holder.tvCatalog.setText(catalog);
//			}
//		}
		
		holder.tvNick.setText(nick);
		
		return convertView;
	}
	
	@Override
	public int getPositionForSection(int section) {
		
//		char b = '#';
//		if(section == b )
//		{
//			return 0;
//		}else{
//			System.out.println("section  ---->  "+section);
//			for (int i = 0; i < oldcontacts.size(); i++) {
//				String l = oldcontacts.get(i).getName_pinyin_cap();
//				char firstChar = l.toUpperCase().charAt(0);
//
//				if (firstChar == section) {
//					return i;
//				}
//			}
			return -1;
//		}
	}

	
	public void filter(String s)
	{
		s = s.toLowerCase();
		filterNum = s;
		
//		System.out.println("  ===performFiltering===   "  + s );
		
		result = new ArrayList<ContactBean>();
		
		System.out.println("oldInfoList  size   ----- > "+oldInfoList.size());
		
		if (oldInfoList != null && oldInfoList.size() != 0) {
			
			for(ContactBean c : oldInfoList)
			{
				if(c.getNick() !=null && c.getNick().contains(s))
				{
					result.add(c);
				}else if(c.getNumber() !=null && letterParser.numberMatch(c.getNumber(), s.toString()))
				{
					result.add(c);
				}else if(c.getName_pinyin() !=null && c.getName_pinyin().contains(s))
				{
					result.add(c);
				}else if(c.getName_pinyin_cap() !=null && c.getName_pinyin_cap().contains(s))
				{
					result.add(c);
				}
			}
		}
		
		handler.sendEmptyMessage(6);
	}
	
	@Override
	public int getSectionForPosition(int position) {
		return position;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	
	private void popUpMenu(View view) {
		
		//隐藏    同一时间只能有一个下来菜单被展开
		if (menu != null && menu != view) {  
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
			menu.setVisibility(View.GONE);
		}

		menu = view;
//	    height = menu.getHeight();
		margin_bottom = ((LinearLayout.LayoutParams) menu.getLayoutParams()).bottomMargin;

//		System.out.println(" margin_bottom ---> " + margin_bottom);
//		System.out.println(" height ---> " + height);

//		System.out.println(" height ---->" + height);
//		System.out.println(" margin_bottom --->" + margin_bottom);
		
		if(margin_bottom<0)
		{
			menu.setVisibility(View.VISIBLE);
		}
		
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
	
	class ViewHolder {
		TextView tvCatalog;// 目录
		ImageView ivAvatar;// 头像
		TextView tvNick;// 昵称
		TextView number;
		TextView area;
		ImageButton othernumber_tips;
		LinearLayout othernumberlayout;
		
		
		ProgressBar progressBar;
		Button menu_call;
		Button menu_sms_detail;
		Button menu_contact_detail;
		Button menu_delete;
		Button menu_remind;
		Button menu_add_to;
		
		LinearLayout content_layout;
		LinearLayout main_layout;
		
		LinearLayout menu;
		
		ImageView img_remind_tips;//提醒标示
	}
	
	
	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public static String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				}
				catch (BadHanyuPinyinOutputFormatCombination e) {
//					e.printStackTrace();
				}
				catch(Exception ex){
//					ex.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	@Override
	public Filter getFilter() {
//		Filter filter = new Filter() {
//
//			@SuppressWarnings("unchecked")
//			@Override
//			protected void publishResults(CharSequence constraint,
//					FilterResults results) {
//				contacts = (ArrayList<ContactBean>) results.values;
//				if (results.count > 0) {
//					notifyDataSetChanged();
//				} else {
//					notifyDataSetInvalidated();
//				}
//			}
//
//			@Override
//			protected FilterResults performFiltering(CharSequence s) {
//				
//				filterNum = s.toString();
//				
//				FilterResults results = new FilterResults();
//				
//				ArrayList<ContactBean> result = new ArrayList<ContactBean>();
//				
//				if (oldcontacts != null && oldcontacts.size() != 0) {
//					
//						for (int i = 0; i < oldcontacts.size(); i++) {
//							
//							if(!isNumeric(s.toString())){
//								
//								if (oldcontacts.get(i).getSork_key().contains(s.toString().toLowerCase())) 
//									result.add(oldcontacts.get(i));
//								else if(oldcontacts.get(i).getName_letter().contains(s.toString().toLowerCase())) 
//									result.add(oldcontacts.get(i));
//								else if(oldcontacts.get(i).getName_pinyin().contains(s.toString().toLowerCase())) 
//									result.add(oldcontacts.get(i));
//								else if(oldcontacts.get(i).getNick().contains(s.toString().toLowerCase())) 
//									result.add(oldcontacts.get(i));
//								
//							}else{
//								
//								if(oldcontacts.get(i).getNumber().contains(s)){
//									
//									result.add(oldcontacts.get(i));
//									
//								}
//							}
//					}
//				}
//				
//				results.values = result;
//				results.count = result.size();
//				
//				return results;
//			}
//		};
//		return filter;
		
		return null;
	}
	
	public boolean isNumeric(String str) {
	    Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
	}
	
	/**
	 * 重新绑定数据
	 * @param contacts
	 */
	public void reBindDateSet(List<ContactBean> contacts)
	{
       List<ContactBean> cc = new ArrayList<ContactBean>();
		
		//过滤加密联系人
		if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
		{
			for(ContactBean cb:contacts)
			{
				boolean isMath = false;
			
					for(EnContact ec:MainActivity.EN_CONTACTS)
					{
//						System.out.println(" 加密的id:  " +ec.getContactId() + "   联系人的id ：  " + String.valueOf(cb.getContact_id()) );
						if(ec.getContactId().equals(String.valueOf(cb.getContact_id())))
						{
							isMath = true;
							break;
						}
					
//						break;
					}
					
					if(!isMath)
					{
						cc.add(cb);
					}
				}
			this.oldInfoList = cc;
			
		}else{
			this.oldInfoList = contacts;
		}
		
		result = new ArrayList<ContactBean>();
		
//		System.out.println("oldInfoList  size   ----- > "+oldInfoList.size());
		
		if (oldInfoList != null && oldInfoList.size() != 0) {
			
			for(ContactBean c : oldInfoList)
			{
				if(c!=null)
				{
					if(c.getNick() !=null && c.getNick().contains(filterNum))
					{
						result.add(c);
					}else if(c.getNumber() !=null && letterParser.numberMatch(c.getNumber(), filterNum.toString()))
					{
						result.add(c);
					}else if(c.getName_pinyin() !=null && c.getName_pinyin().contains(filterNum))
					{
						result.add(c);
					}else if(c.getName_pinyin_cap() !=null && c.getName_pinyin_cap().contains(filterNum))
					{
						result.add(c);
					}
				}
			}
		}
		
		handler.sendEmptyMessage(6);
		
	}

	public List<ContactBean> getContactinfoList() {
		return contactinfoList;
	}
}
