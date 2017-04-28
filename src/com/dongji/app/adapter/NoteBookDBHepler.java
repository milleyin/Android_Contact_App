package com.dongji.app.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NoteBookDBHepler extends SQLiteOpenHelper {

	static final int NOTEBOOK_MAX_ROW = 1000;

	private static final int version = 1;
	private static final String db_name = "notebook";
	public static final String table = "notebook";

	public static final String _ID = "_id";
	public static final String CONTENT = "_content";
	public static final String DATE_TIME = "_date_time";

	private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
			+ table + " (" + _ID + " INTEGER NOT NULL, " + CONTENT
			+ " TEXT NOT NULL," + DATE_TIME + " TEXT," + "PRIMARY KEY (" + _ID
			+ "));";

	public NoteBookDBHepler(Context context) {
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
