package edu.data.model;

import java.io.Serializable;

public class User implements Serializable{
	private String userID;
	
	private String firstName;
	
	private String lastName;
	
	public User( String firstName, String lastName) {
//		this.userID = userID;
		this.firstName = firstName;
		this.lastName = lastName;
	}
	
	public String getUserID() {
		return userID;
	}
	
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}
}
