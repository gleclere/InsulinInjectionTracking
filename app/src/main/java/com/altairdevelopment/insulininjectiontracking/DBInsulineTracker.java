package com.altairdevelopment.insulininjectiontracking;

import java.util.Calendar;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBInsulineTracker  extends SQLiteOpenHelper {

	final static String dbname=new String("dbinsulintracker");
	final static int version=1;
	
	private SQLiteDatabase db;
	private Cursor cursor;
	
	public DBInsulineTracker(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase _db) {
		try {
			_db.execSQL("CREATE TABLE users (userId INTEGER PRIMARY KEY AUTOINCREMENT," +
						"userFirstName TEXT, userLastName TEXT, birthdate NUMERIC, discoveringDate NUMERIC,"+
					    "diabetesType INTEGER, email TEXT);");


			// TODO POUR TEST
			ContentValues values = new ContentValues();
			values.put("userFirstName", "Chani");
			values.put("userLastName", "GALVAN");
			_db.insert("users", null, values);

			_db.execSQL("CREATE TABLE injections (id INTEGER PRIMARY KEY AUTOINCREMENT," +
					"userId INTEGER, date NUMERIC, type INTEGER, dose FLOAT, area INTEGER);");
}
		catch(SQLException e){
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase _db, int old_version, int new_version) {

	}

	public void openDatabase() {		
		db = this.getWritableDatabase();
	}

	// USERS TABLE
	public long insertUser(User user) {
		ContentValues values = new ContentValues();
		values.put("userFirstName", user.getUserFirstName());
		values.put("userLastName", user.getUserLastName());
		values.put("birthdate", user.getBirthdate().getTimeInMillis());
		values.put("discoveringDate", user.getDiscoveringDate().getTimeInMillis());
		values.put("diabetesType", user.getDiabetesType());
		values.put("email", user.getEmail());
		long rowId = db.insert("users", null, values);
		if (rowId == -1) {
			return -1;
		} else {
			user.setUserId(rowId);
			return rowId;
		}
	}
	
	public boolean selectUsers() {
		cursor = db.query("users", new String[] {"userId", "userFirstName", "userLastName", "birthdate", "discoveringDate", "diabetesType", "email"}, 
				null, null, null, null, "userFirstName DESC", null);
		
		if (cursor==null) { return false; }
		return true;
	}
	
	public User getNextUser() {
		if (!cursor.moveToNext()) {
			return null;
		}
			
		Calendar birthdate = Calendar.getInstance();
		birthdate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("birthdate")));

		Calendar discoveringDate = Calendar.getInstance();
		discoveringDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("discoveringDate")));

		
		User user = new User(
				cursor.getLong(cursor.getColumnIndex("userId")),
				cursor.getString(cursor.getColumnIndex("userFirstName")),
				cursor.getString(cursor.getColumnIndex("userLastName")),
				birthdate,
				discoveringDate,
				cursor.getInt(cursor.getColumnIndex("diabetesType")),
				cursor.getString(cursor.getColumnIndex("email")));
		
		return user;
	}
	
	// INJECTIONS TABLE
	
	public long insertInjection(InsulinInjection injection) {
		ContentValues values = new ContentValues();
		values.put("userId", injection.getId());
		values.put("date", injection.getDate().getTimeInMillis());
		values.put("type", injection.getType());
		values.put("dose", injection.getDose());
		values.put("area", injection.getArea());
		long rowId = db.insert("injections", null, values);
		if (rowId == -1) {
			return -1;
		} else {
			injection.setId(rowId);
			return rowId;
		}
	}
	
	public int deleteInjection(int _id) {
		return db.delete("injections", "id=?", new String[] {String.valueOf(_id)});
	}
	
	public boolean selectInjections(long _userId, int _limit) {
		cursor = db.query("injections", new String[] {"id", "userId", "date", "type", "dose", "area"}, 
				"userId=?", new String[] {String.valueOf(_userId)}, null, null, "date DESC", String.valueOf(_limit));
		
		if (cursor==null) { return false; }
		return true;
	}

	public boolean selectInjectionsByDate(int _userId, long _startDate, long _endDate) {
		cursor = db.query("injections", new String[] {"id", "userId", "date", "type", "dose", "area"}, 
				"userId=? and date>=? and date <=?", new String[] {String.valueOf(_userId), String.valueOf(_startDate), String.valueOf(_endDate)}, 
				null, null, "date ASC", null);
		
		if (cursor==null) { return false; }
		return true;
	}

	public InsulinInjection getNextInjection() {
		if (!cursor.moveToNext()) {
			return null;
		}
		
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("date")));
		
		InsulinInjection ii = new InsulinInjection(
				cursor.getInt(cursor.getColumnIndex("id")),
				cursor.getInt(cursor.getColumnIndex("userId")),
				cal,
				cursor.getInt(cursor.getColumnIndex("type")),
				String.valueOf(cursor.getFloat(cursor.getColumnIndex("dose"))),
				cursor.getInt(cursor.getColumnIndex("area")));
		
		return ii;
	}
	
	public void closeCursor() {
		cursor.close();
	}

}
