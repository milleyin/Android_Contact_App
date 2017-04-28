package com.dongji.app.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.dongji.app.addressbook.R;

/**
 * 
 * 侧边的字母栏
 * @author Administrator
 *
 */
public class SideBar extends View {  
	 private char[] l;  
	    private SectionIndexer sectionIndexter = null;  
	    private ListView list;  
	    private TextView mDialogText;
//	    private  int m_nItemHeight = 30;  
	    WindowManager mWindowManager = null;
	    WindowManager.LayoutParams lp = null;
	    
	    int screen_width;
	    int screen_height;
	    
	    int size;
	    
	    Paint paint ;  
        int  m_nItemHeight;
        
        float widthCenter;  
        
	    
	    public SideBar(Context context) {  
	        super(context);  
	    }  
	    public SideBar(Context context, AttributeSet attrs) {  
	        super(context, attrs);  
	    }
	    
	    public void init(Context context) {  
	    	
	    	paint = new Paint();  
	        
	    	paint.setColor(Color.DKGRAY);
		    paint.setTextAlign(Paint.Align.CENTER);  
		    paint.setAntiAlias(true);
		        
	        l = new char[] { '#', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',  
	                'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };  
	        
	        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	        
	        mDialogText = (TextView) LayoutInflater.from(context).inflate(R.layout.list_position, null);
			mDialogText.setVisibility(View.INVISIBLE);
	        
	        lp = new WindowManager.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
					WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
	        mWindowManager.addView(mDialogText, lp);
	        
	        DisplayMetrics dm = new DisplayMetrics();   
	        mWindowManager.getDefaultDisplay().getMetrics(dm);   
	        
	      screen_width = dm.widthPixels;
	      screen_height = dm.heightPixels;
	      size = context.getResources().getDimensionPixelSize(R.dimen.list_position_size);
	    } 
	    
	    public SideBar(Context context, AttributeSet attrs, int defStyle) {  
	        super(context, attrs, defStyle); 
	    } 
	    
	    public void setListView(ListView _list) {  
	        list = _list;  
	        sectionIndexter = (SectionIndexer) _list.getAdapter();  
	    } 
	    
	    public void setTextView(TextView mDialogText) {  
	    	this.mDialogText = mDialogText;  
	    }  
	    
	    public boolean onTouchEvent(MotionEvent event) {  
	        super.onTouchEvent(event);  
	        int i = (int) event.getY();  
	        int idx = i / m_nItemHeight;  
	        if (idx >= l.length) {  
	            idx = l.length - 1;  
	        } else if (idx < 0) {  
	            idx = 0;  
	        }  
	        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {  
	        	mDialogText.setVisibility(View.VISIBLE);
	        	mDialogText.setText(""+l[idx]);
	        	
	        	lp.x = (int) (screen_width/2 - size);
	        	lp.y = (int) event.getY() - screen_height/2+size;
	        	
	        	mWindowManager.updateViewLayout(mDialogText, lp);
	        	
	        	
	            if (sectionIndexter == null) {  
	                sectionIndexter = (SectionIndexer) list.getAdapter();  
	            }  
	            int position = sectionIndexter.getPositionForSection(l[idx]);  
	            
	            if (position == -1) {  
	                return true;  
	            }  
	            
	            list.setSelection(position);  
	        }else{
	        	mDialogText.setVisibility(View.INVISIBLE); 
	        }  
	        return true;  
	    }  
	    protected void onDraw(Canvas canvas) {  
	    	
	    	int text_size  = (getMeasuredHeight()-10)/l.length;
	    	
//	    	System.out.println("  text_size  ---> " + text_size);
	    	
	        paint.setTextSize(text_size); 
	        
	        m_nItemHeight = (int) (getMeasuredHeight()/l.length);
//	        int gap = (getMeasuredHeight()- (27*text_size) ) / 27;
//	        System.out.println(" gap ---> " + gap);
	        
	        widthCenter = getMeasuredWidth() / 2; 
	        
	        for (int i = 0; i < l.length; i++) {  
	            canvas.drawText(String.valueOf(l[i]), widthCenter, (i+1) * text_size + (text_size/4) , paint);  
	        }  
	        super.onDraw(canvas);  
	    }  
}
