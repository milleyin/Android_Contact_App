package com.dongji.app.addressbook;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentProviderOperation;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Vibrator;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.CallLogsAdapter;
import com.dongji.app.adapter.EncryptionContentProvider;
import com.dongji.app.adapter.EncryptionDBHepler;
import com.dongji.app.adapter.SearchContactsAdapter;
import com.dongji.app.adapter.StrangeNumberContactsAdapter;
import com.dongji.app.addressbook.RemindsActivity.CotanctNameOnClick;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.MessageLibrary;
import com.dongji.app.entity.NetWorkResult;
import com.dongji.app.net.NetworkUnit;
import com.dongji.app.service.MyService;
import com.dongji.app.service.UpdateVersionService;
import com.dongji.app.sqllite.ContactLauncherDBHelper;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.sqllite.PhoneDatabaseUtil;
import com.dongji.app.tool.AddBlackWhite;
import com.dongji.app.tool.AndroidUtils;
import com.dongji.app.tool.FavoritContactTool;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.ToolUnit;
import com.dongji.app.ui.ArcInputPanel;
import com.dongji.app.ui.ArcInputPanel.OnItemPressed;
import com.dongji.app.ui.ArcInputPanel.OnTextchange;
import com.dongji.app.ui.ArcInputPanel.OnTouchOutsideArea;
import com.dongji.app.ui.ScrollLayout;
import com.dongji.app.ui.VerScrollLayout;
import com.dongji.app.ui.VerScrollLayout.OnScrollerFinish;
import com.dongji.app.ui.VerScrollLayout.onScrollerStart;
import com.hp.hpl.sparta.xpath.PositionEqualsExpr;
import com.umeng.analytics.MobclickAgent;

public class MainActivity extends Activity implements OnClickListener {

	boolean isNeedRefresh = false; // 通话记录是否需要刷新

	public static boolean isEncryption = false;  //是否激活了加密内容 的 功能
	
	public static boolean isChangeRemindData = false;  //是否改变了提醒的内容： true 则要刷新联系人列表的提醒标识
	
	public static boolean isChangeEnData = false; //是否改变了加密内容  :  true 调用 updateAfterChangeEncryption 刷新相关列表
	
	
	////////////首页的内容显示区域
	static LinearLayout home_content;
	
	// 四个子模块
	FrameLayout fn_call_logs;   //通话记录
	ContactLayout contactLayout; //联系人列表
	ConversationsListLayout messageListLayout;  //短信会话列表
	SettingLayout settingLayout;  //设置
	
	
	//状态: 当前正在哪个界面
	public int state = 0;
	public static final int STATE_HOME = 0;
	public static final int STATE_CONTACT = 1; // 联系人列表
	public static final int STATE_MESSAGE_LIST = 2; // 短信列表
	public static final int STATE_SETTING = 3; // 设置
	public static final int STATE_COLLECT_CONTACT = 4; // 收藏联系人

	
	FrameLayout fram_tip_mask;// 引导提示层
	
	
    /////////////// 底栏
	private LinearLayout bottom_layout;
	LinearLayout setting_ly;
	LinearLayout dialing_ly;
	LinearLayout huxing_ly;
	
	// 底栏的按钮
	Button btn_dialing; // 拨号按钮
	Button btn_huxing;// 弧形拨号按钮 (默认隐藏)

	Button btn_contacts; // 联系人列表
	Button btn_messages; // 消息会话列表
	Button btn_settings; // 系统设置
	
	
	///////////////拨号盘
	LinearLayout dialing_panel;
	TextView mask_tv; //蒙层  (除拨号盘有效区域外的  其他区域  ，也就是上面的 那些透明区域  ，点击后，拨号盘将收起 )
	EditText et_dialing_number; //号码输入
	ImageButton btn_back_dialing; //号码删除
	
	int dialing_state = 0; // 拨号按钮的状态： 拨号盘， 打电话 ， 短信
	final int DIALING_STATE_PANEL = 0; // 拨号盘
	final int DIALING_STATE_CALL = 1; // 打电话
	final int DIALING_STATE_SMS = 2; // 短信列表
	
	
	// 拨号盘号码输入的三种字体大小  以及   每种字体下能允许输入的最大长度
	private int DIALING_NUMBER_BIG;
	private int DIALING_NUMBER_BIG_LENGTH;
	private int DIALING_NUMBER_MID;
	private int DIALING_NUMBER_MID_LENGTH;
	private int DIALING_NUMBER_SMALL;
	private int DIALING_NUMBER_SMALL_LENGTH;
	
	
    ///////////////弧形键盘
	ArcInputPanel mAcrInputPanel; 
	Animation huXingAnimation;

	int huxing_state; // 弧形键盘按钮的状态 : 1,正常 ; 2, 拨打电话
	private final static int HUXING_DIALING_STATE_PANEL = 0;
	private final static int HUXING_DIALING_STATE_CALL = 1;

	
	///////////////// 号码联想 (在拨号盘输入数字后所展示的区域)
	public ListView lv_contact_association;  //匹配列表
	SearchContactsAdapter searchContactsAdapter;
	List<ContactBean> all_contacts;  //所有联系人信息，匹配的数据源
	public LinearLayout ln_association_tips;  //没有相应的联想号码所显示的操作项


	//////////////通话记录
	public ListView lv_calls; // 通话记录列表
	public static CallLogsAdapter homeCallsAdapter;
	int type;  //当前正在显示的通话记录类型:  全部  ， 未接  
	

	RelativeLayout recent_calllog; 	//最近通话栏
	ImageButton img_goto_search; //搜索通话记录按钮
	EditText et_search_calllog; //通话记录搜索输入
	public Button search_img;
	public LinearLayout delete_search_info;
	
	
	//////////// 归属地查询的数据库
	public static PhoneDatabaseUtil phoneDatabaseUtil = null;
	private MyDatabaseUtil myDatabaseUtil = null;

	private String assets_db_name = "phone.zip";
	String phone_db_name= "phone.db"; //数据库的名称
	String filePath ; // 归属地数据库存储路径

	
	// 所有被加密的联系人
	public static List<EnContact> EN_CONTACTS = new ArrayList<EnContact>();

	
	/////////// 收藏联系人 (在拨号盘首次输入#)
	LinearLayout ln_collect_contact;
	int back_index = -1; // 0:从正常拨号盘进入收藏联系人 , 1:从弧形键盘进入
	List<ContactBean> collect_contacts;
	Button btn_out_collect_contact; // 退出收藏联系人
	Button btn_edit_collect_contact; // 编辑收藏联系人

	boolean isModifyCollectContactMode = false; // 是否为修改收藏联系人  模式
	View selected_collect_contact_view; // 被选中的那个收藏联系人所在的View的引用
	
	
	LinearLayout ln_collect_contacts;

	Dialog dialog_collect_pick_contact;
	ListView lv_collect_pick_contacts; 

	
	private boolean isSupport; // 是否支持弧形键盘

	PopupWindow popup;

	
	ProgressDialog progressDialog;

	
	String add_to_number;
	Dialog dialog_add_to;
	LinearLayout dialog_add_to_view;

	Dialog dialog_pick_contact;  //选择联系人
	ListView lv_pick_contact; 
	
	long call_id;
	String call_number; // 原始号码
	String nick;

	Dialog dialog;

	public static NotificationManager updateNotificationManager = null;

	ContactProviderObserver contactProviderObserver;
	SmsProviderObserver smsProviderObserver;

	RelativeLayout calllog_search_ly;
	private MyHandler myHandler = new MyHandler();

	String arrVersionUpdate[];
	String update_url;
	String version_info = "1.0";
	String version_date = "2012-12-28";
	float currentAppVersion = -1;

	boolean isRead = false;
//	int num = 0;

	public static long MY_CONTACT_ID; // 机主的 联系人id
	public static final String MY_CONTACT_MIME_TYPE = "my_contact_mime_type";

	
	public boolean isRefreshing = false; // 正在刷新通话记录

	
	private static final String FIRST_LAUNCHER = "fisrtLauncher"; //是否第一次启动应用
	
	//////////////////// 生成至桌面的快捷方式
	static int shortcutType;  //如果以快捷方式的形式进入应用  会被赋予相应的值
	private static final int CALL_PHONE_SHORTCUT = 1;  
	private static final int CONTACT_SHORTCUT = 2;
	private static final int SMS_SHORTCUT = 3;
	private static final String CALL_PHONE_STRING = "打电话";
	private static final String CONTACT_STRING = "联系人";
	private static final String SMS_STRING = "短信";
	

	public boolean isInserting = false;

	// 是否正在长按正常拨号盘的删除按钮
	public boolean isLongPresseding = false;
	String dialing_number_source;

	// 联系人热度数据库
	ContactLauncherDBHelper mContactLauncherDBHelper;

	long start;
	public static NotificationManager cloudBackupRecoveryNotification = null;
	public static boolean isNotifacation = false;

	AddBlackWhite addBlackWhite;

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}

			switch (msg.what) {
			case 1: // 清空通话记录 ，不管是清空所有记录还是未接来电记录,都调用此方法,重置 CallLogsAdapter 并且
					// 显示为全部通话记录

				homeCallsAdapter = new CallLogsAdapter(MainActivity.this,
						new OnMenuItemClickListener(),
						CallLogsAdapter.TYPE_ALL, lv_calls);
				lv_calls.setAdapter(homeCallsAdapter);
				type = CallLogsAdapter.TYPE_ALL;

				break;

			case 2:// 删除此号码全部的通讯记录

				homeCallsAdapter.deleteAllByNumber(call_number);

				homeCallsAdapter.changeToType(type);

				Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT)
						.show();

				break;

			case 3:

				searchContactsAdapter = new SearchContactsAdapter(
						MainActivity.this, all_contacts);
				lv_contact_association.setAdapter(searchContactsAdapter);
				

				break;

			case 4: // 加载收藏联系人

				refreshCollectContact();

				StrangeNumberContactsAdapter strangeNumberContactsAdapter = new StrangeNumberContactsAdapter(
						MainActivity.this, all_contacts,new ModifyCollectContactClickListener());
				lv_collect_pick_contacts.setAdapter(strangeNumberContactsAdapter);

				break;

			case 5: // 第一次复制归属地数据库成功后，刷新首页的通话记录
				homeCallsAdapter.notifyDataSetChanged();
				
				break;

			default:
				break;
			}
		};
	};

	
	Handler mQucikDeleteHandler = new Handler() {
		public void handleMessage(Message msg) {
			et_dialing_number.setText(dialing_number_source);
		};
	};
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// 友盟统计 ： 错误报告
		MobclickAgent.onError(this);

		state = STATE_HOME;

		myDatabaseUtil = DButil.getInstance(this);
		updateNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		addBlackWhite = new AddBlackWhite(this);

		isSupport = checkPhoneModel();

		// 提示层
		fram_tip_mask = (FrameLayout) findViewById(R.id.fram_tip_mask);
		fram_tip_mask.setOnTouchListener(new TipFrameTouchListener());

		home_content = (LinearLayout) findViewById(R.id.home_content);

		// 底部栏的四个按钮
		btn_dialing = (Button) findViewById(R.id.btn_dialing);
		btn_dialing.setOnClickListener(this);

		btn_contacts = (Button) findViewById(R.id.btn_contacts);
		btn_contacts.setOnClickListener(this);

		btn_messages = (Button) findViewById(R.id.btn_messages);
		btn_messages.setOnClickListener(this);

		btn_settings = (Button) findViewById(R.id.btn_settings);
		btn_settings.setOnClickListener(this);

		// 通话记录
		fn_call_logs = (FrameLayout) findViewById(R.id.fn_call_logs);

		lv_calls = (ListView) findViewById(R.id.lv_top_10_calls);
		calllog_search_ly = (RelativeLayout) findViewById(R.id.calllog_search_ly);
		et_search_calllog = (EditText) findViewById(R.id.search_calllog);
		et_search_calllog.addTextChangedListener(searchCallLog);
		search_img = (Button) findViewById(R.id.search_img);
		delete_search_info = (LinearLayout) findViewById(R.id.delete_search_info);
		delete_search_info.setOnClickListener(this);

		btn_huxing = (Button) findViewById(R.id.huxing);
		btn_huxing.setOnClickListener(this);

		mAcrInputPanel = (ArcInputPanel) findViewById(R.id.mAcrInputPanel);
		
		mAcrInputPanel.setOnTouchOutsideArea(new OnTouchOutsideArea() {

			@Override
			public void OnTouchOutsideArea() {

				Animation an = AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.huxing_back);

				an.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						mAcrInputPanel.setVisibility(View.GONE);
					}
				});
				mAcrInputPanel.startAnimation(an);
				btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);

				huxing_state = HUXING_DIALING_STATE_PANEL;
			}
		});
		

		// 延迟初始化加载，这样才能获取到尺寸
		mAcrInputPanel.post(new Runnable() {
			@Override
			public void run() {
				FrameLayout ln_huxing_area = (FrameLayout) findViewById(R.id.ln_huxing_area);
				mAcrInputPanel.init(ln_huxing_area.getWidth(),ln_huxing_area.getHeight());
			}
		});

		mAcrInputPanel.setmOnTextchange(new OnTextchange() {

			@Override
			public void OnTextchange(String s) {
				try {
					handleTextChange(s);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		mAcrInputPanel.setOnItemPressed(new OnItemPressed() {

			@Override
			public void OnItemPressed() {
				playSetting();
			}
		});

		mask_tv = (TextView) findViewById(R.id.mask_tv);
		mask_tv.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (lv_contact_association.getVisibility() != View.VISIBLE
							&& ln_association_tips.getVisibility() != View.VISIBLE) {
						et_dialing_number.setText("");

						if (dialing_panel.getVisibility() != View.GONE) {
							dialing_panel.setAnimation(AnimationUtils
									.loadAnimation(MainActivity.this,
											R.anim.dialing_out));
							dialing_panel.setVisibility(View.GONE);
						}
						btn_dialing
								.setBackgroundResource(R.drawable.btn_dialing_panel);

						dialing_state = DIALING_STATE_PANEL;
						return true;
					}
				}
				return false;
			}
		});

		dialing_panel = (LinearLayout) findViewById(R.id.dialing_panel);

		// 正常拨号盘
		et_dialing_number = (EditText) findViewById(R.id.et_dialing_number);
		et_dialing_number.addTextChangedListener(textWatcher);

		
		DIALING_NUMBER_BIG = getResources().getDimensionPixelSize(
				R.dimen.dialing_nume_text_big);
		DIALING_NUMBER_MID = getResources().getDimensionPixelSize(
				R.dimen.dialing_nume_text_mid);
		DIALING_NUMBER_SMALL = getResources().getDimensionPixelSize(
				R.dimen.dialing_nume_text_small);

		et_dialing_number.setTextSize(DIALING_NUMBER_BIG);

		huXingAnimation = AnimationUtils.loadAnimation(this, R.anim.huxing);

		btn_back_dialing = (ImageButton) findViewById(R.id.btn_back_dialing);
		btn_back_dialing.setOnTouchListener(new DialingDeleteTouchListener());
		btn_back_dialing.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				isLongPresseding = true;
				new Thread(new quickDeleteRunnable()).start();
				return false;
			}
		});

		MyTouchListener myTouchListener = new MyTouchListener();
		LinearLayout btn_number_1 = (LinearLayout) findViewById(R.id.btn_number_1);
		btn_number_1.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_2 = (LinearLayout) findViewById(R.id.btn_number_2);
		btn_number_2.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_3 = (LinearLayout) findViewById(R.id.btn_number_3);
		btn_number_3.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_4 = (LinearLayout) findViewById(R.id.btn_number_4);
		btn_number_4.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_5 = (LinearLayout) findViewById(R.id.btn_number_5);
		btn_number_5.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_6 = (LinearLayout) findViewById(R.id.btn_number_6);
		btn_number_6.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_7 = (LinearLayout) findViewById(R.id.btn_number_7);
		btn_number_7.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_8 = (LinearLayout) findViewById(R.id.btn_number_8);
		btn_number_8.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_9 = (LinearLayout) findViewById(R.id.btn_number_9);
		btn_number_9.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_xing = (LinearLayout) findViewById(R.id.btn_number_xing);
		btn_number_xing.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_0 = (LinearLayout) findViewById(R.id.btn_number_0);
		btn_number_0.setOnTouchListener(myTouchListener);

		LinearLayout btn_number_jing = (LinearLayout) findViewById(R.id.btn_number_jing);
		btn_number_jing.setOnTouchListener(myTouchListener);

		lv_contact_association = (ListView) findViewById(R.id.lv_contact_association);

		lv_contact_association.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (dialing_panel.getVisibility() == View.VISIBLE) {
					dialing_panel.setVisibility(View.GONE);
				}

				if (mAcrInputPanel.getVisibility() == View.VISIBLE) {
					mAcrInputPanel.setVisibility(View.GONE);
				}
				return false;
			}
		});

		ln_association_tips = (LinearLayout) findViewById(R.id.ln_association_tips);
		NumberAssociationOnClickListener numberAssociationOnClickListener = new NumberAssociationOnClickListener();
		(findViewById(R.id.add_contact_association)).setOnClickListener(numberAssociationOnClickListener);
		(findViewById(R.id.add_contact_in_association)).setOnClickListener(numberAssociationOnClickListener);
		(findViewById(R.id.send_sms_association)).setOnClickListener(numberAssociationOnClickListener);

		recent_calllog = (RelativeLayout) findViewById(R.id.recent_calllog);
		((ImageButton) findViewById(R.id.recent_call_img)).setOnClickListener(this);
		
		img_goto_search = (ImageButton) findViewById(R.id.img_goto_search);
		img_goto_search.setOnClickListener(this);


		SharedPreferences encryption = getSharedPreferences(EncryptionActivity.SF_NAME, 0);
		isEncryption = encryption.getBoolean(EncryptionActivity.KEY_ISENCRYPTION, false);

		
		bottom_layout = (LinearLayout) findViewById(R.id.bottom_layout);
		setting_ly = (LinearLayout) findViewById(R.id.setting_ly);
		dialing_ly = (LinearLayout) findViewById(R.id.dialing_ly);
		huxing_ly = (LinearLayout) findViewById(R.id.huxing_ly);

		SharedPreferences sf = getSharedPreferences(
				SystemSettingActivity.SF_NAME, 0);
		int hand = sf.getInt(SystemSettingActivity.SF_KEY_HAND, 1);

		if (hand == 0) {

			bottom_layout.removeAllViews();

			bottom_layout.addView(huxing_ly);
			bottom_layout.addView(dialing_ly);
			bottom_layout.addView(setting_ly);

		}


		homeCallsAdapter = new CallLogsAdapter(this,
				new OnMenuItemClickListener(), CallLogsAdapter.TYPE_ALL,
				lv_calls);
		lv_calls.setAdapter(homeCallsAdapter);
		type = CallLogsAdapter.TYPE_ALL;

		ln_collect_contact = (LinearLayout) findViewById(R.id.ln_collect_contact);
		btn_out_collect_contact = (Button) findViewById(R.id.btn_out_collect_contact);
		btn_out_collect_contact.setOnClickListener(this);
		btn_edit_collect_contact = (Button) findViewById(R.id.btn_edit_collect_contact);
		btn_edit_collect_contact.setOnClickListener(this);

		ln_collect_contacts = (LinearLayout) findViewById(R.id.ln_collect_contacts);

		
		dialog_collect_pick_contact = new Dialog(this, R.style.theme_myDialog);
		dialog_collect_pick_contact.setContentView(R.layout.dialog_pick_contact);
		lv_collect_pick_contacts = (ListView) dialog_collect_pick_contact.findViewById(R.id.lv_pick_contact);
		

		sf = getSharedPreferences("systemsetting", 0);

		int DIALING_PANEL = sf.getInt("dialing_panel", 1);

		if (DIALING_PANEL == 0) {
			dialing_state = DIALING_STATE_PANEL;

			if (dialing_panel.getVisibility() == View.GONE) {
				dialing_panel.setAnimation(AnimationUtils.loadAnimation(this,
						R.anim.dialing_in));

				dialing_panel.setVisibility(View.VISIBLE);
				btn_dialing.setBackgroundResource(R.drawable.btn_dialing);
			}
		}


		// 第一次进入初始化联系人热度值
		SharedPreferences spf = getSharedPreferences("First", 0);
		SharedPreferences.Editor edit = spf.edit();

		boolean isFirst = spf.getBoolean("isFirst", true);

		if (isFirst) {
			MyHandlerThread handlerThread = new MyHandlerThread("heat_thread");
			handlerThread.start();
			Handler handler = new Handler(handlerThread.getLooper(),
					handlerThread);
			handler.sendEmptyMessage(1);

			edit.putBoolean("isFirst", false);
			edit.commit();
		}

		// 系统数据库监听: 联系人 ， 短信
		contactProviderObserver = new ContactProviderObserver(new Handler());
		getContentResolver().registerContentObserver(
				ContactsContract.Contacts.CONTENT_URI, true,
				contactProviderObserver);

		smsProviderObserver = new SmsProviderObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://mms-sms/conversations?simple=true"), true,
				smsProviderObserver);
		

		// 拨号盘默认显示模式
		SharedPreferences sf1 = getSharedPreferences(
				SystemSettingActivity.SF_NAME, 0);
		int new_panel_state = sf1.getInt(
				SystemSettingActivity.SF_KEY_DIALING_PANEL, 1);
		if (new_panel_state == 0) {

			dialing_panel.setAnimation(AnimationUtils.loadAnimation(this,
					R.anim.dialing_in));
			dialing_panel.setVisibility(View.VISIBLE);
			btn_dialing.setBackgroundResource(R.drawable.btn_dialing);
			dialing_state = DIALING_STATE_CALL;
		}

		sf_fist_in = getSharedPreferences(BACKUP, 0);

		// 第一次进入应用
		checkFirstIn();

		//查询所有加密的联系人
		queryEnContact();
		

		// 检查更新
		checkUpdate();

		// 第一次进入应用创建快捷方式
		checkFirstLauncher();

		// 归属地数据库
		copyOrOpenPhoneDatabse();

		// 短信库
		insertMessageLibrary();

		// 处理其他动作： 打电话，发短信····
		handleIntentAction(getIntent());

		//开启新消息监听服务
		Intent serviceIntent = new Intent(this, MyService.class);
		startService(serviceIntent);
		
	}
	

	/**
	 * 
	 * 机主的相关操作
	 * 
	 * 检测机主是否存在，不存在则创建并保存
	 * 
	 * 
	 */
	void checkLord() {

		SharedPreferences ss = getSharedPreferences("myNumberContactId", 0);
		long contactId = ss.getLong("myContactId", -1);

		// 不存在机主（第一次安装 或 数据已被清空）
		if (contactId == -1) {
			Cursor myCursor = getContentResolver().query(Data.CONTENT_URI,
					new String[] { Data._ID, Data.CONTACT_ID },
					Data.MIMETYPE + "='" + MY_CONTACT_MIME_TYPE + "'", null,
					null);

			if (myCursor.moveToNext()) {
				contactId = myCursor.getLong(myCursor
						.getColumnIndex(Data.CONTACT_ID));
			}

			myCursor.close();

			if (contactId != -1) // 保存起来
			{
				Editor editor = ss.edit();
				editor.putLong("myContactId", contactId);
				editor.commit();
			}

		}

		Cursor c = getContentResolver().query(RawContacts.CONTENT_URI, null,
				RawContacts.CONTACT_ID + " = " + contactId, null, null);
		int size = c.getCount();

		if (size > 0) {
			c.moveToNext();
			String nn = c.getString(c
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			System.out.println("  机主的名称    --->" + nn);
		}

		c.close();

		// 不存在本机联系人id，则创建机主联系人
		if (contactId == -1 || size == 0) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

					ContentValues values = new ContentValues();
					Uri rawcontacturi = getContentResolver().insert(
							RawContacts.CONTENT_URI, values);

					long rawcontactid = ContentUris.parseId(rawcontacturi);

					// 联系人姓名
					ContentProviderOperation.Builder builder = ContentProviderOperation
							.newInsert(Data.CONTENT_URI);

				
					builder.withValue(Data.RAW_CONTACT_ID, rawcontactid);
					builder.withValue(Data.MIMETYPE,
							StructuredName.CONTENT_ITEM_TYPE);
					builder.withValue(StructuredName.DISPLAY_NAME, "机主");
					builder.withYieldAllowed(true);

					
					ContentProviderOperation.Builder builder1 = ContentProviderOperation
							.newInsert(Data.CONTENT_URI);
					builder1.withValue(Data.RAW_CONTACT_ID, rawcontactid);
					builder1.withValue(Data.MIMETYPE, MY_CONTACT_MIME_TYPE);
					builder1.withValue("data2", "我是机主");
				

					ops.add(builder.build());
					ops.add(builder1.build());

					try {
						getContentResolver().applyBatch( ContactsContract.AUTHORITY, ops);
						

						Cursor contactIdCursor = getContentResolver().query(
								RawContacts.CONTENT_URI,
								new String[] { RawContacts.CONTACT_ID },
								RawContacts._ID + "=" + rawcontactid, null,
								null);

						if (contactIdCursor != null
								&& contactIdCursor.moveToFirst()) {
							long contactId = contactIdCursor.getLong(0);
							SharedPreferences ss = getSharedPreferences(
									"myNumberContactId", 0);
							Editor ed = ss.edit();
							ed.putLong("myContactId", contactId);
							ed.commit();

							MY_CONTACT_ID = contactId;
						}

						contactIdCursor.close();

						System.out.println(" rawcontactid --->" + rawcontactid);
						System.out.println(" 机主联系人信息已创建  ---");

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		} else {
			MY_CONTACT_ID = contactId;
		}
	}

	private void handleIntentAction(Intent intent) {

		String intent_action = intent.getAction();
		String mime_type = intent.getType();
		String data = intent.getDataString();

//		System.out.println("  intent_action  --->" + intent_action);
//		System.out.println("  mime_type -----> " + mime_type);
//		System.out.println("  scheme -----> " + scheme);
//		System.out.println("  data -----> " + data);
		
		int type = shortcutType;
				
		if (intent_action != null) {

			if (intent_action.equals(Intent.ACTION_DIAL)) // 调用拨号
			{
				type = CALL_PHONE_SHORTCUT;
				
			} else if (intent_action.equals(Intent.ACTION_MAIN) 
					&& (mime_type != null && mime_type.equals("vnd.android-dir/mms-sms"))) // 查看短信列表
			{

				type = SMS_SHORTCUT;
				
			} else if (intent_action.equals(Intent.ACTION_VIEW)
					&& ( (mime_type != null && mime_type.equals("vnd.android.cursor.dir/contact")) 
							|| (data!=null && data.contains("contact")) ) ) // 查看联系人列表
			{

				type = CONTACT_SHORTCUT;
			} 
		}

		
		switch (type) {
		case CALL_PHONE_SHORTCUT: // 调用拨号
			
			switchToCallLogs();
			
			break;

		case SMS_SHORTCUT: // 查看短信列表
			
			swithcToConversation();
			
			break;
			
		case CONTACT_SHORTCUT: // 查看联系人列表
			
			switchToContacts();
			
			break;
			
		default:
			break;
		}
		
		shortcutType = 0;
		
	}

	/**
	 * 
	 * 切换 至 通话记录
	 * 
	 */
	void switchToCallLogs() {
		
		if(state != STATE_HOME)
		{

			if(messageListLayout!=null)
			{
				messageListLayout.view.setVisibility(View.GONE);
			}
			if(contactLayout!=null)
			{
				contactLayout.view.setVisibility(View.GONE);
			}
			if(settingLayout!=null)
			{
				settingLayout.view.setVisibility(View.GONE);
			}
			

			fn_call_logs.setVisibility(View.VISIBLE);
			
			reSetTheCallLogsView();

			if (isNeedRefresh) {
				refresh();
			}
			state = STATE_HOME;
			
		}
	}
	
	
	/**
	 * 
	 * 每次切换回 通话记录界面前，通话记录界面 都将被重置会原始状态
	 * 
	 */
	void reSetTheCallLogsView() {
		// 隐藏拨号联想
		if (lv_contact_association.getVisibility() == View.VISIBLE || ln_association_tips.getVisibility() == View.VISIBLE) {
			lv_contact_association.setVisibility(View.GONE);
			ln_association_tips.setVisibility(View.GONE);
		}

		// 隐藏拨号盘
		if (dialing_panel.getVisibility() != View.GONE) {
			dialing_panel.setVisibility(View.GONE);
		}

		// 隐藏弧形键盘
		if (mAcrInputPanel.getVisibility() != View.GONE) {
			mAcrInputPanel.setVisibility(View.GONE);
			
		}
		
		//收藏联系人(快捷拨号)
		if(ln_collect_contact.getVisibility() == View.VISIBLE)
		{
			ln_collect_contact.setVisibility(View.GONE);
		}
		
		//修改收藏联系人  弹出的全部联系人选择弹窗
		if(dialog_collect_pick_contact!=null && dialog_collect_pick_contact.isShowing())
		{
			dialog_collect_pick_contact.dismiss();
		}
		
		
		if(dialog_pick_contact!=null && dialog_pick_contact.isShowing())
		{
			dialog_pick_contact.dismiss();
		}
		
		
		btn_dialing.setBackgroundResource(R.drawable.btn_dialing_panel);
		dialing_state = DIALING_STATE_PANEL;
		
		btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
		huxing_state = HUXING_DIALING_STATE_PANEL;
		
		
	}

	/**
	 * 
	 * 切换至 短信  会话列表
	 * 
	 */
	void swithcToConversation() {

		if (state != STATE_MESSAGE_LIST) {

//
			reSetTheCallLogsView();
			
			fn_call_logs.setVisibility(View.GONE);
			
			if(settingLayout!=null)
			{
				settingLayout.view.setVisibility(View.GONE);
			}
			if(contactLayout!=null)
			{
				contactLayout.view.setVisibility(View.GONE);
			}
			

			if (messageListLayout == null) {
				messageListLayout = new ConversationsListLayout(MainActivity.this);
			}

			messageListLayout.view.setVisibility(View.VISIBLE);
			

			if (messageListLayout != null && messageListLayout.isNeedRefresh) {
				messageListLayout.refresh(false);
			} 
			else {
				messageListLayout.onResume();
			}

			state = STATE_MESSAGE_LIST;
			
		}
	}

	/**
	 * 
	 * 切换至  联系人列表
	 * 
	 */
	void switchToContacts() {

		if (state != STATE_CONTACT) {

			reSetTheCallLogsView();
			
			fn_call_logs.setVisibility(View.GONE);
			if(messageListLayout!=null)
			{
				messageListLayout.view.setVisibility(View.GONE);
			}
			if(settingLayout!=null)
			{
				settingLayout.view.setVisibility(View.GONE);
			}

			if (contactLayout == null) {
				contactLayout = new ContactLayout(MainActivity.this);
			}

			contactLayout.view.setVisibility(View.VISIBLE);

			if (contactLayout.isNeedRefresh) {
				contactLayout.refresh();
			} else if(isChangeRemindData) {
				contactLayout.updateAfterChangeRemind();
			}

			state = STATE_CONTACT;

			// 判断是否要显示操作提示层
			boolean b = sf_fist_in.getBoolean(SF_KEY_FIRST_CONTACTS, true);
			if (b) {
				fram_tip_mask.removeAllViews();
				fram_tip_mask.addView(LayoutInflater.from(this).inflate(
						R.layout.tip_contacts_first, null));
				fram_tip_mask.setVisibility(View.VISIBLE);
			}

		}

	}

	
	/**
	 * 
	 * 切换 至 设置界面
	 * 
	 */
	void switchToSettings() {

		if (state != STATE_SETTING) {

			reSetTheCallLogsView();

			fn_call_logs.setVisibility(View.GONE);
			if(messageListLayout!=null)
			{
				messageListLayout.view.setVisibility(View.GONE);
			}
			if(contactLayout!=null)
			{
				contactLayout.view.setVisibility(View.GONE);
			}
			

			if(settingLayout==null)
			{
				settingLayout = new SettingLayout(this);
			}
			
			settingLayout.view.setVisibility(View.VISIBLE);
			
			state = STATE_SETTING;

			boolean b = sf_fist_in.getBoolean(SF_KEY_FIRST_SETTINGS, true);

			if (b) {
				fram_tip_mask.removeAllViews();
				fram_tip_mask.addView(LayoutInflater.from(MainActivity.this)
						.inflate(R.layout.tip_settings_first, null));
				fram_tip_mask.setVisibility(View.VISIBLE);
			}

		}
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();

		checkLord();
		
		try {
			MobclickAgent.onResume(this);
			isNotifacation = true;
		} catch (Exception e) {
		}

		handleResume();
		
	}
	
	

	void handleResume() {

		switch (state) {
		case STATE_HOME:

			if (isNeedRefresh) {
				refresh();
			}

			break;

		case STATE_CONTACT:

			if (contactLayout.isNeedRefresh) {
				contactLayout.refresh();
			} else if(isChangeRemindData) {
				contactLayout.updateAfterChangeRemind();
			}

			break;

		case STATE_MESSAGE_LIST:

			if (messageListLayout != null && messageListLayout.isNeedRefresh) {
				messageListLayout.refresh(false);
			} else {
				messageListLayout.onResume();
			}

			break;

		case STATE_SETTING:

//			SharedPreferences sf1 = getSharedPreferences(SystemSettingActivity.SF_NAME, 0);
//
//			int old_calllog_sort = sf1.getInt(SystemSettingActivity.REFRESH_CALLLOG, 1);
//			int new_calllog_sort = sf1.getInt(SystemSettingActivity.SF_KEY_COLLOG_SORT, 1);
//
//			int old_contact_sort = sf1.getInt(SystemSettingActivity.REFRESH_CONTACT, 1);
//			int new_contact_sort = sf1.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);
//
//			int old_sms_sort = sf1.getInt(SystemSettingActivity.REFRESH_SMS, 1);
//			int new_sms_sort = sf1.getInt(SystemSettingActivity.SF_KEY_SMS_SORT,1);
//
//			if (old_calllog_sort != new_calllog_sort) {
//
//				refreshCallLogSort();
//
//				Editor editor = sf1.edit();
//				editor.putInt(SystemSettingActivity.REFRESH_CALLLOG,new_calllog_sort);
//				editor.commit();
//			}
//
//			if (old_contact_sort != new_contact_sort) {
//
//				if (contactLayout != null) {
//					contactLayout.refresh();
//				}
//
//				Editor editor = sf1.edit();
//				editor.putInt(SystemSettingActivity.REFRESH_CONTACT,new_contact_sort);
//				editor.commit();
//			}
//
//			if (old_sms_sort != new_sms_sort) {
//
//				if (messageListLayout != null) {
//					messageListLayout.refreshSort();
//				}
//
//				Editor editor = sf1.edit();
//				editor.putInt(SystemSettingActivity.REFRESH_SMS, new_sms_sort);
//				editor.commit();
//			}

			break;

		default:
			break;
		}
		
		
		if(isChangeEnData)
		{
			updateAfterChangeEncryption();
		}
	}

	
	@Override
	protected void onPause() {
		super.onPause();

		MobclickAgent.onPause(this);
	}


	@Override
	protected void onNewIntent(final Intent intent) {
		// super.onNewIntent(intent);

		System.out.println("  onNewIntent  --->"+ intent.getIntExtra("SHORT_TYPE", 0));

		SharedPreferences encryption = getSharedPreferences(EncryptionActivity.SF_NAME, 0);
		
		Editor editor = encryption.edit();   //再次进入程序后，需要解密
		editor.putBoolean("decryption", false);
		editor.commit();
		
		//如果修改了排序方式，则按照新的排序方式显示数据
		isRefreshSort();
		
		boolean isEN = encryption.getBoolean(EncryptionActivity.KEY_ISENCRYPTION, false);

		System.out.println("  isEncryption  --->" + isEN);

		if (isEN && !isEncryption) {
			isEncryption = true;
			updateAfterChangeEncryption();
		}
		
		handleIntentAction(intent);

	}
	
	
	public void reSetCallingState() {
		if (calllog_search_ly.getVisibility() == View.VISIBLE) {
			calllog_search_ly.setVisibility(View.GONE);
			recent_calllog.setVisibility(View.VISIBLE);
		}

		img_goto_search.setVisibility(View.VISIBLE);

		home_content.postDelayed(new Runnable() {

			@Override
			public void run() {
				isRefreshing = false;
//				isCalling = false;
			}
		}, 5000);
		
	}

	/**
	 * 
	 *  
	 *  获取所有联系人，所有号码, 作为拨号键盘输入时的号码联想数据源
	 *  
	 *  
	 */
	public void getAllContacts() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				start = System.currentTimeMillis();
				// try {
				// Thread.sleep(500);
				// } catch (Exception e) {
				// }

				all_contacts = getAllContact();
				handler.sendEmptyMessage(3);

			}
		}).start();
	}

	
	private class MyHandlerThread extends HandlerThread implements Callback {

		public MyHandlerThread(String name) {
			super(name);
		}

		@Override
		public boolean handleMessage(Message msg) {

			Cursor contact = getContentResolver().query(
					ContactsContract.Contacts.CONTENT_URI,
					new String[] { ContactsContract.Contacts._ID,
							ContactsContract.Contacts.DISPLAY_NAME }, null,
					null, null);

			ContactLauncherDBHelper dbHelper = new ContactLauncherDBHelper(
					MainActivity.this);
			SQLiteDatabase db = dbHelper.getWritableDatabase();

			ContentValues values = new ContentValues();

			if (contact.moveToFirst()) {

				do {

					long id = contact.getLong(contact
							.getColumnIndex(ContactsContract.Contacts._ID));
					String name = contact
							.getString(contact
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

					values.put(dbHelper.CONTACT_ID, id);
					values.put(dbHelper.CONTACTNAME, name);

					String phone = "";
					Cursor phones = getContentResolver()
							.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
									new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID
											+ " = " + id, null, null);

					if (phones.moveToNext()) {
						phone = PhoneNumberTool
								.cleanse(phones.getString(phones
										.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
					}
					phones.close();

					values.put(dbHelper.NUMBER, phone);

					long time = System.currentTimeMillis()
							- (10 * 24 * 60 * 3600 * 1000);

					Cursor calllog = getContentResolver().query(
							CallLog.Calls.CONTENT_URI,
							null,
							CallLog.Calls.CACHED_NAME + "='" + name
									+ "' and date > " + time, null,
							CallLog.Calls.DATE + " desc");

					try {

						Cursor sms = getContentResolver().query(
								Uri.parse("content://sms/"),
								new String[] { "address" }, " date > " + time,
								null, " date desc");

						int smsCount = 0;

						if (sms.getCount() > 0) {

							if (sms.moveToFirst()) {

								do {
									try {
										String address = PhoneNumberTool
												.cleanse(sms.getString(sms
														.getColumnIndex("address")));

										if (address != null) {

											if (address.equals(phone)) {
												smsCount++;
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}

								} while (sms.moveToNext());
							}
						}
						sms.close();

						values.put(dbHelper.HEAT,
								(calllog.getCount() + smsCount));

						calllog.close();

						db.insert(dbHelper.heat_table, dbHelper._ID, values);
					} catch (Exception e) {
						// TODO: handle exception
					}
				} while (contact.moveToNext());
			}

			db.close();

			contact.close();
			return true;
		}

	}

	/**
	 * 
	 * 查询全部联系人 : 如果联系人多个号码，则全部显示
	 * 
	 * 
	 * @return
	 */
	private List<ContactBean> getAllContact() {

		List<ContactBean> contacts = new ArrayList<ContactBean>();
		String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
//		String[] projection = { ContactsContract.Contacts._ID,
//				ContactsContract.Contacts.PHOTO_ID,
//				ContactsContract.Contacts.DISPLAY_NAME };
		Cursor cursor = getContentResolver().query(
				ContactsContract.Contacts.CONTENT_URI,
				null,
				" sort_key is not null AND "
						+ ContactsContract.Contacts.HAS_PHONE_NUMBER
						+ " is not 0", null, sortOrder);

		int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
		int name_column = cursor
				.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		int photo_column = cursor
				.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);

		while (cursor.moveToNext()) {

			String name = cursor.getString(name_column);
			if (name != null) {
				long id = cursor.getLong(id_column);
				String photo_id = cursor.getString(photo_column);

				Cursor phones = getContentResolver().query(
						ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
						null,
						ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + id, null, null);

				while (phones.moveToNext()) {
					ContactBean contactBean = new ContactBean();
					contactBean.setContact_id(id);
					contactBean.setPhoto_id(photo_id);
					contactBean.setNick(name);
					String phone = phones
							.getString(phones
									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
					contactBean.setNumber(phone);
					contacts.add(contactBean);
				}
				phones.close();
			}
		}

		if (cursor != null)
			cursor.close();

		return contacts;
	}

	private int getUnreadSMSNum() {
		int count = 0;

		// Cursor isRead =
		// getContentResolver().query(Uri.parse("content://sms/"),
		// new String[] { "Count(read) as isRead" }, " read = 0", null,
		// " read desc");// 1已读 0未读
		// if (isRead.moveToFirst()) {
		// count = isRead.getInt(isRead.getColumnIndex("isRead"));
		// }
		// if (isRead != null) {
		// isRead.close();
		// }

		Cursor cursor = getContentResolver().query(Uri.parse("content://sms/"),
				new String[] { "address" }, " read = 0", null, " read desc");// 1已读
																				// 0未读

		if (cursor.moveToFirst()) {

			do {

				String address = cursor.getString(cursor.getColumnIndex("address"));
				boolean isMath = false;
				for (EnContact ec : MainActivity.EN_CONTACTS) {
					for (String number : ec.getNumbers()) {
						number = PhoneNumberTool.cleanse(number);
						if (number.equals(PhoneNumberTool.cleanse(address))) {
							isMath = true;
							break;
						}
					}
				}

				if (!isMath) {

					++count;
				}

			} while (cursor.moveToNext());
		}

		if (cursor != null) {
			cursor.close();
		}
		return count;
	}

	
	// 查询所有加密联系人的信息
	private void queryEnContact() {
		EN_CONTACTS.clear();
		Cursor cr = getContentResolver().query(EncryptionContentProvider.URIS,
				null, null, null, null);

		List<String> cotacts_ids = new ArrayList<String>();

		System.out.println(" all_en_contacts cout is --->" + cr.getCount());

		while (cr.moveToNext()) {
			String contactid = cr.getString(cr.getColumnIndex(EncryptionDBHepler.CONTACT_ID));
			cotacts_ids.add(contactid);

			// System.out.println(" contactid --->" + contactid);
		}
		cr.close();

		for (String contactId : cotacts_ids) {
			EnContact enContact = new EnContact();
			enContact.setContactId(contactId);
			Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "+ contactId, null, null);
			while (phones.moveToNext()) {
				// 遍历所有的电话号码
				String phoneNumber = PhoneNumberTool.cleanse(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				enContact.getNumbers().add(phoneNumber);
				// System.out.println("phoneNumber ---> " + phoneNumber);
			}
			;
			phones.close();
			EN_CONTACTS.add(enContact);
		}
	}

	
	private void insertMessageLibrary() {
		final MessageLibrary messageLibrary = new MessageLibrary();
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(5000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				isInserting = true;
				// TODO Auto-generated method stub
				if (myDatabaseUtil == null) {
					myDatabaseUtil = DButil.getInstance(MainActivity.this);
				}
				Cursor cursor = myDatabaseUtil.fetchMessageLibrary("1");

				if (cursor != null && cursor.moveToNext()) {

				} else {
					for (int i = 0; i < messageLibrary.getMessageHoliday().length; i++) {
						myDatabaseUtil.insertDataMessage(
								messageLibrary.getMessageHoliday()[i], "1", "1");
					}
					for (int i = 0; i < messageLibrary.getMessageFunny().length; i++) {
						myDatabaseUtil.insertDataMessage(
								messageLibrary.getMessageFunny()[i], "2", "2");
					}
					for (int i = 0; i < messageLibrary.getMessageLove().length; i++) {
						myDatabaseUtil.insertDataMessage(
								messageLibrary.getMessageLove()[i], "3", "3");
					}
					// 电信
					for (int i = 0; i < messageLibrary.getMessageOperatorTele().length; i++) {
						myDatabaseUtil.insertDataMessage(
								messageLibrary.getMessageOperatorTele()[i],
								"4", "4");
					}
					// 移动
					for (int i = 0; i < messageLibrary
							.getMessageOperatorMobile().length; i++) {
						myDatabaseUtil.insertDataMessage(
								messageLibrary.getMessageOperatorMobile()[i],
								"5", "5");
					}
					// 联通
					for (int i = 0; i < messageLibrary.getMessageOperatorLink().length; i++) {
						myDatabaseUtil.insertDataMessage(
								messageLibrary.getMessageOperatorLink()[i],
								"6", "6");
					}
				}
				cursor.close();

				isInserting = false;
			}
		}).start();

	}

	
	
	/**
	 * 
	 * 检测归属地数据库是否存在于指定的SD卡路径上；
	 * 
	 * 存在： 打开数据库 ;  不存在: 复制数据文件到指定路径后打开
	 * 
	 */
	private void copyOrOpenPhoneDatabse() {
		
		filePath = "data/data/"+ getPackageName() +"/phone.db";
		System.out.println("filePath:" + filePath);
		final File jhPath = new File(filePath);
		
		// 查看数据库文件是否存在
		if (jhPath.exists()) {
			// 存在则直接返回打开的数据库
			phoneDatabaseUtil = new PhoneDatabaseUtil(filePath);

			System.out.println("成功打开数据库--->");

		} else {

			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED) // sd卡可用
					&& Environment.getExternalStorageDirectory().canWrite()) {

				new Thread(new Runnable() {

					@Override
					public void run() {

						if (!jhPath.exists()) {
							try {
								jhPath.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}

						try {
							// //得到资源
							AssetManager am = getAssets();
							// 得到数据库的输入流
							InputStream is = am.open(assets_db_name);

							BufferedOutputStream dest = null;
							ZipInputStream zis = new ZipInputStream(
									new BufferedInputStream(is));
							ZipEntry entry;
							int BUFFER = 1024 * 16;
							while ((entry = zis.getNextEntry()) != null) {

								int count;
								byte data[] = new byte[BUFFER];
								FileOutputStream fos = new FileOutputStream(
										jhPath);
								dest = new BufferedOutputStream(fos, BUFFER);
								while ((count = zis.read(data, 0, BUFFER)) != -1) {
									dest.write(data, 0, count);
								}
								dest.flush();
								dest.close();
							}
							zis.close();
						} catch (IOException e) {
							e.printStackTrace();
							jhPath.delete();
						}

						phoneDatabaseUtil = new PhoneDatabaseUtil(filePath);

						handler.sendEmptyMessage(5);

					}
				}).start();

			} else {
				System.out.println("sd卡 不可用  --->");
			}
		}
	}

	@Override
	public void onBackPressed() {

		handleBackPressed();
		
	}

	public void handleBackPressed() {

		switch (state) {

		// //////////////通话记录
		case STATE_HOME:

			if (fram_tip_mask.getVisibility() == View.VISIBLE) {
				fram_tip_mask.setVisibility(View.GONE);

				boolean isFirst = sf_fist_in.getBoolean(FIRSTBACKUP, true);

				if (isFirst) {
					showFirskBackUpDialog();

					Editor editor = sf_fist_in.edit();
					editor.putBoolean(FIRSTBACKUP, false);
					editor.commit();
				}

			} else if (calllog_search_ly.getVisibility() == View.VISIBLE) {

				et_search_calllog.setText("");

				calllog_search_ly.setVisibility(View.GONE);
				recent_calllog.setVisibility(View.VISIBLE);

			} else if (dialing_panel.getVisibility() != View.GONE
					|| lv_contact_association.getVisibility() != View.GONE
					|| mAcrInputPanel.getVisibility() != View.GONE) {
				
				et_dialing_number.setText("");

				if (dialing_panel.getVisibility() != View.GONE) {
					dialing_panel.setAnimation(AnimationUtils.loadAnimation(this, R.anim.dialing_out));
					dialing_panel.setVisibility(View.GONE);

					Editor editor = sf_fist_in.edit();
					editor.putBoolean(SF_KEY_FIRST_JING, false);
					editor.commit();
				}

				btn_dialing.setBackgroundResource(R.drawable.btn_dialing_panel);

				if (mAcrInputPanel.getVisibility() != View.GONE) {
					
					mAcrInputPanel.setIntpuText("");
					Animation an = AnimationUtils.loadAnimation(MainActivity.this, R.anim.huxing_back);
					an.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {}

						@Override
						public void onAnimationRepeat(Animation animation) {}

						@Override
						public void onAnimationEnd(Animation animation) {
							mAcrInputPanel.setVisibility(View.GONE);
						}
					});
					
					mAcrInputPanel.startAnimation(an);

					btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
					huxing_state = HUXING_DIALING_STATE_PANEL;
					
				}

				if (lv_contact_association.getVisibility() == View.VISIBLE
						|| ln_association_tips.getVisibility() == View.VISIBLE) {
					lv_contact_association.setVisibility(View.GONE);
					ln_association_tips.setVisibility(View.GONE);
				}

				dialing_state = DIALING_STATE_PANEL;

			} else if ( dialog_pick_contact!=null && dialog_pick_contact.isShowing()) {

				dialog_pick_contact.dismiss();

			} else if (mAcrInputPanel.getVisibility() != View.GONE) {

				mAcrInputPanel.setIntpuText("");

				Animation an = AnimationUtils.loadAnimation(MainActivity.this,
						R.anim.huxing_back);

				an.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						mAcrInputPanel.setVisibility(View.GONE);
					}
				});
				mAcrInputPanel.startAnimation(an);

				btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
				huxing_state = HUXING_DIALING_STATE_PANEL;

				if (lv_contact_association.getVisibility() == View.VISIBLE
						|| ln_association_tips.getVisibility() == View.VISIBLE) {
					lv_contact_association.setVisibility(View.GONE);
					ln_association_tips.setVisibility(View.GONE);

				}

				dialing_state = DIALING_STATE_PANEL;

			} else {

				SharedPreferences encryption = getSharedPreferences(EncryptionActivity.SF_NAME, 0);
				String pw = encryption.getString(EncryptionActivity.KEY_PWD, "");

				if (!pw.equals("")) {

					SharedPreferences.Editor editor = encryption.edit();
					editor.putBoolean(EncryptionActivity.KEY_ISENCRYPTION, true);
					editor.putBoolean(EncryptionActivity.KEY_ISREINPUTPWD, true);
					editor.commit();
				}

				fakeExit();
			}
			break;

		// //////////////联系人列表
		case STATE_CONTACT:

			if (fram_tip_mask.getVisibility() == View.VISIBLE) {
				fram_tip_mask.setVisibility(View.GONE);

				Editor editor = sf_fist_in.edit();
				editor.putBoolean(SF_KEY_FIRST_CONTACTS, false);
				editor.commit();

			} else if (contactLayout != null && !contactLayout.onBackPressed()) {

				fakeExit();
			}

			break;

		// //////////////短信会话列表
		case STATE_MESSAGE_LIST:

			if (messageListLayout != null) {

				if (!messageListLayout.onBackPress()) {
					fakeExit();
				}
			}

			break;

		// //////////////系统设置
		case STATE_SETTING:

			if (fram_tip_mask.getVisibility() == View.VISIBLE) {
				fram_tip_mask.setVisibility(View.GONE);

				Editor editor = sf_fist_in.edit();
				editor.putBoolean(SF_KEY_FIRST_SETTINGS, false);
				editor.commit();

			} else {

				fakeExit();
			}

			break;

		// 收藏联系人 (快捷拨号)
		case STATE_COLLECT_CONTACT:

			if (isModifyCollectContactMode) {

				cancelModifyMode();

			} else if(dialog_collect_pick_contact!=null && dialog_collect_pick_contact.isShowing())
			{
				dialog_collect_pick_contact.dismiss();
				
			} else {

				ln_collect_contact.setVisibility(View.GONE);

				if (back_index == 0) {
					dialing_panel.setVisibility(View.VISIBLE);
					dialing_state = DIALING_STATE_CALL;
				} else {
					mAcrInputPanel.setVisibility(View.VISIBLE);
					huxing_state = HUXING_DIALING_STATE_CALL;
				}

				lv_contact_association.setVisibility(View.VISIBLE);

				state = STATE_HOME;
			}
			break;

		default:
			break;
		}
	}

	
	/**
	 * 
	 * 非退出程序，只是把应用隐藏到后台，相当于点击home键
	 * 
	 */
	void fakeExit() {
		
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
		
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// case R.id.huxing: //弧形键盘
		//
		// if(!isSupport) {
		// Toast.makeText(this, "弧形键盘目前不支持此型号的手机", Toast.LENGTH_SHORT).show();
		// break;
		// }
		//
		// if (ln_collect_contact != null) {
		// btn_dialing.setBackgroundResource(R.drawable.btn_dialing_panel);
		// ln_collect_contact.setVisibility(View.GONE);
		//
		// }
		//
		//
		// if(lv_contact_association.getVisibility()==View.VISIBLE ||
		// ln_association_tips.getVisibility()==View.VISIBLE)
		// {
		// lv_contact_association.setVisibility(View.GONE);
		// ln_association_tips.setVisibility(View.GONE);
		// }
		//
		// if (ln_lv_all_contacts != null&& ln_lv_all_contacts.getVisibility()
		// != View.INVISIBLE) {
		// ln_lv_all_contacts.setVisibility(View.INVISIBLE);
		// }
		//
		// if (dialing_panel.getVisibility() != View.GONE) {
		//
		// dialing_panel.setVisibility(View.GONE);
		// btn_dialing.setBackgroundResource(R.drawable.btn_dialing_panel);
		//
		// if (mAcrInputPanel.getVisibility() == View.GONE) {
		// mAcrInputPanel.setAnimation(huXingAnimation);
		//
		// huxing_state = HUXING_DIALING_STATE_CALL;
		//
		// mAcrInputPanel.setVisibility(View.VISIBLE);
		// btn_huxing.setBackgroundResource(R.drawable.btn_dialing);
		//
		// if (et_dialing_number.getText().toString() != "") {
		// mAcrInputPanel.setIntpuText(et_dialing_number.getText().toString());
		// }
		//
		// state = STATE_HOME;
		// dialing_state = DIALING_STATE_PANEL;
		//
		// btn_dialing.setBackgroundResource(R.drawable.btn_dialing_panel);
		//
		// }
		//
		// } else {
		//
		// if (state == STATE_CONTACT) // 在联系人列表界面中点击了弧形键盘
		// {
		// if (!contactLayout.pressDialingButton())
		// {
		// ver_scroller.snapToScreen(0);
		// top_scroller.snapToScreen(4);
		// ver_scroller.removeOnScrollerFinish();
		// l_scrolelr.snapToScreen(0);
		//
		// btn_setting.setBackgroundResource(R.drawable.btn_bottom_setting);
		//
		// if(isNeedRefresh)
		// {
		// refresh();
		// }
		// state = STATE_HOME;
		// dialing_state = DIALING_STATE_PANEL;
		// }
		//
		// }
		//
		// if (huxing_state == HUXING_DIALING_STATE_CALL) { //打电话
		//
		// if (!mAcrInputPanel.getInputText().equals("")) {
		// btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
		//
		// Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+
		// mAcrInputPanel.getInputText()));
		// startActivity(intent);
		//
		// dimissPanelAndReSet();
		// huxing_state = HUXING_DIALING_STATE_PANEL;
		// mAcrInputPanel.setIntpuText("");
		// }
		// }else if (mAcrInputPanel.getVisibility() == View.GONE) {
		//
		// mAcrInputPanel.setAnimation(huXingAnimation);
		//
		// mAcrInputPanel.setVisibility(View.VISIBLE);
		// btn_huxing.setBackgroundResource(R.drawable.btn_dialing);
		// huxing_state = HUXING_DIALING_STATE_CALL;
		// if (ver_scroller.getCurScreen() != 0) {
		// ver_scroller.snapToScreen(0);
		// top_scroller.snapToScreen(4);
		// l_scrolelr.snapToScreen(0);
		// }
		//
		// btn_setting.setBackgroundResource(R.drawable.btn_bottom_setting);
		//
		// if(isNeedRefresh)
		// {
		// refresh();
		// }
		//
		// state = STATE_HOME;
		//
		// dialing_state = DIALING_STATE_PANEL;
		//
		// if (homeCallsAdapter.menu != null) {
		// LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
		// LinearLayout.LayoutParams.FILL_PARENT,
		// LinearLayout.LayoutParams.WRAP_CONTENT, 0);
		// lp.setMargins(0, 0, 0,
		// -homeCallsAdapter.menu.getHeight());
		//
		// homeCallsAdapter.menu.setLayoutParams(lp);
		// }
		// }
		// }
		//
		// // collapseList();
		//
		// if(contactLayout!=null) {
		// // contactLayout.collapseList();
		// }
		//
		// if(messageListLayout!=null) {
		// messageListLayout.collapse();
		// }
		// break;

		case R.id.btn_dialing: // 拨号盘

			if (state != STATE_HOME) // 非通话记录界面状态
			{
				if (state == STATE_CONTACT) // 在联系人列表界面中点击了拨号盘按钮
				{
					if (!contactLayout.pressDialingButton()) {
						switchToCallLogs();
						
					}

				} else {

					switchToCallLogs();
					
				}

			} else {

				
				switch (dialing_state) { // 拨号盘的状态

				case DIALING_STATE_PANEL:

					if (ln_collect_contact != null) {
						btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
						ln_collect_contact.setVisibility(View.GONE);
					}

					if (lv_contact_association.getVisibility() == View.VISIBLE
							|| ln_association_tips.getVisibility() == View.VISIBLE) {
						lv_contact_association.setVisibility(View.GONE);
						ln_association_tips.setVisibility(View.GONE);
					}

					if(dialog_collect_pick_contact!=null && dialog_collect_pick_contact.isShowing())
					{
						dialog_collect_pick_contact.dismiss();
					}

					// 隐藏弧形键盘
					if (mAcrInputPanel.getVisibility() != View.GONE) {

						mAcrInputPanel.setVisibility(View.GONE);
						btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
						huxing_state = HUXING_DIALING_STATE_PANEL;

						et_dialing_number.setText(mAcrInputPanel.getInputText());
					}

					dialing_panel.setAnimation(AnimationUtils.loadAnimation(MainActivity.this, R.anim.dialing_in));
					dialing_panel.setVisibility(View.VISIBLE);
					btn_dialing.setBackgroundResource(R.drawable.btn_dialing);

					dialing_state = DIALING_STATE_CALL;

					// 输入号码的edittext有内容时，触发联想
					if (!et_dialing_number.getText().toString().equals("") && searchContactsAdapter!=null) {
						new Thread(new Runnable() {
							@Override
							public void run() {
								searchContactsAdapter.filter(et_dialing_number.getText().toString());
							}
						}).start();
					}

					boolean b = sf_fist_in.getBoolean(SF_KEY_FIRST_JING, true);

					if (b) {
						fram_tip_mask.removeAllViews();
						fram_tip_mask.addView(LayoutInflater.from(
								MainActivity.this).inflate(
								R.layout.tip_dialing_panel_first, null));
						fram_tip_mask.setVisibility(View.VISIBLE);
					}

					break;

				case DIALING_STATE_CALL: // 拨打电话

					if (state == STATE_COLLECT_CONTACT) {
						ln_collect_contact.setVisibility(View.GONE);

						dialing_panel.setVisibility(View.VISIBLE);
						lv_contact_association.setVisibility(View.VISIBLE);
						state = STATE_HOME;

					} else if (dialing_panel.getVisibility() == View.GONE) {

						dialing_panel.setVisibility(View.VISIBLE);

					} else if (!et_dialing_number.getText().toString()
							.equals("")) {
						Intent intent = new Intent(Intent.ACTION_CALL,
								Uri.parse("tel:"+ et_dialing_number.getText().toString()));
						startActivity(intent);

						dimissPanelAndReSet();
					}

					break;

				default:
					break;
				}
			}

			break;

		case R.id.recent_call_img: // 最近通话

			if (popup == null) {
				View view = LayoutInflater.from(this).inflate(
						R.layout.dialog_recent_call, null);
				popup = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
						LayoutParams.WRAP_CONTENT, true);
				popup.setBackgroundDrawable(new BitmapDrawable()); // 按返回键 以及点击
																	// 区域外 消失
																	// (神奇的语句)
				popup.setOutsideTouchable(true);

				RecentCallOnClickListener menuClickListener = new RecentCallOnClickListener();

				((TextView) view.findViewById(R.id.pop_all))
						.setOnClickListener(menuClickListener);
				((TextView) view.findViewById(R.id.pop_accept))
						.setOnClickListener(menuClickListener);
				((TextView) view.findViewById(R.id.pop_reject))
						.setOnClickListener(menuClickListener);
				((TextView) view.findViewById(R.id.pop_clear))
						.setOnClickListener(menuClickListener);
			}

			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);

			int view_width = getResources().getDimensionPixelSize(
					R.dimen.pop_width);

			int screen_wdith = metric.widthPixels;

			System.out.println(" view_width ---> " + view_width
					+ " screen_wdith ---> " + screen_wdith);

			popup.showAsDropDown(findViewById(R.id.recent_call_img),
					screen_wdith - view_width, 10);
			break;

		case R.id.img_goto_search:
			recent_calllog.setVisibility(View.GONE);
			calllog_search_ly.setVisibility(View.VISIBLE);
			break;

		/////// 编辑收藏联系人
		case R.id.btn_edit_collect_contact:

			if (!isModifyCollectContactMode) {
				gotoModifyMode();
			} else {
				cancelModifyMode();
			}
			break;

		/////// 退出收藏联系人
		case R.id.btn_out_collect_contact:

			ln_collect_contact.setVisibility(View.GONE);

			if (back_index == 0) {
				dialing_panel.setVisibility(View.VISIBLE);
				dialing_state = DIALING_STATE_CALL;
			} else {
				mAcrInputPanel.setVisibility(View.VISIBLE);
				huxing_state = HUXING_DIALING_STATE_PANEL;
			}

			lv_contact_association.setVisibility(View.VISIBLE);

			state = STATE_HOME;

			break;

		case R.id.delete_search_info:

			et_search_calllog.setText("");

			recent_calllog.setVisibility(View.VISIBLE);
			calllog_search_ly.setVisibility(View.GONE);

			break;

		/////// 底栏   联系人列表
		case R.id.btn_contacts:
			switchToContacts();
			break;

		/////// 底栏    短信会话列表
		case R.id.btn_messages:
			swithcToConversation();
			break;

		/////// 底栏    系统设置
		case R.id.btn_settings:
			switchToSettings();
			break;

		default:
			break;
		}

	}

	/**
	 * 
	 * 触发震动  和 播放按钮音
	 * 
	 */
	private void playSetting() {

		SharedPreferences dialing_checkbox_state = getSharedPreferences(DialingSettingActivity.SF_NAME, 0);
		boolean isVibrator = dialing_checkbox_state.getBoolean(
				"press_state_cb1", true);
		boolean isSound = dialing_checkbox_state.getBoolean("press_state_cb2",
				false);

		Vibrator vbr = null;
		if (isVibrator) {
			vbr = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vbr.vibrate(40);
		}
		if (isSound) {
			MediaPlayer mp = null;
			try {

				mp = MediaPlayer.create(MainActivity.this, R.raw.congestion);
				mp.start();

			} catch (Exception e) {
				e.printStackTrace();
			}

			mp.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.release();
				}
			});
		}
	}


	class DialingDeleteTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			String input = et_dialing_number.getText().toString();

			switch (event.getAction()) {

			case MotionEvent.ACTION_UP:

				playSetting();

				if (input.length() > 0) {
					et_dialing_number.setText(input.substring(0,
							input.length() - 1));
				}

				isLongPresseding = false;

				break;

			default:
				break;
			}
			return false;
		}
	}

	// 输入电话号码后 触发的联想界面
	public void triggerNumberAssociation() {
		lv_contact_association.setVisibility(View.VISIBLE);

	}

	// 号码联想 界面
	class NumberAssociationOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_contact_association:

				ln_association_tips.setVisibility(View.GONE);
				lv_contact_association.setVisibility(View.GONE);

				reSetTheCallLogsView();

				Intent intent = new Intent(MainActivity.this,
						AddContactActivity.class);
				intent.putExtra(AddContactActivity.DATA_TYPE,
						AddContactActivity.TYPE_ADD_CONTACT);
				intent.putExtra(AddContactActivity.DATA_STRANGE_NUMBER,
						et_dialing_number.getText().toString());

				startActivity(intent);

				break;

			case R.id.add_contact_in_association:

				ln_association_tips.setVisibility(View.GONE);
				lv_contact_association.setVisibility(View.GONE);
				dialing_panel.setVisibility(View.GONE);

				btn_dialing.setBackgroundResource(R.drawable.btn_dialing_panel);

				if (mAcrInputPanel.getVisibility() != View.GONE) {
					mAcrInputPanel.setAnimation(AnimationUtils.loadAnimation(
							MainActivity.this, R.anim.dialing_out));
					mAcrInputPanel.setVisibility(View.GONE);
					btn_huxing.setBackgroundResource(R.drawable.btn_arc_panel);
					huxing_state = HUXING_DIALING_STATE_PANEL;
				}

				dialing_state = DIALING_STATE_PANEL;

				List<ContactBean> contacts = new ArrayList<ContactBean>();
				String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
				Cursor cursor = getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, sortOrder);
				while (cursor.moveToNext()) {
					ContactBean contactBean = new ContactBean();
					contactBean.setContact_id(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
					contactBean.setPhoto_id(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
					contactBean.setNick(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
					contacts.add(contactBean);
				}
				cursor.close();

				add_to_number = et_dialing_number.getText().toString();

				StrangeNumberContactsAdapter strangeNumberContactsAdapter = new StrangeNumberContactsAdapter(
						MainActivity.this, contacts,
						new OnPickContactFinishClickListener());
				
				if(dialog_pick_contact==null)
				{
					dialog_pick_contact = new Dialog(MainActivity.this, R.style.theme_myDialog);
					
					View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_pick_contact, null);

					dialog_pick_contact.setContentView(view);
					dialog_pick_contact.setCanceledOnTouchOutside(true);
					
					lv_pick_contact = (ListView) view.findViewById(R.id.lv_pick_contact);
				}
				
				lv_pick_contact.setAdapter(strangeNumberContactsAdapter);

				dialog_pick_contact.show();
				
				break;

			case R.id.send_sms_association:

				ln_association_tips.setVisibility(View.GONE);
				lv_contact_association.setVisibility(View.GONE);
				dialing_panel.setVisibility(View.GONE);

				String number = et_dialing_number.getText().toString();

				reSetTheCallLogsView();

				String thread_id = NewMessageActivity.queryThreadIdByNumber(
						MainActivity.this, number);

				Intent m_intent = new Intent(MainActivity.this,
						NewMessageActivity.class);
				m_intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
				m_intent.putExtra(NewMessageActivity.DATA_NUMBER, number);

				startActivity(m_intent);

				break;

			default:
				break;
			}
		}
	}

	class TipClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {

			case R.id.delete_one_info:

				getContentResolver().delete(CallLog.Calls.CONTENT_URI,CallLog.Calls._ID + " = " + call_id, null);

				// 删除
				homeCallsAdapter.deleteById(type, call_id);

				// 刷新
				homeCallsAdapter.changeToType(type);

				Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

				dialog.cancel();

				break;

			case R.id.delete_all_info:

				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("正在删除，请稍等");
				progressDialog.show();

				System.out.println("call_number : --->" + call_number);
				new Thread(new Runnable() {

					@Override
					public void run() {
						int result = getContentResolver().delete(
								CallLog.Calls.CONTENT_URI,
								CallLog.Calls.NUMBER + " = " + call_number,
								null);
						System.out.println(" 已删除  --->" + result);

						handler.sendEmptyMessage(2);
					}
				}).start();

				dialog.cancel();

				break;

			case R.id.btn_top_tips_no:
				dialog.cancel();
				break;

			case R.id.btn_top_tips_yes:

					progressDialog = new ProgressDialog(MainActivity.this);
					progressDialog.setMessage("正在删除，请稍等");
					progressDialog.show();

					System.out.println("call_number : --->" + call_number);
					new Thread(new Runnable() {

						@Override
						public void run() {
							int result = getContentResolver().delete(CallLog.Calls.CONTENT_URI,CallLog.Calls.NUMBER + " = " + call_number,null);
							System.out.println(" 已删除  --->" + result);
							handler.sendEmptyMessage(2);
						}
					}).start();
					
				dialog.cancel();
				break;

			default:
				break;
			}
		}
	}

	class OnMenuItemClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (homeCallsAdapter.menu != null) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -homeCallsAdapter.menu.getHeight());

				homeCallsAdapter.menu.setLayoutParams(lp);
			}

			switch (v.getId()) {
			case R.id.menu_call:

				String phone_number = (String) v.getTag();
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phone_number));
				startActivity(intent);

				break;

			case R.id.menu_sms_detail:

				String numbers = (String) v.getTag();

				String thread_id = NewMessageActivity.queryThreadIdByNumber(
						MainActivity.this, numbers);

				Intent m_intent = new Intent(MainActivity.this,NewMessageActivity.class);
				m_intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
				m_intent.putExtra(NewMessageActivity.DATA_NUMBER, numbers);

				startActivity(m_intent);

				break;

			case R.id.menu_contact_detail:

				String number = (String) v.getTag();
				System.out.println("number ---> " + number);
				String nweNumber = number.replace("(", "").replace(") ", "")
						.replace("-", "").replace(" ", "");
				System.out.println("nweNumber ---> " + nweNumber);
				checkIsStrangeNumber(number);

				break;

			case R.id.menu_delete:

				String[] sp = ((String) v.getTag()).split(":");

				call_id = Long.valueOf(sp[0]);
				call_number = sp[1];
				nick = sp[3];

				View view = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.home_delete_tips, null);

				dialog = new Dialog(MainActivity.this, R.style.theme_myDialog);
				dialog.setContentView(view);
				dialog.setCanceledOnTouchOutside(true);
				dialog.show();

				Button delete_one_info = (Button) dialog
						.findViewById(R.id.delete_one_info);
				Button delete_all_info = (Button) dialog
						.findViewById(R.id.delete_all_info);

				String source = null;
				if (!nick.equals("")) {
					source = "清空 " + nick + " 的所有通话记录";
					delete_all_info.setText(Html.fromHtml(source.replace(nick,
							"<font color='#3d8eba'>" + nick + "</font>")));
				} else {
					source = "清空 " + call_number + " 的所有通话记录";
					delete_all_info.setText(Html.fromHtml(source.replace(
							call_number, "<font color='#3d8eba'>" + call_number
									+ "</font>")));
				}

				delete_one_info.setOnClickListener(new TipClickListener());
				delete_all_info.setOnClickListener(new TipClickListener());

				break;

			case R.id.menu_remind:
				String number1 = (String) v.getTag();
				goToContactRemind(number1);
				break;

			case R.id.menu_add_to:

				String[] values = ((String) v.getTag()).split(":");
				add_to_number = values[0];

				int contact_id = Integer.valueOf(values[1]);

				if (dialog_add_to == null) {
					dialog_add_to = new Dialog(MainActivity.this,
							R.style.theme_myDialog);
					View d_view = LayoutInflater.from(MainActivity.this)
							.inflate(R.layout.dialog_home_add_to, null);
					dialog_add_to_view = (LinearLayout) d_view
							.findViewById(R.id.ln_content);
					dialog_add_to.setContentView(d_view);
					dialog_add_to.setCanceledOnTouchOutside(true);

					PopAddToClickListener popAddToClickListener = new PopAddToClickListener();
					((Button) dialog_add_to.findViewById(R.id.add_to_new))
							.setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_already))
							.setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_black))
							.setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_white))
							.setOnClickListener(popAddToClickListener);
					((Button) dialog_add_to.findViewById(R.id.add_to_desktop))
							.setOnClickListener(popAddToClickListener);
				}

				System.out.println("  contact_id --->" + contact_id);

				if (contact_id != -1) // 有联系人
				{
					// 隐藏线
					dialog_add_to_view.getChildAt(1).setVisibility(View.GONE);
					dialog_add_to_view.getChildAt(3).setVisibility(View.GONE);

					// dialog_add_to_view.getChildAt(7).setVisibility(View.VISIBLE);

					((Button) dialog_add_to.findViewById(R.id.add_to_desktop))
							.setVisibility(View.VISIBLE);

					((Button) dialog_add_to.findViewById(R.id.add_to_new))
							.setVisibility(View.GONE);
					((Button) dialog_add_to.findViewById(R.id.add_to_already))
							.setVisibility(View.GONE);

				} else { // 没有联系人

					// 显示线
					dialog_add_to_view.getChildAt(1)
							.setVisibility(View.VISIBLE);
					// dialog_add_to_view.getChildAt(3).setVisibility(View.VISIBLE);

					dialog_add_to_view.getChildAt(7).setVisibility(View.GONE);

					((Button) dialog_add_to.findViewById(R.id.add_to_desktop))
							.setVisibility(View.GONE);

					((Button) dialog_add_to.findViewById(R.id.add_to_new))
							.setVisibility(View.VISIBLE);
					((Button) dialog_add_to.findViewById(R.id.add_to_already))
							.setVisibility(View.VISIBLE);

				}

				dialog_add_to.show();

				break;

			default:
				break;
			}
		}
	}

	// 判断是否为陌生号码
	private void checkIsStrangeNumber(String number) {

		boolean isStrangeNumber = true;
		String contact_id = "";
		String data[] = PhoneNumberTool.getContactInfo(this, number);

		if (data[2] != null) {
			isStrangeNumber = false;
			contact_id = data[2];
		}

		if (!isStrangeNumber) // 已有联系人
		{

			Intent intent = new Intent(MainActivity.this,
					AddContactActivity.class);
			intent.putExtra(AddContactActivity.DATA_TYPE,
					AddContactActivity.TYPE_DETAIL_CONTACT);
			intent.putExtra(AddContactActivity.DATA_CONTACT_ID, contact_id);
			startActivity(intent);

		} else { // 陌生号码

			Intent strange_intent = new Intent(MainActivity.this,
					StrangeNumberActivity.class);
			strange_intent.putExtra(StrangeNumberActivity.STRANGE_NUMBER,
					number);

			startActivity(strange_intent);
		}
	}

	private void goToContactRemind(String number) {
		String contact_id = "";
		String data[] = PhoneNumberTool.getContactInfo(this, number);

		if (data[2] != null) {
			contact_id = data[2];
		}

		Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
		intent.putExtra(AddContactActivity.DATA_TYPE,
				AddContactActivity.TYPE_DETAIL_CONTACT);
		intent.putExtra(AddContactActivity.DATA_CONTACT_ID, contact_id);
		intent.putExtra(AddContactActivity.DATA_GO_REMIND, true);
		startActivity(intent);

	}

	private TextWatcher searchCallLog = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {

			if (s == null) {
				search_img.setVisibility(View.VISIBLE);
				delete_search_info.setVisibility(View.GONE);
			}

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			homeCallsAdapter.getFilter().filter(s);

			if (s != null) {

				search_img.setVisibility(View.GONE);
				delete_search_info.setVisibility(View.VISIBLE);

			} else {

				search_img.setVisibility(View.VISIBLE);
				delete_search_info.setVisibility(View.GONE);
			}
		}

	};

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

		@Override
		public void afterTextChanged(Editable s) {

			if (searchContactsAdapter != null && !s.toString().equals("")) // 触发联想
			{
				final String ss = new String(s.toString());
				new Thread(new Runnable() {

					@Override
					public void run() {
						searchContactsAdapter.filter(ss);
					}
				}).start();
			}

			// 初始化每种字体大小小，最多可放多少个字
			if (DIALING_NUMBER_BIG_LENGTH == 0) {
				int content_width = et_dialing_number.getWidth();

				Paint p = new Paint();
				p.setTextSize(et_dialing_number.getTextSize());

				int big_width = (int) p.measureText("5");

				// System.out.println("  big_width  ---> " + big_width);
				// System.out.println("  content_width  ---> " + content_width);

				DIALING_NUMBER_BIG_LENGTH = (content_width / big_width);

				// System.out.println(" DIALING_NUMBER_BIG_LENGTH  ---> "+
				// DIALING_NUMBER_BIG_LENGTH);

			}

			String ss = s.toString();

			// System.out.println("  ss  --->" + ss);

			if (ss.length() == 1 && ss.toString().equals("#")
					&& mAcrInputPanel.getVisibility() != View.VISIBLE) {

				back_index = 0;

				Toast.makeText(MainActivity.this, "正在载入收藏联系人",
						Toast.LENGTH_SHORT).show();

				new Thread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub

						collect_contacts = new ArrayList<ContactBean>();
//						String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
//						Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,new String[] {ContactsContract.Contacts._ID,ContactsContract.Contacts.DISPLAY_NAME },ContactsContract.Contacts.STARRED + " = 1", null, sortOrder);
//						System.out.println(cursor.getCount()+ "===========================contact=count");

//						while (cursor.moveToNext()) {
//							ContactBean contactBean = new ContactBean();
//							contactBean.setContact_id(cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts._ID)));
//							contactBean.setNick(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
//
//							Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
//											ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactBean.getContact_id(),null, null);
//							if (phones.moveToNext()) {
//								String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//								contactBean.setNumber(phone);
//							}
//
//							phones.close();
//
//							collect_contacts.add(contactBean);
//						}
//						cursor.close();

						if (all_contacts == null) {
							all_contacts = getAllContact();
						}

						handler.sendEmptyMessage(4);
					}
				}).start();

				ln_collect_contact.setVisibility(View.VISIBLE);

				dialing_panel.setVisibility(View.GONE);
				ln_association_tips.setVisibility(View.GONE);
				lv_contact_association.setVisibility(View.GONE);

				state = STATE_COLLECT_CONTACT;

			} else {

				if (!s.toString().equals("")
						&& lv_contact_association.getVisibility() == View.GONE) {
					lv_contact_association.setVisibility(View.VISIBLE);

				}
			}

			// 根据输入在字符长度，改变字体的大小
			int len_size = s.toString().length();

			System.out.println("cur  text size --->" + s.toString().length());

			if (len_size < DIALING_NUMBER_BIG_LENGTH) {
				et_dialing_number.setTextSize(DIALING_NUMBER_BIG);
				// if (huxing_et_dialing_number != null) {
				// huxing_et_dialing_number.setTextSize(DIALING_NUMBER_BIG);
				// }
			}

			if (DIALING_NUMBER_BIG_LENGTH != 0
					&& len_size > DIALING_NUMBER_BIG_LENGTH) {
				et_dialing_number.setTextSize(DIALING_NUMBER_MID);
				if (DIALING_NUMBER_MID_LENGTH == 0) {
					int content_width = et_dialing_number.getWidth();

					Paint p = new Paint();
					p.setTextSize(et_dialing_number.getTextSize());

					int mid_width = (int) p.measureText("5");

					// System.out.println("  mid_width  ---> " + mid_width);
					// System.out.println("  content_width  ---> " +
					// content_width);

					DIALING_NUMBER_MID_LENGTH = (content_width / mid_width);
					// System.out.println(" DIALING_NUMBER_MID_LENGTH  ---> " +
					// DIALING_NUMBER_MID_LENGTH);
				}

			}

			if (DIALING_NUMBER_MID_LENGTH != 0
					&& len_size > DIALING_NUMBER_MID_LENGTH) {

				et_dialing_number.setTextSize(DIALING_NUMBER_SMALL);

				if (DIALING_NUMBER_SMALL_LENGTH == 0) {
					int content_width = et_dialing_number.getWidth();

					Paint p = new Paint();
					p.setTextSize(et_dialing_number.getTextSize());

					int small_width = (int) p.measureText("5");

					System.out.println("  small_width  ---> " + small_width);
					System.out
							.println("  content_width  ---> " + content_width);

					DIALING_NUMBER_SMALL_LENGTH = (content_width / small_width);

					System.out.println(" DIALING_NUMBER_SMALL_LENGTH  ---> "
							+ DIALING_NUMBER_SMALL_LENGTH);
				}
			}

		};
	};

	void handleTextChange(String s) {
		if (searchContactsAdapter != null && !s.toString().equals("")) // 触发联想
		{
			final String ss = new String(s.toString());
			new Thread(new Runnable() {

				@Override
				public void run() {
					searchContactsAdapter.filter(ss);
				}
			}).start();
		}

		String ss = s.toString();

		System.out.println("  ss  --->" + ss);

		if (ss.length() == 1 && ss.toString().equals("#")) {
			// isFirstInputJingHao = false;

			back_index = 1;

			Toast.makeText(MainActivity.this, "正在载入收藏联系人", Toast.LENGTH_SHORT)
					.show();

			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					collect_contacts = new ArrayList<ContactBean>();
//					String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
//					Cursor cursor = getContentResolver().query(
//							ContactsContract.Contacts.CONTENT_URI,
//							new String[] { ContactsContract.Contacts._ID,
//									ContactsContract.Contacts.DISPLAY_NAME },
//							ContactsContract.Contacts.STARRED + " = 1", null,
//							sortOrder);
//					System.out.println(cursor.getCount()
//							+ "===========================contact=count");
//
//					while (cursor.moveToNext()) {
//						ContactBean contactBean = new ContactBean();
//						contactBean.setContact_id(cursor.getLong(cursor
//								.getColumnIndex(ContactsContract.Contacts._ID)));
//						contactBean.setNick(cursor.getString(cursor
//								.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
//
//						Cursor phones = getContentResolver()
//								.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//										new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
//										ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//												+ " = "
//												+ contactBean.getContact_id(),
//										null, null);
//						if (phones.moveToNext()) {
//							String phone = phones.getString(phones
//									.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//							contactBean.setNumber(phone);
//						}
//
//						phones.close();
//
//						collect_contacts.add(contactBean);
//					}
//					cursor.close();

					if (all_contacts == null) {
						all_contacts = getAllContact();
					}

					handler.sendEmptyMessage(4);
				}
			}).start();

			ln_collect_contact.setVisibility(View.VISIBLE);

			mAcrInputPanel.setVisibility(View.GONE);
			ln_association_tips.setVisibility(View.GONE);
			lv_contact_association.setVisibility(View.GONE);

			state = STATE_COLLECT_CONTACT;

		} else {

			if (!s.toString().equals("")
					&& lv_contact_association.getVisibility() == View.GONE) {
				lv_contact_association.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

		isNotifacation = false;
		// System.out.println(" ---- onStop  ----> ");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		SharedPreferences spf = getSharedPreferences("position", 0);
		int position = spf.getInt("positionvalue", 0);

		if (position > 0) {
			SharedPreferences.Editor editor = spf.edit();
			editor.putInt("positionvalue", 0);
			editor.commit();
		}

		SharedPreferences remind = getSharedPreferences("remindPosition", 0);
		int value = remind.getInt("remindPositionValue", 0);

		if (value > 0) {

			SharedPreferences.Editor editor = spf.edit();
			editor.putInt("remindPositionValue", 0);
			editor.commit();
		}

		if (phoneDatabaseUtil != null) {
			phoneDatabaseUtil.close();
		}

		if (!isInserting) {
			DButil.close();
		}

		// 解除数据库监听
		if (contactProviderObserver != null) {
			getContentResolver().unregisterContentObserver(contactProviderObserver);
		}


		if (smsProviderObserver != null) {
			getContentResolver().unregisterContentObserver(smsProviderObserver);
		}

	}

	/**
	 * 根据电话号码 查询 归属地
	 * @param number
	 * @return
	 */
	public static String CheckNumberArea(String number) {

		try {
			if (phoneDatabaseUtil != null && number != null) {
				String newNum = PhoneNumberTool.cleanse(number);
				if (newNum.length() >= 11) {
					return MainActivity.phoneDatabaseUtil.fetch(newNum
							.substring(0, 7));
				} else {
					return "未知归属地";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return "";
	}
	
	/**
	 * 根据电话号码 查询 是否为加密联系人
	 * @param number
	 * @return
	 */
	public static boolean checkIsEnContactByNumber(String number)
	{
		boolean b = false;
		
		if(MainActivity.isEncryption && (MainActivity.EN_CONTACTS.size()>0))
		{
			for(EnContact ec:MainActivity.EN_CONTACTS)
			{
				for(String encryption_number:ec.getNumbers())
				{
					encryption_number = PhoneNumberTool.cleanse(encryption_number);
					if(encryption_number.equals(number))
					{
						b = true;
						break;
					}
				}
			}
		}
		
		return b;
	}
	

	public void triggerCall(String number) {
		System.out.println(" number  -->" + number);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));
		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		dimissPanelAndReSet();
	}

	public void triggetAssocsionCall(String number) {
		System.out.println(" number  -->" + number);
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
				+ number));

		try {
			startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}

		dimissPanelAndReSet();
	}

	// 延迟1秒，重置回正常模式
	public void dimissPanelAndReSet() {

		et_dialing_number.postDelayed(new Runnable() {
			@Override
			public void run() {
				et_dialing_number.setText("");

				reSetTheCallLogsView();
			}
		}, 100);
	}

	public void triggerSms(String number) {

		dimissPanelAndReSet();

		String thread_id = NewMessageActivity.queryThreadIdByNumber(this,
				number);

		Intent intent = new Intent(MainActivity.this, NewMessageActivity.class);
		intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
		intent.putExtra(NewMessageActivity.DATA_NUMBER, number);

		startActivity(intent);
	}

	class MyTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			try {
				LinearLayout l = (LinearLayout) v;

				TextView tv_big = (TextView) l.getChildAt(0);
				TextView tv_small = (TextView) l.getChildAt(1);

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					tv_big.setTextColor(Color.WHITE);
					tv_small.setTextColor(Color.WHITE);
					v.setBackgroundColor(getResources().getColor(
							R.color.dialog_pressed));
					// v.setBackgroundResource(R.drawable.dialing_btn_bg_pressed);
				}
				if (event.getAction() == MotionEvent.ACTION_UP
						|| event.getAction() == MotionEvent.ACTION_OUTSIDE) {

					String input = et_dialing_number.getText().toString();

					tv_big.setTextColor(getResources().getColor(
							R.color.text_color_dialing_panel_big));
					tv_small.setTextColor(getResources().getColor(
							R.color.text_color_dialing_panel_small));
					v.setBackgroundResource(R.drawable.dialing_btn_bg_normal);

					playSetting();
					et_dialing_number.setText(input + (String) v.getTag());

					// triggerNumberAssociation();
				}

			} catch (Exception e) {
				// TODO: handle exception
			}

			return true;
		}
	}

	// 刷新收藏联系人
	void refreshCollectContact() {
		
		ln_collect_contacts.setVisibility(View.GONE);

		List<String> posititonContactIds = FavoritContactTool.getContactPosition(MainActivity.this);

		CollectCaontactTouchListener caontactTouchListener = new CollectCaontactTouchListener();

		for (int i = 0; i < posititonContactIds.size(); i++) {
			
			String[] tag = posititonContactIds.get(i).split(",");
			
			String cid = "";
			String name = "";
			String number = "";
			System.out.println(" position ---- > " +posititonContactIds.get(i)+" tag length ---- > " + posititonContactIds.get(i).indexOf(","));
			if (!posititonContactIds.get(i).equals("-1")) {
				
				cid = tag[0];
				name = tag[1];
				number = tag[2];
				
			}
			
//			String cid = posititonContactIds.get(i);

			View v = null;

			switch (i) {
			case 0:
				v = findViewById(R.id.fa_0);
				break;

			case 1:
				v = findViewById(R.id.fa_1);
				break;

			case 2:
				v = findViewById(R.id.fa_2);
				break;

			// ///
			case 3:
				v = findViewById(R.id.fa_3);
				break;

			case 4:
				v = findViewById(R.id.fa_4);
				break;

			case 5:
				v = findViewById(R.id.fa_5);
				break;

			// ////
			case 6:
				v = findViewById(R.id.fa_6);
				break;
			case 7:
				v = findViewById(R.id.fa_7);
				break;
			case 8:
				v = findViewById(R.id.fa_8);
				break;

			// ////
			case 9:
				v = findViewById(R.id.fa_9);
				break;

			case 10:
				v = findViewById(R.id.fa_10);
				break;

			case 11:
				v = findViewById(R.id.fa_11);
				break;

			default:
				break;
			}

//			boolean isFind = false;
//			for (ContactBean cb : collect_contacts) {
//				if (String.valueOf(cb.getContact_id()).equals(number)) {
			if (!posititonContactIds.get(i).equals("-1")) {
					LinearLayout collect_click = (LinearLayout) v.findViewById(R.id.btn_collect_contact);
					collect_click.setTag(cid + ":"+ number);
					collect_click.setOnTouchListener(caontactTouchListener);
					collect_click.setOnClickListener(new CollectContactClickListener());
					collect_click.setOnLongClickListener(new CollectContactLongClickListener());

					TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
					tv_name.setText(name);
					TextView tv_number = (TextView) v.findViewById(R.id.tv_number);
					tv_number.setText(number);

					ImageView btn_delete_collect_contact = (ImageView) v.findViewById(R.id.btn_delete_collect_contact);
					btn_delete_collect_contact.setTag(R.id.btn_delete_collect_contact,String.valueOf(number));
					btn_delete_collect_contact.setTag(R.id.btn_collect_contact,collect_click);

					btn_delete_collect_contact.setOnClickListener(new DeleteCollectContactClikListener());

					collect_click.setTag(R.id.ln_delete_collect_contact,btn_delete_collect_contact);
					collect_click.setTag(R.id.tv_number, i);

//					isFind = true;
//					break;
			}
//				}
//			}

//			if (!isFind || cid.equals("-1")) {
			else {
				LinearLayout collect_click = (LinearLayout) v.findViewById(R.id.btn_collect_contact);
				collect_click.setTag("");
				collect_click.setOnTouchListener(caontactTouchListener);
				collect_click.setOnClickListener(new CollectContactClickListener());
				collect_click.setOnLongClickListener(new CollectContactLongClickListener());

				ImageView btn_delete_collect_contact = (ImageView) v.findViewById(R.id.btn_delete_collect_contact);
				btn_delete_collect_contact.setTag(R.id.btn_collect_contact,collect_click);
				btn_delete_collect_contact.setOnClickListener(new DeleteCollectContactClikListener());

				collect_click.setTag(R.id.ln_delete_collect_contact,btn_delete_collect_contact);
				collect_click.setTag(R.id.tv_number, i);

				TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
				tv_name.setVisibility(View.GONE);

				TextView tv_number = (TextView) v.findViewById(R.id.tv_number);
				tv_number.setText("长按添加");
			}
//			}

		}

		ln_collect_contacts.setVisibility(View.VISIBLE);
	}

	
	int c_x;
	int c_y;
	
	class CollectCaontactTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {

			TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
			TextView tv_number = (TextView) v.findViewById(R.id.tv_number);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:

				tv_name.setTextColor(Color.WHITE);
				tv_number.setTextColor(Color.WHITE);
				v.setBackgroundResource(R.drawable.dialing_btn_bg_pressed);

				c_x = (int) event.getX();
				c_y = (int) event.getY();

				break;

			case MotionEvent.ACTION_UP:

				tv_name.setTextColor(getResources().getColor(
						R.color.fa_contact_name_normal_color));
				tv_number.setTextColor(getResources().getColor(
						R.color.fa_contact_number_normal_color));
				v.setBackgroundResource(R.drawable.dialing_btn_bg_normal);

				break;

			case MotionEvent.ACTION_MOVE:

				int n_x = (int) event.getX();
				int n_y = (int) event.getY();

				int l_x = Math.abs(n_x - c_x);
				int l_y = Math.abs(n_y - c_y);

				if (Math.abs(n_x - c_x) > 3 || Math.abs(n_y - c_y) > 3) {
					tv_name.setTextColor(getResources().getColor(
							R.color.text_color_dialing_panel_big));
					tv_number.setTextColor(getResources().getColor(
							R.color.text_color_dialing_panel_small));
					v.setBackgroundResource(R.drawable.dialing_btn_bg_normal);
				}
				break;

			case MotionEvent.ACTION_OUTSIDE:
				break;

			default:
				break;
			}

			return false;
		}
	}

	// 收藏联系单击监听 , 单击拨打电话
	class CollectContactClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String s = (String) v.getTag();
			if (!isModifyCollectContactMode && !s.equals("")) {
				String number = (((String) v.getTag()).split(":"))[1];
				// System.out.println(" CollectContactClickListener ---> " +
				// number);
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ number));
				startActivity(intent);
			}
		}
	}

	// 收藏联系人长按监听
	class CollectContactLongClickListener implements OnLongClickListener {

		@Override
		public boolean onLongClick(View v) {

			if (!isModifyCollectContactMode) {
				selected_collect_contact_view = v;

				TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
				TextView tv_number = (TextView) v.findViewById(R.id.tv_number);

				String tag = (String) v.getTag();
				System.out.println(" CollectContactLongClickListener ---> " + tag);

				tv_name.setTextColor(getResources().getColor(R.color.text_color_dialing_panel_big));
				tv_number.setTextColor(getResources().getColor(R.color.text_color_dialing_panel_small));
				v.setBackgroundResource(R.drawable.dialing_btn_bg_normal);

				Toast.makeText(MainActivity.this, "请选择联系人", Toast.LENGTH_SHORT).show();
				
				dialog_collect_pick_contact.show();
				
				return true;
			}

			return false;
		}
	}

	// 删除监听
	class DeleteCollectContactClikListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			final String number = (String) v.getTag(R.id.btn_delete_collect_contact);
			final View view = v;

			Dialog dialog = new AlertDialog.Builder(MainActivity.this)
					.setTitle("提示")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setMessage("确定删除选中的收藏联系人?")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

									System.out.println(" DeleteCollectContactClikListener ---> "+ number);

//									ContentValues cv = new ContentValues();
//									cv.put(Contacts.STARRED, 0);
//									getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, cv,ContactsContract.Contacts._ID+ "=" + id, null);

									LinearLayout l_v = (LinearLayout) view.getTag(R.id.btn_collect_contact);

									TextView tv_name = (TextView) l_v.findViewById(R.id.tv_name);
									tv_name.setText("");
									tv_name.setVisibility(View.GONE);

									TextView tv_number = (TextView) l_v.findViewById(R.id.tv_number);
									tv_number.setText("长按添加");

									l_v.setTag("");

									view.setVisibility(View.GONE);// 隐藏删除按钮
									
									int position = (Integer) l_v.getTag(R.id.tv_number);
									FavoritContactTool.changePostionContact(MainActivity.this, position, "", "", "");
									

								}
							}).setNegativeButton("取消", null).create();

			dialog.show();
		}
	}

	class ModifyCollectContactClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			String s = (String) v.getTag();
			System.out.println(" s  --->" + s);
			String[] ss = s.split(":");
			String id = ss[0];
			String name = ss[1];
			String number = ss[2];

//			String oldContactId = (((String) selected_collect_contact_view.getTag()).split(":"))[0];

//			ContentValues cv = new ContentValues();
//			cv.put(Contacts.STARRED, 0);
//			if (oldContactId != null && !oldContactId.equals("")) {
//				getContentResolver().update(ContactsContract.Contacts.CONTENT_URI, cv,ContactsContract.Contacts._ID + "=" + oldContactId,null);
//			}

//			cv.clear();
//			cv.put(Contacts.STARRED, 1);
//			getContentResolver().update(ContactsContract.Contacts.CONTENT_URI,cv, ContactsContract.Contacts._ID + "=" + id, null);

			
			
			// 保存位置信息
			int position = (Integer) selected_collect_contact_view.getTag(R.id.tv_number);
			FavoritContactTool.changePostionContact(MainActivity.this,position, id,name,number);

			TextView tv_name = (TextView) selected_collect_contact_view.findViewById(R.id.tv_name);
			tv_name.setText(name);
			tv_name.setVisibility(View.VISIBLE);
			TextView tv_number = (TextView) selected_collect_contact_view.findViewById(R.id.tv_number);
			tv_number.setText(number);

			ImageView btn_delete_collect_contact = (ImageView) selected_collect_contact_view.getTag(R.id.ln_delete_collect_contact);
			btn_delete_collect_contact.setOnClickListener(new DeleteCollectContactClikListener());
			btn_delete_collect_contact.setTag(R.id.btn_delete_collect_contact,number);

			selected_collect_contact_view.setTag(id + ":" + number);
			// selected_collect_contact_view.setOnClickListener(new
			// CollectContactClickListener()); //设置单击监听

			Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();

			if(dialog_collect_pick_contact!=null && dialog_collect_pick_contact.isShowing())
			{
				dialog_collect_pick_contact.dismiss();
			}
		}
	}

	// 进入修改收藏联系人设置 模式
	void gotoModifyMode() {

		for (int i = 0; i < ln_collect_contacts.getChildCount(); i++) {

			if (ln_collect_contacts.getChildAt(i) instanceof LinearLayout) {
				LinearLayout l = (LinearLayout) ln_collect_contacts.getChildAt(i);

				for (int j = 0; j < l.getChildCount(); j++) {

					if (l.getChildAt(j) instanceof LinearLayout) {
						TextView tv_number = (TextView) l.getChildAt(j).findViewById(R.id.tv_number);

						System.out.println(" t_name.getText().toString() ---> "+ tv_number.getText().toString());

						if (!tv_number.getText().toString().equals("长按添加")) {
							ImageView btn_delete_collect_contact = (ImageView) l.getChildAt(j).findViewById(R.id.btn_delete_collect_contact);
							btn_delete_collect_contact.setVisibility(View.VISIBLE);
						}
					}
				}
			}

		}
		isModifyCollectContactMode = true;

		btn_edit_collect_contact.setText("完成");
	}

	// 取消修改收藏联系人设置 模式
	void cancelModifyMode() {
		for (int i = 0; i < ln_collect_contacts.getChildCount(); i++) {
			if (ln_collect_contacts.getChildAt(i) instanceof LinearLayout) {
				LinearLayout l = (LinearLayout) ln_collect_contacts.getChildAt(i);

				for (int j = 0; j < l.getChildCount(); j++) {
					if (l.getChildAt(j) instanceof LinearLayout) {
						TextView tv_number = (TextView) l.getChildAt(j).findViewById(R.id.tv_number);
						if (!tv_number.getText().toString().equals("长按添加")) {
							ImageView btn_delete_collect_contact = (ImageView) l.getChildAt(j).findViewById(R.id.btn_delete_collect_contact);
							btn_delete_collect_contact.setVisibility(View.GONE);
						}
					}
				}
			}
			isModifyCollectContactMode = false;
		}

		btn_edit_collect_contact.setText("编辑");
	}


	
	class RecentCallOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			switch (v.getId()) {

			case R.id.pop_all:
				if (type != CallLogsAdapter.TYPE_ALL) {

					img_goto_search.setVisibility(View.VISIBLE);

					homeCallsAdapter.changeToType(CallLogsAdapter.TYPE_ALL);
					lv_calls.setSelection(0);
					type = CallLogsAdapter.TYPE_ALL;
				}
				break;

			case R.id.pop_reject:
				
				if (type != CallLogsAdapter.TYPE_REJECT) {

					img_goto_search.setVisibility(View.GONE);

					homeCallsAdapter.changeToType(CallLogsAdapter.TYPE_REJECT);
					lv_calls.setSelection(0);
					type = CallLogsAdapter.TYPE_REJECT;
				}
				
				break;

			case R.id.pop_accept:
				if (type != CallLogsAdapter.TYPE_ACCEPT) {
					img_goto_search.setVisibility(View.GONE);

					homeCallsAdapter.changeToType(CallLogsAdapter.TYPE_ACCEPT);
					lv_calls.setSelection(0);
					type = CallLogsAdapter.TYPE_ACCEPT;
				}
				break;

			case R.id.pop_clear:

				String tips = "";
				switch (type) {
				case CallLogsAdapter.TYPE_ALL:
					tips = "确定清空所有通话记录?";
					break;

				case CallLogsAdapter.TYPE_ACCEPT:
					tips = "确定清空所有已接通话记录?";
					break;

				case CallLogsAdapter.TYPE_REJECT:
					tips = "确定清空所有未接通话记录?";
					break;
				default:
					break;
				}

				View view = LayoutInflater.from(MainActivity.this).inflate(
						R.layout.home_delete_one_tips, null);

				dialog = new Dialog(MainActivity.this, R.style.theme_myDialog);
				dialog.setCanceledOnTouchOutside(true);
				dialog.setContentView(view);
				dialog.show();

				TextView tv_top_tips = (TextView) dialog
						.findViewById(R.id.tv_top_tips);
				tv_top_tips.setText(tips);

				dialog.findViewById(R.id.btn_top_tips_yes).setOnClickListener(new TipClickListener());
				dialog.findViewById(R.id.btn_top_tips_no).setOnClickListener(new TipClickListener());

				break;

			default:
				break;
			}

			popup.dismiss();
		}
	}

	class PopAddToClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.add_to_new: // 添加联系人

				Intent intent = new Intent(MainActivity.this,
						AddContactActivity.class);
				intent.putExtra(AddContactActivity.DATA_TYPE,
						AddContactActivity.TYPE_ADD_CONTACT);
				intent.putExtra(AddContactActivity.DATA_STRANGE_NUMBER,
						add_to_number);
				startActivity(intent);

				break;

			case R.id.add_to_already:

				List<ContactBean> contacts = new ArrayList<ContactBean>();
				String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
				Cursor cursor = getContentResolver().query(
						ContactsContract.Contacts.CONTENT_URI, null, null,
						null, sortOrder);
				System.out.println(cursor.getCount()
						+ "===========================contact=count");
				while (cursor.moveToNext()) {
					ContactBean contactBean = new ContactBean();
					contactBean.setContact_id(cursor.getLong(cursor
							.getColumnIndex(ContactsContract.Contacts._ID)));
					contactBean
							.setPhoto_id(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.PHOTO_ID)));
					contactBean
							.setNick(cursor.getString(cursor
									.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));

					contacts.add(contactBean);
				}
				cursor.close();

				StrangeNumberContactsAdapter strangeNumberContactsAdapter = new StrangeNumberContactsAdapter(
						MainActivity.this, contacts,
						new OnPickContactFinishClickListener());
				
				if(dialog_pick_contact==null)
				{
					dialog_pick_contact = new Dialog(MainActivity.this, R.style.theme_myDialog);
					
					View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.dialog_pick_contact, null);

					dialog_pick_contact.setContentView(view);
					dialog_pick_contact.setCanceledOnTouchOutside(true);
					
					lv_pick_contact = (ListView) view.findViewById(R.id.lv_pick_contact);
				}
				
				lv_pick_contact.setAdapter(strangeNumberContactsAdapter);

				dialog_pick_contact.show();

				break;

			case R.id.add_to_black:

				int count = addBlackWhite.saveBlack(add_to_number);
				if (count > 0) {

					Toast.makeText(MainActivity.this, "黑名单中存在该联系人！",
							Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.add_to_white:

				int count1 = addBlackWhite.saveWhite(add_to_number);
				if (count1 > 0) {

					Toast.makeText(MainActivity.this, "白名单中存在该联系人！",
							Toast.LENGTH_SHORT).show();
				}

				break;

			case R.id.add_to_desktop:

				Long contact_id = getContactId(PhoneNumberTool
						.cleanse(add_to_number));

				ContactBean contactBean = getContactInfo(contact_id,
						PhoneNumberTool.cleanse(add_to_number));

				ContactLauncherDBHelper contactlauncher = new ContactLauncherDBHelper(
						MainActivity.this);

				SQLiteDatabase db = contactlauncher.getReadableDatabase();

				Cursor launcher = db.query(contactlauncher.table, null, null,
						null, null, null, null);

				if (launcher.getCount() == 6) {

					Toast.makeText(MainActivity.this, "超过最大添加数量，无法进行此操作！",
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
						values.put(contactlauncher.PHOTO,
								contactBean.getPhoto());

						db.insert(contactlauncher.table, contactlauncher._ID,
								values);

					}

					launchercursor.close();

				}
				db.close();
				launcher.close();
				Intent widget = new Intent();
				widget.setAction("com.dongji.app.ui.appwidget.refresh");
				sendBroadcast(widget);
				break;

			default:
				break;
			}

			dialog_add_to.dismiss(); // 对话框 消失
		}
	}

	// //////以下 黑白名单相关
	private Long getContactId(String number) {
		List<ContactBean> list = new ArrayList<ContactBean>();

		Long id = null;
		Cursor phones = getContentResolver().query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		if (phones.moveToFirst()) {
			do {
				String phonenumber = PhoneNumberTool
						.cleanse(phones.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
				Long contact_id = phones
						.getLong(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));

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
			// String sub =
			// telephone.substring((telephone.length()-number.length()),
			// telephone.length());
			if (telephone.equals(number))
				id = contactBean.getContact_id();
		}
		return id;
	}

	private ContactBean getContactInfo(Long id, String number) {

		Cursor cursor = getContentResolver().query(
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
				Cursor photo = getContentResolver().query(
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

	// //////以上 黑白名单相关

	class OnPickContactFinishClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String s = (String) v.getTag();

			String[] ss = s.split(":");

			Intent intent = new Intent(MainActivity.this,
					AddContactActivity.class);
			intent.putExtra(AddContactActivity.DATA_TYPE,
					AddContactActivity.TYPE_ADD_NUMBER_TO_CURENT_CONTACT);
			intent.putExtra(AddContactActivity.DATA_CONTACT_ID, ss[0]);
			intent.putExtra(AddContactActivity.DATA_STRANGE_NUMBER,add_to_number);
			startActivity(intent);
			
			if(dialog_pick_contact!=null)
			{
				dialog_pick_contact.dismiss();
			}
		}
	}


	// 监听联系人数据库被改变了
	public class ContactProviderObserver extends ContentObserver {

		public ContactProviderObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {

			System.out.println(" 联系人数据库  发生变化");

			// 编辑分组时候，刷新联系人相关
			if (contactLayout != null && contactLayout.isEditingGroup) {
				return;
			}

			// if (isCalling) {
			// return;
			// }

			if (isRefreshing) {
				return;
			}

			if (contactLayout != null) { // 联系人列表不为空

				if (state == STATE_CONTACT) // 正在显示 : 立刻刷新
				{
					contactLayout.refresh();
				} else {
					contactLayout.isNeedRefresh = true; // 设置标识位： 联系人列表再次显示的时候刷新
				}
			}

			if (messageListLayout != null) {
				messageListLayout.updateWhenContactChange();
			}

			if (state == STATE_HOME) {
				refresh();
			} else {
				isNeedRefresh = true;
			}

		}
	}

	public void refresh() {
		if (homeCallsAdapter != null) {
			homeCallsAdapter.reQueryContactName();
		}

		try {
			getAllContacts();
		} catch (Exception e) {
			e.printStackTrace();
		}

		isNeedRefresh = false;
	}

	// 刷新排序方式
	public static void refreshCallLogSort() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				System.out.println(" 重刷新  ======》");

				if (homeCallsAdapter != null) {
					homeCallsAdapter.refreshCalllogSort();
				}
			}
		}).start();
	}

	// 刷新通话记录
	public static void refreshCallLog() {
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					Thread.sleep(3000);
				} catch (Exception e) {
				}

				// System.out.println(" 重刷新  ======》");

				if (homeCallsAdapter != null) {
					homeCallsAdapter.refreshNewest();
				}
			}
		}).start();
	}

	public void checkUpdate() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {

					Thread.sleep(10000); // 睡眠10秒

					if (ToolUnit.checkNetWork(MainActivity.this)) {

						NetworkUnit networkUnit = new NetworkUnit();

						String packageName = MainActivity.this.getPackageName();
						PackageManager pm = MainActivity.this
								.getPackageManager();
						PackageInfo packageInfo;
						// http://192.168.1.200/cms/index.php?g=api&m=Message&a=Opt
						packageInfo = pm.getPackageInfo(packageName,
								PackageManager.GET_ACTIVITIES);
						int versionCode = packageInfo.versionCode == 0 ? 1
								: packageInfo.versionCode;

						NetWorkResult net = networkUnit.updateVersion(
								packageName, String.valueOf(versionCode));

						if (net != null) {
							String success = net.getSuccess();
							if ("1".equals(success)) {
								// String versionInfo=net.getVersion_number();
								// String sendDate=net.getSend_date();
								update_url = net.getDownload_url();
								if (update_url != null) {
									myHandler.sendEmptyMessage(7);
								}
							}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	public void downloadUpdatedApp() {
		if (AndroidUtils.isSdcardExists()) // sd卡是否可用
		{
			Intent updateIntent = new Intent(MainActivity.this,
					UpdateVersionService.class);
			updateIntent.putExtra("update_url", update_url);

			startService(updateIntent);
		} else {
			Toast.makeText(this, "sd card faild", Toast.LENGTH_SHORT).show();
		}
	}

	private class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 6: //
					if (!UpdateVersionService.IS_DOWNLOAD) {
						Dialog dialog = new AlertDialog.Builder(
								MainActivity.this)
								.setIcon(android.R.drawable.ic_dialog_info)
								.setTitle("发现新版本")
								.setPositiveButton("马上更新",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {

												downloadUpdatedApp();
											}
										}).create();

						dialog.show();
					} else {
						Toast.makeText(MainActivity.this, "动机通讯录正在后台下载，请稍后",
								Toast.LENGTH_SHORT).show();
					}
					break;
				case 7: // 如果有版本更新
					if (!UpdateVersionService.IS_DOWNLOAD) {
						downloadUpdatedApp();
					} else {
						Toast.makeText(MainActivity.this, "动机通讯录正在后台下载，请稍后",
								Toast.LENGTH_SHORT).show();
					}
					break;

				case 9: // 没有版本更新
					Toast.makeText(MainActivity.this, "已是最新版本",
							Toast.LENGTH_LONG).show();
					break;
				case 10: // 没有版本更新
					Toast.makeText(MainActivity.this, "网络异常，请检查网络",
							Toast.LENGTH_LONG).show();
					break;
				case 11: // 版本更新出现问题
					/*
					 * if (updateDialog.isShowing()) { updateDialog.dismiss(); }
					 * Toast.makeText(Launcher.this, "下载失败，请重试",
					 * Toast.LENGTH_LONG).show(); break;
					 */
				default:
					break;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	// 加密内容被修改的时候，更新全部联系人界面
	public void updateAfterChangeEncryption() {

		homeCallsAdapter.fitterAll();
		homeCallsAdapter.notifyDataSetChanged();

		if (searchContactsAdapter != null) {
			searchContactsAdapter.resetData();
		}
		
		if (contactLayout != null) {
			contactLayout.updateAfterChangeEncryption();
		}

		if (messageListLayout != null) {
			messageListLayout.updateAfterChangeEncryption();
		}

		
		isChangeEnData = false;
	}

	
	// 监听短信库数据变化
	public class SmsProviderObserver extends ContentObserver {

		public SmsProviderObserver(Handler handler) {
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {

			if (messageListLayout != null) {
				if (state == STATE_MESSAGE_LIST) {
					messageListLayout.refresh(true);
				} else {
					messageListLayout.isNeedRefresh = true;
				}
			}

			//查询未接短信的总数
//			new Thread(new Runnable() {
//				@Override
//				public void run() {
//					try {
//						Thread.sleep(2000);
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//
//					isRead = false;
//					num = getUnreadSMSNum();
//					refresh_handler.sendEmptyMessage(0);
//
//				}
//			}).start();

		}
	}

	
	Handler refresh_handler = new Handler() {

		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

//				if (num > 0) {
//					tv_new_message_num.setText(String.valueOf(num));
//				} else {
//					tv_new_message_num.setText("");
//				}
				
				break;

			default:
				break;
			}
		};
	};


	
	private void checkFirstLauncher() {
		SharedPreferences mSharedPreferences = getSharedPreferences(
				getPackageName() + "_prefs", MainActivity.MODE_PRIVATE);
		boolean isFirst = mSharedPreferences.getBoolean(FIRST_LAUNCHER, true);
		if (isFirst) {
			boolean callPhoneExists = checkExistsShortcut(CALL_PHONE_SHORTCUT);
			if (!callPhoneExists) {
				createShortcut(CALL_PHONE_SHORTCUT);
			}
			boolean contactExists = checkExistsShortcut(CONTACT_SHORTCUT);
			if (!contactExists) {
				createShortcut(CONTACT_SHORTCUT);
			}
			boolean smsExists = checkExistsShortcut(SMS_SHORTCUT);
			if (!smsExists) {
				createShortcut(SMS_SHORTCUT);
			}
			changeFirseLaunch(mSharedPreferences);
		}
	}

	
	private void changeFirseLaunch(SharedPreferences mSharedPreferences) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRST_LAUNCHER, false);
		mEditor.commit();
	}

	
	/**
	 * 创建桌面快捷方式
	 */
	private void createShortcut(int type) {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		shortcut.putExtra("duplicate", false); // 不允许重复创建

		switch (type) {
		case CALL_PHONE_SHORTCUT:
			// 指定当前的Activity为快捷方式启动的对象: com.everest.video.VideoPlayer
			// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程
			ComponentName comp = new ComponentName(this.getPackageName(),CallPhoneActivity.class.getName());
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp));
			// 快捷方式的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, CALL_PHONE_STRING);
			// 快捷方式的图
			ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(this, R.drawable.call_phone_icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
			break;
			
		case CONTACT_SHORTCUT:
			// 指定当前的Activity为快捷方式启动的对象: com.everest.video.VideoPlayer
			// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程
			ComponentName comp2 = new ComponentName(this.getPackageName(),ContactActivity.class.getName());
					
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp2));
			// 快捷方式的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, CONTACT_STRING);
			// 快捷方式的图
			ShortcutIconResource iconRes2 = Intent.ShortcutIconResource.fromContext(this, R.drawable.contact_icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes2);
			break;
			
		case SMS_SHORTCUT:
			// 指定当前的Activity为快捷方式启动的对象: com.everest.video.VideoPlayer
			// 注意: ComponentName的第二个参数必须加上点号(.)，否则快捷方式无法启动相应程
			ComponentName comp3 = new ComponentName(this.getPackageName(),SMSActivity.class.getName());
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, new Intent(Intent.ACTION_MAIN).setComponent(comp3));
			// 快捷方式的名称
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, SMS_STRING);
			// 快捷方式的图
			ShortcutIconResource iconRes3 = Intent.ShortcutIconResource.fromContext(this, R.drawable.send_sms_icon);
			shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes3);
			break;
			
		}

		sendBroadcast(shortcut);
	}

	/***
	 * 检查桌面是否存在此快捷方式
	 */
	private boolean checkExistsShortcut(int type) {
		boolean result = false;
		// 获取当前应用名称
		String title = null;

		switch (type) {
		case CALL_PHONE_SHORTCUT:
			title = CALL_PHONE_STRING;
			break;
		case CONTACT_SHORTCUT:
			title = CONTACT_STRING;
			break;
		case SMS_SHORTCUT:
			title = SMS_STRING;
			break;
		}

		final String uriStr;

		if (android.os.Build.VERSION.SDK_INT < 8) {
			uriStr = "content://com.android.launcher.settings/favorites?notify=true";
		} else {
			uriStr = "content://com.android.launcher2.settings/favorites?notify=true";
		}
		final Uri CONTENT_URI = Uri.parse(uriStr);
		final Cursor c = getContentResolver().query(CONTENT_URI, null,
				"title=?", new String[] { title }, null);
		if (c != null && c.getCount() > 0) {
			result = true;
		}
		return result;
	}

	
	/**
	 * 
	 * 快速删除
	 * 
	 * @author Administrator
	 *
	 */
	class quickDeleteRunnable implements Runnable {

		@Override
		public void run() {
			while (isLongPresseding) {
				String source = et_dialing_number.getText().toString();
				int length = source.length();

				if (length == 0) {
					break;
				}

				if (length == 1) {
					source = "";
				} else {
					source = source.substring(0, length - 1);
				}

				dialing_number_source = source;

				mQucikDeleteHandler.sendEmptyMessage(0);

				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	/********** 初次启动,与初次进入 某个页面 的标示 ****************/
	private static String BACKUP = "BACKUP_SF";
	private static String FIRSTBACKUP = "FIRST_BACK_UP";
	public static String ISBACKUP = "IS_BACK_UP";
	public boolean isKeyBack = false;

	public static String SF_KEY_FIRST_JING = "SF_KEY_FISTR_JING"; // 第一次,打开拨号盘
	public static String SF_KEY_FIRST_CONTACTS = "SF_KEY_FIRST_CONTACTS"; // 第一次，进入联系人列表
	public static String SF_KEY_FIRST_SETTINGS = "SF_KEY_FIRST_SETTINGS"; // 第一次，进入设置界面

	private Dialog backUpDialog = null;
	private SharedPreferences sf_fist_in = null;

	private void checkFirstIn() {
		boolean isFirst = sf_fist_in.getBoolean(FIRSTBACKUP, true);

		if (isFirst) {
			fram_tip_mask.removeAllViews();
			fram_tip_mask.addView(LayoutInflater.from(this).inflate(
					R.layout.tip_home_first, null));
			fram_tip_mask.setVisibility(View.VISIBLE);
		}
	}

	private void showFirskBackUpDialog() {
		// backUpDialog = new Dialog(MainActivity.this, R.style.theme_myDialog);
		// backUpDialog.setContentView(R.layout.first_back_up_tip);
		// backUpDialog.setCanceledOnTouchOutside(true);
		// backUpDialog.show();
		//
		// Button backupBtn = (Button)
		// backUpDialog.findViewById(R.id.btn_backup);
		// Button closeBtn = (Button) backUpDialog.findViewById(R.id.btn_close);
		//
		// backupBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		//
		// backUpDialog.cancel();
		//
		// View view = null;
		// ImageView img_top = null;
		// TextView tv_top;
		//
		// second_layout.removeAllViews();
		// top_second.removeAllViews();
		//
		// SystemBackUp systemBackUp = new SystemBackUp(MainActivity.this);
		//
		// second_layout.addView(systemBackUp.backupView);
		//
		// view =
		// LayoutInflater.from(MainActivity.this).inflate(R.layout.item_top,
		// null);
		//
		// img_top = (ImageView)view.findViewById(R.id.img_top);
		// // img_top.setImageResource(resId);
		// tv_top = (TextView)view.findViewById(R.id.tv_top);
		// tv_top.setText("备份/恢复");
		//
		// top_second.addView(view);
		//
		//
		// state = STATE_SETTING;
		//
		// }
		// });
		//
		// closeBtn.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// backUpDialog.cancel();
		//
		//
		// changeIsBackUp(sf_fist_in);
		// }
		// });
		//
		// changeValue(sf_fist_in);
		//
		// backUpDialog.setOnCancelListener(new OnCancelListener() {
		//
		// @Override
		// public void onCancel(DialogInterface dialog) {
		//
		// changeIsBackUp(sf_fist_in);
		// }
		// });
	}

	
	private void changeValue(SharedPreferences mSharedPreferences) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(FIRSTBACKUP, false);
		mEditor.commit();
	}

	public void changeIsBackUp(SharedPreferences mSharedPreferences) {
		SharedPreferences.Editor mEditor = mSharedPreferences.edit();
		mEditor.putBoolean(ISBACKUP, false);
		mEditor.commit();
	}

	class TipFrameTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				fram_tip_mask.setVisibility(View.GONE);
				Editor editor = sf_fist_in.edit();

				switch (state) {
				case STATE_HOME:
					boolean isFirst = sf_fist_in.getBoolean(FIRSTBACKUP, true);

					if (isFirst) {
						showFirskBackUpDialog();

						editor.putBoolean(FIRSTBACKUP, false);
						editor.commit();

					} else if (dialing_panel.getVisibility() == View.VISIBLE) {

						editor.putBoolean(SF_KEY_FIRST_JING, false);
						editor.commit();
					}

					break;

				case STATE_CONTACT:

					editor.putBoolean(SF_KEY_FIRST_CONTACTS, false);
					editor.commit();
					break;

				case STATE_SETTING:

					editor.putBoolean(SF_KEY_FIRST_SETTINGS, false);
					editor.commit();
					break;

				default:
					break;

				}

				editor.clear();
			}

			return true;
		}
	}

	private boolean checkPhoneModel() {
		String model = Build.MODEL;
		if (!TextUtils.isEmpty(model)) {

			model = model.toLowerCase();

			String[] modelArr = getResources().getStringArray(
					R.array.phoneModel);
			for (int i = 0; i < modelArr.length; i++) {
				String temp = modelArr[i].toLowerCase();
				if (model.indexOf(temp) != -1) {
					return true;
				}
			}
		}
		return false;
	}

	private void isRefreshSort() {
		
		SharedPreferences sf1 = getSharedPreferences(SystemSettingActivity.SF_NAME, 0);

		int old_calllog_sort = sf1.getInt(SystemSettingActivity.REFRESH_CALLLOG, 1);
		int new_calllog_sort = sf1.getInt(SystemSettingActivity.SF_KEY_COLLOG_SORT, 1);

		int old_contact_sort = sf1.getInt(SystemSettingActivity.REFRESH_CONTACT, 1);
		int new_contact_sort = sf1.getInt(SystemSettingActivity.SF_KEY_CONTACT_SORT, 1);

		int old_sms_sort = sf1.getInt(SystemSettingActivity.REFRESH_SMS, 1);
		int new_sms_sort = sf1.getInt(SystemSettingActivity.SF_KEY_SMS_SORT,1);

		if (old_calllog_sort != new_calllog_sort) { //通话记录排序方式是否修改

			refreshCallLogSort();

			Editor editor = sf1.edit();
			editor.putInt(SystemSettingActivity.REFRESH_CALLLOG,new_calllog_sort);
			editor.commit();
		}

		if (old_contact_sort != new_contact_sort) { //联系人排序方式是否修改

			if (contactLayout != null) { //如果联系人列表界面未被初始化，则不用刷新数据
				contactLayout.refresh();
			}

			Editor editor = sf1.edit();
			editor.putInt(SystemSettingActivity.REFRESH_CONTACT,new_contact_sort);
			editor.commit();
		}

		if (old_sms_sort != new_sms_sort) { //短信排序方式是否修改

			if (messageListLayout != null) { //如果短信列表界面未被初始化，则不用刷新数据
				messageListLayout.refreshSort();
			}

			Editor editor = sf1.edit();
			editor.putInt(SystemSettingActivity.REFRESH_SMS, new_sms_sort);
			editor.commit();
		}
		
	}
	
}
