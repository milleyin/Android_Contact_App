package com.dongji.app.tool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.widget.EditText;

public class FavoritContactTool {

	public static String FA_CONTACT_SF_NAME ="favorit_contact";
	
	private static String FA_0 ="fa_0";
	private static String FA_0_NAME = "fa_0_name";
	private static String FA_0_NUMBER = "fa_0_number";
	
	private static String FA_1 ="fa_1";
	private static String FA_1_NAME = "fa_1_name";
	private static String FA_1_NUMBER = "fa_1_number";
	
	private static String FA_2 ="fa_2";
	private static String FA_2_NAME = "fa_2_name";
	private static String FA_2_NUMBER = "fa_2_number";
	
	private static String FA_3 ="fa_3";
	private static String FA_3_NAME = "fa_3_name";
	private static String FA_3_NUMBER = "fa_3_number";
	
	private static String FA_4 ="fa_4";
	private static String FA_4_NAME = "fa_4_name";
	private static String FA_4_NUMBER = "fa_4_number";
	
	private static String FA_5 ="fa_5"; 
	private static String FA_5_NAME = "fa_5_name";
	private static String FA_5_NUMBER = "fa_5_number";
	
	private static String FA_6 ="fa_6";
	private static String FA_6_NAME = "fa_6_name";
	private static String FA_6_NUMBER = "fa_6_number";
	
	private static String FA_7 ="fa_7";
	private static String FA_7_NAME = "fa_7_name";
	private static String FA_7_NUMBER = "fa_7_number";
	
	private static String FA_8 ="fa_8";
	private static String FA_8_NAME = "fa_8_name";
	private static String FA_8_NUMBER = "fa_8_number";
	
	private static String FA_9 ="fa_9";
	private static String FA_9_NAME = "fa_9_name";
	private static String FA_9_NUMBER = "fa_9_number";
	
	private static String FA_10 ="fa_10";
	private static String FA_10_NAME = "fa_10_name";
	private static String FA_10_NUMBER = "fa_10_number";
	
	private static String FA_11 ="fa_11";
	private static String FA_11_NAME = "fa_11_name";
	private static String FA_11_NUMBER = "fa_11_number";
	
	private static String FA_key = "fa_key";
	
	
	public static  List<String> getContactPosition (Context context)
	{
		
		List<String> cotact_ids = new ArrayList<String>();
		
		SharedPreferences sf = context.getSharedPreferences(FA_CONTACT_SF_NAME, 0);
		
		cotact_ids.add(sf.getString(FA_0, "-1"));
		cotact_ids.add(sf.getString(FA_1, "-1"));
		cotact_ids.add(sf.getString(FA_2, "-1"));
		
		cotact_ids.add(sf.getString(FA_3, "-1"));
		cotact_ids.add(sf.getString(FA_4, "-1"));
		cotact_ids.add(sf.getString(FA_5, "-1"));
		
		cotact_ids.add(sf.getString(FA_6, "-1"));
		cotact_ids.add(sf.getString(FA_7, "-1"));
		cotact_ids.add(sf.getString(FA_8, "-1"));
		
		cotact_ids.add(sf.getString(FA_9, "-1"));
		cotact_ids.add(sf.getString(FA_10, "-1"));
		cotact_ids.add(sf.getString(FA_11, "-1"));
		
		return cotact_ids;
	}
	
	
	public static void changePostionContact(Context context, int position, String contact_id,String name,String number)
	{
		SharedPreferences sf = context.getSharedPreferences(FA_CONTACT_SF_NAME, 0);
		Editor editor = sf.edit();
		
//		editor.putString(FA_key, key);
		
		if (!number.equals(""))
			contact_id = contact_id + "," + name + "," + number;
		else 
			contact_id = "-1";
		
		switch (position) {
		case 0:
			editor.putString(FA_0, contact_id);
//			editor.putString(FA_0_NAME, name);
//			editor.putString(FA_0_NUMBER, number);
			break;
			
		case 1:
			editor.putString(FA_1, contact_id);
//			editor.putString(FA_1_NAME, name);
//			editor.putString(FA_1_NUMBER, number);
			break;
			
		case 2:
			editor.putString(FA_2, contact_id);
//			editor.putString(FA_2_NAME, name);
//			editor.putString(FA_2_NUMBER, number);
			break;
			
			///////////////////////
		case 3:
			editor.putString(FA_3, contact_id);
//			editor.putString(FA_3_NAME, name);
//			editor.putString(FA_3_NUMBER, number);
			break;
			
		case 4:
			editor.putString(FA_4, contact_id);
//			editor.putString(FA_4_NAME, name);
//			editor.putString(FA_4_NUMBER, number);
			break;
			
		case 5:
			editor.putString(FA_5, contact_id);
//			editor.putString(FA_5_NAME, name);
//			editor.putString(FA_5_NUMBER, number);
			break;
			
			////////////////////////////
			
		case 6:
			editor.putString(FA_6, contact_id);
//			editor.putString(FA_6_NAME, name);
//			editor.putString(FA_6_NUMBER, number);
			break;
			
		case 7:
			editor.putString(FA_7, contact_id);
//			editor.putString(FA_7_NAME, name);
//			editor.putString(FA_7_NUMBER, number);
			break;
			
		case 8:
			editor.putString(FA_8, contact_id);
//			editor.putString(FA_8_NAME, name);
//			editor.putString(FA_8_NUMBER, number);
			break;
			
			//////
			
		case 9:
			editor.putString(FA_9, contact_id);
//			editor.putString(FA_9_NAME, name);
//			editor.putString(FA_9_NUMBER, number);
			break;
			
		case 10:
			editor.putString(FA_10, contact_id);
//			editor.putString(FA_10_NAME, name);
//			editor.putString(FA_10_NUMBER, number);
			break;
			
		case 11:
			editor.putString(FA_11, contact_id);
//			editor.putString(FA_11_NAME, name);
//			editor.putString(FA_11_NUMBER, number);
			break;
			
		default:
			break;
		}
		
		editor.commit();
	}
	
	public static List<String> getFavoriteContactNumber(Context context){
		
		List<String> list = new ArrayList<String>();
		
		SharedPreferences sf = context.getSharedPreferences(FA_CONTACT_SF_NAME, 0);
		
		return list;
	}
}
