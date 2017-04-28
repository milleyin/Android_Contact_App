package com.dongji.app.addressbook;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.provider.ContactsContract.RawContacts;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.telephony.TelephonyManager;
import android.telephony.gsm.SmsManager;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.GroupNameNumberAdapter;
import com.dongji.app.adapter.MessageDetailChatAdapter;
import com.dongji.app.adapter.MessageListAdapter;
import com.dongji.app.adapter.MyExpandableListAdapter;
import com.dongji.app.adapter.NewSmsContactTipsAdapter;
import com.dongji.app.adapter.RemindContactsMutilAdapter;
import com.dongji.app.adapter.SmsExpandableListAdapter;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.GroupInfo;
import com.dongji.app.entity.MessageLibrary;
import com.dongji.app.entity.MmsContent;
import com.dongji.app.entity.MmsSmsContent;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.entity.Threads;
import com.dongji.app.entity.WheelMain;
import com.dongji.app.service.MessagetIntercepteNotifationUtil;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.ExpressionUtil;
import com.dongji.app.tool.MMSSender;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.tool.SdCardUtils;
import com.dongji.app.tool.TimeTool;
import com.dongji.app.ui.EmotionHelper;
import com.dongji.app.ui.MyDialog;

/**
 * 
 * 新建短信   OR 短信详情
 * 
 * @author Administrator
 *
 */
public class NewMessageActivity extends Activity implements OnClickListener {
	
	/////// 启动时 从Intent传过来的数据   所对应的DATA_KEY
	public static String DATA_THREAD_ID ="thread_id";
	public static String DATA_FROM_STATE ="from_state";
	public static String DATA_NUMBER = "number";
	public static String DATA_OTHER = "other";
	
	TextView tv_top;
	Button btn_back;
	
	//////// 状态
	public static int state;
	public static int STATE_NEW = 1; //新建短信
	public static int STATE_DETAIL = 2;  //短信详情
	
	SmsProviderObserver smsProviderObserver; //系统短信数据库的监听
	public final Uri THREADS_URI = Uri.parse("content://mms-sms/conversations?simple=true");
	
	SmsManager smsManager;
	boolean isSending = false;//是否正在发送短信
	
	boolean isMultiConversation ; //是否为群发会话
	
	public View view;
	
	private EditText et_content; //短信内容输入
	private ListView lv_sms;
	private Button btn_send;
	private MessageDetailChatAdapter smsDetailChatAdapter;
	private ImageButton Ibtn_more;
	
	 //更多
	LinearLayout ln_more;
	ImageButton btn_expression; //表情
	ImageButton btn_timing_sms; //定时短信
	ImageButton btn_pic_mms; //图片彩信
	
	Dialog dialog_expression;
	GridView gv_expression;
	
	Dialog dialog_timing_sms;
	WheelMain mWheelMain;
	
	Dialog dialog_pic_mms; //图片彩信
	
	//彩信
	LinearLayout ln_mms; 
	ImageView img_mms;
	EditText tv_mms_subject;
	Button btn_view_mms;
	Button btn_delete_mms;
	Bitmap bmp_mms;
	Uri uri_mms;
	
	ImageView img_timing_sms; //定时短信的icon
	
	ExpandableListAdapter myExpandableListAdapter;
	ExpandableListView  expandableListView;
	Button smsLibaryButton;
	
	TextView send_time;
	String showTime;
	RelativeLayout rl_number_input;
	
	ImageButton btn_add_number;
	ImageButton Ibtn_message_library_show;
	
	LinearLayout rl_group; //分组信息
	int partner_type = 0;  //  0:多选联系人     1:分组内选联系人
	List<ContactBean> group_contacts  ; //所有联系人
	Button btn_all_contact;
	Button btn_group;
	
	LinearLayout contact_ln;
	ListView lv_partake_contact;
	ExpandableListView group_expandableListView ;
	
	RemindContactsMutilAdapter remindContactsMutilAdapter;
	SmsExpandableListAdapter smsExpandableListAdapter;
	ArrayList<ArrayList<GroupInfo>> childInfos = new ArrayList<ArrayList<GroupInfo>>();
	ArrayList<GroupInfo> aGroupInfos ;

	ImageView img_group_left_icon;
	boolean isExpanded = false;
	LinearLayout addSmsButton;
	ArrayList<String> phoneNumberList = new ArrayList<String>();
	
	StringBuffer numberBuffer = new StringBuffer() ; // 以    姓名:电话   为单位   逗号隔开    组合起来的字符串 ， 实例     小明：123456，小红:2874665,张老板:5632541
	
	int input_width;
	int input_height;
	
	ScrollView scroller;
	LinearLayout ln_container ;
	
	EditText et_number_input;  //电话号码输入
	
	//输入电话号码弹出的提示层
	LinearLayout ln_number_tips;
	ListView lv_number_tips;
	List<ContactBean> contacts = new ArrayList<ContactBean>() ; //所有联系人
	boolean isQuerying = false;
	
	NewSmsContactTipsAdapter search; //提示层List的Adapter
	Dialog popup_message;
	ListView messageList;
	Button holidayButton;
	Button funnyButton;
	Button loveButton;
	Button operatorButton;
	TextView searchResult;
	LinearLayout sort_message;
	
	Button messageConfirm;
	Button messageCancel;
	CheckBox messageCheckBox;
	MessageListAdapter messageListAdapter;
	MyDatabaseUtil myDatabaseUtil;
    public   static String selectMessage;
	String thread_id="-1";
	long deleteOrFavoriteMessId=-1;
	
	MmsSmsContent mMessage; //长按触发时 被选中的 短信
	String selected_content;
	
	Dialog popup_text;
	Button messageCopy;
	Button messageDelete;
	Button messageRemind;
	Button messageForward;
	Button messageFavorite;
	Button messageSendAgain;
	
	
	 /**发送与接收的广播**/  
	public static String SENT_SMS_ACTION = "DONGJI_SENT_SMS_ACTION";  
    public static String DELIVERED_SMS_ACTION = "DONGJI_DELIVERED_SMS_ACTION"; 
    
    
    boolean iscont;
    String nameAndNumber[];
    String phoneNumber;
	String user_name;
	
	//短信的状态
	public static final int SEND_SUCCESS = 0; 
	public static final int SEND_FAILD = 128; 
	public static final int SEND_PENDING = 64;
	
	//短信类型
	public static final int SMS_TYPE_ALL = 0;
	public static final int SMS_TYPE_INBOX = 1;
	public static final int SMS_TYPE_SENT = 2; //发送成功
	public static final int SMS_TYPE_DRAFT = 3; //草稿
	public static final int SMS_TYPE_FAILED = 5; //发送失败
	
	RelativeLayout rl_message_detail_top;
	ImageView contactImage;
	TextView  tv_name;
	TextView  tv_number;
	ImageButton Ibtn_call;
	ArrayList<MmsSmsContent> messages=new ArrayList<MmsSmsContent>();
	
	public static final Uri SMS_URI = Uri.parse("content://sms/");
	
	private String base_uri_str = "content://mms-sms/conversations/";
	
	TelephonyManager tm ;
	
	String selectMessageStatus="";
	
	
	LinearLayout faceLayout;
	LinearLayout clockLayout;
	
	ImageButton btn_showGroupInfo; //下来显示群发短信的详细号码信息
	PopupWindow popupGroup;
	ArrayList<String> uNameNumber=new ArrayList<String>();
	
	//草稿
	long draft_sms_id = -1;
	String draft_text;//草稿的内容
	
	//定时短信
	long timing_sms_time = -1; //时间
	long selected_timing_id = -1;
	
	/**
	 * 使用照相机拍摄照片作为头像时会使用到这个路径
	 */
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int ICON_SIZE = 96;
	
	/**
	 * 照相机拍摄照片转化为该File对象
	 */
	private File mCurrentPhotoFile;
	
	
	private long start;
	private long end;
	
	Dialog show_pic_dialog;
	TextView tv_mms_title;
	Button btn_save_pic;
	ImageView img_show_mms_pic;
	Bitmap showing_bmp; 
	
	
	MyDialog  myDialog;
	Handler refresh_handler = new Handler(){

		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case 1:
					search = new NewSmsContactTipsAdapter(NewMessageActivity.this, contacts);
					search.setLntips(ln_number_tips);
				    lv_number_tips.setAdapter(search);
				
				break;
				
			case 2:
				
				//是否需要滚动至列表最下方
				boolean isNeedScroll2Bottom = false;
				
				if(smsDetailChatAdapter!=null)
				{
					if(smsDetailChatAdapter.getCount() == 0)
					{
						isNeedScroll2Bottom = true;
						
					}else {
						
						MmsSmsContent old_last = smsDetailChatAdapter.getItem(smsDetailChatAdapter.getCount()-1);
						MmsSmsContent new_last = messages.get(messages.size()-1);
						
						if(old_last.getMessage_type() != new_last.getMessage_type())
						{
							isNeedScroll2Bottom = true;
							
						}else if (old_last.getId()!=new_last.getId()){
							isNeedScroll2Bottom = true;
						}
					}
					
				}
				
					smsDetailChatAdapter.setDate(messages);
					end = System.currentTimeMillis();
					
					if(isNeedScroll2Bottom)
					{
						btn_send.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								setAllRead();
								lv_sms.smoothScrollToPosition(smsDetailChatAdapter.getCount()-1);
								lv_sms.setSelection(smsDetailChatAdapter.getCount()-1);
							}
						}, 500);
					}
					
					System.out.println(" 刷新完成 ，耗时  ---->" + (end - start));
					
					if(thread_id!=null && !"".equals(thread_id) && Integer.valueOf(thread_id) == MessagetIntercepteNotifationUtil.THREAD_ID && MainActivity.isNotifacation)
					{
						NotificationManager nm = (NotificationManager) NewMessageActivity.this.getSystemService(NewMessageActivity.this.NOTIFICATION_SERVICE); 
						nm.cancel(MessagetIntercepteNotifationUtil.Notification_ID);
					}
				
				break;

			default:
				break;
			}
		};
	};

	
	Handler handler = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				
			case 1:
				remindContactsMutilAdapter = new RemindContactsMutilAdapter(NewMessageActivity.this, group_contacts);
	 			lv_partake_contact.setAdapter(remindContactsMutilAdapter);
	 			  
				isQuerying = false;
				break;
				
			case 2:
				
				smsExpandableListAdapter = new SmsExpandableListAdapter(NewMessageActivity.this,aGroupInfos,childInfos);
        		
				group_expandableListView.setAdapter(smsExpandableListAdapter);
				
				break;

			default:
				break;
			}
			
		};
	};
	
	
	Handler error_handler = new Handler(){
		
		public void handleMessage(Message msg) {
			Toast.makeText(NewMessageActivity.this, "彩信无法发送,请设置您的移动网络为cmwap", Toast.LENGTH_SHORT).show();
		};
	};
	
	
	/**
	 * 
	 * @param thread_id 会话id
	 * @param state  新建短信：1  ， 短信详情： 2
	 * @param number 号码
	 * @param other  新建短信中： 转发的内容  ；    短信详情中： 在收藏短信中查看原短信的短信id
	 * 
	 */
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.new_message, null);
		
		setContentView(view);
		
		this.thread_id  = getIntent().getStringExtra(DATA_THREAD_ID);
		String number = getIntent().getStringExtra(DATA_NUMBER);
		String other = getIntent().getStringExtra(DATA_OTHER);

		
		System.out.println("  thread_id  --->" + thread_id);
		//根据会话id判断是新建短信  还是 短信详情
		if(thread_id!=null)
		{
			state = STATE_DETAIL;
		}else{
			state = STATE_NEW;
		}
		
		
		smsManager = SmsManager.getDefault();
		
		tm = (TelephonyManager)NewMessageActivity.this.getSystemService(NewMessageActivity.this.TELEPHONY_SERVICE);
		
		myDatabaseUtil = DButil.getInstance(NewMessageActivity.this);
		
		tv_top = (TextView) findViewById(R.id.tv_top);
		btn_back  = (Button) findViewById(R.id.btn_back); 
		btn_back.setOnClickListener(this);
		
		
		rl_message_detail_top=(RelativeLayout)view.findViewById(R.id.message_detail);
		contactImage=(ImageView)view.findViewById(R.id.message_contact_image);
		tv_name=(TextView)view.findViewById(R.id.message_contact_name);
		tv_number=(TextView)view.findViewById(R.id.message_contact_number);
		
		btn_send = (Button)view.findViewById(R.id.btn_send_mms_sms);
		et_content = (EditText)view.findViewById(R.id.team_singlechat_id_edit);
		et_content.addTextChangedListener(textWatcher);
		et_content.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				ln_more=(LinearLayout)view.findViewById(R.id.more_functions);
				if(ln_more.getVisibility()!=View.GONE)
				{
					ln_more.setVisibility(View.GONE);
					
					//显示输入键盘
					InputMethodManager inputMethodManager = (InputMethodManager)NewMessageActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.showSoftInput(et_content,InputMethodManager.SHOW_FORCED);
				}
				return false;
			}
		});
		
		
		btn_showGroupInfo=(ImageButton)view.findViewById(R.id.show_group_info);
		btn_showGroupInfo.setOnClickListener(this);
		
		Ibtn_call=(ImageButton)view.findViewById(R.id.message_contact_call_button);
		Ibtn_call.setOnClickListener(this);
		
		ln_more=(LinearLayout)view.findViewById(R.id.more_functions);
		
		btn_expression = (ImageButton) view.findViewById(R.id.btn_expression);
		btn_expression.setOnClickListener(this);
		btn_timing_sms = (ImageButton) view.findViewById(R.id.btn_timing_sms);
		btn_timing_sms.setOnClickListener(this);
		btn_pic_mms = (ImageButton) view.findViewById(R.id.btn_pic_mms);
		btn_pic_mms.setOnClickListener(this);
		
	 
		createDialog();

		img_timing_sms = (ImageView)view.findViewById(R.id.img_timing_sms);
		img_timing_sms.setOnClickListener(this);
		
		ln_mms = (LinearLayout)view.findViewById(R.id.ln_mms);
		img_mms = (ImageView)view.findViewById(R.id.img_mms);
		tv_mms_subject = (EditText)view.findViewById(R.id.et_mms_subject);
		btn_view_mms = (Button)view.findViewById(R.id.btn_view_mms);
		btn_view_mms.setOnClickListener(this);
		btn_delete_mms = (Button)view.findViewById(R.id.btn_delete_mms);
		btn_delete_mms.setOnClickListener(this);
		
		if(state == STATE_NEW)   ////////新建短信
		{
			tv_top.setText("新建短信");
			
			rl_number_input=(RelativeLayout)view.findViewById(R.id.team_singlechat_id_head);
		    rl_number_input.setVisibility(View.VISIBLE);
			addSmsButton=(LinearLayout)view.findViewById(R.id.btn__layout);
			btn_add_number = (ImageButton)view.findViewById(R.id.btn_add_number);
			btn_add_number.setOnClickListener(this);
			btn_add_number.setVisibility(View.VISIBLE);
			
			rl_group = (LinearLayout)view.findViewById(R.id.rl_group);
			
			((TextView)view.findViewById(R.id.mask_top)).setOnClickListener(this);
			btn_all_contact = (Button) view.findViewById(R.id.btn_all_contact);
			btn_all_contact.setOnClickListener(this);
			btn_group = (Button) view.findViewById(R.id.btn_group);
			btn_group.setOnClickListener(this);
			((Button) view.findViewById(R.id.btn_pick_number_yes)).setOnClickListener(this);
		    ((Button) view.findViewById(R.id.btn_pick_number_cancle)).setOnClickListener(this);
			
			contact_ln = (LinearLayout) view.findViewById(R.id.contact_ln);
			
			lv_partake_contact = (ListView) view.findViewById(R.id.lv_partake_contact);
			group_expandableListView = (ExpandableListView) view.findViewById(R.id.group_expandableListView);
			
			scroller = (ScrollView)view.findViewById(R.id.scroller);
	        ln_container = (LinearLayout)view.findViewById(R.id.ln_container);
	        
	       
	        et_number_input = (EditText)view.findViewById(R.id.et_number_input);
	        et_number_input.setOnFocusChangeListener(new OnFocusChangeListener() {
				
				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if(hasFocus)
					{
//						scroller.setBackgroundResource(R.drawable.editbox_background_focus_yellow);
					}else{
//						scroller.setBackgroundResource(R.drawable.editbox_background_normal);
					}
				}
			});
	        
	        et_number_input.addTextChangedListener(new MyTextWacther());
	        
	        ln_container.post(new Runnable() {
				@Override
				public void run() {
					input_width = scroller.getWidth();
					input_height = scroller.getHeight();
//					System.out.println(" input_width ---> " + input_width + " input_height ---> " + input_height);
				}
			});
			
	        ln_number_tips = (LinearLayout)view.findViewById(R.id.ln_number_tips);
	        lv_number_tips = (ListView)view.findViewById(R.id.lv_number_tips);
	        lv_number_tips.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
						long arg3) {
					addOnePerson(String.valueOf(search.getItem(arg2)));
					et_number_input.setText("");
					et_number_input.requestFocus();
				}
			});
	      
	        
	       new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					getContast(); //获取所有联系人
					refresh_handler.sendEmptyMessage(1);
				}
			}).start();
	        
			Ibtn_call.setOnClickListener(this);
			
	     	if(other != null)
	     	{
				et_content.setText(other);
	     	}
			
			if(number!=null)
			{
				setNumber(number);
			}
			
		}else {  ////////短信详情
			
			tv_top.setText("短信详情");
			
			System.out.println("  thread_id  ---> " + thread_id);
			System.out.println("phone number  -----> "+this.phoneNumber);
			
			getNumbersByThreadId();
			messages=queryConversation();
			
			rl_message_detail_top.setVisibility(View.VISIBLE);
			setAllRead();
			
		}
		
		Ibtn_message_library_show=(ImageButton)view.findViewById(R.id.Ibtn_message_library_show);
	    Ibtn_message_library_show.setOnClickListener(this);
	    
		lv_sms = (ListView)view.findViewById(R.id.team_singlechat_id_showlist);
		
		Ibtn_more = (ImageButton)view.findViewById(R.id.Ibtn_team_singlechat_id_expression);
		
		btn_send.setOnClickListener(this);
		Ibtn_more.setOnClickListener(this);
		
		smsDetailChatAdapter = new MessageDetailChatAdapter(NewMessageActivity.this,isMultiConversation,messages,new MessageItemClickListener(),new MessagetItemLongClickListener());
		lv_sms.setAdapter(smsDetailChatAdapter);
		
		
		if(draft_sms_id!=-1) //存在草稿
		{
			SpannableString spannableString = ExpressionUtil.getExpressionString(NewMessageActivity.this, draft_text);
			et_content.setText(spannableString);
		}
		
		if(state == STATE_DETAIL)
		{
			//查看原短信
            if(other!=null && !other.equals("")){	
            	//mark
				int size = 0;
				for ( int i = 0; i < messages.size(); i++){
					if(messages.get(i) instanceof SmsContent)
					{
						SmsContent smsContents = (SmsContent)messages.get(i);
						if(other.equals(String.valueOf(smsContents.getId()))){
							size = i;
							break;
						}
					}
				}
				lv_sms.setSelection(size);
			}else{
				//滚动到列表最下方
				lv_sms.setSelection(messages.size()-1);
			}
		}
		
		
		smsProviderObserver = new SmsProviderObserver(new Handler());
		getContentResolver().registerContentObserver(THREADS_URI, true,smsProviderObserver);
		
	
//		System.out.println("  MessagetIntercepteNotifationUtil.THREAD_ID  --->" + MessagetIntercepteNotifationUtil.THREAD_ID);
	
		if(thread_id!=null && !"".equals(thread_id) && Integer.valueOf(thread_id) == MessagetIntercepteNotifationUtil.THREAD_ID)
		{
			NotificationManager nm = (NotificationManager) NewMessageActivity.this.getSystemService(NewMessageActivity.this.NOTIFICATION_SERVICE); 
			nm.cancel(MessagetIntercepteNotifationUtil.Notification_ID);
		}
		
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		try { //移除系统短信数据库的监听
			getContentResolver().unregisterContentObserver(smsProviderObserver);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	void createDialog()
	{
		//表情对话框
		dialog_expression = new Dialog(NewMessageActivity.this, R.style.theme_myDialog);
		dialog_expression.setCanceledOnTouchOutside(true);
		dialog_expression.setContentView(R.layout.dialog_expression);
		
		dialog_expression.findViewById(R.id.btn_expression_cancle).setOnClickListener(this);
		
		gv_expression=(GridView)dialog_expression.findViewById(R.id.gv_expression);
	    gv_expression.setOnItemClickListener(new EmotionListItemClickListener());
	    List<Map<String,Object>> listItems = new ArrayList<Map<String,Object>>();
	    
		int[] imageIds = new int[EmotionHelper.emotionResID.length];
		//生成107个表情的id，封装
		for(int i = 0; i < 40; i++){
			try {
				if(i<10){
					Field field = R.drawable.class.getDeclaredField("f00" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else if(i<100){
					Field field = R.drawable.class.getDeclaredField("f0" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}else{
					Field field = R.drawable.class.getDeclaredField("f" + i);
					int resourceId = Integer.parseInt(field.get(null).toString());
					imageIds[i] = resourceId;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
	        Map<String,Object> listItem = new HashMap<String,Object>();
			listItem.put("image", imageIds[i]);
			listItems.add(listItem);
			
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(NewMessageActivity.this, listItems, R.layout.team_layout_single_expression_cell, new String[]{"image"}, new int[]{R.id.image});
		gv_expression.setAdapter(simpleAdapter);
		
		//定时短信对话框
		dialog_timing_sms = new Dialog(NewMessageActivity.this, R.style.theme_myDialog);
		dialog_timing_sms.setCanceledOnTouchOutside(true);
		dialog_timing_sms.setOnDismissListener(new DialogInterface.OnDismissListener() {
			
			@Override
			public void onDismiss(DialogInterface dialog) {
				selected_timing_id = -1;
			}
		});
		dialog_timing_sms.setContentView(R.layout.dialog_timing_sms);
		
		View timePicker1 = dialog_timing_sms.findViewById(R.id.timePicker1);
		mWheelMain = new WheelMain(timePicker1);
		mWheelMain.initDateTimePicker();
			
		dialog_timing_sms.findViewById(R.id.btn_send_timing_sms_ok).setOnClickListener(this);
		dialog_timing_sms.findViewById(R.id.btn_send_timing_sms_cancle).setOnClickListener(this);
		
		
		//图片彩信对话框
		dialog_pic_mms = new Dialog(NewMessageActivity.this, R.style.theme_myDialog);
		dialog_pic_mms.setCanceledOnTouchOutside(true);
		dialog_pic_mms.setContentView(R.layout.dialog_pic_mms);
		
		
		dialog_pic_mms.findViewById(R.id.r_item_take_photo).setOnClickListener(this);
		dialog_pic_mms.findViewById(R.id.r_item_from_gallery).setOnClickListener(this);
		dialog_pic_mms.findViewById(R.id.btn_pic_mms_cancle).setOnClickListener(this);
		
	}
	
	
	/**
	 * 
	 * 将所有未读短信的状态设置为已读
	 * 
	 */
	private void setAllRead() {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				ContentValues values = new ContentValues();
				values.put("read", 1);
				int i = NewMessageActivity.this.getContentResolver().update(Uri.parse(base_uri_str+thread_id), values,"read = 0", null);
//				System.out.println(" ===== " + i +" 条  已读");
			}
		}).start();
	} 
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//         System.out.println("  resultCode  ---> " + resultCode);
		
		if (resultCode != NewMessageActivity.this.RESULT_OK)
			return;

		if(bmp_mms!=null && !bmp_mms.isRecycled())
		{
			bmp_mms.recycle();
		}
		
		Bitmap photo = null;
		BitmapFactory.Options ops = new BitmapFactory.Options();
		
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {
			Uri uri = data.getData();
			
//			System.out.println(" uri is ---> " + uri.toString());

			InputStream imgIS = null;
			try {
				ContentResolver cr = NewMessageActivity.this.getContentResolver();
				imgIS = cr.openInputStream(uri);
				
				ops.inJustDecodeBounds = true;
	        	
	        	BitmapFactory.decodeStream(imgIS,null, ops);

	        	 int sample_size = ops.outWidth / 480 / 4;
	        	 
	        	 if(sample_size>0)
	        	 {
	        			ops.inSampleSize = sample_size ;
	        	 }else{
	        		    ops.inSampleSize = 1 ;
	        	 }
	        	 
//	        	 System.out.println(" ops.inSampleSize  ---> : "  + ops.inSampleSize);
	        	 
	        	ops.inJustDecodeBounds = false;
	        	
	        	imgIS = cr.openInputStream(uri);
				photo = BitmapFactory.decodeStream(imgIS,null, ops);
				
				uri_mms = uri;
				
//				System.out.println(" photo  --->" + photo);
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				try {
					if (imgIS != null) {
//						imgIS.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mCurrentPhotoFile = null;
			break;
		}

		case CAMERA_WITH_DATA: {
			
        	ops.inJustDecodeBounds = true;
        	
        	BitmapFactory.decodeFile(mCurrentPhotoFile.getPath(),ops);

        	 int sample_size = ops.outWidth / 480 / 4;
        	 
        	 if(sample_size>0)
        	 {
        			ops.inSampleSize = sample_size ;
        	 }else{
        		 ops.inSampleSize = 1 ;
        	 }
        
        	ops.inJustDecodeBounds = false;
            
			photo = BitmapFactory.decodeFile(mCurrentPhotoFile.getPath(),ops);
			uri_mms = Uri.fromFile(mCurrentPhotoFile);
			
//			System.out.println(" mCurrentPhotoFile path ---> " + mCurrentPhotoFile.getPath());
//			System.out.println(" uri_mms --->" + uri_mms.toString());
			
			break;
		  }
		}
		
		if(photo!=null)
		{
			img_mms.setImageBitmap(photo);
			ln_mms.setVisibility(View.VISIBLE);
			
			btn_timing_sms.setImageResource(R.drawable.timing_sms);
			img_timing_sms.setVisibility(View.GONE);
			timing_sms_time = -1;
			
		}else{
			ln_mms.setVisibility(View.GONE);
		}
		
		bmp_mms = photo;
		
		super.onActivityResult(requestCode, resultCode, data);
		
	}
	

	/**
	 * 
	 * 查询当前会话的所有号码
	 * 
	 */
	public void  getNumbersByThreadId()
	{
		List<String> address_list = new ArrayList<String>();
			
		Uri canonical_addresses_uri = Uri.parse("content://mms-sms/canonical-addresses");
			
//		String [] projection = new String [] {"_id" , "date" , "message_count" , "recipient_ids" , "snippet" , "snippet_cs" , "read" ,"type" , "error" , "has_attachment" };
		Cursor c = NewMessageActivity.this.getContentResolver().query(Uri.parse("content://mms-sms/conversations?simple=true"), null , "_id = " +thread_id , null, null );
			
		if(c.moveToNext())
		{
			String recipient_ids = c.getString(c.getColumnIndex("recipient_ids"));
				
			String [] recipient_ids_array = recipient_ids.split(" ");
				
			//查询此会话下的所有号码
			String sql_str = "";
			int total = recipient_ids_array.length;
				
			for (int i = 0; i < total; i++) {
				sql_str += "_id =? OR ";
			}

			sql_str = sql_str.substring(0, sql_str.length() - 4); //去除最后一个 OR

			Cursor address_cursor = NewMessageActivity.this.getContentResolver().query(canonical_addresses_uri,new String[] { "address" }, sql_str,recipient_ids_array, null);

			while (address_cursor.moveToNext()) {
				String address = address_cursor.getString(address_cursor.getColumnIndex("address"));
				address_list.add(PhoneNumberTool.cleanse(address));
			}
			address_cursor.close();

			for (String a : address_list) 
			{
					System.out.println(" a  ---->" + a);
			}
		}
			
			c.close();
			
			int size = address_list.size();
			
			String numbers = null;
			
			if(size==1) //单个号码
			{
				numbers = address_list.get(0);
				
				String [] date = PhoneNumberTool.getContactInfo(NewMessageActivity.this, numbers);
				
				if(date!=null)
				{
					this.user_name = date[0];
					Bitmap bitmap = getPhoto(date[1]);
					
					if(bitmap!=null)
					{
						contactImage.setImageBitmap(bitmap);
					}
				}
				
				this.phoneNumber = numbers;
				
				if(this.user_name!=null)
				{
					tv_name.setText(user_name);
					tv_number.setText(phoneNumber);
				}else{
					tv_name.setText(phoneNumber);
				}
			
			}else{  // 群发
				
				String name  = " ";
				
				for(int i = 0 ;i <size;i++)
				{
					String nn = address_list.get(i);
					String info [] = PhoneNumberTool.getContactInfo(NewMessageActivity.this, nn);
					String display_name = info [0];
					
					if (display_name!=null) {
						name+=display_name+",";
					}else{
						display_name =" ";
						name+=nn+" ;";
					}
					
					phoneNumberList.add(nn);
					uNameNumber.add(display_name+":"+ nn);
				}
				name = name.substring(0, name.length()-1);
				
				contactImage.setVisibility(View.GONE);
				Ibtn_call.setVisibility(View.GONE);
				
				btn_showGroupInfo.setVisibility(View.VISIBLE);
				this.user_name = name;
				tv_name.setTextSize(18);
				tv_name.setText(name);
				
				tv_number.setVisibility(View.GONE); //隐藏号码栏，让名字栏居中显示
				
				
				GroupNameNumberAdapter groupNameNumberAdapter=new GroupNameNumberAdapter(NewMessageActivity.this, uNameNumber);
				
				View conViews=LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.group_list, null);
				ListView lv = (ListView) conViews.findViewById(R.id.group_list);
				lv.setAdapter(groupNameNumberAdapter);
				popupGroup = new PopupWindow(conViews, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
				popupGroup.setBackgroundDrawable(new BitmapDrawable());  //按返回键  以及点击  区域外 消失  (神奇的语句)
				popupGroup.setOutsideTouchable(true);
				
				isMultiConversation = true;
				
			}
		}
	
	
		// 监听短信库数据变化
	public class SmsProviderObserver extends ContentObserver {

		public SmsProviderObserver(Handler handler) {
			super(handler);
		}

			@Override
			public void onChange(boolean selfChange) {
			 refresh();
		}
	}
		
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			
			if (et_content.getText().toString().length()!=0){
				
				btn_send.setBackgroundResource(R.drawable.btn_send_ok);
			    btn_send.setTextColor(Color.WHITE);
			}else{
				btn_send.setBackgroundResource(R.drawable.btn_send_normal);
				btn_send.setTextColor(NewMessageActivity.this.getResources().getColor(R.color.text_color_base));
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
	};
	
	
	/**
	 * 
	 * 消息的长按的事件
	 * @author Administrator
	 *
	 */
	class MessagetItemLongClickListener implements OnLongClickListener
	{

		@Override
		public boolean onLongClick(View v) {
			try {
				mMessage= (MmsSmsContent)v.getTag();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			int type = -1;
			
			if(mMessage instanceof SmsContent)
			{
				selected_content = ((SmsContent)mMessage).getSms_body();
				type = ((SmsContent)mMessage).getSms_type();
			}else{
				selected_content = ((MmsContent)mMessage).getPart_text();
				type = ((MmsContent)mMessage).getMsg_box();
			}
			
			
			if(popup_text==null)
			{
				View conView=LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.drop_down_message, null);
				
				messageCopy=(Button)conView.findViewById(R.id.message_copy);
				messageDelete=(Button)conView.findViewById(R.id.message_delete);
				messageForward=(Button)conView.findViewById(R.id.message_forward);
				messageRemind=(Button)conView.findViewById(R.id.message_remind);
				messageFavorite=(Button)conView.findViewById(R.id.message_favorite);
				
				messageSendAgain=(Button)conView.findViewById(R.id.message_send_again);
				
				messageCopy.setOnClickListener(NewMessageActivity.this);
				messageDelete.setOnClickListener(NewMessageActivity.this);
				messageForward.setOnClickListener(NewMessageActivity.this);
				messageRemind.setOnClickListener(NewMessageActivity.this);
				messageFavorite.setOnClickListener(NewMessageActivity.this);
				
				messageSendAgain.setOnClickListener(NewMessageActivity.this);
				messageCopy.setTag(selected_content);
				popup_text = new Dialog(NewMessageActivity.this,R.style.theme_myDialog);
				popup_text.setCanceledOnTouchOutside(true);
				popup_text.setContentView(conView);
			}
				
				
				if(mMessage instanceof SmsContent) ////////短信
				{
					if( type ==  NewMessageActivity.SMS_TYPE_FAILED) //发送失败
					{
						messageSendAgain.setVisibility(View.VISIBLE);
						
						messageCopy.setVisibility(View.GONE);
						messageRemind.setVisibility(View.GONE);
						messageForward.setVisibility(View.GONE);
						messageFavorite.setVisibility(View.GONE);
						
						LinearLayout ln_content = (LinearLayout)popup_text.findViewById(R.id.content);
						ln_content.getChildAt(4).setVisibility(View.GONE);
						ln_content.getChildAt(6).setVisibility(View.GONE);
						ln_content.getChildAt(8).setVisibility(View.GONE);
						
					}else{
						
						messageSendAgain.setVisibility(View.GONE);
						
						messageCopy.setVisibility(View.VISIBLE);
						messageRemind.setVisibility(View.VISIBLE);
						messageForward.setVisibility(View.VISIBLE);
						messageFavorite.setVisibility(View.VISIBLE);
						
						LinearLayout ln_content = (LinearLayout)popup_text.findViewById(R.id.content);
						ln_content.getChildAt(2).setVisibility(View.VISIBLE);
						ln_content.getChildAt(4).setVisibility(View.VISIBLE);
						ln_content.getChildAt(6).setVisibility(View.VISIBLE);
						ln_content.getChildAt(8).setVisibility(View.VISIBLE);
					}
					
				}else{ ////////彩信
					
					if(((MmsContent)mMessage).getResp_st() == 130)
                	{
                        messageSendAgain.setVisibility(View.VISIBLE);
						
						messageCopy.setVisibility(View.GONE);
						messageRemind.setVisibility(View.GONE);
						messageForward.setVisibility(View.GONE);
						messageFavorite.setVisibility(View.GONE);
						
						LinearLayout ln_content = (LinearLayout)popup_text.findViewById(R.id.content);
						ln_content.getChildAt(4).setVisibility(View.GONE);
						ln_content.getChildAt(6).setVisibility(View.GONE);
						ln_content.getChildAt(8).setVisibility(View.GONE);
						
                	}else{
                		
                		messageSendAgain.setVisibility(View.GONE);
    					
    					messageCopy.setVisibility(View.GONE);
    					messageRemind.setVisibility(View.GONE);
    					messageForward.setVisibility(View.VISIBLE);
    					messageFavorite.setVisibility(View.GONE);
    					
    					LinearLayout ln_content = (LinearLayout)popup_text.findViewById(R.id.content);
    					ln_content.getChildAt(2).setVisibility(View.GONE);
    					ln_content.getChildAt(4).setVisibility(View.VISIBLE);
    					ln_content.getChildAt(6).setVisibility(View.GONE);
    					ln_content.getChildAt(8).setVisibility(View.GONE);
    					
                	}
				}
				
			
			popup_text.show();
			
			return true;
		}
   }
	
	/**
	 * 
	 * 删除某条消息: 彩信  OR 短信
	 * 
	 */
	public void deleteOneMessage()
	{
		//mark
		try {
			
			if(mMessage instanceof SmsContent) 
			{
				int i = NewMessageActivity.this.getContentResolver().delete(Uri.parse("content://sms/conversations/" + thread_id),"_id =" + mMessage.getId(), null);
				System.out.println(" i --->" + i);
			}else{
				int i = NewMessageActivity.this.getContentResolver().delete(Uri.parse("content://mms/"),"_id =" + mMessage.getId(), null);
				System.out.println(" i --->" + i);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * 
	 * 收藏某条短信
	 * 
	 */
	public void favoriteMessage()
	{

		String content = "";
		int type = -1;
		String address = "";
		
		if(mMessage instanceof SmsContent)
		{
			content = ((SmsContent)mMessage).getSms_body();
			type = ((SmsContent)mMessage).getSms_type();
			address = ((SmsContent)mMessage).getSms_number();
		}else{
			content = ((MmsContent)mMessage).getSubject();
			type = ((MmsContent)mMessage).getMsg_box();
			address = ((MmsContent)mMessage).getAddress();
		}
		
		String info [] = PhoneNumberTool.getContactInfo(NewMessageActivity.this, address);
		String name = info [0];
		
		if(name ==null)
		{
			name = address;
		}
		
		try {
			myDatabaseUtil.insertDataFavorite(thread_id, String.valueOf(mMessage.getId()), content, System.currentTimeMillis(), type==2 ?  "我":name, PhoneNumberTool.cleanse(address));
			Toast.makeText(NewMessageActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			e.getStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * 查询当前会话下的所有消息
	 * 
	 * @return
	 */
	public ArrayList<MmsSmsContent> queryConversation()   
	{   
		ArrayList<MmsSmsContent> messages = new ArrayList<MmsSmsContent>();
		
//		System.out.println("thread_id --- > " + thread_id);
		
		Uri CONVERSATION_URI = Uri.parse("content://mms-sms/conversations/"+thread_id);
		
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
				 "read",
				 "m_type",
				 "m_size",
				 "msg_box",
				 "d_rpt",
				 "rr",
				 "ct_t",
				 "st",
				 "retr_st",
				 "resp_st",
				 "d_rpt",
				 "exp",
				 "err_type",
				 "locked",
				 "sim_id",
				 "resp_txt",
				 "read_status",
				 "d_tm",
				 "service_center" };
		 
		try {
			
			 ContentResolver cr = NewMessageActivity.this.getContentResolver();   
			 Cursor cursor = cr.query(CONVERSATION_URI, PROJECTION, null, null,null);

			 int id_colmun = cursor.getColumnIndex("_id");
			 int ct_t_column = cursor.getColumnIndex("ct_t");
			 
			 System.out.println("  message  size   ---> " + cursor.getCount() );
			 
			 while (cursor.moveToNext()) {
				
				 String ct_t = cursor.getString(ct_t_column);
				 long id = cursor.getLong(id_colmun);
				 
				 int  st = cursor.getInt(cursor.getColumnIndex("st"));
				 
				 int msg_box = cursor.getInt(cursor.getColumnIndex("msg_box"));
				 
				 if( ct_t!=null || ( st!=0) )// 彩信
				 {
						 long date =  (Long.valueOf(cursor.getLong(cursor.getColumnIndex("date"))+"000"));
						 
						 
//						 if(date!=0)
//						 {
							 MmsContent mmsContent = new MmsContent();
							 mmsContent.setMessage_type(MmsSmsContent.M_TYPE_MMS);
							 mmsContent.setId(id);
							 
							 mmsContent.setDate(Long.valueOf(cursor.getLong(cursor.getColumnIndex("date"))+"000")); //彩信的时间在数据库中存储的单位为秒,在这转换为毫秒
							 
							 String subject = cursor.getString(cursor.getColumnIndex("sub"));
							 
							 if(subject!=null)
							 {
								 mmsContent.setSubject(new String(subject.getBytes("ISO8859_1"),"utf-8"));
							 }else{
								 mmsContent.setSubject("无主题");
							 }
							 mmsContent.setMsg_box(cursor.getInt(cursor.getColumnIndex("msg_box")));
							 mmsContent.setSt(cursor.getInt(cursor.getColumnIndex("st")));
							 mmsContent.setExp(cursor.getLong(cursor.getColumnIndex("exp")));
							 mmsContent.setRetr_st(cursor.getInt(cursor.getColumnIndex("retr_st")));
							 mmsContent.setResp_st(cursor.getInt(cursor.getColumnIndex("resp_st")));
							 mmsContent.setD_rpt(cursor.getInt(cursor.getColumnIndex("d_rpt")));
							 mmsContent.setD_tm(cursor.getLong(cursor.getColumnIndex("d_tm")));
							 mmsContent.setM_size(cursor.getInt(cursor.getColumnIndex("m_size"))); 
							 mmsContent.setResp_txt(cursor.getString(cursor.getColumnIndex("resp_txt"))); 
							 mmsContent.setRead_status(cursor.getInt(cursor.getColumnIndex("read_status"))); 
							 messages.add(mmsContent);
							 
//							System.out.println("  mmsContent  ---> " + mmsContent.toString());
//						 }
						 
				 }else{ //短信
					 
						 SmsContent smsContent=new SmsContent();
						 
						 String phone = cursor.getString(cursor.getColumnIndex("address"));
						 int type = cursor.getInt(cursor.getColumnIndex("type")); // 2 = sent, etc.
						 long date = cursor.getLong(cursor.getColumnIndex("date"));
						 String body = cursor.getString(cursor.getColumnIndex("body"));
						 int status = cursor.getInt(cursor.getColumnIndex("status"));
						 
						 smsContent.setMessage_type(MmsSmsContent.M_TYPE_SMS);
						  
//						 System.out.println("  type  --->" + type);
						 
						 smsContent.setId(id);
						 smsContent.setSend_type(String.valueOf(type));
						 smsContent.setSms_type(type);
						 smsContent.setDate(date);
						 smsContent.setSms_body(body);
						 smsContent.setSms_number(phone);
						 smsContent.setStatus(status);
						 
				         messages.add(smsContent);
				         
				 }
			}
			cursor.close();
			
			
			//是否有短信草稿
			Cursor draft_cur = cr.query(Uri.parse("content://sms/draft"), null , "thread_id = " + thread_id, null, null);
			if(draft_cur.moveToNext())
			{
				   draft_sms_id = draft_cur.getLong(draft_cur.getColumnIndex("_id"));
	               draft_text = draft_cur.getString(draft_cur.getColumnIndex("body"));
			}
			draft_cur.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		//按时间排序,从小到大 ， 最新收到的放在最低
		ArrayList<MmsSmsContent> result = new ArrayList<MmsSmsContent>();
		
		if(messages.size() > 0) //有可能还没有存进数据库
		{
			
			int size = messages.size();
			
			//找出最大的 
			int max_index = -1;
			
			for(int i = 0; i < size ; i++)
			{
				if(i==0)
				{
					max_index = 0;
				}else{
					if(messages.get(i).getDate() >= messages.get(max_index).getDate())
					{
						max_index = i;
					}
				}
			}
			
			result.add(0,messages.get(max_index));
			
			
			for (int i = 0; i < size ; i++) {
				
				if(i!=max_index)
				{
					MmsSmsContent source_c = messages.get(i);
					
				    int index = -1;
				
					for (int j = 0; j < result.size(); j++) {
						
						MmsSmsContent re_c = result.get(j);
						
						if (source_c.getDate() < re_c.getDate()) {
							index = j;
							break;
						}
							
						if (j == result.size() - 1) {
							index = j;
						}
					}
					result.add(index, source_c);
				}
			}
		}else{
			result = messages;
		}
		
		return result;
	} 
	
	
	/**
	 * 草稿的相关
	 * @return
	 */
	boolean  draftStuff()
	{
		Uri uri = Uri.parse("content://sms/draft");
		
		if(draft_sms_id!=-1 && et_content.getText().toString().length()==0) //删除草稿
		{
			Uri uri_all = Uri.parse("content://sms/");
			NewMessageActivity.this.getContentResolver().delete(uri_all, "_id = " +  draft_sms_id, null);
			
			System.out.println(" 删除草稿  --->");
		    return false;
		}
		
		if(state == STATE_DETAIL  && et_content.getText().toString().length()!=0)  //在短信详情中，并且有输入内容
		{
			
			System.out.println("draft_text  --->" + draft_text );
			System.out.println(" et_content.getText().toString() --->" + et_content.getText().toString() );
			
			if(draft_sms_id!=-1) //更新草稿
			{
				ContentValues cv = new ContentValues();
				cv.put("body", et_content.getText().toString());
				cv.put("date", System.currentTimeMillis());
				cv.put("address", phoneNumber);
				cv.put("thread_id", thread_id);
					
				NewMessageActivity.this.getContentResolver().update(uri, cv, "_id = " +  draft_sms_id  , null);
//				System.out.println(" 更新草稿 ");
				
			}else{ //新增草稿
				
				ContentValues cv = new ContentValues();
				cv.put("body", et_content.getText().toString());
				cv.put("date", System.currentTimeMillis());
				cv.put("address", phoneNumber);
				cv.put("thread_id", thread_id);
				
				NewMessageActivity.this.getContentResolver().insert(uri, cv);
			}
			
			Toast.makeText(NewMessageActivity.this, "已存草稿", Toast.LENGTH_SHORT).show();
			return false;
			
		}
		
		else if( state ==  STATE_NEW ) {  //新建短信
			
			if( (et_content.getText().toString().length()!=0 ) && phoneNumberList.size()>0 || (et_number_input!=null &&  et_number_input.getText().toString().length()>0))
			{
				
				List<String> addr=new ArrayList<String>();
		        for(int i=0;i<phoneNumberList.size();i++){
		                addr.add(phoneNumberList.get(i));         
		        }
		        
		        if(et_number_input!=null)
		        {
		        	   String input_number  = et_number_input.getText().toString();
		               
		        	   System.out.println(" input_number  --->" + input_number);
		        	   
		               boolean isNumber = false;
		               
		               try {
		       			Long.valueOf(input_number);
		       			isNumber = true;
		       		} catch (Exception e) {
		       			
		       		}
		               
		               if(input_number.length()>0 && isNumber)
		               {
		               	 addr.add(input_number);
		               }
		        }
		     
		        
		        if(addr.size()>0 && et_content.getText().toString().length()>0)
		        {
		        	long thread_id = Threads.getOrCreateThreadId(NewMessageActivity.this, addr);
					 
		        	System.out.println(" thread_id ---> " + thread_id);
		        	
					ContentValues cv = new ContentValues();
					cv.put("body", et_content.getText().toString());
					cv.put("date", System.currentTimeMillis());
					String number ="";
					
					if(phoneNumberList.size()>0)
					{
						number = phoneNumberList.get(0);
					}else{
						number = et_number_input.getText().toString();
					}
					
					cv.put("address",number);
					
					cv.put("thread_id", thread_id);
					
					NewMessageActivity.this.getContentResolver().insert(uri, cv);
					
					Toast.makeText(NewMessageActivity.this, "已存草稿", Toast.LENGTH_SHORT).show();
		        }
		        return false;
		        
			}else{ //没有有效的联系人，短信将丢失
				
				if(et_content.getText().toString().length()>0) //有内容
				{
					Dialog dialog = new AlertDialog.Builder(NewMessageActivity.this).setMessage("无有效收件人,短信会被丢弃。").setPositiveButton("确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					}).setNegativeButton("取消", null).create();
					
					dialog.show();
					return true;
				}
			}
		}
		return false;
	}
	
	
	   //复制
	  public void setClipboard(String text) {
	     ClipboardManager clipboard = (ClipboardManager)NewMessageActivity.this.getSystemService(Context.CLIPBOARD_SERVICE);
	     clipboard.setText(text);
	   }
	  
	  
	/**
	 * 
	 * 重发
	 * 
	 */
	void sendAgain()
	{
		
		List<String> addr = new ArrayList<String>();
		String address = "" ;
		if(mMessage instanceof SmsContent)   //重发短信
		{
			address = ((SmsContent)mMessage).getSms_number();
			addr.add(address);
			
			deleteOneMessage();
			sendSMS(addr, selected_content, Long.valueOf(thread_id));
			
		}else{  //重发彩信
			
			MmsContent mms = (MmsContent)mMessage;
			
			String imagPath = saveBitmap2SD(mms.getPart_bmp());
				
			System.out.println(" imagPath  --->" + imagPath);
			
		   if(imagPath!=null)
		   {
				
				String subject = "";
				String content = "";
				if(!"无主题".equals(mms.getSubject()))
				{
					subject =mms.getSubject();
				}
				
				if(mms.getPart_text()!=null)
				{
					content = mms.getPart_text();
				}
				
				//查address(号码)
				System.out.println(" mms.getId()  --->" + mms.getId());
				
				Cursor address_cursor = NewMessageActivity.this.getContentResolver().query(Uri.parse("content://mms/"+mms.getId()+"/addr"), new String [] {"address"}, null, null, null);
				if(address_cursor.moveToNext())
				{
					address = address_cursor.getString(address_cursor.getColumnIndex("address"));
				}
				address_cursor.close();
				addr.add(address);
				
				
				System.out.println(" subject ---> " + subject);
				System.out.println(" content ---> " + content);
				System.out.println(" address  --->" + address);
				
				deleteOneMessage();
				
				sendMMS(addr, Long.valueOf(thread_id),subject,content,imagPath);
		   }
		}

		et_content.setText("");
		hideKeyBoard();
		
		if(popup_text!=null)
		{
			popup_text.dismiss();
		}
		
	}
	
	
	public static boolean isPhoneNumberValid(String phoneNumber){  
        
        boolean isValid = false;  
        String expression = "^//(?(//d{3})//)?[- ]?(//d{3})[- ]?(//d{5})$";  
        String expression2 = "^//(?(//d{3})//)?[- ]?(//d{4})[- ]?(//d{4})$";  
        CharSequence inputStr = phoneNumber;  
          
        Pattern pattern = Pattern.compile(expression);  
          
        Matcher matcher = pattern.matcher(inputStr);  
          
        Pattern pattern2 = Pattern.compile(expression2);  
          
        Matcher matcher2 = pattern2.matcher(inputStr);  
          
        if(matcher.matches()||matcher2.matches()){  
            isValid = true;  
        }  
          
          
        return isValid;  
          
    }  
	
	
	private int writeSmsToDataBase(String phoneNumber, String smsContent,long threadId)  
    {  
	
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", smsContent);  
        values.put("type", 0); 
        values.put("read", 1);
        values.put("status", SEND_PENDING );
        values.put("thread_id", threadId);
        
        Uri uri =   NewMessageActivity.this.getContentResolver().insert(SMS_URI, values);
//        Uri uri =   NewMessageActivity.this.getContentResolver().insert(uri, values);
        
        Cursor c = NewMessageActivity.this.getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        
        c.moveToNext();

        int id = c.getInt(c.getColumnIndex("_id"));
        
        c.close();
        
        System.out.println("  threadId  ---> " + threadId);
        System.out.println("  插入短信成功了 id    ----->"  +  id);

        SmsContent sms = new SmsContent();
        sms.setDate(System.currentTimeMillis());
        sms.setId(Long.valueOf(id));
        sms.setSms_number(phoneNumber);
        sms.setSms_body(smsContent);
        sms.setSend_type("0");
        sms.setTypeId(0);
        sms.setStatus(SEND_PENDING);
        
        c.close();
        
        return id;
    }  
	
	
	private int writeToDataBaseTiming(String phoneNumber, String smsContent,long threadId)  
    {  
        ContentValues values = new ContentValues();
        values.put("address", phoneNumber);
        values.put("body", smsContent);  
        values.put("type", 0); 
        values.put("read", 1);
        values.put("status", 32);
        values.put("date", timing_sms_time);
        values.put("thread_id", threadId);
        Uri uri =   NewMessageActivity.this.getContentResolver().insert(SMS_URI, values);
        
        Cursor c = NewMessageActivity.this.getContentResolver().query(uri, new String[]{"_id"}, null, null, null);
        
        c.moveToNext();
        
        int id = c.getInt(c.getColumnIndex("_id"));
        
        c.close();

        SmsContent sms = new SmsContent();
        sms.setDate(timing_sms_time);
        sms.setId(Long.valueOf(id));
        sms.setSms_number(phoneNumber);
        sms.setSms_body(smsContent);
        sms.setSend_type("0");
        sms.setTypeId(0);
        sms.setStatus(32);
        
        c.close();
    	
        return id;
    }  
	
	
	/**
	 * 
	 * 从新建短信界面布局   切换至  短信详情布局
	 * 
	 * @param numbers
	 */
	public void fromNewToDetail(List<String> numbers)
	{
		
		tv_top.setText("短信详情");
		
		if(rl_number_input!=null)
		{
			if(rl_number_input.getVisibility()!=View.GONE)
			rl_number_input.setVisibility(View.GONE);
		}
		
		rl_message_detail_top.setVisibility(View.VISIBLE);
		
		
		state= STATE_DETAIL;
		
		Ibtn_call=(ImageButton)view.findViewById(R.id.message_contact_call_button);
		
		
		if(numbers.size()>1) //群发
		{
			String nameAndNumber[]	=numberBuffer.toString().split(",");
			ArrayList<String> uName=new ArrayList<String>();
			
			uNameNumber.clear();
			
			String ss ="";
			for(String names:nameAndNumber)
			{
				String name[] =   names.split(":");
				uName.add(name[0]);
				uNameNumber.add(names);
				
				if (name[0].equals("")) {
					ss = name[1];
				} else {
					ss += name[0]+",";
				}
			 }
			
			ss.substring(ss.length()-2, ss.length()-1);
		    	 
			tv_name.setText(ss);
			
			btn_showGroupInfo.setVisibility(View.VISIBLE);
			
			Ibtn_call.setVisibility(View.GONE);
			contactImage.setVisibility(View.GONE);
			
			isMultiConversation = true;
			
		}else{
			
			String number = numbers.get(0);
			String [] data = PhoneNumberTool.getContactInfo(NewMessageActivity.this, number);
			
			phoneNumber = number;
			
			if(data==null)
			{
				tv_name.setText(number);
				tv_number.setText("");
				
				contactImage.setImageResource(R.drawable.default_contact);
				
			}else{
				
				tv_name.setText(data[0]);
				tv_number.setText(number);
				
				Bitmap bitmap = getPhoto(data[1]);
				if(bitmap !=null)
				{
					contactImage.setImageBitmap(bitmap);
				}
			}
			
			btn_showGroupInfo.setVisibility(View.GONE);
			
			Ibtn_call.setVisibility(View.VISIBLE);
			contactImage.setVisibility(View.VISIBLE);
		}
		
		
		messages = queryConversation();
		smsDetailChatAdapter=new MessageDetailChatAdapter(NewMessageActivity.this, isMultiConversation, messages, new MessageItemClickListener() , new MessagetItemLongClickListener());
		lv_sms.setAdapter(smsDetailChatAdapter);
		lv_sms.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				lv_sms.setSelection(smsDetailChatAdapter.getCount()-1);
			}
		}, 500);
		
		
	}
	
	
	/**
	 * 发送短信
	 * @param phone 号码列表
	 * @param body 短息内容
	 * @param threadId  会话id
	 */
	public void sendSMS(List<String> phone,String body,long threadId){
		
		    isSending = true;
		   
    		
    		Intent sentIntent = new Intent(SENT_SMS_ACTION);  
    	    Intent deliverIntent = new Intent(DELIVERED_SMS_ACTION);  
    	    
    	    System.out.println(" body  --->" + body);
    	    
            for(final String pno:phone ){
                
                List<String> texts = smsManager.divideMessage(body); 
                
		        //逐条发送短信  
				for(final String text:texts)  
				{
				   int id = writeSmsToDataBase(pno, text, threadId);
				   
				   sentIntent.putExtra("_id", id);
				   final PendingIntent  sentPI = PendingIntent.getBroadcast(NewMessageActivity.this, id, sentIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				   
				   deliverIntent.putExtra("_id", id);
				   final PendingIntent deliverPI = PendingIntent.getBroadcast(NewMessageActivity.this, id, deliverIntent,PendingIntent.FLAG_UPDATE_CURRENT);
				   
				   new Thread(new Runnable() {
					
					@Override
					public void run() {
						smsManager.sendTextMessage(pno, null, text, sentPI, deliverPI);
					}
				}).start();
				   
				   System.out.println(" 发送 ： ============ pno :" + pno + " text :" + text );
				   
				}         
           }   
            
            isSending = false;
            refresh();
            
        }
	  
	
	  /**
	   * 定时发送短信
	   * @param phone 号码列表
	   * @param body 短信内容
	   * @param threadId 会话id
	   */
	  public void sendSMSTimming(List<String> phone,String body,long threadId){
  		
		  isSending = true;
		  
          for(String pno:phone ){
              
              List<String> texts = smsManager.divideMessage(body); 
              
		        //逐条发送短信  
				for(String text:texts)  
				{
				   int id = writeToDataBaseTiming(pno, text, threadId);
				   
				   triggleAlarmService(id);
				   System.out.println(" triggle   timing sms --->" + TimeTool.getTimeStrYYMMDDHHMM(timing_sms_time));
				}         
          }
          
          isSending = false;
          refresh();
      }
	  
	  
	  /**
	   * 发送彩信
	   * @param phone  电话号码
	   * @param threadId  会话id
	   * @param subject 主题
	   * @param content 内容
	   * @param imagePath  图片路径
	   */
	  private void sendMMS(List<String> phone,final long threadId,final String subject,final String content,String imagePath)
	  {

		  isSending = true;
		  
		  if(imagePath==null)
		  {
			  if(mCurrentPhotoFile!=null)
			   {
				  imagePath = mCurrentPhotoFile.getPath();
				   
			   }else{
				   
				   if(uri_mms.toString().contains("content://"))
				   {
					   String[] proj = { MediaStore.Images.Media.DATA };
					   Cursor actualimagecursor = NewMessageActivity.this.managedQuery(uri_mms,proj,null,null,null);
					   int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					   actualimagecursor.moveToFirst();
					   imagePath = actualimagecursor.getString(actual_image_column_index);

					   if(Integer.parseInt(Build.VERSION.SDK) < 14)  
		                {  
						   actualimagecursor.close();  
		                }  
				   }else{
					   imagePath = uri_mms.toString().replace("file://", "");
				   }
			   }
			   
		  }
		  
		  
		   final String img_path = imagePath;
		   
           for(final String pno:phone ){
        	   
        	   new Thread(new Runnable() {
   				
   				@Override
   				public void run() {
   					
   					System.out.println("发送彩信    --->" + " pno: " + pno + "  subject :" +subject +"   content:" +content +"  img_path:" +img_path );
   						boolean b = MMSSender.sendMMS(NewMessageActivity.this, threadId, pno, subject, content, img_path);
   						if(!b)
   						{
   							error_handler.sendEmptyMessage(0);
   						}
   				}
   			}).start();
           } 
           
           isSending = false;
           refresh();
	}
	  
	  
	  
	/**
	 * 
	 * 注册闹钟服务，发送定时短信
	 * 
	 * @param id
	 */
	void triggleAlarmService(int id) 
	{
		Intent it = new Intent(NewMessageActivity.this, AlarmReceiver.class);
		it.putExtra(AlarmReceiver.ALARM_TYPE, AlarmReceiver.ALARM_SMS);
		it.putExtra("_id", id);

		// 采用基数 +短信id的拼接， 得出一个绝对唯一的 request code , 让系统区分不同的闹钟服务
		PendingIntent pit = PendingIntent.getBroadcast(NewMessageActivity.this,MyDatabaseUtil.BASE_SMS_TIMING_ID + id, it, 0);
		AlarmManager amr = (AlarmManager) NewMessageActivity.this.getSystemService(Activity.ALARM_SERVICE);
		amr.set(AlarmManager.RTC_WAKEUP, timing_sms_time, pit);
	}
	
	
	public void send() 
	{
		
		String msg_str = et_content.getText().toString();
		
		// 隐藏输入联想布局
		if (ln_number_tips != null) {
			ln_number_tips.setVisibility(View.GONE);
		}
		

		if (msg_str.length() > 0 || ln_mms.getVisibility()==View.VISIBLE ) {

			
			if (state == STATE_NEW) // =================  新建短信  =======================
			{

				String input_number = et_number_input.getText().toString();

				boolean isCheck = false;

				try {
					Long.valueOf(et_number_input.getText().toString());
					isCheck = true;
				} catch (Exception e) {

				}

				if (phoneNumberList.size() == 0 && !isCheck) {
					
					Toast.makeText(NewMessageActivity.this, "请输入有效的收件人", Toast.LENGTH_SHORT).show();
					
				}else if (phoneNumberList.size() > 0 || input_number.length() > 0) // 至少有一个号码
				{
					//  =====================  符合发送条件   =========================
					
					List<String> addr = new ArrayList<String>();
					for (int i = 0; i < phoneNumberList.size(); i++) {
						addr.add(phoneNumberList.get(i));
					}

					if (input_number.length() > 0 && isCheck) {
						addr.add(input_number);
					}

					long id = Threads.getOrCreateThreadId(NewMessageActivity.this, addr);
					this.thread_id = String.valueOf(id);

					if(img_timing_sms.getVisibility()==View.VISIBLE)  //定时短信
					{
	                    sendSMSTimming(addr, msg_str, Long.valueOf(thread_id));
						
						img_timing_sms.setVisibility(View.GONE);
						btn_timing_sms.setImageResource(R.drawable.timing_sms);
						timing_sms_time = -1;
						
					}else if (ln_mms.getVisibility() == View.VISIBLE){  //彩信
						   
						   String subject = tv_mms_subject.getText().toString();
						   String content =  et_content.getText().toString();
						   
						   sendMMS(addr,Long.valueOf(thread_id),subject,content,null);
						   ln_mms.setVisibility(View.GONE);
						   tv_mms_subject.setText("");
						   
					}else{ //正常短信
						
						sendSMS(addr, msg_str, Long.valueOf(thread_id));
						
					}
					
					fromNewToDetail(addr);

					et_content.setText("");
					hideKeyBoard();
					ln_more.setVisibility(View.GONE);
					
					// =====================  发送完毕  =========================

				} else {
					Toast.makeText(NewMessageActivity.this, "请输入有效的号码", Toast.LENGTH_SHORT).show();
				}

			} else { //=================  短信详情    =======================

				List<String> addr = new ArrayList<String>();
				
				if (phoneNumberList.size() != 0) // 群发
				{
					for (int i = 0; i < phoneNumberList.size(); i++) {
						addr.add(phoneNumberList.get(i));
					}
				} else { // 单发
					addr.add(phoneNumber);
				}
				
				
				if(img_timing_sms.getVisibility()==View.VISIBLE)  //定时短信
				{
                    sendSMSTimming(addr, msg_str, Long.valueOf(thread_id));
					
					img_timing_sms.setVisibility(View.GONE);
					btn_timing_sms.setImageResource(R.drawable.timing_sms);
					timing_sms_time = -1;
					
				}else if (ln_mms.getVisibility() == View.VISIBLE){  //彩信
					
					   String subject = tv_mms_subject.getText().toString();
					   String content =  et_content.getText().toString();
					   
					   sendMMS(addr,Long.valueOf(thread_id),subject,content,null);
					   
					   ln_mms.setVisibility(View.GONE);
					   tv_mms_subject.setText("");
					   
				}else{ //正常短信
					
					  sendSMS(addr, msg_str, Long.valueOf(thread_id));
				}

				et_content.setText("");
				hideKeyBoard();
				ln_more.setVisibility(View.GONE);
				
				// =====================  发送完毕    =========================
			}

		} else {
			Toast.makeText(NewMessageActivity.this, "请输入短信内容", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	
	/**
	 * 
	 * 添加定时短信的时间
	 * 
	 */
	void addTimingSms()
	{
		String time = mWheelMain.getTimemilInFormat();
		
//		System.out.println(" addTimingSms ---> " + time );
		
		String [] tt = time.split("/");
		String year =tt[0];
		String month = tt[1];
		String day = tt[2];
		String hour = tt[3];
		String minute = tt[4];
		
		Date date = new Date(Integer.valueOf(year)-1900, Integer.valueOf(month)-1, Integer.valueOf(day), Integer.valueOf(hour), Integer.valueOf(minute));
		
//		System.out.println("date: " + year +"-"+ month +"-"+day+" "+hour+":"+minute);
//		System.out.println(" date.getTime();  ----> " + date.getTime());
		
		long pick_time = date.getTime();
		
		if(pick_time<System.currentTimeMillis())
		{
			Toast.makeText(NewMessageActivity.this, "定时短信时间不得小于当前时间", Toast.LENGTH_SHORT).show();
			
		}else
		{
			
			if(selected_timing_id==-1) //新建定时短信
			{
				timing_sms_time = pick_time;
				
				img_timing_sms.setVisibility(View.VISIBLE);
				btn_timing_sms.setImageResource(R.drawable.timing_sms_selected);
				
			}else //修改定时短信的时间
			{
				ContentValues cv = new ContentValues();
				cv.put("date", pick_time);
				NewMessageActivity.this.getContentResolver().update(Uri.parse("content://sms/"), cv, "_id = " + selected_timing_id , null);
				
				cancleTimingSms(selected_timing_id);
				triggleAlarmService(Integer.valueOf(String.valueOf(selected_timing_id)));
				updateAfterChangeTimingSmsTime(selected_timing_id,pick_time);
				selected_timing_id = -1;
				
			}
		}
	}
	
	
	/**
	 * 
	 * 取消定时短信
	 * 
	 */
	void cancleTimingSms(long id)
	{
		Intent it = new Intent(NewMessageActivity.this, AlarmReceiver.class);
		PendingIntent pit = PendingIntent.getBroadcast(NewMessageActivity.this, MyDatabaseUtil.BASE_SMS_TIMING_ID + Integer.valueOf(String.valueOf(id)), it, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager amr = (AlarmManager) NewMessageActivity.this.getSystemService(Activity.ALARM_SERVICE);
		amr.cancel(pit);
	}
	   
	
	/**
	 * 隐藏输入法键盘
	 * 
	 */
	void hideKeyBoard()
	{
		InputMethodManager imm = (InputMethodManager) NewMessageActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);  
	}
	
	
	@Override
	public void onBackPressed() {
		
		if(!handleBackPressed())
		{
			state = 0;
			super.onBackPressed();
		}
	}
	
	/**
	 * 
	 * 处理返回键事件
	 * 
	 * @return
	 */
	public boolean handleBackPressed()
	{
		if(rl_group!=null && rl_group.getVisibility()==View.VISIBLE)
		{
			Animation a = AnimationUtils.loadAnimation(NewMessageActivity.this, R.anim.dialing_out);
			a.setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation animation) {
				}
				
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				
				@Override
				public void onAnimationEnd(Animation animation) {
					rl_group.setVisibility(View.GONE);
				}
			});
			rl_group.startAnimation(a);
			return true;
		}else if(ln_more!=null && ln_more.getVisibility()==View.VISIBLE) {
			ln_more.setVisibility(View.GONE);
			return true;
		}else if(ln_number_tips!=null && ln_number_tips.getVisibility() ==View.VISIBLE )
		{
			ln_number_tips.setVisibility(View.GONE);
			return true;
		}
		
		return draftStuff();
	}
	
	
	/**
	 * 
	 * 获取所有  有号码的  且名字为非空的联系人
	 * 
	 */
	public void getContast()
	{
		
		 /*
         * 获取联系人信息，将联系人的名字存入nameList,将联系人的号码存入numberList
         */
		Uri uri = Contacts.CONTENT_URI;
		String[] projection = new String[] { Contacts._ID, // 0
				Contacts.DISPLAY_NAME, // 1
				Contacts.STARRED, // 2
				Contacts.TIMES_CONTACTED, // 3
				Contacts.CONTACT_PRESENCE, // 4
				Contacts.PHOTO_ID, // 5
				Contacts.LOOKUP_KEY, // 6
				"sort_key",
				Contacts.HAS_PHONE_NUMBER, // 7
				Contacts.IN_VISIBLE_GROUP, // 8
		};
		ContentResolver content = NewMessageActivity.this.getContentResolver();
		String sortOrder =  "sort_key  COLLATE LOCALIZED ASC ";
		Cursor cursor = content.query(uri, projection, " sort_key is not null AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER  + " is not 0", null,
				sortOrder);
        
        
		int name_column = cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME);
		int contact_id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
		int sort_key_column = cursor.getColumnIndex("sort_key");
		int has_phone_number_column = cursor.getColumnIndex(Contacts.HAS_PHONE_NUMBER);
		
        while(cursor.moveToNext()){    
        
            //获取联系人的ID，并根据ID来获取电话号码
            //注意：一个联系人可以有多个电话号码，按照这种方式储存的话，nameList与numberList中的数据将失去联系
            long contactId = cursor.getLong(contact_id_column);
          
            int phone_count = cursor.getInt(has_phone_number_column);
            String sort_key = cursor.getString(sort_key_column);
            
            if(phone_count>0 && sort_key!=null && !sort_key.equals(""));
            {
            	Cursor phoneCursor = content.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String [] {ContactsContract.CommonDataKinds.Phone.NUMBER}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="+ contactId, null, null);
                
                
                if(phoneCursor!=null)
                {
                	 while(phoneCursor.moveToNext()){   
                		 
                			//获取联系人的姓名
                         String name=cursor.getString(name_column);
                         
                		 ContactBean contactBean = new ContactBean(); 
                		 contactBean.setNick(name);
                		 contactBean.setContact_id(contactId);
                		 contactBean.setSork_key(sort_key);
                         String number = phoneCursor.getString(0);
                         contactBean.setNumber(number);
                         
                         if(contactBean.getNick()!=null && number!=null)
                         {
                         	contacts.add(contactBean);
                         }
                     }
                     phoneCursor.close();
                }
            }
            
        }
        cursor.close();
	}
	
	
	/**
	 * 
	 * 获取分组信息
	 * 
	 * @return
	 */
	public ArrayList<GroupInfo> getContactGroup()
	{
		// 我们要得到分组的id 分组的名字
		String[] RAW_PROJECTION = new String[] { ContactsContract.Groups._ID,
		                ContactsContract.Groups.TITLE, };
		    // 查询条件是Groups.DELETED=0
		String RAW_CONTACTS_WHERE = ContactsContract.Groups.DELETED + " = ? ";
		// 条用内容提供者查询 new String[] { "" + 0 } 是给Groups.DELETED赋值
		        Cursor cursor = NewMessageActivity.this.getContentResolver().query(
		        ContactsContract.Groups.CONTENT_URI, RAW_PROJECTION,
		        RAW_CONTACTS_WHERE, new String[] { "" + 0 }, null);
		 
		        // 存放分组信息
		        ArrayList<GroupInfo> islist = new ArrayList<GroupInfo>();
		        // 默认在集合放一个没有分组的，组名
		        // 便于 以后查询，没有分组的联系人
		        GroupInfo ginfo = new GroupInfo();
		        ginfo.setGroup_name("未分组");
		        ginfo.setGroup_id((-1));
		        islist.add(ginfo);
		        GroupInfo groupInfo = null;
		        // cursor.moveToFirst();
		        while (cursor.moveToNext()) {
		            // 分组的实体类
		        	
				    groupInfo = new GroupInfo();
				    groupInfo.setGroup_id(cursor.getInt(cursor.getColumnIndex("_id")));
				    groupInfo.setGroup_name(cursor.getString(cursor.getColumnIndex("title")));
				            // 把分组放到集合里去
				    islist.add(groupInfo);
		        }
		        cursor.close();
		        return islist;
	}
	
	
    /**
     * 
     * 批量添加收件人
     * 
     */
	private void setNumbers() {
		
		String source = numberBuffer.toString();
		
		if(partner_type==0 && remindContactsMutilAdapter!=null)
		{
			int [] selecteds =  remindContactsMutilAdapter.getSelectedItemIndexes();
			
			
			if(selecteds.length>0)
			{
				for(int i=0;i<selecteds.length;i++)
				{
					ContactBean c = group_contacts.get(selecteds[i]);
					 String number = c.getNumber();
					 
					 if(!number.equals("") && !source.contains(c.getNick()+":"+number)) //号码不为空
					 {
						 phoneNumberList.add(number);
					     numberBuffer.append(c.getNick()+":"+number+",");
					 }
				}
				
				addPersonList(numberBuffer.toString());
			}
		}else{
			
			if(smsExpandableListAdapter!=null)
			{
				List<GroupInfo> arrayLists= smsExpandableListAdapter.getSelected_info();
			
				 if(arrayLists.size()>0)
				 {
				     for(GroupInfo g:arrayLists)
					 {
				    		 if( g.getPhone_number()!=null && !g.getPhone_number().equals("") && !source.contains(g.getPhone_name()+":"+g.getPhone_number()) ) //号码不为空
							 {
					    		 phoneNumberList.add(g.getPhone_number());
							     numberBuffer.append(g.getPhone_name()+":"+g.getPhone_number()+",");
							 }
					 }
				     
					 addPersonList(numberBuffer.toString());
				 }
			}
		}
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		
		case R.id.show_group_info:
			
			btn_showGroupInfo.setImageResource(R.drawable.group_up);
			
			GroupNameNumberAdapter groupNameNumberAdapter=new GroupNameNumberAdapter(NewMessageActivity.this, uNameNumber);
			
			View conViews=LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.group_list, null);
			ListView lv = (ListView) conViews.findViewById(R.id.group_list);
			lv.setAdapter(groupNameNumberAdapter);
			popupGroup = new PopupWindow(conViews, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
			popupGroup.setBackgroundDrawable(new BitmapDrawable());  //按返回键  以及点击  区域外 消失  (神奇的语句)
			popupGroup.setOutsideTouchable(true);
			popupGroup.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					btn_showGroupInfo.setImageResource(R.drawable.group_down);
				}
			});
			popupGroup.showAsDropDown(rl_message_detail_top);
			break;
			
		case R.id.message_contact_call_button:
			
			System.out.println("  message_contact_call_button --->" + phoneNumber);
			Intent intentCall = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumber));
			NewMessageActivity.this.startActivity(intentCall);
			    
		break;
            case R.id.message_copy:
            setClipboard(String.valueOf(v.getTag()));
			Toast.makeText(NewMessageActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
			if(popup_text!=null && popup_text.isShowing())
			{
				popup_text.dismiss();
			}
			
			break;
			
		case R.id.message_delete:
			
		   myDialog = new MyDialog(NewMessageActivity.this, "确定删除该条信息？", new DialogOnClickListener());
		   myDialog.normalDialog();
			
		   if(popup_text != null && popup_text.isShowing())
			{
				popup_text.dismiss();
			}
			break;
			
		case R.id.message_forward://转发
			
			boolean check = true;
			
			if(mMessage instanceof MmsContent) //转发的是彩信
			{
				MmsContent mms = (MmsContent)mMessage;
				
				if(mms.getPart_bmp() == null)
				{
					check = false;
				}
			}

			if(check)
			{
				reSend();
			}else{
				Toast.makeText(NewMessageActivity.this, "彩信图片正在下载，无法转发", Toast.LENGTH_SHORT).show();
			}
		
			
			break;
		case R.id.message_remind:
			
			if(popup_text!=null && popup_text.isShowing())
			{
				popup_text.dismiss();
			}
			
			//提醒
			String conId = "-1";
			
		    String info [] = PhoneNumberTool.getContactInfo(NewMessageActivity.this, phoneNumber);
		    String c_id = info[2];
		    if(c_id!=null)
		    {
		    	conId = c_id;
		    }
		    
			AddEditRmindLayout AddEditRmindLayout = new AddEditRmindLayout(NewMessageActivity.this, -1, Integer.valueOf(conId),null);
			AddEditRmindLayout.setRemindContent(selected_content);
			
			break;
			
		case R.id.message_favorite:
			favoriteMessage();
			Toast.makeText(NewMessageActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
			if(popup_text!=null && popup_text.isShowing())
			{
				popup_text.dismiss();
			}
			break;
			
		case R.id.btn_send_mms_sms:
			
			send();
			
			break;
			
		case R.id.Ibtn_team_singlechat_id_expression:
			
			if(ln_more.getVisibility()==View.GONE)
			{
			ln_more.setVisibility(View.VISIBLE);
			
			if(img_timing_sms.getVisibility()==View.VISIBLE)
			{
				btn_timing_sms.setImageResource(R.drawable.timing_sms_selected);
			}
			
			}else{
				ln_more.setVisibility(View.GONE);
			}
			
			//隐藏输入法
			hideKeyBoard();
			
			break;
			
		case R.id.Ibtn_message_library_show:
			ArrayList<MessageLibrary> libraries=new ArrayList<MessageLibrary>();
//			if(popup_message==null)
//			{
			    MessageLibraryClickListener messageLibraryClickListener = new MessageLibraryClickListener();
			
				View view = LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.message_library, null);
				popup_message = new Dialog(NewMessageActivity.this,R.style.theme_myDialog);
				popup_message.setContentView(view);
				popup_message.setCanceledOnTouchOutside(true);
				
				searchResult=(TextView)view.findViewById(R.id.editText1);
				messageList=(ListView)view.findViewById(R.id.message_list);
				messageConfirm=(Button)view.findViewById(R.id.message_confirm);
				messageCancel=(Button)view.findViewById(R.id.message_cancel);
				messageCancel.setOnClickListener(messageLibraryClickListener);
				messageConfirm.setOnClickListener(messageLibraryClickListener);
				if(et_content.getText().toString()!=null && !"".equals(et_content.getText().toString()))
				{
					searchResult.setText("自动搜索结果");
					Cursor cursorSearch1=myDatabaseUtil.fetchLibraryContentSecond(et_content.getText().toString());
					if(cursorSearch1!=null)
					{
						while(cursorSearch1.moveToNext())
						{
							MessageLibrary mLibrary=new MessageLibrary();
							mLibrary.setMessage_context(cursorSearch1.getString(cursorSearch1.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
							libraries.add(mLibrary);
						}
					}
					cursorSearch1.close();
				}else
				{
					holidayButton=(Button)view.findViewById(R.id.holiday);
					funnyButton=(Button)view.findViewById(R.id.funny);
					loveButton=(Button)view.findViewById(R.id.love);
					operatorButton=(Button)view.findViewById(R.id.message_operator);
					sort_message=(LinearLayout)view.findViewById(R.id.sort_message);
					sort_message.setVisibility(View.VISIBLE);
					
					holidayButton.setOnClickListener(messageLibraryClickListener);
					funnyButton.setOnClickListener(messageLibraryClickListener);
					loveButton.setOnClickListener(messageLibraryClickListener);
					operatorButton.setOnClickListener(messageLibraryClickListener);
					Cursor cursor=	myDatabaseUtil.fetchMessageLibrary("1");
					
					if(cursor!=null)
					{
						while(cursor.moveToNext())
						{
							MessageLibrary mLibrary=new MessageLibrary();
							mLibrary.setMessage_context(cursor.getString(cursor.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
							libraries.add(mLibrary);
						}
					}
				}
				
				selectMessageStatus="holiday";
				messageListAdapter=new MessageListAdapter(NewMessageActivity.this, libraries);
				messageList.setAdapter(messageListAdapter);
				messageList.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View view,
							int position, long arg3) {
						selectMessage=messageListAdapter.setCheck(view, position);
						
					}
				});
//			}
			
			popup_message.show();
			
			break;
	
			
		case R.id.message_send_again: //重发
			try {
				sendAgain();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
			
		case R.id.btn_pick_number_yes:
			setNumbers();
			dismissGroupPickLayout();
		break;
		
		case R.id.btn_pick_number_cancle:
			
			dismissGroupPickLayout();
			break;
			
        case R.id.btn_add_number: //从联系人分组中批量添加联系人
        	
        	if(rl_group.getVisibility()==View.GONE)
        	{
        		showPickDialog();
        	}
        	
        	break;
        	
        case R.id.btn_all_contact:
        	
        	contact_ln.setVisibility(View.VISIBLE);
			group_expandableListView.setVisibility(View.GONE);
			
			btn_all_contact.setBackgroundResource(R.drawable.remind_partake_select);
			btn_all_contact.setTextColor(Color.WHITE);
			
			btn_group.setBackgroundResource(R.drawable.remind_partake_normal);
			btn_group.setTextColor(NewMessageActivity.this.getResources().getColor(R.color.text_color_base));
			
			partner_type = 0;
			
        	break;
        	
        case R.id.btn_group:
        	showGroup();
        	break;
			
        case R.id.mask_top:
        	dismissGroupPickLayout();
        	break;
        	
        case R.id.btn_expression:
        	dialog_expression.show();
        	break;
        	
        case R.id.btn_timing_sms:
        	
        	if(img_timing_sms.getVisibility()==View.VISIBLE)
        	{
        		img_timing_sms.setVisibility(View.GONE);
        		btn_timing_sms.setImageResource(R.drawable.timing_sms);
        	}else{
        		setTimingSmsTime(System.currentTimeMillis()+(1000*60*10)); //第一次新建时，设置显示时间为当前时间 10分钟后
        		dialog_timing_sms.show();
        	}
        	
        	timing_sms_time = -1;
        	break;
        	
        case R.id.btn_pic_mms:
        	dialog_pic_mms.show();
        	break;
        	
        case R.id.btn_send_timing_sms_ok:
        	
        	if(ln_mms.getVisibility() ==  View.VISIBLE)
        	{
        		Toast.makeText(NewMessageActivity.this, "无法发送定时彩信", Toast.LENGTH_SHORT).show();
        	}else{
        		addTimingSms();
            	dialog_timing_sms.dismiss();
        	}
        	
        	break;
        	
        case R.id.btn_send_timing_sms_cancle:
        	dialog_timing_sms.dismiss();
        	break;
        	
        case R.id.r_item_take_photo:
        	doTakePhoto();
        	dialog_pic_mms.dismiss();
            break;
        
        case R.id.r_item_from_gallery:
        	doPickPhotoFromGallery();
        	dialog_pic_mms.dismiss();
        	break;
        	
        case R.id.btn_pic_mms_cancle: //图片彩信
        	dialog_pic_mms.dismiss();
        	break;
        	
        case R.id.btn_expression_cancle:
        	dialog_expression.dismiss();
        	break;
        	
        case R.id.img_timing_sms:
        	setTimingSmsTime(timing_sms_time);
        	dialog_timing_sms.show();
        	break;
        	
        case R.id.btn_view_mms:
        	try {
        		Intent intent = new Intent(Intent.ACTION_VIEW); 
        		intent.setDataAndType(uri_mms, "image/*"); 
        		NewMessageActivity.this.startActivity(intent); 
			} catch (Exception e) {
				e.printStackTrace();
			}
        	break;
        	
        case R.id.btn_delete_mms:
        	ln_mms.setVisibility(View.GONE);
        	img_mms.setImageResource(R.drawable.default_contact);
        	bmp_mms = null;
        	mCurrentPhotoFile = null;
            break;
        
        case R.id.btn_back:
        	if(!handleBackPressed())
    		{
        		state = 0;
    			super.onBackPressed();
    		}
        	break;
        	
		default:
			break;
		}
	}
	
	class DialogOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			case R.id.btn_top_tips_yes:
				messages.remove(mMessage);
				smsDetailChatAdapter.notifyDataSetChanged();
				
				deleteOneMessage();
				
				//mark
				if(mMessage instanceof SmsContent)
				{
					SmsContent smsContent = (SmsContent)mMessage;
					if(smsContent.getDate()>System.currentTimeMillis()) //取消定时短信
					{
						cancleTimingSms(mMessage.getId());
					}
				}
				
				Toast.makeText(NewMessageActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
				myDialog.closeDialog();
				
				if (smsDetailChatAdapter.getCount() == 0) {  //如果删除短信后，短信详情为空，直接跳转到短信列表界面
					
					onBackPressed();
				}
				
				break;

			default:
				break;
			}
			
		}
		
	}
	
	
	/**
	 * 
	 * 转发
	 * 
	 * 
	 */
	void reSend()
	{
		rl_number_input=(RelativeLayout)view.findViewById(R.id.team_singlechat_id_head);
	    rl_number_input.setVisibility(View.VISIBLE);
	    
		addSmsButton=(LinearLayout)view.findViewById(R.id.btn__layout);
		btn_add_number = (ImageButton)view.findViewById(R.id.btn_add_number);
		btn_add_number.setOnClickListener(NewMessageActivity.this);
		btn_add_number.setVisibility(View.VISIBLE);
		
		rl_group = (LinearLayout)view.findViewById(R.id.rl_group);
		
		((TextView)view.findViewById(R.id.mask_top)).setOnClickListener(NewMessageActivity.this);
		btn_all_contact = (Button) view.findViewById(R.id.btn_all_contact);
		btn_all_contact.setOnClickListener(NewMessageActivity.this);
		
		btn_group = (Button) view.findViewById(R.id.btn_group);
		btn_group.setOnClickListener(NewMessageActivity.this);
		
		
		((Button) view.findViewById(R.id.btn_pick_number_yes)).setOnClickListener(NewMessageActivity.this);
	    ((Button) view.findViewById(R.id.btn_pick_number_cancle)).setOnClickListener(NewMessageActivity.this);
		
		contact_ln = (LinearLayout) view.findViewById(R.id.contact_ln);
		
		lv_partake_contact = (ListView) view.findViewById(R.id.lv_partake_contact);
		group_expandableListView = (ExpandableListView) view.findViewById(R.id.group_expandableListView);
		
		scroller = (ScrollView)view.findViewById(R.id.scroller);
        ln_container = (LinearLayout)view.findViewById(R.id.ln_container);
        
       
        et_number_input = (EditText)view.findViewById(R.id.et_number_input);
        et_number_input.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(hasFocus)
				{
//					scroller.setBackgroundResource(R.drawable.editbox_background_focus_yellow);
				}else{
//					scroller.setBackgroundResource(R.drawable.editbox_background_normal);
				}
			}
		});
        
        et_number_input.addTextChangedListener(new MyTextWacther());
        
        ln_container.post(new Runnable() {
			@Override
			public void run() {
				input_width = scroller.getWidth();
				input_height = scroller.getHeight();
				System.out.println(" input_width ---> " + input_width + " input_height ---> " + input_height);
			}
		});
		
        ln_number_tips = (LinearLayout)view.findViewById(R.id.ln_number_tips);
        lv_number_tips = (ListView)view.findViewById(R.id.lv_number_tips);
        lv_number_tips.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				addOnePerson(String.valueOf(search.getItem(arg2)));
				et_number_input.setText("");
				et_number_input.requestFocus();
			}
		});
      
		if (contacts.size() == 0) 
		{
			new Thread(new Runnable() {

				@Override
				public void run() {
					getContast(); // 获取所有联系人
					refresh_handler.sendEmptyMessage(1);
				}
			}).start();
		}
        
       if(rl_message_detail_top!=null)
       rl_message_detail_top.setVisibility(View.GONE);
		
		
		messages.clear();
		smsDetailChatAdapter.notifyDataSetChanged();
		lv_sms.setAdapter(smsDetailChatAdapter);
		
		if(popup_text!=null && popup_text.isShowing())
		{
			popup_text.dismiss();
		}
		
		state = STATE_NEW;
		
		img_timing_sms.setVisibility(View.GONE);
		timing_sms_time = -1;
		selected_timing_id = -1;
		
		
		
		if(mMessage instanceof MmsContent) //转发的是彩信
		{
			MmsContent mms = (MmsContent)mMessage;
			ln_mms.setVisibility(View.VISIBLE);
			img_mms.setImageBitmap(mms.getPart_bmp());
			
			mCurrentPhotoFile = new File(saveBitmap2SD(mms.getPart_bmp()));

			System.out.println("  mCurrentPhotoFile ---> "+ mCurrentPhotoFile.getPath() );
			
			et_content.setText("");
			tv_mms_subject.setText("");
		}else{
			et_content.setText(selected_content);
		}
		
		mMessage = null;
	}
	
	
	/**
	 * 
	 * 隐藏选择联系人的布局
	 * 
	 */
	void dismissGroupPickLayout()
	{
		Animation dismiss = AnimationUtils.loadAnimation(NewMessageActivity.this, R.anim.dialing_out);
		dismiss.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				rl_group.setVisibility(View.GONE);
			}
		});
		
		rl_group.setAnimation(dismiss);
		
	}
	
	/**
	 * 
	 * 
	 * 
	 */
	private void showGroup() {
		
		if(aGroupInfos==null)
		{
			Toast.makeText(NewMessageActivity.this, "正在载入分组", Toast.LENGTH_SHORT).show();
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					long start = System.currentTimeMillis();
					
					aGroupInfos = getContactGroup();
					
					//查全部联系人
					String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
					
					ContentResolver contentResolver =  NewMessageActivity.this.getContentResolver();
					Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
					
					
					int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
					int photo_column = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
					int display_column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
					int has_phone_column = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
					int sort_key_column = cursor.getColumnIndex("sort_key");
					
					List<GroupInfo> all_group_infos = new ArrayList<GroupInfo>();
					
					while(cursor.moveToNext())
					{
						
						int phone_count = cursor.getInt(has_phone_column);
						String sort_key = cursor.getString(sort_key_column);
						
						
						long id = cursor.getLong(id_column);
						String name = cursor.getString(display_column);
						String photo_id = cursor.getString(photo_column);
						
						if(phone_count>0 && sort_key!=null && !sort_key.equals("")) //有号码  名字非空
						{
							
							long contact_id = cursor.getLong(id_column);
							long group_id = -1;
							
							 //查询联系人的分组信息
							 String[] groups= new String[]{GroupMembership.GROUP_ROW_ID};
							 String where=GroupMembership.CONTACT_ID+"="+ contact_id +" AND " +Data.MIMETYPE + "=" + " '" + GroupMembership.CONTENT_ITEM_TYPE+"'";
							 Cursor groupCursor=contentResolver.query(Data.CONTENT_URI, groups, where, null, null);
							   
							 if(groupCursor.moveToNext()){
							    group_id=groupCursor.getLong(groupCursor.getColumnIndex(GroupMembership.GROUP_ROW_ID));
							 }
							 groupCursor.close();
							 
							
							 Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
									ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
					
					              while (phones.moveToNext()) {
						               String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
										
						               GroupInfo groupInfo = new GroupInfo();
									   groupInfo.setPerson_id(String.valueOf(id));
									   groupInfo.setPhone_name(name);
									   groupInfo.setPhone_number(phone);
									   groupInfo.setPhoto_id(photo_id);
									   groupInfo.setGroup_id(group_id);
										 
									   all_group_infos.add(groupInfo);
					                }
					                phones.close();
						}
					}
					
					cursor.close();
	        		
					for(GroupInfo parent_g:aGroupInfos)
					{
						long g_id = parent_g.getGroup_id();
						
						ArrayList<GroupInfo> childs = new ArrayList<GroupInfo>();
						for(GroupInfo child_g:all_group_infos)
						{
							if(g_id==child_g.getGroup_id())
							{
								childs.add(child_g);
							}
						}
						childInfos.add(childs);
					}
					
					long end = System.currentTimeMillis();
					
					System.out.println(" 载入分组完成 ---> 耗时 :" + (end - start));
					
	        		handler.sendEmptyMessage(2);
				}
			}).start();
		}else{
			System.out.println( " aGroupInfos !==null");
		}
		
		btn_all_contact.setBackgroundResource(R.drawable.remind_partake_normal);
		btn_all_contact.setTextColor(NewMessageActivity.this.getResources().getColor(R.color.text_color_base));
		
		btn_group.setBackgroundResource(R.drawable.remind_partake_select);
		btn_group.setTextColor(Color.WHITE);
		
		contact_ln.setVisibility(View.GONE);
		group_expandableListView.setVisibility(View.VISIBLE);
		
		partner_type = 1;
	}

	/**
	 * 
	 * 
	 */
	private void showPickDialog() {
		
		if(!isQuerying)
	    {
	    	  Toast.makeText(NewMessageActivity.this, "正在载入联系人", Toast.LENGTH_SHORT).show();
	    	  
	 	       new Thread(new Runnable() {
	 				
	 				@Override
	 				public void run() {
	 					
	 					ContentResolver contentResolver = NewMessageActivity.this.getContentResolver();
	 					
	 					group_contacts = new ArrayList<ContactBean>() ;
						String sortOrder = "sort_key  COLLATE LOCALIZED ASC ";
						Cursor cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,sortOrder);
						
//						System.out.println(cursor.getCount() + "===========================contact=count");
						
						int id_column = cursor.getColumnIndex(ContactsContract.Contacts._ID);
						int photo_column = cursor.getColumnIndex(ContactsContract.Contacts.PHOTO_ID);
						int display_column = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
						int hsa_phone_column = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
						int sort_key_column = cursor.getColumnIndex("sort_key");
						
						while(cursor.moveToNext())
						{
							
							int phone_count = cursor.getInt(hsa_phone_column);
							
							String sort_key = cursor.getString(sort_key_column);
							
							long id = cursor.getLong(id_column);
							String name = cursor.getString(display_column);
							String photo_id = cursor.getString(photo_column);
							
							if(phone_count>0 && sort_key!=null && !sort_key.equals("")) //有号码  名字非空
							{
								Cursor phones = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER },
										ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null);
						
						              while (phones.moveToNext()) {
							               ContactBean contactBean = new ContactBean();
							               contactBean.setContact_id(id);
							               contactBean.setPhoto_id(photo_id);
							               contactBean.setNick(name);
							               String phone = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
							               contactBean.setNumber(phone);
//							               System.out.println("  phone  --->" + phone);
//							               contacts.add(contactBean);
							               
							                 boolean b = false;
											 String capPingYin = "";
											 
											 String key =sort_key.replace(" ", "");
											 for(int i = 0;i<key.length();i++){
													 char c = key.charAt(i);
													 
													 if(c>256){//汉字符号 
														 b=false;
													 }else{
														 if(!b){
															 capPingYin += c;
															 b=true;
														 }
													 }
													}
											contactBean.setName_pinyin_cap(capPingYin.replace(" ", "").toLowerCase());
											
											group_contacts.add(contactBean);
						                }
						                phones.close();
							}
						}
						cursor.close();
						
						handler.sendEmptyMessage(1);
	 				}
	 			}).start();
	 	       
	 	       isQuerying = true;
	       }
 		
 		rl_group.setVisibility(View.VISIBLE);
     	rl_group.startAnimation(AnimationUtils.loadAnimation(NewMessageActivity.this, R.anim.dialing_in));
	}

	/**
	 * 
	 * 添加单个收件人
	 * 
	 * @param value
	 */
	void addOnePerson(String value)
	{
		
			if(!numberBuffer.toString().contains(value))  //去重
			{
				String n ;
				String [] spirts = value.split(":");
				String name = spirts[0];
				String number = spirts[1];
				if(!name.equals("null"))
				{
					n = name;
				}else{
					n = number;
				}
				int old_lin = ln_container.getChildCount(); //旧行数
				Button button = (Button) LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.new_sms_person_button, null);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.leftMargin = 8;
				lp.rightMargin = 8;
				lp.topMargin = 8;
				lp.bottomMargin = 8;
			    button.setLayoutParams(lp);
				button.setText(n);
				button.setTag(value); //绑定号码
				button.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						deletePerson((String)v.getTag());
					}
				});
				
				float [] widths = new float [n.length()];
				button.getPaint().getTextWidths(n, widths);
				
				//计算按钮的长度
				float total = 0;
			    for(float f:widths)
			    {
			    	total+=f;
			    }
			    
			    button.setWidth((int)total + 2*10 + 51);
			    button.setPadding(0, 0, 8, 0);
			    button.setHeight(input_height);
			    	
				int b_width = (int)total + 2*10 + 51;    //文本长度加左右Marigin
				
//				lp.width = b_width;
//				lp.height =  input_height;
				
				//得到最下方的LinearLayout
				System.out.println(" n --->" + n +" b_width --->" + b_width);
				
				LinearLayout last_linear = (LinearLayout) ln_container.getChildAt(ln_container.getChildCount()-1);
				
				
				if(et_number_input.getWidth()>b_width  && (input_width-(input_width-et_number_input.getWidth()+b_width))>100 ) //当前行添加 
				{
					last_linear.removeView(et_number_input);
					last_linear.addView(button);
					last_linear.addView(et_number_input);
					
				}else{   //换行添加
					
					if(et_number_input.getWidth()>b_width)
					{
						last_linear.removeView(et_number_input);
						last_linear.addView(button);
						
						LinearLayout  l = new LinearLayout(NewMessageActivity.this);
						l.setGravity(Gravity.CENTER_VERTICAL);
						
						LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						lp_1.height = input_height;
						l.setLayoutParams(lp_1);
						
						l.addView(et_number_input);
						
						ln_container.addView(l);
					}else{
						
						last_linear.removeView(et_number_input);
						
						LinearLayout  l = new LinearLayout(NewMessageActivity.this);
						l.setGravity(Gravity.CENTER_VERTICAL);
						
						LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						lp_1.height = input_height;
						l.setLayoutParams(lp_1);
						
						l.addView(button);
						l.addView(et_number_input);
						
						ln_container.addView(l);
					}
					
					
					if(old_lin ==1)
					{
						 LinearLayout.LayoutParams lpp =  new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						 lpp.height = input_height*2;
						 lpp.weight=1;
						 scroller.setLayoutParams(lpp);
					}
					if(old_lin ==2)
					{
						 LinearLayout.LayoutParams lpp =  new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						 lpp.weight=1;
						 lpp.height = input_height*3;
						 scroller.setLayoutParams(lpp);
					}
					
					//超过三行向下滑动至新的那一行
					if(old_lin >=3)
					{
						scroller.post(new Runnable() {
							@Override
							public void run() {
								try {
									Thread.sleep(100);
									scroller.fullScroll(View.FOCUS_DOWN);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					}
				}
				
				numberBuffer.append(value+",");
				phoneNumberList.add(number);
			}
			
		}
	
	/**
	 * 
	 * 添加多个收件人
	 * 
	 * @param newnumberlist
	 */
	void addPersonList(String newnumberlist)
	{
		
		String [] values = newnumberlist.split(",");
		
		//将 et_number_input 从父类中移除
		LinearLayout last_l = (LinearLayout) ln_container.getChildAt(ln_container.getChildCount()-1);
		last_l.removeAllViews();
		
		ln_container.removeAllViews();
		
		LinearLayout l0 = new LinearLayout(NewMessageActivity.this);
		l0.setGravity(Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams lp_0 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		lp_0.height = input_height;
		l0.setLayoutParams(lp_0);
		l0.setOrientation(LinearLayout.HORIZONTAL);
		ln_container.addView(l0);
		
		
		int total_width = 0;
		for (String value : values) {
			
			if(value!=null && !value.equals(""))
			{
				String n;

				String[] spirts = value.split(":");
				String name = spirts[0];
				String number = spirts[1];

				if (!name.equals("null")) {
					n = name;
				} else {
					n = number;
				}
				
				Button button = (Button) LayoutInflater.from(NewMessageActivity.this).inflate(R.layout.new_sms_person_button, null);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				
				lp.leftMargin = 8;
				lp.rightMargin = 8;
				lp.topMargin = 8;
				lp.bottomMargin = 8;
				button.setLayoutParams(lp);

				button.setText(n);
				button.setTag(value); // 绑定名称与号码   
				
				button.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						deletePerson((String) v.getTag());
					}
				});

				float[] widths = new float[n.length()];
				button.getPaint().getTextWidths(n, widths);

				// 计算按钮的长度
				float total = 0;
				for (float f : widths) {
					// System.out.println(" f --->" + f);
					total += f;
				}
				button.setWidth((int) total + 2 * 10+51);
				button.setPadding(0, 0, 8, 0);
				button.setHeight(input_height);

				int b_width = (int) total + 2 * 10+51; // 文本长度加左右Marigin

				// 得到最下方的LinearLayout
				LinearLayout last_linear = (LinearLayout) ln_container .getChildAt(ln_container.getChildCount() - 1);
			
				if (input_width - total_width > b_width ) // 当前行添加
				{
					last_linear.addView(button);
					total_width+=(b_width+20);

				} else { // 换行添加

						LinearLayout l = new LinearLayout(NewMessageActivity.this);
						l.setGravity(Gravity.CENTER_VERTICAL);

						LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
						lp_1.height = input_height;
						l.setLayoutParams(lp_1);
						l.setOrientation(LinearLayout.HORIZONTAL);
						
						l.addView(button);
						ln_container.addView(l);
						
						total_width = b_width+20;
				}
			}
		}
		
		//添加EditText
		LinearLayout last_linear = (LinearLayout) ln_container .getChildAt(ln_container.getChildCount() - 1);

		System.out.println(" input_width-total_width ---> " + (input_width-total_width));
		
		if(input_width-total_width>120)
		{
			last_linear.addView(et_number_input);
		}else{
			
			LinearLayout l = new LinearLayout(NewMessageActivity.this);
			l.setGravity(Gravity.CENTER_VERTICAL);

			LinearLayout.LayoutParams lp_1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			lp_1.height = input_height;
			l.setLayoutParams(lp_1);
			l.setOrientation(LinearLayout.HORIZONTAL);
			
			l.addView(et_number_input);
			ln_container.addView(l);
		}
		
		int lin_count = ln_container.getChildCount();
		
		if(lin_count<=3)
		{
			 LinearLayout.LayoutParams lpp =  new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			 lpp.weight= 1;
			 lpp.height = input_height*lin_count;
			 scroller.setLayoutParams(lpp);
		}else{
			 LinearLayout.LayoutParams lpp =  new LinearLayout.LayoutParams (LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			 lpp.weight= 1;
			 lpp.height = input_height*3;
			 scroller.setLayoutParams(lpp);
		}
		
		 scroller.post(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(100);
						scroller.fullScroll(View.FOCUS_DOWN);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		 
		 et_number_input.setHint("");
	}
	
	
	/**
	 * 删除一个收件人号码
	 * 
	 * @param nmber
	 */
	void deletePerson(String nmber)
	{
		String numberlist = numberBuffer.toString();
		String newnumberlist = numberlist.replace(nmber+",", "");
		
		String [] ss = nmber.split(":");
		
		phoneNumberList.remove(ss[1]); //移除电话号码
		
		numberBuffer =new StringBuffer(newnumberlist);
		addPersonList(newnumberlist);
		
		if(phoneNumberList.size()==0)
		{
			et_number_input.setHint("添加收件人");
		}
    }
	
	
	class MyTextWacther implements TextWatcher{

		@Override
		public void afterTextChanged(Editable s) {
			
			final  String key = s.toString();
			if(!s.toString().equals("") && search !=null){
				
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						search.fiter(key);
					}
				}).start();
			}else if(key.equals("")) {
				ln_number_tips.setVisibility(View.GONE);
			}
			
			if(key.equals("") && phoneNumberList.size()==0)
			{
				et_number_input.setHint("添加收件人");
			}else{
				et_number_input.setHint("");
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
		}
	}
	
	
	
	public void updateAfterChangeTimingSmsTime(long id,long date)
	{
		if(smsDetailChatAdapter!=null)
		{
			try {
				smsDetailChatAdapter.refreshTimmingTime(id,date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 
	 * 获取运营商的代号
	 * 
	 * @return
	 */
	public int backOperator()
	{
		String operator = tm.getSimOperator();
       int op=-1;
		if(operator!=null){

			if(operator.equals("46000") || operator.equals("46002")|| operator.equals("46007")){
	
			//中国移动
				op=0;
			}else if(operator.equals("46001")){
	
			//中国联通
				op=1;
			}else if(operator.equals("46003")){
	
			//中国电信
				op=2;
			}
	   }
		return op;
	}
	
	
	class EmotionListItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			String name = EmotionHelper.emotionStringNames[position];
			
			Drawable d = NewMessageActivity.this.getResources().getDrawable(EmotionHelper.emotionResID[position]);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			
			ImageSpan imageSpan = new ImageSpan(d);
		    //  创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
		    SpannableString spannableString = new SpannableString(name);
		    //  用ImageSpan对象替换face
		    spannableString.setSpan(imageSpan, 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		        
			/*SpannableString spannableString = new SpannableString("["+name+"]");
	        //  用ImageSpan对象替换face
	        spannableString.setSpan(imageSpan, 0, name.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);*/
		    //  将随机获得的图像追加到EditText控件的最后
		    int index = et_content.getSelectionStart();//获取光标所在位置

		    Editable editable = et_content.getEditableText();//获取EditText的文字

		    if (index < 0 || index >= editable.length() ){
		    	editable.append(spannableString);
		    }else{
		    	editable.insert(index,spannableString);//光标所在位置插入文字
		    }
		       
//		    System.out.println(et_content.getText().toString());
		    
		    dialog_expression.dismiss();
		}
	}
	
	
	public static Object obj = new Object();
	
	/**
	 * 
	 * 刷新消息列表
	 * 
	 */
	public void refresh() {
		
		if(isSending)
		{
			return ;
		}
		
		start = System.currentTimeMillis();
		
		new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					synchronized (obj) { //同步锁
						
						messages = queryConversation();
						
						if(messages.size()>0)
						{
							refresh_handler.sendEmptyMessage(2);
						}
						
					}
				}
			}).start();
		
	}
	
	/**
	 * 
	 * 
	 * 
	 * @param number
	 */
	public void setNumber(String number){
		
		String info [] = PhoneNumberTool.getContactInfo(NewMessageActivity.this, number);
	    String na=	info [0];
	    
	    final String finalName= na+":"+number;
	    
	    
	    if(na!=null)
	    {
	    	 rl_number_input.postDelayed(new Runnable() {
	 			
	 			@Override
	 			public void run() {
	 			    addOnePerson(finalName);
	 			    
	 			    et_number_input.setHint("");  //不显示hint： 添加收件人
	 			}
	 		}, 900);
	    }else{
	    	et_number_input.setText(number);
	    }
	}
	
	
	/**
	 * 
	 * 根据photo_id 查联系人 头像
	 * 
	 * @param photo_id
	 * @return
	 */
	private Bitmap getPhoto(String photo_id)
	{
		Bitmap contactPhoto =null;
		Cursor cursor3 = NewMessageActivity.this.getContentResolver().query(ContactsContract.Data.CONTENT_URI,new String[] { "data15" },
						"ContactsContract.Data._ID=" + photo_id,
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
	
	
	class MessageLibraryClickListener implements OnClickListener{

		@Override
			// TODO Auto-generated method stub
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.message_confirm:
				
				if(selectMessageStatus.equals("operator"))//如果选择运营商的话，是新建短信
				{
					if(popup_message!=null && popup_message.isShowing())
					{
						if(et_content!=null)
						{
							
						int op=	backOperator();
						String messages[]=selectMessage.split(":");
						
						rl_number_input=(RelativeLayout)view.findViewById(R.id.team_singlechat_id_head);
					    rl_number_input.setVisibility(View.VISIBLE);
						rl_group = (LinearLayout)view.findViewById(R.id.rl_group);
						group_expandableListView = (ExpandableListView)view.findViewById(R.id.group_expandableListView);
						scroller = (ScrollView)view.findViewById(R.id.scroller);
				        ln_container = (LinearLayout)view.findViewById(R.id.ln_container);
				        et_number_input = (EditText)view.findViewById(R.id.et_number_input);
				        
				        et_number_input.addTextChangedListener(new MyTextWacther());
				        
				        ln_container.post(new Runnable() {
							@Override
							public void run() {
								input_width = scroller.getWidth();
								input_height = scroller.getHeight();
								System.out.println(" input_width ---> " + input_width + " input_height ---> " + input_height);
							}
						});
						
				        ln_number_tips = (LinearLayout)view.findViewById(R.id.ln_number_tips);
				        lv_number_tips = (ListView)view.findViewById(R.id.lv_number_tips);
				        lv_number_tips.setOnItemClickListener(new OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
									long arg3) {
								addOnePerson(String.valueOf(search.getItem(arg2)));
								et_number_input.setText("");
								et_number_input.requestFocus();
							}
						});
				        
						if(rl_message_detail_top!=null)
						{
							rl_message_detail_top.setVisibility(View.GONE);
						}
						
						state= STATE_NEW;
						
							switch (op) {
							case 0:
								 et_number_input.setText("10086");
								break;
								
	                         case 1:
	                        	  et_number_input.setText("10010");
								break;
								
	                         case 2:
	                        	  et_number_input.setText("10001");
								break;
								
							default:
								break;
							}
							et_content.setText(messages[0]);
						}
						
						messages.clear();
						smsDetailChatAdapter.notifyDataSetChanged();
						
						popup_message.dismiss();
					}
				}
				else
				{
					if(popup_message!=null && popup_message.isShowing())
					{
						if(et_content!=null)
						{
							et_content.setText(selectMessage);
						}
						popup_message.dismiss();
					}
				}
				
				break;
			case R.id.message_cancel:
				if(popup_message!=null && popup_message.isShowing())
				{
					popup_message.dismiss();
				}
				break;
				
				
			case R.id.holiday:
				
				selectMessageStatus="holiday";
				holidayButton.setBackgroundResource(R.drawable.intercept_top_selected_bg);
				
				funnyButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				loveButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				operatorButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				holidayButton.setTextColor(Color.WHITE);
				funnyButton.setTextColor(Color.BLACK);
				loveButton.setTextColor(Color.BLACK);
				operatorButton.setTextColor(Color.BLACK);
				
				Cursor cursor=	myDatabaseUtil.fetchMessageLibrary("1");
				ArrayList<MessageLibrary> libraries_holiday=new ArrayList<MessageLibrary>();
				if(cursor!=null)
				{
					while(cursor.moveToNext())
					{
						MessageLibrary mLibrary=new MessageLibrary();
						mLibrary.setMessage_context(cursor.getString(cursor.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
						libraries_holiday.add(mLibrary);
					}
				}
				if(cursor!=null)
				{
					cursor.close();
				}
				messageListAdapter=new MessageListAdapter(NewMessageActivity.this, libraries_holiday);
				messageList.setAdapter(messageListAdapter);
				break;
				
				
			case R.id.funny:
				
				selectMessageStatus="funny";
				holidayButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				funnyButton.setBackgroundResource(R.drawable.intercept_top_selected_bg);
				funnyButton.setTextColor(Color.WHITE);
				loveButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				operatorButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				Cursor cursor_funny=	myDatabaseUtil.fetchMessageLibrary("2");
				
				holidayButton.setTextColor(Color.BLACK);
				loveButton.setTextColor(Color.BLACK);
				operatorButton.setTextColor(Color.BLACK);
				ArrayList<MessageLibrary> libraries_funny=new ArrayList<MessageLibrary>();
				if(cursor_funny!=null)
				{
					while(cursor_funny.moveToNext())
					{
						MessageLibrary mLibrary=new MessageLibrary();
						mLibrary.setMessage_context(cursor_funny.getString(cursor_funny.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
						libraries_funny.add(mLibrary);
					}
				}
				if(cursor_funny!=null)
				{
					cursor_funny.close();
				}
				messageListAdapter=new MessageListAdapter(NewMessageActivity.this, libraries_funny);
				messageList.setAdapter(messageListAdapter);
				break;
				
				
			case R.id.love:
				
				holidayButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				funnyButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				loveButton.setBackgroundResource(R.drawable.intercept_top_selected_bg);
				operatorButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				
				loveButton.setTextColor(Color.WHITE);
				
				holidayButton.setTextColor(Color.BLACK);
				funnyButton.setTextColor(Color.BLACK);
				operatorButton.setTextColor(Color.BLACK);
				selectMessageStatus="love";
				Cursor cursor_love=	myDatabaseUtil.fetchMessageLibrary("3");
				ArrayList<MessageLibrary> libraries_love=new ArrayList<MessageLibrary>();
				if(cursor_love!=null)
				{
					while(cursor_love.moveToNext())
					{
						MessageLibrary mLibrary=new MessageLibrary();
						mLibrary.setMessage_context(cursor_love.getString(cursor_love.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
						libraries_love.add(mLibrary);
					}
				}
				if(cursor_love!=null)
				{
					cursor_love.close();
				}
				messageListAdapter=new MessageListAdapter(NewMessageActivity.this, libraries_love);
				messageList.setAdapter(messageListAdapter);
				break;
				
				
			case R.id.message_operator:
				
				holidayButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				funnyButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				loveButton.setBackgroundResource(R.drawable.intercept_top_normal_bg);
				operatorButton.setBackgroundResource(R.drawable.intercept_top_selected_bg);
				operatorButton.setTextColor(Color.WHITE);
				holidayButton.setTextColor(Color.BLACK);
				funnyButton.setTextColor(Color.BLACK);
				loveButton.setTextColor(Color.BLACK);
				selectMessageStatus="operator";
			    int op=	backOperator();
			    ArrayList<MessageLibrary> libraries_operator=new ArrayList<MessageLibrary>();
				if(op==0)//中国移动
				{
					Cursor cursor_operator=	myDatabaseUtil.fetchMessageLibrary("5");
					
					if(cursor_operator!=null)
					{
						while(cursor_operator.moveToNext())
						{
							MessageLibrary mLibrary=new MessageLibrary();
							mLibrary.setMessage_context(cursor_operator.getString(cursor_operator.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
							libraries_operator.add(mLibrary);
						}
					}
					if(cursor_operator!=null)
					{
						cursor_operator.close();
					}
				}else if(op==1)//中国联通
				{
					Cursor cursor_operator=	myDatabaseUtil.fetchMessageLibrary("6");
					
					if(cursor_operator!=null)
					{
						while(cursor_operator.moveToNext())
						{
							MessageLibrary mLibrary=new MessageLibrary();
							mLibrary.setMessage_context(cursor_operator.getString(cursor_operator.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
							libraries_operator.add(mLibrary);
						}
					}
					if(cursor_operator!=null)
					{
						cursor_operator.close();
					}
					
				}else if(op==2)//中国电信
				{
					Cursor cursor_operator=	myDatabaseUtil.fetchMessageLibrary("4");
					
					if(cursor_operator!=null)
					{
						while(cursor_operator.moveToNext())
						{
							MessageLibrary mLibrary=new MessageLibrary();
							mLibrary.setMessage_context(cursor_operator.getString(cursor_operator.getColumnIndexOrThrow(myDatabaseUtil.MESSAGE_CONTENT)));
							libraries_operator.add(mLibrary);
						}
					}
					if(cursor_operator!=null)
					{
						cursor_operator.close();
					}
					
				}
				
				messageListAdapter=new MessageListAdapter(NewMessageActivity.this, libraries_operator);
				messageList.setAdapter(messageListAdapter);
				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 
	 * 消息的点击事件
	 * 
	 * @author Administrator
	 *
	 */
	class MessageItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.img_timing: //点击了定时短信的那个图标
				SmsContent sms = (SmsContent) v.getTag();
				selected_timing_id = sms.getId();
				setTimingSmsTime(sms.getDate());
//				System.out.println(" selected_timing_id ---> " + selected_timing_id);
				dialog_timing_sms.show();
				break;
				
			default:
				
				Object message = v.getTag();
				
				if(message instanceof MmsContent) //============ 彩信 =================
				{
					MmsContent mms = (MmsContent)message;
					int box_type = mms.getMsg_box();
					int mms_state = mms.getSt();
					
					if(box_type==1) //收到的
					{
						switch (mms_state) {
						case 0: //已接收完成的,弹出详情对话框
							showMmsDetailDialog(mms);
							break;
							
						case 130: //失败,点击重新下载(没实现功能)
						case 135: 
							break;
							
						default:
							break;
						}
						
					}
					else if(box_type==5) //发送失败的
					{
					}
					else if(box_type==2) //发出去的
					{
						showMmsDetailDialog(mms);
					}
				}
				
				break;
			}
		}
	}
	
	/**
	 * 设置定时短信对话框显示的时间
	 * @param time
	 */
	void setTimingSmsTime(long time)
	{
		Calendar car = Calendar.getInstance();
		Date date = new Date(time);
		car.setTime(date);
		int year = car.get(Calendar.YEAR);
		int month = car.get(Calendar.MONTH);
		int day = car.get(Calendar.DATE);
		int hour = car.get(Calendar.HOUR_OF_DAY);
		int min = car.get(Calendar.MINUTE);
		
		mWheelMain.setToTime(year, month, day, hour, min);
	}
	
	
	/**
	 * 
	 * 获取一个文件名 
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	
	protected void doPickPhotoFromGallery() {
		try {
			Intent intentReadPic = new Intent(Intent.ACTION_PICK,Media.INTERNAL_CONTENT_URI);
			NewMessageActivity.this.startActivityForResult(intentReadPic,PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(NewMessageActivity.this, "未找到图片浏览应用",Toast.LENGTH_LONG).show();
		}
	}
	
	
	protected void doTakePhoto() {
		try {
			PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			NewMessageActivity.this.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(NewMessageActivity.this,"未找到拍照应用",Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * 
	 * 根据电话号码 查询会话id
	 * 
	 * @param context  
	 * @param number  电话号码
	 * 
	 * @return 会话id ;    null： 该号码不存在消息会话
	 */
	public static String queryThreadIdByNumber(Context context, String number)
	{
		String thread_id = null;
		
//		ContentResolver mContentResolver = NewMessageActivity.this.getContentResolver();
//		Cursor cursor = mContentResolver.query(Uri.parse("content://mms-sms/threadID"), null, "recipient = " + number, null, null);
//		
//		if(cursor.moveToNext())
//		{
//			thread_id = cursor.getString(cursor.getColumnIndex("_id"));
//		}
//		cursor.close();
		
		String n = PhoneNumberTool.cleanse(number);
		
		Cursor c = context.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"thread_id"}, "address =? OR address =? OR address =? OR address =? OR address =?  OR address =?  OR address =? ) group by (thread_id ", new String [] {number , n  , "+86"+n , "17951"+n, "12593"+n , "17911"+n , "17909"+n }, null);
		List<String> thread_ids = new ArrayList<String>();
		
		while(c.moveToNext())
		{
			thread_ids.add(c.getString(0));
		}
		c.close();
		
		if (thread_ids.size()>1) {
			
			for(String t_id:thread_ids)
			{
				Cursor numbers_cursor = context.getContentResolver().query(Uri.parse("content://sms/"), new String [] { "address" }, " thread_id = " + t_id +" ) group by (address ", null, null);
				
				List<String> n_list = new ArrayList<String>();
    			
    			StringBuffer sb = new StringBuffer();
    			while (numbers_cursor.moveToNext()) {
    				String nb = numbers_cursor.getString(0);
    				if(nb!=null && !nb.equals(""))
    				{
    					String new_n = PhoneNumberTool.cleanse(nb);
    					if(!n_list.contains(new_n))
    					{
    						n_list.add(new_n);
    						sb.append(new_n+",");
    					}
    				}
				}
    			
				if(n_list.size()==1)
				{
					thread_id = t_id;
					break;
				}
			}
			
		}else if(thread_ids.size()==1){
			thread_id= thread_ids.get(0);
		}
		
		if(thread_id!=null)
		{
			return thread_id;
		}else{
			return null;
			
		}
	}
	
	
	 /**
	  * 
	  * 读取图片附件
	  * 
	  * @param _id
	  * @return
	  */
	  private Bitmap getMmsImage(String _id){ 
	      Uri partURI = Uri.parse("content://mms/part/" + _id ); 
	      ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
	      InputStream is = null; 
	      Bitmap bitmap=null;
	      try { 
	          is = NewMessageActivity.this.getContentResolver().openInputStream(partURI); 
	          
	          
	          BitmapFactory.Options options = new BitmapFactory.Options(); 
//	          options.inSampleSize = 2;
	          
	          byte[] buffer = new byte[256];  
	          int len = -1;
	          while ((len = is.read(buffer)) != -1) {
	              baos.write(buffer, 0, len);
	          }
	          bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length,options);
	          
//	          int height = options.outHeight * 200 / options.outWidth; 
//	          options.outWidth = 200;
//	          options.inSampleSize = 2;
//	          options.outHeight = height;  
//	          options.inJustDecodeBounds = false; 
//	          
//	          bitmap= BitmapFactory.decodeByteArray(baos.toByteArray(), 0, baos.toByteArray().length, options);
	        
	      }catch (IOException e) { 
	          e.printStackTrace();  
//	          Log.v(TAG, "读取图片异常"+e.getMessage());
	      }finally{ 
	          if (is != null){ 
	              try { 
	                  is.close(); 
	              }catch (IOException e){
//	                  Log.v(TAG, "读取图片异常"+e.getMessage());
	              }
	          } 
	      }
	      return bitmap;
	  }
	  
	  
	  /**
	   * 
	   * 
	   * @param mms
	   * 
	   */
	  private void showMmsDetailDialog(MmsContent mms)
	  {
		    System.out.println(" 已接收完成的,弹出详情对话框  --->" + mms.getPart_pic_id());
			
			show_pic_dialog = new Dialog(NewMessageActivity.this, R.style.theme_myDialog);
			show_pic_dialog.setContentView(R.layout.dialog_show_mms);
			show_pic_dialog.setCanceledOnTouchOutside(true);
			
			show_pic_dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
				
				@Override
				public void onDismiss(DialogInterface dialog) {
					
					if(showing_bmp!=null && !showing_bmp.isRecycled())
					{
						showing_bmp.recycle();
						showing_bmp = null;
					}
				}
			});
			
			tv_mms_title = (TextView)show_pic_dialog.findViewById(R.id.tv_mms_title);
			btn_save_pic = (Button)show_pic_dialog.findViewById(R.id.btn_save_pic);
			img_show_mms_pic = (ImageView)show_pic_dialog.findViewById(R.id.img_show_mms_pic);
			show_pic_dialog.findViewById(R.id.btn_close).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					show_pic_dialog.dismiss();
				}
			});
			
			showing_bmp = getMmsImage(mms.getPart_pic_id());
			img_show_mms_pic.setImageBitmap(showing_bmp);
			
			tv_mms_title.setText(mms.getSubject());
			
			btn_save_pic.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if(SdCardUtils.checkSDState())
					{
						Toast.makeText(NewMessageActivity.this, "正在保存", Toast.LENGTH_SHORT).show();
						
						try {
							File file = new File(PHOTO_DIR, getPhotoFileName());
							
							FileOutputStream fop = new FileOutputStream(file);
							showing_bmp.compress(Bitmap.CompressFormat.JPEG, 100, fop);
							
							fop.flush();  
							fop.close();
							
							Toast.makeText(NewMessageActivity.this, "已保存在sd卡："+file.getPath(), Toast.LENGTH_SHORT).show();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						Toast.makeText(NewMessageActivity.this, "sd卡不可用", Toast.LENGTH_SHORT).show();
					}
					
//						new Thread(new Runnable() {
//							
//							@Override
//							public void run() {
//							
//							}
//						}).start();
				}
			});
			show_pic_dialog.show();
	  }

		/**
		 *  将  Bitmap 转存到 SD卡中
		 * @param bmp
		 * @return  保存成功文件路径
		 */
	public String saveBitmap2SD(Bitmap bmp){
		
		    String result = null;
		    
	      if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
	                	try {
							
	                		// 创建一个文件夹对象，赋值为外部存储器的目录
	                         File sdcardDir =Environment.getExternalStorageDirectory();
	                       //得到一个路径，内容是sdcard的文件夹路径和名字
	                         String folder_path=sdcardDir.getPath()+"/XTemp/tempImages/";//newPath在程序中要声明
	                         File folder = new File(folder_path);
	                        if (!folder.exists()) 
	                            folder.mkdirs(); //若不存在，创建目录，可以在应用启动的时候创建
	                        
	                        Date date = new Date(System.currentTimeMillis());
	                		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
	                		
	                        String path = folder_path + dateFormat.format(date)+".png";
	                        File f = new File(path);
	                        
							f.createNewFile();
	             	        
	             	        FileOutputStream fOut = new FileOutputStream(f);        
	             	        bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
	             	        
	             	        result = path;
	             	       
						} catch (Exception e) {
							e.printStackTrace();
						}
	             	        
	               }else{
	            	   Toast.makeText(NewMessageActivity.this, "SD卡不可用", Toast.LENGTH_SHORT).show();
	               }
	        
	        return result;
	    }
}

