package com.dongji.app.addressbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.AggregationExceptions;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.Website;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContactsEntity;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dongji.app.adapter.ContactDetailCallogAdapter;
import com.dongji.app.adapter.ContactDetailCollectSmsAdapter;
import com.dongji.app.adapter.ContactDetailRemindAdapter;
import com.dongji.app.adapter.ContactHistorySmsDetailAdapter;
import com.dongji.app.adapter.EncryptionContentProvider;
import com.dongji.app.adapter.EncryptionDBHepler;
import com.dongji.app.adapter.PartnerDetailAdapter;
import com.dongji.app.adapter.StrangeNumberListAdapter;
import com.dongji.app.addressbook.AddEditRmindLayout.OnFinishEditRemindListener;
import com.dongji.app.entity.ContactEditableBean;
import com.dongji.app.entity.ContactEditableItem;
import com.dongji.app.entity.EnContact;
import com.dongji.app.entity.GroupBean;
import com.dongji.app.entity.NumericWheelAdapter;
import com.dongji.app.entity.OnWheelChangedListener;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.sqllite.DButil;
import com.dongji.app.sqllite.MyDatabaseUtil;
import com.dongji.app.tool.AndroidUtils;
import com.dongji.app.tool.Constellations;
import com.dongji.app.tool.LoginTool;
import com.dongji.app.tool.PhoneNumberTool;
import com.dongji.app.ui.WheelView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

/**
 * 
 * 添加  编辑  联系人
 * 
 * @author zy_wu
 *
 */
public class AddContactActivity extends Activity implements OnClickListener {

    /////// 启动时 从Intent传过来的数据   所对应的DATA_KEY
	public static String DATA_TYPE ="type"; //类型
	public static final int TYPE_ADD_CONTACT = 0;  //添加联系人
	public static final int TYPE_DETAIL_CONTACT = 1; //联系人详情
	public static final int TYPE_ADD_NUMBER_TO_CURENT_CONTACT = 2; //添加号码到已有的联系人
	
	public static String DATA_CONTACT_ID ="contact_id"; //联系人ID
	public static String DATA_STRANGE_NUMBER ="stranger_number";//陌生号码
	public static String DATA_GO_REMIND = "go_to_remind"; //是否跳转至提醒界面
	
	
	private boolean isInsert = false; //新建联系人 还是  编辑联系人
	
	
	public String contactId;
	public long rawContactId;
	
	public View view;
	
	TextView tv_top;
	Button btn_back;
	
	
	ImageView img_add_contact_photo; //联系人头像
	Bitmap cur_photo; //当前头像
	Bitmap old_photo; //旧头像
	private long dataIdOfPhoto;
	
	//使用照相机拍摄照片作为头像时会使用到这个路径
	private static final File PHOTO_DIR = new File(Environment.getExternalStorageDirectory() + "/DCIM/Camera");
	private static final int CAMERA_WITH_DATA = 3023;
	private static final int PHOTO_PICKED_WITH_DATA = 3021;
	private static final int ICON_SIZE = 96;
	
	//照相机拍摄照片转化为该File对象
	private File mCurrentPhotoFile;
	
	//用于存放联系人头像。用于数据库的插入和更新
	private ContentValues photoContentValues = new ContentValues();

	//联系人是否有头像
	private boolean hasPhoto = false;
	
	//是否改变了联系人的头像
	private boolean hasChangedPhoto = false;
	
	String disPlayName ; //联系人姓名
	private long dataIdOfName;
	
	
	//编辑联系人的布局
	LinearLayout ln_add_content;
	Button btn_add_contact;
	Button btn_add_cancel; //取消
	
	Button btn_add_field; //添加字段
	
	EditText et_name;
	boolean isChangedName = false; 
	EditText et_company;
	boolean isChangedCompany = false;
	TextView et_birthday;
	boolean isChangedBirthday = false;
	String birthday_str = "无"; //生日信息
	long  birthdayId = -1; // 生日对应的id
	
	public  LinearLayout mobileContainer;
	public  LinearLayout homePhoneContainer;
	public  LinearLayout emailContainer;
	public  LinearLayout addressContainer;
	public  LinearLayout websitesContainer;
	public  LinearLayout noteContainer;
	
	public List<ContactEditableItem> ceiList = new ArrayList<ContactEditableItem>();
	
	Dialog popup_menu;
	Button btn_home_hone;
	Button btn_mobile;
	Button btn_email;
	Button btn_address;
	Button btn_website;
	Button btn_note;
	
	
	//联系人详情的布局
	LinearLayout ln_detail_content;
	
	LinearLayout ln_details;
	TextView tv_detail_name;
	TextView tv_detail_company;
	TextView tv_detail_birthday;
	TextView tv_detail_constellations; //星座信息
	
	ImageView img_photo;
	
	ImageView img_cr_code; //二维码图片
	
	Button btn_edit;
	Button btn_my_card;
	
	Button btn_send_vcard; //发送名片
	String send_str;//发送名片的内容
	
	List<ContactEditableBean> mobiles = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> homePhones = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> emails = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> address = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> websites = new ArrayList<ContactEditableBean>();
	List<ContactEditableBean> notes = new ArrayList<ContactEditableBean>();
	
	ContactEditableBean companyCeb = new ContactEditableBean();
	
	StrangeNumberListAdapter callogAdapter;
	
	
	////////// 分组相关
	long groupid = -1;
	
	List<GroupBean> allGroup = new ArrayList<GroupBean>();
	int old_index;
	int gourp_index ;
	int temp_group_index;
	Button btn_pick_group;
	
	TextView tv_group;
	Dialog group_picker_dialog; //选择分组
	LinearLayout ln_group_content;
	String [] groups ;
	Button btn_picgroup_yes;
	Button btn_picgroup_no;
	CheckBox group_ck;
	
	String stranger_number;
	
	String selected_number;  //被选中的号码
	Dialog call_or_sms_menu; //给XXX打电话或发短信
	Button btn_close;
	TextView tv_messge;
	Button btn_call;
	Button btn_send_sms;
	
	
	Button btn_encryption; //加密联系人
	String en_pw; //加密密码
	boolean isEncryption; //是否被加密了
	PopupWindow en_menu;
	EditText et_encryption;
	Button btn_en_ok;
	Button btn_en_cancle;

	
	String job_str =  "无"; //职业信息
	String temp_job_str ;
	boolean isChangeJob = false;
	long job_id = -1;
	CheckBox ck;
	Button btn_pick_job;
	TextView tv_job;
	Dialog job_menu;
	Button btn_picjob_yes;
	Button btn_picjob_no;
	
	CheckBox ck_custom;
	EditText et_input_job;
	
	Dialog birthday_pick_menu;//选择生日，自动匹配星座
	int bir_month = 1;
	int bir_day = 1;
	WheelView months;
	WheelView days;
	Button btn_picbirthday_yes;
	Button btn_picbirthday_no;
	
	ProgressDialog progressDialog ; 
	
	//批量删除时，底部显示的删除层，两个按钮：  确定，取消
	LinearLayout ln_delete;
    Button btn_delete_ok;
    Button btn_delete_cancle;
	
    
    //////////////联系人详情里的 四个内容模块
	Button cur_item;
	LinearLayout cur_ln;
	
	//联系人详情
	Button contact_item_detail;
	LinearLayout item_detail;
	
	//通话记录
	ImageView img; //提示icon
	
	Button contact_item_calls;
	LinearLayout item_history;
	
	Button cur_history;
	Button btn_callog_history;
	Button btn_sms_history;
	Button btn_history_muilt_delete;//批量删除
	CheckBox check_all;
	ListView lv_history;
	List<String> all_numbers = new ArrayList<String>();
	ContactDetailCallogAdapter contactDetailCallogAdapter; //历史中   通话记录Adapter
	ContactHistorySmsDetailAdapter contactHistorySmsDetailAdapter; //历史中   短信列表Adapter 
	  
	int selected_number_index = -1;
	TextView tv_history_tip;  //显示一共多少条记录
	Button btn_pick_history_number;
	int delete_positon;
	long delete_id;
	
	
	//短信收藏
	Button contact_item_collect_msg;
	LinearLayout item_collect;
	ContactDetailCollectSmsAdapter contactDetailCollectSmsAdapter;
	ListView lv_collect_msg;
	Button btn_collect_msg_delete;
	TextView tv_collect_msg_tip; //条数
	CheckBox check_collect_all;
	
	
	//事件提醒
	Button contact_item_remind;
	LinearLayout item_remind;
	ContactDetailRemindAdapter contactDetailRemindAdapter;
	Button btn_delete_reminds; 
	Button btn_add_reminds;
	ListView lv_reminds;
	CheckBox check_remind_all;
	
	TextView tv_reminds_msg_tip;//条数
	
	Dialog pop_partner; //参与人详情
	
	Button btn_muitl_mode; //被触发的多选模式的按钮
	
	List<Long> delete_date_id = new ArrayList<Long>();//所有待删除的指定数据的 date_id
	
	ProgressDialog mProgressDialog;
	
	
	Handler handler = new Handler(){
		
		public void handleMessage(android.os.Message msg) {
			
			if(progressDialog!=null && progressDialog.isShowing())
			{
				progressDialog.dismiss();
			}
			
			switch (msg.what) {

			case 1:
				contactDetailCallogAdapter = new ContactDetailCallogAdapter(AddContactActivity.this, AddContactActivity.this,all_numbers.get(selected_number_index),new OnMenuItemClickListener());
				lv_history.setAdapter(contactDetailCallogAdapter);
				tv_history_tip.setText("通话记录(共"+ contactDetailCallogAdapter.getCount() + "条)");
				
				check_all.setChecked(false);
				check_all.setVisibility(View.GONE);
				ln_delete.setVisibility(View.GONE);
				
				btn_history_muilt_delete.setVisibility(View.VISIBLE);
				break;
				
			case 2:
				
				contactHistorySmsDetailAdapter = new ContactHistorySmsDetailAdapter(AddContactActivity.this, AddContactActivity.this,getSmsInPhone(all_numbers.get(selected_number_index)));
				lv_history.setAdapter(contactHistorySmsDetailAdapter);
				tv_history_tip.setText("短信记录(共"+contactHistorySmsDetailAdapter.getCount()+"条)");
				
				lv_history.setSelection(contactHistorySmsDetailAdapter.getCount()-1);
				
				check_all.setChecked(false);
				check_all.setVisibility(View.GONE);
				ln_delete.setVisibility(View.GONE);
				
				btn_history_muilt_delete.setVisibility(View.VISIBLE);
				break;
				

			case 3:
				contactDetailCollectSmsAdapter = new ContactDetailCollectSmsAdapter(AddContactActivity.this, AddContactActivity.this, all_numbers, new CollectMsgMenuItemClickListener());
				lv_collect_msg.setAdapter(contactDetailCollectSmsAdapter);
				tv_collect_msg_tip.setText("短信收藏(共"+contactDetailCollectSmsAdapter.getCount()+"条)");
				
				
				check_collect_all.setChecked(false);
				check_collect_all.setVisibility(View.GONE);
				ln_delete.setVisibility(View.GONE);
				
				btn_collect_msg_delete.setVisibility(View.VISIBLE);
				break;
				
			case 4:
				
				contactDetailRemindAdapter = new ContactDetailRemindAdapter(AddContactActivity.this,AddContactActivity.this, contactId, new RemindMenuItemClickListener());
				lv_reminds.setAdapter(contactDetailRemindAdapter);
				tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
				
				check_remind_all.setChecked(false);
				check_remind_all.setVisibility(View.GONE);
				ln_delete.setVisibility(View.GONE);
				
				btn_delete_reminds.setVisibility(View.VISIBLE);
				btn_add_reminds.setVisibility(View.VISIBLE);
				
				break;
				
			default:
				break;
			}
		};
	};
	
	
	Handler saveHanlder = new Handler()
	{
		public void handleMessage(android.os.Message msg) {
			
			if(mProgressDialog!=null && mProgressDialog.isShowing())
			{
				mProgressDialog.dismiss();
			}
			Toast.makeText(AddContactActivity.this, "联系人已保存", Toast.LENGTH_SHORT).show();
			switchToViewMode();
		};
	};
	
	
	
	protected void onCreate(android.os.Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.add_edit_contact, null);
		setContentView(view);
		
		int type = getIntent().getIntExtra(DATA_TYPE, TYPE_ADD_CONTACT);
		
        this.contactId = getIntent().getStringExtra(DATA_CONTACT_ID);
        String stranger_number = getIntent().getStringExtra(DATA_STRANGE_NUMBER);
		boolean isGotoRemind = getIntent().getBooleanExtra(DATA_GO_REMIND, false);
		
		tv_top = (TextView) findViewById(R.id.tv_top);
		btn_back  = (Button) findViewById(R.id.btn_back); 
		btn_back.setOnClickListener(this);
		
		ln_add_content = (LinearLayout)view.findViewById(R.id.ln_add_content);
		btn_add_contact = (Button)view.findViewById(R.id.btn_add_contact);
		btn_add_contact.setOnClickListener(this);
		
		btn_add_cancel = (Button)view.findViewById(R.id.btn_add_cancel);
		btn_add_cancel.setOnClickListener(this);
		
		
		img_add_contact_photo = (ImageView)view.findViewById(R.id.img_add_contact_photo);
		img_add_contact_photo.setClickable(true);
		img_add_contact_photo.setOnClickListener(this);
		
		et_name = (EditText)view.findViewById(R.id.et_name);
		et_company = (EditText)view.findViewById(R.id.et_company);
		et_birthday = (TextView)view.findViewById(R.id.et_birthday);
		et_birthday.setOnClickListener(new BirthdayClickListener());
		
		ln_detail_content = (LinearLayout)view.findViewById(R.id.ln_detail_content);
		
		ln_details = (LinearLayout)view.findViewById(R.id.ln_details);
		
		tv_detail_name = (TextView)view.findViewById(R.id.tv_detail_name);
		tv_detail_company = (TextView)view.findViewById(R.id.tv_detail_company);
		tv_detail_birthday = (TextView)view.findViewById(R.id.tv_detail_birthday);
		tv_detail_constellations = (TextView)view.findViewById(R.id.tv_detail_constellations);
		
		img_photo = (ImageView)view.findViewById(R.id.img_photo);
		
		img_cr_code = (ImageView)view.findViewById(R.id.img_cr_code);
		
		btn_edit = (Button)view.findViewById(R.id.btn_edit);
		btn_edit.setOnClickListener(this);
		
		btn_my_card = (Button)view.findViewById(R.id.btn_my_card);
		
		btn_send_vcard = (Button)view.findViewById(R.id.btn_send_vcard);
		btn_send_vcard.setOnClickListener(this);
		
		btn_add_field = (Button)view.findViewById(R.id.btn_add_field); 
		btn_add_field.setOnClickListener(this);
		
		mobileContainer = (LinearLayout)view.findViewById(R.id.mobileContainer);
		homePhoneContainer = (LinearLayout)view.findViewById(R.id.homePhoneContainer);
		emailContainer = (LinearLayout)view.findViewById(R.id.emailContainer);
		addressContainer = (LinearLayout)view.findViewById(R.id.addressContainer);
		websitesContainer = (LinearLayout)view.findViewById(R.id.websitesContainer);
		noteContainer = (LinearLayout)view.findViewById(R.id.noteContainer);
		
		
		btn_encryption = (Button)view.findViewById(R.id.btn_encryption);
		
		tv_group = (TextView)view.findViewById(R.id.tv_group);
		
		btn_pick_group = (Button)view.findViewById(R.id.btn_pick_group);
		btn_pick_group.setOnClickListener(this);
		
		tv_job = (TextView)view.findViewById(R.id.tv_job);
		btn_pick_job =(Button)view.findViewById(R.id.btn_pick_job);
		btn_pick_job.setOnClickListener(this);
		
		
		View group_picker_view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_group_picker, null);
		group_picker_dialog = new Dialog(AddContactActivity.this,R.style.theme_myDialog);
		group_picker_dialog.setContentView(group_picker_view);
		group_picker_dialog.setCanceledOnTouchOutside(true);
		
		ln_group_content = (LinearLayout)group_picker_view.findViewById(R.id.ln_group_content);
		
		btn_picgroup_yes = (Button) group_picker_view.findViewById(R.id.btn_picgroup_yes);
		btn_picgroup_yes.setOnClickListener(this);
		btn_picgroup_no = (Button) group_picker_view.findViewById(R.id.btn_picgroup_no);
		btn_picgroup_no.setOnClickListener(this);
		
		
		ItemClickListener itemClickListener = new ItemClickListener();
		
		contact_item_detail = (Button)view.findViewById(R.id.contact_item_detail);
		contact_item_detail.setOnClickListener(itemClickListener);
		
		contact_item_calls = (Button)view.findViewById(R.id.contact_item_calls);
		contact_item_calls.setOnClickListener(itemClickListener);
		
		contact_item_collect_msg = (Button)view.findViewById(R.id.contact_item_collect_msg);
		contact_item_collect_msg.setOnClickListener(itemClickListener);
		
		contact_item_remind = (Button)view.findViewById(R.id.contact_item_remind);
		contact_item_remind.setOnClickListener(itemClickListener);
		
		//联系人详情里的 四个内容模块: 联系人详情  , 通话记录 , 短信收藏  , 事件提醒
		item_detail = (LinearLayout)view.findViewById(R.id.item_detail);
		item_history = (LinearLayout)view.findViewById(R.id.item_history);
		item_collect = (LinearLayout)view.findViewById(R.id.item_collect);
		item_remind = (LinearLayout)view.findViewById(R.id.item_remind);
		
		cur_item = contact_item_detail;
		cur_ln = item_detail;
		
		//默认显示第一个 : 联系人详情
		item_history.setVisibility(View.GONE); 
		item_collect.setVisibility(View.GONE);
		item_remind.setVisibility(View.GONE);
		
		
		ln_delete = (LinearLayout)view.findViewById(R.id.ln_delete);
	    btn_delete_ok = (Button)view.findViewById(R.id.btn_delete_ok);
	    btn_delete_cancle = (Button)view.findViewById(R.id.btn_delete_cancle);
		
		
		lv_history = (ListView)view.findViewById(R.id.lv_history);
		tv_history_tip = (TextView)view.findViewById(R.id.tv_history_tip);
		
		
		HistoryButtonClickListener historyButtonClickListener = new HistoryButtonClickListener();
		btn_callog_history = (Button)view.findViewById(R.id.btn_callog_history);
		btn_callog_history.setTag(1);
		btn_callog_history.setOnClickListener(historyButtonClickListener);
		
		img = (ImageView)view.findViewById(R.id.img);
		
		btn_sms_history = (Button)view.findViewById(R.id.btn_sms_history);
		btn_sms_history.setTag(0);
		btn_sms_history.setOnClickListener(historyButtonClickListener);
		
		cur_history = btn_callog_history; //默认显示  通话记录选项
		
		btn_pick_history_number = (Button)view.findViewById(R.id.btn_pick_history_number);
		btn_pick_history_number.setOnClickListener(historyButtonClickListener);
		
		btn_history_muilt_delete = (Button)view.findViewById(R.id.btn_history_muilt_delete);
		btn_history_muilt_delete.setOnClickListener(historyButtonClickListener);
		
		check_all = (CheckBox) view.findViewById(R.id.check_all);
		check_all.setOnCheckedChangeListener(oncheck);
		
		check_collect_all = (CheckBox) view.findViewById(R.id.check_collect_all);
		check_collect_all.setOnCheckedChangeListener(oncheck);
		
		check_remind_all = (CheckBox) view.findViewById(R.id.check_remind_all);
		check_remind_all.setOnCheckedChangeListener(oncheck);
		
		
		lv_collect_msg = (ListView)view.findViewById(R.id.lv_collect_msg);
		btn_collect_msg_delete = (Button)view.findViewById(R.id.btn_collect_msg_delete);
		btn_collect_msg_delete.setOnClickListener(new CollectMsgDeleteClickListener());
		tv_collect_msg_tip = (TextView)view.findViewById(R.id.tv_collect_msg_tip);
		
		
		RemindClickListener remindClickListener = new RemindClickListener();
		btn_delete_reminds = (Button)view.findViewById(R.id.btn_delete_reminds);
		btn_delete_reminds.setOnClickListener(remindClickListener);
		tv_reminds_msg_tip = (TextView)view.findViewById(R.id.tv_reminds_msg_tip);
		btn_add_reminds = (Button)view.findViewById(R.id.btn_add_reminds);
		btn_add_reminds.setOnClickListener(remindClickListener);
		lv_reminds = (ListView)view.findViewById(R.id.lv_reminds);
		
		
		//加载分组信息
		groupStuff();
		
		
		if(type ==TYPE_DETAIL_CONTACT ) //联系人详情
		{
			tv_top.setText("联系人详情");
			
			ln_add_content.setVisibility(View.GONE);
			ln_detail_content.setVisibility(View.VISIBLE);
			loadContactData();
			layoutDetail();
			
			
			SharedPreferences ss = AddContactActivity.this.getSharedPreferences("myNumberContactId", 0);
			long myCId = ss.getLong("myContactId", -1);
			
			//本机联系人特别处理
			if(contactId!=null && !contactId.equals("") && myCId==Long.valueOf(contactId))
			{
				btn_my_card.setVisibility(View.VISIBLE); //显示电子名片
				btn_my_card.setOnClickListener(new MyCardClickListener());
				
				contact_item_calls.setVisibility(View.GONE);
				contact_item_collect_msg.setVisibility(View.GONE);
				contact_item_remind.setVisibility(View.GONE);
			}
			
			if(isGotoRemind)
			{
				gotoRemindLayout();
			}
			
		}else if(type ==TYPE_ADD_CONTACT){ //新建联系人
			
			tv_top.setText("新建联系人");
			
			isInsert = true;
			addContact();
			
			if(stranger_number!=null)
			{
				setStrangeNumber(stranger_number);
			}
			
		}else if(type == TYPE_ADD_NUMBER_TO_CURENT_CONTACT ) {  //添加号码到已有的联系人
			this.stranger_number = stranger_number;
			addNumberToContact();
		}
		
	}

	
	/**
	 * 
	 * 加载分组信息
	 * 
	 */
	void groupStuff()
	{
		//查询分组信息
				String[] projection = new String[] { Groups._ID, Groups.TITLE };
				String selection = Groups.DELETED + "=?";
				String[] selectionArgs = new String[] { String.valueOf(0) };
				Cursor c = AddContactActivity.this.getContentResolver().query(Groups.CONTENT_URI, projection,selection, selectionArgs, null);
				while(c.moveToNext())
				{
					GroupBean gb = new GroupBean();
					gb.setGroup_id(c.getLong(c.getColumnIndex( Groups._ID)));
					
					String group_title = c.getString(c.getColumnIndex( Groups.TITLE));
					if (group_title.contains("Group:")) {
						group_title = group_title.substring(group_title.indexOf("Group:") + 6).trim();
		   	    	}
		       	  
		   		    if (group_title.contains("Favorite_")) {
		   		    	group_title = "Favorites";
		   		   }
		   		
		   		   if (group_title.contains("Starred in Android")) {
		   			group_title = "Android";
		   		  }
		   		
		   		  if(group_title.contains("My Contacts")){
		   			group_title = "Contacts";
		   			
		   		  } 
		   		   gb.setGroup_title(group_title);
				   allGroup.add(gb);
				}
				c.close();
				
				GroupBean last_no_name_gb = new GroupBean();
				last_no_name_gb.setGroup_title("未分组");
				last_no_name_gb.setGroup_id(-1);
				
				allGroup.add(last_no_name_gb);
				
				//准备分组信息
				groups = new String [allGroup.size()];
				for(int i =0;i<allGroup.size();i++)
				{
					groups[i] = allGroup.get(i).getGroup_title();
				}
				
				System.out.println("  allGroup  size  ---> " + allGroup.size());
				
				//初始化分组布局
				int temp = 0;
				LinearLayout cur_group_ln = new LinearLayout(AddContactActivity.this);
				cur_group_ln.setOrientation(LinearLayout.HORIZONTAL);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				lp.weight=1;
				cur_group_ln.setLayoutParams(lp);
				
				GroupCheckBosChangeListener groupCheckBosChangeListener = new GroupCheckBosChangeListener();
				for(int i =0; i<groups.length; i++)
				{
					System.out.println("i  --->" + i+  "  temp  --->" + temp);
					
					CheckBox ck = (CheckBox)LayoutInflater.from(AddContactActivity.this).inflate(R.layout.group_cell_ck, null);
					ck.setLayoutParams(lp);
					ck.setOnTouchListener(groupCheckBosChangeListener);
					
					if( temp==3 ){
						ln_group_content.addView(cur_group_ln);
						
						cur_group_ln = new LinearLayout(AddContactActivity.this);
						cur_group_ln.setOrientation(LinearLayout.HORIZONTAL);
						cur_group_ln.setLayoutParams(lp);
						
						ck.setText(groups[i]);
						ck.setTag(i);
						cur_group_ln.addView(ck);
						
						temp = 0;
						
					}else{
						
						if(i==groups.length-1 && (i+1)%3==1)
						{
							ln_group_content.addView(cur_group_ln);
							
							cur_group_ln = new LinearLayout(AddContactActivity.this);
							cur_group_ln.setOrientation(LinearLayout.HORIZONTAL);
							cur_group_ln.setLayoutParams(lp);
							
							ck.setText(groups[i]);
							ck.setTag(i);
							cur_group_ln.addView(ck);
						}else{
							
							ck.setText(groups[i]);
							ck.setTag(i);
							cur_group_ln.addView(ck);
							temp++;
							
						}
					}
				}
				
				ln_group_content.addView(cur_group_ln);
	}
	
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(contactDetailRemindAdapter!=null)
		{
			contactDetailRemindAdapter = new ContactDetailRemindAdapter(AddContactActivity.this,AddContactActivity.this, contactId, new RemindMenuItemClickListener());
			lv_reminds.setAdapter(contactDetailRemindAdapter);
			tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
		}
	}
	
	
	public void gotoRemindLayout()
	{
		contactDetailRemindAdapter = new ContactDetailRemindAdapter(AddContactActivity.this,AddContactActivity.this, contactId, new RemindMenuItemClickListener());
		lv_reminds.setAdapter(contactDetailRemindAdapter);
		tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
		
		cur_ln.setVisibility(View.GONE);
		item_remind.setVisibility(View.VISIBLE);
		
		cur_item.setBackgroundResource(R.drawable.intercept_top_normal_bg);
		cur_item.setTextColor(AddContactActivity.this.getResources().getColor(R.color.text_color_base));
		
		contact_item_remind.setBackgroundResource(R.drawable.intercept_top_selected_bg);
		contact_item_remind.setTextColor(Color.WHITE);
		
		cur_item = contact_item_remind;
		cur_ln = item_remind;
	}
	
	
	OnCheckedChangeListener oncheck = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			
//			System.out.println(" === onCheckedChanged ===");
			
			switch (buttonView.getId()) {
			
			case R.id.check_all:

                if(cur_history==btn_callog_history)
                {
//                	System.out.println(" ============ 进 1 " + isChecked);
                	
                	contactDetailCallogAdapter.selectALL(isChecked);
    				contactDetailCallogAdapter.notifyDataSetChanged();    
    			 	updateDeleteNum(contactDetailCallogAdapter.getSelectedItemIndexes().length);
                }else{
//                	System.out.println(" ============ 进 2 " + isChecked);
                	
                	contactHistorySmsDetailAdapter.selectALL(isChecked);
                	contactHistorySmsDetailAdapter.notifyDataSetChanged();    
    			 	updateDeleteNum(contactHistorySmsDetailAdapter.getSelectedItemIndexes().length);
                }
				
				break;
			
			case R.id.check_collect_all:
				
				contactDetailCollectSmsAdapter.selectALL(isChecked);
				contactDetailCollectSmsAdapter.notifyDataSetChanged();
				updateDeleteNum(contactDetailCollectSmsAdapter.getSelectedItemIndexes().length);
				
				break;
				
			case R.id.check_remind_all:
				
				contactDetailRemindAdapter.selectALL(isChecked);
				contactDetailRemindAdapter.notifyDataSetChanged();
				updateDeleteNum(contactDetailRemindAdapter.getSelectedItemIndexes().length);
				
				break;
				
			default:
				break;
			}
			
		}
	};
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		if (resultCode != AddContactActivity.this.RESULT_OK)
			return;

		// resultCode == RESULT_OK
		hasChangedPhoto = true;

		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: {
			final Bitmap photo = data.getParcelableExtra("data");
			
			if(cur_photo!=null)
			{
				old_photo = Bitmap.createBitmap(cur_photo);
			}
			cur_photo = photo;
			img_add_contact_photo.setImageBitmap(photo);
			hasPhoto = true;
			break;
		}

		case CAMERA_WITH_DATA: {
			doCropPhoto(mCurrentPhotoFile);
			break;
		}
	 }
		
 }
	
	
	/**
	 * 
	 * 加载联系人的数据
	 * 
	 */
	private void loadContactData(){
		
		mobiles.clear();
		homePhones.clear();
		emails.clear();
		address.clear();
		websites.clear();
		notes.clear();
		
	     ContentResolver resolver = AddContactActivity.this.getContentResolver();
	     
	     Cursor tmpCursor = resolver.query(
					RawContacts.CONTENT_URI, new String[] { RawContacts._ID },
					RawContacts.CONTACT_ID + "=?",
					new String[] { String.valueOf(contactId) }, null);
	     
			if (tmpCursor.moveToFirst())rawContactId = tmpCursor.getLong(0);
			tmpCursor.close();
			
//			System.out.println(" rawContactId --->" + rawContactId);
	     
	    //获取联系人姓名
	    String selection = RawContacts.CONTACT_ID + "=" + contactId;
	    Cursor mCursor = resolver.query(RawContactsEntity.CONTENT_URI,null, selection, null, null);
	 	int count = mCursor.getCount();
		String itemMimeType;
		mCursor.moveToFirst();
		for (int i = 0; i < count; i++) {
			itemMimeType = mCursor.getString(mCursor.getColumnIndex(RawContactsEntity.MIMETYPE));
			if (itemMimeType!=null && itemMimeType.equals(StructuredName.CONTENT_ITEM_TYPE)) {
				// 先保存在data表中_id的值
				long id = mCursor.getLong(mCursor.getColumnIndex(RawContactsEntity.DATA_ID));
				dataIdOfName = id;
				disPlayName = mCursor.getString(mCursor.getColumnIndex(StructuredName.DISPLAY_NAME));
			}
			mCursor.moveToNext();
		}
		mCursor.close();
		
		
		//获取联系人头像
		Cursor cursor = resolver.query(Contacts.CONTENT_URI,new String[] { Contacts.PHOTO_ID }, Contacts._ID + " = ? ",
				new String[] { String.valueOf(contactId) }, null);
		
		while (cursor.moveToNext()) {// 查到了数据
			// 如果没有头像photoId将被赋值为0,更新时注意判断photoId是否大于0
			long photoId = cursor.getLong(0);
			// 保存photo在data表中_id的值,更新时使用
			dataIdOfPhoto = photoId;
			if (photoId > 0) {
				String[] projection = new String[] { Photo.PHOTO };
				String photoSelection = Data._ID + " = ? ";
				String[] selectionArgs = new String[] { String.valueOf(photoId) };
				Cursor photoCursor = resolver.query(Data.CONTENT_URI, projection, photoSelection,selectionArgs, null);
				
				if (photoCursor.moveToFirst()) {// 用户设置了头像
					byte[] photo = photoCursor.getBlob(0);
					Bitmap bitmapPhoto = BitmapFactory.decodeByteArray(photo,0, photo.length);
					
					if(bitmapPhoto!=null)
					{
						img_add_contact_photo.setImageBitmap(bitmapPhoto);
						img_photo.setImageBitmap(bitmapPhoto);
						cur_photo = bitmapPhoto;
						old_photo = bitmapPhoto;
						// 我们将头像存储在自定义的一个ContentValues对象中,在更新时使用
						photoContentValues.put(Photo.PHOTO, photo);
						hasPhoto = true;
						//MyLog.i("用户设置了头像");
					}else{
						hasPhoto = false;
						img_photo.setImageResource(R.drawable.default_contact);
						img_add_contact_photo.setImageResource(R.drawable.default_contact);
					}
				} else {// 该联系人没有头像，使用默认的图片
					hasPhoto = false;
					img_photo.setImageResource(R.drawable.default_contact);
					img_add_contact_photo.setImageResource(R.drawable.default_contact);
					//MyLog.i("用户没有设置头像,使用默认图片");
				}
				photoCursor.close();
			} else {
				// 没有头像,使用默认图片
				img_photo.setImageResource(R.drawable.default_contact);
				img_add_contact_photo.setImageResource(R.drawable.default_contact);
			}
		}
		cursor.close();// 注意Cursor对象的关闭
		
//		System.out.println(" dataIdOfPhoto ---> " + dataIdOfPhoto);
//		System.out.println(" hasPhoto ---> " + hasPhoto);
		
		// 获取该联系人组织,, 只取第一个 有内容的公司
		Cursor organizations = resolver.query(
					Data.CONTENT_URI,new String[] { Data._ID, Organization.COMPANY,Organization.TITLE,Organization.JOB_DESCRIPTION },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Organization.CONTENT_ITEM_TYPE + "'",
					new String[] { contactId }, null);
				while (organizations.moveToNext()) {
					
					String company = organizations.getString(organizations.getColumnIndex(Organization.COMPANY));
					long data_id = organizations.getLong(organizations.getColumnIndex(Data._ID));
					
					String job = organizations.getString(organizations.getColumnIndex(Organization.JOB_DESCRIPTION));
					
					if(company!=null)//公司 组织名称
					{
						companyCeb.setContent(company);
						companyCeb.setData_id(data_id);
					}
					
					if(job!=null) //职业
					{
						job_str = job;
						job_id = data_id;
//						System.out.println("  job_id  ---> " + job_id);
					}
				} ;
		organizations.close();
		
		//获取联系人生日
		Cursor birthrdayCursor = resolver.query(
				Data.CONTENT_URI,new String[] { Data._ID, ContactsContract.CommonDataKinds.Event.START_DATE},
				Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE + "'",
				new String[] { contactId }, null);
		
			while (birthrdayCursor.moveToNext()) { //获取第一个生日
				
				String bir = birthrdayCursor.getString(birthrdayCursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE));
				int index = bir.indexOf("-");
				birthday_str = bir.substring(index+1);//不显示年份
				birthdayId  = birthrdayCursor.getLong(birthrdayCursor.getColumnIndex(Data._ID));
				
//				System.out.println(" birthday_str ---> " + birthday_str + " birthdayId --->" + birthdayId);
				break;
			} ;
	    birthrdayCursor.close();
	    
		
		// 获得联系人的电话号码
		Cursor phones = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
								+ " = " + contactId, null, null);
					while (phones.moveToNext()) {
						// 遍历所有的电话号码
						String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
						int phoneType = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
						long data_id = phones.getLong(phones.getColumnIndex(Data._ID));
						
						
						if(phoneType==Phone.TYPE_MOBILE) //手机
						{
							ContactEditableBean ceb = new ContactEditableBean();
							ceb.setData_id(data_id);
							ceb.setTitle("手机"+ String.valueOf(mobiles.size()+1));
//							ceb.setContent(PhoneNumberTool.cleanse(phoneNumber));
							ceb.setContent(phoneNumber);
							ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_MOBILE);
							mobiles.add(ceb);
							
						}else{ //其他均归为固话
							ContactEditableBean ceb = new ContactEditableBean();
							ceb.setData_id(data_id);
							ceb.setTitle("固话"+String.valueOf(homePhones.size()+1));
							ceb.setContent(phoneNumber);
//							ceb.setContent( PhoneNumberTool.cleanse(phoneNumber));
							ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_HOME_PHONE);
							homePhones.add(ceb);
						}
					};
		phones.close();

		
		// 获取该联系人邮箱
		Cursor emailsCur = resolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
							+ " = " + contactId, null, null);
				while (emailsCur.moveToNext()) {
					String emailValue = emailsCur.getString(emailsCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
					long data_id = emailsCur.getLong(emailsCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("邮箱"+String.valueOf(emails.size()+1));
					ceb.setContent(emailValue);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_EMAIL);
					emails.add(ceb);
					
				} ;
		emailsCur.close();
			
		
		// 获取该联系人地址  
		Cursor addressCur = resolver.query(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID
									+ " = " + contactId, null, null);
				while (addressCur.moveToNext()) {
					// 遍历所有的地址
					String street = addressCur.getString(addressCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                    long data_id = addressCur.getLong(addressCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("地址"+String.valueOf(address.size()+1));
					ceb.setContent(street);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_ADDRESS);
					address.add(ceb);
				} ;
		addressCur.close();
				
		
		//获取网址信息
		Cursor websitesCur =  resolver.query(
					Data.CONTENT_URI,new String[] { Data._ID, Website.URL },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Website.CONTENT_ITEM_TYPE + "'",new String[] { contactId }, null);
			
				while (websitesCur.moveToNext())
				{
					String website = websitesCur.getString(websitesCur.getColumnIndex(Website.URL));
					long data_id = websitesCur.getLong(websitesCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("网站"+String.valueOf(websites.size()+1));
					ceb.setContent(website);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_WEBSITE);
				    websites.add(ceb);
				}
		websitesCur.close();
			
		
		// 获取备注信息
		Cursor notesCur = resolver.query(
					Data.CONTENT_URI,new String[] { Data._ID, Note.NOTE },
					Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Note.CONTENT_ITEM_TYPE + "'",new String[] { contactId }, null);
			
			while (notesCur.moveToNext()) {
					String noteinfo = notesCur.getString(notesCur.getColumnIndex(Note.NOTE));
					
                    long data_id = notesCur.getLong(notesCur.getColumnIndex(Data._ID));
					
					ContactEditableBean ceb = new ContactEditableBean();
					ceb.setData_id(data_id);
					ceb.setTitle("备注"+String.valueOf(notes.size()+1));
					ceb.setContent(noteinfo);
					ceb.setType(ContactEditableBean.CONTACT_EDITABLE_TYPE_NOTE);
				    notes.add(ceb);
				};
	   notesCur.close();
	   
	   //分组信息
	   final Cursor dataCursor = resolver.query(Data.CONTENT_URI, null,
		         Data.RAW_CONTACT_ID + " = " + rawContactId + " AND " + Data.MIMETYPE + "='" + GroupMembership.CONTENT_ITEM_TYPE + "'", null, null);
		    
		     if (dataCursor.moveToFirst()) {
		         // 取得分组的组ID
		        	 groupid = dataCursor.getInt(dataCursor .getColumnIndex(Data.DATA1));
		     }else{
		    	 groupid = -1;
		     }
		     dataCursor.close();

//	   System.out.println(" groupid --->" + groupid);
//	   System.out.println(" contact id --->" + contactId);
//	   System.out.println(" company --->" + companyCeb.getContent());
	 
	}
	
	/**
	 * 
	 * 进入 联系人 详情的ui布局
	 * 
	 */
	private void layoutDetail()
	{
		tv_top.setText("联系人详情");
		
		ln_details.removeAllViews();
		
		tv_detail_name.setText(disPlayName);
		
		all_numbers.clear();
		
		
		final StringBuffer sf = new StringBuffer();  //VCard二维码格式  字符串
		final StringBuffer ssf = new StringBuffer(); //发送名片用到的字符串
		
		sf.append("BEGIN:VCARD"+"\n");
		sf.append("N:"+disPlayName+"\n");
		ssf.append("联系人:"+disPlayName+"\n");
		
		if(!birthday_str.equals("无"))
		{
			if(Constellations.check(birthday_str)!=null)
			{
				tv_detail_birthday.setText(birthday_str);
				tv_detail_constellations.setText("("+Constellations.check(birthday_str)+")");
				sf.append("BDAY:"+birthday_str+"\n");
				ssf.append("生日:"+birthday_str+"\n");
			}else{
				birthday_str="无";
			}
		}
		
		
		if(companyCeb.getContent()!=null && !companyCeb.getContent().equals(""))
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			tv_title.setText("公司");
			tv_content.setText(companyCeb.getContent());
			
			ln_details.addView(view);
			
			sf.append("ORG:"+companyCeb.getContent()+"\n");
			ssf.append("公司:"+companyCeb.getContent()+"\n");
		}
		
		
		//显示职业信息
		View view_job = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
		TextView tv_job = (TextView)view_job.findViewById(R.id.tv_title);
		tv_job.setText("职业");
		TextView tv_content_job = (TextView)view_job.findViewById(R.id.tv_content);
		tv_content_job.setText(job_str);
		ln_details.addView(view_job);
				
		if(!"无".equals(job_str))
		{
			sf.append("TITLE:"+job_str+"\n");
			ssf.append("职业:"+job_str+"\n");
		}
		
		for(ContactEditableBean ceb:mobiles)
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			Button btn_call = (Button) view.findViewById(R.id.btn_call);
			Button btn_send_sms = (Button) view.findViewById(R.id.btn_send_sms);
			
			btn_call.setVisibility(View.VISIBLE);
			btn_send_sms.setVisibility(View.VISIBLE);
			
			btn_call.setTag(ceb.getContent());
			btn_call.setOnClickListener(new PhoneClickListener());
			btn_send_sms.setTag(ceb.getContent());
			btn_send_sms.setOnClickListener(new PhoneClickListener());
			
			tv_title.setText(ceb.getTitle());
			tv_content.setText(ceb.getContent());
			
			all_numbers.add(ceb.getContent()); //添加进所有号码
			ln_details.addView(view);
			
			sf.append("TEL:"+ceb.getContent()+"\n");
			ssf.append("电话:"+ceb.getContent()+"\n");
		}
		
		
		for(ContactEditableBean ceb:homePhones)
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			Button btn_call = (Button) view.findViewById(R.id.btn_call);
			Button btn_send_sms = (Button) view.findViewById(R.id.btn_send_sms);
			
			btn_call.setVisibility(View.VISIBLE);
			btn_send_sms.setVisibility(View.VISIBLE);
			
			btn_call.setTag(ceb.getContent());
			btn_call.setOnClickListener(new PhoneClickListener());
			btn_send_sms.setTag(ceb.getContent());
			btn_send_sms.setOnClickListener(new PhoneClickListener());
			
			
			tv_title.setText(ceb.getTitle());
			tv_content.setText(ceb.getContent());
			
//			tv_content.setClickable(true);
//			tv_content.setTag(ceb.getContent());
//			tv_content.setOnClickListener(new PhoneClickListener());
			
			all_numbers.add(ceb.getContent()); //添加进所有号码
			ln_details.addView(view);
			
			sf.append("TEL:"+ceb.getContent()+"\n");
			ssf.append("电话:"+ceb.getContent()+"\n");
		}
		
		
		for(ContactEditableBean ceb:emails)
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			tv_title.setText(ceb.getTitle());
			tv_content.setText(ceb.getContent());
			
			ln_details.addView(view);
			
			sf.append("EMAIL:"+ceb.getContent()+"\n");
			ssf.append("邮箱:"+ceb.getContent()+"\n");
		}
		
		
		for(ContactEditableBean ceb:address)
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			tv_title.setText(ceb.getTitle());
			tv_content.setText(ceb.getContent());
			
			ln_details.addView(view);
			
			sf.append("ADR:"+ceb.getContent()+"\n");
			ssf.append("地址:"+ceb.getContent()+"\n");
		}
		
		
		for(ContactEditableBean ceb:websites)
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			tv_title.setText(ceb.getTitle());
			tv_content.setText(ceb.getContent());
			
			ln_details.addView(view);
			
			sf.append("URL:"+ceb.getContent()+"\n");
			ssf.append("网址:"+ceb.getContent()+"\n");
		}
		
		
		for(ContactEditableBean ceb:notes)
		{
			View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
			TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
			TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
			
			tv_title.setText(ceb.getTitle());
			tv_content.setText(ceb.getContent());
			
			ln_details.addView(view);
			
			sf.append("NOTE:"+ceb.getContent()+"\n");
			ssf.append("备注:"+ceb.getContent()+"\n");
		}
		
		
		//设置分组的显示信息
		View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.contact_detail_item, null);
		TextView tv_title = (TextView)view.findViewById(R.id.tv_title);
		tv_title.setText("分组");
		TextView tv_content = (TextView)view.findViewById(R.id.tv_content);
		
		TextView bottom_line = (TextView)view.findViewById(R.id.bottom_line);
		bottom_line.setVisibility(View.GONE);
		
			for(int i = 0;i<allGroup.size();i++)
			{
				GroupBean g = allGroup.get(i);
				
				if(g.getGroup_id()==groupid)
				{
					tv_content.setText(g.getGroup_title());
					gourp_index = i;
					break;
				}
				
			}
		ln_details.addView(view);
		
		
		//生成二维码图片
		sf.append("END:VCARD ");
//		System.out.println(" crcode str --->" + sf.toString());
		send_str = ssf.toString();
		
		try {
			img_cr_code.setBackgroundDrawable(new BitmapDrawable(create2DCode(0, sf.toString())));
			
			img_cr_code.setClickable(true);
			img_cr_code.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					final Dialog d = new Dialog(AddContactActivity.this, R.style.theme_myDialog);
					View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_crcode_show, null);
					d.setCanceledOnTouchOutside(true);
					d.setContentView(view);
					
					ImageView img_cr_code_big = (ImageView)view.findViewById(R.id.img_cr_code_big);
					try {
						img_cr_code_big.setBackgroundDrawable(new BitmapDrawable(create2DCode(1, sf.toString())));
					} catch (WriterException e) {
						e.printStackTrace();
					}
				
					d.setCanceledOnTouchOutside(true);
					d.show();
				}
			});
			
		} catch (WriterException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * 添加联系人
	 * 
	 */
	private void addContact(){
		
		ContactEditableItem c = new ContactEditableItem(AddContactActivity.this, this,ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE, "手机1");
		c.btn_delete.setVisibility(View.GONE); //添加联系人至少输入一个电话号码，不允许删除
		ceiList.add(c);
		mobileContainer.addView(c.view);
		
		//分组相关
		tv_group.setText(groups[groups.length-1]); //显示分组信息为 未分组
		gourp_index = groups.length-1;
		
		et_company.addTextChangedListener(new MyCompanyTextWatch());
		
		btn_encryption.setVisibility(View.GONE);
		
		btn_add_cancel.setVisibility(View.GONE);
		
		reLayoutEditMode();
		
	}
	
	
	/**
	 * 
	 * 添加陌生号码到新建联系人中
	 * 
	 * @param number
	 */
	public void setStrangeNumber(String number)
	{
		ContactEditableItem c = ceiList.get(0);
		c.getEditText().setText(number);
		c.setContentChanged(true);
	}
	
	
	/**
	 * 
	 * 添加号码到已有的联系人中
	 * 
	 */
	private void addNumberToContact(){
		loadContactData();
		
		goToEditModeLayout();
		
		ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this, this,ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE, "手机"+String.valueOf(mobileContainer.getChildCount()+1));
		cei.getEditText().setText(stranger_number);
		cei.setContentChanged(true);
		ceiList.add(cei);
		mobileContainer.addView(cei.view);
		
		reLayoutEditMode();
		checkEncryption();
		
	}
	
	
	/**
	 * 
	 * 
	 * 
	 */
	void checkEncryption() {
		
		btn_encryption.setVisibility(View.VISIBLE);
		
		SharedPreferences sf = getSharedPreferences(EncryptionActivity.SF_NAME, 0);
		
		String pw =sf.getString(EncryptionActivity.KEY_PWD, "");

		if (pw.equals("")) // 没有设置密码
		{
			btn_encryption.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Toast.makeText(AddContactActivity.this, "请先到设置页面设置加密密码",
							Toast.LENGTH_SHORT).show();
				}
			});

		} else {
			
			en_pw = pw;

//			System.out.println("  加密密码 ： ---》" + en_pw);

			Cursor c = AddContactActivity.this.getContentResolver().query(EncryptionContentProvider.URIS, null,EncryptionDBHepler.CONTACT_ID + " = '" + contactId +"'", null,null);
			if (c.getCount() > 0) {
				
				while(c.moveToNext())
				{
					int id = c.getInt(c.getColumnIndex(EncryptionDBHepler._ID));
					String cid = c.getString(c.getColumnIndex(EncryptionDBHepler.CONTACT_ID));
//					System.out.println(" id ---> " + id +"  :cid --->" + cid);
				}
				
				btn_encryption.setText("解密联系人");
				btn_encryption.setOnClickListener(new EnCryptionClickListener());
				isEncryption = true;
				
			} else {
				isEncryption = false;
				btn_encryption.setText("加密联系人");
				btn_encryption.setOnClickListener(new EnCryptionClickListener());
				
				System.out.println(" 没被加密---》");
			}

			c.close();
		}
	}
	
	
	
	/**
	 * 
	 * 
	 *  修改后  保存联系人
	 * 
	 * 
	 */
	public void saveContact()
	{
		if( check())
		{
			//显示对话框
			mProgressDialog = new ProgressDialog(AddContactActivity.this);
			mProgressDialog.setMessage("保存中");
			
			mProgressDialog.setOnKeyListener(new OnKeyListener() {  //拦截返回键
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
			mProgressDialog.show();
			
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
					 
					 //修改联系人名称
					 if(isChangedName)
					 {
						ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
						builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(dataIdOfName) });
						builder.withValue(StructuredName.FAMILY_NAME, "");
						builder.withValue(StructuredName.GIVEN_NAME, et_name.getText().toString());
						builder.withValue(StructuredName.MIDDLE_NAME, "");
//						builder.withValue(StructuredName.DISPLAY_NAME,et_name.getText().toString());
						builder.withYieldAllowed(true);
						ops.add(builder.build());
					    isChangedName = false;
					 }
					 
				     
//					 System.out.println(" isChangedCompany --->" +  isChangedCompany);
					 
				     //添加 或修改联系人 公司信息
				     if(isChangedCompany)
				     {
//				     	 System.out.println(" 添加 或修改联系人 公司信息 ");
				    	 if(companyCeb.getContent()==null)
					     {
					    	ContentProviderOperation.Builder bb = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Organization.CONTENT_ITEM_TYPE);
							bb.withValue(Data.RAW_CONTACT_ID,rawContactId);
							ContentValues cv = new ContentValues();
							cv.put(Organization.COMPANY, et_company.getText().toString());
							bb.withValues(cv);
							bb.withYieldAllowed(true);
							ops.add(bb.build());
//							System.out.println(" 添加公司 ---> ");
					     }else{
					    	ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
							b.withSelection(Data._ID + "=?",new String[] { String.valueOf(companyCeb.getData_id()) });	 // 更新时的条件
							ContentValues cv = new ContentValues();
							cv.put(Organization.COMPANY, et_company.getText().toString());
							b.withValues(cv);
							b.withYieldAllowed(true);
							ops.add(b.build());
//							System.out.println(" 更新公司 ---> ");
					     }
				    	 isChangedCompany = false;
				     }
				     
				     //添加 或修改 联系人的职业信息
				     if(isChangeJob)
				     {
					     if(job_id==-1) //新增
					     {
					    	 ContentProviderOperation.Builder bb = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Organization.CONTENT_ITEM_TYPE);
							 bb.withValue(Data.RAW_CONTACT_ID,rawContactId);
							 ContentValues cv = new ContentValues();
							 cv.put(Organization.JOB_DESCRIPTION, job_str);
							 bb.withValues(cv);
							 bb.withYieldAllowed(true);
							 ops.add(bb.build());
//							 System.out.println(" 添加职业信息 ---> " +job_id + "    :" + job_str);
					     }else{  //修改
					    	 ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
							 b.withSelection(Data._ID + "=?",new String[] { String.valueOf(job_id) });	 // 更新时的条件
							 ContentValues cv = new ContentValues();
							 cv.put(Organization.JOB_DESCRIPTION, job_str);
							 b.withValues(cv);
							 b.withYieldAllowed(true);
							 ops.add(b.build());
//							 System.out.println(" 更新职业信息  ---> " +job_id + "    :" +job_str);
					     }
					     isChangeJob = false;
				     }
				    
				     if(isChangedBirthday)
				     {
				    	 Calendar c = Calendar.getInstance();
				    	 int year =  c.get(Calendar.YEAR);
				    	 if(birthdayId==-1) //新增 
					     {
					    	 ContentProviderOperation.Builder bb = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE);
							 bb.withValue(Data.RAW_CONTACT_ID,rawContactId);
							 
							 ContentValues cv = new ContentValues();
							 cv.put(ContactsContract.CommonDataKinds.Event.START_DATE, year+"-"+birthday_str);
							 bb.withValues(cv);
							 bb.withYieldAllowed(true);
							 ops.add(bb.build());
					     }else{  //修改
					    	 ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
							 b.withSelection(Data._ID + "=?",new String[] { String.valueOf(birthdayId) });	 // 更新时的条件
							 
							 ContentValues cv = new ContentValues();
							 cv.put(ContactsContract.CommonDataKinds.Event.START_DATE, year+"-"+birthday_str);
							 b.withValues(cv);
							 b.withYieldAllowed(true);
							 ops.add(b.build());
					     }
					     isChangeJob = false;
				     }
				     
				     try {
							
							img_add_contact_photo.setDrawingCacheEnabled(false);
							if (hasChangedPhoto) {// 用户执行了更换联系人头像的操作
								//MyLog.d("改变头像...");
								// 简单一点,只要用户执行了更换头像的动作,我们就认为头像要被插入到数据库
								if (hasPhoto) {// 编辑联系人,有头像
//									if (!compare2Bitmaps(old_photo, cur_photo) && cur_photo!=null) {
									if(cur_photo!=null){
										//MyLog.w("	不同于原有的头像");
										// 如果不相同,我们就保存
										ByteArrayOutputStream out = new ByteArrayOutputStream();
										cur_photo.compress(Bitmap.CompressFormat.PNG, 100, out);
										photoContentValues.put(Photo.PHOTO, out.toByteArray());
									}
								} else {// 没有头像,我们直接保存新的头像
								//	MyLog.d("	新建头像");
									if(cur_photo!=null)
									{
										ByteArrayOutputStream out = new ByteArrayOutputStream();
										cur_photo.compress(Bitmap.CompressFormat.PNG, 100, out);
										photoContentValues.put(Photo.PHOTO, out.toByteArray());
									}
								}
								
								if (dataIdOfPhoto > 0) {// 编辑联系人时有头像,直接更新
									ContentProviderOperation.Builder builder = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
									builder.withSelection(Data._ID + "=?",new String[] { String.valueOf(dataIdOfPhoto) });
									builder.withValues(photoContentValues);
									builder.withYieldAllowed(true);
									ops.add(builder.build());
									
								} else {// 编辑联系人时,没有头像.因此需要使用插入操作
									
									ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
									builder.withValue(Data.RAW_CONTACT_ID, rawContactId);
									builder.withValue(Data.MIMETYPE,Photo.CONTENT_ITEM_TYPE);
									
									builder.withValue(Photo.IS_SUPER_PRIMARY, 1);
									builder.withValues(photoContentValues);
									builder.withYieldAllowed(true);
									ops.add(builder.build());
								}
								
							}
					} catch (Exception e) {
						e.printStackTrace();
					}
				       
				     //修改联系人分组信息 
				     if(gourp_index!=old_index)
				     {
				    	 if(gourp_index==groups.length-1 && old_index!=groups.length-1) //被选为未分组   且之前存在分组信息
				    	 {
				    		 
				    		 //开启新线程，删除原来的分组记录
				    		 new Thread(new Runnable() {
								@Override
								public void run() {
									
									Cursor rawContactIdCursor = null;
						    		 long rawContactId = -1;
						    			try {
						    				rawContactIdCursor = AddContactActivity.this.getContentResolver().query(RawContacts.CONTENT_URI,
						    						new String[] { RawContacts._ID }, RawContacts.CONTACT_ID
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
						    			
						    			Cursor aggregationCursor = AddContactActivity.this.getContentResolver().query(AggregationExceptions.CONTENT_URI,
						    					null,AggregationExceptions.RAW_CONTACT_ID1 + " = ? or "+ AggregationExceptions.RAW_CONTACT_ID2 + " = ? ",
						    					new String[] { String.valueOf(rawContactId),String.valueOf(rawContactId) }, null);
						    			int count = aggregationCursor.getCount();
						    			
						    			String where = null;
						    			String[] selectionArgs = null;
						    			
						    			if (count > 0) {// 该机录与其他记录有聚合(Aggregation)
						    				// 由于在queryForRawContactId()方法中我们是根据contactId查rawContactId,且只返回了第一个
						    				StringBuilder sb = new StringBuilder();
						    				for (int i = 0; i < count; i++) {
						    					aggregationCursor.moveToPosition(i);
						    					sb.append(aggregationCursor.getLong(aggregationCursor.getColumnIndex(AggregationExceptions.RAW_CONTACT_ID2)));
						    					sb.append(',');
						    				}
						    				sb.append(rawContactId);
						    				
						    				//
						    				where = GroupMembership.GROUP_ROW_ID + " = ? " + " AND "+ Data.MIMETYPE + " = ? " + " AND " + Data.RAW_CONTACT_ID
						    						+ " in ( " + sb.toString() + " ) ";
						    				selectionArgs = new String[] { String.valueOf(allGroup.get(old_index).getGroup_id()),GroupMembership.CONTENT_ITEM_TYPE };
						    			} else {// 该机录没有聚合(Aggregation)
						    				
						    				where = Data.RAW_CONTACT_ID + " = ? " + " AND " + Data.MIMETYPE+ " = ? " + " AND " + GroupMembership.GROUP_ROW_ID+ " = ? ";
						    				
						    				selectionArgs = new String[] { String.valueOf(rawContactId),GroupMembership.CONTENT_ITEM_TYPE, String.valueOf(allGroup.get(old_index).getGroup_id()) };
						    			}
						    			aggregationCursor.close();
						    			AddContactActivity.this.getContentResolver().delete(Data.CONTENT_URI, where, selectionArgs);
								}
							}).start();
				    		 
//				    			System.out.println(" 成功删除分组  ---> ");
				    			
				    	 }else{ //修改成了其他分组
				    		 
				    		 if(old_index!=groups.length-1)
				    		 {
				    			 //开启新线程，删除原来的分组记录
					    		 new Thread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Cursor rawContactIdCursor = null;
							    		 long rawContactId = -1;
							    			try {
							    				rawContactIdCursor = AddContactActivity.this.getContentResolver().query(RawContacts.CONTENT_URI,
							    						new String[] { RawContacts._ID }, RawContacts.CONTACT_ID
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
							    			
							    			Cursor aggregationCursor = AddContactActivity.this.getContentResolver().query(AggregationExceptions.CONTENT_URI,
							    					null,AggregationExceptions.RAW_CONTACT_ID1 + " = ? or "+ AggregationExceptions.RAW_CONTACT_ID2 + " = ? ",
							    					new String[] { String.valueOf(rawContactId),String.valueOf(rawContactId) }, null);
							    			int count = aggregationCursor.getCount();
							    			
							    			String where = null;
							    			String[] selectionArgs = null;
							    			
							    			if (count > 0) {// 该机录与其他记录有聚合(Aggregation)
							    				// 由于在queryForRawContactId()方法中我们是根据contactId查rawContactId,且只返回了第一个
							    				StringBuilder sb = new StringBuilder();
							    				for (int i = 0; i < count; i++) {
							    					aggregationCursor.moveToPosition(i);
							    					sb.append(aggregationCursor.getLong(aggregationCursor.getColumnIndex(AggregationExceptions.RAW_CONTACT_ID2)));
							    					sb.append(',');
							    				}
							    				sb.append(rawContactId);
							    				
							    				//
							    				where = GroupMembership.GROUP_ROW_ID + " = ? " + " AND "+ Data.MIMETYPE + " = ? " + " AND " + Data.RAW_CONTACT_ID
							    						+ " in ( " + sb.toString() + " ) ";
							    				selectionArgs = new String[] { String.valueOf(allGroup.get(old_index).getGroup_id()),GroupMembership.CONTENT_ITEM_TYPE };
							    			} else {// 该机录没有聚合(Aggregation)
							    				where = Data.RAW_CONTACT_ID + " = ? " + " AND " + Data.MIMETYPE+ " = ? " + " AND " + GroupMembership.GROUP_ROW_ID+ " = ? ";
							    				selectionArgs = new String[] { String.valueOf(rawContactId),GroupMembership.CONTENT_ITEM_TYPE, String.valueOf(allGroup.get(old_index).getGroup_id()) };
							    			}
							    			
							    			aggregationCursor.close();
							    			
							    			AddContactActivity.this.getContentResolver().delete(Data.CONTENT_URI, where, selectionArgs);
//							    			System.out.println(" 成功删除分组  ---> ");
									}
								}).start(); 
				    		 }
				    		 
				    		 Cursor rawContactIdCursor = null;
				    		 long rawContactId = -1;
				    			try {
				    				rawContactIdCursor = AddContactActivity.this.getContentResolver().query(RawContacts.CONTENT_URI,new String[] { RawContacts._ID }, RawContacts.CONTACT_ID
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
							values.put(GroupMembership.GROUP_ROW_ID, allGroup.get(gourp_index).getGroup_id());
							
							AddContactActivity.this.getContentResolver().insert(Data.CONTENT_URI, values);
				    	 }
				     }
				     
//					 System.out.println(" ceiList.size is ---> " + ceiList.size());
					 
					 for(ContactEditableItem c:ceiList)
					 {
						 ContentProviderOperation cpo = c.getContentProviderOperation();
						 if(cpo!=null)
						 {
							 ops.add(cpo);
						 }
					 }
					 
					 ContentResolver contentResolver =  AddContactActivity.this.getContentResolver();
					 for(Long date_id:delete_date_id)
					 {
						 contentResolver.delete(Data.CONTENT_URI,Data._ID + "=?",new String[] { String.valueOf(date_id) });
						 System.out.println("删除了  --->: " + date_id);
					 }
					 delete_date_id.clear();
					 
//					 System.out.println(" ************ops size ---> " + ops.size());
					 
					 try {
				            //一次性应用全部修改操作
						    contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
//				        	Toast.makeText(AddContactActivity.this, "联系人已保存", Toast.LENGTH_SHORT).show();
				        } catch (Exception e) {
				            e.printStackTrace();
				        } 
					 
					 saveHanlder.sendEmptyMessage(0);
				}
			}).start();
			 
		}
	}
	
	/**
	 * 
	 * 保存联系人资料前的 校验
	 * 
	 * @return
	 */
	boolean check()
	{
		if(et_name.getText().toString().equals(""))
		{
			Toast.makeText(AddContactActivity.this, "请输入联系人名称", Toast.LENGTH_SHORT).show();
			
			return false;
		}
		
		boolean isHaveOneMobile = false;
		for(ContactEditableItem c:ceiList)
		{
			if((c.getType()==ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE  && !c.getEditText().getText().toString().equals("")) ||( c.getType()==ContactEditableItem.CONTACT_EDITABLE_TYPE_HOME_PHONE && !c.getEditText().getText().toString().equals("")))
			{
				isHaveOneMobile = true;
				break;
			}
		}
		
		if(!isHaveOneMobile)
		{
			Toast.makeText(AddContactActivity.this, "请输入至少一个号码", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		
		for(ContactEditableItem c:ceiList)
		{
			if(c.getType() == ContactEditableItem.CONTACT_EDITABLE_TYPE_EMAIL)
			{
				if(!LoginTool.isEmail(c.getEditText().getText().toString()))
				{
					Toast.makeText(AddContactActivity.this, "请输入正确的邮箱", Toast.LENGTH_SHORT).show();
					return false;
				}
			}
		}
		
		return true;
	}
	
	/**
	 * 
	 * 新建联系人
	 * 
	 */
	void newContact(){
		
		if(check())
		{

			//显示对话框
			mProgressDialog = new ProgressDialog(AddContactActivity.this);
			mProgressDialog.setMessage("保存中");
			
			mProgressDialog.setOnKeyListener(new OnKeyListener() {  //拦截返回键
				
				@Override
				public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
					return true;
				}
			});
			mProgressDialog.show();

			
			//在线程进行保存操作
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					try {
						ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
						
						//插入联系人
						ContentResolver cr = AddContactActivity.this.getContentResolver();
						
						ContentValues  values=new ContentValues();
						Uri  rawcontacturi=cr.insert(RawContacts.CONTENT_URI, values);
						long  rawcontactid=ContentUris.parseId(rawcontacturi);
						
//						ops.add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
//								.withValue(RawContacts.ACCOUNT_TYPE, null)
//								.withValue(RawContacts.ACCOUNT_NAME, null).build());
						
//						int backRef =0;
						
						//联系人姓名
						ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(Data.CONTENT_URI);
//						builder.withValueBackReference(Data.RAW_CONTACT_ID, (int)backRef); 
						builder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
						builder.withValue(Data.MIMETYPE,StructuredName.CONTENT_ITEM_TYPE);
						builder.withValue(StructuredName.FAMILY_NAME, "");
						builder.withValue(StructuredName.GIVEN_NAME, et_name.getText().toString());
						builder.withValue(StructuredName.MIDDLE_NAME, "");
//						builder.withValue(StructuredName.DISPLAY_NAME,et_name.getText().toString());
						builder.withYieldAllowed(true);
						ops.add(builder.build());
						
						
						//联系人头像
						if (hasChangedPhoto) {// 用户执行了更换联系人头像的操作
		
							// 简单一点,只要用户执行了更换头像的动作,我们就认为头像要被插入到数据库
							if (hasPhoto) {// 编辑联系人,有头像
								if(cur_photo!=null){
//								if (!compare2Bitmaps(old_photo, cur_photo)) {
									//MyLog.w("	不同于原有的头像");
									// 如果不相同,我们就保存
									ByteArrayOutputStream out = new ByteArrayOutputStream();
									cur_photo.compress(Bitmap.CompressFormat.PNG, 100, out);
									photoContentValues.put(Photo.PHOTO, out.toByteArray());
								}
							} else {// 没有头像,我们直接保存新的头像
							
								ByteArrayOutputStream out = new ByteArrayOutputStream();
								cur_photo.compress(Bitmap.CompressFormat.PNG, 100, out);
								photoContentValues.put(Photo.PHOTO, out.toByteArray());
							}
						}
						
						if(hasChangedPhoto)
						{
							ContentProviderOperation.Builder b_photo = ContentProviderOperation.newInsert(Data.CONTENT_URI);
//							b_photo.withValueBackReference(Data.RAW_CONTACT_ID, backRef);
							b_photo.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
							b_photo.withValue(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
							//
							b_photo.withValue(Photo.IS_SUPER_PRIMARY, 1);
							b_photo.withValues(photoContentValues);
							b_photo.withYieldAllowed(true);
							ops.add(b_photo.build());
						}
						
//						System.out.println(" isChangedCompany --->" +  isChangedCompany);
						
						//添加 或修改联系人 公司信息
					     if(isChangedCompany && !et_company.getText().toString().equals(""))
					     {	
					    	ContentProviderOperation.Builder b_company = ContentProviderOperation.newInsert(Data.CONTENT_URI);
					    	b_company.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
							b_company.withValue(Data.MIMETYPE,  Organization.CONTENT_ITEM_TYPE);
							b_company.withValue(Organization.COMPANY,et_company.getText().toString());
							b_company.withYieldAllowed(true);
							ops.add(b_company.build());
					    	isChangedCompany = false;
					     }
						
					     //添加职业信息
					     ContentProviderOperation.Builder b_job = ContentProviderOperation.newInsert(Data.CONTENT_URI);
					     b_job.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
						 b_job.withValue(Data.MIMETYPE,  Organization.CONTENT_ITEM_TYPE);
						 b_job.withValue(Organization.JOB_DESCRIPTION,job_str);
						 b_job.withYieldAllowed(true);
						 ops.add(b_job.build());
					     
						 
						 ContentProviderOperation.Builder b = ContentProviderOperation.newUpdate(Data.CONTENT_URI);
						 b.withSelection(Data._ID + "=?",new String[] { String.valueOf(birthdayId) });	 // 更新时的条件
						 
						 //插入生日
						 if(birthday_str.contains("-"))
						 {
							 Calendar c = Calendar.getInstance();
					    	 int year =  c.get(Calendar.YEAR);
					    	 
							 ContentValues cv = new ContentValues();
							 cv.put(ContactsContract.CommonDataKinds.Event.START_DATE, year+"-"+birthday_str);
							 b.withValues(cv);
							 b.withYieldAllowed(true);
							 ops.add(b.build());
						 }
						
						 
					     //联系人分组
						 if(allGroup.get(gourp_index).getGroup_id()!= -1){
						 
					    	ContentValues vv = new ContentValues();
							vv.put(Data.RAW_CONTACT_ID,rawcontactid);
							vv.put(Data.MIMETYPE, GroupMembership.CONTENT_ITEM_TYPE);
							vv.put(GroupMembership.GROUP_ROW_ID, allGroup.get(gourp_index).getGroup_id());
							AddContactActivity.this.getContentResolver().insert(Data.CONTENT_URI, vv);
							
					     }
					     
						 
					     for(ContactEditableItem c:ceiList)
						 {
					    	
							 if(c.getContentProviderOperation()!=null)
							 {
								 ContentProviderOperation.Builder contentbuilder = null;
						    	 ContentValues cv= null;
								 switch (c.getType()) {
									case ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE:
											contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
											contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
											cv = new ContentValues();
									 		cv.put(Phone.TYPE, Phone.TYPE_MOBILE);
									 		cv.put(Phone.NUMBER, c.getEditText().getText().toString());
											contentbuilder.withValues(cv);
											contentbuilder.withYieldAllowed(true);
											contentbuilder.build();
										break;

										
									case ContactEditableItem.CONTACT_EDITABLE_TYPE_HOME_PHONE:

											contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
											contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
											cv = new ContentValues();
										 	cv.put(Phone.TYPE, Phone.TYPE_HOME);
										 	cv.put(Phone.NUMBER, c.getEditText().getText().toString());
											contentbuilder.withValues(cv);
											contentbuilder.withYieldAllowed(true);
											contentbuilder.build();
										break;

										
									case ContactEditableItem.CONTACT_EDITABLE_TYPE_EMAIL: 
											contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
											contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
											cv = new ContentValues();
									 		cv.put(Email.DATA,c.getEditText().getText().toString());
									 		cv.put(Email.TYPE,Email.TYPE_WORK);
											contentbuilder.withValues(cv);
											contentbuilder.withYieldAllowed(true);
										break;

										
									case ContactEditableItem.CONTACT_EDITABLE_TYPE_ADDRESS:
											contentbuilder = ContentProviderOperation
													.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
											contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
											cv = new ContentValues();
									 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET,c.getEditText().getText().toString());
									 		cv.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);
											contentbuilder.withValues(cv);
											contentbuilder.withYieldAllowed(true);
										break;

										
									case ContactEditableItem.CONTACT_EDITABLE_TYPE_WEBSITE:
											contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Website.CONTENT_ITEM_TYPE);
											contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
											cv = new ContentValues();
											cv.put(Website.URL, c.getEditText().getText().toString());
											cv.put(Website.TYPE, Website.TYPE_WORK);
											contentbuilder.withValues(cv);
											contentbuilder.withYieldAllowed(true);
										break;
										
										
									case ContactEditableItem.CONTACT_EDITABLE_TYPE_NOTE:
											contentbuilder = ContentProviderOperation.newInsert(Data.CONTENT_URI).withValue(Data.MIMETYPE,Note.CONTENT_ITEM_TYPE);
											contentbuilder.withValue(Data.RAW_CONTACT_ID, rawcontactid); 
											cv = new ContentValues();
											cv.put(Note.NOTE, c.getEditText().getText().toString());
											contentbuilder.withValues(cv);
											contentbuilder.withYieldAllowed(true);
										break;

									default:
										break;
									}
								 ops.add(contentbuilder.build());
							 }
						 }
						 
					     
						 try {
					            //一次性应用全部修改操作
					            AddContactActivity.this.getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//					        	Toast.makeText(AddContactActivity.this, "联系人已保存", Toast.LENGTH_SHORT).show();
					        } catch (Exception e) {
					            e.printStackTrace();
					        } 
					
						 Cursor contactIdCursor = null;
						 
						try {
							contactIdCursor = cr.query(RawContacts.CONTENT_URI,new String[] { RawContacts.CONTACT_ID },RawContacts._ID + "=" + rawcontactid, null, null);
							if (contactIdCursor != null&& contactIdCursor.moveToFirst()) {
								contactId = String.valueOf(contactIdCursor.getLong(0));
							}
						} finally {
							if (contactIdCursor != null) {
								contactIdCursor.close();
							}
						}
//							System.out.println(" contactId --->" + contactId);
						
						isInsert = false; //设置标示 
					    ceiList.clear(); //清空
					    
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					saveHanlder.sendEmptyMessage(0);
				}
			}).start();
		}
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_add_contact:
			if(isInsert)  //新建联系人
			{
				newContact();
			}else{
				saveContact();  //编辑联系人
			}
			
			break;
			
		case R.id.btn_add_cancel: //取消编辑
			switchToViewMode();
			break;
			
		case R.id.btn_edit:
			switchToEditMode();
			break;

			
		case R.id.btn_add_field:  //添加字段
			if(popup_menu==null)
			{
				View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_contact_detail, null);
				popup_menu = new Dialog(AddContactActivity.this,R.style.theme_myDialog);
				popup_menu.setContentView(view);
				popup_menu.setCanceledOnTouchOutside(true);
				
				MenuClickListener menuClickListener = new MenuClickListener();
				
				btn_home_hone = (Button)view.findViewById(R.id.btn_home_hone);
				btn_home_hone.setOnClickListener(menuClickListener);
				btn_mobile= (Button)view.findViewById(R.id.btn_mobile);
				btn_mobile.setOnClickListener(menuClickListener);
				btn_email= (Button)view.findViewById(R.id.btn_email);
				btn_email.setOnClickListener(menuClickListener);
				btn_address= (Button)view.findViewById(R.id.btn_address);
				btn_address.setOnClickListener(menuClickListener);
				btn_website= (Button)view.findViewById(R.id.btn_website);
				btn_website.setOnClickListener(menuClickListener);
				btn_note= (Button)view.findViewById(R.id.btn_note);
				btn_note.setOnClickListener(menuClickListener);
			}

			popup_menu.show();
			
			break;
			
		case R.id.btn_pick_group: //编辑分组
			
			//选定
			LinearLayout ln_group_content = (LinearLayout)group_picker_dialog.findViewById(R.id.ln_group_content);
			
			for(int i = 0 ; i<ln_group_content.getChildCount() ;i++)
			{
				LinearLayout p_v = (LinearLayout)ln_group_content.getChildAt(i);
				
				
				for(int j = 0 ; j<p_v.getChildCount() ; j++)
				{
					CheckBox c = (CheckBox)p_v.getChildAt(j);
					long g_id = allGroup.get(((Integer)c.getTag())).getGroup_id();
					
					if( g_id ==  groupid)
					{
						c.setChecked(true);
						group_ck = c;
						gourp_index = (Integer)c.getTag();
						
					}else{
						c.setChecked(false);
					}
				}
			}
			
			group_picker_dialog.show();
			
			break;
			
		case R.id.btn_picgroup_yes:
			gourp_index =  temp_group_index ;
			tv_group.setText(groups[gourp_index]);
	
			group_picker_dialog.dismiss();
			break;
			
        case R.id.btn_picgroup_no:
        	
        	temp_group_index = gourp_index;
    		
        	group_picker_dialog.dismiss();
			break;
			
		case R.id.img_add_contact_photo:
			createPickPhotoDialog();
			break;
			
		case R.id.btn_pick_job:
			if(job_menu==null)
			{
				View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_jobs, null);
				job_menu = new Dialog(AddContactActivity.this,R.style.theme_myDialog);
				job_menu.setContentView(view);
				job_menu.setCanceledOnTouchOutside(true);
//				job_menu.setBackgroundDrawable(new BitmapDrawable());  //按返回键  以及点击  区域外 消失  (神奇的语句)
//				job_menu.setOutsideTouchable(true);
				
				btn_picjob_yes = (Button)view.findViewById(R.id.btn_picjob_yes);
				btn_picjob_yes.setOnClickListener(this);
				btn_picjob_no = (Button)view.findViewById(R.id.btn_picjob_no);
				btn_picjob_no.setOnClickListener(this);
				
				LinearLayout ln_container = (LinearLayout)view.findViewById(R.id.container);
				
				JobCheckBosChangeListener jl = new JobCheckBosChangeListener();
				
				for(int i = 0 ; i<ln_container.getChildCount() ;i++)
				{
					LinearLayout p_v = (LinearLayout)ln_container.getChildAt(i);
					
					for(int j = 0 ; j<p_v.getChildCount() ; j++)
					{
						CheckBox c = (CheckBox)p_v.getChildAt(j);
						c.setOnTouchListener(jl);
					}
				}
				
				ck_custom = (CheckBox)view.findViewById(R.id.ck_custom);
				ck_custom.setOnTouchListener(jl);
				
				et_input_job = (EditText)view.findViewById(R.id.et_input_job);
			}
			
			//选定
			LinearLayout ln_container = (LinearLayout)job_menu.findViewById(R.id.container);
			
			boolean isMatch = false;
			for(int i = 0 ; i<ln_container.getChildCount() ;i++)
			{
				LinearLayout p_v = (LinearLayout)ln_container.getChildAt(i);
				
				for(int j = 0 ; j<p_v.getChildCount() ; j++)
				{
					CheckBox c = (CheckBox)p_v.getChildAt(j);
					
					if(c.getText().toString().equals(job_str))
					{
						c.setChecked(true);
						ck = c;
						isMatch = true;
					}else{
						c.setChecked(false);
					}
				}
			}
			
			if(!isMatch)  //其他职业信息
			{
				et_input_job.setText(job_str);
				ck_custom.setChecked(true);
				ck = ck_custom;
			}else{
				ck_custom.setChecked(false);
			}
			
			job_menu.show();
			
			break;
			
		case R.id.btn_picjob_yes:
			
			if(ck_custom.isChecked())
			{
				if(!et_input_job.getText().toString().equals(""))
				{
					tv_job.setText(et_input_job.getText().toString());
					job_str = et_input_job.getText().toString();
					isChangeJob = true;
				}else{
					tv_job.setText("无");
					job_str = "无" ;
					isChangeJob = true;
				}
			}else{
				job_str = temp_job_str;
				tv_job.setText(job_str);
				isChangeJob = true;
			}
			
			job_menu.dismiss();
			
			break;
			
		case R.id.btn_picjob_no:
			job_menu.dismiss();
			break;	
			
		case R.id.btn_send_vcard: //发送联系人名片
			
			smsForward(send_str);
			
			break;
			
		case R.id.btn_back: //返回
			finish();
			break;
			
		default:
			break;
		}
	}
	
	
	/**
	 * 
	 * 切换至 浏览模式(联系人详情)
	 * 
	 */
	public void switchToViewMode(){
		
		loadContactData();
		layoutDetail();
		
		//显示第一个选项卡
		cur_ln.setVisibility(View.GONE);
		item_detail.setVisibility(View.VISIBLE);
		
		cur_item.setBackgroundResource(R.drawable.intercept_top_normal_bg);
		cur_item.setTextColor(AddContactActivity.this.getResources().getColor(R.color.text_color_base));
		
		contact_item_detail.setBackgroundResource(R.drawable.intercept_top_selected_bg);
		contact_item_detail.setTextColor(Color.WHITE);
		
		cur_item = contact_item_detail;
		cur_ln = item_detail;
		
		//切换动画
		Animation a = AnimationUtils.loadAnimation(AddContactActivity.this, R.anim.fade_out);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				ln_add_content.setVisibility(View.GONE);
				
				ln_detail_content.setVisibility(View.VISIBLE);
				
				Animation aa = AnimationUtils.loadAnimation(AddContactActivity.this, R.anim.fade_in);
				ln_detail_content.startAnimation(aa);
			}
		});
		
		ln_add_content.startAnimation(a);
		
	}
	
	
	/**
	 * 
	 *  切换至  编辑模式 (编辑联系人)
	 *  
	 */
	public void switchToEditMode(){
		
		goToEditModeLayout();
		
		//切换动画
		Animation a = AnimationUtils.loadAnimation(AddContactActivity.this, R.anim.fade_out);
		a.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				ln_detail_content.setVisibility(View.GONE);
				
				ln_add_content.setVisibility(View.VISIBLE);
				
				Animation aa = AnimationUtils.loadAnimation(AddContactActivity.this, R.anim.fade_in);
				
				ln_add_content.startAnimation(aa);
			}
		});
		
		ln_detail_content.startAnimation(a);
		
	}
	
	
	/**
	 * 
	 * 进入编辑联系人的 ui布局
	 * 
	 */
	private void goToEditModeLayout()
	{
		tv_top.setText("编辑联系人");
		
		mobileContainer.removeAllViews();
		homePhoneContainer.removeAllViews();
		emailContainer.removeAllViews();
		addressContainer.removeAllViews();
		websitesContainer.removeAllViews();
		noteContainer.removeAllViews();
		
		ceiList.clear();
		
		et_name.setText(disPlayName);
		et_company.setText(companyCeb.getContent());
		et_name.addTextChangedListener(new MyNameTextWatch());
		et_company.addTextChangedListener(new MyCompanyTextWatch());
		
		for(ContactEditableBean ceb:mobiles)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this,this,ceb);
			ceiList.add(cei);
			mobileContainer.addView(cei.view);
		}
		
		for(ContactEditableBean ceb:homePhones)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this,this,ceb);
			ceiList.add(cei);
			homePhoneContainer.addView(cei.view);
		}
		
		for(ContactEditableBean ceb:emails)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this,this,ceb);
			ceiList.add(cei);
			emailContainer.addView(cei.view);
		}
		
		for(ContactEditableBean ceb:address)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this,this,ceb);
			ceiList.add(cei);
			addressContainer.addView(cei.view);
		}
		
		for(ContactEditableBean ceb:websites)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this,this,ceb);
			ceiList.add(cei);
			websitesContainer.addView(cei.view);
		}
		
		for(ContactEditableBean ceb:notes)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this,this,ceb);
			ceiList.add(cei);
			noteContainer.addView(cei.view);
		}
		
		tv_job.setText(job_str);
		
		if(!birthday_str.equals("无"))
		{
			
			et_birthday.setText(birthday_str+"("+Constellations.check(birthday_str)+")");
			String [] ss = birthday_str.split("-");
			bir_month = Integer.valueOf(ss[0]);
			bir_day = Integer.valueOf(ss[1]);
		}else{
			et_birthday.setText("设置生日");
		}
		
		
		//分组相关
		if(groupid ==-1)
		{
			old_index = groups.length-1;
			gourp_index = groups.length-1;
			tv_group.setText("未分组");
		}else{
			for(int i = 0;i<allGroup.size();i++)
			{
				GroupBean g = allGroup.get(i);
				if(g.getGroup_id()==groupid)
				{
					old_index = i;
					gourp_index = i;
					tv_group.setText(groups[i]);
					break;
				}
			}
		}
		
		
		if(mobiles.size()==0 && homePhones.size()==0)
		{
			ContactEditableItem cei = new ContactEditableItem(AddContactActivity.this, this,ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE, "手机"+String.valueOf(mobileContainer.getChildCount()+1));
			cei.getEditText().setText("");
			cei.setContentChanged(false);
			ceiList.add(cei);
			mobileContainer.addView(cei.view);
		}
		
		reLayoutEditMode();
		checkEncryption();
		
	}
	
	
	/**
	 * 
	 * 重新选渲染联系人资料选择的 背景
	 * 
	 */
	public void reLayoutEditMode()
	{
		boolean isHaveFirst = false;
		
		for(int i = 0;i<mobileContainer.getChildCount();i++)
		{
			View v  = mobileContainer.getChildAt(i);
			if( i==0 &&  !isHaveFirst  )
			{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_top_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_top_right);
				isHaveFirst = true;
			}else{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_middle_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_middle_right);
			}
		}
		
		for(int i = 0;i<homePhoneContainer.getChildCount();i++)
		{
			View v  = homePhoneContainer.getChildAt(i);
			if( i==0 &&  !isHaveFirst  )
			{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_top_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_top_right);
				isHaveFirst = true;
			}else{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_middle_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_middle_right);
			}
		}
		
		for(int i = 0;i<emailContainer.getChildCount();i++)
		{
			View v  = emailContainer.getChildAt(i);
			if( i==0 &&  !isHaveFirst  )
			{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_top_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_top_right);
				isHaveFirst = true;
			}else{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_middle_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_middle_right);
			}
		}
		
		for(int i = 0;i<addressContainer.getChildCount();i++)
		{
			View v  = addressContainer.getChildAt(i);
			if( i==0 &&  !isHaveFirst  )
			{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_top_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_top_right);
				isHaveFirst = true;
			}else{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_middle_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_middle_right);
			}
		}
		
		for(int i = 0;i<websitesContainer.getChildCount();i++)
		{
			View v  = websitesContainer.getChildAt(i);
			if( i==0 &&  !isHaveFirst  )
			{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_top_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_top_right);
				isHaveFirst = true;
			}else{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_middle_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_middle_right);
			}
		}
		
		for(int i = 0;i<noteContainer.getChildCount();i++)
		{
			View v  = noteContainer.getChildAt(i);
			if( i==0 &&  !isHaveFirst  )
			{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_top_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_top_right);
				isHaveFirst = true;
			}else{
				((TextView)v.findViewById(R.id.tv)).setBackgroundResource(R.drawable.contact_detail_edit_middle_left);
				((LinearLayout)v.findViewById(R.id.ln)).setBackgroundResource(R.drawable.contact_detail_edit_middle_right);
			}
		}
		
		
	}
	
	
	/**
	 * 
	 * 添加字段选项监听
	 * 
	 * @author Administrator
	 *
	 */
	class MenuClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			ContactEditableItem cei;
			switch (v.getId()) {
			case R.id.btn_home_hone:
				cei= new ContactEditableItem(AddContactActivity.this, AddContactActivity.this,ContactEditableItem.CONTACT_EDITABLE_TYPE_HOME_PHONE, "固话"+String.valueOf(homePhoneContainer.getChildCount()+1));
				ceiList.add(cei);
				homePhoneContainer.addView(cei.view);
				break;

			case R.id.btn_mobile:
				cei = new ContactEditableItem(AddContactActivity.this, AddContactActivity.this,ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE, "手机"+String.valueOf(mobileContainer.getChildCount()+1));
				ceiList.add(cei);
				mobileContainer.addView(cei.view);
				break;
				
			case R.id.btn_email:
				cei= new ContactEditableItem(AddContactActivity.this, AddContactActivity.this,ContactEditableItem.CONTACT_EDITABLE_TYPE_EMAIL, "邮箱"+String.valueOf(emailContainer.getChildCount()+1));
				ceiList.add(cei);
				emailContainer.addView(cei.view);
				break;
				
			case R.id.btn_address:
				cei= new ContactEditableItem(AddContactActivity.this,AddContactActivity.this, ContactEditableItem.CONTACT_EDITABLE_TYPE_ADDRESS, "地址"+String.valueOf(addressContainer.getChildCount()+1));
				ceiList.add(cei);
				addressContainer.addView(cei.view);
				break;
				
			case R.id.btn_website:
				cei= new ContactEditableItem(AddContactActivity.this, AddContactActivity.this,ContactEditableItem.CONTACT_EDITABLE_TYPE_WEBSITE, "网站"+String.valueOf(websitesContainer.getChildCount()+1));
				ceiList.add(cei);
				websitesContainer.addView(cei.view);
				break;
				
			case R.id.btn_note:
				cei= new ContactEditableItem(AddContactActivity.this, AddContactActivity.this,ContactEditableItem.CONTACT_EDITABLE_TYPE_NOTE, "备注"+String.valueOf(noteContainer.getChildCount()+1));
				ceiList.add(cei);
				noteContainer.addView(cei.view);
				break;

			default:
				break;
			}
			
			reLayoutEditMode();
			popup_menu.dismiss();
		}
	}
	
	
   class MyNameTextWatch implements TextWatcher{
		
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			isChangedName = true;
		}
	}
	
   
	class MyCompanyTextWatch implements TextWatcher{
		
		@Override
		public void afterTextChanged(Editable s) {
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			isChangedCompany = true;
		}
	}
	
	
	/**
	 * 
	 * 添加联系人头像的对话框
	 * 
	 */
	private void createPickPhotoDialog() {
		
		if(!hasPhoto) //没有头像
		{
			Context context = AddContactActivity.this;

			final Context dialogContext = new ContextThemeWrapper(context,android.R.style.Theme_Light);

			String[] choices;
			choices = new String[2];
			choices[0] = "去拍照";
			choices[1] = "从相册中读取";
			
			final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
					android.R.layout.simple_list_item_1, choices);

			final AlertDialog.Builder builder = new AlertDialog.Builder(
					dialogContext);
			builder.setTitle("设置联系人头像");
			builder.setSingleChoiceItems(adapter, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							switch (which) {
							case 0:
								doTakePhoto();
								break;
							case 1:
								doPickPhotoFromGallery();
								break;
							}
						}
					});
			builder.create().show();
			
		}else{ //有头像
			
			Context context = AddContactActivity.this;

			final Context dialogContext = new ContextThemeWrapper(context,android.R.style.Theme_Light);

			String[] choices;
			choices = new String[3];
			choices[0] = "去拍照";
			choices[1] = "从相册中读取";
			choices[2] = "删除";
			
			final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
					android.R.layout.simple_list_item_1, choices);

			final AlertDialog.Builder builder = new AlertDialog.Builder(
					dialogContext);
			builder.setTitle("设置联系人头像");
			builder.setSingleChoiceItems(adapter, -1,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							switch (which) {
							case 0:
								doTakePhoto();
								break;
							case 1:
								doPickPhotoFromGallery();
								break;
								
							case 2:  //删除
								
								int result = AddContactActivity.this.getContentResolver().delete(Data.CONTENT_URI,Data._ID + "=?",new String[] { String.valueOf(dataIdOfPhoto)});
								
								System.out.println(" 删除头像    ---->" + result);
								
								hasPhoto = false;
								dataIdOfPhoto = -1;
								cur_photo = null;
								old_photo = null;
								img_add_contact_photo.setImageResource(R.drawable.default_contact);
								break;
							}
						}
					});
			builder.create().show();
		}
		
	}
	
	
	/**
	 * 
	 * 打开图片浏览应用选择图片
	 * 
	 */
	protected void doPickPhotoFromGallery() {
		try {
			final Intent intent = getPhotoPickIntent();
			AddContactActivity.this.startActivityForResult(intent,PHOTO_PICKED_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(AddContactActivity.this, "未找到图片浏览应用",Toast.LENGTH_LONG).show();
		}
	}
	
	
	/**
	 * 
	 * 打开拍照应用  拍取照片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			PHOTO_DIR.mkdirs();
			mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());
			final Intent intent = getTakePickIntent(mCurrentPhotoFile);
			AddContactActivity.this.startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(AddContactActivity.this,"未找到拍照应用",
					Toast.LENGTH_LONG).show();
		}
	}
	
	
	
	/**
	 * 
	 * 创建一个文件路径  用以存储拍照后所获取的图片
	 * 
	 */
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}
	
	
	
	public static Intent getTakePickIntent(File f) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
		return intent;
	}

	
	public static Intent getPhotoPickIntent() {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}
	
	
	protected void doCropPhoto(File f) {
		try {
			final Intent intent = getCropImageIntent(Uri.fromFile(f));
			AddContactActivity.this.startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
		} catch (Exception e) {
		}
	}
	
	
	public static Intent getCropImageIntent(Uri photoUri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(photoUri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", ICON_SIZE);
		intent.putExtra("outputY", ICON_SIZE);
		intent.putExtra("return-data", true);
		return intent;
	}
	
	
	class PhoneClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			selected_number = (String)v.getTag();
			
			switch (v.getId()) {
			case R.id.btn_call:
				
				Uri localUri = Uri.parse("tel:" + selected_number);
				Intent call = new Intent(Intent.ACTION_CALL, localUri);
				call.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				AddContactActivity.this.startActivity(call);
				
				break;

			case R.id.btn_send_sms:
				goToSendSms(selected_number);
				break;
			}
		}
	}
	
	
	class EnCryptionClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if(en_menu==null)
			{
				View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_encryption_contact, null);
				en_menu = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
				en_menu.setBackgroundDrawable(new BitmapDrawable());  //按返回键  以及点击  区域外 消失  (神奇的语句)
				en_menu.setOutsideTouchable(true);
				
				et_encryption = (EditText)view.findViewById(R.id.et_encryption);
				
				btn_en_ok = (Button)view.findViewById(R.id.btn_en_ok);
				btn_en_cancle = (Button)view.findViewById(R.id.btn_en_cancle);
				btn_en_cancle.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						en_menu.dismiss();
					}
				});
			}
			
			if(isEncryption) 
			{
				btn_en_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) { //解密
						
						if(et_encryption.getText().toString().equals(en_pw))
						{
							int i = AddContactActivity.this.getContentResolver().delete(EncryptionContentProvider.URIS, EncryptionDBHepler.CONTACT_ID+" = '" + contactId +"'", null);
							System.out.println(" i ---> " + i);
							
							isEncryption = false;
							
							Toast.makeText(AddContactActivity.this, "解密联系人成功", Toast.LENGTH_SHORT).show();
							
							
							//解密成功后AddContactActivity.this中删除相应的加密联系人
							List<EnContact> delete_en = new ArrayList<EnContact>();
							for(EnContact e:MainActivity.EN_CONTACTS)
							{
								if(e.getContactId().equals(contactId))
								{
									delete_en.add(e);
								}
							}
							
							MainActivity.EN_CONTACTS.removeAll(delete_en);
							
							
							en_menu.dismiss();
							et_encryption.setText("");
							btn_encryption.setText("加密联系人");
							MainActivity.isChangeEnData = true;
							
						}else{
							et_encryption.setText("");
							Toast.makeText(AddContactActivity.this, "输入密码有误", Toast.LENGTH_SHORT).show();
						}
					}
				});
				
			}else{
				
				btn_en_ok.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) { //加密
						
						if(et_encryption.getText().toString().equals(en_pw))
						{
							ContentValues cv = new ContentValues();
							cv.put(EncryptionDBHepler.CONTACT_ID, contactId);
							AddContactActivity.this.getContentResolver().insert(EncryptionContentProvider.URIS, cv);
							
							isEncryption = true;
							
							Toast.makeText(AddContactActivity.this, "加密联系人成功", Toast.LENGTH_SHORT).show();
							
							//加密成功后在AddContactActivity.this中添加相应的加密联系人
							EnContact en = new EnContact();
							en.setContactId(contactId);
							List<String> numbers = new ArrayList<String>();
							
							for(ContactEditableBean cb:mobiles)
							{
								numbers.add(cb.getContent());
							}
							
							for(ContactEditableBean cb:homePhones)
							{
								numbers.add(cb.getContent());
							}
							
							en.setNumbers(numbers);
							
							MainActivity.EN_CONTACTS.add(en);
							MainActivity.isChangeEnData = true;
							
							en_menu.dismiss();
							et_encryption.setText("");
							btn_encryption.setText("解密联系人");
					
						}else{
							et_encryption.setText("");
							Toast.makeText(AddContactActivity.this, "输入密码有误", Toast.LENGTH_SHORT).show();
						}
					}
				});				
			}
			
			en_menu.showAtLocation(btn_add_contact, Gravity.CENTER, 0, 0);
		}
		
	}
	
	
	class  JobCheckBosChangeListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP)
			{
				if(v!=ck)
				{
					CheckBox c = (CheckBox)v;
					c.setChecked(true);
					ck.setChecked(false);
					
					ck = c;
					
					temp_job_str = (String) ck.getTag();
					
				}
			}
			return true;
		}
	}
	
	
	class  GroupCheckBosChangeListener implements OnTouchListener{

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_UP)
			{
				if(v!=group_ck)
				{
					CheckBox c = (CheckBox)v;
					c.setChecked(true);
					group_ck.setChecked(false);
					
					group_ck = c;
					
					temp_group_index = (Integer) group_ck.getTag();
				}
			}
			return true;
		}
	}
	
	
	//选择生日
	class BirthdayClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(birthday_pick_menu==null)
			{
				View view = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_birthday_picker, null);
				birthday_pick_menu = new Dialog(AddContactActivity.this,R.style.theme_myDialog);
				birthday_pick_menu.setContentView(view);
				birthday_pick_menu.setCanceledOnTouchOutside(true);
				
				months = (WheelView) view.findViewById(R.id.birthday_month);
				months.setAdapter(new NumericWheelAdapter(1, 12));
//				months.setCurrentItem(bir_month-1);
				months.setVisibleItems(3);
				
				days = (WheelView) view.findViewById(R.id.birthday_day);
				
				if(bir_month==2)
				{
					days.setAdapter(new NumericWheelAdapter(1, 29));
				}else if("1,3,5,7,8,10,12".contains(String.valueOf(bir_month))){
					
					days.setAdapter(new NumericWheelAdapter(1, 31));
				}else{
					days.setAdapter(new NumericWheelAdapter(1, 30));
				}
				
//				days.setCurrentItem(bir_day-1);
				days.setVisibleItems(3);
				
				months.addChangingListener(new OnWheelChangedListener() {
					
					@Override
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
						int month = months.getCurrentItem()+1;
						
						System.out.println(" bir_month ---> " + month);
						
						if(month==2) //2月
						{
							days.setAdapter(new NumericWheelAdapter(1, 29));
							days.setCurrentItem(0);
							
						}else if("1,3,5,7,8,10,12".contains(String.valueOf(month))){ //31天的月份
							
							days.setAdapter(new NumericWheelAdapter(1, 31));
							days.setCurrentItem(0);
							
						}else{ //30天的月份
							
							days.setAdapter(new NumericWheelAdapter(1, 30));
							days.setCurrentItem(0);
							
						}
					}
				});
				
				days.addChangingListener(new OnWheelChangedListener() {
					
					@Override
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
					}
				});
				
				btn_picbirthday_yes = (Button)view.findViewById(R.id.btn_picbirthday_yes);
				btn_picbirthday_yes.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						bir_month = months.getCurrentItem()+1;
						bir_day = days.getCurrentItem()+1;
						
						birthday_str=bir_month+"-"+bir_day;
						et_birthday.setText(birthday_str+"("+Constellations.check(birthday_str)+")");
						
						isChangedBirthday = true;
						
						birthday_pick_menu.dismiss();
					}
				});
				
				btn_picbirthday_no = (Button)view.findViewById(R.id.btn_picbirthday_no);
				btn_picbirthday_no.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						birthday_pick_menu.dismiss();
					}
				});
			}
			
			//设定为当前生日日期
			days.setCurrentItem(bir_day-1);
			months.setCurrentItem(bir_month-1);
			
			birthday_pick_menu.show();
			
		}
	}
	
	
	/**
	 * 
	 * 联系人详情中 ，四个子内容模块的切换监听
	 * 
	 * @author Administrator
	 *
	 */
	class  ItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if(cur_item!=v)
			{
				cancleMuitlMode();
				
				switch (v.getId()) {
				case R.id.contact_item_detail:
					
					cur_ln.setVisibility(View.GONE);
					item_detail.setVisibility(View.VISIBLE);
					
					cur_item.setBackgroundResource(R.drawable.intercept_top_normal_bg);
					cur_item.setTextColor(AddContactActivity.this.getResources().getColor(R.color.text_color_base));
					
					contact_item_detail.setBackgroundResource(R.drawable.intercept_top_selected_bg);
					contact_item_detail.setTextColor(Color.WHITE);
					
					cur_item = contact_item_detail;
					cur_ln = item_detail;
					
					break;
					
				case R.id.contact_item_calls:
					
					cur_ln.setVisibility(View.GONE);
					item_history.setVisibility(View.VISIBLE);
					
					cur_item.setBackgroundResource(R.drawable.intercept_top_normal_bg);
					cur_item.setTextColor(AddContactActivity.this.getResources().getColor(R.color.text_color_base));
					
					contact_item_calls.setBackgroundResource(R.drawable.intercept_top_selected_bg);
					contact_item_calls.setTextColor(Color.WHITE);
					
					cur_item = contact_item_calls;
					cur_ln = item_history;
					
					if(contactDetailCallogAdapter==null)
					{
						if(all_numbers.size()>0)
						{
							contactDetailCallogAdapter = new ContactDetailCallogAdapter(AddContactActivity.this, AddContactActivity.this, all_numbers.get(0), new OnMenuItemClickListener());
							lv_history.setAdapter(contactDetailCallogAdapter);
							tv_history_tip.setText("通话记录(共"+contactDetailCallogAdapter.getCount()+"条)");
							selected_number_index = 0; 
						}
					}
					
					break;
					
				case R.id.contact_item_collect_msg:
					
					cur_ln.setVisibility(View.GONE);
					item_collect.setVisibility(View.VISIBLE);
					
					cur_item.setBackgroundResource(R.drawable.intercept_top_normal_bg);
					cur_item.setTextColor(AddContactActivity.this.getResources().getColor(R.color.text_color_base));
					
					contact_item_collect_msg.setBackgroundResource(R.drawable.intercept_top_selected_bg);
					contact_item_collect_msg.setTextColor(Color.WHITE);
					
					cur_item = contact_item_collect_msg;
					cur_ln = item_collect;
					
					//每次都刷新
					contactDetailCollectSmsAdapter = new ContactDetailCollectSmsAdapter(AddContactActivity.this, AddContactActivity.this, all_numbers, new CollectMsgMenuItemClickListener());
					lv_collect_msg.setAdapter(contactDetailCollectSmsAdapter);
					tv_collect_msg_tip.setText("短信收藏(共"+contactDetailCollectSmsAdapter.getCount()+"条)");
					
					
					break;

				case R.id.contact_item_remind:
					
					contactDetailRemindAdapter = new ContactDetailRemindAdapter(AddContactActivity.this,AddContactActivity.this, contactId, new RemindMenuItemClickListener());
					lv_reminds.setAdapter(contactDetailRemindAdapter);
					tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
					
					cur_ln.setVisibility(View.GONE);
					item_remind.setVisibility(View.VISIBLE);
					
					cur_item.setBackgroundResource(R.drawable.intercept_top_normal_bg);
					cur_item.setTextColor(AddContactActivity.this.getResources().getColor(R.color.text_color_base));
					
					contact_item_remind.setBackgroundResource(R.drawable.intercept_top_selected_bg);
					contact_item_remind.setTextColor(Color.WHITE);
					
					cur_item = contact_item_remind;
					cur_ln = item_remind;
					break;
					
				default:
					break;
				}
			}
		}
	}
	
	
	class OnMenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if(contactDetailCallogAdapter.menu!=null)
			{
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -contactDetailCallogAdapter.menu.getHeight());
				
				contactDetailCallogAdapter.menu.setLayoutParams(lp);
			}
			
			switch (v.getId()) {
			case R.id.menu_call: //打电话
				
				String phone_number = (String) v.getTag();
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phone_number));
				AddContactActivity.this.startActivity(intent);
				break;
				
				
			case R.id.menu_sms_detail: //发短信
				
				String n = (String)v.getTag();
				
				goToSendSms(n);
				
				break;
				
			case R.id.menu_delete: //删除
				
				String [] sp = ((String)v.getTag()).split(",");
				
				delete_positon = Integer.valueOf(sp[0]);
				delete_id = Long.valueOf(sp[1]);

				new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
					     android.R.drawable.ic_dialog_info).setMessage("确定删除选中的通话记录?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								
								int result = AddContactActivity.this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " = "+delete_id, null);
								System.out.println(" result---> " + result);
								
								contactDetailCallogAdapter.remove(delete_positon);
								
								tv_history_tip.setText("通话记录(共"+contactDetailCallogAdapter.getCount()+"条)");
								Toast.makeText(AddContactActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
								
							}
						}).setNegativeButton("取消", null).show();
				
				break;
				
				
			default:
				break;
			}
		}
	}
	
	
	/**
	 * 
	 * 
	 * 通话历史记录中 三个按钮的点击监听:   通话，短信，选择号码
	 * 
	 * @author Administrator
	 *
	 */
	class HistoryButtonClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.btn_callog_history: //通话记录
				
				if(cur_history!=btn_callog_history)
				{
					cancleMuitlMode();
				
					img.setBackgroundResource(R.drawable.top_recent_call);
					
					contactDetailCallogAdapter = new ContactDetailCallogAdapter(AddContactActivity.this, AddContactActivity.this, all_numbers.get(selected_number_index), new OnMenuItemClickListener());
					lv_history.setAdapter(contactDetailCallogAdapter);
					tv_history_tip.setText("通话记录(共"+contactDetailCallogAdapter.getCount()+"条)");
					
					cur_history.setBackgroundResource(R.drawable.black_white_top_right_normal);
					btn_callog_history.setBackgroundResource(R.drawable.black_white_top_left_selected);
					
					cur_history=btn_callog_history;
				}
				break;
				
				
			case R.id.btn_sms_history: //短信记录
				
				if(cur_history!=btn_sms_history)
				{
					cancleMuitlMode();
					
					img.setBackgroundResource(R.drawable.sms_collect_img);

					contactHistorySmsDetailAdapter = new ContactHistorySmsDetailAdapter(AddContactActivity.this, AddContactActivity.this,getSmsInPhone(all_numbers.get(selected_number_index)));
					lv_history.setAdapter(contactHistorySmsDetailAdapter);
					tv_history_tip.setText("短信记录(共"+contactHistorySmsDetailAdapter.getCount()+"条)");
					
					lv_history.setSelection(contactHistorySmsDetailAdapter.getCount()-1);
					
					cur_history.setBackgroundResource(R.drawable.black_white_top_left_normal);
					btn_sms_history.setBackgroundResource(R.drawable.black_white_top_right_selected);
					
					cur_history=btn_sms_history;
				}
				break;
				
			
			case R.id.btn_pick_history_number: //选择号码 ，弹出对话框
				if(all_numbers.size()!=0)
				{
					String [] numbers = new String [all_numbers.size()];
					for(int i = 0 ;i<all_numbers.size();i++)
					{
						numbers[i] = all_numbers.get(i);
					}
					new AlertDialog.Builder(AddContactActivity.this).setTitle("选择号码").setIcon(
						     android.R.drawable.ic_dialog_info).setSingleChoiceItems(
						    		 numbers, selected_number_index,
						     new DialogInterface.OnClickListener() {
						      public void onClick(DialogInterface dialog, int which) {

						    	  //通话记录
						    	  if(cur_history==btn_callog_history && selected_number_index != which)
						    	  {
						    		  cancleMuitlMode();
						    		  
						    		  selected_number_index = which;
						    		  contactDetailCallogAdapter = new ContactDetailCallogAdapter(AddContactActivity.this, AddContactActivity.this, all_numbers.get(selected_number_index), new OnMenuItemClickListener());
									  lv_history.setAdapter(contactDetailCallogAdapter);
									  tv_history_tip.setText("通话记录(共"+contactDetailCallogAdapter.getCount()+"条)");
									  
								  //短信历史
						    	  }else if(cur_history==btn_sms_history && selected_number_index != which) {
						    		  
						    		  cancleMuitlMode();
						    		  
						    		  selected_number_index = which;
						    		  contactHistorySmsDetailAdapter = new ContactHistorySmsDetailAdapter(AddContactActivity.this, AddContactActivity.this,getSmsInPhone(all_numbers.get(selected_number_index)));
									  lv_history.setAdapter(contactHistorySmsDetailAdapter);
									  
									  lv_history.setSelection(contactHistorySmsDetailAdapter.getCount()-1);
									  
									  tv_history_tip.setText("短信记录(共"+contactHistorySmsDetailAdapter.getCount()+"条)");
						    	  }
						    		  
						       dialog.dismiss();
						       
						      }
						     }).setNegativeButton("取消", null).show();
				}
				break;

			case R.id.btn_history_muilt_delete: //批量删除
				
				 if(cur_history == btn_callog_history )//批量删除通话记录
		    	  {
					 if(!contactDetailCallogAdapter.isEditMode())
					 {
						 btn_muitl_mode = btn_history_muilt_delete;
						 check_all.setVisibility(View.VISIBLE);
						 btn_history_muilt_delete.setVisibility(View.GONE);
						 
						 ln_delete.setVisibility(View.VISIBLE);
						 contactDetailCallogAdapter.setEditMode(true);
						 
						 btn_delete_ok.setOnClickListener(new OnClickListener() { 
							
							@Override
							public void onClick(View v) {
								
								long [] ids = contactDetailCallogAdapter.getSelectedCallogId();
								if(ids==null)
								{
									Toast.makeText(AddContactActivity.this, "请选择至少一条记录", Toast.LENGTH_SHORT).show();
								}else
								{
									
									new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
										     android.R.drawable.ic_dialog_info).setMessage("确定删除选中的通话记录?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, int which) {
													progressDialog = new ProgressDialog(AddContactActivity.this);
													progressDialog.setMessage("正在删除,请稍后");
													progressDialog.show();
													
													new Thread(new Runnable() { //开线程
														
														@Override
														public void run() {
															long [] ids = contactDetailCallogAdapter.getSelectedCallogId();
															
															int size = ids.length;
															
															String [] args = new String[size];
															
															for(int i = 0 ;i < size ;i++)
															{
																args[i] = String.valueOf(ids[i]);
																System.out.println(" i --->" + i + " ids ---->" + ids[i]);
															}
															
															StringBuffer sf = new StringBuffer();
															for(int k = 0;k<size;k++)
															{
																 if(k==size-1)
																{
																	sf.append(CallLog.Calls._ID+"=?");
																}else{
																	sf.append(CallLog.Calls._ID+"=? OR ");
																}
															}
															System.out.println(" sf ---> " + sf.toString());
															
															int delete = AddContactActivity.this.getContentResolver().delete(CallLog.Calls.CONTENT_URI, sf.toString(), args);
															
															System.out.println(" delete --->" + delete);
															
															handler.sendEmptyMessage(1);
														}
													}).start();
													
												}
											}).setNegativeButton("取消", null).show();
								}
							}
						});
						 
						 btn_delete_cancle.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								 ln_delete.setVisibility(View.GONE);
								 contactDetailCallogAdapter.setEditMode(false);
								 
								 check_all.setChecked(false);
								 check_all.setVisibility(View.GONE);
								 btn_history_muilt_delete.setVisibility(View.VISIBLE);
							}
						});
						 
					 }else{
						 ln_delete.setVisibility(View.GONE);
						 contactDetailCallogAdapter.setEditMode(false);
					 }
					 
		    	  }else if(cur_history==btn_sms_history) { //批量删除 短信历史
		    		  
		    		  
		    		  if(!contactHistorySmsDetailAdapter.isEditMode())
		    		  {
		    			  btn_muitl_mode = btn_history_muilt_delete;
		    			  
		    			  check_all.setVisibility(View.VISIBLE);
					      btn_history_muilt_delete.setVisibility(View.GONE);
							 
		    			  ln_delete.setVisibility(View.VISIBLE);
			    		  contactHistorySmsDetailAdapter.setEditMode(true);
			    		  
			    		  btn_delete_ok.setOnClickListener(new OnClickListener() { 
								
								@Override
								public void onClick(View v) {
									
									long [] ids = contactHistorySmsDetailAdapter.getSelectedCallogId();
									if(ids==null)
									{
										Toast.makeText(AddContactActivity.this, "请选择至少一条记录", Toast.LENGTH_SHORT).show();
									}else
									{
										new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
											     android.R.drawable.ic_dialog_info).setMessage("确定删除选中的短信记录?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
													
													@Override
													public void onClick(DialogInterface dialog, int which) {
														progressDialog = new ProgressDialog(AddContactActivity.this);
														progressDialog.setMessage("正在删除,请稍后");
														progressDialog.show();
														
														new Thread(new Runnable() { //开线程
															
															@Override
															public void run() {
																long [] ids = contactHistorySmsDetailAdapter.getSelectedCallogId();
																
																int size = ids.length;
																
																String [] args = new String[size];
																
																
																for(int i = 0 ;i < size ;i++)
																{
																	args[i] = String.valueOf(ids[i]);
																	
																	int delete = AddContactActivity.this.getContentResolver().delete(Uri.parse("content://sms/conversations/" + contactHistorySmsDetailAdapter.getThread_id()), " _id = " + args[i] , null);
																	
																	System.out.println(" delete --->" + delete);
																}
																
																handler.sendEmptyMessage(2);
															}
														}).start();
														
													}
												}).setNegativeButton("取消", null).show();
										
									}
								}
							});
			    		  
			    		  
			    		  btn_delete_cancle.setOnClickListener(new OnClickListener() {
								
								@Override
								public void onClick(View v) {
									
									  ln_delete.setVisibility(View.GONE);
						    		  contactHistorySmsDetailAdapter.setEditMode(false);
						    		  
						    		  check_all.setChecked(false);
						    		  check_all.setVisibility(View.GONE);
								      btn_history_muilt_delete.setVisibility(View.VISIBLE);
								}
							});
			    		  
			    		  
		    		  }else{
		    			  ln_delete.setVisibility(View.GONE);
			    		  contactHistorySmsDetailAdapter.setEditMode(false);
		    		  }
		    	  }
				break;
				
			default:
				break;
			}
		}
	}
	
	
	/**
	 * 
	 * 
	 * 跳转至发送短信
	 * 
	 * 
	 * @param number
	 */
	private void goToSendSms(String number)
	{
		
		String thread_id = NewMessageActivity.queryThreadIdByNumber(AddContactActivity.this, number);

		Intent intent = new Intent(AddContactActivity.this, NewMessageActivity.class);
		intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
		intent.putExtra(NewMessageActivity.DATA_NUMBER, number);
				
		AddContactActivity.this.startActivity(intent);
		
	}
	
	
	/**
	 * 
	 * 
	 * 根据电话号码 获取整个短信会话记录
	 * 
	 * 
	 * @param number
	 * @return
	 */
	public ArrayList<SmsContent> getSmsInPhone(String number)   
	{   
		String thread_id = NewMessageActivity.queryThreadIdByNumber(AddContactActivity.this, number);
		
		ArrayList<SmsContent>  listContent = new ArrayList<SmsContent>();
		
		if(thread_id != null)
		{
		    try{   
		        ContentResolver cr = getContentResolver();   
		        String[] projection = new String[]{"_id", "address", "person", "body", "date", "type"};   
		        
		        Uri uri =  Uri.parse("content://sms/conversations/" + thread_id);
		        Cursor cur = cr.query(uri, projection, "thread_id = ?", new String[]{thread_id}, "date asc");   
		        
		        if (cur.moveToFirst()) {   
		            String name;    
		            String phoneNumber;          
		            String smsbody;   
		            String date;   
		            String type;   
		            
		            int idColumn = cur.getColumnIndex("_id");
		            int nameColumn = cur.getColumnIndex("person");   
		            int phoneNumberColumn = cur.getColumnIndex("address");   
		            int smsbodyColumn = cur.getColumnIndex("body");   
		            int dateColumn = cur.getColumnIndex("date");   
		            int typeColumn = cur.getColumnIndex("type");   
		            
		            do{   
		                name = cur.getString(nameColumn);  
		                
		                phoneNumber = cur.getString(phoneNumberColumn);   
		                smsbody = cur.getString(smsbodyColumn);   
		                   
		                int typeId = cur.getInt(typeColumn); 
		                
		                SmsContent smsContent=new SmsContent();
		                smsContent.setId(cur.getLong(idColumn));
		                smsContent.setThread_id(Integer.valueOf(thread_id));
		                smsContent.setSms_name(cur.getString(nameColumn));
		                smsContent.setSms_number(cur.getString(phoneNumberColumn));
		                smsContent.setSms_body(cur.getString(smsbodyColumn));
		                smsContent.setDate(cur.getLong(dateColumn));
		                smsContent.setSms_type(typeId);
		                smsContent.setSend_type(String.valueOf(typeId));
		                
		                if(smsbody == null) smsbody = "";    
		                listContent.add(smsContent);
		            }while(cur.moveToNext());   
		            
		            cur.close();
		            
		        } else {   
		        }   
		            
		    } catch(Exception ex) {   
		    	ex.printStackTrace();
		    }   
		}
	    
		return listContent;  
	} 
	
	
	public void updateDeleteNum(int selected)
	{
		btn_delete_ok.setText("删除("+selected+")");
	}
	
	
	/**
	 * 
	 * 批量删除收藏短信监听
	 * @author Administrator
	 *
	 */
	class CollectMsgDeleteClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			if(!contactDetailCollectSmsAdapter.isEditMode())
			 {
				btn_muitl_mode = btn_collect_msg_delete;
				
				btn_collect_msg_delete.setVisibility(View.GONE);
				
				 ln_delete.setVisibility(View.VISIBLE);
				 check_collect_all.setVisibility(View.VISIBLE);
				 contactDetailCollectSmsAdapter.setEditMode(true);
				 
				 btn_delete_ok.setOnClickListener(new OnClickListener() { 
					
					@Override
					public void onClick(View v) {
						
						long [] ids = contactDetailCollectSmsAdapter.getSelectedIds();
						if(ids==null)
						{
							Toast.makeText(AddContactActivity.this, "请选择至少一条记录", Toast.LENGTH_SHORT).show();
						}else
						{
							new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
								android.R.drawable.ic_dialog_info).setMessage("确定删除选中的短信?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											progressDialog = new ProgressDialog(AddContactActivity.this);
											progressDialog.setMessage("正在删除,请稍后");
											progressDialog.show();
											
											new Thread(new Runnable() { //开线程
												
												@Override
												public void run() {
													long [] ids = contactDetailCollectSmsAdapter.getSelectedIds();
													
													int size = ids.length;
													
													String [] args = new String[size];
													
													for(int i = 0 ;i < size ;i++)
													{
														args[i] = String.valueOf(ids[i]);
														System.out.println(" i --->" + i + " ids ---->" + ids[i]);
													}
													
													StringBuffer sf = new StringBuffer();
													for(int k = 0;k<size;k++)
													{
														 if(k==size-1)
														{
															sf.append(MyDatabaseUtil.FAVORITE_ID+"="+args[k]);
														}else{
															sf.append(MyDatabaseUtil.FAVORITE_ID+"="+args[k]+" OR ");
														}
													}
													System.out.println(" sf ---> " + sf.toString());
													
													DButil.getInstance(AddContactActivity.this).deleteFavorite(sf.toString());
													
													handler.sendEmptyMessage(3);
												}
											}).start();
											
										}
									}).setNegativeButton("取消", null).show();
						}
					}
				});
				 
				 btn_delete_cancle.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						 ln_delete.setVisibility(View.GONE);
						 
						 check_collect_all.setChecked(false);
						 check_collect_all.setVisibility(View.GONE);
						 contactDetailCollectSmsAdapter.setEditMode(false);
						 
						 btn_collect_msg_delete.setVisibility(View.VISIBLE);
					}
				});
				 
			 }else{
				 ln_delete.setVisibility(View.GONE);
				 contactDetailCollectSmsAdapter.setEditMode(false);
			 }
		}
	}
	
	/**
	 * 
	 * 收藏短信列表  单击后 下拉弹框的选项监听
	 * 
	 * @author Administrator
	 *
	 */
	class CollectMsgMenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if(contactDetailCollectSmsAdapter.menu!=null)
			{
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -contactDetailCollectSmsAdapter.menu.getHeight());
				
				contactDetailCollectSmsAdapter.menu.setLayoutParams(lp);
			}
			
			switch (v.getId()) {
			case R.id.menu_check: //查看原短信
				
				String all = v.getTag().toString();
				
				String [] ss = all.split(":");
				
				//原短信是否存在
				String thread_id =null;
				Cursor c = AddContactActivity.this.getContentResolver().query(Uri.parse("content://sms/"), new String[]{"thread_id"}, "_id =? ", new String [] {ss[0]}, null);
				if(c.moveToNext())
				{
					thread_id = c.getString(c.getColumnIndex("thread_id"));
				}
				c.close();
				
				if(thread_id!=null)
				{
					Intent intent = new Intent(AddContactActivity.this, NewMessageActivity.class);
					intent.putExtra(NewMessageActivity.DATA_THREAD_ID, thread_id);
					intent.putExtra(NewMessageActivity.DATA_OTHER, ss[0]);
							
					AddContactActivity.this.startActivity(intent);
					
				}else{
					Toast.makeText(AddContactActivity.this, "原短信已删除",Toast.LENGTH_SHORT).show();
				}
				
				
				break;
				
			case R.id.menu_re_send: //转发
				
				String content = v.getTag().toString();

				Intent intent = new Intent(AddContactActivity.this, NewMessageActivity.class);
				intent.putExtra(NewMessageActivity.DATA_OTHER, content);
						
				AddContactActivity.this.startActivity(intent);
				
				
				break;
				
			case R.id.menu_delete: //删除
				String s = (String)v.getTag();
				String [] sss = s.split(":");
				
				final int id = Integer.valueOf(sss[0]);
				final int position = Integer.valueOf(sss[1]);
				
				new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
						android.R.drawable.ic_dialog_info).setMessage("确定删除选中的收藏短信?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									DButil.getInstance(AddContactActivity.this).deleteFavorite(MyDatabaseUtil.FAVORITE_ID+" = "+id);
									contactDetailCollectSmsAdapter.remove(position);
									tv_collect_msg_tip.setText("短信收藏(共"+contactDetailCollectSmsAdapter.getCount()+"条)");
								}
							}).setNegativeButton("取消", null).show();
				
				break;

			default:
				break;
			}
		}
	}
	
	
	/**
	 * 生成二维码图片
	 * 
	 * @param type  类型 :  0 为小图, 1为大图
	 * @param str  内容  
	 * @return
	 * @throws WriterException
	 */
	public Bitmap create2DCode(int type,String str) throws WriterException {
		
		int size = 0;
		
		if(type==0) //小图
		{
			size = AddContactActivity.this.getResources().getDimensionPixelSize(R.dimen.crcode_size_small);
			System.out.println("size  ---> " +size);
		}else{ //大图
			size = AddContactActivity.this.getResources().getDimensionPixelSize(R.dimen.crcode_size_big);
		}
		
        //生成二维矩阵,编码时要指定大小,不要生成了图片以后再进行缩放,以防模糊导致识别失败
        BitMatrix matrix = new MultiFormatWriter().encode(str,BarcodeFormat.QR_CODE,size,size);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        // 二维矩阵转为一维像素数组（一直横着排）
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(matrix.get(x, y)){
                    pixels[y * width + x] = 0xff000000;
                }
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        // 通过像素数组生成bitmap,具体参考api
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }
	
	
	/**
	 * 
	 * 事件提醒内容区的  按钮监听
	 * @author Administrator
	 *
	 */
	class RemindClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			
			case R.id.btn_delete_reminds: //删除
				if(!contactDetailRemindAdapter.isEditMode())
				 {
					btn_muitl_mode = btn_delete_reminds;
					
					btn_delete_reminds.setVisibility(View.GONE);
					btn_add_reminds.setVisibility(View.GONE);
					
					 ln_delete.setVisibility(View.VISIBLE);
					 check_remind_all.setVisibility(View.VISIBLE);
					 contactDetailRemindAdapter.setEditMode(true);
					 
					 btn_delete_ok.setOnClickListener(new OnClickListener() { 
						
						@Override
						public void onClick(View v) {
							
							long [] ids = contactDetailRemindAdapter.getSelectedId();
							
							if(ids==null)
							{
								Toast.makeText(AddContactActivity.this, "请选择至少一条记录", Toast.LENGTH_SHORT).show();
							}else
							{
								new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
									android.R.drawable.ic_dialog_info).setMessage("确定删除选中的提醒?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
											
											@Override
											public void onClick(DialogInterface dialog, int which) {
												
												progressDialog = new ProgressDialog(AddContactActivity.this);
												progressDialog.setMessage("正在删除,请稍后");
												progressDialog.show();
//												
												new Thread(new Runnable() { //开线程
													
													@Override
													public void run() {
														
														long [] ids = contactDetailRemindAdapter.getSelectedId();
														
														int size = ids.length;
														
														for(int i = 0; i<size ; i++)
														{
															long delete =  DButil.getInstance(AddContactActivity.this).delete(ids[i]);
															System.out.println("ids[i]  --->"+ ids[i] + "  delete --->" + delete);
															
															if(delete>0)
															{
																Intent it = new Intent(AddContactActivity.this, AlarmReceiver.class);
													    		it.putExtra(MyDatabaseUtil.REMIND_ID, ids[i]);		
													    		PendingIntent pit = PendingIntent.getBroadcast(AddContactActivity.this, (int)ids[i], it, 0);
													    		AlarmManager amr = (AlarmManager) AddContactActivity.this.getSystemService(Activity.ALARM_SERVICE);
													    		amr.cancel(pit); //取消
															}
														}
														handler.sendEmptyMessage(4);
													}
												}).start();
												
											}
										}).setNegativeButton("取消", null).show();
							}
						}
					});
					 
					 btn_delete_cancle.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							 ln_delete.setVisibility(View.GONE);
							 check_remind_all.setChecked(false);
							 check_remind_all.setVisibility(View.GONE);
							 contactDetailRemindAdapter.setEditMode(false);
							 
							 btn_delete_reminds.setVisibility(View.VISIBLE);
							 btn_add_reminds.setVisibility(View.VISIBLE);
						}
					});
					 
				 }else{
					 ln_delete.setVisibility(View.GONE);
					 contactDetailRemindAdapter.setEditMode(false);
				 }
				break;
				
				
			case R.id.btn_add_reminds:
				
				
				new AddEditRmindLayout(AddContactActivity.this, -1, Integer.valueOf(contactId),new OnFinishEditRemindListener() {
					@Override
					public void OnFinishEditRemind() {  //操作成功的回调刷新
						contactDetailRemindAdapter = new ContactDetailRemindAdapter(AddContactActivity.this,AddContactActivity.this, contactId, new RemindMenuItemClickListener());
						lv_reminds.setAdapter(contactDetailRemindAdapter);
						tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
						
					}
				});
				
				break;

			default:
				break;
			}
		}
	}
	
	/**
	 * 
	 * 事件提醒列表中  点击后  下拉弹框 操作选项的监听
	 * 
	 * @author Administrator
	 *
	 */
	class RemindMenuItemClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			if(contactDetailRemindAdapter.menu!=null)
			{
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
				lp.setMargins(0, 0, 0, -contactDetailRemindAdapter.menu.getHeight());
				
				contactDetailRemindAdapter.menu.setLayoutParams(lp);
			}
			
			switch (v.getId()) {
			case R.id.menu_edit_remind: //编辑
				
				String tag = v.getTag().toString();
				int remind_id = Integer.parseInt(tag.substring(0, tag.indexOf(",")));
				
				new AddEditRmindLayout(AddContactActivity.this, remind_id, -1,new OnFinishEditRemindListener() {
					@Override
					public void OnFinishEditRemind() {  //修改成功的回调刷新
						contactDetailRemindAdapter = new ContactDetailRemindAdapter(AddContactActivity.this,AddContactActivity.this, contactId, new RemindMenuItemClickListener());
						lv_reminds.setAdapter(contactDetailRemindAdapter);
						tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
					}
				});
				
				break;
				
			case R.id.menu_delete_remind: //删除指定的一条提醒
				
				String[] ss = ((String)v.getTag()).split(":");
				
				final int id = Integer.valueOf(ss[0]);
				final int position = Integer.valueOf(ss[1]);
				
				new AlertDialog.Builder(AddContactActivity.this).setTitle("提示").setIcon(
						android.R.drawable.ic_dialog_info).setMessage("确定删除选中的提醒?").setPositiveButton("确定", new  DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									
									
									long result = DButil.getInstance(AddContactActivity.this).delete(id);
									
//									System.out.println(" id --->" + id);
//									System.out.println(" position --->" + position);
//									System.out.println(" result --->" + result);
									
									contactDetailRemindAdapter.remove(position);

									//取消闹钟服务
									Intent it = new Intent(AddContactActivity.this, AlarmReceiver.class);
						    		it.putExtra(MyDatabaseUtil.REMIND_ID, id);		
						    		PendingIntent pit = PendingIntent.getBroadcast(AddContactActivity.this, (int)id, it, 0);
						    		AlarmManager amr = (AlarmManager) AddContactActivity.this.getSystemService(Activity.ALARM_SERVICE);
						    		amr.cancel(pit);
									tv_reminds_msg_tip.setText("事件提醒(共"+contactDetailRemindAdapter.getCount()+"条)");
									
								}
							}).setNegativeButton("取消", null).show();
				break;

			default:
				break;
			}
		}
	}

	
	/**
	 * 
	 * 显示参与人的详细信息
	 * 
	 * @param contactId
	 * @param name
	 * @param phone_list
	 */
	public void showPartnerDetail(int contactId, String name,
			String[] phone_list) {
		
		View v = LayoutInflater.from(AddContactActivity.this).inflate(R.layout.dialog_partner_detail, null);
		pop_partner = new Dialog(AddContactActivity.this,R.style.theme_myDialog);
		pop_partner.setCanceledOnTouchOutside(true);
		pop_partner.setContentView(v);
		
		TextView tv_title = (TextView)v.findViewById(R.id.tv_title);

		String source = "参与人: "+name+" 的资料";
		tv_title.setText(Html.fromHtml(source.replace(name, "<font color='#3d8eba'>" + name+ "</font>")));
		
		Button btn_close = (Button)v.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				pop_partner.dismiss();
			}
		});
		
		TextView tv_job = (TextView)v.findViewById(R.id.tv_job);
		String job = "无";
		// 获取该联系人组织,, 只取第一个 有内容的公司
		Cursor organizations = AddContactActivity.this.getContentResolver().query(
							Data.CONTENT_URI,new String[] { Data._ID, Organization.COMPANY,Organization.TITLE,Organization.JOB_DESCRIPTION },
							Data.CONTACT_ID + "=?" + " AND " + Data.MIMETYPE + "='"+ Organization.CONTENT_ITEM_TYPE + "'",
							new String[] { String.valueOf(contactId) }, null);
						while (organizations.moveToNext()) {
							String job_str = organizations.getString(organizations.getColumnIndex(Organization.JOB_DESCRIPTION));
							
							if(job_str!=null) //职业
							{
								job = job_str;
								break;
							}
						} ;
		organizations.close();
		tv_job.setText(job);
		
		ListView lv_partner_numbers = (ListView)v.findViewById(R.id.lv_partner_numbers);
		lv_partner_numbers.setAdapter(new PartnerDetailAdapter(AddContactActivity.this,AddContactActivity.this,contactId,name, phone_list));
		
		pop_partner.show();
		
	}
	
	
	/**
	 * 
	 * 打电话 
	 * 
	 * @param number
	 */
	public void trigglerPhoneCall(String number)
	{
		if(pop_partner!=null)
		{
			pop_partner.dismiss();
		}
		
		Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+number));
		AddContactActivity.this.startActivity(intent);
		
	}
	
	
	/**
	 * 
	 * 发短信
	 * 
	 * @param number
	 * @param name
	 */
	public void trigglerSms(String number,String name)
	{
		
		if(pop_partner!=null)
		{
			pop_partner.dismiss();
		}
		
		goToSendSms(number);
	}
	
	
	/**
	 * 
	 * 撤销多选模式(批量模式)
	 * 
	 */
	private void cancleMuitlMode() 
	{
		if(btn_muitl_mode!=null)
		{
			switch (btn_muitl_mode.getId()) {
			case R.id.btn_history_muilt_delete:
				
				if((contactDetailCallogAdapter!=null && contactDetailCallogAdapter.isEditMode()) ||  (contactHistorySmsDetailAdapter!=null && contactHistorySmsDetailAdapter.isEditMode()))
				{
					check_all.setChecked(false);
					check_all.setVisibility(View.GONE);
					
					btn_history_muilt_delete.setVisibility(View.VISIBLE);
					
					 if(cur_history==btn_callog_history)
		             {
						
		                contactDetailCallogAdapter.selectALL(false);
		                contactDetailCallogAdapter.setEditMode(false);
		    			contactDetailCallogAdapter.notifyDataSetChanged();    
		    			updateDeleteNum(contactDetailCallogAdapter.getSelectedItemIndexes().length);
		             }else{
		            	 
		                contactHistorySmsDetailAdapter.selectALL(false);
		                contactHistorySmsDetailAdapter.setEditMode(false);
		                contactHistorySmsDetailAdapter.notifyDataSetChanged();    
		    			updateDeleteNum(contactHistorySmsDetailAdapter.getSelectedItemIndexes().length);
		             }
				}
				break;
				
			case R.id.btn_collect_msg_delete:
				
				if(contactDetailCollectSmsAdapter.isEditMode())
				{
					check_collect_all.setChecked(false);
					check_collect_all.setVisibility(View.GONE);
					
					contactDetailCollectSmsAdapter.selectALL(false);
					contactDetailCollectSmsAdapter.setEditMode(false);
					contactDetailCollectSmsAdapter.notifyDataSetChanged();    
    			 	updateDeleteNum(contactDetailCollectSmsAdapter.getSelectedItemIndexes().length);
					
					btn_collect_msg_delete.setVisibility(View.VISIBLE);
				}
				break;
				
			case R.id.btn_delete_reminds:
				
				if(contactDetailRemindAdapter.isEditMode())
				{
					check_remind_all.setChecked(false);
					check_remind_all.setVisibility(View.GONE);
					
					contactDetailRemindAdapter.selectALL(false);
					contactDetailRemindAdapter.setEditMode(false);
					contactDetailRemindAdapter.notifyDataSetChanged();    
    			 	updateDeleteNum(contactDetailRemindAdapter.getSelectedItemIndexes().length);
    			 	
					btn_delete_reminds.setVisibility(View.VISIBLE);
					btn_add_reminds.setVisibility(View.VISIBLE);
				}
				break;

			default:
				break;
			}
			
			ln_delete.setVisibility(View.GONE);
		}
	}
	
	
	/**
	 * 电子名片点击
	 * @author Administrator
	 *
	 */
	class MyCardClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
				showMyCard();
		}
	}
	
	
	/**
	 * 
	 * 显示电子名片
	 * 
	 */
	void showMyCard()
	{
		Intent myCardIntent = new Intent(AddContactActivity.this, MyCardActivity.class);
		myCardIntent.putExtra(MyCardActivity.MY_CONTACT_ID, contactId);
		AddContactActivity.this.startActivity(myCardIntent);
	}
	
	
	/**
	 * 
	 * 添加事件提醒
	 * 
	 * @param msg
	 */
	public void addRemind(String msg)
	{
		AddEditRmindLayout AddEditRmindLayout = new AddEditRmindLayout(AddContactActivity.this, -1, Integer.valueOf(contactId),null);
		AddEditRmindLayout.setRemindContent(msg);
	}

	
	/**
	 * 
	 * 添加 短信收藏 
	 * 
	 * @param sms
	 */
	public void addCollectMsg(SmsContent sms) {
		
		MyDatabaseUtil myDatabaseUtil  = DButil.getInstance(AddContactActivity.this);
		
		String phone_num = PhoneNumberTool.cleanse(sms.getSms_number());
		
		String info [] = PhoneNumberTool.getContactInfo(AddContactActivity.this, phone_num);
		String name = info[0];
	    if(name==null)
	    {
	    	name=phone_num;
	    }
	    
		long i = myDatabaseUtil.insertDataFavorite(String.valueOf(sms.getThread_id()), String.valueOf(sms.getId()), sms.getSms_body(), System.currentTimeMillis(), ("1").equals(sms.getSend_type()) ?name:"我", phone_num);
//		System.out.println(" i  --->" + i + "  "+ disPlayName);
		Toast.makeText(AddContactActivity.this, "收藏成功", Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 
	 * 通过电话号码获取名字
	 * 
	 * @param numbers
	 * @return
	 */
	public String getNameByNumber(String numbers)
	{
		
		String info [] = PhoneNumberTool.getContactInfo(AddContactActivity.this, numbers);
		
	    return 	info [0];	
	}
	
	
	/**
	 * 
	 * 短信转发
	 * 
	 * @param msg
	 */
	public void smsForward(String msg)
	{

		Intent intent = new Intent(AddContactActivity.this, NewMessageActivity.class);
		intent.putExtra(NewMessageActivity.DATA_OTHER, msg);
				
		AddContactActivity.this.startActivity(intent);
		
	}
	
	
	public void updateSmsNumAfterDelete()
	{
		tv_history_tip.setText("短信记录(共"+ contactHistorySmsDetailAdapter.getCount() + "条)");
	}
	
	
	/**
	 * 
	 * 检测 当前是否只有一个号码 
	 * 
	 * @return
	 */
	public boolean isLastNumber()
	{
		int count =0;
		for(ContactEditableItem cei:ceiList)
		{
			int type = cei.getType();
			
			if(type == ContactEditableItem.CONTACT_EDITABLE_TYPE_HOME_PHONE || type == ContactEditableItem.CONTACT_EDITABLE_TYPE_MOBILE)
			{
				count++;
			}
		}
		
		System.out.println(" count ---> " + count);
		
		if(count>1)
		{
			return false;
		}else{
			return true;
		}
	}
	
	
	public void addDeleteDateId(long date_id)
	{
		delete_date_id.add(date_id);
	}
	
	
}