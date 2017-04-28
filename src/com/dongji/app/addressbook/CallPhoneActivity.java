package com.dongji.app.addressbook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class CallPhoneActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MainActivity.shortcutType=1;
		Intent intent=new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
