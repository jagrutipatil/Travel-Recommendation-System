package edu.data.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Location implements Serializable{
	private String locationId;
	
	private String locationName;
	
	private String locationType;
	
	public Location (String locationName, String locationType) {
		this.locationName = locationName;
		this.locationType = locationType;
	}
	
	public String getLocationId() {
		return locationId;
	}

	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return locationName;
	}

	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}

	public String getLocationType() {
		return locationType;
	}

	public void setLocationType(String locationType) {
		this.locationType = locationType;
	}
		
}
