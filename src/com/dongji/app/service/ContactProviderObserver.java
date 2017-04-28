package com.dongji.app.service;

import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;

public class ContactProviderObserver extends ContentObserver {

	public ContactProviderObserver(Handler handler) {
		super(handler);
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
	}
}
