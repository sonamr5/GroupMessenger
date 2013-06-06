package edu.buffalo.cse.cse486586.groupmessenger;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class MessageStore extends ContentProvider{
	private MessageDB Msgdb;
	static final int message_ID = 2;
	static final String PROVIDER_NAME ="edu.buffalo.cse.cse486586.groupmessenger.provider";
	public static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME);
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub
		return 0;
	}
    private static final UriMatcher matchURI;
    static{
        matchURI = new UriMatcher(UriMatcher.NO_MATCH);
    }

	@Override
	public String getType(Uri arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		// TODO Auto-generated method stub
		 SQLiteDatabase DB = Msgdb.getWritableDatabase();
		 long id=0;
		 id=DB.insertWithOnConflict(MessageDB.TABLE_MESSAGEDB, "", arg1, SQLiteDatabase.CONFLICT_REPLACE);
		 return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		Msgdb=new MessageDB(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionargs,
			String sortorder) {
		// TODO Auto-generated method stub
		SQLiteDatabase DB = Msgdb.getWritableDatabase();
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        sqlBuilder.setTables(MessageDB.TABLE_MESSAGEDB);
        if (matchURI.match(uri) == message_ID)
            //---if getting a particular book---
            sqlBuilder.appendWhere(
                    MessageDB.key + " = " + uri.getPathSegments().get(1));
       
        selection = "KEY = '"+selection+"'";
        Cursor c = sqlBuilder.query(
                DB,
                projection,
                selection,
                selectionargs,
                null,
                null,
                sortorder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
	}

	@Override
	public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
		// TODO Auto-generated method stub
		return 0;
	}

}
