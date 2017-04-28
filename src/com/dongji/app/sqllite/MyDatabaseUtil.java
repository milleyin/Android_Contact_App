package com.dongji.app.sqllite;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.entity.KeywordEntity;
import com.dongji.app.entity.SmsContent;
import com.dongji.app.tool.PhoneNumberTool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class MyDatabaseUtil {

	private Context mContext;
	private SQLiteDatabase mSQLiteDatabase = null;
	private DatabaseHelper mDatabaseHelper = null;

	private static final String DB_NAME = "addressbook.db";//数据库名称
    private static final int DB_VERSION =1;//版本
    private static final String TABLE_1="table_1";
  
    public static final String  KEY_ID ="key_id";
    public static final String  KEY_NAME ="key_name";
    public static final String  KEY_AGE ="key_age";
	//用户表start
    public static final String USERID="uid";
    public static final String USERNAME="username";
    public static final String PASSWORD="password";
    
    private static final String DEL1="del1";
    private static final String DEL2="del2";
    private static final String DEL3="del3";
    //用户表end
	//系统表start
    private static final String USER="user";
    
    //短信库表
    private static final String MESSAGE_LIBRARY="message_library";
    
    public static final String  MESSAGE_ID ="message_id";
    public static final String  MESSAGE_CONTENT ="message_content";//内容
    public static final String  MESSAGE_CATEGORY ="message_category";//分类
    public static final String  MESSAGE_DATE ="message_date";//时间
    public static final String  MESSAGE_DEL1 ="message_del1";//备用字段
    public static final String  MESSAGE_DEL2 ="message_del2";//备用字段
    public static final String  MESSAGE_DEL3 ="message_del3";//备用字段
    
    
    //短信收藏表Favorite
    private static final String MESSAGE_FAVORITE="message_favorite";
    public static final String  FAVORITE_ID ="favorite_id"; //自增id
    public static final String  THREAD_ID ="thread_id"; //会话id
    public static final String  CONTENT_ID ="content_id"; //某条短信的具体id
    public static final String  FAVORITE_CONTENT ="favorite_content"; //短信内容
    public static final String  CONTENT_TIME ="favorite_time"; //短信时间   　：　　long 型
    public static final String  FAVORITE_SENDER ="favorite_send"; //短信发送者
    public static final String  FAVORITE_NUMBER ="favorite_number"; //电话号码
    public static final String  FAVORITE_DEL1 ="favorite_del1"; //备用字段
    public static final String  FAVORITE_DEL2 ="favorite_del2"; //备用字段
    public static final String  FAVORITE_DEL3 ="favorite_del3"; //备用字段
    
    
    //提醒表 
    private static final String TABLE_REMIND="table_remind";
    
    public static final String REMIND_ID="remind_id";  //提醒id
    public static final String REMIND_CONTENT = "remind_content"; //内容
    public static final String REMIND_CONTACT = "remind_contact"; //联系人相关信息 : #id#:name:p,p,p 
//    public static final String REMIND_CONTACT_ID = "remind_contact_id";
    public static final String REMIND_PARTICIPANT = "remind_participants"; //参与人相关信息 (以分号隔开)  :  #id#:name:p,p,p ;  #id#:name:p,p,p  ;
    public static final String REMIND_START = "remind_start";
    public static final String REMIND_END = "remind_end";
    
    public static final String REMIND_TYPE = "remind_type";//提醒类型,提前多久提醒
    public static final String REMIND_NUM = "remind_num"; 
    public static final int REMIND_TYPE_MIN = 0;
    public static final int REMIND_TYPE_HOUR = 1;
    public static final int REMIND_TYPE_DAY = 2;
    public static final int REMIND_TYPE_WEEK = 3;
    
    public static final String REMIND_TIME = "remind_time"; //提醒次数
    
    public static final String REPEAT_TYPE = "repeat_type"; //重复类型
    public static final int REPEAT_TYPE_ONE = 0;
    public static final int REPEAT_TYPE_DAY = 1;
    public static final int REPEAT_TYPE_WEEK = 2;
    public static final int REPEAT_TYPE_MONTH = 3;
    public static final int REPEAT_TYPE_YEAR = 4;
    
    public static final String REPEAT_FREQ = "repeat_freq"; //重复频率
    public static final String REPEAT_CONDITION = "repeat_condition"; //重复条件
    
    public static final String REPEAT_START_TIME ="repeat_start_time";
    public static final String REPEAT_END_TIME ="repeat_end_time";
    public static final String HAS_REMIND_TIME = "has_remind_time"; //已经提醒了多少次
    
    public static final String TIME_FILTER = "time_filter";  //时间过滤： text类型   long,long,long ;  在此时间内的提醒将不被触发
    
    private static final String REMIND_DEL1="remind_del1";
    private static final String REMIND_DEL2="remind_del2";
    private static final String REMIND_DEL3="remind_del3";
    
    //联系人分组提醒 表
    private static final String TABLE_CONTACT_GROUP_REMIND = "table_contact_group_remind";
    
    public static final String  CONTACT_GROUP_REMIND_ID = "contact_group_remind_id"; // 自增
    public static final String  CONTACT_GROUP_ID ="contact_group_id"; // 联系人分组的ID 
    public static final String  CONTACT_GROUP_TIME_GAP ="contact_group_time_gap"; // 隔多久提醒
    
    
    //定时发送短信表 
    private static final String TABLE_SMS_TIMING = "table_sms_timing";
    
    public static final String SMS_TIMING_ID = "sms_timing_id";
    public static final String SMS_TIMING_TIME ="sms_timing_time";  //定时的时间
    public static final String SMS_TIMING_NUMBER =  "sms_timing_number"; //号码    如有多个号码，使用冒号隔开，如 : 150142121;4546545;
    public static final String SMS_TIMING_CONTENT = "sms_timing_content"; //内容
    
    //黑名单--白名单
    public static final String black_table = "blacklist";
    public static final String white_table = "whitelist";
	public static final String _ID = "_id";
	public static final String CONTACT_ID = "contact_id";
	public static final String CONTACT_NAME = "contact_name";
	public static final String CONTACT_NUMBER = "phone_number";
	public static final String CONTACT_PHOTO = "contact_photo";
	
	//关键字
	public static final String KEYWORD_TABLE = "keyword_info";
	public static final String K_ID = "_id";
	public static final String CONTENT = "content";
	public static final String DATE_TIME = "_date_time";
	public static final String GET_TYPE = "get_type";
	
	//被拦截的电话信息
	public static final String PHONEINTERCEPT_TABLE = "phoneIntercept";
	public static final String P_ID = "_id";
	public static final String CALLLOG_ID = "calllog_id";
	public static final String CALLLOG_NAME = "calllog_name";
	public static final String CALLLOG_NUMBER = "phone_number";
	public static final String PHONEINTERCEPT_DATE_TIME = "_date_time";
	public static final String SYS_TIME = "system_time";
	
	//添加响铃一声电话信息
	public static final String RINGONECALL_TABLE = "ringonecall";
	public static final String R_ID = "_id";
	public static final String R_CALLLOG_ID = "callLog_id";
	public static final String NUMBER = "number";
	public static final String R_DATE_TIME = "_date_time";
	
	//被拦截的短信信息表
	public static final String SMSINTERCEPT_TABLE = "SMSIntercept";
	public static final String S_ID = "_id";
//	public static final String THREAD_ID = "thread_id";
	public static final String S_CONTACT_ID = "contact_id";
	public static final String S_CONTACT_NUMBER = "phone_number"; 
	public static final String S_CONTACT_NAME = "contact_name";
	public static final String S_SMS_CONTENT = "sms_content";
	public static final String S_DATE_TIME = "_date_time";
	public static final String S_SYS_TIME = "sys_time";
	public static final String S_CONTACT_PHOTO = "contact_photo";
    
    //创建提醒表
   	private static final String CREATE_REMIND = "CREATE TABLE " + TABLE_REMIND + "("
   	+ REMIND_ID + " INTEGER PRIMARY KEY ,"  +REMIND_CONTENT+" TEXT ,"+REMIND_CONTACT+" TEXT , "+REMIND_PARTICIPANT+" TEXT , "
   	+REMIND_START+" LONG , "+REMIND_END+" LONG , "+REMIND_TYPE+" INTEGER , "+REMIND_NUM+" INTEGER ,"+REMIND_TIME+" INTEGER , "+REPEAT_TYPE+" INTEGER , "+REPEAT_FREQ+" INTEGER , "+REPEAT_CONDITION+" TEXT ,"+REPEAT_START_TIME+" LONG ,"+REPEAT_END_TIME+" LONG ,"+ HAS_REMIND_TIME +" INTEGER," + TIME_FILTER+ " TEXT,"+ REMIND_DEL1 +" CHAR,"+REMIND_DEL2+" CHAR,"+REMIND_DEL3+" CHAR);";
    
   	
   	public static int BASE_CONTACT_GOUP_REMIND_ID = 100000000; //基数
   	//创建联系人分组提醒表
   	private static final String CREATE_TABLE_CONTACT_GROUP_REMIND = "CREATE TABLE " + TABLE_CONTACT_GROUP_REMIND + "("
   		   	+ CONTACT_GROUP_REMIND_ID + " INTEGER PRIMARY KEY ,"  +CONTACT_GROUP_ID+" INTEGER ,"+ CONTACT_GROUP_TIME_GAP +" LONG);";
   	
   	
  	public static int BASE_SMS_TIMING_ID = 200000000; //基数
  	
  	
    //创建定时短信表
   	private static final String CREATE_TABLE_SMS_TIMING = "CREATE TABLE " + TABLE_SMS_TIMING + "("
   		   	+ SMS_TIMING_ID + " INTEGER PRIMARY KEY ," +SMS_TIMING_TIME +" LONG ," +SMS_TIMING_NUMBER+" TEXT ,"+ SMS_TIMING_CONTENT +" TEXT);";
    
   	
    //短信收藏表
	private static final String CREATE_MESSAGE_FAVORITE = "CREATE TABLE " + MESSAGE_FAVORITE + "("
	+ FAVORITE_ID + " INTEGER PRIMARY KEY ,"  +THREAD_ID+" CHAR ,"+CONTENT_ID+" CHAR , "+FAVORITE_CONTENT+" CHAR , "+CONTENT_TIME+" LONG , "+FAVORITE_SENDER+" CHAR , "+FAVORITE_NUMBER+" CHAR , "+FAVORITE_DEL1+" CHAR , "+FAVORITE_DEL2+" CHAR , "+FAVORITE_DEL3+" CHAR);";
    
	
	
	//短信库表
	private static final String CREATE_MESSAGE_LIBRARY = "CREATE TABLE " + MESSAGE_LIBRARY + "("
	+ MESSAGE_ID + " INTEGER PRIMARY KEY ,"  +MESSAGE_CONTENT+" CHAR ,"+MESSAGE_CATEGORY+" CHAR , "+MESSAGE_DATE+" CHAR , "+MESSAGE_DEL1+" CHAR , "+MESSAGE_DEL2+" CHAR , "+MESSAGE_DEL3+" CHAR);";
	
	//创建黑名单表
	private static final String CREATE_BLACK_SQL = "CREATE TABLE IF NOT EXISTS "+ black_table + " (" +
			_ID + " INTEGER NOT NULL, " + CONTACT_ID +" TEXT NOT NULL,"+CONTACT_NAME + " TEXT,"+CONTACT_NUMBER + " TEXT NOT NULL,"+CONTACT_PHOTO + " BYTE,"+" PRIMARY KEY (" + _ID+ "));";
	
	//创建白名单表
	private static final String CREATE_WHITE_SQL = "CREATE TABLE IF NOT EXISTS "+ white_table + " (" + 
			_ID + " INTEGER NOT NULL, " + CONTACT_ID +" TEXT NOT NULL,"+CONTACT_NAME + " TEXT,"+CONTACT_NUMBER + " TEXT NOT NULL,"+CONTACT_PHOTO + " BYTE,"+" PRIMARY KEY (" + _ID+ "));";
	
	//创建关键字表
	private static final String CREATE_KEYWORD_SQL = "CREATE TABLE IF NOT EXISTS "+ KEYWORD_TABLE + " (" + 
			K_ID + " INTEGER NOT NULL, " +CONTENT +" TEXT NOT NULL,"+DATE_TIME + " TEXT," + GET_TYPE + " INTEGER NOT NULL," +"PRIMARY KEY (" + K_ID+ "));";
	
	//创建被拦截电话信息
	private static final String CREATE_PHONEINTERCEPT_SQL = "CREATE TABLE IF NOT EXISTS "+ PHONEINTERCEPT_TABLE + " (" + 
			P_ID + " INTEGER NOT NULL, " + CALLLOG_ID +" TEXT NOT NULL,"+ CALLLOG_NAME + " TEXT,"+ CALLLOG_NUMBER + " TEXT NOT NULL,"+ PHONEINTERCEPT_DATE_TIME + " TEXT," + SYS_TIME + " TEXT," + "PRIMARY KEY (" + P_ID+ "));";
	
	//创建响铃一声电话信息
	private static final String CREATE_RINGONECALL_SQL = "CREATE TABLE IF NOT EXISTS "+ RINGONECALL_TABLE + " (" + 
			R_ID + " INTEGER NOT NULL, " + R_CALLLOG_ID +" INTEGER NOT NULL,"+NUMBER +" TEXT NOT NULL,"+R_DATE_TIME + " TEXT," + "PRIMARY KEY (" +R_ID+ "));";
	
	//创建被拦截短信信息表
	private static final String CREATE_SMSINTERCEPT_SQL = "CREATE TABLE IF NOT EXISTS "+ SMSINTERCEPT_TABLE + " (" + 
			S_ID + " INTEGER NOT NULL, " + S_CONTACT_ID +" TEXT,"+S_CONTACT_NAME +" TEXT,"+S_CONTACT_NUMBER + " TEXT NOT NULL,"+S_SMS_CONTENT + " TEXT NOT NULL,"+S_DATE_TIME + " TEXT," + S_CONTACT_PHOTO + " BYTE,"+S_SYS_TIME + " INTEGER NOT NULL," +"PRIMARY KEY (" + S_ID+ "));";
	
	public MyDatabaseUtil(Context mContext)
	{
		this.mContext = mContext;
	}
	
	//打开数据库
	public void open() throws SQLException
	{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}


	//关闭数据库
	public void close()
	{
		mDatabaseHelper.close();
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper{

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
//			db.execSQL(CREATE_USER); //
			System.out.println("CREATE_MESSAGE_LIBRARY---->"+CREATE_MESSAGE_LIBRARY);
			db.execSQL(CREATE_MESSAGE_LIBRARY); //短信库表
			db.execSQL(CREATE_MESSAGE_FAVORITE); //短信收藏表
			db.execSQL(CREATE_REMIND);
			db.execSQL(CREATE_TABLE_CONTACT_GROUP_REMIND);
			db.execSQL(CREATE_TABLE_SMS_TIMING);
			
			db.execSQL(CREATE_BLACK_SQL);
			db.execSQL(CREATE_WHITE_SQL);
			db.execSQL(CREATE_KEYWORD_SQL);
			db.execSQL(CREATE_PHONEINTERCEPT_SQL);
			db.execSQL(CREATE_RINGONECALL_SQL);
			db.execSQL(CREATE_SMSINTERCEPT_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
   public long deleteAllRemind()
   {
			return mSQLiteDatabase.delete(TABLE_REMIND,  null, null);
   }
	//新建新的提醒
	public long insertRemind(String content,String contact,String partnerIds,long start_time,long end_time,int remind_type, int remind_num , int remind_time ,int repeat_type , int repeat_freq,String repeat_condition,long repeat_start_time, long repeat_end_time)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(REMIND_CONTENT,tranSQLSting(content));
		
		
		initialValues.put(REMIND_CONTACT,contact);
		initialValues.put(REMIND_PARTICIPANT,partnerIds);
		
		initialValues.put(REMIND_START,start_time);
		initialValues.put(REMIND_END,end_time);
		
		initialValues.put(REMIND_TYPE,remind_type);
		initialValues.put(REMIND_NUM, remind_num);
		
		initialValues.put(REMIND_TIME, remind_time);
		
		initialValues.put(REPEAT_TYPE, repeat_type);
		initialValues.put(REPEAT_FREQ, repeat_freq);
		initialValues.put(REPEAT_CONDITION, repeat_condition);
		
		initialValues.put(REPEAT_START_TIME, repeat_start_time);
		initialValues.put(REPEAT_END_TIME, repeat_end_time);
		
		initialValues.put(HAS_REMIND_TIME, 0);
		initialValues.put(TIME_FILTER, "");
		
		return mSQLiteDatabase.insert(TABLE_REMIND, REMIND_ID, initialValues);
	}
	
	//更新提醒
	public long updateRemind(long id,String content,String contact,String partnerIds,long start_time,long end_time,int remind_type, int remind_num , int remind_time ,int repeat_type , int repeat_freq,String repeat_condition,long repeat_start_time, long repeat_end_time)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(REMIND_CONTENT,tranSQLSting(content));
		initialValues.put(REMIND_CONTACT,contact);
		initialValues.put(REMIND_PARTICIPANT,partnerIds);
		
		initialValues.put(REMIND_START,start_time);
		initialValues.put(REMIND_END,end_time);
		
		initialValues.put(REMIND_TYPE,remind_type);
		initialValues.put(REMIND_NUM, remind_num);
		
		initialValues.put(REMIND_TIME, remind_time);
		
		initialValues.put(REPEAT_TYPE, repeat_type);
		initialValues.put(REPEAT_FREQ, repeat_freq);
		initialValues.put(REPEAT_CONDITION, repeat_condition);
		
		initialValues.put(REPEAT_START_TIME, repeat_start_time);
		initialValues.put(REPEAT_END_TIME, repeat_end_time);
		
		initialValues.put(HAS_REMIND_TIME, 0);
		initialValues.put(TIME_FILTER, "");
		
		return mSQLiteDatabase.update(TABLE_REMIND, initialValues, REMIND_ID+"="+id, null);
	}
	
	//更新已经提醒的次数
	public long updateHasRemindNum(long id,int has_remind_time)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(HAS_REMIND_TIME, has_remind_time);
		
		return mSQLiteDatabase.update(TABLE_REMIND, initialValues, REMIND_ID+"="+id, null);
	}
	
	//查询全部提醒
	public Cursor queryAllRemind()
	{
		return mSQLiteDatabase.query(TABLE_REMIND, null , null, null , null, null, null);
	}
	
	//查询指定id的提醒
	public Cursor queryRemind(long id)
	{
		return mSQLiteDatabase.query(TABLE_REMIND, null , REMIND_ID +" = "+id, null , null, null, null);
	}
	
	public Cursor queryRemindByContactId(String contactId)
	{
		String key = "#"+contactId+"#";
		return mSQLiteDatabase.query(TABLE_REMIND, null , REMIND_CONTACT +" LIKE '%"+key+"%' OR " + REMIND_PARTICIPANT + " LIKE '%"+key+"%'", null , null, null, null);
	}
	
    //查询收藏信息
    public Cursor queryRemindContact(){
//    	Cursor mCursor = mSQLiteDatabase.query(TABLE_REMIND, new String[]{REMIND_CONTACT,"COUNT(REMIND_CONTACT_ID) AS COUNT"}, null, null, REMIND_CONTACT, null, REMIND_CONTACT+" desc");
    	Cursor mCursor = mSQLiteDatabase.query(TABLE_REMIND, new String[]{REMIND_CONTACT}, null, null, null, null, null);
    	return mCursor;
    }
	
    
    //删除指定id的提醒
	public long delete(long id)
	{
		return mSQLiteDatabase.delete(TABLE_REMIND,  REMIND_ID +" = "+id, null);
	}
	
	//删除指定联系人id的全部提醒
	public long deleteContactRemind(String contactId)
	{
		return mSQLiteDatabase.delete(TABLE_REMIND,  REMIND_CONTACT +" like '%#"+ contactId +"#%'", null);
	}
	
	//更新时间过滤
	public long updateRemindTimeFilter(long id , long filter_time)
	{
		Cursor c = queryRemind(id);
		String filter_str ;
		
		c.moveToNext();
		filter_str = c.getString(c.getColumnIndex(TIME_FILTER));
		c.close();
		
		filter_str = filter_str + filter_time+",";
		
		System.out.println("  filter_str  --->  " + filter_str);
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(TIME_FILTER,filter_str);
		
		return mSQLiteDatabase.update(TABLE_REMIND, initialValues, REMIND_ID+"="+id, null);
	}
	
	//插入定时短信
	public long insertDataTimeing(long time,String number,String contents)
	{
			ContentValues initialValues = new ContentValues();
			initialValues.put(SMS_TIMING_TIME,time);
			initialValues.put(SMS_TIMING_NUMBER,number);
			initialValues.put(SMS_TIMING_CONTENT,contents);
			return mSQLiteDatabase.insert(TABLE_SMS_TIMING, SMS_TIMING_ID, initialValues);
	}
	//查询定时短信
	public Cursor queryDataTimeing()
	{
		return mSQLiteDatabase.query(TABLE_SMS_TIMING, new String [] {SMS_TIMING_ID,SMS_TIMING_TIME,SMS_TIMING_NUMBER,SMS_TIMING_CONTENT}, null, null, null, null, null);
	}
	//查询定时短信
	public Cursor queryDataTimeingById(long timeingId)
	{
		return mSQLiteDatabase.query(TABLE_SMS_TIMING, new String [] {SMS_TIMING_TIME,SMS_TIMING_NUMBER,SMS_TIMING_CONTENT}, SMS_TIMING_ID + " = "+timeingId, null, null, null, null);
	}
	//删除定时短信
	public long deleteDataTimeing(int timeMessageId)
	{
		return mSQLiteDatabase.delete(TABLE_SMS_TIMING, SMS_TIMING_ID + " = "+timeMessageId, null);
	}
	
	
	//更新定时短信
		public long updateDataTimeing(int timeMessageId,String content,long time)
		{
			
			ContentValues initialValues = new ContentValues();
			initialValues.put(SMS_TIMING_CONTENT, content);
			initialValues.put(SMS_TIMING_TIME, time);
			return mSQLiteDatabase.update(TABLE_REMIND, initialValues, SMS_TIMING_ID+"="+timeMessageId, null);
			
			
		}
	
	//插入短信收藏表
	public long insertDataFavorite(String threadId,String contentId,String content,long time,String sender,String number)
	{
		Cursor c = mSQLiteDatabase.query(MESSAGE_FAVORITE, null, CONTENT_ID + " = '"+contentId +"'" , null, null, null, null);
		
		int size = c.getCount();
		
		c.close();
		
		System.out.println(" size ---> " + size);
		
		if(size ==0) //避免重复收藏
		{
			ContentValues initialValues = new ContentValues();
			initialValues.put(THREAD_ID,threadId);
			initialValues.put(CONTENT_ID,contentId);
			initialValues.put(FAVORITE_CONTENT,content);
			initialValues.put(CONTENT_TIME,time);
			initialValues.put(FAVORITE_SENDER,sender);
			initialValues.put(FAVORITE_NUMBER,number);
			return mSQLiteDatabase.insert(MESSAGE_FAVORITE, FAVORITE_ID, initialValues);
		}else{
			return -1;
		}
	}
	
	//检索收藏短信表   多个号码
	public Cursor queryFavorite(List<String> all_numbers)
	{
		StringBuffer sf = new StringBuffer();
		
		int size = all_numbers.size();
		for(int k = 0;k<size;k++)
		{
			 if(k==size-1)
			{
				sf.append(FAVORITE_NUMBER+"=?");
			}else{
				sf.append(FAVORITE_NUMBER+"=? OR ");
			}
		}
		
		String [] numbers = new String [size];
		
		for(int i =0;i<size;i++)
		{
			String n = all_numbers.get(i);
			numbers[i] = n;
		}
		System.out.println(" queryFavorite numbers ---- > "+numbers);
		return mSQLiteDatabase.query(MESSAGE_FAVORITE, new String []{FAVORITE_ID,THREAD_ID,CONTENT_ID,FAVORITE_CONTENT,CONTENT_TIME,FAVORITE_SENDER,FAVORITE_NUMBER}, sf.toString(), numbers, null, null, null);
	}
	
	public void deleteFavorite(String where)
	{
		mSQLiteDatabase.delete(MESSAGE_FAVORITE, where, null);
	}
	
	
	/**
	 * 查询  指定联系人分组  是否有对应的分组提醒id
	 * @param group_id
	 * @return 
	 */
	public Cursor querGgroupRemindId(long group_id)
	{
		return mSQLiteDatabase.query(TABLE_CONTACT_GROUP_REMIND, new String [] {CONTACT_GROUP_REMIND_ID,CONTACT_GROUP_ID,CONTACT_GROUP_TIME_GAP}, CONTACT_GROUP_ID + " = " +  group_id , null, null, null, null);
	}
	
	public Cursor queryGroupRmind(long group_remind_id)
	{
		return mSQLiteDatabase.query(TABLE_CONTACT_GROUP_REMIND, new String [] {CONTACT_GROUP_REMIND_ID,CONTACT_GROUP_ID,CONTACT_GROUP_TIME_GAP}, CONTACT_GROUP_REMIND_ID + " = " +  group_remind_id , null, null, null, null);
	}
	
	/**
	 * 插入新的联系人分组提醒
	 * @param group_id
	 * @param time_gap
	 * @return
	 */
	public long  insertGroupRemind(long group_id, long time_gap)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(CONTACT_GROUP_ID, group_id);
		initialValues.put(CONTACT_GROUP_TIME_GAP, time_gap);
		
		return mSQLiteDatabase.insert(TABLE_CONTACT_GROUP_REMIND, CONTACT_GROUP_REMIND_ID, initialValues);
	}
	
	public long deleteGroupRemind(int group_remind_id)
	{
		return mSQLiteDatabase.delete(TABLE_CONTACT_GROUP_REMIND, CONTACT_GROUP_REMIND_ID + " = " + group_remind_id, null);
	}
	
	/**
	 * 更新联系人分组的提醒
	 * @param group_remind_id
	 * @param group_id
	 * @param time_gap
	 * @return
	 */
	public long updateGroupRemind(int group_remind_id,long group_id, long time_gap)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(CONTACT_GROUP_ID, group_id);
		initialValues.put(CONTACT_GROUP_TIME_GAP, time_gap);
		
		return mSQLiteDatabase.update(TABLE_CONTACT_GROUP_REMIND, initialValues, CONTACT_GROUP_REMIND_ID + " = " + group_remind_id , null);
	}
	
	//插入短信内置库
	public long insertDataMessage(String content,String category,String date)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(MESSAGE_CONTENT,content);
		initialValues.put(MESSAGE_CATEGORY,category);
		initialValues.put(MESSAGE_DATE,date);
		return mSQLiteDatabase.insert(MESSAGE_LIBRARY, MESSAGE_ID, initialValues);
	}
	
	//-----------------------插入数据 start---------------------------------//	
	//用户表
	
    /**
     * 用户表插入数据
     * @param userid
     * @param username
     * @param password
     * @param state
     * @param del1 (备用字段)
     * @param del2 (备用字段)
     * @param del2 (备用字段)
     * @return
     */
	public long insertDataUser(String userid,String username,String password,String state,String wapsessionid,String cn_name,String en_name,String nick_name,String school_name,String level,String school_class,String email,String contact,String head,String notice,String del1,String del2,String del3)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(USERID,userid);
		initialValues.put(USERNAME,username);
		initialValues.put(PASSWORD,password);
		
		return mSQLiteDatabase.insert(USER, KEY_ID, initialValues);
	}
	
	//-----------------------根据id删除数据start--------------------------//	
	/**
	 * 根据 id 删除数据
	 * @param key_id
	 * @return 
	 */
	public boolean deleteByIdUser(String u_id)
	{
		return mSQLiteDatabase.delete(USER,USERID+"="+u_id+"",null) >0;
	}
	
	

	//-----------------------根据key删除数据start--------------------------//	
	/**
	 * 根据userid删除数据
	 * @param userid
	 * @return
	 */
	public boolean deleteByUserIdUser(String userid)
	{
		String user_id = tranSQLSting(userid);//需要转码
		return mSQLiteDatabase.delete(USER,USERID+"='"+user_id+"'",null) >0;
	}
	
	
	
	/**
	 * 删除表
	 * @param name
	 */
	public void dropTable(String name)
	{
		String sql = "DROP TABLE "+name;
		mSQLiteDatabase.execSQL(sql);
	}
	
	//-------------------更新数据start--------------------------//	
	/**
	 * 更新数据
	 * @param userid
	 * @param username
	 * @param password
	 * @param state
	 * @param del1
	 * @param del2
	 * @param del3
	 * @return
	 */
	public boolean updateDataUser(String userid,String username,String password,String state,String del1,String del2,String del3)
	{
		ContentValues args = new ContentValues();
		args.put(USERNAME, username);
		args.put(PASSWORD,password);
		args.put(DEL1,del1);
		args.put(DEL2,del2);
		args.put(DEL3,del3);
		
		return mSQLiteDatabase.update(USER, args, USERID+"="+userid, null)>0;
	}
	
	public Cursor fetchMessageLibrary(String category)
    {
    	String categorys = tranSQLSting(category);
    	Cursor mCursor=mSQLiteDatabase.query(MESSAGE_LIBRARY, new String [] {MESSAGE_ID,MESSAGE_CONTENT}, MESSAGE_CATEGORY+" = "+categorys, null, null, null, null);
    	return mCursor;
    }  
	
	public Cursor fetchLibraryContentFirst(String key_content)
    {
    	String key_contents = tranSQLSting(key_content);
    	Cursor mCursor=mSQLiteDatabase.query(MESSAGE_LIBRARY, new String [] {MESSAGE_ID,MESSAGE_CONTENT}, MESSAGE_CONTENT+" LIKE "+"'"+key_contents+"%'", null, null, null, null);
    	return mCursor;
    }  
	public Cursor fetchLibraryContentSecond(String key_content)
    {
    	String key_contents = tranSQLSting(key_content);
    	Cursor mCursor=mSQLiteDatabase.query(MESSAGE_LIBRARY, new String [] {MESSAGE_ID,MESSAGE_CONTENT}, MESSAGE_CONTENT+" LIKE "+"'%"+key_contents+"%'", null, null, null, null);
    	return mCursor;
    }  
	public Cursor fetchLibraryContentThird(String key_content)
    {
    	String key_contents = tranSQLSting(key_content);
    	Cursor mCursor=mSQLiteDatabase.query(MESSAGE_LIBRARY, new String [] {MESSAGE_ID,MESSAGE_CONTENT}, MESSAGE_CONTENT+" LIKE "+"'%"+key_contents+"'", null, null, null, null);
    	return mCursor;
    }  
	
	public Cursor fetchMessageFavorite()
    {
    	Cursor mCursor=mSQLiteDatabase.query(MESSAGE_FAVORITE, new String [] {THREAD_ID,CONTENT_ID,FAVORITE_CONTENT,CONTENT_TIME,FAVORITE_SENDER,FAVORITE_NUMBER}, null, null, null, null, null);
    	return mCursor;
    }   
	
    public Cursor fetchDataUser(String username)
    {
    	String name = tranSQLSting(username);
    	Cursor mCursor=mSQLiteDatabase.query(USER, new String [] {KEY_ID,USERID,USERNAME,PASSWORD}, USERNAME+" LIKE "+"'%"+name+"%'", null, null, null, null);
    	return mCursor;
    }   
    
	
    //查询收藏信息
    public Cursor queryMessageSender(){
    	Cursor mCursor = mSQLiteDatabase.query(MESSAGE_FAVORITE, new String[]{FAVORITE_SENDER,"COUNT(favorite_send) AS sendNum",FAVORITE_NUMBER}, null, null, FAVORITE_SENDER, null, FAVORITE_SENDER+" asc");
    	return mCursor;
    }
    
    public Cursor queryMessageFavorite(String name){
    	Cursor mCursor = mSQLiteDatabase.query(MESSAGE_FAVORITE, null, FAVORITE_SENDER+"='"+name+"'", null, null, null, null);
    	return mCursor;
    }
    
    //删除收藏信息
    public boolean deleteByIdFavorite(String f_id)
	{
		return mSQLiteDatabase.delete(MESSAGE_FAVORITE,FAVORITE_ID+" = "+f_id,null) >0;
	} 
    
    //查询黑名单
    public Cursor queryAllBlack() {
    	Cursor black = mSQLiteDatabase.query(black_table, null, null, null, null, null, null);
    	return black;
    }
    
    //查询白名单
    public Cursor queryAllWhite() {
    	Cursor white = mSQLiteDatabase.query(white_table, null, null, null, null, null, null);
    	return white;
    }
    
    //根据号码查询黑名单
    public Cursor queryBlack(String number) {
    	Cursor black = mSQLiteDatabase.query(black_table, null, CONTACT_NUMBER+"='"+PhoneNumberTool.cleanse(number)+"'", null, null, null, null);
    	return black;
    }
    
    //根据号码查询白名单
    public Cursor queryWhite(String number) {
    	Cursor white = mSQLiteDatabase.query(white_table, null, CONTACT_NUMBER+"='"+PhoneNumberTool.cleanse(number)+"'", null, null, null, null);
    	return white;
    }
    
	//删除黑名单
    public boolean deleteBlack(String number) {
    	return mSQLiteDatabase.delete(black_table, CONTACT_NUMBER+"='"+PhoneNumberTool.cleanse(number)+"'", null) > 0;
    }
    
    //删除白名单
    public boolean deleteWhite(String number) {
    	return mSQLiteDatabase.delete(white_table, CONTACT_NUMBER+"='"+PhoneNumberTool.cleanse(number)+"'", null) > 0;
    }
    
    //添加黑名单
    public long insertBlack(String number)
	{
    	
    	String[] data = PhoneNumberTool.getContactInfo(mContext, number);
    	
    	long id = 0;
    	if (data[2] != null) {
    		id = Long.parseLong(data[2]);
    	}
    	
		ContentValues values = new ContentValues();
		values.put(CONTACT_ID, id);
		values.put(CONTACT_NUMBER, PhoneNumberTool.cleanse(number));
		
//		if( id != null){
//			values.put(CONTACT_NAME, contactBean.getNick());
//			values.put(CONTACT_NUMBER, PhoneNumberTool.cleanse(contactBean.getNumber()));
//			values.put(CONTACT_PHOTO, contactBean.getPhoto());
//		} else {
//			values.put(CONTACT_ID, "");
//			values.put(CONTACT_NAME, "");
//			values.put(CONTACT_PHOTO, "");		
//		}
		
		return mSQLiteDatabase.insert(black_table, _ID, values);
	}
    
    //添加白名单
    public long insertWhite(String number)
	{
    	String[] data = PhoneNumberTool.getContactInfo(mContext, number);
    	
    	String id = null;
    	if (data[2] != null) {
    		id = data[2];
    	}
    	
		ContentValues values = new ContentValues();
		values.put(CONTACT_ID, id != null ? id : "");
		values.put(CONTACT_NUMBER, PhoneNumberTool.cleanse(number));
		
//		if( id != null){
//			
//			values.put(CONTACT_NAME, contactBean.getNick());
//			values.put(CONTACT_NUMBER, PhoneNumberTool.cleanse(contactBean.getNumber()));
//			values.put(CONTACT_PHOTO, contactBean.getPhoto());
//		} else {
//			values.put(CONTACT_ID, "");
//			values.put(CONTACT_NAME, "");
//			values.put(CONTACT_PHOTO, "");		
//		}
		
		return mSQLiteDatabase.insert(white_table, _ID, values);
	}
    
    public int saveBlack(String number){
		
		Cursor contact = queryBlack(number);
		
		int count = contact.getCount();
		
		if(count == 0){
			
			Cursor white = queryWhite(number);
			
			if (white.getCount() > 0) {
				
				deleteWhite(number);
			}
			
			white.close();
			
			insertBlack(number);
			
								
		}
		contact.close();
			
		return count;
	}
	
	public int saveWhite(String number){
		
		Cursor contact = queryWhite(number);
		
		int count = contact.getCount();
		
		if( count == 0){
			
			Cursor black = queryBlack(number);
			
			if (black.getCount() > 0) {
				
				deleteBlack(number);
				
			}
			
			black.close();
		
			insertWhite(number);
			
		}
		contact.close();
	
		return count;
	}
	
	//初始化一条关键字数据
	public long firstAddKeyWord(){
		
		ContentValues values = new ContentValues();
		values.put(CONTENT, "毒品");
		SimpleDateFormat df = new SimpleDateFormat("MM月dd日");
		values.put(DATE_TIME, df.format(new Date()));
		values.put(GET_TYPE, 0);
		
		return mSQLiteDatabase.insert(KEYWORD_TABLE, K_ID, values);
	}
	//添加关键字
	public long addKeyWord(String keyword,String date,int type){
		
		ContentValues values = new ContentValues();
		values.put(CONTENT, keyword);
		values.put(DATE_TIME, date);
		values.put(GET_TYPE, type);
		
		return mSQLiteDatabase.insert(KEYWORD_TABLE, K_ID, values);
	}
	//查询关键字
	public List<KeywordEntity> queryKeyWord(){
		
		List<KeywordEntity> keywordList = new ArrayList<KeywordEntity>();
		
		Cursor cursor = mSQLiteDatabase.query(KEYWORD_TABLE, null, null, null, null, null, K_ID + " desc");
		
		if (cursor.moveToFirst()) {
			
			do {
				
				int id = cursor.getInt(cursor.getColumnIndex(K_ID));
				String content = cursor.getString(cursor.getColumnIndex(CONTENT));
				
				KeywordEntity keywordEntity = new KeywordEntity();
				
				keywordEntity.setId(id);
				keywordEntity.setContent(content);
				
				keywordList.add(keywordEntity);
				
			} while (cursor.moveToNext());
			
		}
		cursor.close();
		return keywordList;
	}
	//删除关键字
	public long deleteKeyWord(int keyword){
		return mSQLiteDatabase.delete(KEYWORD_TABLE, K_ID+"="+keyword, null);
	}
	
	//添加被拦截电话信息
	public long insertPhoneIntercept(long calllog_id,String name,String incomingNumber,String date,long time){
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm");
		
		ContentValues contentValues = new ContentValues();
		contentValues.put(CALLLOG_ID, calllog_id);
		contentValues.put(CALLLOG_NAME, name);
		contentValues.put(CALLLOG_NUMBER, incomingNumber);
		contentValues.put(PHONEINTERCEPT_DATE_TIME, df.format(new Date(System.currentTimeMillis())));
		contentValues.put(SYS_TIME, System.currentTimeMillis());
		
		return mSQLiteDatabase.insert(PHONEINTERCEPT_TABLE, P_ID, contentValues);
	}
	
	//查询所有被拦截电话信息
	public List<CallLogInfo> queryPhoneIntercept(){
		
		List<CallLogInfo> list = new ArrayList<CallLogInfo>();
		
		Cursor c = mSQLiteDatabase.query(PHONEINTERCEPT_TABLE, null, null, null, null, null, SYS_TIME + " desc");
		
		if (c.moveToFirst()) {
			
			do {
				
				CallLogInfo calllog = new CallLogInfo();
				
				calllog.setId(c.getInt(c.getColumnIndex(P_ID)));
				calllog.setmCall_type(c.getString(c.getColumnIndex(CALLLOG_ID))); //此处绑定为callLog id
				calllog.setmCaller_name(c.getString(c.getColumnIndex(CALLLOG_NAME)));
				calllog.setmCaller_number(PhoneNumberTool.cleanse(c.getString(c.getColumnIndex(CALLLOG_NUMBER))));
				calllog.setmCall_date(c.getString(c.getColumnIndex(PHONEINTERCEPT_DATE_TIME)));
				
				list.add(calllog);
				
			} while (c.moveToNext());
			
		}
		c.close();
		return list;
	}

	//根据id删除被拦截电话信息
	public long deletePhoneIntercept(String id) {
		
		return mSQLiteDatabase.delete(PHONEINTERCEPT_TABLE, P_ID+" = '"+ id + "'", null);
	}
	
	//查询响铃一声电话通话记录Id
	public String queryCallLogId(){
		String id = "";
		
		Cursor cursor = mSQLiteDatabase.query(RINGONECALL_TABLE, new String[]{R_CALLLOG_ID}, null, null, null, null, null);
		
		if (cursor.moveToFirst()) {
			
			do {
				
				String c_id = cursor.getString(cursor.getColumnIndex(R_CALLLOG_ID));
				
				if(c_id != null)
					id = c_id+",";
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		
		return id;
	}
	
	//添加响铃一声信息
	public long insertRingOneCall(long id,String number) {
	
		ContentValues values = new ContentValues();
		
		values.put(R_CALLLOG_ID, id);
		values.put(NUMBER, number);
		values.put(R_DATE_TIME, System.currentTimeMillis());
		
		return mSQLiteDatabase.insert(RINGONECALL_TABLE, R_ID, values);
	}
	
	//查询所有被拦截短信信息
	public List<SmsContent> querySmsIntercept() {
		List<SmsContent> list = new ArrayList<SmsContent>();
		
		Cursor cursor = mSQLiteDatabase.query(SMSINTERCEPT_TABLE, null, null, null, null, null, " _date_time desc");
		
		if (cursor.moveToFirst()) {
			
			do {
				
				SmsContent smsContent = new SmsContent();
				
				smsContent.setId(cursor.getLong(cursor.getColumnIndex(S_ID)));
//				smsContent.setThread_id(cursor.getLong(cursor.getColumnIndex(smsInterceptDBHelper.THREAD_ID)));
//				smsContent.setSubject(cursor.getString(cursor.getColumnIndex(smsInterceptDBHelper.SMS_ID)));//此处存储为短信ID
				smsContent.setPerson_id(cursor.getString(cursor.getColumnIndex(S_CONTACT_ID)));
				smsContent.setSms_number(cursor.getString(cursor.getColumnIndex(S_CONTACT_NUMBER)));
				smsContent.setSms_body(cursor.getString(cursor.getColumnIndex(S_SMS_CONTENT)));
				smsContent.setDate(cursor.getLong(cursor.getColumnIndex(S_DATE_TIME)));
				smsContent.setSystemTime(cursor.getLong(cursor.getColumnIndex(S_SYS_TIME)));
				smsContent.setPhoto(cursor.getBlob(cursor.getColumnIndex(S_CONTACT_PHOTO)));
				smsContent.setSms_name(cursor.getString(cursor.getColumnIndex(S_CONTACT_NAME)));
				
				list.add(smsContent);
				
			} while (cursor.moveToNext());
		}
		
		cursor.close();
		return list;
	}
	
	//根据id删除被拦截的短信信息
	public long deleteSmsIntercept(String id) {
		
		return mSQLiteDatabase.delete(SMSINTERCEPT_TABLE, S_ID+" = '" + id + "'", null);
	}
	
	//添加被拦截的短信信息
	public long addSmsIntercept(String contact_id,String address,String body,String date,byte[] photoicon,String display_name) {
		
		ContentValues values = new ContentValues();
		
		values.put(S_CONTACT_ID, contact_id);
		values.put(S_CONTACT_NUMBER, address);
		values.put(S_SMS_CONTENT, body);
		
		SimpleDateFormat df = new SimpleDateFormat("MM/dd hh:mm");
		
		values.put(S_DATE_TIME, df.format(new Date(System.currentTimeMillis())));
		values.put(S_SYS_TIME,System.currentTimeMillis());
		values.put(S_CONTACT_PHOTO, photoicon);
		values.put(S_CONTACT_NAME, display_name);
		
		return mSQLiteDatabase.insert(SMSINTERCEPT_TABLE, S_ID, values);
	}
	
	
	/**
	 * 转义符
	 *
	 */
	public String tranSQLSting(String str)
	{
		String [] parts=str.split("'");
		String str_tran ="";
		for(int i =0;i<parts.length;i++)
		{
			if(i==0)
			{
				str_tran+=parts[i];
			}else{
				str_tran+="''"+parts[i];
			}
		}
		return str_tran;
	}

}

