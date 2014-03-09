package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.File;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

public class Dbhandler extends SQLiteOpenHelper {
	
	private static final String DBNAME = "mydb";
	private static final String TABLE_NAME="main";
	
	//private static final String SQL_CREATE_MAIN = "CREATE TABLE "+TABLE_NAME+" ( " + "key TEXT PRIMARY KEY,"+" value TEXT"+")";
    private static final String SQL_CREATE_MAIN = "CREATE TABLE " +TABLE_NAME+ " (" + "key" + " TEXT PRIMARY KEY, " + "value" + " TEXT);";	
    //CREATE TABLE main ( key TEXT PRIMARY KEY, value TEXT);
    Dbhandler(Context context) {
        super(context, DBNAME, null, 1);
        context.deleteDatabase(DBNAME);
    }


	


	@Override
	public void onCreate(SQLiteDatabase db) {
		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		 db.execSQL(SQL_CREATE_MAIN);
		Log.v("IN onCreate of Dbhandler"," TAble Created ");
		File abc=Environment.getDataDirectory();
		Log.v(abc.toString(),"Database created here");
		Log.v("sql query ->",SQL_CREATE_MAIN);	
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
		
	}


	public static String getTableName() {              // getter method
		
		return TABLE_NAME;
	}
}
