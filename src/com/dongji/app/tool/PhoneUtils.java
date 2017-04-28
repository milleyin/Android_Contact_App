package com.dongji.app.tool;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.telephony.ITelephony;

public class PhoneUtils {
	/**
	* ��TelephonyManager��ʵ��ITelephony,������
	10
	*/
	static public ITelephony getITelephony(TelephonyManager telMgr) throws Exception {
	Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony");
	getITelephonyMethod.setAccessible(true);//˽�л�����Ҳ��ʹ��
	return (ITelephony)getITelephonyMethod.invoke(telMgr);
	}
	static public void printAllInform(Class clsShow) {
	try {
	// ȡ�����з���
	Method[] hideMethod = clsShow.getDeclaredMethods();
	int i = 0;
	for (; i < hideMethod.length; i++) {
//	Log.e("method name", hideMethod);
	}
	// ȡ�����г���
	Field[] allFields = clsShow.getFields();
	for (i = 0; i < allFields.length; i++) {
//	Log.e("Field name", allFields.getName());
	}
	} catch (SecurityException e) {
	// throw new RuntimeException(e.getMessage());
	e.printStackTrace();
	} catch (IllegalArgumentException e) {
	// throw new RuntimeException(e.getMessage());
	e.printStackTrace();
	} catch (Exception e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
	}
	}

}
