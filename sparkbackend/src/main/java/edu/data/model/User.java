package edu.data.model;

import java.io.Serializable;

public class User implements Serializable{
	private Integer userID;
	
	private String firstName;
	
	private String lastName;
	
	public User(Integer userID , String firstName, String lastName) {
		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public Integer getUserID() {
		return userID;
	}	
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
	
	public void setUserID(Integer userID) {
		this.userID = userID;
	}	
	
	public void getFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void getLastName(String lastName) {
		this.lastName = lastName;
	}

}
