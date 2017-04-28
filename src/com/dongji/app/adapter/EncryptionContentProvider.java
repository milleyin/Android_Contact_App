package com.dongji.app.adapter;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class EncryptionContentProvider extends ContentProvider {
	private static final String AUTHORITY = "com.dongji.app.addressbook.encryption";
	private static final String table = EncryptionDBHepler.table;
	
	private EncryptionDBHepler encryptionDBHepler;
	private DatabaseUtils.InsertHelper insertHelper;
	
	private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	private static final int all = 11;
	private static final int single = 12;
	public static final Uri URIS = Uri.parse("content://"+AUTHORITY+"/");
	public static final Uri URI_SINGLE = Uri.parse("content://"+AUTHORITY+"/#");
	
	static {
		matcher.addURI(AUTHORITY, "/", all);
		matcher.addURI(AUTHORITY,"#", single);
	}
	
	protected static final String PROJECTION[] = {
		EncryptionDBHepler._ID,
		EncryptionDBHepler.CONTACT_ID,
	};
	
	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		encryptionDBHepler = new EncryptionDBHepler(getContext());
		insertHelper = new DatabaseUtils.InsertHelper(encryptionDBHepler.getWritableDatabase(), table);
		return true;
	}
	
	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		int match = matcher.match(uri);
		switch(match){
			case all:
				return "vnd.android.cursor.dir/"+AUTHORITY;
			case single:
				return "vnd.android.cursor.item/"+AUTHORITY;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
	}
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		Cursor c ;
		SQLiteDatabase db = encryptionDBHepler.getReadableDatabase();
		if(projection == null){
			projection = PROJECTION;
		}
		switch(matcher.match(uri)){
			case all:
			case single:
				break;
			default:
				throw new IllegalArgumentException("Unknown URI: " + uri);
		}
		c = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
		return c;
	}
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		long rowCount = DatabaseUtils.queryNumEntries(encryptionDBHepler.getReadableDatabase(), table);
		if(rowCount > encryptionDBHepler.ENCRYPTION_MAX_ROW){
			throw new IndexOutOfBoundsException(); 
		}
		long rowId = insertHelper.insert(values);
		if(rowId > 0){
			notifyChange();
			return ContentUris.withAppendedId(uri, rowId);
		}
		return null;
	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = encryptionDBHepler.getWritableDatabase();
		String where;
		final int matchId = matcher.match(uri);
		switch(matchId) {
			case all :
				where = selection;
				break;
			case single:
				where = EncryptionDBHepler._ID + "=" + uri.getPathSegments().get(1);
				break;
			default :
				throw new UnsupportedOperationException("Cannot update URL: " + uri);
		}
		int count = db.update(table, values, where, selectionArgs);
		if(count > 0){
			notifyChange();
		}
		return count;
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		final SQLiteDatabase db = encryptionDBHepler.getWritableDatabase();
		final int matchId = matcher.match(uri);
		switch(matchId) {
			case all :
			case single:
				int count = db.delete(table, selection, selectionArgs);
				if(count > 0 ){
					notifyChange();
				}
				return count;
			default :
				throw new UnsupportedOperationException("Cannot delete that URL: " + uri);
		}
	}
	
	protected void notifyChange(){
		getContext().getContentResolver().notifyChange(URIS, null,false);
	}

}
