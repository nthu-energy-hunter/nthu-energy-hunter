package nthu.cs.EnHunter.Logger.DbAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

import nthu.cs.EnHunter.Logger.Uploder.Global;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DbAdapter {
	
	private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	
	private static final String TAG = "EnHunter - DbAdpater";
	
	private static final String DB_NAME = "EnHunter";
	private static final String DB_TABLE = "log";
	private static final int DB_VERSION = 1;
	
	public static final String KEY_DATA = "data";
	public static final String KEY_CREATED_AT = "created_at";
	public static final String KEY_ROW_ID = "_id";
	
	private final Context mContext;
	
	private static final String DB_CREATE = "create table " + DB_TABLE + 
					" (_id integer primary key autoincrement, " +
					KEY_DATA + " string not null," + 
					KEY_CREATED_AT + " string not null);";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DB_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
		
		}
		
	}
	/**
	 * Take the context to allow the database to be 
	 */
	public DbAdapter(Context context) {
		mContext = context;
	}

	public DbAdapter open() throws SQLException {
		this.mDbHelper = new DatabaseHelper(mContext);
		this.mDb = this.mDbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		this.mDb.close();
	}
	
	/**
	 * Delete events before the provided data
	 * 
	 * @param data data before which events should be deleted 
	 * */
	public void cleanupEvents(Date date) {
		SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		String strDate = dataFormat.format(date);
		
		this.mDb.delete(DB_TABLE, KEY_CREATED_AT + " < \"" + strDate +"\"", null);
	}
	
	/**
	 * Create a new set of events
	 * 
	 * @param data the base64 encoded data of the events
	 * @return rowId or -1 if failed
	 * */
	public long createEvents(String data){
		SimpleDateFormat dataFormat = new SimpleDateFormat(DATE_TIME_FORMAT);
		Date date = new Date();
		
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_DATA, data);
		initialValues.put(KEY_CREATED_AT, dataFormat.format(date));
		
		
		return this.mDb.insert(DB_TABLE, null, initialValues);
		
	}
	
	/**
	 * Delete the set of events with given rowId
	 * 
	 * @param rowId id of the set of events to delete
	 * @return true if deleted, false otherwise
	 * */
	public boolean deleteEvents(long rowId) {
		return this.mDb.delete(DB_TABLE, KEY_ROW_ID + "=" + rowId, null) > 0;
	}
	
	
	/**
	 * Delete all event
	 * 
	 * @return true if deleted, false otherwise
	 * */
	public boolean deleteEvents() {
		return  this.mDb.delete(DB_TABLE, null, null) > 0;
	}
	
	/**
	 * Update the set of events using the details provided. The events to be updated is
	 * specified using the rowId, and it is altered to use the data values passed in
	 * @param rowId id of set of events to update
	 * @param data the base64 encoded data of the events
	 * @return true if the set of events was successfully updated, false otherwise
	 * */
	//updateEvents
	
	/**
	 * Return a Cursor positioned at the set of events that matches the given rowId
	 * 
	 * @param rowId id  of set of events to retrieve
	 * @return String the base64 encoded data of the events, null of the rowId doesn't exist
	 * */
	public String fetchEventsData(long rowId) throws SQLException {
		Cursor mCursor = this.mDb.query(true, this.DB_TABLE,new String[] {this.KEY_DATA}, this.KEY_ROW_ID+"="+rowId, 
				null, null, null,null,null);
		
		if(mCursor != null) {
			mCursor.moveToFirst();
			int dataColumnIndex = mCursor.getColumnIndex(KEY_DATA);
			String data = mCursor.getString(dataColumnIndex);
			mCursor.close();
			return data;
		}
		return null;
	}
	
	/**
	 * Return a Cursor for all the events in the database.
	 * Cursors will be returned in descending id order
	 * 
	 * @return Cursor positioned at the beginning of all set of events
	 * */
	// fetchEvents()
}
