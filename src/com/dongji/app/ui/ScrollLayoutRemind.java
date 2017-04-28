package com.dongji.app.ui;

import com.dongji.app.addressbook.RemindsActivity;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;


public class ScrollLayoutRemind extends ViewGroup{
	private static final String TAG = "ScrollLayout";  
    private Scroller mScroller;  
    private VelocityTracker mVelocityTracker;  
      
    private  int mCurScreen;  
    private int mDefaultScreen = 0;  
      
    private static final int TOUCH_STATE_REST = 0;  
    private static final int TOUCH_STATE_SCROLLING = 1;  
      
    private static final int SNAP_VELOCITY = 600;  
    
    public float vRate = 0.6f;
    public int sVDur = 1000;
    public int normaVDur = 3000;
    public int perOffX = 28;
    public int offX = 10;
      
    private int mTouchState = TOUCH_STATE_REST;  
    private int mTouchSlop; 
    private int mTouchYSlop = 50 ;   //Y方向最小滑动距离 
    private float mLastMotionX;  
    private float mLastMotionY;  
    private OnCurrentViewChangedListener mOnCurrentViewChangedListener;  
    
    boolean isScrollerable = true; //是否可以左右滑动
    
    private OnScrollerFinish onScrollerFinish;
    boolean isFinish = false; 
    

    RemindsActivity remindLayout;
    
    public OnCurrentViewChangedListener getmOnCurrentViewChangedListener() {  
        return mOnCurrentViewChangedListener;  
    }  
  
    public void setmOnCurrentViewChangedListener(  
            OnCurrentViewChangedListener mOnCurrentViewChangedListener) {  
        this.mOnCurrentViewChangedListener = mOnCurrentViewChangedListener;  
    }  
  
    public interface OnCurrentViewChangedListener {  
  
        public void onCurrentViewChanged(View view, int currentview);  
    }  
      
    public ScrollLayoutRemind(Context context, AttributeSet attrs) {  
        this(context, attrs, 0);  
        // TODO 再执行这个函数       
        Log.i(TAG, "ScrollLayout(1)");  
    }  
  
    public ScrollLayoutRemind(Context context, AttributeSet attrs, int defStyle) {  
        super(context, attrs, defStyle);  
        // TODO 首先执行这个函数  
        Log.i(TAG, "ScrollLayout(2)");  
        mScroller = new Scroller(context);  
          
        mCurScreen = mDefaultScreen;  
        mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
        
        Log.i(TAG, "mTouchSlop ===== " + mTouchSlop);  
    }  
  
    @Override  
    protected void onLayout(boolean changed, int l, int t, int r, int b) {  
        // TODO Auto-generated method stub  
        Log.i(TAG, "onLayout");  
        Log.i(TAG, "changed === " + changed);  
//        if (changed) {  
            int childLeft = 0;  
            final int childCount = getChildCount();  
              
            for (int i=0; i<childCount; i++) {  
                final View childView = getChildAt(i);  
                if (childView.getVisibility() != View.GONE) {  
                    final int childWidth = childView.getMeasuredWidth();  
                    try {
                        childView.layout(childLeft, 0, childLeft+childWidth, childView.getMeasuredHeight());  
					} catch (Exception e) {
						e.printStackTrace();
					}
          
                              
                    childLeft += childWidth;  
                }  
            }  
//        }  
    }  
  
  
    @Override    
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {     
//        Log.e(TAG, "onMeasure");  
//        Log.e(TAG, "onMeasure widthMeasureSpec === " + widthMeasureSpec);  
//        Log.e(TAG, "onMeasure heightMeasureSpec ==== " + heightMeasureSpec);  
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);     
    
        final int width = MeasureSpec.getSize(widthMeasureSpec);   
//        Log.e(TAG, "width ==onMeasure== " + width);  
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);     
//        Log.e(TAG, "widthMode ==onMeasure== " + widthMode);  
//        if (widthMode != MeasureSpec.EXACTLY) {     
//            throw new IllegalStateException("ScrollLayout only canmCurScreen run at EXACTLY mode!");   
//        }     
    
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);     
//        if (heightMode != MeasureSpec.EXACTLY) {     
//            throw new IllegalStateException("ScrollLayout only can run at EXACTLY mode!");  
//        }     
    
        // The children are given the same width and height as the scrollLayout     
        final int count = getChildCount();    
//        Log.i(TAG, "count = getChildCount()  ===== " + count);  
        for (int i = 0; i < count; i++) {     
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);     
        }     
        // Log.e(TAG, "moving to screen "+mCurScreen);     
        scrollTo(mCurScreen * width, 0);           
    }    
      
    /** 
     * According to the position of current layout 
     * scroll to the destination page. 
     */  
    public void snapToDestination() {  
        final int screenWidth = getWidth();  
        final int destScreen = (getScrollX()+ screenWidth/2)/screenWidth;  
        
        if(mCurScreen>destScreen)
        {
        	if (remindLayout!=null) {
        		setOnScrollerFinish(new OnScrollerFinish() {
					
					@Override
					public void onScrollerFinish() {
						remindLayout.activatePreWeek();
					}
				});
			}
        	
        }else if(mCurScreen>destScreen){
        	if (remindLayout!=null) {
        		setOnScrollerFinish(new OnScrollerFinish() {
					
					@Override
					public void onScrollerFinish() {
						remindLayout.activateNextWeek();
					}
				});
			}
        }else{
        	setOnScrollerFinish(null);
        }
        
        snapToScreen(destScreen);  
    }  
      
    public void snapToScreen(int whichScreen) {  
        // get the valid layout page  
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));  
        if (getScrollX() != (whichScreen*getWidth())) {  
              
            final int delta = whichScreen*getWidth()-getScrollX();  
            Log.i(TAG, "delta ===== " + delta);  
            mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta)*2);  
            mCurScreen = whichScreen;  
              
            isFinish = false;
              
            // 这里监听是为了在滑动的时候能改变的button的图            
            if (mOnCurrentViewChangedListener != null) {  
                mOnCurrentViewChangedListener.onCurrentViewChanged(this,mCurScreen);  
            }  
            invalidate();      
        }  
    }  
      
    public void setToScreen(int whichScreen) {  
        Log.i(TAG, "whichScreen ==setToScreen== " + whichScreen);  
        whichScreen = Math.max(0, Math.min(whichScreen, getChildCount()-1));  
        
        mCurScreen = whichScreen;  
        scrollTo(whichScreen*getWidth(), 0);  
        
        if (mOnCurrentViewChangedListener != null) {  
            mOnCurrentViewChangedListener.onCurrentViewChanged(this,mCurScreen);  
        }  
    }  
      
    public int getCurScreen() {  
        return mCurScreen;  
    }  
      
    @Override  
    public void computeScroll() {  
    	if (mScroller.computeScrollOffset()) { 
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());  
            postInvalidate();  
        } else{
        	if(onScrollerFinish != null && !isFinish)
        	{
        		onScrollerFinish.onScrollerFinish();
        		isFinish = true;
        	}
        } 
    }  
  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {  
        // TODO Auto-generated method stub  
          
        if (mVelocityTracker == null) {  
            mVelocityTracker = VelocityTracker.obtain();  
        }  
        
        mVelocityTracker.addMovement(event);  
          
        final int action = event.getAction();  
        final float x = event.getX();  
       
        switch (action) {  
        case MotionEvent.ACTION_DOWN:  
            if (!mScroller.isFinished()){  
                mScroller.abortAnimation();  
            }  
            mLastMotionX = x;  
            break;  
              
        case MotionEvent.ACTION_MOVE:  
        	    int deltaX = (int)(mLastMotionX - x);  
                mLastMotionX = x;  
                scrollBy(deltaX, 0);  
            break;  
              
        case MotionEvent.ACTION_UP:  
            final VelocityTracker velocityTracker = mVelocityTracker;     
            velocityTracker.computeCurrentVelocity(1000);     
            int velocityX = (int) velocityTracker.getXVelocity();   // 滑动的速度，左边为负，右边为正  
              
            if (velocityX > SNAP_VELOCITY && mCurScreen > 0) {
            	
            	if (remindLayout!=null) {
            		setOnScrollerFinish(new OnScrollerFinish() {
						
						@Override
						public void onScrollerFinish() {
							remindLayout.activatePreWeek();
						}
					});
				}
            	
                snapToScreen(mCurScreen - 1);     
            } else if (velocityX < -SNAP_VELOCITY  && mCurScreen < getChildCount() - 1) {
            	
            	if (remindLayout!=null) {
            		setOnScrollerFinish(new OnScrollerFinish() {
						
						@Override
						public void onScrollerFinish() {
							remindLayout.activateNextWeek();
						}
					});
				}
                snapToScreen(mCurScreen + 1);  
                
            } else {     
                snapToDestination();     
            }     
  
            if (mVelocityTracker != null) {     
                mVelocityTracker.recycle();     
                mVelocityTracker = null;     
            }     
            
            mTouchState = TOUCH_STATE_REST;     
              
            break;  
        case MotionEvent.ACTION_CANCEL:  
            mTouchState = TOUCH_STATE_REST;  
            break;  
        }  
          
        return true;  
    }  
  
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent ev) {  
        // TODO Auto-generated method stub  
        //Log.e(TAG, "onInterceptTouchEvent-slop:"+mTouchSlop);  
        if(isScrollerable)
        {
        	 final int action = ev.getAction();  
             
             System.out.println(" mTouchState ---> " + mTouchState);
             
             if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {  
                 return true;  
             }  
               
             final float x = ev.getX();  
             final float y = ev.getY();  
               
             switch (action) {  
             case MotionEvent.ACTION_MOVE:  
                 final int xDiff = (int)Math.abs(mLastMotionX-x);
                 final int yDiff = (int)Math.abs(mLastMotionY-y);
                 if (xDiff>mTouchSlop && yDiff < mTouchYSlop) {  
                     mTouchState = TOUCH_STATE_SCROLLING;  
                 }  
                 break;  
                   
             case MotionEvent.ACTION_DOWN:  
                 mLastMotionX = x;  
                 mLastMotionY = y;  
                 mTouchState = mScroller.isFinished()? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;  
                 break;  
                   
             case MotionEvent.ACTION_CANCEL:  
             case MotionEvent.ACTION_UP:  
                 mTouchState = TOUCH_STATE_REST;  
                 break;  
             }  
             return mTouchState != TOUCH_STATE_REST;  
        }else{
        	return false;
        }
    }  
    
    public void setScrollerable(boolean isScrollerable){
    	this.isScrollerable = isScrollerable;
    }
    
    public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}

    
	public void setOnScrollerFinish(OnScrollerFinish onScrollerFinish) {
		this.onScrollerFinish = onScrollerFinish;
	}

	public interface OnScrollerFinish
    {
    	public void onScrollerFinish();
    }

	public void setRemindLayout(RemindsActivity remindLayout) {
		this.remindLayout = remindLayout;
	}
	
}  
  

