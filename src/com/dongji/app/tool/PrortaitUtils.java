package com.dongji.app.tool;

import com.dongji.app.addressbook.R;


public class PrortaitUtils {
	private static int[] protraits=new int[]{R.drawable.default_contact,R.drawable.default_contact,R.drawable.default_contact,R.drawable.default_contact};
	
	public static int  conversionIdToRes(int id){
		if(isSystemProtraits(id)){
			return protraits[(int) id];
		}else{
			return protraits[0];
		}
	}
	
	public static boolean isSystemProtraits(int id){
		return id >= 0 && id < protraits.length;
	}
}
