package com.dongji.app.tool;

import java.util.regex.Pattern;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.provider.ContactsContract;

public class PhoneNumberTool {

	/**
	 * 各运营商  IP拨号的 前缀 (可能有漏的)
	 */
	private static final String YIDONG_IP_1 = "17951";//移动
	private static final String YIDONG_IP_2 = "12593";
	
	private static final String LIANTONG_IP = "17911"; //联通
	
	private static final String DIANXIN_IP = "17909"; //电信
	
	
	private static final String NATION_COAD = "+86"; //中国的国际区号
	
	
	/**
	 * 去除所有可能添加的前缀  ： +86 , 区号 ， ip拨号， 省外号码前加0
	 * @param number  最原始的号码
	 * @return  纯净号码
	 */
	public static String cleanse(String number)
	{
		
		number = number.replace("(", "").replace(") ", "").replace("-", "").replace(" ", "").replace("+86", "").replace("N", "").replace("*", "");
		
		
		if(number.length()>1 && number.substring(0, 1).equals("0"))
		{
			number = number.substring(1, number.length());
		}
		
		if(number.length()>5 && (number.substring(0, 5).equals(YIDONG_IP_1) || number.substring(0, 5).equals(YIDONG_IP_2) || number.substring(0, 5).equals(LIANTONG_IP) || number.substring(0, 5).equals(DIANXIN_IP)))
		{
			number = number.substring(5, number.length());
		}
		
		return number;
	}

	
	/**
	 * 通过号码，查询联系人的相关信息  姓名[0] , 头像[1] , contactId[2] , sort_key[3]
	 * @param context
	 * @param number  号码
	 * @return
	 */
	public static String[] getContactInfo(Context context ,String number)
	{
		
		String [] date = new String [4]; //可更改的大小
		
		String n = cleanse(number);
		String s_1 = YIDONG_IP_1 + n;
		String s_2 = YIDONG_IP_1 + n;
		String s_3 = LIANTONG_IP + n;
		String s_4 = DIANXIN_IP + n;
		String s_5 = NATION_COAD + n;
		
		String s_6 = addRequirements(number);
		
		//7个查询条件:原始号码 ，纯净号码, 17951*纯净号码 , 12593*纯净号码 , 17911*纯净号码 , 17909*纯净号码 , +86*纯净号码, "1-300-000-0000"
		String selection = ContactsContract.CommonDataKinds.Phone.NUMBER+"=? OR " + ContactsContract.CommonDataKinds.Phone.NUMBER+"=? OR "+ ContactsContract.CommonDataKinds.Phone.NUMBER+"=? OR " +ContactsContract.CommonDataKinds.Phone.NUMBER+"=? OR " + ContactsContract.CommonDataKinds.Phone.NUMBER+"=? OR " +ContactsContract.CommonDataKinds.Phone.NUMBER+"=? OR " +ContactsContract.CommonDataKinds.Phone.NUMBER+"=?";
		String[] selectionArgs = new String[]{number,n,s_1,s_2,s_3,s_4,s_5};
		
		if (s_6 != null) {
			selection += " OR " +ContactsContract.CommonDataKinds.Phone.NUMBER+"=?";
			selectionArgs = new String[]{number,n,s_1,s_2,s_3,s_4,s_5,s_6};
		}
		
		Cursor phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[] { ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.CONTACT_ID,ContactsContract.CommonDataKinds.Phone.PHOTO_ID,"sort_key" },selection, selectionArgs, null);
		
//		for(String key:phones.getColumnNames())
//		{
//			System.out.println("columnName  --->" + key);
//		}
		
		if (phones.moveToFirst()) {
			   date[0] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			   date[1] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID));
			   date[2] = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
			   date[3] = phones.getString(phones.getColumnIndex("sort_key"));
		    }
		phones.close();
		
		return date;
	}
	
	/**
	 * 查询指定电话号码的通话记录
	 * @param context
	 * @param number
	 * @return
	 */
	public static Cursor getCallLogByNumber(Context context, String number)
	{
		String n = cleanse(number);
		String s_1 = YIDONG_IP_1 + n;
		String s_2 = YIDONG_IP_1 + n;
		String s_3 = LIANTONG_IP + n;
		String s_4 = DIANXIN_IP + n;
		String s_5 = NATION_COAD + n;
		
		//7个查询条件:原始号码 ，纯净号码, 17951*纯净号码 , 12593*纯净号码 , 17911*纯净号码 , 17909*纯净号码 , +86*纯净号码
		Cursor callLogs = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,null,CallLog.Calls.NUMBER+"=? OR " + CallLog.Calls.NUMBER+"=? OR "+ CallLog.Calls.NUMBER+"=? OR " +CallLog.Calls.NUMBER+"=? OR " + CallLog.Calls.NUMBER+"=? OR " +CallLog.Calls.NUMBER+"=? OR " + CallLog.Calls.NUMBER+"=?", new String[]{number,n,s_1,s_2,s_3,s_4,s_5}, CallLog.Calls.DATE +" DESC");
		return callLogs;
	}
	
	
	
	private static String addRequirements(String number) {
		
		String s_6 = null;
		
		String treg = "[0]{1}[0-9]{2,3}[0-9]{7,8}";  //是否为固定号码（加区号）
		String str = "[0-9]{7,8}"; //是否为固定号码（不加区号）

		boolean isTreg = Pattern.compile(treg).matcher(number).find();
		boolean isTel = Pattern.compile(str).matcher(number).find();
		
		if (isTreg) {  //021-000-00000
			
			String str1 = number.substring(0, 3);
			String str2 = number.substring(3, 6);
			String str3 = number.substring(6, number.length());
			
			s_6 = str1 + "-" + str2 + "-" + str3;
			
		} else if (isTel && number.length() < 9){ //000-0000
			
			String str1 = number.substring(0, 3);
			String str2 = number.substring(3, number.length());
			
			s_6 = str1 + "-" + str2 ;
			
		} else if (number.length() == 11 && number.substring(0, 1).equals("1")) { //1-300-000-0000
			
			String str1 = number.substring(0, 1);
			String str2 = number.substring(1, 4);
			String str3 = number.substring(4, 7);
			String str4 = number.substring(7, number.length());
			
			s_6 = str1 + "-" + str2 + "-" + str3 + "-" + str4;
		}
		
//		System.out.println(isTreg +" --------  " +isTel+" s 6666 ---- > " + s_6 + "   number ----- > " + number);
		
		return s_6;
	}
	
}
