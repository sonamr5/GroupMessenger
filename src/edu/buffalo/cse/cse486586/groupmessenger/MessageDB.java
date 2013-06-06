package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class MessageDB extends SQLiteOpenHelper{
	public static final String TABLE_MESSAGEDB = "Messages";
	public static final String key = "key";
	public static final String value = "value";
	public static final String DATABASE_NAME = "Messages.db";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_MESSAGEDB = "create table " + TABLE_MESSAGEDB
			+ " (" + key + " text not null, " + value
			+ " text not null);";
	public MessageDB(Context context) {
		super(context,DATABASE_NAME , null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_MESSAGEDB);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}