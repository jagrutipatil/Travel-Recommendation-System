package edu.data.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Location implements Serializable{
	private int locationId;
	
	private String name;
	private String type;
	private String desc;
	private String country;
	private String state;
	private String address;
	
	private double minTemp;
	private double maxTemp;

	private double minPrice;
	private double maxPrice;

	private String currency;
	
	public Location (Integer locationId, String name, String desc, String type, String country, String state, String address, 
			double minTemp, double maxTemp, double minPrice, double maxPrice, String currency) {
		this.locationId = locationId;
		this.name = name;
		this.type = type;
		this.desc = desc;
		this.country = country;
		this.state = state;
		this.address = address;
		this.minTemp = minTemp;
		this.maxTemp = maxTemp;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
		this.currency = currency;
	}
	

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getLocationName() {
		return name;
	}

	public void setLocationName(String locationName) {
		this.name = locationName;
	}

	public String getLocationType() {
		return type;
	}

	public void setLocationType(String locationType) {
		this.type = locationType;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getDesc() {
		return desc;
	}


	public void setDesc(String desc) {
		this.desc = desc;
	}


	public String getCountry() {
		return country;
	}


	public void setCountry(String country) {
		this.country = country;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public double getMinTemp() {
		return minTemp;
	}


	public void setMinTemp(double minTemp) {
		this.minTemp = minTemp;
	}


	public double getMaxTemp() {
		return maxTemp;
	}


	public void setMaxTemp(double maxTemp) {
		this.maxTemp = maxTemp;
	}


	public double getMinPrice() {
		return minPrice;
	}


	public void setMinPrice(double minPrice) {
		this.minPrice = minPrice;
	}


	public double getMaxPrice() {
		return maxPrice;
	}


	public void setMaxPrice(double maxPrice) {
		this.maxPrice = maxPrice;
	}


	public String getCurrency() {
		return currency;
	}


	public void setCurrency(String currency) {
		this.currency = currency;
	}
		
}
