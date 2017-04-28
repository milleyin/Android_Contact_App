package com.dongji.app.tool;

/**
 * 
 * 星座查询相关
 * @author zhiYong
 *
 */
public class Constellations {

	public static String check(String birthday)
	{
		try {
			String s = null;
			String [] ss = birthday.split("-");
			int month = Integer.valueOf(ss[0]);
			int day = Integer.valueOf(ss[1]);
			
			switch (month) {
			case 1:
				if(day<20)
				{
					s="摩羯座";
				}else{
					s="水瓶座";
				}
				break;

			case 2:
				if(day<19)
				{
					s="水瓶座";
				}else{
					s="双鱼座";
				}
				break;

			case 3:
				if(day<21)
				{
					s="双鱼座";
				}else{
					s="白羊座";
				}
				break;

			case 4:
				if(day<20)
				{
					s="白羊座";
				}else{
					s="金牛座";
				}
				break;

			case 5:
				if(day<21)
				{
					s="金牛座";
				}else{
					s="双子座";
				}
				break;

			case 6:
				if(day<21)
				{
					s="双子座";
				}else{
					s="巨蟹座";
				}
				break;

			case 7:
				if(day<23)
				{
					s="巨蟹座";
				}else{
					s="狮子座";
				}
				break;

			case 8:
				if(day<22)
				{
					s="狮子座";
				}else{
					s="处女座";
				}
				break;

			case 9:
				if(day<23)
				{
					s="处女座";
				}else{
					s="天枰座";
				}
				break;

			case 10:
				if(day<24)
				{
					s="天枰座";
				}else{
					s="天蝎座";
				}
				break;

			case 11:
				if(day<23)
				{
					s="天蝎座";
				}else{
					s="射手座";
				}
				break;

			case 12:
				if(day<22)
				{
					s="射手座";
				}else{
					s="摩羯座";
				}
				break;

			default:
				break;
			}
					
			return s ;
		} catch (Exception e) {
			e.printStackTrace();
			
			return null;
		}
	}
}
