package edu.data.model;

public class Product {
	String id;
	String json;
	
	public Product(String id, String name) {
		this.id = id;
		this.json = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String name) {
		this.json = name;
	}
}
