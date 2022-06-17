package com.kumkangkind.kumkangsm2.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public class DbOpenHelper {
	 
    private static final String DATABASE_NAME = "hoosik.db";
    private static final int DATABASE_VERSION = 2;
    public static SQLiteDatabase mDB;
    public DatabaseHelper mDBHelper;
    private Context mCtx;
 
    /**
     * 내부클래스
     * @author hoosik
     *
     */
    public class DatabaseHelper extends SQLiteOpenHelper{
        // 생성자
        public DatabaseHelper(Context context, String name,
                CursorFactory factory, int version) {
            super(context, name, factory, version);
        }
 
        // 최초 DB를 만들때 한번만 호출된다.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Databases.CreateDB._CREATE);
        }
 
        // 버전이 업데이트 되었을 경우 DB를 다시 만들어 준다.
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS "+Databases.CreateDB._TABLENAME);
            onCreate(db);
        }
    }
 
    public DbOpenHelper(Context context){
        this.mCtx = context;
    }
    
    public DbOpenHelper open() throws SQLException{
        mDBHelper = new DatabaseHelper(mCtx, DATABASE_NAME, null, DATABASE_VERSION);
        mDB = mDBHelper.getWritableDatabase();
        return this;
    }
    
    public void close(){
        mDB.close();
    }
	

    /**
     * 
     * @return
     */
	public Cursor getAllColumns(){
		return mDB.query(Databases.CreateDB._TABLENAME, null, null, null, null, null, null);
	}

	// Insert DB
	public long insertColumn(String header, String contents, String time, String certificateNo){
		ContentValues values = new ContentValues();
		values.put(Databases.CreateDB.HEADER, header);
		values.put(Databases.CreateDB.CONTENTS, contents);
        values.put(Databases.CreateDB.CERTIFICATENO, certificateNo);
		values.put(Databases.CreateDB.TIME, time);
		return mDB.insert(Databases.CreateDB._TABLENAME, null, values);
	}
	
	/**
	 * 지운다.
	 * @param header
	 * @param time
	 */
	public void Delete(String header, String time){
		
		String sql = String.format("DELETE FROM mailBox WHERE header = '%s' And time = '%s'" , header, time);
		mDB.execSQL(sql);
	}
	
	

	/**
	 * 테이블의 모든 Data를 지운다.
	 */
	public void deleteAll(){
		mDB.execSQL("delete from "+ Databases.CreateDB._TABLENAME);
	}
}

