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
import android.widget.ListView;
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
import com.dongji.app.tool.PhoneNumberTool;

public class ContactsAdapter extends BaseAdapter  implements SectionIndexer,Filterable {

	Context mContext;
	private String[] mNicks;
	
	private int pos;
	private ListView mListView;
	
	ContactLayout contactLayout;
	
	OnClickListener onClickListener;
	
	List<ContactBean> contacts = new ArrayList<ContactBean>();
	
	List<ContactBean> original_contacts; //数据
	
	private String filterNum;
	
	public View menu;
	int margin_bottom;
	int original_x;

	View content_layout;
	int target_dis;
	int l_r_dis;
	int l_r;
	
	int height;
	
	String number ;
	
	LinearLayout ln_otherlayout;
	int otherlayout_margin_bottom;
	int target_height;
	
	int sort = 1; //联系人列表的排序方式
	
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			
			final AbsoluteLayout.LayoutParams ablp = new AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.FILL_PARENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0, 0);

			switch (msg.what) {
			case 0:
				lp.setMargins(0, 0, 0, margin_bottom);

				menu.setLayoutParams(lp);
				menu.postInvalidate();

				System.out.println(" margin_bottom ---> " + margin_bottom);
				if(margin_bottom <= -height)
				{
					menu.setVisibility(View.GONE);
				}
				
				mListView.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						
						if(pos!=-1)
						{
							mListView.smoothScrollToPosition(pos);
						}
//						System.out.println("滑动--->");
					}
				}, 100);
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
				
//				FrameLayout.LayoutParams lp3 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT,FrameLayout.LayoutParams.WRAP_CONTENT);
//				lp3.setMargins(0, 0, 0, 0);
//				content_layout.setLayoutParams(lp3);
//				content_layout.postInvalidate();
				
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
		    	
			default:
				break;
			}

		};
	};
	
	public ContactsAdapter(Context context,ContactLayout contactLayout, List<ContactBean> contacts,OnClickListener onClickListener, ListView mListView,int sort)
	{
		this.mContext = context;
		this.contactLayout = contactLayout;
		
		this.height = context.getResources().getDimensionPixelSize(R.dimen.menu_height);
		
		this.mListView=mListView;
		this.sort = sort;
		
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
					}
					
					if(!isMath)
					{
						cc.add(cb);
					}
					
				}
			
			this.contacts = cc;
			
		}else{
			this.contacts = contacts;
		}
		this.original_contacts = this.contacts;
		this.onClickListener = onClickListener;
		
		target_dis = context.getResources().getDimensionPixelSize(R.dimen.target_dis);
	}
	
	@Override
	public int getCount() {
		return contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		
		final ViewHolder holder;
		if (convertView == null) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.contactitems, null);
		holder = new ViewHolder();
		holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
		holder.split = (TextView) convertView.findViewById(R.id.split);
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

		final ContactBean contactBean = contacts.get(position);
		
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
							if(contactBean.getContact_id()!=MainActivity.MY_CONTACT_ID) //机主不允许打电话和发短信
							{
								l_r = 1;
								scrollLeftOrRight(content_layout);
//								System.out.println("  //向右滑 ");
								number = contactBean.getNumber();
							}
						
						} else if (destain < -60) // 向左滑
						{
							if(contactBean.getContact_id()!=MainActivity.MY_CONTACT_ID) //机主不允许打电话和发短信
							{
								l_r = 2;
								scrollLeftOrRight(content_layout);
								number = contactBean.getNumber();
							}
							
						} else { // 点击事件
							popUpMenu(holder.menu);
							pos = position;
						}

						break;

					default:
						break;
					}

					return false;
				}
			});
		
		if(contactBean.getContact_id()==MainActivity.MY_CONTACT_ID) //机主只显示 联系人详情选项
		{
			holder.menu_call.setVisibility(View.GONE);
			holder.menu_sms_detail.setVisibility(View.GONE);
			holder.menu_delete.setVisibility(View.GONE);
			holder.menu_remind.setVisibility(View.GONE);
			holder.menu_add_to.setVisibility(View.GONE);
			
			convertView.findViewById(R.id.menu_ln_call).setVisibility(View.GONE);
			convertView.findViewById(R.id.menu_ln_sms_detail).setVisibility(View.GONE);
			convertView.findViewById(R.id.menu_ln_delete).setVisibility(View.GONE);
			convertView.findViewById(R.id.menu_ln_remind).setVisibility(View.GONE);
			convertView.findViewById(R.id.menu_ln_add_to).setVisibility(View.GONE);
			
		}else{
			holder.menu_call.setVisibility(View.VISIBLE);
			holder.menu_sms_detail.setVisibility(View.VISIBLE);
			holder.menu_delete.setVisibility(View.VISIBLE);
			holder.menu_remind.setVisibility(View.VISIBLE);
			holder.menu_add_to.setVisibility(View.VISIBLE);
			
			convertView.findViewById(R.id.menu_ln_call).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.menu_ln_sms_detail).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.menu_ln_delete).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.menu_ln_remind).setVisibility(View.VISIBLE);
			convertView.findViewById(R.id.menu_ln_add_to).setVisibility(View.VISIBLE);
		}
		
		holder.menu_call.setOnClickListener(onClickListener);
		
		holder.menu_sms_detail.setOnClickListener(onClickListener);

		holder.menu_contact_detail.setOnClickListener(onClickListener);
		
		holder.menu_delete.setOnClickListener(onClickListener);
		
		holder.menu_remind.setOnClickListener(onClickListener);
		
		holder.menu_add_to.setOnClickListener(onClickListener);

		
		String photo_id = contactBean.getPhoto_id();
		if (photo_id != null) {
			Bitmap contactPhoto = getPhoto(photo_id);
			
			if (contactPhoto == null){
				holder.ivAvatar.setImageResource(R.drawable.default_contact);
			}
			else{
				holder.ivAvatar.setImageBitmap(contactPhoto);
			}
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
//					contactBean.setNumber(PhoneNumberTool.cleanse(phone));
					contactBean.setNumber(phone);
					//其他的号码
					while (phones.moveToNext()) { 
						String ph = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//						contactBean.getNumberlist().add(PhoneNumberTool.cleanse(ph));
						contactBean.getNumberlist().add(ph);
					}
				}
			phones.close();
		}
		
		
		
		if (null == filterNum) {
			holder.number.setText(contactBean.getNumber());
		} else {
			holder.number.setText(Html.fromHtml(contactBean.getNumber().replace(filterNum, "<font color='#cc0000'>" + filterNum+ "</font>")));
		}
		
	
		if(contactBean.getArea()!=null)
		{
			holder.area.setText(contactBean.getArea());
		}else{
			String area = MainActivity.CheckNumberArea(contactBean.getNumber());
			holder.area.setText(area);
			contactBean.setArea(area);
		}
		
		if (contactBean.getContact_id() == MainActivity.MY_CONTACT_ID) {
			
			holder.progressBar.setVisibility(View.GONE);
			
		} else {
			holder.progressBar.setVisibility(View.VISIBLE);
			ContactLauncherDBHelper dbHelpler = new ContactLauncherDBHelper(mContext);
			SQLiteDatabase db = dbHelpler.getReadableDatabase();
			Cursor heat = db.query(dbHelpler.heat_table, new String[]{dbHelpler.HEAT}, dbHelpler.CONTACT_ID + " = " +contactBean.getContact_id(),null, null, null, null);
			
			if(heat.getCount() > 0){
				if(heat.moveToFirst()){
					int progress = heat.getInt(heat.getColumnIndex(dbHelpler.HEAT));
	//				System.out.println(" nick ----- > "+ nick + " progress ---- > " +progress);
					holder.progressBar.setProgress(progress);
				}
			}
			heat.close();
			db.close();
		}
		
		String nick = contactBean.getNick();
		holder.tvNick.setText(nick);
		
		if (sort == 1) {
		
			String catalog = converterToFirstSpell(nick).substring(0, 1).toUpperCase();
	      
			if(contactBean.getContact_id()==MainActivity.MY_CONTACT_ID || !isNumberic(nick.charAt(0))) //如果是机主或者名字首位为数字，都用“#”表示
			{
				catalog = "#";
			} 
	
			if (position == 0) {
				holder.tvCatalog.setVisibility(View.VISIBLE);
				holder.split.setVisibility(View.VISIBLE);
				holder.tvCatalog.setText(catalog);
			} else {
				
				String beforNick = contacts.get(position-1).getNick(); 
				String lastCatalog = converterToFirstSpell(beforNick).substring(0, 1).toUpperCase();
				
				if(beforNick.equals("机主") || !isNumberic(beforNick.charAt(0))) //如果是机主或者名字首位为数字，都用“#”表示
				{
					lastCatalog = "#";
				}  
	
				if (catalog.equals(lastCatalog)) {
					holder.tvCatalog.setVisibility(View.GONE);
					holder.split.setVisibility(View.GONE);
					holder.tvCatalog.setText("");
				} else {
					holder.tvCatalog.setVisibility(View.VISIBLE);
					holder.split.setVisibility(View.VISIBLE);
					holder.tvCatalog.setText(catalog);
				}
			}
		} else {
			
			holder.tvCatalog.setVisibility(View.GONE);
			holder.split.setVisibility(View.GONE);
			holder.tvCatalog.setText("");
		}
		
		int size = contactBean.getNumberlist().size() ;
//		System.out.println(" phone size   ---- > " + size);
		holder.othernumberlayout.setVisibility(View.GONE);
				
		//是否显示提醒标识
		boolean isShowRemindIcon = false;
		Cursor c = DButil.getInstance(mContext).queryRemindByContactId(contactBean.getContact_id().toString());
		if(c.getCount()>0)
		{
			holder.img_remind_tips.setVisibility(View.VISIBLE);
			isShowRemindIcon = true;
		}
		c.close();
				
		if( size > 0){
			holder.othernumber_tips.setVisibility(View.VISIBLE);
			holder.othernumber_tips.setImageResource(R.drawable.add_buttom);
			holder.othernumberlayout.removeAllViews();
			for(int i =0;i<size ; i++)
			{
				holder.othernumberlayout.addView(getItemView(contactBean, position, i, isShowRemindIcon));
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
						holder.othernumber_tips.setImageResource(R.drawable.add_buttom_up);
					}else{
						dismissOtherNumberLayout(holder.othernumberlayout); //隐藏
//						holder.othernumber_tips.setText("展");
						holder.othernumber_tips.setImageResource(R.drawable.add_buttom);
					}
				}
			});
		}else{
			holder.othernumber_tips.setVisibility(View.GONE);
		}
		
		
		//绑定数据
		holder.menu_call.setTag(contactBean.getNumber());
		holder.menu_sms_detail.setTag(contactBean.getNumber());
		holder.menu_contact_detail.setTag(String.valueOf(contactBean.getContact_id()));
		holder.menu_remind.setTag(String.valueOf(contactBean.getContact_id()));
		holder.menu_add_to.setTag(contactBean.getNumber());
		//包含两个位置，一个父位置，一个子位置。  如果子位置为-1，说明删除的是父类，则删除整个联系人, 如果子位置不为-1，则说明删除的是具体的某一个号码.
	    holder.menu_delete.setTag(position+":-1"+","+String.valueOf(contactBean.getContact_id())+","+contactBean.getNick()); 
	    
		
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
//		holder.tvCatalog = (TextView) convertView.findViewById(R.id.contactitem_catalog);
		holder.ivAvatar = (ImageView) convertView.findViewById(R.id.contactitem_avatar_iv);
		holder.tvNick = (TextView) convertView.findViewById(R.id.contactitem_nick);
		holder.number = (TextView) convertView.findViewById(R.id.number);
		holder.area = (TextView) convertView.findViewById(R.id.area);
		holder.othernumber_tips = (ImageButton) convertView.findViewById(R.id.othernumber_tips);
		holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
		holder.menu = (LinearLayout) convertView.findViewById(R.id.menu);
		holder.img_remind_tips  = (ImageView) convertView.findViewById(R.id.img_remind_tips);
		
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

						if (destain > 100) // 向右滑
						{
							if(contactBean.getContact_id()!=MainActivity.MY_CONTACT_ID) //机主不允许打电话和发短信
							{
								l_r = 1;
								scrollLeftOrRight(content_layout);
//								System.out.println("  //向右滑 ");
								number = contactBean.getNumberlist().get(childPosition);
							}
						} else if (destain < -100) // 向左滑
						{
							if(contactBean.getContact_id()!=MainActivity.MY_CONTACT_ID) //机主不允许打电话和发短信
							{
								l_r = 2;
								scrollLeftOrRight(content_layout);
								number = contactBean.getNumberlist().get(childPosition);
							}
						} else { // 点击事件
							popUpMenu(holder.menu);
							pos = parentPosition;
							
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
		holder.menu_sms_detail.setTag(contactBean.getNumberlist().get(childPosition));
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
		holder.menu_remind.setTag(String.valueOf(contactBean.getContact_id()));
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
			holder.number.setText(Html.fromHtml(contactBean.getNumberlist().get(childPosition).replace(filterNum, "<font color='#cc0000'>" + filterNum+ "</font>")));
		}
		
//		holder.number.setText(contactBean.getNumberlist().get(childPosition));
		holder.area.setText(MainActivity.CheckNumberArea(contactBean.getNumberlist().get(childPosition)));
		
		String nick = contactBean.getNick();
		
		ContactLauncherDBHelper dbHelpler = new ContactLauncherDBHelper(mContext);
		SQLiteDatabase db = dbHelpler.getReadableDatabase();
		
		Cursor heat = db.query(dbHelpler.heat_table, new String[]{dbHelpler.HEAT}, dbHelpler.CONTACT_ID + " = " +contactBean.getContact_id(),null, null, null, null);
		
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
		
		if(contactBean.getContact_id()==MainActivity.MY_CONTACT_ID) //机主至显示 联系人详情选项
		{
			holder.menu_call.setVisibility(View.GONE);
			holder.menu_sms_detail.setVisibility(View.GONE);
			holder.menu_delete.setVisibility(View.GONE);
			holder.menu_remind.setVisibility(View.GONE);
			holder.menu_add_to.setVisibility(View.GONE);
		}else{
			holder.menu_call.setVisibility(View.VISIBLE);
			holder.menu_sms_detail.setVisibility(View.VISIBLE);
			holder.menu_delete.setVisibility(View.VISIBLE);
			holder.menu_remind.setVisibility(View.VISIBLE);
			holder.menu_add_to.setVisibility(View.VISIBLE);
		}
		
		return convertView;
	}
	
	@Override
	public int getPositionForSection(int section) {
		
		char b = '#';
		if(section == b )
		{
			return 0;
		}else{
//			System.out.println("section  ---->  "+section);
			
			int size = contacts.size();
			
			for (int i = 0; i < size; i++) {
				ContactBean cb = contacts.get(i);
				String l = cb.getName_pinyin_cap();
				char firstChar = l.toUpperCase().charAt(0);

				if ( cb.getContact_id() !=MainActivity.MY_CONTACT_ID &&  firstChar == section) {
					return i;
				}
			}
			return -1;
		}
	}

	@Override
	public int getSectionForPosition(int position) {
		return position;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	public void collapse() {
		if (menu != null) {
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.setMargins(0, 0, 0, -menu.getHeight());

			menu.setLayoutParams(lp);
			menu.setVisibility(View.GONE);
		}
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
		TextView split;
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
		Filter filter = new Filter() {

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint,
					FilterResults results) {
				contacts = (ArrayList<ContactBean>) results.values;
				if (results.count > 0) {
					notifyDataSetChanged();
				} else {
					notifyDataSetInvalidated();
				}
			}

			@Override
			protected FilterResults performFiltering(CharSequence s) {
				
				filterNum = s.toString();
				
				FilterResults results = new FilterResults();
				
				ArrayList<ContactBean> result = new ArrayList<ContactBean>();
				
				if (original_contacts != null && original_contacts.size() != 0) {
					
						for (int i = 0; i < original_contacts.size(); i++) {
							
							if(!isNumeric(s.toString())){
								
								if (original_contacts.get(i).getSork_key().contains(s.toString().toLowerCase())) 
									result.add(original_contacts.get(i));
								else if(original_contacts.get(i).getName_letter().contains(s.toString().toLowerCase())) 
									result.add(original_contacts.get(i));
								else if(original_contacts.get(i).getName_pinyin().contains(s.toString().toLowerCase())) 
									result.add(original_contacts.get(i));
								else if(original_contacts.get(i).getNick().contains(s.toString().toLowerCase())) 
									result.add(original_contacts.get(i));
								
							}else{
								
								if(original_contacts.get(i).getNumber().contains(s)){
									
									result.add(original_contacts.get(i));
									
								}
							}
					}
				}
				
				results.values = result;
				results.count = result.size();
				
				return results;
			}
		};
		return filter;
	}
	
	public boolean isNumeric(String str) {
	    Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
	}
	
	/**
	 * 重新绑定数据
	 * @param contacts
	 */
	public void reBindDateSet(List<ContactBean> contacts,int sort)
	{
		this.original_contacts = contacts;
		
		this.sort = sort;
		
       List<ContactBean> cc = new ArrayList<ContactBean>();
		
		//过滤加密联系人
		if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
		{
			for(ContactBean cb:contacts)
			{
				boolean isMath = false;
			
					for(EnContact ec:MainActivity.EN_CONTACTS)
					{
//						System.out.println(" 加密的id:  " +ec.getContactId() + "   联系人的id ：  " + String.valueOf(cb.getContact_id()));
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
			
			this.contacts = cc;
			
		}else{
			this.contacts = contacts;
		}
		
		notifyDataSetChanged();
	}

	public List<ContactBean> getOriginal_contacts() {
		return original_contacts;
	}
	
	private Bitmap getPhoto(String photo_id)
	{
//  	System.out.println(" photo id ---- > " + photo_id);
		Bitmap contactPhoto =null;
		Cursor cursor3 = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
						new String[] { "data15" },"ContactsContract.Data._ID=" + photo_id,
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
	
	/**
	 * 输入的第一个是否为汉字
	 * @param a char
	 * @return boolean
	 */
	public static boolean isChinese(char a) { 
	     int v = (int)a; 
	     return (v >=19968 && v <= 171941); 
	}
	
	/**
	 * 输入的第一个是否为数字
	 * @param a char
	 * @return boolean ,返回false表示为数字
	 */
	public static boolean isNumberic(char n) {
		int chr = (int)n;
		return (chr<48 || chr>57);
	}
}
