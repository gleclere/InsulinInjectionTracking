package com.altairdevelopment.insulininjectiontracking;

import java.util.Calendar;

// TODO Adding address (object?), 
//      doctors (object with phone number, adress (object?), email, 

public class User {
	private long userId;
	private String userFirstName;
	private String userLastName;
	private Calendar birthdate;
	private Calendar discoveringDate;
	private Integer diabetesType;
	private String email;
	
	public long getUserId() {
		return userId;
	}
	
	public void setUserId(long userId) {
		this.userId = userId;
	}
	
	public String getUserFirstName() {
		return userFirstName;
	}
	
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	
	public String getUserLastName() {
		return userLastName;
	}
	
	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
	}
	public Calendar getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(Calendar birthdate) {
		this.birthdate = birthdate;
	}

	public Calendar getDiscoveringDate() {
		return discoveringDate;
	}

	public void setDiscoveringDate(Calendar discoveringDate) {
		this.discoveringDate = discoveringDate;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getDiabetesType() {
		return diabetesType;
	}

	public void setDiabetesType(Integer diabetesType) {
		this.diabetesType = diabetesType;
	}
	
	public User() {

	}

	public User(long _userId, String _userFirstName, String _userLastName, Calendar _birthdate, Calendar _discoveringDate, 
				Integer _diabetesType, String _email) {
		setUserId(_userId);
		setUserFirstName(_userFirstName);
		setUserLastName(_userLastName);
		setBirthdate(_birthdate);
		setDiscoveringDate(_discoveringDate);
		setDiabetesType(_diabetesType);
		setEmail(_email);
	}
}
