package com.dongji.app.ui;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.ImageView;

import com.dongji.app.addressbook.R;

/**
 * 
 * 弧形输入键盘
 * 
 * @author Administrator
 *
 */
public class ArcInputPanel extends ImageView {
	
	boolean isAlready = false;

	int mWidth; //view的宽度 
	int mHeight; //view的高度
	
	Paint line_p;  //线
	int line_width ;//线的宽度

	///////////所有的点  从 下至上  左至右     p为水平的点   vP为竖直的点
	Point p0_0 = new Point();
	Point p0_1 = new Point();
	Point vP0_0 = new Point();
	Point vP0_1 = new Point();
	
	Point p1_0 = new Point();
	Point p1_1 = new Point();
	Point vP1_0 = new Point();
	Point vP1_1 = new Point();
	
	Point p2_0 = new Point();
	Point p2_1 = new Point();
	Point vP2_0 = new Point();
	Point vP2_1 = new Point();
	
	
	Point p3_0 = new Point();
	Point p3_1 = new Point();
	Point vP3_0 = new Point();
	Point vP3_1 = new Point();
	
	Point p4_0 = new Point();
	Point p4_1 = new Point();
	Point vP4_0 = new Point();
	Point vP4_1 = new Point();
	
	Point p5_0 = new Point();
	Point p5_1 = new Point();

	Paint item_p; //每个按键区域点击下的画笔

	int item_height; //水平直线之间的间隔高度
	
	float density ; //像素密度参数
	
	
	/////////// 每个按钮  以及  输入框的内容区域
	Path content_path = new Path(); //内容区域
	Paint content_paint ;
	
	Path number_1_path = new Path();
	Path number_2_path = new Path();
	Path number_3_path = new Path();
	
	Path number_4_path = new Path();
	Path number_5_path = new Path();
	Path number_6_path = new Path();
	
	Path number_7_path = new Path();
	Path number_8_path = new Path();
	Path number_9_path = new Path();
	
	Path number_xing_path = new Path();
	Path number_0_path = new Path();
	Path number_jing_path = new Path();
	
	Path number_1_text_path = new Path();
	
	Path delete_buttion_path = new Path();
	Bitmap bmp_delete;
	Paint bmp_paint;
	
	////////// path中点的集合
	Point [] number1_ps = new Point [4];
	Point [] number2_ps = new Point [4];
	Point [] number3_ps = new Point [4];
	
	Point [] number4_ps = new Point [4];
	Point [] number5_ps = new Point [4];
	Point [] number6_ps = new Point [4];
	
	Point [] number7_ps = new Point [4];
	Point [] number8_ps = new Point [4];
	Point [] number9_ps = new Point [4];
	
	Point [] numberXing_ps = new Point [4];
	Point [] number0_ps = new Point [4];
	Point [] numberJing_ps = new Point [4];
	
	Point [] delete_area = new Point [4];
	Point [] edittext_input_area = new Point [4];
	
	
	Point eP0_0 = new Point();
	Point eP0_1 = new Point();
	Path editText_path = new Path();
	Paint ediText_paint ;
	String input_text_str=""; //所有输入的内容
	String show_text_str=""; //当前显示的内容
	
	//三种字体的大小
	int et_text_size_big;
	int et_text_size_mid;
	int et_text_size_small;
	
	//三种字体大小对应的所能输入的最大字符数
	int max_big_char_num; 
	int max_mid_char_num;
	int max_small_char_num;
	
	Point d_p_0 = new Point();
	Point d_p_1 = new Point();
	Point d_p_2 = new Point();
	Point d_p_3 = new Point();
	
	//正常的状态
	Paint text_paint_big ; 
	Paint text_paint_small ; 
	
	//按下的状态
	Paint text_paint_big_pressed;
	Paint text_paint_small_pressed;
	
	int big_text_size;
	int small_text_size;
	
	//距离参数
	int arch_width_1;
	int arch_width_2;
	int arch_width_3;
	
	
	String cur_input_str = "-1"; 
	
	//点击了内容外区域回调
	private OnTouchOutsideArea mOnTouchOutsideArea;
	
	//输入内容改变 触发回调
	private OnTextchange mOnTextchange;
	
	//按钮点击  触发回调
	private OnItemPressed mOnItemPressed;

	
	//长按 相关
	private int mLastMotionX, mLastMotionY;   
	private boolean isMoved;   //是否移动了    
	private Runnable mLongPressRunnable;  //长按的runnable     
	private static final int TOUCH_SLOP = 40;  //移动的阈值   
	boolean isLongPresseding = false; //是否长按
	
	
	Handler mHandler = new Handler()
	{
		public void handleMessage(android.os.Message msg){
			invalidate();
		};
	};

	
	public ArcInputPanel(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		//必须先调用此方法
		setLayerType();
		
		DisplayMetrics dm = new DisplayMetrics();
		((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		Resources mResources = context.getResources();
		
		line_width = mResources.getDimensionPixelSize(R.dimen.line_heigh);
		item_height = mResources.getDimensionPixelSize(R.dimen.dialing_panel_line_height);
		
		big_text_size = (int) (32 * density);
		small_text_size = (int) (16 * density );
		
		et_text_size_big = (int) (36 * density);
		et_text_size_mid = (int) (26 * density);
		et_text_size_small = (int) (18 * density);
		
		content_paint = new Paint();
		content_paint.setColor(Color.WHITE);
		content_paint.setStyle(Style.FILL);
		
		line_p = new Paint();
		line_p.setColor(mResources.getColor(R.color.line_color));
		line_p.setStrokeWidth(line_width);
		line_p.setAntiAlias(true); //抗锯齿
		
		item_p = new Paint();
		item_p.setStyle(Style.FILL);
		item_p.setColor(mResources.getColor(R.color.dialing_panel_press));
		item_p.setAntiAlias(true);
		
		text_paint_big = new Paint();
		text_paint_big.setColor(mResources.getColor(R.color.text_color_dialing_panel_big));
		text_paint_big.setTextSize(big_text_size);
		text_paint_big.setAntiAlias(true);
		
		text_paint_big_pressed = new Paint();
		text_paint_big_pressed.setColor(Color.WHITE);
		text_paint_big_pressed.setTextSize(big_text_size);
		text_paint_big_pressed.setAntiAlias(true);
		
		text_paint_small = new Paint();
		text_paint_small.setColor(mResources.getColor(R.color.text_color_dialing_panel_small));
		text_paint_small.setTextSize(small_text_size);
		text_paint_small.setAntiAlias(true);
		
		text_paint_small_pressed = new Paint();
		text_paint_small_pressed.setColor(Color.WHITE);
		text_paint_small_pressed.setTextSize(small_text_size);
		text_paint_small_pressed.setAntiAlias(true);
		
		bmp_paint = new Paint();
		bmp_paint.setDither(true);
		
		ediText_paint = new Paint();
		ediText_paint.setColor(mResources.getColor(R.color.text_color_dialing_panel_big));
		ediText_paint.setTextSize(big_text_size);
		ediText_paint.setAntiAlias(true);
		ediText_paint.setTextAlign(Align.RIGHT);
		
		//长按线程
		mLongPressRunnable = new Runnable() {

			@Override
			public void run() {
				handleLongPressed();
			}
		};
		
	}
	
	/**
	 * 针对 4.0.3  SDK  15 出现的: drawTextOnPath 失效的bug
	 */
	@TargetApi(15)
	void setLayerType()
	{
		try {
			 int version = Integer.valueOf(android.os.Build.VERSION.SDK);
			 
			if(version>=15)
			{
				this.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
			}
		
			System.out.println("  version  ---> " + version);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 
	 * 计算所有的需要用到的点   由点构成直接   再组成每个按钮的路径(Path)
	 * 
	 * @param width view的宽度
	 * @param height view的高度 
	 */
	public void init(int width , int height)
	{
//		this.mWidth = getMeasuredWidth();
//		this.mHeight = getMeasuredHeight();
		
//		System.out.println(" mHeight  --->" + getMeasuredHeight());
//		System.out.println(" mWidth  --->" + getMeasuredWidth());
		
		this.mWidth = width;
		this.mHeight = height;
		
//		System.out.println(" mHeight  --->" + mWidth);
//		System.out.println(" mWidth  --->" + mHeight);
		
        
		p0_0.x = 0;
		p0_0.y = mHeight;
		
		p0_1.x = mWidth;
		p0_1.y = mHeight;
		
		List<Point> l_points = new ArrayList<Point>();
		l_points.add(p1_0);
		l_points.add(p2_0);
		l_points.add(p3_0);
		l_points.add(p4_0);
		l_points.add(p5_0);
		
		for(int i = 0 ; i<5;i++)
		{
			if(i==0)
			{
				p1_0.x = 0;
				p1_0.y = mHeight - line_width - (item_height/2);
				
			}else{
				Point pre_p = l_points.get(i-1);
				Point cur_p = l_points.get(i);
				cur_p.x = 0;
				cur_p.y = pre_p.y - item_height;
			}
		}
		
		List<Point> r_points = new ArrayList<Point>();
		r_points.add(p1_1);
		r_points.add(p2_1);
		r_points.add(p3_1);
		r_points.add(p4_1);
		r_points.add(p5_1);
		
		for(int i = 0 ; i<5;i++)
		{
			if(i==0)
			{
				p1_1.x = mWidth;
				p1_1.y = mHeight - line_width - item_height - (int)(35*density);
				
			}else{
				Point pre_p = r_points.get(i-1);
				Point cur_p = r_points.get(i);
				cur_p.x = mWidth;
				cur_p.y = pre_p.y - item_height;
			}
		}
		
//		double tanA = Math.tan( (p1_0.y - p1_1.y ) /(p1_0.x - p1_1.x));
		
		double tanA = ((double)p1_0.y - (double)p1_1.y ) / ((double)p1_0.x - (double)p1_1.x) ;
		System.out.println(" tanA  --->" + tanA);
		
		double xx =  (mWidth/14) *6;
		arch_width_1 = (int) xx;
		
		double yy =  xx/ tanA;
		
		vP0_0.x = (int) xx;
		vP0_0.y = mHeight;
		
		int a_y = (int) (Math.abs(yy) - mHeight);
		
		System.out.println(" yy --->" + yy);
		System.out.println(" a_y ---->" + a_y);
		
		////////////////第一条竖线
		Line ver_line_1 = new Line(xx,mHeight,0,-a_y);
		
		Line hor_line_4 = new Line(p4_0.x, p4_0.y, p4_1.x, p4_1.y);
		Line hor_line_3 = new Line(p3_0.x, p3_0.y, p3_1.x,  p3_1.y);
		Line hor_line_2 = new Line(p2_0.x, p2_0.y, p2_1.x,  p2_1.y);
		Line hor_line_1 = new Line(p1_0.x, p1_0.y, p1_1.x,  p1_1.y);
		
		
		com.dongji.app.ui.Point intersect_p4 =  ver_line_1.intersects(hor_line_4);
		com.dongji.app.ui.Point intersect_p3 =  ver_line_1.intersects(hor_line_3);
		com.dongji.app.ui.Point intersect_p2 =  ver_line_1.intersects(hor_line_2);
		com.dongji.app.ui.Point intersect_p1 =  ver_line_1.intersects(hor_line_1);
		
//		System.out.println(" 交点 : --->" +  p.x + " , " + p.y);
		
		vP4_0.x = (int) intersect_p4.x;
		vP4_0.y = (int) intersect_p4.y;
		
		vP3_0.x = (int) intersect_p3.x;
		vP3_0.y = (int) intersect_p3.y;
		
		vP2_0.x = (int) intersect_p2.x;
		vP2_0.y = (int) intersect_p2.y;
		
		vP1_0.x = (int) intersect_p1.x;
		vP1_0.y = (int) intersect_p1.y;
		
		
	   ////////////////第二条竖线
       double xxx =  (mWidth/14) * 11;
       arch_width_2 = (int) ((int) xxx - xx);
       arch_width_3 = mWidth - arch_width_1 - arch_width_2;
		
       System.out.println(" xxxx ---->" + xxx);
       
	   double yyy =  xxx/ tanA;
		
		vP0_1.x = (int) xxx;
		vP0_1.y = mHeight;
		
		
		int a_yy = (int) (Math.abs(yyy) - mHeight);
		
		Line ver_line_2 = new Line(xxx,mHeight,0,-a_yy);
		
		com.dongji.app.ui.Point intersect_p4_1 =  ver_line_2.intersects(hor_line_4);
		com.dongji.app.ui.Point intersect_p3_1 =  ver_line_2.intersects(hor_line_3);
		com.dongji.app.ui.Point intersect_p2_1 =  ver_line_2.intersects(hor_line_2);
		com.dongji.app.ui.Point intersect_p1_1 =  ver_line_2.intersects(hor_line_1);
		
		vP4_1.x = (int) intersect_p4_1.x;
		vP4_1.y = (int) intersect_p4_1.y;
		
		vP3_1.x = (int) intersect_p3_1.x;
		vP3_1.y = (int) intersect_p3_1.y;
		
		vP2_1.x = (int) intersect_p2_1.x;
		vP2_1.y = (int) intersect_p2_1.y;
		
		vP1_1.x = (int) intersect_p1_1.x;
		vP1_1.y = (int) intersect_p1_1.y;
		
		
		//内容区域
		content_path.moveTo(p5_0.x, p5_0.y);
		content_path.lineTo(p5_1.x, p5_1.y);
		content_path.lineTo(p0_1.x, p0_1.y);
		content_path.lineTo(p0_0.x, p0_0.y);
		
		content_path.close();
		
		
		//计算每个按钮的点击区域路径
		number_1_path.moveTo(p4_0.x, p4_0.y);
		number_1_path.lineTo(vP4_0.x, vP4_0.y);
		number_1_path.lineTo(vP3_0.x, vP3_0.y);
		number_1_path.lineTo(p3_0.x, p3_0.y);
		number_1_path.close();
		
		number1_ps[0] = p4_0;
		number1_ps[1] = vP4_0;
		number1_ps[2] = vP3_0;
		number1_ps[3] = p3_0;
		
		
		number_2_path.moveTo(vP4_0.x , vP4_0.y);
		number_2_path.lineTo(vP4_1.x , vP4_1.y);
		number_2_path.lineTo(vP3_1.x , vP3_1.y);
		number_2_path.lineTo(vP3_0.x , vP3_0.y);
		number_2_path.close();
		
		number2_ps[0] = vP4_0;
		number2_ps[1] = vP4_1;
		number2_ps[2] = vP3_1;
		number2_ps[3] = vP3_0;
		
		number_3_path.moveTo(vP4_1.x , vP4_1.y);
		number_3_path.lineTo(p4_1.x , p4_1.y);
		number_3_path.lineTo(p3_1.x , p3_1.y);
		number_3_path.lineTo(vP3_1.x , vP3_1.y);
		number_3_path.close();
		
		number3_ps[0] = vP4_1;
		number3_ps[1] = p4_1;
		number3_ps[2] = p3_1;
		number3_ps[3] = vP3_1;
		
		number_4_path.moveTo(p3_0.x, p3_0.y);
		number_4_path.lineTo(vP3_0.x, vP3_0.y);
		number_4_path.lineTo(vP2_0.x, vP2_0.y);
		number_4_path.lineTo(p2_0.x, p2_0.y);
		number_4_path.close();
		
		number4_ps[0] = p3_0;
		number4_ps[1] = vP3_0;
		number4_ps[2] = vP2_0;
		number4_ps[3] = p2_0;
		
		number_5_path.moveTo(vP3_0.x , vP3_0.y);
		number_5_path.lineTo(vP3_1.x , vP3_1.y);
		number_5_path.lineTo(vP2_1.x , vP2_1.y);
		number_5_path.lineTo(vP2_0.x , vP2_0.y);
		number_5_path.close();
		
		number5_ps[0] = vP3_0;
		number5_ps[1] = vP3_1;
		number5_ps[2] = vP2_1;
		number5_ps[3] = vP2_0;
		
		number_6_path.moveTo(vP3_1.x , vP3_1.y);
		number_6_path.lineTo(p3_1.x , p3_1.y);
		number_6_path.lineTo(p2_1.x , p2_1.y);
		number_6_path.lineTo(vP2_1.x , vP2_1.y);
		number_6_path.close();
		
		number6_ps[0] = vP3_1;
		number6_ps[1] = p3_1;
		number6_ps[2] = p2_1;
		number6_ps[3] = vP2_1;
		
		number_7_path.moveTo(p2_0.x, p2_0.y);
		number_7_path.lineTo(vP2_0.x, vP2_0.y);
		number_7_path.lineTo(vP1_0.x, vP1_0.y);
		number_7_path.lineTo(p1_0.x, p1_0.y);
		number_7_path.close();
		
		number7_ps[0] = p2_0;
		number7_ps[1] = vP2_0;
		number7_ps[2] = vP1_0;
		number7_ps[3] = p1_0;
		
		number_8_path.moveTo(vP2_0.x , vP2_0.y);
		number_8_path.lineTo(vP2_1.x , vP2_1.y);
		number_8_path.lineTo(vP1_1.x , vP1_1.y);
		number_8_path.lineTo(vP1_0.x , vP1_0.y);
		number_8_path.close();
		
		number8_ps[0] = vP2_0;
		number8_ps[1] = vP2_1;
		number8_ps[2] = vP1_1;
		number8_ps[3] = vP1_0;
		
		number_9_path.moveTo(vP2_1.x , vP2_1.y);
		number_9_path.lineTo(p2_1.x , p2_1.y);
		number_9_path.lineTo(p1_1.x , p1_1.y);
		number_9_path.lineTo(vP1_1.x , vP1_1.y);
		number_9_path.close();
		
		number9_ps[0] = vP2_1;
		number9_ps[1] = p2_1;
		number9_ps[2] = p1_1;
		number9_ps[3] = vP1_1;
		
		number_xing_path.moveTo(p1_0.x, p1_0.y);
		number_xing_path.lineTo(vP1_0.x, vP1_0.y);
		number_xing_path.lineTo(vP0_0.x, vP0_0.y);
		number_xing_path.lineTo(p0_0.x, p0_0.y);
		number_xing_path.close();
		
		numberXing_ps[0] = p1_0;
		numberXing_ps[1] = vP1_0;
		numberXing_ps[2] = vP0_0;
		numberXing_ps[3] = p0_0;
		
		number_0_path.moveTo(vP1_0.x , vP1_0.y);
		number_0_path.lineTo(vP1_1.x , vP1_1.y);
		number_0_path.lineTo(vP0_1.x , vP0_1.y);
		number_0_path.lineTo(vP0_0.x , vP0_0.y);
		number_0_path.close();
		
		number0_ps[0] = vP1_0;
		number0_ps[1] = vP1_1;
		number0_ps[2] = vP0_1;
		number0_ps[3] = vP0_0;
		
		number_jing_path.moveTo(vP1_1.x , vP1_1.y);
		number_jing_path.lineTo(p1_1.x , p1_1.y);
		number_jing_path.lineTo(p0_1.x , p0_1.y);
		number_jing_path.lineTo(vP0_1.x , vP0_1.y);
		number_jing_path.close();
		
		numberJing_ps[0] = vP1_1;
		numberJing_ps[1] = p1_1;
		numberJing_ps[2] = p0_1;
		numberJing_ps[3] = vP0_1;
		

		 ////////////////////////////第三条竖线
		 double xxxx = mWidth;
			
	     System.out.println(" xxxx ---->" + xxx);
	       
		 double yyyy =  xxxx/ tanA;
			
		 int a_yyy = (int) (Math.abs(yyyy) - mHeight);
			
		 Line ver_line_3 = new Line(xxxx,mHeight,0,-a_yyy);
		 Line hor_line_5 = new Line(p5_0.x, p5_0.y, p5_1.x, p5_1.y);
		   
		 com.dongji.app.ui.Point pp1 = ver_line_3.intersects(hor_line_5);
		 com.dongji.app.ui.Point pp2 = ver_line_3.intersects(hor_line_4);
		 
		 d_p_0.x = (int) pp1.x;
		 d_p_0.y = (int) pp1.y;
		 
		 d_p_2.x = (int) pp2.x;
		 d_p_2.y = (int) pp2.y;
		 
		 //删除键的路径
		 delete_buttion_path.moveTo((float)pp1.x,(float)pp1.y);
		 delete_buttion_path.lineTo(p5_1.x, p5_1.y);
		 delete_buttion_path.lineTo(p4_1.x, p4_1.y);
		 delete_buttion_path.lineTo((float)pp2.x,(float)pp2.y);
		 delete_buttion_path.close();
		 
		 delete_area[0] = d_p_0;
		 delete_area[1] = p5_1;
		 delete_area[2] = p4_1;
		 delete_area[3] = d_p_2;
		 
		 //将删除键的图片旋转一定的角度(tanA)
		 Matrix  m = new Matrix();
		 m.postRotate((float) Math.toDegrees((float) Math.atan(tanA)));
		 Bitmap orignal = BitmapFactory.decodeResource(getResources(), R.drawable.back_delete);
		 bmp_delete = Bitmap.createBitmap(orignal, 0, 0, orignal.getWidth(), orignal.getHeight(), m, false);
		 
		 
		 ///////////////////////////////输入显示的text路径 (方向为 屏幕的右边  到 屏幕的左边)
		 eP0_0.x = p5_0.x;
		 eP0_0.y = p5_0.y + item_height/2 + (big_text_size)/3 ;
		 
		 eP0_1.x = (int) (d_p_0.x - (5 * density));
		 eP0_1.y = d_p_0.y + item_height/2 + (big_text_size)/3 ;
		 
		 editText_path.moveTo(eP0_1.x,eP0_1.y);
		 editText_path.lineTo(eP0_0.x,eP0_0.y);
		 editText_path.close();
		 
		 edittext_input_area[0] = p5_0;
		 edittext_input_area[1] = d_p_0;
		 edittext_input_area[2] = d_p_2;
		 edittext_input_area[3] = p4_0;
		 
		 Paint temp_text_p = new Paint();
		 temp_text_p.setTextSize(et_text_size_big);
		 int big_text_width = (int) temp_text_p.measureText("8");
		 
		 temp_text_p.setTextSize(et_text_size_mid);
		 int mid_text_width = (int) temp_text_p.measureText("8");
		 
		 temp_text_p.setTextSize(et_text_size_small);
		 int small_text_width = (int) temp_text_p.measureText("8");
		 
		 int content_length = (eP0_1.x - eP0_0.x);
//		 System.out.println(" content_length ----> " + content_length);
//		 System.out.println(" big_text_width ----> " + big_text_width);
		 
		 max_big_char_num = content_length/big_text_width;
		 max_mid_char_num = content_length/mid_text_width;
		 max_small_char_num = content_length/small_text_width;
		 
		 
//		 System.out.println(" et_text_size_big --->" + et_text_size_big);
//		 System.out.println(" et_text_size_mid --->" + et_text_size_mid);
//		 System.out.println(" et_text_size_small --->" + et_text_size_small);
		 
//		 System.out.println("  max_big_char_num ---->" + max_big_char_num);
//		 System.out.println("  max_mid_char_num ---->" + max_mid_char_num);
//		 System.out.println("  max_small_char_num ---->" + max_small_char_num);
		 
		 
		 isAlready = true;
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		
		if(!isAlready)
		{
			return ;
		}
		
		canvas.drawPath(content_path, content_paint);
		
		//线
		//水平
		canvas.drawLine(p1_0.x, p1_0.y, p1_1.x, p1_1.y, line_p);
		canvas.drawLine(p2_0.x, p2_0.y, p2_1.x, p2_1.y, line_p);
		canvas.drawLine(p3_0.x, p3_0.y, p3_1.x, p3_1.y, line_p);
		canvas.drawLine(p4_0.x, p4_0.y, p4_1.x, p4_1.y, line_p);
		canvas.drawLine(p5_0.x, p5_0.y, p5_1.x, p5_1.y, line_p);
		
		//竖直
		canvas.drawLine(vP0_0.x, vP0_0.y, vP4_0.x, vP4_0.y, line_p);
		canvas.drawLine(vP0_1.x, vP0_1.y, vP4_1.x, vP4_1.y, line_p);
		
		//数字
		canvas.drawTextOnPath("1", number_1_path , (arch_width_1/20) * 3 , item_height/2 + (big_text_size)/3 , text_paint_big);
		
		canvas.drawTextOnPath("4", number_4_path , (arch_width_1/20) * 4 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("HGI", number_4_path , (arch_width_1/20) * 8 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("7", number_7_path , (arch_width_1/20) * 5 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("PQRS", number_7_path , (arch_width_1/20) * 9 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("*", number_xing_path , (arch_width_1/20) * 8 , item_height/2 + (big_text_size)/3 , text_paint_big);
		
		
		canvas.drawTextOnPath("2", number_2_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("ABC", number_2_path , (arch_width_2/20) * 11 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("5", number_5_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("JKL", number_5_path , (arch_width_2/20) * 11 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("8", number_8_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("TUV", number_8_path , (arch_width_2/20) * 11 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("0", number_0_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("+", number_0_path , (arch_width_2/20) * 12 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		
		canvas.drawTextOnPath("3", number_3_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("DEF", number_3_path , (arch_width_3/20) * 15 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("6", number_6_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("MNO", number_6_path , (arch_width_3/20) * 15 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("9", number_9_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big);
		canvas.drawTextOnPath("WXYZ", number_9_path , (arch_width_3/20) * 15 , item_height/2 + (small_text_size)/3 , text_paint_small);
		
		canvas.drawTextOnPath("#", number_jing_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big);
		
		//删除键
		canvas.drawBitmap(bmp_delete, d_p_0.x + bmp_delete.getWidth()/2, d_p_0.y + (item_height/2) - bmp_delete.getHeight()/5 * 3, bmp_paint);
		
		//输入的字符
		canvas.drawTextOnPath(show_text_str, editText_path , 0 , 0, ediText_paint);
		
		//点击效果
		drawPressEffect(canvas);
		
	}
	
	/**
	 * 
	 * 按下去的效果
	 * 
	 * @param canvas
	 */
	void drawPressEffect(Canvas canvas)  
	{
		if (cur_input_str.equals("1")) 
		{
			canvas.drawPath(number_1_path, item_p);
			
			canvas.drawTextOnPath("1", number_1_path , (arch_width_1/20) * 3 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			
		}else if(cur_input_str.equals("2")){
			
			canvas.drawPath(number_2_path, item_p);
			
			canvas.drawTextOnPath("2", number_2_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("ABC", number_2_path , (arch_width_2/20) * 11 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("3")){
			
			canvas.drawPath(number_3_path, item_p);
			
			canvas.drawTextOnPath("3", number_3_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("DEF", number_3_path , (arch_width_3/20) * 15 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
			
		}else if(cur_input_str.equals("4")){
			
			canvas.drawPath(number_4_path, item_p);
			
			canvas.drawTextOnPath("4", number_4_path , (arch_width_1/20) * 4 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("HGI", number_4_path , (arch_width_1/20) * 8 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("5")){
			
			canvas.drawPath(number_5_path, item_p);
			
			canvas.drawTextOnPath("5", number_5_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("JKL", number_5_path , (arch_width_2/20) * 11 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("6")){
			
			canvas.drawPath(number_6_path, item_p);
			
			canvas.drawTextOnPath("6", number_6_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("MNO", number_6_path , (arch_width_3/20) * 15 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("7")){
			
			canvas.drawPath(number_7_path, item_p);
			
			canvas.drawTextOnPath("7", number_7_path , (arch_width_1/20) * 5 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("PQRS", number_7_path , (arch_width_1/20) * 9 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("8")){
			
			canvas.drawPath(number_8_path, item_p);
			
			canvas.drawTextOnPath("8", number_8_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("TUV", number_8_path , (arch_width_2/20) * 11 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("9")){
			
			canvas.drawPath(number_9_path, item_p);
			
			canvas.drawTextOnPath("9", number_9_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("WXYZ", number_9_path , (arch_width_3/20) * 15 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);
		}else if(cur_input_str.equals("*")){
			
			canvas.drawPath(number_xing_path, item_p);
			
			canvas.drawTextOnPath("*", number_xing_path , (arch_width_1/20) * 8 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
		}else if(cur_input_str.equals("0")){
			
			canvas.drawPath(number_0_path, item_p);
			
			canvas.drawTextOnPath("0", number_0_path , (arch_width_2/20) * 6 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
			canvas.drawTextOnPath("+", number_0_path , (arch_width_2/20) * 12 , item_height/2 + (small_text_size)/3 , text_paint_small_pressed);

		}else if(cur_input_str.equals("#")){
			
			canvas.drawPath(number_jing_path, item_p);
			
			canvas.drawTextOnPath("#", number_jing_path , (arch_width_3/20) * 7 , item_height/2 + (big_text_size)/3 , text_paint_big_pressed);
		}
		
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
//		System.out.println(" event x --->" + event.getX());
//		System.out.println(" event y --->" + event.getY());
		
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			
			mLastMotionX = x;   
			mLastMotionY = y;   
			isMoved = false;   
			
			postDelayed(mLongPressRunnable, ViewConfiguration.getLongPressTimeout()); //post长按监听Runnable 
			
			handleTouchDown(event);
			
//			System.out.println(" MotionEvent.ACTION_DOWN  ");
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			if(isMoved) break;   
			
			if(Math.abs(mLastMotionX-x) > TOUCH_SLOP  || Math.abs(mLastMotionY-y) > TOUCH_SLOP) {   //移动超过阈值，则表示取消了长按  
			isMoved = true;   
			removeCallbacks(mLongPressRunnable); //移除长按监听Runnable 
			isLongPresseding = false;
			
			}   
			break;
			
		case MotionEvent.ACTION_UP:
			
			removeCallbacks(mLongPressRunnable); //释放长长按  
			isLongPresseding = false;
			
			handleTextInput(cur_input_str);
			cur_input_str = "";
			break;

		default:
			break;
		}
		
		invalidate();
		
		return true;
	}
	
	/**
	 * 
	 * 处理字符输入 删除
	 * 
	 * @param s 输入的字符;  -1 为退格删除键
	 * 
	 */
	private void handleTextInput(String s)
	{
		
		int length = input_text_str.length();
		
		if(s!="-1")
		{
			input_text_str +=s;
			
		}else{ //退格删除
			
			if(length>0)
			{
				if(length==1)
				{
					input_text_str ="";
				}else{
					input_text_str = input_text_str.substring(0, length-1);
				}
			}
		}
		
		show_text_str = new String(input_text_str);
		
		
		int cur_length = input_text_str.length();
		
		if(cur_length <= max_big_char_num)
		{
			ediText_paint.setTextSize(et_text_size_big);
			
		}else if(cur_length <= max_mid_char_num){
			
			ediText_paint.setTextSize(et_text_size_mid);
			
		}else if (cur_length <= max_small_char_num){
			
			ediText_paint.setTextSize(et_text_size_small);
			
		}else {
		
			show_text_str = show_text_str.substring(length-max_small_char_num+1);
			ediText_paint.setTextSize(et_text_size_small);
		}
		
		//输入内容改变时，回调
		if(mOnTextchange!=null)
		{
			mOnTextchange.OnTextchange(input_text_str);
		}
	}
	
	
	/**
	 * 
	 * 处理按下去的点击事件
	 * 
	 * @param event
	 * 
	 * 
	 */
	private void handleTouchDown(MotionEvent event)
	{
		int x = (int) event.getX();
		int y = (int) event.getY();
		
		Point p = new Point(x, y);
		
//		System.out.println("  x ---> " + x + "  y --->" + y);
		
		boolean isContant = false;
		
		for(int i = 0; i<14;i++)
		{
			switch (i) {
			
			case 0:
				isContant = isPolygonContainPoint(p,number1_ps);
				if(isContant)
				{
					cur_input_str="1";
				}
				break;
				
			case 1:
				isContant = isPolygonContainPoint(p,number2_ps);
				if(isContant)
				{
					cur_input_str="2";
				}
				break;
				
			case 2:
				isContant = isPolygonContainPoint(p,number3_ps);
				if(isContant)
				{
					cur_input_str="3";
				}
				break;
				
			case 3:
				isContant = isPolygonContainPoint(p,number4_ps);
				if(isContant)
				{
					cur_input_str="4";
				}
				break;
				
			case 4:
				isContant = isPolygonContainPoint(p,number5_ps);
				if(isContant)
				{
					cur_input_str="5";
				}
				break;
				
			case 5:
				isContant = isPolygonContainPoint(p,number6_ps);
				if(isContant)
				{
					cur_input_str="6";
				}
				break;
				
			case 6:
				isContant = isPolygonContainPoint(p,number7_ps);
				if(isContant)
				{
					cur_input_str="7";
				}
				break;
				
			case 7:
				isContant = isPolygonContainPoint(p,number8_ps);
				if(isContant)
				{
					cur_input_str="8";
				}
				break;
				
			case 8:
				isContant = isPolygonContainPoint(p,number9_ps);
				if(isContant)
				{
					cur_input_str="9";
				}
				break;
				
			case 9:
				isContant = isPolygonContainPoint(p,number0_ps);
				if(isContant)
				{
					cur_input_str="0";
				}
				break;
				
			case 10:
				isContant = isPolygonContainPoint(p,numberXing_ps);
				if(isContant)
				{
					cur_input_str="*";
				}
				break;
				
			case 11:
				isContant = isPolygonContainPoint(p,numberJing_ps);
				if(isContant)
				{
					cur_input_str="#";
				}
				break;
				
			case 12: //删除键
				isContant = isPolygonContainPoint(p,delete_area);
				if(isContant)
				{
					cur_input_str="-1";
				}
				break;
				
			case 13: 
				isContant = isPolygonContainPoint(p,edittext_input_area); 
				break;
				

			default:
				break;
			}
			
			if(isContant)
			{
				break;
			}
		}
		
		if(!isContant)
		{
			if(mOnTouchOutsideArea!=null)
			{
				mOnTouchOutsideArea.OnTouchOutsideArea();
			}
			
		}else{
			
			if(mOnItemPressed!=null)
			{
				mOnItemPressed.OnItemPressed();
			}
		}
	}
	
	
	/**
	 * 触发长按
	 * 
	 */
	private void handleLongPressed()
	{
		System.out.println(" 长按！！！ ");
		
		if(cur_input_str.equals("-1")) //长按删除键
		{
			isLongPresseding = true;
			new Thread(new quickDeleteRunnable()).start();
		}
		
	}
	
	
	/**
	 * 长按返回键 触发的快速删除功能
	 * 
	 * @author Administrator
	 *
	 */
	class quickDeleteRunnable implements Runnable{

		@Override
		public void run() {
			while(isLongPresseding)
			{
				int length = input_text_str.length();
				
				if(length==0)
				{
					break;
				}
				
				if(length==1)
				{
					input_text_str ="";
				}else{
					input_text_str = input_text_str.substring(0, length-1);
				}
				handleTextInput("");
				
				mHandler.sendEmptyMessage(0);
				
				try {
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 检测一个点是否在给定的多边形区域内
	 * @param point 点
	 * @param vertexPointFs 多边形的顶点
	 * @return
	 */
   private boolean isPolygonContainPoint(Point point, Point[] vertexPointFs) {
		
		int nCross = 0;
		for (int i = 0; i < vertexPointFs.length; i++) {
			Point p1 = vertexPointFs[i];
			Point p2 = vertexPointFs[(i + 1) % vertexPointFs.length];
			if (p1.y == p2.y)
				continue;
			if (point.y < Math.min(p1.y, p2.y))
				continue;
			if (point.y >= Math.max(p1.y, p2.y))
				continue;
			double x = (double) (point.y - p1.y) * (double) (p2.x - p1.x)
					/ (double) (p2.y - p1.y) + p1.x;
			if (x > point.x)
				nCross++;
		}
		return (nCross % 2 == 1);
	}
	
   
   public interface OnTouchOutsideArea{
	   public void OnTouchOutsideArea();
   }

   public void setOnTouchOutsideArea(OnTouchOutsideArea mOnTouchOutsideArea) {
	this.mOnTouchOutsideArea = mOnTouchOutsideArea;
  }
   
   
   /**
    * 
    * 获取输入的内容
    * 
    * @return
    */
   public String getInputText()
   {
	   return input_text_str;
   }
   
   public void setIntpuText(String str)
   {
	   input_text_str = str;
	   handleTextInput("");
   }
   
  public interface OnTextchange
  {
	   public void OnTextchange(String s);
  }

  public void setmOnTextchange(OnTextchange mOnTextchange) 
  {
	   this.mOnTextchange = mOnTextchange;
  }
  
  public interface OnItemPressed{
	  public void OnItemPressed();
  }

  public void setOnItemPressed(OnItemPressed mOnItemPressed) {
	  this.mOnItemPressed = mOnItemPressed;
  }
  
}
