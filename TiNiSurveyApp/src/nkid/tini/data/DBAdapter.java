package nkid.tini.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

import android.R.bool;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

public class DBAdapter {
	public static final String KEY_ID = "Id";
	public static final String KEY_NAME = "Name";
	public static final String KEY_DESCRIPTION = "Description";
	public static final String KEY_VOTE = "Vote";
	public static final String KEY_DATETIME = "Datetime";
	
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDB;
	
	private static final String DATABASE_CREATE = "create table Poll("
			+ KEY_ID + " integer primary key autoincrement, "
			+ KEY_NAME + " text not null, " + KEY_DESCRIPTION + " text,"
			+ KEY_VOTE + " integer not null, " + KEY_DATETIME + " datetime default (datetime('now','localtime')));";
	
	private static final String DATABASE_NAME = "tiNiSurveyDB";
	private static final String DATABASE_TABLE = "Poll";
	private static final int DATABASE_VERSION = 3;
	
	private final Context mContext;
	
	public DBAdapter(Context ctx) {
		this.mContext = ctx;
	}
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS Match");
			onCreate(db);
		}

	}
	
	public void export() {
		File sd = Environment.getExternalStorageDirectory();
		File data = Environment.getDataDirectory();
		
		FileChannel source = null;
		FileChannel destination = null;
		
		File pkgFolder = new File(sd + "/Android/data/nkid.tini.data");
		if (!pkgFolder.exists()) {
			pkgFolder.mkdir();
		}
		
		String currentDBPath = "/data/" + "nkid.tini.tinisurveyapp" + "/databases/" + DATABASE_NAME;
		String backupDBPath = "/Android/data/nkid.tini.data/" + DATABASE_NAME;
		
		
		boolean success = true;
		
		if (success) {
			File currentDB = new File(data, currentDBPath);
			File backupDB = new File(sd, backupDBPath);
			try {
				source = new FileInputStream(currentDB).getChannel();
				destination = new FileOutputStream(backupDB).getChannel();
				destination.transferFrom(source, 0, source.size());
				source.close();
				destination.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
	
	public DBAdapter open() {
		mDbHelper = new DatabaseHelper(mContext, DATABASE_NAME, null,
				DATABASE_VERSION);
		mDB = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		mDbHelper.close();
	}
	
	public void addPoll(Poll poll) {
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NAME, poll.getName());
		initialValues.put(KEY_DESCRIPTION, poll.getDescription());
		initialValues.put(KEY_VOTE, poll.getVote());
		
		mDB.insertOrThrow(DATABASE_TABLE, null, initialValues);
	}
	
	public void deleteAll() {
		mDB.delete(DATABASE_TABLE, null, null);
	}
	
	public Cursor getData() {
		return mDB.query(DATABASE_TABLE, new String [] {KEY_ID, KEY_NAME, KEY_DESCRIPTION, KEY_VOTE, KEY_DATETIME }, 
				"Id" + "=" + KEY_ID, null, null, null, null);
	}
	
	public int countRows() {
		Cursor c;

		c = mDB.rawQuery("SELECT * FROM Poll", null);
		return c.getCount();
	}
	
	public int getOverallResults(int vote) {
		Cursor mCursor = mDB.query(DATABASE_TABLE, new String [] { KEY_VOTE }, 
				KEY_VOTE + " = " + vote , null, null, null, null);
		
		return mCursor.getCount();
	}
	
	public int getTodayResult(String date, int vote) {
		Cursor mCursor = mDB.query(DATABASE_TABLE, new String [] { KEY_VOTE }, 
				KEY_VOTE + " = " + vote + " AND date(" + KEY_DATETIME 
				+ ") = '" + date + "'", 
				null, null, null, null);
		int result = mCursor.getCount();
		mCursor.close();
		return result;
	}
	
	public int getResultByMonth(int month, int year, int vote) {
		String strMonth = month < 10 ? ("0" + month) : (month + "");
		Cursor mCursor = mDB.query(DATABASE_TABLE, new String [] { KEY_VOTE }, 
				KEY_VOTE + " = " + vote + " AND strftime('%m', Datetime) = '" + strMonth + "'"
				+ " AND strftime('%Y', Datetime) = '" + year + "'",
				null, null, null, null);
		
		int result = mCursor.getCount();
		mCursor.close();
		return result;
	}
	
	public int getResultByWeek(int vote) {
		Cursor mCursor = mDB.query(DATABASE_TABLE, new String [] { KEY_VOTE }, 
				KEY_VOTE + " = " + vote + " AND (date(" + KEY_DATETIME + ") >= date('now', 'localtime', '-7 day') "
						+ "AND date(" + KEY_DATETIME + ") < date('now', 'localtime'))",
				null, null, null, null);
		
		int result = mCursor.getCount();
		mCursor.close();
		return result;
	}
}
