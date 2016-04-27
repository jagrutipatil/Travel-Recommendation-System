package edu.data.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LocRating implements Serializable{
	private String userId;
	private String locationId;
	private double rating;
	
	public LocRating(String userId, String locationId, double rating) {
		this.userId = userId;
		this.locationId = locationId;
		this.rating = rating;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}
}
