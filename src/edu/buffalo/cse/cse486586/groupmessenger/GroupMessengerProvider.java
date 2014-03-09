package edu.buffalo.cse.cse486586.groupmessenger;



import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;


public class GroupMessengerProvider extends ContentProvider {

	
	
	  Dbhandler mOpenHelper;
      SQLiteDatabase db;
	    

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // You do not need to implement this.
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // You do not need to implement this.
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
         	
    	 db.insert(Dbhandler.getTableName(), null, values);
    	 //Log.v("Insert long value =",Long.toString(id));
    	
    	getContext().getContentResolver().notifyChange(uri, null);

        Log.v("inserted value =", values.toString());
        return uri;
    }

   
    
    @Override
    public boolean onCreate() {
        // If you need to perform any one-time initialization task, please do it here.
    	mOpenHelper = new Dbhandler(getContext());
    	db = mOpenHelper.getWritableDatabase();
        return true;
    }

    
	@Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
       
		
		db = mOpenHelper.getReadableDatabase();
		SQLiteQueryBuilder queryBuilder=new SQLiteQueryBuilder();
		
		queryBuilder.setTables(Dbhandler.getTableName());
				
		Cursor cursor = queryBuilder.query(db, projection,"key="+"'"+selection+"'",selectionArgs, null, null, sortOrder);

		Log.v("query ",selection);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
       
        return 0;
    }
}

