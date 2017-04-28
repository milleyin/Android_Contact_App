package com.dongji.app.addressbook;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes.Name;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.AggregationExceptions;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.ContactsAdapter;
import com.dongji.app.adapter.ContactsSearchAdapter;
import com.dongji.app.adapter.EditGroupModeAdapter;
import com.dongji.app.adapter.GroupInfoAdapter;
import com.dongji.app.adapter.CallLogsAdapter;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.GroupInfo;
import com.dongji.app.sqllite.ContactLauncherDBHelper;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.AddBlackWhite;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.ui.MyDialog;
import com.dongji.app.ui.ScrollLayout;
import com.dongji.app.ui.SideBar;
import com.dongji.app.ui.VerScrollLayout.OnScrollerFinish;
import com.umeng.common.net.m;
import com.umeng.common.net.o;

/**
 * 
 * 联系人列表
 * 
 * @author Administrator
 * 
 */
public class ContactLayout implements OnClickListener {

	final MainActivity mainActivity;
	
	boolean isNeedRefresh  = false; //是否需要刷新
	
	ContactsAdapter contactsAdapter;
	public View view;
	public ScrollLayout contact_scroller;
	
	
	LinearLayout ln_contact;
	public ListView lvcontact;
	public SideBar indexBar;
	WindowManager mWindowManager;
	TextView mDialogText;
	NewMessageActivity newMessageLayout;
	LinearLayout add_ln;
	LinearLayout ln_search;
	ContactsSearchAdapter contactsSearchAdapter;
	EditText ed_search;
	public ListView lv_search;

	
	ListView lv_group_info; // 分组信息列表
	Button editButton;
	LinearLayout ln_edit_group_title;
	LinearLayout dataSaveCancel;

	
	// 编辑分组 布局
	Button setRemind; // 分组提醒
	Button saveButton; // 保存
	Button cancelButton; // 取消
	
	
	public static int MY_GROUP_TITLE_ID_ALL = -1; // 全部分组
	public static int MY_GROUP_TITLE_ID_NO = -2; // 未分组
	public static int MY_GOUTP_TITLE_ID_ADD = -3; // 添加分组


	ArrayList<GroupInfo> cur_groupInfo = null; // 当前显示的分组
	ArrayList<GroupInfo> all_group_contact_infos = null; // 全部分组的联系人信息
	ArrayList<GroupInfo> groupInfos;// 分组的信息 ： id ， title

	FrameLayout cur_group_title; // 当前被选中的分组 title
	int group_index;

	List<ContactBean> contacts = new ArrayList<ContactBean>();
	GroupInfoAdapter curGroupAdapter;
	Animation a_out;

	InputMethodManager imm;
	private StringBuilder sb;
	public AddContactActivity addContactLayout;

	// 编辑分组模式
	EditGroupModeAdapter editGroupModeAdapter;
	String groupNameCache; // 正在编辑的分组的名称的缓存

	boolean isHaveAddGroup;
	Button showAdapterButton;
	boolean isHaveDrop;

	int selected_delete_parent_position;
	int selected_delete_child_position;
	String selected_delete_contact_id;

	ProgressDialog progressDialog;

	// 顶栏
	Button btn_search; //搜索联系人
	Button btn_add_contact; //新建联系人
	TextView tv_top; //顶栏的标题
	Button btn_change; //分组视图 与 联系人视图的切换

	SQLiteDatabase db = null;

	public static String SF_NAME = "systemsetting";
	SharedPreferences sf;

	Dialog dialog_add_to;
	String add_to_number;
	Dialog popup_menu;
	CheckBox ck; // 时间被选中的
	Button setRemindConfirm;
	Button setRemindCancel;

	boolean isLoadFinish = false;

	MyDialog myDialog;

	ProgressDialog groupProgressDialog;

	// 正在修改分组信息
	public boolean isEditingGroup = false;

	int sort = 1; //联系人列表的排序方式

	int cur_group_remind_id;
	long cur_group_id; // 当前正在显示的联系人分组id
	EditText et_day_gap;
	
	AddBlackWhite addBlackWhite;

	Handler handler = new Handler() {
		
		public void handleMessage(android.os.Message msg) {

			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			switch (msg.what) {
			case 1:

				contactsAdapter = new ContactsAdapter(mainActivity,ContactLayout.this, contacts,new OnMenuItemClickListener(), lvcontact,sort);
				lvcontact.setAdapter(contactsAdapter);
				ln_contact.setVisibility(View.VISIBLE);
				
				break;
				
			case 2:
				
				break;
			default:
				break;
			}
		};
	};

	public ContactLayout(final MainActivity mainActivity) {
		this.mainActivity = mainActivity;
		
		
		view = mainActivity.findViewById(R.id.layout_contact);

		//顶栏
		btn_search = (Button)view.findViewById(R.id.btn_contact_search);
		btn_search.setOnClickListener(this);
		btn_add_contact = (Button)view.findViewById(R.id.add_contact);
		btn_add_contact.setOnClickListener(this);
		
		btn_change = (Button)view.findViewById(R.id.btn_change);
		btn_change.setOnClickListener(this);
		
		tv_top = (TextView)view.findViewById(R.id.tv_name);
	
		
		sf = mainActivity.getSharedPreferences(SF_NAME, 0);

		sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);
		addBlackWhite = new AddBlackWhite(mainActivity);
		contact_scroller = (ScrollLayout) view.findViewById(R.id.contact_scroller);
		contact_scroller.setToScreen(1);
		add_ln = (LinearLayout) view.findViewById(R.id.add_all);
		lv_group_info = (ListView) view.findViewById(R.id.contact_info);
		lv_group_info.setOnItemClickListener(new GrouInfoListItemClickListener());

		dataSaveCancel = (LinearLayout) view.findViewById(R.id.data_save_cancel);
		saveButton = (Button) view.findViewById(R.id.save);
		saveButton.setOnClickListener(this);

		editButton = (Button) view.findViewById(R.id.edit_contact);
		editButton.setOnClickListener(new EditGroupClickListener());

		cancelButton = (Button) view.findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);
		setRemind = (Button) view.findViewById(R.id.set_remind);
		setRemind.setOnClickListener(this);

		
		ln_search = (LinearLayout) view.findViewById(R.id.ln_search);
		ln_search.setLongClickable(true);
		ln_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

				a_out.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {

						ln_search.setVisibility(View.GONE);
						
						sf = mainActivity.getSharedPreferences(SF_NAME, 0);

						sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

						if (sort == 1) {
							// if ( indexBar.getVisibility() == View.GONE)
							indexBar.setVisibility(View.VISIBLE);

						} else {

							// if ( indexBar.getVisibility() == View.VISIBLE)
							indexBar.setVisibility(View.GONE);

						}
					}
				});
				ln_search.startAnimation(a_out);
			}
		});

		mWindowManager = (WindowManager) mainActivity.getSystemService(Context.WINDOW_SERVICE);

		ln_contact = (LinearLayout) view.findViewById(R.id.ln_contact);
		lvcontact = (ListView) view.findViewById(R.id.lvContact);
		ed_search = (EditText) view.findViewById(R.id.ed_search);
		lv_search = (ListView) view.findViewById(R.id.lv_search);


		new Thread(new Runnable() {

			@Override
			public void run() {

				String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";

				ContentResolver contentResolver = mainActivity.getContentResolver();
				//排除没号码 和  为空的联系人 : sort_key == null 则联系人为空
				
//				Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null , " sort_key is not null AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER  + " is not 0",null, sortOrder);
				Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null , " sort_key is not null",null, sortOrder);
				
				int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
				int photo_column = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
				int nick_column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				int sort_key_column = cursor.getColumnIndex("sort_key");

				int contact_account_type_column = cursor.getColumnIndex("contact_account_type");
				
				int phone_count = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);


				while (cursor.moveToNext()) {

					long contact_id = cursor.getLong(id_column);

//					boolean isBlack = false;


						String key = cursor.getString(sort_key_column);

//						int phone_num = cursor.getInt(phone_count); //是否有号码
						
//						boolean isCheck = true; //此联系人是否有效
//						
//						long c_id  = cursor.getLong(id_column);
//						if(phone_num ==0 &&  c_id != MainActivity.MY_CONTACT_ID)  //没有电话号码，并且不是机主
//						{
//							isCheck = false;
//						}
//						
//						if (key != null && isCheck ) // 没有sortkey ，则为空的联系人 
//						{
							key = key.replace(" ", "");
							String array = "";
							boolean b = false;
							String capPingYin = "";

							for (int i = 0; i < key.length(); i++) {
								char c = key.charAt(i);

								if (c > 256) {// 汉字符号
									b = false;
								} else {
									array += c;
									if (!b) {
										capPingYin += c;
										b = true;
									}
								}
							}

							ContactBean contactBean = new ContactBean();

							contactBean.setContact_id(cursor.getLong(id_column));
							contactBean.setPhoto_id(cursor.getString(photo_column));
							contactBean.setNick(cursor.getString(nick_column));

							contactBean.setSork_key(key.replace(" ", "").toLowerCase());
							contactBean.setName_pinyin(array.replace(" ", "").toLowerCase());
							contactBean.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());

							if( cursor.getLong(id_column) == MainActivity.MY_CONTACT_ID ) //机主置顶
							{
								contacts.add(0,contactBean);
							}else{
								contacts.add(contactBean);
							}
							// }
//						}

				}
				cursor.close();

				System.out.println(" contact sort ---- > " + sort);

				if (sort == 0) {

					ContactLauncherDBHelper dbHelpler = new ContactLauncherDBHelper(mainActivity);
					SQLiteDatabase db = dbHelpler.getReadableDatabase();

					Cursor heat = db.query(dbHelpler.heat_table,new String[] { dbHelpler.CONTACT_ID }, null, null,null, null, dbHelpler.HEAT + " desc");

//					System.out.println(" contacts -----> " + contacts.size());

					List<ContactBean> new_all = new ArrayList<ContactBean>();

					if (heat.moveToFirst()) {

						do {

							long contact_id = heat.getInt(heat.getColumnIndex(dbHelpler.CONTACT_ID));

							for (ContactBean c : contacts) {
								if (c.getContact_id() == contact_id) {
									new_all.add(c);
								}
							}
						} while (heat.moveToNext());

						contacts.removeAll(new_all);
						new_all.addAll(contacts);

						contacts = new_all;
					}

					heat.close();

				}

//				System.out.println(" contacts ---> " + contacts.size());

				// db.close();

				isLoadFinish = true;
				
				handler.sendEmptyMessage(1);
			}
		}).start();

		indexBar = (SideBar) view.findViewById(R.id.sideBar);
		indexBar.setListView(lvcontact);
		indexBar.init(mainActivity);

		if (sort == 0) {

			indexBar.setVisibility(View.GONE);

		} else {

			indexBar.setVisibility(View.VISIBLE);
		}

		a_out = AnimationUtils.loadAnimation(mainActivity, R.anim.up_out);
		imm = (InputMethodManager) mainActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);

	}

	public void onScrollerFinish() {

		if (isLoadFinish && contactsAdapter == null) {
			contactsAdapter = new ContactsAdapter(mainActivity,ContactLayout.this, contacts,
					new OnMenuItemClickListener(), lvcontact,sort);
			lvcontact.setAdapter(contactsAdapter);
			ln_contact.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 
	 * 显示 左侧的  分组栏
	 * 
	 */
	public void layouGroupsBar() {
		
		add_ln.removeAllViews();

		for (int i = 0; i < groupInfos.size(); i++) {
			GroupInfo g = groupInfos.get(i);
			g.setIndex(i);

			final FrameLayout group_view = (FrameLayout) LayoutInflater.from(mainActivity).inflate(R.layout.add_button_item, null);
			final Button deleteGroupButton = (Button) group_view.findViewById(R.id.delete_group_button);

			Button button_item = (Button) group_view.findViewById(R.id.button_item);

			String gTitle = g.getGroup_name();

			if (gTitle.contains("Group:")) {
				gTitle = gTitle.substring(gTitle.indexOf("Group:") + 6).trim();
			}

			if (gTitle.contains("Favorite_")) {
				gTitle = "Favorites";
			}

			if (gTitle.contains("Starred in Android")) {
				gTitle = "Android";
			}

			if (gTitle.contains("My Contacts")) {
				gTitle = "Contacts";
			}

			g.setGroup_name(gTitle+"("+g.getGroup_member_count()+")");
			
			// 设置tag
			group_view.setTag(g);

			deleteGroupButton.setOnClickListener(new DeleteGroupListener());

			if(!gTitle.equals("+添加分组"))
			{
				button_item.setText(gTitle+"("+g.getGroup_member_count()+")");
			}else{
				button_item.setText(gTitle);
			}
			
			button_item.setOnClickListener(new GroupTitleClickListener(group_view, g));

			add_ln.addView(group_view);
		}


		// 隐藏
		editButton.setVisibility(View.GONE);
	}

	/**
	 * 
	 * 根据 groupId 从全部联系人 过滤出指定分组的联系人
	 * 
	 * @param groupId -1:未为分组
	 * @return
	 */
	public ArrayList<GroupInfo> getFilterGroupByGroupId(long groupId) {
		ArrayList<GroupInfo> group = new ArrayList<GroupInfo>();

		for (GroupInfo g : all_group_contact_infos) {
			if (g.getGroup_id() == groupId) {
				group.add(g);
			}
		}

		return group;
	}

	public ArrayList<GroupInfo> getAllContactInfo() {
		long start = System.currentTimeMillis();

		ArrayList<GroupInfo> arrayList = new ArrayList<GroupInfo>();

		ContentResolver contentResolver = mainActivity.getContentResolver();

		List<ContactBean> cotactbeans = contactsAdapter.getOriginal_contacts();
		for (ContactBean cb : cotactbeans) {
			GroupInfo groupInfo = new GroupInfo();

			groupInfo.setPhone_name(cb.getNick());
			groupInfo.setPerson_id(String.valueOf(cb.getContact_id()));
			groupInfo.setPhoto_id(cb.getPhoto_id());

			if (cb.getNumber() != null) {
				groupInfo.setPhone_number(cb.getNumber());
			}

			// 查询联系人的分组信息
			String[] groups = new String[] { GroupMembership.GROUP_ROW_ID };
			String where = GroupMembership.CONTACT_ID + "="+ cb.getContact_id() + " AND " + Data.MIMETYPE + "=" + " '"+ GroupMembership.CONTENT_ITEM_TYPE + "'";
			Cursor groupCursor = contentResolver.query(Data.CONTENT_URI,groups, where, null, null);

			if (groupCursor.moveToNext()) {
				long gourp_id = groupCursor.getLong(groupCursor.getColumnIndex(GroupMembership.GROUP_ROW_ID));
				groupInfo.setGroup_id(gourp_id);
			} else {
				groupInfo.setGroup_id(-1);
			}

			groupCursor.close();

			arrayList.add(groupInfo);
		}

		long end = System.currentTimeMillis();

//		System.out.println("getAllContactInfo  完成  耗时：   ---> " + (end - start));

		return arrayList;
	}

	public ArrayList<GroupInfo> getContactGroup() {

		long start = System.currentTimeMillis();

		// 存放分组信息
		ArrayList<GroupInfo> islist = new ArrayList<GroupInfo>();
		GroupInfo groupInfo2 = new GroupInfo();
		
		
		groupInfo2.setGroup_name("全部");
		groupInfo2.setGroup_id(MY_GROUP_TITLE_ID_ALL);
		groupInfo2.setGroup_member_count(all_group_contact_infos.size());
		islist.add(groupInfo2);
		
		// 我们要得到分组的id 分组的名字
		String[] RAW_PROJECTION = new String[] { ContactsContract.Groups._ID,ContactsContract.Groups.TITLE};
		// 查询条件是Groups.DELETED=0
		String RAW_CONTACTS_WHERE = ContactsContract.Groups.DELETED + " = ? ";
		// 条用内容提供者查询 new String[] { "" + 0 } 是给Groups.DELETED赋值
		Cursor cursor = mainActivity.getContentResolver().query(ContactsContract.Groups.CONTENT_URI, RAW_PROJECTION,RAW_CONTACTS_WHERE, new String[] { "" + 0 }, null);
		
		int id_column = cursor.getColumnIndex("_id");
		int title_column = cursor.getColumnIndex("title");
		
		GroupInfo groupInfo = null;
		
		while (cursor.moveToNext()) {
			// 分组的实体类
			groupInfo = new GroupInfo();
			int id = cursor.getInt(id_column);
			groupInfo.setGroup_id(id);
			groupInfo.setGroup_name(cursor.getString(title_column));
			
			int count = 0;
			
			for(GroupInfo g_m : all_group_contact_infos)
			{
				if(g_m.getGroup_id() == id)
				{
					count ++;
				}
			}

			groupInfo.setGroup_member_count(count);
			
			// 把分组放到集合里去
			islist.add(groupInfo);
		}

		// 默认在集合放一个没有分组的，组名
		// 便于 以后查询，没有分组的联系人
		GroupInfo ginfo = new GroupInfo();
		ginfo.setGroup_name("未分组");
		ginfo.setGroup_id(MY_GROUP_TITLE_ID_NO);
		int count = 0;
		
		for(GroupInfo g_m : all_group_contact_infos)
		{
			if(g_m.getGroup_id() == -1)
			{
				count ++;
			}
		}
		ginfo.setGroup_member_count(count);
		islist.add(ginfo);

		GroupInfo ginfo3 = new GroupInfo();
		ginfo3.setGroup_name("+添加分组");
		ginfo3.setGroup_id(MY_GOUTP_TITLE_ID_ADD);
		islist.add(ginfo3);

		cursor.close();

		long end = System.currentTimeMillis();

//		System.out.println(" 分组 查询　结束   耗时: --->" + (end - start));

		return islist;
	}
	

	public void popSearchLayout() {

		InputMethodManager imm = (InputMethodManager) mainActivity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

		ed_search.requestFocus();

		if (contactsSearchAdapter == null) {
			new Thread(new Runnable() {
				@Override
				public void run() {

					try {
						Thread.sleep(500);
					} catch (Exception e) {
						e.printStackTrace();
					}

					for (ContactBean contactBean : contacts) {
						String number = contactBean.getNumber();
						if (number == null) {
							Cursor phones = mainActivity
									.getContentResolver()
									.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
											new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
											ContactsContract.CommonDataKinds.Phone.CONTACT_ID
													+ " = "
													+ contactBean
															.getContact_id(),
											null, null);

							if (phones.moveToNext()) { // 第一个号码
								String phone = phones.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
								contactBean.setNumber(phone);
								// 其他的号码
								while (phones.moveToNext()) {
									String ph = phones.getString(phones
											.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
									contactBean.getNumberlist().add(ph);
								}
							}
							phones.close();
						}
					}
				}
			}).start();
			contactsSearchAdapter = new ContactsSearchAdapter(mainActivity,
					ContactLayout.this, contacts,
					new OnSearchAdapterMenuItemClickListener());
			ed_search.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
			ed_search.addTextChangedListener(textWatcher);
			lv_search.setAdapter(contactsSearchAdapter);
			lv_search.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {

					((InputMethodManager) mainActivity
							.getSystemService(mainActivity.INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(mainActivity
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);

					return false;
				}
			});
		}

		ln_search.setVisibility(View.VISIBLE);
		ln_search.startAnimation(AnimationUtils.loadAnimation(mainActivity,
				R.anim.up_in));

		indexBar.setVisibility(View.GONE);
	}

	
	public boolean onBackPressed() {
		
		if (ln_search.getVisibility() == View.VISIBLE) {
			Animation a_out = AnimationUtils.loadAnimation(mainActivity,
					R.anim.up_out);
			a_out.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {

					ln_search.setVisibility(View.GONE);
					sf = mainActivity.getSharedPreferences(SF_NAME, 0);

					sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

					if (sort == 1) {
						// if ( indexBar.getVisibility() == View.GONE)
						indexBar.setVisibility(View.VISIBLE);

					} else {

						// if ( indexBar.getVisibility() == View.VISIBLE)
						indexBar.setVisibility(View.GONE);

					}
				}
			});
			ln_search.startAnimation(a_out);

			// 清空输入栏
			ed_search.setText("");
			return true;

		} else if (contact_scroller.getCurScreen() == 0) // 在分组界面,点返回键
		{

			if (dataSaveCancel.getVisibility() == View.VISIBLE) {
				
				long groupId = editGroupModeAdapter.getGroupid();

				if (groupId == MY_GOUTP_TITLE_ID_ADD) // 新建分组
				{
					saveGroupAfterNewOnBackPress();

				} else { // 修改分组
					saveGroupAfterEditedOnBackPress();
				}
				
			} else {
				
				setViewToAllContact();
			}

			return true;
		}
		return false;
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		
		case R.id.save:

			EditText edit_group = (EditText) cur_group_title.findViewById(R.id.edit_group);
			String group_name = edit_group.getText().toString();
            if(group_name!=null)
            {
            	if(group_name.length()>20)
            	{
            		Toast.makeText(mainActivity, "分组名称不能超过20个字符", Toast.LENGTH_SHORT).show();
            	}
            	else
            	{
            		if (group_name.equals("")) {
        				group_name = "未命名";
        			}

        			long groupId = editGroupModeAdapter.getGroupid();

        			if (groupId == MY_GOUTP_TITLE_ID_ADD) // 新建分组，后保存
        			{
        				saveGroupAfterNew(group_name);
        				
        			} else { // 修改分组
        				saveGroupAfterEdited(group_name);
        			}
            	}
            }
			

			break;

		case R.id.cancel:

			// 重置回正常的布局
			dataSaveCancel.setVisibility(View.GONE);
			((LinearLayout) cur_group_title
					.findViewById(R.id.ln_edit_group_title))
					.setVisibility(View.GONE);
			editButton.setVisibility(View.VISIBLE);

			Object tag = cur_group_title.getTag();

			if (tag == null) // 新建分组时，点取消，需要移除
			{
				add_ln.removeView(cur_group_title);

				// 选中第一个
				cur_group_title = (FrameLayout) add_ln.getChildAt(0);

				cur_groupInfo = all_group_contact_infos;
				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 改变背景
				cur_group_title.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
				cur_group_title.findViewById(R.id.remove_ln).setBackgroundResource(R.drawable.add_button_bg_selected);

				editButton.setVisibility(View.GONE);
				
				group_index = 0;

			} else {

				// 在当前编辑分组的里面 移除未分组的联系人
				List<GroupInfo> no_groups = new ArrayList<GroupInfo>();
				for (GroupInfo g : cur_groupInfo) {
					if (g.getGroup_id() == -1) {
						no_groups.add(g);
					}
				}
				cur_groupInfo.removeAll(no_groups);

				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);
			}
			break;

		case R.id.set_remind_cancel:

			if (popup_menu != null && popup_menu.isShowing()) {
				popup_menu.dismiss();
			}

			break;

		case R.id.set_remind_confirm:

			if (popup_menu != null && popup_menu.isShowing()) {
				popup_menu.dismiss();
			}

			triggerGroupRmind();

			break;

		case R.id.set_remind:
			popGroupRemind();
			break;

		case R.id.add_contact: //新建联系人
			Intent intent = new Intent(mainActivity, AddContactActivity.class);
			intent.putExtra(AddContactActivity.DATA_TYPE, AddContactActivity.TYPE_ADD_CONTACT);
			mainActivity.startActivity(intent);
			break;
			
		case R.id.btn_contact_search: //搜索联系人
			popSearchLayout();
			break;
			
		case R.id.btn_change: //分组 与 联系人列表 视图切换
			int cur_Screen = contact_scroller.getCurScreen();
			snapTo(cur_Screen);
			break;
			
		default:
			break;
		}
	}

	
	/**
	 * 
	 * 联系人组提醒
	 * 
	 */
	void popGroupRemind()
	{
		View view = LayoutInflater.from(mainActivity).inflate(R.layout.set_group_remind, null);
		popup_menu = new Dialog(mainActivity, R.style.theme_myDialog);
		popup_menu.setContentView(view);
		popup_menu.setCanceledOnTouchOutside(true);

		CheckBox no_remind = (CheckBox) view.findViewById(R.id.no_remind);
		// no_remind.setChecked(true);
		// ck = no_remind;

		setRemindConfirm = (Button) view
				.findViewById(R.id.set_remind_confirm);
		setRemindCancel = (Button) view
				.findViewById(R.id.set_remind_cancel);
		setRemindConfirm.setOnClickListener(this);
		setRemindCancel.setOnClickListener(this);

		MyCheckBoxOnCheckChangeListener myCheckBoxOnCheckChangeListener = new MyCheckBoxOnCheckChangeListener();
		no_remind.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox seven_day = (CheckBox) view.findViewById(R.id.seven_day);
		seven_day.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox two_week = (CheckBox) view.findViewById(R.id.two_week);
		two_week.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox one_month = (CheckBox) view.findViewById(R.id.one_month);
		one_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox two_month = (CheckBox) view.findViewById(R.id.two_month);
		two_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox three_month = (CheckBox) view
				.findViewById(R.id.three_month);
		three_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox four_month = (CheckBox) view.findViewById(R.id.four_month);
		four_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox five_month = (CheckBox) view.findViewById(R.id.five_month);
		five_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox six_month = (CheckBox) view.findViewById(R.id.six_month);
		six_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox seven_month = (CheckBox) view
				.findViewById(R.id.seven_month);
		seven_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox eight_month = (CheckBox) view
				.findViewById(R.id.eight_month);
		eight_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox nine_month = (CheckBox) view.findViewById(R.id.nine_month);
		nine_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox ten_month = (CheckBox) view.findViewById(R.id.ten_month);
		ten_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox eleven_month = (CheckBox) view
				.findViewById(R.id.eleven_month);
		eleven_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox twele_month = (CheckBox) view
				.findViewById(R.id.twele_month);
		twele_month.setOnTouchListener(myCheckBoxOnCheckChangeListener);

		CheckBox custom = (CheckBox) view.findViewById(R.id.custom);
		custom.setOnTouchListener(myCheckBoxOnCheckChangeListener);
		// }

		et_day_gap = (EditText) view.findViewById(R.id.et_day_gap);

		Cursor c = DButil.getInstance(mainActivity).querGgroupRemindId(
				cur_group_id);

		int group_remind_id = -1;
		if (c.moveToNext()) {
			group_remind_id = c
					.getInt(c
							.getColumnIndex(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID));
			cur_group_remind_id = group_remind_id;
		}

		System.out.println("  cur_group_remind_id  --->"
				+ cur_group_remind_id);

		if (group_remind_id == -1) // 无提醒
		{
			no_remind.setChecked(true);
			ck = no_remind;

			cur_group_remind_id = -1;
		} else {

			long time_gap = c.getLong(c
					.getColumnIndex(MyDatabaseUtil.CONTACT_GROUP_TIME_GAP));
			if (time_gap == (7L * 24L * 60L * 60L * 1000L)) // 7天
			{
				seven_day.setChecked(true);
				ck = seven_day;

			} else if (time_gap == (2L * 7L * 24L * 60L * 60L * 1000L)) // 2周
			{
				two_week.setChecked(true);
				ck = two_week;
			} else if (time_gap == (30L * 24L * 60L * 60L * 1000L)) // 1个月
			{
				one_month.setChecked(true);
				ck = one_month;
			} else if (time_gap == (2L * 30L * 24L * 60L * 60L * 1000L)) // 2个月
			{
				two_month.setChecked(true);
				ck = two_month;
			} else if (time_gap == (3L * 30L * 24L * 60L * 60L * 1000L)) // 3个月
			{
				three_month.setChecked(true);
				ck = three_month;
			} else if (time_gap == (4L * 30L * 24L * 60L * 60L * 1000L)) // 4个月
			{
				four_month.setChecked(true);
				ck = four_month;
			} else if (time_gap == (5L * 30L * 24L * 60L * 60L * 1000L)) // 5个月
			{
				five_month.setChecked(true);
				ck = five_month;
			} else if (time_gap == (6L * 30L * 24L * 60L * 60L * 1000L)) // 6个月
			{
				six_month.setChecked(true);
				ck = six_month;
			} else if (time_gap == (7L * 30L * 24L * 60L * 60L * 1000L)) // 7个月
			{
				seven_month.setChecked(true);
				ck = seven_month;
			} else if (time_gap == (8L * 30L * 24L * 60L * 60L * 1000L)) // 8个月
			{
				eight_month.setChecked(true);
				ck = eight_month;
			} else if (time_gap == (9L * 30L * 24L * 60L * 60L * 1000L)) // 9个月
			{
				nine_month.setChecked(true);
				ck = nine_month;
			} else if (time_gap == (10L * 30L * 24L * 60L * 60L * 1000L)) // 10个月
			{
				ten_month.setChecked(true);
				ck = ten_month;
			} else if (time_gap == (11L * 30L * 24L * 60L * 60L * 1000L)) // 11个月
			{
				eleven_month.setChecked(true);
				ck = eleven_month;
			} else if (time_gap == (12L * 30L * 24L * 60L * 60L * 1000L)) // 12个月
			{
				twele_month.setChecked(true);
				ck = twele_month;
			} else { // 自定义 (天为单位)
				et_day_gap.setText(String.valueOf(time_gap
						/ (24L * 60L * 60L * 1000L)));
				custom.setChecked(true);
				ck = custom;
			}
		}

		c.close();
		popup_menu.show();
	}
	
	private void triggerGroupRmind() {

		int position = Integer.valueOf((String) ck.getTag());

		// System.out.println(" position --->" + position);

		if (position == 0) {
			if (cur_group_remind_id == -1) {
				return;
			} else {
				Intent it = new Intent(mainActivity, AlarmReceiver.class);
				PendingIntent pit = PendingIntent.getBroadcast(mainActivity,
						MyDatabaseUtil.BASE_CONTACT_GOUP_REMIND_ID
								+ cur_group_remind_id, it, 0);
				AlarmManager amr = (AlarmManager) mainActivity
						.getSystemService(Activity.ALARM_SERVICE);
				amr.cancel(pit);// 先取消

				// 从数据库删除
				DButil.getInstance(mainActivity).deleteGroupRemind(
						cur_group_remind_id);

				// System.out.println(" 删除分组提醒   ---->   cur_group_remind_id : "
				// + cur_group_remind_id +
				// " MyDatabaseUtil.BASE_CONTACT_GOUP_REMIND_ID+cur_group_remind_id : "
				// +
				// (MyDatabaseUtil.BASE_CONTACT_GOUP_REMIND_ID+cur_group_remind_id));

				return;
			}
		}

		long time_gap = -1;

		switch (position) {

		case 0:
			// time_gap = 1000*10;
			break;

		case 1:
			time_gap = 7L * 24L * 60L * 60L * 1000L;
			break;

		case 2:
			time_gap = 2L * 7L * 24L * 60L * 60L * 1000L;
			break;

		case 3:
			time_gap = 30L * 24L * 60L * 60L * 1000L;
			break;

		case 4:
			time_gap = 2L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 5:
			time_gap = 3L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 6:
			time_gap = 4L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 7:
			time_gap = 5L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 8:
			time_gap = 6L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 9:
			time_gap = 7L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 10:
			time_gap = 8L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 11:
			time_gap = 9L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 12:
			time_gap = 10L * 30 * 24L * 60L * 60L * 1000L;
			break;

		case 13:
			time_gap = 11L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 14:
			time_gap = 12L * 30L * 24L * 60L * 60L * 1000L;
			break;

		case 15:
			long day = 1L;

			try {
				day = Long.valueOf(et_day_gap.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
			}

			time_gap = day * 24L * 60L * 60L * 1000L;
			break;

		default:
			break;
		}

		System.out.println(" time_gap ----> " + time_gap + "   |"
				+ (time_gap / (24L * 60L * 60L * 1000L)) + " 天  ");

		if (cur_group_remind_id != -1) // 更新
		{
			System.out.println(" 更新 分组提醒  ---->"+ DButil.getInstance(mainActivity).updateGroupRemind(cur_group_remind_id, cur_group_id, time_gap));

			Intent it = new Intent(mainActivity, AlarmReceiver.class);
			it.putExtra(AlarmReceiver.ALARM_TYPE,AlarmReceiver.ALARM_GROUP_REMIND);
			it.putExtra(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID,cur_group_remind_id);
			PendingIntent pit = PendingIntent.getBroadcast(mainActivity,MyDatabaseUtil.BASE_CONTACT_GOUP_REMIND_ID+ cur_group_remind_id, it, 0);
			AlarmManager amr = (AlarmManager) mainActivity.getSystemService(Activity.ALARM_SERVICE);
			// amr.cancel(pit);//先取消 ？
			amr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ time_gap, pit);

		} else { // 新建

			int new_group_remind_id = Integer.parseInt(DButil.getInstance(mainActivity).insertGroupRemind(cur_group_id, time_gap)+ "");

			System.out.println(" 插入分组提醒    id --->" + new_group_remind_id);

			Intent it = new Intent(mainActivity, AlarmReceiver.class);
			it.putExtra(AlarmReceiver.ALARM_TYPE,
					AlarmReceiver.ALARM_GROUP_REMIND);
			it.putExtra(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID,
					new_group_remind_id);
			PendingIntent pit = PendingIntent
					.getBroadcast(
							mainActivity,
							(MyDatabaseUtil.BASE_CONTACT_GOUP_REMIND_ID + new_group_remind_id),
							it, 0);
			AlarmManager amr = (AlarmManager) mainActivity
					.getSystemService(Activity.ALARM_SERVICE);
			amr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
					+ time_gap, pit);
		}
	}

	/**
	 * 
	 *  删除联系人分组提醒
	 *  
	 */
	void deleteGroupRemind() {
		Cursor c = DButil.getInstance(mainActivity).querGgroupRemindId(
				cur_group_id);

		int group_remind_id = -1;
		if (c.moveToNext()) {
			group_remind_id = c.getInt(c
					.getColumnIndex(MyDatabaseUtil.CONTACT_GROUP_REMIND_ID));
		}

//		System.out.println("  group_remind_id  --->" + group_remind_id);

		if (group_remind_id != -1) // 有提醒
		{
			// 取消
			Intent it = new Intent(mainActivity, AlarmReceiver.class);
			PendingIntent pit = PendingIntent.getBroadcast(mainActivity,
					MyDatabaseUtil.BASE_CONTACT_GOUP_REMIND_ID
							+ group_remind_id, it, 0);
			AlarmManager amr = (AlarmManager) mainActivity
					.getSystemService(Activity.ALARM_SERVICE);
			amr.cancel(pit);

			// 从数据库删除
			DButil.getInstance(mainActivity).deleteGroupRemind(group_remind_id);
			
		}
	}

	
	class MyCheckBoxOnCheckChangeListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_UP) {
				if (v != ck) {
					CheckBox c = ((CheckBox) v);
					c.setChecked(true);
					ck.setChecked(false);
					ck = c;
				}
				System.out.println(" 被选中的是  --->" + ck.getText().toString());
			}
			return true;
		}
	}

	
	// 编辑后 保存分组
	public void saveGroupAfterEdited(final String new_group_name) {

		groupProgressDialog = new ProgressDialog(mainActivity);
		groupProgressDialog.setMessage("正在保存分组信息");
		groupProgressDialog.show();

		new Thread(new Runnable() {

			@Override
			public void run() {

				isEditingGroup = true;
				
				long groupId = editGroupModeAdapter.getGroupid();

				// 组名被修改
				if (!groupNameCache.equals(new_group_name)) {
					removeGroup(groupId, new_group_name);
					groupNameCache = new_group_name;
				}
				
				long start = System.currentTimeMillis();

				ArrayList<GroupInfo> move_out = new ArrayList<GroupInfo>(); // 移出分组
				
				ArrayList<GroupInfo> move_in = new ArrayList<GroupInfo>(); // 移进分组

				boolean[] itemStatus = editGroupModeAdapter.getItemStatus();

				int size = cur_groupInfo.size();

				for (int i = 0; i < size; i++) {
					GroupInfo g = cur_groupInfo.get(i);

					if (itemStatus[i] == false && g.getGroup_id() == groupId) {
						move_out.add(g);
					} else if (itemStatus[i] == true && g.getGroup_id() == -1) {
						move_in.add(g);
					}
				}

				for (GroupInfo g : move_out) {
					dropGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(-1);
					System.out.println(" 移出： " + g.getPhone_name());
				}

				for (GroupInfo g : move_in) {
					addGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(groupId);
					System.out.println(" 移进： " + g.getPhone_name());
				}

				long end = System.currentTimeMillis();

				cur_groupInfo = getFilterGroupByGroupId(groupId);

				System.out.println(" 保存分组信息完成   耗时　　：　　－－－>" + (end - start));
				group_Handler.sendEmptyMessage(1);
			}
		}).start();

	}

	// 编辑后 保存分组
	public void saveGroupAfterEditedOnBackPress() {

		groupProgressDialog = new ProgressDialog(mainActivity);
		groupProgressDialog.setMessage("正在保存分组信息");
		groupProgressDialog.show();

		final String new_group_name = ((EditText) cur_group_title
				.findViewById(R.id.edit_group)).getText().toString();
		new Thread(new Runnable() {

			@Override
			public void run() {

				isEditingGroup = true;
				
				long groupId = editGroupModeAdapter.getGroupid();

				// 组名被修改
				if (!groupNameCache.equals(new_group_name)) {
					removeGroup(groupId, new_group_name);
					groupNameCache = new_group_name;
				}

				long start = System.currentTimeMillis();

				ArrayList<GroupInfo> move_out = new ArrayList<GroupInfo>(); // 移出分组
				ArrayList<GroupInfo> move_in = new ArrayList<GroupInfo>(); // 移进分组

				boolean[] itemStatus = editGroupModeAdapter.getItemStatus();

				int size = cur_groupInfo.size();

				for (int i = 0; i < size; i++) {
					GroupInfo g = cur_groupInfo.get(i);

					if (itemStatus[i] == false && g.getGroup_id() == groupId) {
						move_out.add(g);
					} else if (itemStatus[i] == true && g.getGroup_id() == -1) {
						move_in.add(g);
					}
				}

				for (GroupInfo g : move_out) {
					dropGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(-1);
					System.out.println(" 移除： " + g.getPhone_name());
				}

				for (GroupInfo g : move_in) {
					addGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(groupId);
					System.out.println(" 移进： " + g.getPhone_name());
				}

				long end = System.currentTimeMillis();

				cur_groupInfo = getFilterGroupByGroupId(groupId);

				System.out.println(" 保存分组信息完成   耗时　　：　　－－－>" + (end - start));
				group_Handler.sendEmptyMessage(5);
			}
		}).start();

	}

	// 编辑后 保存分组
	public void saveGroupAfterEditedPressDialing() {

		groupProgressDialog = new ProgressDialog(mainActivity);
		groupProgressDialog.setMessage("正在保存分组信息");
		groupProgressDialog.show();

		final String new_group_name = ((EditText) cur_group_title
				.findViewById(R.id.edit_group)).getText().toString();
		new Thread(new Runnable() {

			@Override
			public void run() {

				isEditingGroup = true;
				
				long groupId = editGroupModeAdapter.getGroupid();

				// 组名被修改
				if (!groupNameCache.equals(new_group_name)) {
					removeGroup(groupId, new_group_name);
					groupNameCache = new_group_name;
				}

				long start = System.currentTimeMillis();

				ArrayList<GroupInfo> move_out = new ArrayList<GroupInfo>(); // 移出分组
				ArrayList<GroupInfo> move_in = new ArrayList<GroupInfo>(); // 移进分组

				boolean[] itemStatus = editGroupModeAdapter.getItemStatus();

				int size = cur_groupInfo.size();

				for (int i = 0; i < size; i++) {
					GroupInfo g = cur_groupInfo.get(i);

					if (itemStatus[i] == false && g.getGroup_id() == groupId) {
						move_out.add(g);
					} else if (itemStatus[i] == true && g.getGroup_id() == -1) {
						move_in.add(g);
					}
				}

				for (GroupInfo g : move_out) {
					dropGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(-1);
					System.out.println(" 移除： " + g.getPhone_name());
				}

				for (GroupInfo g : move_in) {
					addGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(groupId);
					System.out.println(" 移进： " + g.getPhone_name());
				}

				long end = System.currentTimeMillis();

				cur_groupInfo = getFilterGroupByGroupId(groupId);

				System.out.println(" 保存分组信息完成   耗时　　：　　－－－>" + (end - start));

				group_Handler.sendEmptyMessage(7);
			}
		}).start();

	}

	
	public void saveGroupAfterNew(final String group_name) {

		groupProgressDialog = new ProgressDialog(mainActivity);
		groupProgressDialog.setMessage("正在保存分组信息");
		groupProgressDialog.show();

		// final String group_name = edit_group.getText().toString();

		new Thread(new Runnable() {

			@Override
			public void run() {

				isEditingGroup = true;
				
				Uri uri = addGroup(group_name);

				groupNameCache = group_name;

				Cursor cu = mainActivity.getContentResolver().query(uri, null,
						null, null, null);
				cu.moveToNext();
				long groupId = cu.getLong(cu.getColumnIndex(GroupMembership._ID));
				cu.close();

				System.out.println(" groupId --->  " + groupId);


				long start = System.currentTimeMillis();

				ArrayList<GroupInfo> move_in = new ArrayList<GroupInfo>(); // 移进分组

				boolean[] itemStatus = editGroupModeAdapter.getItemStatus();

				int size = cur_groupInfo.size();

				for (int i = 0; i < size; i++) {
					GroupInfo g = cur_groupInfo.get(i);

					if (itemStatus[i] == true) {
						move_in.add(g);
					}
				}

				for (GroupInfo g : move_in) {
					addGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(groupId);
					System.out.println(" 移进： " + g.getPhone_name());
				}

				long end = System.currentTimeMillis();

				cur_groupInfo = getFilterGroupByGroupId(groupId);

				System.out.println(" 保存分组信息完成   耗时　　：　　－－－>" + (end - start));

				GroupInfo new_groupInfo = new GroupInfo();
				new_groupInfo.setGroup_id(groupId);
				new_groupInfo.setGroup_name(new String(groupNameCache));

				int position = add_ln.getChildCount() - 2;
				groupInfos.add(position, new_groupInfo);

				cur_group_title.setTag(new_groupInfo);

				group_Handler.sendEmptyMessage(2);
			}
		}).start();

	}

	public void saveGroupAfterNewOnBackPress() {
		EditText edit_group = (EditText) cur_group_title
				.findViewById(R.id.edit_group);

		groupProgressDialog = new ProgressDialog(mainActivity);
		groupProgressDialog.setMessage("正在保存分组信息");
		groupProgressDialog.show();

		String n = edit_group.getText().toString();

		if (n.equals("")) {
			n = "未命名";
		}

		final String group_name = n;

		new Thread(new Runnable() {

			@Override
			public void run() {

				isEditingGroup = true;
				
				Uri uri = addGroup(group_name);

				groupNameCache = group_name;

				Cursor cu = mainActivity.getContentResolver().query(uri, null,
						null, null, null);
				cu.moveToNext();
				long groupId = cu.getLong(cu
						.getColumnIndex(GroupMembership._ID));
				cu.close();

				System.out.println(" groupId --->  " + groupId);

				isEditingGroup = true;

				long start = System.currentTimeMillis();

				ArrayList<GroupInfo> move_in = new ArrayList<GroupInfo>(); // 移进分组

				boolean[] itemStatus = editGroupModeAdapter.getItemStatus();

				int size = cur_groupInfo.size();

				for (int i = 0; i < size; i++) {
					GroupInfo g = cur_groupInfo.get(i);

					if (itemStatus[i] == true) {
						move_in.add(g);
						g.setGroup_id(groupId);
					}
				}

				for (GroupInfo g : move_in) {
					addGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(groupId);
					System.out.println(" 移进： " + g.getPhone_name());
				}

				long end = System.currentTimeMillis();

				cur_groupInfo = getFilterGroupByGroupId(groupId);

				System.out.println(" 保存分组信息完成   耗时　　：　　－－－>" + (end - start));

				GroupInfo new_groupInfo = new GroupInfo();
				new_groupInfo.setGroup_id(groupId);
				new_groupInfo.setGroup_name(new String(groupNameCache));

				int position = add_ln.getChildCount() - 2;
				groupInfos.add(position, new_groupInfo);

				cur_group_title.setTag(new_groupInfo);

				group_Handler.sendEmptyMessage(4);
			}
		}).start();
	}

	public void saveGroupAfterNewPressDialing() {
		EditText edit_group = (EditText) cur_group_title
				.findViewById(R.id.edit_group);

		groupProgressDialog = new ProgressDialog(mainActivity);
		groupProgressDialog.setMessage("正在保存分组信息");
		groupProgressDialog.show();

		String n = edit_group.getText().toString();

		if (n.equals("")) {
			n = "未命名";
		}

		final String group_name = n;

		new Thread(new Runnable() {

			@Override
			public void run() {

				isEditingGroup = true;
				
				Uri uri = addGroup(group_name);

				groupNameCache = group_name;

				Cursor cu = mainActivity.getContentResolver().query(uri, null,
						null, null, null);
				cu.moveToNext();
				long groupId = cu.getLong(cu
						.getColumnIndex(GroupMembership._ID));
				cu.close();

				System.out.println(" groupId --->  " + groupId);

				isEditingGroup = true;

				long start = System.currentTimeMillis();

				ArrayList<GroupInfo> move_in = new ArrayList<GroupInfo>(); // 移进分组

				boolean[] itemStatus = editGroupModeAdapter.getItemStatus();

				int size = cur_groupInfo.size();

				for (int i = 0; i < size; i++) {
					GroupInfo g = cur_groupInfo.get(i);

					if (itemStatus[i] == true) {
						move_in.add(g);
						g.setGroup_id(groupId);
					}
				}

				for (GroupInfo g : move_in) {
					addGroupContact(groupId, Long.valueOf(g.getPerson_id()));
					g.setGroup_id(groupId);
					System.out.println(" 移进： " + g.getPhone_name());
				}

				long end = System.currentTimeMillis();

				cur_groupInfo = getFilterGroupByGroupId(groupId);

				System.out.println(" 保存分组信息完成   耗时　　：　　－－－>" + (end - start));

				GroupInfo new_groupInfo = new GroupInfo();
				new_groupInfo.setGroup_id(groupId);
				new_groupInfo.setGroup_name(new String(groupNameCache));

				int position = add_ln.getChildCount() - 2;
				groupInfos.add(position, new_groupInfo);

				cur_group_title.setTag(new_groupInfo);

				group_Handler.sendEmptyMessage(6);
			}
		}).start();
	}


	/**
	 * 添加联系人 进某个分组
	 * @param groupId
	 * @param contactId
	 */
	public void addGroupContact(long groupId, long contactId) {
		
		 Cursor rawContactIdCursor = null;
		 long rawContactId = -1;
			try {
				rawContactIdCursor = mainActivity.getContentResolver().query(RawContacts.CONTENT_URI,new String[] { RawContacts._ID }, RawContacts.CONTACT_ID
								+ "=" + contactId, null, null);
				if (rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
					// Just return the first one.
					rawContactId = rawContactIdCursor.getLong(0);
				}
			} finally {
				if (rawContactIdCursor != null) {
					rawContactIdCursor.close();
				}
			}
		
		ContentValues values = new ContentValues();
		values.put(Data.RAW_CONTACT_ID,rawContactId);
		values.put(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE);
		values.put(GroupMembership.GROUP_ROW_ID, groupId);
		
		mainActivity.getContentResolver().insert(Data.CONTENT_URI, values);
	}

	
	/**
	 * 将联系人重某个分组 移出
	 * @param groupId
	 * @param contactId
	 */
	public void dropGroupContact(long groupId, long contactId) {

		Cursor rawContactIdCursor = null;
		long rawContactId = -1;
		try {
			rawContactIdCursor = mainActivity.getContentResolver().query( RawContacts.CONTENT_URI, new String[] { RawContacts._ID },
					RawContacts.CONTACT_ID + "=" + contactId, null, null);
			if (rawContactIdCursor != null && rawContactIdCursor.moveToFirst()) {
				// Just return the first one.
				rawContactId = rawContactIdCursor.getLong(0);
			}
		} finally {
			if (rawContactIdCursor != null) {
				rawContactIdCursor.close();
			}
		}

		Cursor aggregationCursor = mainActivity.getContentResolver().query(AggregationExceptions.CONTENT_URI,null,
				AggregationExceptions.RAW_CONTACT_ID1 + " = ? or "
						+ AggregationExceptions.RAW_CONTACT_ID2 + " = ? ",
				new String[] { String.valueOf(rawContactId),
						String.valueOf(rawContactId) }, null);
		int count = aggregationCursor.getCount();

		String where = null;
		String[] selectionArgs = null;

		if (count > 0) {// 该机录与其他记录有聚合(Aggregation)
			// 由于在queryForRawContactId()方法中我们是根据contactId查rawContactId,且只返回了第一个
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < count; i++) {
				aggregationCursor.moveToPosition(i);
				sb.append(aggregationCursor.getLong(aggregationCursor
						.getColumnIndex(AggregationExceptions.RAW_CONTACT_ID2)));
				sb.append(',');
			}
			sb.append(rawContactId);

			//
			where = GroupMembership.GROUP_ROW_ID + " = ? " + " AND "
					+ Data.MIMETYPE + " = ? " + " AND " + Data.RAW_CONTACT_ID
					+ " in ( " + sb.toString() + " ) ";
			selectionArgs = new String[] { String.valueOf(groupId),
					GroupMembership.CONTENT_ITEM_TYPE };
		} else {// 该机录没有聚合(Aggregation)
			where = Data.RAW_CONTACT_ID + " = ? " + " AND " + Data.MIMETYPE+ " = ? " + " AND " + GroupMembership.GROUP_ROW_ID
					+ " = ? ";
			selectionArgs = new String[] { String.valueOf(rawContactId),GroupMembership.CONTENT_ITEM_TYPE, String.valueOf(groupId) };
		}

		aggregationCursor.close();

		mainActivity.getContentResolver().delete(Data.CONTENT_URI, where,selectionArgs);
	}

	/**
	 * 重命名 联系人分组
	 * @param groupId
	 * @param newName
	 */
	public void removeGroup(long groupId, String newName) {
		Uri uri = ContentUris.withAppendedId(Groups.CONTENT_URI, groupId);
		ContentValues values = new ContentValues();
		values.put(Groups.TITLE, newName);
		mainActivity.getContentResolver().update(uri, values, null, null);
	}

	/**
	 * 删除 联系人分组
	 * @param groupId
	 */
	public void dropGroup(long groupId) {
		mainActivity.getContentResolver().delete(
				Uri.parse(Groups.CONTENT_URI + "?"
						+ ContactsContract.CALLER_IS_SYNCADAPTER + "=true"),
				Groups._ID + "=" + groupId, null);

	}

	/**
	 * 新增  联系人分组
	 * @param newName
	 * @return
	 */
	public Uri addGroup(String newName) {
		
		ContentValues values = new ContentValues();
		values.put(Groups.TITLE, newName);
		return mainActivity.getContentResolver().insert(Groups.CONTENT_URI,values);
		
	}

	class OnMenuItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (contactsAdapter.menu != null) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -contactsAdapter.menu.getHeight());

				contactsAdapter.menu.setLayoutParams(lp);
				contactsAdapter.menu.setVisibility(View.GONE);
			}

			switch (v.getId()) {
			case R.id.menu_call:

				String number = (String) v.getTag();

				if (number != null && !number.equals("")) {
					System.out.println(" number --> " + number);

					Intent intent = new Intent(Intent.ACTION_CALL,
							Uri.parse("tel:" + number));
					mainActivity.startActivity(intent);
				} else {
					Toast.makeText(mainActivity, "此联系人不存在号码",
							Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.menu_sms_detail:

				String my_number = (String) v.getTag();

				if (my_number != null && !my_number.equals("")) {
					
					
					String thread_id = NewMessageActivity.queryThreadIdByNumber(mainActivity, my_number);

					Intent intent = new Intent(mainActivity, NewMessageActivity.class);
					intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
					intent.putExtra(NewMessageActivity.DATA_NUMBER, my_number);
							
					mainActivity.startActivity(intent);
					
				} else {
					Toast.makeText(mainActivity, "此联系人不存在号码",Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.menu_contact_detail:

				String contact_id = (String) v.getTag();
				System.out.println(" menu_contact_detail ::contactId ---> "
						+ contact_id);

				goToContactDetail(contact_id);
				
				break;

			case R.id.menu_delete:

				System.out.println("menu_delete ");
				String s = (String) v.getTag();

				System.out.println(" s ---> " + s);
				String[] sprits = s.split(",");

				String[] ns = sprits[0].split(":"); // 包含两个位置，一个父位置，一个子位置。
													// 如果子位置为-1，说明删除的是父类，则删除整个联系人,
													// 如果子位置不为-1，则说明删除的是具体的某一个号码.

				selected_delete_parent_position = Integer.valueOf(ns[0]);
//				selected_delete_child_position = Integer.valueOf(ns[1]);
				selected_delete_child_position = -1 ; //直接删除联系人

				System.out.println(" selected_delete_parent_position --->  " + selected_delete_parent_position);
				System.out.println(" selected_delete_child_position --->  " + selected_delete_child_position);

				selected_delete_contact_id = sprits[1];
				String selected_delete_name = sprits[2];

				System.out.println("  position --->  " + selected_delete_parent_position);
				System.out.println(" contactId  ---> " + selected_delete_contact_id);
				String tips = "确定删除联系人  " + selected_delete_name + " ?"; 

				SharedPreferences ss = mainActivity.getSharedPreferences("myNumberContactId", 0);
				long contactId = ss.getLong("myContactId", -1);

				if (Long.valueOf(sprits[1]) == contactId) {
					Toast.makeText(mainActivity, "机主号码无法删除", Toast.LENGTH_SHORT).show();
				} else {

					myDialog = new MyDialog(mainActivity, tips,new DialogOnClickListener());
					myDialog.normalDialog();
				}

				break;

			case R.id.menu_remind:

				String contact_id_1 = (String) v.getTag();
				System.out.println(" menu_contact_detail ::contactId ---> "+ contact_id_1);

				Intent intent = new Intent(mainActivity, AddContactActivity.class);
				intent.putExtra(AddContactActivity.DATA_TYPE, AddContactActivity.TYPE_DETAIL_CONTACT);
				intent.putExtra(AddContactActivity.DATA_CONTACT_ID, contact_id_1);
				intent.putExtra(AddContactActivity.DATA_GO_REMIND, true);
				mainActivity.startActivity(intent);
				
				break;

			case R.id.menu_add_to:

				add_to_number = (String) v.getTag();

				System.out.println("  add_to_number --->" + add_to_number);

				if (dialog_add_to == null) {
					dialog_add_to = new Dialog(mainActivity,R.style.theme_myDialog);
					dialog_add_to.setContentView(R.layout.dialog_home_add_to);
					dialog_add_to.setCanceledOnTouchOutside(true);

					PopAddToClickListener popAddToClickListener = new PopAddToClickListener();
					((Button) dialog_add_to.findViewById(R.id.add_to_new)).setVisibility(View.GONE);
					((Button) dialog_add_to.findViewById(R.id.add_to_already)).setVisibility(View.GONE);
					((Button) dialog_add_to.findViewById(R.id.add_to_black)).setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_white)).setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_desktop)).setOnClickListener(popAddToClickListener);
				}

				dialog_add_to.show();

				break;

			default:
				break;
			}
		}
	}

	class OnSearchAdapterMenuItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (contactsSearchAdapter.menu != null) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -contactsSearchAdapter.menu.getHeight());

				contactsSearchAdapter.menu.setLayoutParams(lp);
				contactsSearchAdapter.menu.setVisibility(View.GONE);
			}

			switch (v.getId()) {
			case R.id.menu_call:

				String number = (String) v.getTag();

				System.out.println(" number  --->" + number);

				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ number));
				mainActivity.startActivity(intent);

				break;

			case R.id.menu_sms_detail:

				String number2 = (String) v.getTag();
				
				String thread_id = NewMessageActivity.queryThreadIdByNumber(mainActivity, number2);

				Intent m_intent = new Intent(mainActivity, NewMessageActivity.class);
				m_intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
				m_intent.putExtra(NewMessageActivity.DATA_NUMBER, number2);
						
				mainActivity.startActivity(m_intent);
				
				break;

			case R.id.menu_contact_detail:

				String contact_id = (String) v.getTag();
				System.out.println(" menu_contact_detail ::contactId ---> "
						+ contact_id);

				goToContactDetail(contact_id);
				
				break;

			case R.id.menu_delete:

				System.out.println("menu_delete ");
				String s = (String) v.getTag();

				System.out.println(" s ---> " + s);
				String[] sprits = s.split(",");

				String[] ns = sprits[0].split(":"); // 包含两个位置，一个父位置，一个子位置。
													// 如果子位置为-1，说明删除的是父类，则删除整个联系人,
													// 如果子位置不为-1，则说明删除的是具体的某一个号码.

				selected_delete_parent_position = Integer.valueOf(ns[0]);
				selected_delete_child_position = Integer.valueOf(ns[1]);

				System.out.println(" selected_delete_parent_position --->  "
						+ selected_delete_parent_position);
				System.out.println(" selected_delete_child_position --->  "
						+ selected_delete_child_position);

				selected_delete_contact_id = sprits[1];
				String selected_delete_name = sprits[2];

				System.out.println("  position --->  "
						+ selected_delete_parent_position);
				System.out.println(" contactId  ---> "
						+ selected_delete_contact_id);
				// System.out.println(" selected_delete_lookup_key  ---> " +
				// selected_delete_lookup_key);
				String tips = "";
				if (selected_delete_child_position == -1) {
					tips = "确定删除联系人  " + selected_delete_name
							+ " ?" ;
				} else {
					tips = "确定删除号码  "
									+ contactsSearchAdapter
											.getContactinfoList()
											.get(selected_delete_parent_position)
											.getNumberlist()
											.get(selected_delete_child_position)
									+ " ?" ;
				}

				SharedPreferences ss = mainActivity.getSharedPreferences(
						"myNumberContactId", 0);
				
				long contactId = ss.getLong("myContactId", -1);

				if (Long.valueOf(sprits[1]) == contactId) {
					Toast.makeText(mainActivity, "机主号码无法删除", Toast.LENGTH_SHORT)
							.show();
				} else {

					myDialog = new MyDialog(mainActivity, tips,
							new DialogOnClickListener());
					myDialog.normalDialog();
				}

				break;

			case R.id.menu_remind:

				String contact_id_1 = (String) v.getTag();
				System.out.println(" menu_contact_detail ::contactId ---> "
						+ contact_id_1);

				Intent intent1 = new Intent(mainActivity, AddContactActivity.class);
				intent1.putExtra(AddContactActivity.DATA_TYPE, AddContactActivity.TYPE_DETAIL_CONTACT);
				intent1.putExtra(AddContactActivity.DATA_CONTACT_ID, contact_id_1);
				intent1.putExtra(AddContactActivity.DATA_GO_REMIND, true);
				mainActivity.startActivity(intent1);
				
				break;

			case R.id.menu_add_to:

				add_to_number = (String) v.getTag();

				if (dialog_add_to == null) {
					dialog_add_to = new Dialog(mainActivity,
							R.style.theme_myDialog);
					dialog_add_to.setContentView(R.layout.dialog_home_add_to);
					dialog_add_to.setCanceledOnTouchOutside(true);

					PopAddToClickListener popAddToClickListener = new PopAddToClickListener();
					((Button) dialog_add_to.findViewById(R.id.add_to_new))
							.setVisibility(View.GONE);
					((Button) dialog_add_to.findViewById(R.id.add_to_already))
							.setVisibility(View.GONE);
					((Button) dialog_add_to.findViewById(R.id.add_to_black))
							.setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_white))
							.setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_desktop))
							.setOnClickListener(popAddToClickListener);
				}

				dialog_add_to.show();

				break;

			default:
				break;
			}
		}
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
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

			if (contactsSearchAdapter != null && !s.toString().equals("")) {
				final String ss = new String(s.toString());
				new Thread(new Runnable() {
					@Override
					public void run() {
						contactsSearchAdapter.filter(ss);
						// System.out.println("------ 联想   刷新  -----");
					}
				}).start();
			} else if (s.toString().equals("")) {
				lv_search.setVisibility(View.GONE);
			}
		}
	};

	public void triggerCall(String number) {
		System.out.println(" number  -->" + number);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));
		mainActivity.startActivity(intent);
	}

	public void triggerSms(String number) {

		number = PhoneNumberTool.cleanse(number);
		
		String thread_id = NewMessageActivity.queryThreadIdByNumber(mainActivity, number);

		Intent intent = new Intent(mainActivity, NewMessageActivity.class);
		intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
		intent.putExtra(NewMessageActivity.DATA_NUMBER, number);
				
		mainActivity.startActivity(intent);
		
	}

	public void setTopButton(Button btn_search, Button btn_add_contact) {
		this.btn_search = btn_search;
		this.btn_add_contact = btn_add_contact;
	}

	public void snapTo(int curScreen) {
		if (curScreen == 0) // 滑动至 全部联系人界面
		{

			if (dataSaveCancel.getVisibility() == View.VISIBLE) {
				long groupId = editGroupModeAdapter.getGroupid();

				if (groupId == MY_GOUTP_TITLE_ID_ADD) // 新建分组
				{
					saveGroupAfterNewOnBackPress();
				} else { // 修改分组
					saveGroupAfterEditedOnBackPress();
				}

			} else {
				setViewToAllContact();
			}

		} else if (curScreen == 1) // 滑动至 联系人分组界面
		{
			if (isLoadFinish) {
				contact_scroller.snapToScreen(0);

				indexBar.setVisibility(View.GONE);
				btn_search.setVisibility(View.GONE);
				btn_add_contact.setVisibility(View.GONE);

				if (all_group_contact_infos == null) {
					initGroupLayout(true);
				}
			}
			
			 //设置标题
			 if(cur_group_title!=null)
			 {
				GroupInfo info = (GroupInfo)cur_group_title.getTag();
				if(info.getGroup_id()==MY_GROUP_TITLE_ID_ALL)
				{
					tv_top.setText("全部联系人");
				}else{
					String title =  ((Button)cur_group_title.findViewById(R.id.button_item)).getText().toString();
					int indxe = title.indexOf("(");
					tv_top.setText(title.substring(0, indxe));
				}
			 }
		}
	}
	
	void setViewToAllContact()
	{
		contact_scroller.snapToScreen(1);
		
		sf = mainActivity.getSharedPreferences(SF_NAME, 0);

		sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

		if (sort == 1) {
			// if ( indexBar.getVisibility() == View.GONE)
			indexBar.setVisibility(View.VISIBLE);

		} else {

			// if ( indexBar.getVisibility() == View.VISIBLE)
			indexBar.setVisibility(View.GONE);

		}
		
		btn_search.setVisibility(View.VISIBLE);
		btn_add_contact.setVisibility(View.VISIBLE);
		
		tv_top.setText("全部联系人");
	}

	/**
	 * 
	 * @param isShowProgressDialog
	 *            是否显示加载框
	 */
	void initGroupLayout(boolean isShowProgressDialog) {
		
		if (isShowProgressDialog) {
			groupProgressDialog = new ProgressDialog(mainActivity);
			groupProgressDialog.setMessage("正在载入分组");
			groupProgressDialog.show();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				all_group_contact_infos = getAllContactInfo();
				groupInfos = getContactGroup();

				group_Handler.sendEmptyMessage(0);
			}
		}).start();

	}

	Handler group_Handler = new Handler() {
		
		
		public void handleMessage(Message msg) {
			
			if (groupProgressDialog != null && groupProgressDialog.isShowing()) {
				groupProgressDialog.dismiss();
			}

			int no_gourp_count = 0;
			for(GroupInfo g_m :all_group_contact_infos)
			{
				if(g_m.getGroup_id() == -1)
				{
					no_gourp_count ++;
				}
			}
			
			switch (msg.what) {
			
			case 0: // 第一次进入 或   当联系数据库改变时重新刷新分组(新增分组时也会触发联系人数据库的改变)
				
				layouGroupsBar();

//				System.out.println(" group_index --- " + group_index);
//				System.out.println(" add_ln.getChildCount()-2  --->" + (add_ln.getChildCount()-2));
				
				if(group_index<=add_ln.getChildCount()-2)
				{
					if(group_index==0)
					{
						cur_groupInfo = all_group_contact_infos;
					}else{
						GroupInfo cur_g = groupInfos.get(group_index);
						cur_groupInfo = getFilterGroupByGroupId(cur_g.getGroup_id());
					}
				}else {
					group_index = 0;
					cur_groupInfo = all_group_contact_infos;
				}
				
				// 选中
				cur_group_title = (FrameLayout) add_ln.getChildAt(group_index);
				cur_group_title.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
				cur_group_title.findViewById(R.id.remove_ln).setBackgroundResource(R.drawable.add_button_bg_selected);
				
				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);
				
				if(group_index!=0 && group_index!= add_ln.getChildCount()-2)
				{
					// 点击正常分组 显示下方的编辑按钮
					editButton.setVisibility(View.VISIBLE);
				}
				
				break;

			case 1:// 修改了分组信息

				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置回正常的布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title.findViewById(R.id.ln_edit_group_title)).setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				// 更新分组名字
				((Button) cur_group_title.findViewById(R.id.button_item)).setText(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				GroupInfo g1 = (GroupInfo) cur_group_title.getTag();
				g1.setGroup_name(new String(groupNameCache)+"("+cur_groupInfo.size()+")");
				
				//刷新未分组数字
				((Button)add_ln.getChildAt(add_ln.getChildCount()-2).findViewById(R.id.button_item)).setText("未分组("+no_gourp_count+")");
				
				//更新标题
				tv_top.setText(groupNameCache);
				
				isEditingGroup = false;

				break;

			case 2:// 新建分组

				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置回正常的布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title
						.findViewById(R.id.ln_edit_group_title))
						.setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				GroupInfo g2 = (GroupInfo) cur_group_title.getTag();
				g2.setGroup_name(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				Button button_item = (Button) cur_group_title.findViewById(R.id.button_item);

				button_item.setText(new String(groupNameCache)+"("+cur_groupInfo.size()+")"); // 更新分组名字
				button_item.setOnClickListener(new GroupTitleClickListener(
						cur_group_title, g2)); // 重新设置监听

				//刷新未分组数字
				((Button)add_ln.getChildAt(add_ln.getChildCount()-2).findViewById(R.id.button_item)).setText("未分组("+no_gourp_count+")");
				
				//更新标题
				tv_top.setText(groupNameCache);
				
				isEditingGroup = false;

				break;

			case 3: // 删除分组

				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title
						.findViewById(R.id.ln_edit_group_title))
						.setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				add_ln.removeView(cur_group_title);

				int position = 0;

				cur_group_title = (FrameLayout) add_ln.getChildAt(position);

				cur_groupInfo = all_group_contact_infos;

				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置背景
				cur_group_title.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
				cur_group_title.findViewById(R.id.remove_ln).setBackgroundResource(R.drawable.add_button_bg_selected);

				// 重置布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title
						.findViewById(R.id.ln_edit_group_title))
						.setVisibility(View.GONE);

				editButton.setVisibility(View.GONE);

				//刷新未分组数字
				((Button)add_ln.getChildAt(add_ln.getChildCount()-2).findViewById(R.id.button_item)).setText("未分组("+no_gourp_count+")");
				
				//更新标题
				tv_top.setText(groupNameCache);
				
				isEditingGroup = false;
				break;

			case 4: // 新建分组时，点击了返回

				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置回正常的布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title.findViewById(R.id.ln_edit_group_title)).setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				GroupInfo g3 = (GroupInfo) cur_group_title.getTag();
				g3.setGroup_name(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				Button button_item3 = (Button) cur_group_title
						.findViewById(R.id.button_item);

				button_item3.setText(new String(groupNameCache)+"("+cur_groupInfo.size()+")"); // 更新分组名字
				button_item3.setOnClickListener(new GroupTitleClickListener(
						cur_group_title, g3)); // 重新设置监听

				setViewToAllContact();

				isEditingGroup = false;
				break;

			case 5: // 编辑分组时 ，点击了返回

				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置回正常的布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title.findViewById(R.id.ln_edit_group_title)).setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				// 更新分组名字
				((Button) cur_group_title.findViewById(R.id.button_item)).setText(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				GroupInfo g5 = (GroupInfo) cur_group_title.getTag();
				g5.setGroup_name(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				setViewToAllContact();

				//刷新未分组数字
				((Button)add_ln.getChildAt(add_ln.getChildCount()-2).findViewById(R.id.button_item)).setText("未分组("+no_gourp_count+")");
				isEditingGroup = false;
				
				break;

			case 6: // 新建分组时 点了拨号按钮

				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置回正常的布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title
						.findViewById(R.id.ln_edit_group_title))
						.setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				GroupInfo g4 = (GroupInfo) cur_group_title.getTag();
				g4.setGroup_name(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				Button button_item4 = (Button) cur_group_title.findViewById(R.id.button_item);

				button_item4.setText(new String(groupNameCache)+"("+cur_groupInfo.size()+")"); // 更新分组名字
				button_item4.setOnClickListener(new GroupTitleClickListener(
						cur_group_title, g4)); // 重新设置监听

				//刷新未分组数字
				((Button)add_ln.getChildAt(add_ln.getChildCount()-2).findViewById(R.id.button_item)).setText("未分组("+no_gourp_count+")");
				
				//更新标题
				tv_top.setText(groupNameCache);
				
				goBack();

				contact_scroller.postDelayed(new Runnable() {

					@Override
					public void run() {
						contact_scroller.setToScreen(1);
						sf = mainActivity.getSharedPreferences(SF_NAME, 0);

						sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

						if (sort == 1) {
							indexBar.setVisibility(View.VISIBLE);

						} else {
							indexBar.setVisibility(View.GONE);
						}
						btn_search.setVisibility(View.VISIBLE);
						btn_add_contact.setVisibility(View.VISIBLE);
						
					}
				}, 1000);

				isEditingGroup = false;

				break;

			case 7: // 编辑分组时 ，点击了拨号按钮

				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 重置回正常的布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title
						.findViewById(R.id.ln_edit_group_title))
						.setVisibility(View.GONE);
				editButton.setVisibility(View.VISIBLE);

				// 更新分组名字
				((Button) cur_group_title.findViewById(R.id.button_item)).setText(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				GroupInfo g7 = (GroupInfo) cur_group_title.getTag();
				g7.setGroup_name(new String(groupNameCache)+"("+cur_groupInfo.size()+")");

				setViewToAllContact();

				//刷新未分组数字
				((Button)add_ln.getChildAt(add_ln.getChildCount()-2).findViewById(R.id.button_item)).setText("未分组("+no_gourp_count+")");
				
				//更新标题
				tv_top.setText(groupNameCache);
				
				goBack();

				contact_scroller.postDelayed(new Runnable() {

					@Override
					public void run() {
						contact_scroller.setToScreen(1);
						sf = mainActivity.getSharedPreferences(SF_NAME, 0);

						sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

						if (sort == 1) {
							// if ( indexBar.getVisibility() == View.GONE)
							indexBar.setVisibility(View.VISIBLE);

						} else {

							// if ( indexBar.getVisibility() == View.VISIBLE)
							indexBar.setVisibility(View.GONE);

						}
						btn_search.setVisibility(View.VISIBLE);
						btn_add_contact.setVisibility(View.VISIBLE);
					}
				}, 1000);

				isEditingGroup = false;

				break;

			case 8:
				break;

			case 9:
				break;

			default:
				break;
			}

		};
	};

	class PopAddToClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_to_black:

//				Long id = getContactId(add_to_number);
//				saveBlack(add_to_number);
				
				int count = addBlackWhite.saveBlack(add_to_number);
				if (count  > 0 ) {

					Toast.makeText(mainActivity, "黑名单中存在该联系人！", Toast.LENGTH_SHORT).show();
				}
				
				break;

			case R.id.add_to_white:

//				Long idd = getContactId(add_to_number);
//				saveWhite(add_to_number);
				
				int count1 = addBlackWhite.saveWhite(add_to_number);
				if (count1  > 0 ) {

					Toast.makeText(mainActivity, "白名单中存在该联系人！", Toast.LENGTH_SHORT).show();
				}
				
				break;

			case R.id.add_to_desktop:

				System.out.println(" add_to_number ---->" + add_to_number);

				if (add_to_number == null ) {
					Toast.makeText(mainActivity, "号码为空无法添加至桌面！", Toast.LENGTH_SHORT).show();
					return;
				}
				
				Long contact_id = getContactId( PhoneNumberTool.cleanse(add_to_number));

				ContactBean contactBean = getContactInfo(contact_id,PhoneNumberTool.cleanse(add_to_number));

				ContactLauncherDBHelper contactlauncher = new ContactLauncherDBHelper(
						mainActivity);

				SQLiteDatabase db = contactlauncher.getReadableDatabase();

				// Cursor launcher = db.query(contactlauncher.table, null,
				// contactlauncher.CONTACTNAME+"= '"+contactBean.getNick()+"'",
				// null, null, null, null);
				Cursor launcher = db.query(contactlauncher.table, null, null,
						null, null, null, null);

				if (launcher.getCount() == 6) {

					Toast.makeText(mainActivity, "超过最大添加数量，无法进行此操作！",
							Toast.LENGTH_SHORT).show();
					return;
				} else {

					Cursor launchercursor = db.query(contactlauncher.table,
							null, contactlauncher.CONTACTNAME + "= '"
									+ contactBean.getNick() + "'", null, null,
							null, null);

					if (launchercursor.getCount() == 0) {

						db = contactlauncher.getWritableDatabase();

						ContentValues values = new ContentValues();
						values.put(contactlauncher.CONTACTNAME,
								contactBean.getNick());
						values.put(contactlauncher.NUMBER, add_to_number);
						values.put(contactlauncher.PHOTO, contactBean.getPhoto());
						
						db.insert(contactlauncher.table, contactlauncher._ID, values);
					}

					launchercursor.close();

				}

				db.close();

				launcher.close();

				Intent widget = new Intent();
				widget.setAction("com.dongji.app.ui.appwidget.refresh");
				mainActivity.sendBroadcast(widget);

				break;

			default:
				break;
			}

			dialog_add_to.dismiss(); // 对话框 消失
		}
	}

	
	private Long getContactId(String number) {
		List<ContactBean> list = new ArrayList<ContactBean>();

		Long id = null;
		Cursor phones = mainActivity.getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		if (phones.moveToFirst()) {
			do {

				String phonenumber = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				Long contact_id = phones.getLong(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

				ContactBean contactBean = new ContactBean();
				contactBean.setNumber(phonenumber);
				contactBean.setContact_id(contact_id);
				list.add(contactBean);

			} while (phones.moveToNext());
		}
		phones.close();

		for (int i = 0; i < list.size(); i++) {
			ContactBean contactBean = (ContactBean) list.get(i);

			String telephone = contactBean.getNumber();
			if (telephone.equals(number))
				id = contactBean.getContact_id();
		}
		return id;
	}

	private ContactBean getContactInfo(Long id, String number) {

		Cursor cursor = mainActivity.getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI, null,
				ContactsContract.Contacts._ID + "=" + id, null,
				ContactsContract.Contacts._ID + " desc");
		ContactBean contactBean = new ContactBean();
		if (cursor.moveToFirst()) {

			contactBean.setContact_id(id);
			contactBean.setNick(cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
			contactBean.setPhoto_id(cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
			contactBean.setNumber(number);

			String photo_id = contactBean.getPhoto_id();
			if (photo_id != null) {
				Cursor photo = mainActivity.getContentResolver().query(
						ContactsContract.Data.CONTENT_URI,
						new String[] { ContactsContract.Contacts.Data.DATA15 },
						"ContactsContract.Data._ID = " + photo_id, null, null);
				if (photo.moveToNext()) {
					byte[] photoicon = photo
							.getBlob(photo
									.getColumnIndex(ContactsContract.Contacts.Data.DATA15));
					if (photoicon != null)
						contactBean.setPhoto(photoicon);
				}
				photo.close();
			}
		}
		cursor.close();
		return contactBean;
	}


	
	public void refresh() {

		sf = mainActivity.getSharedPreferences(SF_NAME, 0);

		sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

		if (sort == 1 && contact_scroller.getCurScreen()==1) {
			indexBar.setVisibility(View.VISIBLE);
		} else {
			indexBar.setVisibility(View.GONE);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {

				long start = System.currentTimeMillis();

				List<ContactBean> all_contacts = new ArrayList<ContactBean>();

				String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";

				ContentResolver contentResolver = mainActivity.getContentResolver();
				Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, sortOrder);

				int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
				int photo_column = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
				int nick_column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
				int sort_key_column = cursor.getColumnIndex("sort_key");
				int phone_count = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);


				ContactBean my_contactBean = null ; //机主bean
				
				while (cursor.moveToNext()) {

					
					long contact_id = cursor.getLong(id_column);

						
						String key = cursor.getString(sort_key_column);

						int phone_num = cursor.getInt(phone_count); //是否有号码

						boolean isCheck = true; //此联系人是否有效
						
						
						long c_id  = cursor.getLong(id_column);
						if(phone_num ==0 &&  c_id != MainActivity.MY_CONTACT_ID)  //没有电话号码，并且不是机主
						{
							isCheck = false;
						}
						
						if (key != null && isCheck ) // 没有sortkey ，则为空的联系人 
						{
							try {
								key = key.replace(" ", "");
								String array = "";
								boolean b = false;
								String capPingYin = "";

								for (int i = 0; i < key.length(); i++) {
									char c = key.charAt(i);

									if (c > 256) {// 汉字符号
										b = false;
									} else {
										array += c;
										if (!b) {
											capPingYin += c;
											b = true;
										}
									}
								}

								ContactBean contactBean = new ContactBean();

								contactBean.setContact_id(cursor.getLong(id_column));
								contactBean.setPhoto_id(cursor.getString(photo_column));
								contactBean.setNick(cursor.getString(nick_column));

								contactBean.setSork_key(key.replace(" ", "").toLowerCase());
								contactBean.setName_pinyin(array.replace(" ", "").toLowerCase());
								contactBean.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());
								
								if(cursor.getLong(id_column) == MainActivity.MY_CONTACT_ID) //机主置顶
								{
									my_contactBean = contactBean;
									all_contacts.add(0,contactBean);
								}else{
									all_contacts.add(contactBean);
								}
								
								
							} catch (Exception ex) {
								ex.getStackTrace();
							}
						}

				}
				cursor.close();

				
				if (sort == 0) {

					ContactLauncherDBHelper dbHelpler = new ContactLauncherDBHelper(mainActivity);
					SQLiteDatabase db = dbHelpler.getReadableDatabase();

					Cursor heat = db.query(dbHelpler.heat_table,new String[] { dbHelpler.CONTACT_ID }, null, null,null, null, dbHelpler.HEAT + " desc");

					System.out.println(" contacts -----> " + all_contacts.size());

					List<ContactBean> new_all = new ArrayList<ContactBean>();

					if (heat.moveToFirst()) {

						do {

							long contact_id = heat.getInt(heat.getColumnIndex(dbHelpler.CONTACT_ID));

							for (ContactBean c : all_contacts) {
								if (c.getContact_id() == contact_id) {
									new_all.add(c);
								}
							}
						} while (heat.moveToNext());

						all_contacts.removeAll(new_all);
						new_all.addAll(all_contacts);

						all_contacts = new_all;
					}

					heat.close();

					if(my_contactBean!=null)
					{
						new_all.remove(my_contactBean);
						new_all.add(0, my_contactBean);
					}

				}

				contacts = all_contacts;
				gourp_refresh_handler.sendEmptyMessage(0);

				long end = System.currentTimeMillis();
				//
//				System.out.println(" 更新全部联系人列表完成    耗时:  --->" + (end - start));
				
			}
		}).start();

	}

	Handler gourp_refresh_handler = new Handler() {

		public void handleMessage(Message msg) {
			
			isNeedRefresh = false;
			
			if (contactsAdapter != null) {
				contactsAdapter.reBindDateSet(contacts,sort);
			}

			if (contactsSearchAdapter != null) {
				contactsSearchAdapter.reBindDateSet(contacts);
			}

			// 刷新分组信息
			if (all_group_contact_infos != null) {
				initGroupLayout(false);
			}
			
		};
	};

	class DialogOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.btn_top_tips_yes:

				myDialog.closeDialog();

//				mainActivity.l_scrolelr.snapToScreen(0);

				progressDialog = new ProgressDialog(mainActivity);
				progressDialog.setMessage("正在删除，请稍后");
				progressDialog.show();

				if (selected_delete_child_position == -1) // 删除整个联系人
				{

					new Thread(new Runnable() {

						@Override
						public void run() {

							// mainActivity.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI,
							// ContactsContract.Data._ID +" = " +
							// selected_delete_contact_id, null);

							Cursor tmpCursor = mainActivity.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,null,Contacts._ID + "=?",new String[] { String.valueOf(selected_delete_contact_id) },null);
							//
							tmpCursor.moveToNext();

							long contactId = tmpCursor.getLong(tmpCursor.getColumnIndex(Contacts._ID));
							String lookupKey = tmpCursor.getString(tmpCursor.getColumnIndex(Contacts.LOOKUP_KEY));//查询键，代替ContactsContract.Data._ID

							tmpCursor.close();

							int delete = mainActivity.getContentResolver().delete(Contacts.getLookupUri(contactId,lookupKey), null, null);

							System.out.println(" delete ---> " + delete);

							handler.sendEmptyMessage(2);
							
						}
					}).start();

				} else { // 删除联系人的某个号码

					new Thread(new Runnable() {

						@Override
						public void run() {
							String delete_number;
							if (ln_search.getVisibility() != View.VISIBLE) {
								delete_number = contacts.get(selected_delete_parent_position).getNumberlist().get(selected_delete_child_position);
							} else {
								delete_number = contactsSearchAdapter.getContactinfoList().get(selected_delete_parent_position).getNumberlist().get(selected_delete_child_position);
							}

							System.out.println(" delete_number  ---> "+ delete_number);

							Cursor phones = mainActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = "+ selected_delete_contact_id+ " AND "+ ContactsContract.CommonDataKinds.Phone.NUMBER+ " = '"+ delete_number+ "'", null, null);
							if (phones.moveToNext()) {
								long data_id = phones.getLong(phones.getColumnIndex(Data._ID));
								int delete = mainActivity.getContentResolver().delete(Data.CONTENT_URI,Data._ID + "=?",new String[] { String.valueOf(data_id) });
								System.out.println(" data_id  --->" + data_id+ " delete ---> " + delete);
							}
							phones.close();

							handler.sendEmptyMessage(2);
							
						}
					}).start();
				}
				break;

			default:
				break;
			}
		}
	}

	// 编辑分组按钮的点击监听
	class EditGroupClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			GroupInfo info = (GroupInfo) cur_group_title.getTag();

			String source = info.getGroup_name();
			int start_index = source.indexOf("(");
			groupNameCache = source.substring(0, start_index);

			System.out.println("  groupNameCache  ---> " + groupNameCache);
			
			dataSaveCancel.setVisibility(View.VISIBLE);
			editButton.setVisibility(View.GONE);

			final long groupid = info.getGroup_id();
			LinearLayout ln_edit_group_title = (LinearLayout) cur_group_title.findViewById(R.id.ln_edit_group_title);
			ln_edit_group_title.setVisibility(View.VISIBLE);

			final EditText editRemoveGroup = (EditText) ln_edit_group_title.findViewById(R.id.edit_group);
			editRemoveGroup.setText(groupNameCache);
			// 进入编辑模式 把未分组的添加进当前分组的列表中
			cur_groupInfo.addAll(getFilterGroupByGroupId(-1));
			editGroupModeAdapter = new EditGroupModeAdapter(mainActivity,cur_groupInfo, groupid);
			lv_group_info.setAdapter(editGroupModeAdapter);
		}
	}

	class DeleteGroupListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			Object tag = cur_group_title.getTag();

			System.out.println(" tag :" + tag);

			if (tag == null) // 删除空的未命名分组
			{
				// 选定第一个
				int position = 0;
				add_ln.removeView(cur_group_title);

				long groupId = groupInfos.get(position).getGroup_id();
				cur_groupInfo = getFilterGroupByGroupId(groupId);

				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				cur_group_title = (FrameLayout) add_ln.getChildAt(position);

				// 重置背景
				cur_group_title.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
				cur_group_title.findViewById(R.id.remove_ln).setBackgroundResource(R.drawable.add_button_bg_selected);

				// 重置布局
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title.findViewById(R.id.ln_edit_group_title)).setVisibility(View.GONE);

				editButton.setVisibility(View.GONE);

			} else {

				final GroupInfo g = (GroupInfo) tag;

				Dialog dialog = new AlertDialog.Builder(mainActivity)
						.setTitle("提示")
						.setMessage("确定删除分组?")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {

										groupProgressDialog = new ProgressDialog(
												mainActivity);
										groupProgressDialog
												.setMessage("正在删除分组");
										groupProgressDialog.show();

										new Thread(new Runnable() {

											@Override
											public void run() {
												// TODO Auto-generated method
												// stub
												isEditingGroup = true;
												dropGroup(g.getGroup_id());
												groupInfos.remove(g);

												for (GroupInfo g : cur_groupInfo) {
													g.setGroup_id(-1);
												}
												deleteGroupRemind();
												group_Handler.sendEmptyMessage(3);
											}
										}).start();

									}
								}).setNegativeButton("取消", null).create();

				dialog.show();
			}
		}
	}

	class GroupTitleClickListener implements OnClickListener {

		FrameLayout group_view;
		GroupInfo info;

		public GroupTitleClickListener(FrameLayout group_view, GroupInfo info) {
			this.group_view = group_view;
			this.info = info;
		}

		@Override
		public void onClick(View v) {

			// 切换分组时 隐藏编辑布局
			if (dataSaveCancel != null&& dataSaveCancel.getVisibility() != View.GONE) {
				
				dataSaveCancel.setVisibility(View.GONE);
				((LinearLayout) cur_group_title.findViewById(R.id.ln_edit_group_title)).setVisibility(View.GONE);

				long groupId = editGroupModeAdapter.getGroupid();
				
				if (groupId == MY_GOUTP_TITLE_ID_ADD) // 删除未命名的 ,空分组
				{
					add_ln.removeView(cur_group_title);
				}
			}

			group_index = info.getIndex();
			
			cur_group_title.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg);
			cur_group_title.findViewById(R.id.remove_ln).setBackgroundResource(R.drawable.add_button_bg);

			cur_group_title = group_view;

			long groupid = info.getGroup_id();
			cur_group_id = info.getGroup_id();

			if (groupid == MY_GROUP_TITLE_ID_ALL) // 全部分组
			{
				cur_groupInfo = all_group_contact_infos;

				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				if (editButton.getVisibility() != View.GONE) {
					editButton.setVisibility(View.GONE);
				}
				
			} else if (groupid == MY_GROUP_TITLE_ID_NO) // 未分组
			{
				cur_groupInfo = getFilterGroupByGroupId(-1);
				curGroupAdapter = new GroupInfoAdapter(mainActivity,
						cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				if (editButton.getVisibility() != View.GONE) {
					editButton.setVisibility(View.GONE);
				}

			} else if (groupid == MY_GOUTP_TITLE_ID_ADD) // 添加分组
			{

				FrameLayout group_view = (FrameLayout) LayoutInflater.from(mainActivity).inflate(R.layout.add_button_item, null);
				final Button deleteGroupButton = (Button) group_view
						.findViewById(R.id.delete_group_button);
				deleteGroupButton.setOnClickListener(new DeleteGroupListener());

				LinearLayout ln_edit_group_title = (LinearLayout) group_view.findViewById(R.id.ln_edit_group_title);
				ln_edit_group_title.setVisibility(View.VISIBLE);

				Button showAdapterButton = (Button) group_view.findViewById(R.id.button_item);
				showAdapterButton.setOnClickListener(new GroupTitleClickListener(group_view, null));

				final EditText edit_group = (EditText)group_view.findViewById(R.id.edit_group);
				edit_group.setText("未命名");
				
				int position = add_ln.getChildCount() - 2;
				add_ln.addView(group_view, position);

				dataSaveCancel.setVisibility(View.VISIBLE);

				if (editButton.getVisibility() != View.GONE) {
					editButton.setVisibility(View.GONE);
				}

				// 添加分组，未命名
				group_view.setTag(null);
				cur_group_title = group_view;

				cur_groupInfo = getFilterGroupByGroupId(-1);

				editGroupModeAdapter = new EditGroupModeAdapter(mainActivity,cur_groupInfo, groupid);
				lv_group_info.setAdapter(editGroupModeAdapter);
				
				group_index = position;
				

			} else // 正常分组
			{
				cur_groupInfo = getFilterGroupByGroupId(groupid);

				curGroupAdapter = new GroupInfoAdapter(mainActivity,cur_groupInfo);
				lv_group_info.setAdapter(curGroupAdapter);

				// 点击正常分组 显示下方的编辑按钮
				editButton.setVisibility(View.VISIBLE);
			}

			// 选中
			cur_group_title.findViewById(R.id.button_item).setBackgroundResource(R.drawable.add_button_bg_selected);
			cur_group_title.findViewById(R.id.remove_ln).setBackgroundResource(R.drawable.add_button_bg_selected);
			// }
			
			
			//设置顶栏的标题
			String source = info.getGroup_name();
			if(info.getGroup_id()==MY_GROUP_TITLE_ID_ALL)
			{
				source = "全部联系人";
			}else if(info.getGroup_id()==MY_GOUTP_TITLE_ID_ADD)
			{
				source = "未分组";
			}
			else{
				int start_index = source.indexOf("(");
				source = source.substring(0, start_index);
			}
			
			tv_top.setText(source);
			
		}
		
	}

	public boolean pressDialingButton() {
		
		if (dataSaveCancel.getVisibility() == View.VISIBLE) {
			
			long groupId = editGroupModeAdapter.getGroupid();
			if (groupId == MY_GOUTP_TITLE_ID_ADD) // 新建分组
			{
				saveGroupAfterNewPressDialing();
			} else { // 修改分组
				saveGroupAfterEditedPressDialing();
			}
			return true;
			
		} else {

			contact_scroller.postDelayed(new Runnable() {

				@Override
				public void run() {
					contact_scroller.setToScreen(1);
					
					sf = mainActivity.getSharedPreferences(SF_NAME, 0);

					sort = sf.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

					if (sort == 1) {
						indexBar.setVisibility(View.VISIBLE);

					} else {

						indexBar.setVisibility(View.GONE);

					}
					
					btn_search.setVisibility(View.VISIBLE);
					btn_add_contact.setVisibility(View.VISIBLE);
				}
			}, 1000);
		}
		return false;
	}

	public void goBack() {
		
		mainActivity.switchToCallLogs();

	}

	class GrouInfoListItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {

			if (lv_group_info.getAdapter() instanceof EditGroupModeAdapter) { // 编辑状态

			} else { // 正常状态 : 点击进入联系人详情
				
				String contact_id = cur_groupInfo.get(arg2).getPerson_id();
				System.out.println(" menu_contact_detail ::contactId ---> " + contact_id);

				goToContactDetail(contact_id);
				
			}
		}
	}

	/**
	 * 跳转至联系人详情
	 */
	void goToContactDetail(String contact_id)
	{
		Intent intent = new Intent(mainActivity, AddContactActivity.class);
		intent.putExtra(AddContactActivity.DATA_TYPE, AddContactActivity.TYPE_DETAIL_CONTACT);
		intent.putExtra(AddContactActivity.DATA_CONTACT_ID, contact_id);
		mainActivity.startActivity(intent);
		
	}
	
	public void updateAfterChangeEncryption() {
		
		contactsAdapter.reBindDateSet(contacts,sort);
		
		if (contactsSearchAdapter != null) {
			contactsSearchAdapter.reBindDateSet(contacts);
		}
		
		// 刷新分组信息
		if (all_group_contact_infos != null) {
			initGroupLayout(false);
		}
	}

	public void updateAfterChangeRemind() {
		
		if(contactsAdapter!=null)
		{
			contactsAdapter.notifyDataSetChanged();
		}
		
		MainActivity.isChangeRemindData = false;
	}

	public void setTv_top(TextView tv_top) {
		this.tv_top = tv_top;
	}

}
