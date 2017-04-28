package com.dongji.app.ui;
import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.dongji.app.addressbook.R;

/**
 * 
 * 短信表情 工具类
 * 
 * @author Administrator
 *
 */
public  class EmotionHelper {

	Context context;
	
	public static HashMap<String,Drawable> map = new HashMap<String,Drawable>(); //全局变量
	
	public EmotionHelper(Context context )
	{
		//初始化所有表情
		
		this.context = context;	
	    
		for(int j = 0;j<emotionStringName.length;j++)
		{
			map.put(emotionStringName[j],context.getResources().getDrawable(emotionResID[j]));
		}
		
	}
	
	public static int[] emotionResID = new int[] {
		       R.drawable.f000,
		       R.drawable.f001,
		       R.drawable.f002,
		       R.drawable.f003,
		       
		       R.drawable.f004, 
		       R.drawable.f005,
		       R.drawable.f006,
		       R.drawable.f007,
		      
		       R.drawable.f008,
		       R.drawable.f009,
		       R.drawable.f010,
		       R.drawable.f011,
		     
		       R.drawable.f012,
		       R.drawable.f013,
		       R.drawable.f014,
		       R.drawable.f015,
		      
		       R.drawable.f016,
		       R.drawable.f017,
		       R.drawable.f018,
		       R.drawable.f019,
		       
		       R.drawable.f020,
		       R.drawable.f021,
		       R.drawable.f022,
		       R.drawable.f023,
		      
		       
		       R.drawable.f024,
		       R.drawable.f025,
		       R.drawable.f026,
		       R.drawable.f027,
		       
		       R.drawable.f028,
		       R.drawable.f029,
		       R.drawable.f030,
		       R.drawable.f031,
		       
		       R.drawable.f032,
		       R.drawable.f033,
		       R.drawable.f034,
		       R.drawable.f035,
		       
		       R.drawable.f036,
		       R.drawable.f037,
		       R.drawable.f038,
		       R.drawable.f039
		     
		};
	
	public static String [] emotionStringNames = new String [] {
		"(￣ˇ￣)",
		"~(￣▽￣)~",
		"(￣)＾(￣)",
		"Y(^_^)Y",
		
		"O__O", 
		"(￣ε￣)",
		"( ' – ' )",
		"ㄟ(▔▽▔)ㄏ",
		
		"~@^_^@~",
		"=^_^=",
		"˙△˙",
		"°(°ˊДˋ°) °",
		
		"//(ㄒoㄒ)//",
		"乀(ˉεˉ乀)",
		"( > c < )",
		"└(^o^)┘",
		
		"(╬▔皿▔)",
		"O__O",
		"(￣﹏￣)",
		"(⊙＿⊙)",
		
		"^_^",
		"--<-<-<@",
		"…(⊙_⊙;)…",
		"(●-●)",
		
		"(．Q．)",
		"(╰_╯)",
		"( ⊙ o ⊙ )",
		"( 9__9 )",
		
		"╮(╯Д╰)╭",
		"≥﹏≤",
		"@x@",
		"(《⊙⊙》)",
		
		"(/≥▽≤/)",
		"#^_^#",
		"⊙ω⊙",
		"⊙△⊙",
		
		"╯△╰",
		"∩__∩",
		"( ^３^ )╱~~",
		"╮(╯3╰)╭"
	};
	
	public static String [] emotionStringName = new String [] {
		   "f000",
	      "f001",
	      "f002",
	      "f003",
	       
	      
	      "f004",
	      "f005",
	      "f006",
	      "f007",
	      
	      "f008",
	      "f009",
	      "f010",
	      "f011",
	     

	      "f012",
	      "f013",
	      "f014",
	      "f015",
	      
	      "f016",
	      "f017",
	      "f018",
	      "f019",
	       
	      "f020",
	      "f021",
	      "f022",
	      "f023",
	      
	       
	      "f024",
	      "f025",
	      "f026",
	      "f027",
	       
	      "f028",
	      "f029",
	      "f030",
	      "f031",
	      "f032",
	      "f033",
	      "f034",
	      "f035",
	       
	      "f036",
	      "f037",
	      "f038",
	      "f039",
	     
		
	};
	
	
}
