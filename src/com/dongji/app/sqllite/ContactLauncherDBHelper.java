package com.dongji.app.sqllite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class ContactLauncherDBHelper extends SQLiteOpenHelper {

	private static final int version = 1;
	private static final String db_name = "contactlauncer";
	public static final String table = "contactlauncer";
	
	public static final String _ID = "_id";
	public static final String CONTACT_ID = "contact_id";
	public static final String CONTACTNAME = "_contactName";
	public static final String NUMBER = "_number";
	public static final String PHOTO = "_photo";
	
	public static final String heat_table = "heat";
	
	public static final String HEAT = "_heat";

	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "+ table + " (" + 
							_ID + " INTEGER NOT NULL, " + 
							CONTACTNAME + " TEXT,"+
							NUMBER +" TEXT NOT NULL,"+
							PHOTO + " BYTE,"+
							"PRIMARY KEY (" + _ID+ "));";
	
	private static final String CREATE_HEAT_TABLE = "CREATE TABLE IF NOT EXISTS "+ heat_table + " (" + 
							_ID + " INTEGER NOT NULL, " + 
							CONTACT_ID + " INTEGER NOT NULL," +
							CONTACTNAME + " TEXT,"+
							NUMBER +" TEXT NOT NULL,"+
							HEAT +" INTEGER,"+
							"PRIMARY KEY (" + _ID+ "));";

	
	public ContactLauncherDBHelper(Context context) {
		super(context, db_name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_SQL);
		db.execSQL(CREATE_HEAT_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}
}
