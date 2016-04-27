package com.edu.spark.generation;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.edu.util.HelperUtils;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

public class DataGeneration {
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost/cmpe239";
	static final String USER = "root";
	static final String PASS = "linux2015";
	long count = 1000;
	
	public int getRandomIntInclusive(int min, int max) {
		  return (int) (Math.floor(Math.random() * (max - min + 1)) + min);
	}
	
	public void insertLocationData() {
		Connection conn = null;
		Statement stmt = null;

//		try {
//			Class.forName("com.mysql.jdbc.Driver");
//			conn = DriverManager.getConnection(DB_URL,USER,PASS);
//			stmt = conn.createStatement();
//		} catch(SQLException se){
//		      se.printStackTrace();
//		   }catch(Exception e){
//		      e.printStackTrace();
//		   }finally{
//		      try{
//		         if(stmt!=null)
//		            conn.close();
//		      }catch(SQLException se){
//		      }
//		      try{
//		         if(conn!=null)
//		            conn.close();
//		      }catch(SQLException se){
//		         se.printStackTrace();
//		      }
//		   }
		
		// locaion_Id, location name, description, type, country, state, city, address, min temp, max temp, price
		GooglePlaces client = new GooglePlaces("AIzaSyD5AG9O2Nscqkz-LzZhhcrPKHNzXqVRFLw");

		String[] countries = Locale.getISOCountries();
		for (String country : countries) {
			Locale locale = new Locale("en", country);
			System.out.println("Name: " + locale.getISO3Country());
//			String json = HelperUtils.httpGET("http://services.groupkt.com/state/get/"+ locale.getISO3Country() + "/all");
//			JSONObject jObj = HelperUtils.toJSONObj(json);
//			JSONObject restResponse = (JSONObject)jObj.get("RestResponse");
//			JSONArray arr = (JSONArray)restResponse.get("result");
//			for (int i = 0; i < arr.size(); i++) {
//				JSONObject obj = (JSONObject)arr.get(i);
//				System.out.println(obj.get("name"));
//			}
		}

		
		System.out.println(" ");
		
		
//		List<Place> places = client.getPlacesByQuery("historical places in"
//				+ " India", 10);
//		generatePlacesSQL(places, 1, stmt);
		
		
		
		
//		for (int i = 1; i < 3952; i++) {
//			
//		}
	}
	
	public void generatePlacesSQL(List<Place> places, int count, Statement stmt) {
		for (Place place : places) {
			System.out.println("Name: " + place.getName() + "\tAddress:" + place.getAddress());
			generateLocationSQL(stmt, count++, place.getName(), place.getVicinity() , "Historical", place.getAddress(), 20, 40, 20000, 30000, "INR");
		}
	}
	
	public void generateLocationSQL(Statement stmnt, int locationID, String name, String description, String type, String address, double minTemp, double maxTemp, double minPrice, double maxPrice, String currency) {
//		stmnt.addBatch("INSERT INTO users VALUES ( "+ locationID + ", '" + name + "', '"+ description + "', '"+ type + "' , '"+ address + "', "+ minTemp+" , "+ maxTemp+" , "+ minPrice+" , "+ maxPrice+", '"+ currency+"' ); \n");
	}
	
	public void insertData() {
//		clearDb("users");
//		clearDb("location");
		clearDb("ratings");
		Connection conn = null;
		Statement stmt = null;
		
		String[] fNames = {"Jagruti", "Prashant", "Vijay", "Pooja", "Mangesh", "Gauri"};
		String[] lNames = {"Patil", "Raut", "Sanap", "Pawara", "Taklikar", "Bhamre"};
		
		String[] location = {"Himalaya", "Kerla", "Kashmir", "Pune", "Mumbai", "Lakhnau"};
		String[] locType = {"Hill_Station", "Historical", "Shopping", "WeekendGateway", "SpringVacation", "Island"};
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();

//	    	generateUserSQL(stmt, i+1, fNames[(int) (i % location.length)], lNames[(int) (i % location.length)]);
//	    	generateLocationSQL(stmt, i+1, location[(int) (i % location.length)], locType[(int) (i % locType.length)]);

		    for (long i = 0; i < count; i++) {
		    	for (long j = 0; j < count; j++) {
		    		generateRatingSQL(stmt, i+1, j+1, getRandomIntInclusive(1, 10), System.currentTimeMillis() + (i %10));
		    	}		    	
		    }
	      stmt.executeBatch();	 	      
		} catch(SQLException se){
		      se.printStackTrace();
		   }catch(Exception e){
		      e.printStackTrace();
		   }finally{
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		   }
	}
	
	
	
	public void clearDb(String tableName) {
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();			
			stmt.executeUpdate("Delete from "+ tableName + ";");	 
	      
		}catch(SQLException se){
		      se.printStackTrace();
		   }catch(Exception e){
		      e.printStackTrace();
		   }finally{
		      try{
		         if(stmt!=null)
		            conn.close();
		      }catch(SQLException se){
		      }
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }
		   }		
	}
	
	public void generateUserSQL(Statement stmnt, long userid, String firstname, String lastname) throws SQLException {
		stmnt.addBatch("INSERT INTO users VALUES ( "+ userid + ", '"+ firstname+ "', '"+ lastname + "'); \n");
	}

	public void generateRatingSQL(Statement stmnt, long userId, long locationId, int rating, long timestamp) throws SQLException {
		stmnt.addBatch("INSERT INTO ratings VALUES (" + userId + ", " + locationId + ", "+ rating + " , "+ timestamp + "); \n");
	}

	public void generateLocationSQL(Statement stmnt, long locationid, String locationName, String locationType) throws SQLException {
		stmnt.addBatch("INSERT INTO location VALUES ("+ locationid + " , '"+ locationName+ "', '"+ locationType + "'); \n");
	}
	
	public static void main(String args[]) {
		DataGeneration db = new  DataGeneration();
//		db.insertData();
		db.insertLocationData();
	}
}
