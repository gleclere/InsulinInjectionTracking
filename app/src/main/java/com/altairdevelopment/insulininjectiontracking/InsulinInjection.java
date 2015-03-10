package com.altairdevelopment.insulininjectiontracking;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.content.Context;

public class InsulinInjection {
	// Area codes
	final static int RIGHT_ARM = 1;
	final static int LEFT_ARM = 2;
	final static int RIGHT_LEG = 3;
	final static int LEFT_LEG = 4;
	final static int STOMACH = 5;
	final static int RIGHT_BUTTOCK = 6;
	final static int LEFT_BUTTOCK = 7;
	
	// Insulin type codes
	final static int RAPID_ACTING = 0;
	final static int SHORT_ACTING = 1;
	final static int INTERMEDIATE_ACTING = 2;
	final static int LONG_ACTING = 3;
	final static int PRE_MIXED = 4;

	// CSV types
	final static short CSV_HEADER = 0;
	final static short CSV_LINE = 1;
	
	private DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
	
	private long id; // unique id
	private long userId; // user id
	private Calendar date; // date and time
	private int type; // Insulin type
	private float dose; // Dose
	private int area; // Injection area
	
	// Getters and Setters

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}	

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getArea() {
		return area;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public float getDose() {
		return dose;
	}

	public void setDose(float dose) {
		this.dose = dose;
	}

	public Calendar getDate() {
		return date;
	}

	public void setDate(Calendar date) {
		this.date = date;
	}
	
	// Methods

	public InsulinInjection() {
	}
	
	public InsulinInjection(long _id, long _userid, Calendar _date, int _type, String _dose, int _area) {
		setId(_id);
		setUserId(_userid);
		setDate(_date);
		setType(_type);
		setDose(Float.valueOf(_dose));
		setArea(_area);
	}
	
	public static String getTextFromArea(Context context, int area) {
		switch (area) {
			case InsulinInjection.RIGHT_ARM :
				return context.getString(R.string.right_arm_area);
			case InsulinInjection.LEFT_ARM :
				return context.getString(R.string.left_arm_area);
			case InsulinInjection.RIGHT_LEG :
				return context.getString(R.string.right_leg_area);
			case InsulinInjection.LEFT_LEG :
				return context.getString(R.string.left_leg_area);
			case InsulinInjection.STOMACH :
				return context.getString(R.string.stomach_area);
			case InsulinInjection.RIGHT_BUTTOCK :
				return context.getString(R.string.right_buttock_area);
			case InsulinInjection.LEFT_BUTTOCK :
				return context.getString(R.string.left_buttock_area);
		}
		return null;
	}

	/**
	 * CSV format of an injection
	 * 
	 * @param _ctx  : context
	 * @param _sep  : separator
	 * @param _del  : delimier
	 * @param _type : 0-header 1-line
	 * @return
	 */
	@SuppressLint("SimpleDateFormat")
	public String getCSV(Context _ctx, String _dateFormat, String _sep, String _del, short _type) {
		if (_dateFormat==null) {
			_dateFormat="yyyy/MM/dd";
		}
		
		if (_sep==null) {
			_sep=",";
		}

		if (_del==null) {
			_del="";
		}

		switch (_type) {
			case CSV_HEADER :
				return  _del+_ctx.getString(R.string.db_userId)+_del+_sep+ //User Id
						_del+_ctx.getString(R.string.db_date)+_del+_sep+ // Date
						_del+_ctx.getString(R.string.db_time)+_del+_sep+ // Time
						_del+_ctx.getString(R.string.db_type)+_del+_sep+ // Insulin type
						_del+_ctx.getString(R.string.db_dose)+_del+_sep+ // Dose
						_del+_ctx.getString(R.string.db_area)+_del+"\r\n"; // Area
			case CSV_LINE :
				SimpleDateFormat sdf = new SimpleDateFormat(_dateFormat);

				return  _del+String.valueOf(getUserId())+_del+_sep+ //User Id
						_del+sdf.format(getDate().getTime())+_del+_sep+ // Date
						_del+timeFormat.format(getDate().getTime())+_del+_sep+ // Time
						_del+_ctx.getResources().getStringArray(R.array.types)[getType()]+_del+_sep+ // Insulin type
						_del+String.valueOf(getDose())+_del+_sep+ // Dose
						_del+InsulinInjection.getTextFromArea(_ctx, getArea())+_del+"\r\n"; // Area
			default :
				return null;
			}
	}
}
