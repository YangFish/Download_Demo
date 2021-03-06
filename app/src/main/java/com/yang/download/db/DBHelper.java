package com.yang.download.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by yuy on 2016/4/14.
 */
public class DBHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "download_demo.db";
	private static final int VERSION = 1;
	private static DBHelper dbHelper = null;

	private static final String SQL_CREAT = "create table thread_info(_id integer primary key autoincrement," +
			"thread_id integer,url text,start integer,end integer,finished integer)";

	private static final String SQL_DROP = "drop table if exists thread_info";

	private DBHelper(Context context) {
		super(context, DB_NAME, null, VERSION);
	}

	/**
	 * 获得对象
	 */
	public static DBHelper getInstance(Context context) {
		if (dbHelper == null) {
			dbHelper = new DBHelper(context);
		}

		return dbHelper;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREAT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DROP);
		db.execSQL(SQL_CREAT);
	}
}
