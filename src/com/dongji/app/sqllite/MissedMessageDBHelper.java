package com.dongji.app.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MissedMessageDBHelper extends SQLiteOpenHelper {

	private static final int version = 1;
	private static final String db_name = "missedmessage";
	public static final String table = "missedmessage";

	public static final String _ID = "_id";
	public static final String NUMBER = "number";
	public static final String DIR = "dir";
	public static final String DATE_TIME = "_date_time";

	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "+ table + " (" + 
							_ID + " INTEGER NOT NULL, " + 
							NUMBER +" TEXT NOT NULL,"+
							DIR + " TEXT NOT NULL,"+
							DATE_TIME + " TEXT," + 
							"PRIMARY KEY (" + _ID+ "));";

	
	public MissedMessageDBHelper(Context context) {
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
