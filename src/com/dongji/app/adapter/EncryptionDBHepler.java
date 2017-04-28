package com.dongji.app.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class EncryptionDBHepler extends SQLiteOpenHelper {

	static final int ENCRYPTION_MAX_ROW = 1000;

	private static final int version = 1;
	private static final String db_name = "encryption";
	public static final String table = "encryption_info";

	public static final String _ID = "_id";
	public static final String CONTACT_ID = "contact_id";
//	public static final String PHONE_PIN = "phone_pin";
//	public static final String PWD = "pwd";

	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ table + " (" + _ID + " INTEGER NOT NULL, " + CONTACT_ID
			+ " TEXT NOT NULL, PRIMARY KEY (" + _ID
			+ "));";
	
	public EncryptionDBHepler(Context context) {
		super(context, db_name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

}
