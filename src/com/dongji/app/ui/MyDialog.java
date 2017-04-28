package com.dongji.app.ui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.dongji.app.addressbook.R;


public class MyDialog implements OnClickListener{

	Context context;
	OnClickListener onClickListener;
	String tips = null;
	Dialog dialog;
	
	private static final int TYPE_NORMAL = 1;
	private static final int TYPE_EDITTEXT = 2;
	private static final int TYPE_CHECKBOX = 3;
	
	TextView tv_tips;
	Button btn_top_tips_yes;
	Button btn_top_tips_no;
	
	public MyDialog(Context context,String tips, OnClickListener onClickListener) {
		this.context = context;
		this.tips = tips;
		this.onClickListener = onClickListener;
	}
	
	public void normalDialog(){
		
		dialog = new Dialog(context,R.style.theme_myDialog_activity);
		dialog.setContentView(R.layout.mydialog);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		
		tv_tips = (TextView) dialog.findViewById(R.id.tv_tips);
		btn_top_tips_yes = (Button) dialog.findViewById(R.id.btn_top_tips_yes);
		btn_top_tips_no = (Button) dialog.findViewById(R.id.btn_top_tips_no);
		
		tv_tips.setText(tips);
		
		btn_top_tips_yes.setOnClickListener(onClickListener);
		btn_top_tips_no.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_top_tips_no:
			
			if ( dialog != null && dialog.isShowing())
				dialog.cancel();
			
			break;

		default:
			break;
		}
		
	}
	
	public void closeDialog() {
		dialog.cancel();
	}
	

}
