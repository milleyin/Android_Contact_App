package com.dongji.app.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SMSActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MainActivity.shortcutType=3;
		Intent intent=new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
