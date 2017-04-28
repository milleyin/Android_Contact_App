package com.dongji.app.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dongji.app.entity.CallLogInfo;
import com.dongji.app.entity.ContactBean;
import com.dongji.app.entity.ContactBean.EmailInfo;
import com.dongji.app.entity.ContactBean.PhoneInfo;
import com.dongji.app.entity.NetWorkResult;
import com.dongji.app.entity.RemindBean;
import com.dongji.app.entity.SmsContent;

/**
 * 网络服务单元
 * @author 
 *
 */
public class NetworkUnit {
	public static final String SUCCESS= "1";//登陆成功
	public static final String FAILURE= "0";//登陆失败
	public static final String NOTLOGIN="-1";//未登录http://192.168.1.171/wuxiuwu/index.php?g=api&m=message&a=opt
	private HttpPost httpPost;
	private HttpResponse httpResponse;
//	private String BASE_URL = "http://192.168.1.200/cms/index.php?g=api&m=Message&a=Opt";  
	private String BASE_URL = "http://www.91dongji.com/index.php?g=api&m=Message&a=Opt";  
//	private String BASE_URL = "http://192.168.1.171/index.php?g=api&m=Message&a=Opt";  
//	private String BASE_URL = "http://192.168.1.171/cms/index.php?g=api&m=Message&a=Opt"; 
	private String ACTION_STR="opt";
	public static final String SUCCESS_RES = "success";
	public static final String SUCCESS_STATUS = "status";
	public static final String SUCCESS_COOKIE = "cookie";
	public static String ACTION_LOGIN = "login";//登陆
	public static String ACTION_MARKET_UID = "market_uid";//登陆
	public static String ACTION_REGISTER = "register";//登陆
	public static String ACTION_ISEXPIRE = "isexpire";//登陆

	//登录注册
	public static String ACTION_EMAIL="email";
	public static String ACTION_PASSWORD="password";
	public static String ACTION_RPASSWORD="rpassword";
	
	
	public static String ACTION_VERSION_UPDATE = "update";//版本升级
	public static String ACTION_POST_SESSION = "post_session";
	public static String ACTION_POST_VERSION_NAME="package";
	public static String ACTION_POST_VERSION_INFO="code";
	
	
	public static String ACTION_POST_CONTACT_RECOVERY = "recovery";//恢复联系人
	public static String ACTION_POST_MESSAGE_RECOVERY = "message_recovery";//恢复联系人
	public static String ACTION_POST_CALLRECORD_RECOVERY = "callrecord_recovery";//恢复联系人
	public static String ACTION_POST_FAVORITE_RECOVERY = "favorite_recovery";//恢复联系人
	public static String ACTION_POST_REMIND_RECOVERY = "remind_recovery";//恢复联系人
	
	public static String ACTION_POST_ACT= "act";
	public static String ACTION_POST_SESSIONID = "sessionid";
	public static String ACTION_POST_BACK = "back";
	public static String ACTION_POST_UID = "market_uid";
	public static String ACTION_POST_DATA = "data";
	public static String ACTION_POST_TYPE = "type";
	public static String ACTION_POST_OPT = "opt";
	public static String ACTION_POST_BACKUP = "backup";
	public static String ACTION_POST_CONTACT = "backup_contact";
	public static String ACTION_POST_MESSAGE = "backup_message";
	public static String ACTION_POST_CALLRECORD = "backup_callrecord";
	public static String ACTION_POST_FAVORITE = "backup_favorite";
	public static String ACTION_POST_REMIND = "backup_remind";
	
	private String USER_NAME_STR="loginCode";
	private String PASSWORD_STR = "password";
	public static final String UID= "uid";
	public static final String WAPSESSIONID= "wapsessionid";
	
	
	public static final String PAGE_STR = "page";
	public static final String CURPAGE_STR = "curpage";
	public static final String DATA_STR = "data";
	public static final String DATA_CONTACT = "contact";
	public static final String DATA_MESSAGE = "message";
	public static final String DATA_RECORD = "callrecord";
	public static final String DATA_REMIND = "remind";
	public static final String DATA_FAVORITE = "favorite";
	public static final String NOTICE_DATA = "notice_data";
	public static final String ANNOUNCE_DATA = "announce_data";
	//flash文件信息 end
	public static final String SERACH_WORD = "word";
	//获取我的分数信息 start
	
	public static final String MYPOING_YEAR = "year";//年
	public static final String MYPOING_MONTH = "month";//月
	
	//获取我的分数信息 end
	
	//排行榜 start
	public static final String TOP_TYPE = "type";//类型
	public static final String TOP_LEVEL = "level";//类型
	public static final String TOP_SCHOOL = "school";//类型
	//排行榜 end
	public static final String NOTICE_DATE = "notice_date";//类型 客户端已缓存的最新一条notice的日期，没有则留空
	public static final String ANNOUNCE_DATE = "announce_date";//客户端已缓存的最新一条announce的日期，没有则留空
	//参数
	private List<NameValuePair> nvps = new ArrayList<NameValuePair>();

	public NetworkUnit(){}
	
	private NetWorkResult result; //结果数据类
	/**
	 * 登录action标识
	 */
	public static final String ACTION_TOP_STUDENT_INFOMATION = "infomation";
	public static final String ACTION_PHONETICS = "phonetics";
	public static final String ACTION_SERACH = "search";
	
	/**
	 * 执行  
	 * @return 执行成功   返回相应的json数据 字符串  ;  执行失败  返回null
	 */
	public String execute() throws Exception {
		
			DefaultHttpClient httpClient = NetworkService.getHttpClient();
			httpPost = new HttpPost(BASE_URL);
			
			//添加参数
			if (nvps != null && nvps.size() > 0) {
				httpPost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
			}
			
			httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
	    	//请求超时
	    	httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
			httpResponse = httpClient.execute(httpPost);
			
			if(httpResponse!=null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			{
				//获取json数据
				String resultStr = stream2String(httpResponse.getEntity().getContent());
				
				System.out.println("resultStr is --->" + resultStr);
				
				return resultStr;
			}
			
		   return null;
	}
	

	
//-----------------------具体的每一项网络操作都往外抛出异常---------------------------------//
	/**
	 * 版本升级
	 */
	public NetWorkResult updateVersion(String name, String version)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_VERSION_UPDATE));
		nvps.add(new BasicNameValuePair(ACTION_POST_VERSION_NAME,name));
		nvps.add(new BasicNameValuePair(ACTION_POST_VERSION_INFO,version));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		result=new NetWorkResult();
		if(resultStr!=null)
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result.setSuccess(success);
			if("1".equals(success))
			{
				
				result.setVersion_number(jobj.getString("versioncode"));
				result.setDownload_url(jobj.getString("down_url"));
				result.setSend_date(jobj.getString("updatetime"));
			}else
			{
				
			}
			
			    
		}
		
		return result;
	}
	
	/**
	 * 登录
	 * 0:邮箱或密码不能为空
	  -1:用户不存在
	  -2:密码错误
	  -3:登陆失败
	 */
	public NetWorkResult login(String email,String password,String imei)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_LOGIN));
		nvps.add(new BasicNameValuePair(ACTION_EMAIL,email));
		nvps.add(new BasicNameValuePair(ACTION_PASSWORD,password));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
//		nvps.add(new BasicNameValuePair(ACTION_PASSWORD,SUCCESS_COOKIE));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		JSONObject jobj = new JSONObject(resultStr);
		String success=jobj.getString(SUCCESS_RES);
		result=new NetWorkResult();
		if("10000".equals(success))
		{
			String cookie=jobj.getString(SUCCESS_COOKIE);
			String backupSuccess=jobj.getString("backup");
			result.setBackUpSuccess(backupSuccess);
			result.setCookieid(cookie);
		}
		result.setSuccess(success);
		return result;
	}
	/**
	 *判断cookie有没有过期
	 * 0:未过期
       1:过期
	 */
	public NetWorkResult isLoginCookie(String cookie)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_ISEXPIRE));
		nvps.add(new BasicNameValuePair(ACTION_MARKET_UID,cookie));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		result=new NetWorkResult();
		if(resultStr!=null && !"".equals(resultStr))
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString("expire");
			
			result.setSuccess(success);
		}
		
		return result;
	}
	/**
	 * 注册
	 *  0:邮箱或密码不能为空
		-1:用户名不合法
		-2:包含不允许注册的词语
		-3:用户名已经存在
		-4:Email 格式有误
		-5:Email 不允许注册
		-6:该 Email 已经被注册
		-7:注册失败
		-9:两次密码不一致
		-10:密码长度在6-15位之间
		10000成功
		cookie
	 */
	public NetWorkResult register(String email,String password,String repassword,String imei)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_REGISTER));
		nvps.add(new BasicNameValuePair(ACTION_EMAIL,email));
		nvps.add(new BasicNameValuePair(ACTION_PASSWORD,password));
		nvps.add(new BasicNameValuePair(ACTION_RPASSWORD,repassword));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		result=new NetWorkResult();
		if(resultStr!=null &&!"".equals(resultStr) )
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			
			if("10000".equals(success))
			{
				String backupSuccess=jobj.getString("backup");
				String cookie=jobj.getString(SUCCESS_COOKIE);
				result.setBackUpSuccess(backupSuccess);
				result.setCookieid(cookie);
			}
			result.setSuccess(success);
		}
		
		return result;
	}
	/**
	   message_data:[{  列表数据，数组形式
	         "message_id" : "联系人id",
	         "message_number" : "电话号码",
	         "message_time" : "发送者电话号码",
	         "message_type" : "接收者电话号码",
	         "message_content" : "短信内容",
	        
	        },{  列表数据，数组形式
	         "message_id" : "联系人id",
	          "message_number" : "电话号码",
	         "message_time" : "发送者电话号码",
	         "message_type" : "接收者电话号码",
	         "message_content" : "短信内容",
	          }
	        ]
	/**
	 * 备份短信
	 */
	public NetWorkResult postCloundMessageBackup(ArrayList<SmsContent> cArrayList,String uid,boolean isBack,String imei)throws Exception
	{
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		for(int i=0;i<cArrayList.size();i++) {
			SmsContent smsContent=cArrayList.get(i);
			sb.append("{\"message_id\":\"").
			append(String.valueOf(smsContent.getId())).
			append("\",\"message_number\":\"").
			append(smsContent.getSms_number()).
			append("\",\"message_time\":\"").
			append(smsContent.getDate()).
			append("\",\"message_type\":\"").
			append(smsContent.getTypeId()).
			append("\",\"message_content\":\"").
			append(smsContent.getSms_body()).
			append("\"}");
			if(i<cArrayList.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		nvps.add(new BasicNameValuePair(ACTION_POST_OPT,ACTION_POST_MESSAGE));
		nvps.add(new BasicNameValuePair(ACTION_POST_DATA,sb.toString()));
		nvps.add(new BasicNameValuePair(ACTION_POST_BACK,isBack?"0":"1"));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID,uid));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		if(resultStr!=null && !"".equals(resultStr))
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result=new NetWorkResult();
			if("1".equals(success)&& isBack)
			{
				String state=jobj.getString(SUCCESS_STATUS);
				result.setState(state);
			}
			result.setSuccess(success);
		}
		
		
		return result;
	}
	/*
	*//**
	 * 备份联系人
	 *//*
	public NetWorkResult postCloundContactBackup(ArrayList<ContactBean> cArrayListContact,ArrayList<SmsContent> cArrayListMessage,ArrayList<RemindBean> cArrayListRemind,ArrayList<CallLogInfo> cArrayListCallLogInfo,ArrayList<SmsContent> cArrayListFavorite,String uid,String imei)throws Exception
	{
		JSONObject jObject = new JSONObject();
		JSONArray jaonContact = new JSONArray();
		StringBuilder sb=new StringBuilder();
		sb.append("{\"contact\":\"");
		sb.append("[");//通话记录
		for(int i=0;i<cArrayListContact.size();i++) {
			ContactBean contactBean=cArrayListContact.get(i);
			sb.append("{contact_id :").
			append(contactBean.getContact_id()).
			append(",").
			append("contact_name :").
			append(contactBean.getNick()).
			append(",").
			append("contact_number :").
			append(contactBean.getNumber()).
			append(",").
			append("contact_image ").
			append(contactBean.getPhoto_id()).
			append(",").
			append("contact_organizations ").
			append(contactBean.getOrganizations()).
			append(",").
			append("contact_birthrday ").
			append(contactBean.getBrithday()).
			append(",").
			append("contact_email ").
			append(contactBean.getEmail()).
			append(",").
			append("contact_address ").
			append(contactBean.getAddress()).
			append(",").
			append("contact_website ").
			append(contactBean.getWebsite()).
			append(",").
			append("contact_notes ").
			append(contactBean.getNotes()).
			append("}");
			if(i<cArrayListContact.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("\"");
		sb.append(",");
		sb.append("\"message\":\"");
		sb.append("[");//短信
		for(int i=0;i<cArrayListMessage.size();i++) {
			SmsContent smsContent=cArrayListMessage.get(i);
			sb.append("{message_id :").
			append(String.valueOf(smsContent.getId())).
			append(",").
			append("message_number ").
			append(smsContent.getSms_number()).
			append(",").
			append("message_time ").
			append(smsContent.getSms_date()).
			append(",").
			append("message_type ").
			append(smsContent.getTypeId()).
			append(",").
			append("message_content ").
			append(smsContent.getSms_body()).
			append(" }");
			if(i<cArrayListMessage.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("\"");
		sb.append(",");
		sb.append("\"remind\":\"");
		sb.append("[");//提醒
		for(int i=0;i<cArrayListRemind.size();i++) {
			RemindBean remindBean=cArrayListRemind.get(i);
			sb.append("{remind_id :").
			append(remindBean.getId()).
			append(",").
			append("remind_content :").
			append(remindBean.getContent()).
			append(",").
			append("remind_participants :").
			append(remindBean.getParticipants()).
			append(",").
			append("remind_start_time :").
			append(remindBean.getStart_time()).
			append(",").
			append("remind_end_time :").
			append(remindBean.getEnd_time()).
			append(",").
			append("remind_remind_type :").
			append(remindBean.getRemind_type()).
			append(",").
			append("remind_remind_num :").
			append(remindBean.getRemind_num()).
			append(",").
			append("remind_remind_time :").
			append(remindBean.getRemind_time()).
			append(",").
			append("remind_repeat_type :").
			append(remindBean.getRepeat_type()).
			append(",").
			append("remind_repeat_fre :").
			append(remindBean.getRepeat_fre()).
			append(",").
			append("remind_repeat_start_time :").
			append(remindBean.getRepeat_start_time()).
			append(",").
			append("remind_repeat_end_time :").
			append(remindBean.getRepeat_end_time()).
			append(",").
			append("remind_repeat_count :").
			append(remindBean.getCount()).
			append("}");
			if(i<cArrayListRemind.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("\"");
		sb.append(",");
		sb.append("\"callrecord\":\"");
		sb.append("[");//通话记录
		for(int i=0;i<cArrayListCallLogInfo.size();i++) {
			CallLogInfo callLogInfo=cArrayListCallLogInfo.get(i);
			sb.append("{call_record_id ").
			append(callLogInfo.getId()).
			append("call_record_name ").
			append(callLogInfo.getmCaller_name()).
			append("call_record_number ").
			append(callLogInfo.getmCaller_number()).
			append("call_record_type ").
			append(callLogInfo.getmCall_type()).
			append("call_record_time ").
			append(callLogInfo.getmCall_date()).
			append("\",\"call_record_duration\":\"").
			append(callLogInfo.getmCall_duration()).
			append("\"}");
			if(i<cArrayListCallLogInfo.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("\"");
		sb.append(",");
		sb.append("\"favorite\":\"");
		sb.append("[");//收藏
		for(int i=0;i<cArrayListFavorite.size();i++) {
			SmsContent sContent=cArrayListFavorite.get(i);
			sb.append("{message_thread_id :").
			append(sContent.getThread_id()).
			append(",").
			append("message_content_id :").
			append(sContent.getSms_content_id()).
			append(",").
			append("message_body :").
			append(sContent.getSms_body()).
			append(",").
			append("message_date :").
			append(sContent.getSms_date()).
			append(",").
			append("message_number :").
			append(sContent.getSms_number()).
			append("}");
			if(i<cArrayListFavorite.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		sb.append("\"");
		sb.append("}");
		nvps.add(new BasicNameValuePair(ACTION_POST_OPT,ACTION_POST_BACKUP));
		nvps.add(new BasicNameValuePair(ACTION_POST_DATA,sb.toString()));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID,uid));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		result=new NetWorkResult();
		if(resultStr!=null)
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			
			
		
			result.setSuccess(success);
			if("1".equals(success))
			{
				String state=jobj.getString(SUCCESS_STATUS);
				result.setState(state);
			}
		}
		
		return result;
	}
	*/
	
	/**
	 * 备份联系人
	 */
	public NetWorkResult postCloundContactBackup(ArrayList<ContactBean> cArrayListContact,ArrayList<SmsContent> cArrayListMessage,ArrayList<RemindBean> cArrayListRemind,ArrayList<CallLogInfo> cArrayListCallLogInfo,ArrayList<SmsContent> cArrayListFavorite,String uid,String imei)throws Exception
	{
		JSONObject jObject = new JSONObject();
		JSONArray jaonContact = new JSONArray();
		JSONArray jaonMessage= new JSONArray();
		JSONArray jaonRemind= new JSONArray();
		JSONArray jaonCallRecord= new JSONArray();
		JSONArray jaonCallFavorite= new JSONArray();
		for(int i=0;i<cArrayListContact.size();i++) {
			ContactBean contactBean=cArrayListContact.get(i);
			String images=null;
			if(contactBean.getPhoto()!=null)
			{
				images = new String(contactBean.getPhoto(),"ISO-8859-1");
			}
			
			JSONObject j = new JSONObject();
			j.put("contact_id", contactBean.getContact_id()!=null?contactBean.getContact_id():"");
			j.put("contact_name", contactBean.getNick()!=null?contactBean.getNick():"");
			
			j.put("contact_image", images!=null?images:"");
//			j.put("contact_image", contactBean.getPhoto_id()!=null?contactBean.getPhoto_id():"");
			j.put("contact_organizations", contactBean.getOrganizations()!=null?contactBean.getOrganizations():"");
			j.put("contact_job", contactBean.getJob()!=null?contactBean.getJob():"");
			j.put("contact_birthrday", contactBean.getBrithday()!=null?contactBean.getBrithday():"");
			
			
			JSONArray number_array = new JSONArray();
			for(PhoneInfo phone:contactBean.getPhoneList())
			{
				JSONObject jb = new JSONObject();
				jb.put("phone_number", phone.number);
				number_array.put(jb);
			}
			j.put("contact_number",number_array);
			
			
			JSONArray email_array = new JSONArray();
			for(EmailInfo emailInfo:contactBean.getEmailList())
			{
				JSONObject jb = new JSONObject();
				jb.put("email", emailInfo.email);
				email_array.put(jb);
			}
			j.put("contact_email", email_array);
			
			JSONArray address_array = new JSONArray();
			for(String address:contactBean.getAddressList())
			{
				JSONObject jb = new JSONObject();
				jb.put("address", address);
				address_array.put(jb);
			}
			j.put("contact_address", address_array);
			
			
			JSONArray website_array = new JSONArray();
			for(String website:contactBean.getWebSiteList())
			{
				JSONObject jb = new JSONObject();
				jb.put("website", website);
				website_array.put(jb);
			}
			j.put("contact_website", website_array);
			
			JSONArray notes_array = new JSONArray();
			for(String note:contactBean.getNoteList())
			{
				JSONObject jb = new JSONObject();
				jb.put("note", note);
				notes_array.put(jb);
			}
			j.put("contact_notes", notes_array);
			
			
			jaonContact.put(j );
		}
		jObject.put("contact", jaonContact);
		
		System.out.println(" jObject toStirng () --->" + jObject.toString());
		
		
		for(int i=0;i<cArrayListMessage.size();i++) {
			SmsContent smsContent=cArrayListMessage.get(i);
			JSONObject j = new JSONObject();
			j.put("message_id", smsContent.getId());
			j.put("message_number", smsContent.getSms_number()!=null ?smsContent.getSms_number():"");
			j.put("message_time", smsContent.getDate()!= -1 ?smsContent.getDate():"");
			j.put("message_type", smsContent.getTypeId());
			j.put("message_content", smsContent.getSms_body()!=null ?smsContent.getSms_body():"");
			jaonMessage.put(j );
		}
		jObject.put("message", jaonMessage);
		
		for(int i=0;i<cArrayListRemind.size();i++) {
			RemindBean remindBean=cArrayListRemind.get(i);
			JSONObject j = new JSONObject();
			j.put("remind_id", remindBean.getId());
			j.put("remind_content", remindBean.getContent()!=null?remindBean.getContent():"");
			j.put("remind_contact", remindBean.getContacts()!=null?remindBean.getContacts():"");
			j.put("remind_participants", remindBean.getParticipants()!=null?remindBean.getParticipants():"");
			j.put("remind_start_time", remindBean.getStart_time());
			j.put("remind_end_time", remindBean.getEnd_time());
			j.put("remind_remind_type", remindBean.getRemind_type());
			j.put("remind_remind_num", remindBean.getRemind_num());
			j.put("remind_remind_time", remindBean.getRemind_time());
			j.put("remind_repeat_type", remindBean.getRepeat_type());
			j.put("remind_repeat_fre", remindBean.getRepeat_fre());
			j.put("remind_repeat_start_time", remindBean.getRepeat_start_time());
			j.put("remind_repeat_end_time", remindBean.getRepeat_end_time());
			j.put("remind_repeat_count", remindBean.getCount()!=null?remindBean.getCount():"");
			jaonRemind.put(j );
		}
		jObject.put("remind", jaonRemind);
		
		for(int i=0;i<cArrayListCallLogInfo.size();i++) {
			CallLogInfo callLogInfo=cArrayListCallLogInfo.get(i);
			
			JSONObject j = new JSONObject();
			j.put("call_record_id", callLogInfo.getId());
			j.put("call_record_name", callLogInfo.getmCaller_name()!=null?callLogInfo.getmCaller_name():"");
			j.put("call_record_number", callLogInfo.getmCaller_number()!=null?callLogInfo.getmCaller_number():"");
			j.put("call_record_type", callLogInfo.getmCall_type()!=null?callLogInfo.getmCall_type():"");
			j.put("call_record_time", callLogInfo.getLong_date()!=-1?callLogInfo.getLong_date():"");
			j.put("call_record_duration", callLogInfo.getmCall_duration()!=null?callLogInfo.getmCall_duration():"");
			jaonCallRecord.put(j );
		}
		jObject.put("callrecord", jaonCallRecord);
		
		for(int i=0;i<cArrayListFavorite.size();i++) {
			SmsContent sContent=cArrayListFavorite.get(i);
			
			JSONObject j = new JSONObject();
			j.put("message_thread_id", sContent.getThread_id());
			j.put("message_content_id", sContent.getId());
			j.put("message_body", sContent.getSms_body()!=null?sContent.getSms_body():"");
			j.put("message_date", sContent.getDate()!=0?sContent.getDate():0);
			j.put("message_number", sContent.getSms_number()!=null?sContent.getSms_number():"");
			j.put("message_send", sContent.getSend_type()!=null?sContent.getSend_type():"");
			jaonCallFavorite.put(j);
		}
		jObject.put("favorite", jaonCallFavorite);
		
		nvps.add(new BasicNameValuePair(ACTION_POST_OPT,ACTION_POST_BACKUP));
		nvps.add(new BasicNameValuePair(ACTION_POST_DATA,jObject.toString()));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID,uid));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		result=new NetWorkResult();
		if(resultStr!=null)
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result.setSuccess(success);
			if("1".equals(success))
			{
				String state=jobj.getString(SUCCESS_STATUS);
				result.setState(state);
			}
		}
		
		return result;
	}
	
	
	
	/**
	 * 备份通话记录
	 * @return
	 */
	public NetWorkResult postCloundCallRecordBackup(ArrayList<CallLogInfo> cArrayList,String uid,boolean isBack,String imei)throws Exception
	{
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		for(int i=0;i<cArrayList.size();i++) {
			CallLogInfo callLogInfo=cArrayList.get(i);
			sb.append("{\"call_record_id\":\"").
			append(callLogInfo.getId()).
			append("\",\"call_record_name\":\"").
			append(callLogInfo.getmCaller_name()).
			append("\",\"call_record_number\":\"").
			append(callLogInfo.getmCaller_number()).
			append("\",\"call_record_type\":\"").
			append(callLogInfo.getmCall_type()).
			append("\",\"call_record_time\":\"").
			append(callLogInfo.getmCall_date()).
			append("\",\"call_record_duration\":\"").
			append(callLogInfo.getmCall_duration()).
			append("\"}");
			if(i<cArrayList.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		nvps.add(new BasicNameValuePair(ACTION_POST_OPT,ACTION_POST_CALLRECORD));
		nvps.add(new BasicNameValuePair(ACTION_POST_DATA,sb.toString()));
		nvps.add(new BasicNameValuePair(ACTION_POST_BACK,isBack?"0":"1"));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID,uid));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		if(resultStr!=null && !"".equals(resultStr))
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result=new NetWorkResult();
			result.setSuccess(success);
			if("1".equals(success) && isBack)
			{
				String state=jobj.getString(SUCCESS_STATUS);
				result.setState(state);
			}
		}
		
		return result;
	}
	//备份收藏表
	public NetWorkResult postCloundFavoriteBackup(ArrayList<SmsContent> cArrayList,String uid,boolean isBack,String imei)throws Exception
	{
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		for(int i=0;i<cArrayList.size();i++) {
			SmsContent sContent=cArrayList.get(i);
			sb.append("{\"message_thread_id\":\"").
			append(sContent.getThread_id()).
			append("\",\"message_content_id\":\"").
			append(sContent.getId()).
			append("\",\"message_body\":\"").
			append(sContent.getSms_body()).
			append("\",\"message_date\":\"").
			append(sContent.getDate()).
			append("\",\"message_number\":\"").
			append(sContent.getSms_number()).
			append("\"}");
			if(i<cArrayList.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		nvps.add(new BasicNameValuePair(ACTION_POST_OPT,ACTION_POST_FAVORITE));
		nvps.add(new BasicNameValuePair(ACTION_POST_DATA,sb.toString()));
		nvps.add(new BasicNameValuePair(ACTION_POST_BACK,isBack?"0":"1"));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID,uid));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		if(resultStr!=null &&!"".equals(resultStr) )
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result=new NetWorkResult();
			result.setSuccess(success);
			if("1".equals(success) && isBack)
			{
				String state=jobj.getString(SUCCESS_STATUS);
				result.setState(state);
			}
		}
		
		
		return result;
	}
    /**
     * 备份提醒
     */
	public NetWorkResult postCloundRemindBackup(ArrayList<RemindBean> cArrayList,String uid,boolean isBack,String imei)throws Exception
	{
		StringBuilder sb=new StringBuilder();
		sb.append("[");
		for(int i=0;i<cArrayList.size();i++) {
			RemindBean remindBean=cArrayList.get(i);
			sb.append("{\"remind_id\":\"").
			append(remindBean.getId()).
			append("\",\"remind_content\":\"").
			append(remindBean.getContent()).
			append("\",\"remind_participants\":\"").
			append(remindBean.getParticipants()).
			append("\",\"remind_start_time\":\"").
			append(remindBean.getStart_time()).
			append("\",\"remind_end_time\":\"").
			append(remindBean.getEnd_time()).
			append("\",\"remind_remind_type\":\"").
			append(remindBean.getRemind_type()).
			append("\",\"remind_remind_num\":\"").
			append(remindBean.getRemind_num()).
			append("\",\"remind_remind_time\":\"").
			append(remindBean.getRemind_time()).
			append("\",\"remind_repeat_type\":\"").
			append(remindBean.getRepeat_type()).
			append("\",\"remind_repeat_fre\":\"").
			append(remindBean.getRepeat_fre()).
			append("\",\"remind_repeat_start_time\":\"").
			append(remindBean.getRepeat_start_time()).
			append("\",\"remind_repeat_end_time\":\"").
			append(remindBean.getRepeat_end_time()).
			append("\",\"remind_repeat_count\":\"").
			append(remindBean.getCount()).
			append("\"}");
			if(i<cArrayList.size()-1) {
				sb.append(",");
			}
		}
		sb.append("]");
		nvps.add(new BasicNameValuePair(ACTION_POST_OPT,ACTION_POST_REMIND));
		nvps.add(new BasicNameValuePair(ACTION_POST_DATA,sb.toString()));
		nvps.add(new BasicNameValuePair(ACTION_POST_BACK,isBack?"0":"1"));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID,uid));
		nvps.add(new BasicNameValuePair(ACTION_POST_SESSIONID,imei));
		//解析json得到相应 NetWrokResult
		String resultStr = execute();
		if(resultStr!=null && !"".equals(resultStr))
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result=new NetWorkResult();
			result.setSuccess(success);
			if("1".equals(success) && isBack)
			{
				String state=jobj.getString(SUCCESS_STATUS);
				result.setState(state);
			}
		}
		
		return result;
	}
	/**
	 * sb.append("{\"contact_id\":\"").
			append(contactBean.getContact_id()).
			append("\",\"contact_name\":\"").
			append(contactBean.getNick()).
			append("\",\"contact_number\":\"").
			append(contactBean.getNumber()).
			append("\",\"contact_image\":\"").
			append(contactBean.getPhoto_id()).
			append("\",\"contact_organizations\":\"").
			append(contactBean.getOrganizations()).
			append("\",\"contact_birthrday\":\"").
			append(contactBean.getBrithday()).
			append("\",\"contact_email\":\"").
			append(contactBean.getEmail()).
			append("\",\"contact_address\":\"").
			append(contactBean.getAddress()).
			append("\",\"contact_website\":\"").
			append(contactBean.getWebsite()).
			append("\",\"contact_notes\":\"").
			append(contactBean.getNotes()).
			append("\"}");
	 * @param cookieid
	 * @return
	 * @throws Exception
	 */
	//联系人云恢复
	public NetWorkResult postCloundContactRecovery(String cookieid,String selectMode,boolean contactSelect,boolean messageSelect,boolean remindSelect,boolean callRecordSelect,boolean favoriteSelect) throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_POST_CONTACT_RECOVERY));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID, cookieid));
		nvps.add(new BasicNameValuePair(ACTION_POST_ACT, selectMode));
		result=new NetWorkResult();
		String resultStr = execute();
		if(resultStr!=null && !"".equals(resultStr))
		{
			JSONObject jobj = new JSONObject(resultStr);
			String success=jobj.getString(SUCCESS_RES);
			result.setSuccess(success);
			 //登陆成功
			if(SUCCESS.equals(success)){
				//联系人备份
				
				if(contactSelect)
				{
					JSONArray jArrayContact = jobj.getJSONArray(DATA_CONTACT);
					if(jArrayContact != null && jArrayContact.length() > 0) {
						for(int i=0; i<jArrayContact.length(); i++) {
							ContactBean contactBean=new ContactBean();
							JSONObject aObj = jArrayContact.getJSONObject(i);
							contactBean.setContact_id(Long.valueOf(aObj.getString("contact_id")));
							contactBean.setNick(aObj.getString("contact_name"));
							String imageRecovery=aObj.getString("contact_image");
							byte[] imageRe = imageRecovery.getBytes("ISO-8859-1");
							contactBean.setPhoto(imageRe);
//							contactBean.setPhoto(imageRecovery.getBytes());
							contactBean.setOrganizations(aObj.getString("contact_organizations"));
							contactBean.setBrithday(aObj.getString("contact_birthrday"));
							contactBean.setJob(aObj.getString("contact_job"));
							
							JSONArray number_array = aObj.getJSONArray("contact_number");
							for(int j = 0 ; j<number_array.length() ;j++)
							{
								JSONObject jb = number_array.getJSONObject(j);
								contactBean.getPhoneList().add(new PhoneInfo(2,jb.getString("phone_number")));
							}
							
							JSONArray email_array = aObj.getJSONArray("contact_email");
							for(int j = 0 ; j<email_array.length() ;j++)
							{
								JSONObject jb = email_array.getJSONObject(j);
								contactBean.getEmailList().add(new EmailInfo(jb.getString("email")));
							}
							
							JSONArray address_array = aObj.getJSONArray("contact_address");
							for(int j = 0 ; j<address_array.length() ;j++)
							{
								JSONObject jb = address_array.getJSONObject(j);
								contactBean.getAddressList().add(jb.getString("address"));
							}
							
							JSONArray website_array = aObj.getJSONArray("contact_website");
							for(int j = 0 ; j<website_array.length() ;j++)
							{
								JSONObject jb = website_array.getJSONObject(j);
								contactBean.getWebSiteList().add(jb.getString("website"));
							}
							
							JSONArray note_array = aObj.getJSONArray("contact_notes");
							for(int j = 0 ; j<note_array.length() ;j++)
							{
								JSONObject jb = note_array.getJSONObject(j);
								contactBean.getNoteList().add(jb.getString("note"));
							}
						
							result.getContactBeans().add(contactBean);
						}
					}
					
				}
				if(messageSelect)
				{
					//短信恢复
					JSONArray jArrayMessage = jobj.getJSONArray(DATA_MESSAGE);
					if(jArrayMessage != null && !"".equals(jArrayMessage)&& jArrayMessage.length() > 0) {
						for(int i=0; i<jArrayMessage.length(); i++) {
							SmsContent smsContent=new SmsContent();
							JSONObject aObj = jArrayMessage.getJSONObject(i);
							smsContent.setId(Long.valueOf(aObj.getString("message_id")));
							smsContent.setSms_number((aObj.getString("message_number")));
							smsContent.setDate(Long.valueOf(aObj.getString("message_time")));
							smsContent.setTypeId(Integer.valueOf(aObj.getString("message_type")));
							smsContent.setSms_body((aObj.getString("message_content")));
							result.getSmsContents().add(smsContent);
						}
					}
				}
				if(remindSelect)
				{
					//提醒恢复
					JSONArray jArrayRemind = jobj.getJSONArray(DATA_REMIND);
					if(jArrayRemind != null && jArrayRemind.length() > 0) {
						for(int i=0; i<jArrayRemind.length(); i++) {
							RemindBean remindBean=new RemindBean();
							JSONObject aObj = jArrayRemind.getJSONObject(i);
							remindBean.setId(Integer.valueOf(aObj.getString("remind_id")));
							remindBean.setContent(aObj.getString("remind_content"));
							remindBean.setContacts(aObj.getString("remind_contact"));
							remindBean.setParticipants(aObj.getString("remind_participants"));
							remindBean.setStart_time(Long.valueOf(aObj.getString("remind_start_time")));
							remindBean.setEnd_time(Long.valueOf(aObj.getString("remind_end_time")));
							remindBean.setRemind_type(Integer.valueOf(aObj.getString("remind_remind_type")));
							remindBean.setRemind_num(Integer.valueOf(aObj.getString("remind_remind_num")));
							remindBean.setRemind_time(Integer.valueOf(aObj.getString("remind_remind_time")));
							remindBean.setRepeat_type(Integer.valueOf(aObj.getString("remind_repeat_type")));
							remindBean.setRepeat_fre(Integer.valueOf(aObj.getString("remind_repeat_fre")));
							remindBean.setRepeat_start_time(Long.valueOf(aObj.getString("remind_repeat_start_time")));
							remindBean.setRepeat_end_time(Long.valueOf(aObj.getString("remind_repeat_end_time")));
							remindBean.setCount(aObj.getString("remind_repeat_count"));
							result.getRemindBeans().add(remindBean);
						}
					}
				}
				if(callRecordSelect)
				{
					//通话恢复
					JSONArray jArrayCallRecord = jobj.getJSONArray(DATA_RECORD);
					if(jArrayCallRecord != null && jArrayCallRecord.length() > 0) {
						for(int i=0; i<jArrayCallRecord.length(); i++) {
							CallLogInfo callLogInfo=new CallLogInfo();
							JSONObject aObj = jArrayCallRecord.getJSONObject(i);
							callLogInfo.setId(Long.valueOf(aObj.getString("call_record_id")));
							callLogInfo.setmCaller_name((aObj.getString("call_record_name")));
							callLogInfo.setmCaller_number((aObj.getString("call_record_number")));
							callLogInfo.setmCall_type((aObj.getString("call_record_type")));
							callLogInfo.setLong_date((aObj.getLong("call_record_time")));
							callLogInfo.setmCall_duration(aObj.getString("call_record_duration"));
							result.getCallLogInfos().add(callLogInfo);
						}
					}
				}
				if(favoriteSelect)
				{
					//收藏恢复
					JSONArray jArrayFavorite = jobj.getJSONArray(DATA_FAVORITE);
					if(jArrayFavorite != null && jArrayFavorite.length() > 0) {
						for(int i=0; i<jArrayFavorite.length(); i++) {
							SmsContent smsContent=new SmsContent();
							JSONObject aObj = jArrayFavorite.getJSONObject(i);
							smsContent.setThread_id(Long.valueOf(aObj.getString("message_thread_id")));
							smsContent.setId(Long.valueOf((aObj.getString("message_content_id"))));
							smsContent.setSms_body((aObj.getString("message_body")));
							smsContent.setSend_type((aObj.getString("message_send")));
							smsContent.setSms_number((aObj.getString("message_number")));
							smsContent.setDate(Long.valueOf(aObj.getString("message_date")));
							result.getSmsContentFavorite().add(smsContent);
						}
					}
				}
					
					
				
				
//				JSONArray jArrayMessage = jobj.getJSONArray(DATA_MESSAGE);
			//登陆失败
			}else if(FAILURE.equals(success)){
//				result.setError_message(jobj.getString("msg"));
			}else//未登录
			{
				
			}
		}
		
		return result;
	}
	//短信云恢复
	public NetWorkResult postCloundMessageRecovery(String cookieid)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_POST_MESSAGE_RECOVERY));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID, cookieid));
		String resultStr = execute();
		JSONObject jobj = new JSONObject(resultStr);
		String success=jobj.getString(SUCCESS_RES);
		result=new NetWorkResult();
		result.setSuccess(success);
		 //登陆成功
		if(SUCCESS.equals(success)){
			JSONArray jArray = jobj.getJSONArray(DATA_STR);
			if(jArray != null && jArray.length() > 0) {
				for(int i=0; i<jArray.length(); i++) {
					SmsContent smsContent=new SmsContent();
					JSONObject aObj = jArray.getJSONObject(i);
					smsContent.setId(Long.valueOf(aObj.getString("message_id")));
					smsContent.setSms_number((aObj.getString("message_number")));
					smsContent.setDate( Long.valueOf(aObj.getString("message_time")));
					smsContent.setTypeId(Integer.valueOf(aObj.getString("message_type")));
					smsContent.setSms_body((aObj.getString("message_content")));
					result.getSmsContents().add(smsContent);
				}
			}
		//登陆失败
		}else if(FAILURE.equals(success)){
			result.setError_message(jobj.getString("msg"));
		}else//未登录
		{
			
		}
		return result;
	}
	//通话记录云恢复
	public NetWorkResult postCloundCallReordRecovery(String cookieid)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_POST_CALLRECORD_RECOVERY));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID, cookieid));
		String resultStr = execute();
		JSONObject jobj = new JSONObject(resultStr);
		String success=jobj.getString(SUCCESS_RES);
		result=new NetWorkResult();
		result.setSuccess(success);
		 //登陆成功
		if(SUCCESS.equals(success)){
			JSONArray jArray = jobj.getJSONArray(DATA_STR);
			if(jArray != null && jArray.length() > 0) {
				for(int i=0; i<jArray.length(); i++) {
					CallLogInfo callLogInfo=new CallLogInfo();
					JSONObject aObj = jArray.getJSONObject(i);
					callLogInfo.setId(Long.valueOf(aObj.getString("call_record_id")));
					callLogInfo.setmCaller_name((aObj.getString("call_record_name")));
					callLogInfo.setmCaller_number((aObj.getString("call_record_number")));
					callLogInfo.setmCall_type((aObj.getString("call_record_type")));
					callLogInfo.setmCall_date((aObj.getString("call_record_time")));
					callLogInfo.setmCall_duration(aObj.getString("call_record_duration"));
					result.getCallLogInfos().add(callLogInfo);
				}
			}
		//登陆失败
		}else if(FAILURE.equals(success)){
			result.setError_message(jobj.getString("msg"));
		}else//未登录
		{
			
		}
		return result;
	}
	//短信收藏云恢复
	public NetWorkResult postCloundFavoriteRecovery(String cookieid)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_POST_FAVORITE_RECOVERY));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID, cookieid));
		String resultStr = execute();
		JSONObject jobj = new JSONObject(resultStr);
		String success=jobj.getString(SUCCESS_RES);
		result=new NetWorkResult();
		result.setSuccess(success);
		 //登陆成功
		if(SUCCESS.equals(success)){
			JSONArray jArray = jobj.getJSONArray(DATA_STR);
			if(jArray != null && jArray.length() > 0) {
				for(int i=0; i<jArray.length(); i++) {
					SmsContent smsContent=new SmsContent();
					JSONObject aObj = jArray.getJSONObject(i);
					smsContent.setThread_id(Long.valueOf(aObj.getString("message_thread_id")));
					smsContent.setId(Long.valueOf((aObj.getString("message_content_id"))));
					smsContent.setSms_body((aObj.getString("message_body")));
					smsContent.setSend_type((aObj.getString("message_date")));
					smsContent.setSms_number((aObj.getString("message_number")));
					result.getSmsContents().add(smsContent);
				}
			}
		//登陆失败
		}else if(FAILURE.equals(success)){
			result.setError_message(jobj.getString("msg"));
		}else//未登录
		{
			
		}
		return result;
	}
	/**
	 * 
	 * @param cookieid
	 * @return
	 * @throws Exception
	 */
	//提醒云恢复
	public NetWorkResult postCloundRemindRecovery(String cookieid)throws Exception
	{
		nvps.add(new BasicNameValuePair(ACTION_STR,ACTION_POST_REMIND_RECOVERY));
		nvps.add(new BasicNameValuePair(ACTION_POST_UID, cookieid));
		String resultStr = execute();
		JSONObject jobj = new JSONObject(resultStr);
		String success=jobj.getString(SUCCESS_RES);
		result=new NetWorkResult();
		result.setSuccess(success);
		 //登陆成功
		if(SUCCESS.equals(success)){
			JSONArray jArray = jobj.getJSONArray(DATA_STR);
			if(jArray != null && jArray.length() > 0) {
				for(int i=0; i<jArray.length(); i++) {
					RemindBean remindBean=new RemindBean();
					JSONObject aObj = jArray.getJSONObject(i);
					remindBean.setId(Integer.valueOf(aObj.getString("remind_id")));
					remindBean.setContent(aObj.getString("remind_content"));
					remindBean.setParticipants(aObj.getString("remind_participants"));
					remindBean.setStart_time(Long.valueOf(aObj.getString("remind_start_time")));
					remindBean.setEnd_time(Long.valueOf(aObj.getString("remind_end_time")));
					remindBean.setRemind_type(Integer.valueOf(aObj.getString("remind_remind_type")));
					remindBean.setRemind_num(Integer.valueOf(aObj.getString("remind_remind_num")));
					remindBean.setRemind_time(Integer.valueOf(aObj.getString("remind_remind_time")));
					remindBean.setRepeat_type(Integer.valueOf(aObj.getString("remind_repeat_type")));
					remindBean.setRepeat_fre(Integer.valueOf(aObj.getString("remind_repeat_fre")));
					remindBean.setRepeat_start_time(Long.valueOf(aObj.getString("remind_repeat_start_time")));
					remindBean.setRepeat_end_time(Long.valueOf(aObj.getString("remind_repeat_end_time")));
					remindBean.setCount(aObj.getString("remind_repeat_count"));
					result.getRemindBeans().add(remindBean);
				}
			}
		//登陆失败
		}else if(FAILURE.equals(success)){
			result.setError_message(jobj.getString("msg"));
		}else//未登录
		{
			
		}
		return result;
	}
	
	/**
	 * 流转字符串 默认编码为utf-8
	 * 
	 * @param in
	 * @return out
	 */
	public String stream2String(InputStream in) {
		StringBuilder sb = new StringBuilder();

		try {
			BufferedReader sr = new BufferedReader(new InputStreamReader(in, HTTP.UTF_8));

			String str = null;
			while ((str = sr.readLine()) != null) {
				sb.append(str);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
	
	////////////////////////////////////
	
	/**
	 * 解析json 示例
	 */
	
	/**{
		  “success”:”true”,
		  “page”:”记录总页数”,
		  “curpage”:”记录当前页数”
		   data:[{  列表数据，数组形式
		         "app_id" : "应用ID",
		         "app_name" : "应用名称",
		         "icon" : "应用图标URL",
		         "score" : "应用分数",
		         "price_str": 应用价格",
		         "download_url" : "应用下载URL",
		         "introduce_short" : "应用描述",
		         "download_num" : "应用下载次数",
		         "size_text" : "应用大小",

		         attachs:[{  附件图片，数组形式
		                   key: "附件图片URL",key为键（数字）
		                 }]
		        }]
		}
		 **/
	/**{
	 * 联系人
	   contact_data:[{  列表数据，数组形式
	         "contact_id" : "联系人id",
	         "contact_name" : "联系人名称",
	         "contact_number" : "联系人名称",
	         "contact_image" : "应用图标URL",
	        },{  列表数据，数组形式
	         "contact_id" : "联系人id",
	         "contact_name" : "联系人名称",
	         "contact_number" : "联系人名称",
	         "contact_image" : "联系人名称",
	          }
	        ]
	}
	 **/
	/**{
	 * 短信
	   message_data:[{  列表数据，数组形式
	         "message_id" : "联系人id",
	         "send_name" : "发件人名称",
	         "receive_name" : "接收者名称",
	          "send_number" : "发送者电话号码",
	         "receive_number" : "接收者电话号码",
	         "message_content" : "短信内容",
	        
	        },{  列表数据，数组形式
	         "contact_id" : "联系人id",
	         "contact_name" : "联系人名称",
	         "contact_number" : "联系人名称",
	         "contact_image" : "应用图标URL",
	          }
	        ]
	}
	 **/
	
	/**
	JSONObject jobj = new JSONObject(resultStr);
	
	String success = jobj.getString(SUCCESS_STR);
	int page = jobj.getInt(PAGE_STR);
	int curpage_searchResult = jobj.getInt(CURPAGE_STR);
	
	JSONArray jArray = jobj.getJSONArray(DATA_STR);
	if(jArray != null && jArray.length() > 0) {
		for(int i=0; i<jArray.length(); i++) {
			JSONObject aObj = jArray.getJSONObject(i);
			
			int app_id = aObj.getInt(APP_ID_STR);
			String app_name = aObj.getString(APP_NAME_STR);
			String app_icon = aObj.getString(ICON_STR);
			String app_download = aObj.getString(DOWNLOAD_STR);
			String app_score = Float.parseFloat(aObj.get(SCORE_STR).toString());
			String app_size = aObj.getString(SIZE_TEXT_STR);
			String app_introduce = aObj.getString(INTRODUCE_STR);
			int app_download_num = aObj.getInt(DOWNLOAD_NUM_STR)
			
			JSONArray ja =  aObj.getJSONArray(ATTACHS_STR); // 对应的图片
			
			if(ja !=null && ja.length() >0){
				for(int j = 0;j<ja.length();j++)
				{
					String attch_url = ja.getJSONObject(j).getString(String.valueOf(j));
					aApp.getAttachs().add(attch_url);
				}
			}
		}
	} **/
	
	
}
