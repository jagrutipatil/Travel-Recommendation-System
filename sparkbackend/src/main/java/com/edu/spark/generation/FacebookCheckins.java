package com.edu.spark.generation;


import java.io.FileReader;
import java.sql.*;
import java.util.*;

 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FacebookCheckins {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3310/cmpe239";
	static final String USER = "root";
	static final String PASS = "Aparna";
	
	 public static void main(String[] args) {
		 
		 
		 Connection conn = null;
		   Statement stmt = null;
	        JSONParser parser = new JSONParser();
	 
	        try {
	 
	        	Class.forName("com.mysql.jdbc.Driver");
	        	conn = DriverManager.getConnection(DB_URL, USER, PASS);
	        	stmt = conn.createStatement();
	        	
	            Object obj = parser.parse(new FileReader(
	                    "C:/Users/Saurabh/Vacation-Recommendation-System/sparkbackend/src/main/java/com/edu/spark/generation/FBCheckinData.txt"));
	 
	           JSONObject jsonObject = (JSONObject) obj; 
	           JSONArray users = (JSONArray) jsonObject.get("users");
	           
	           for(int i=0;i<users.size();i++){
	        	   
	        	   
	        	   JSONObject id = (JSONObject)users.get(i);
	        	   JSONArray data;
	        	   System.out.println("This is the user id : "+id.get("uid"));
	        	   String userid=(String)id.get("uid");
	        	   data = (JSONArray)id.get("data"); 
	        	   List<String> uniqueCheckInEntries = new ArrayList<String>();
	        	   
	        	   for(int j=0;j<data.size();j++){
	        			  
	        		      int k=j+1;
	        		
	        			  
	        			  System.out.println("Checkin "+k+" : ");
	        			  JSONObject innerid = (JSONObject)data.get(j); 
	        			  JSONObject place = (JSONObject)innerid.get("place");
	        			  
	        			  System.out.println("Location id is : "+ place.get("id"));
	        			  String locid =(String)place.get("id");
	        			  
	        			  String uniqueCheckIn = userid+"-"+locid;
	        			  
	        			 // if(uniqueCheckInEntries.contains(uniqueCheckIn)){
	        			  
	        			  System.out.println("Location name is : "+place.get("name"));
	        			  String locname = (String)place.get("name");
	        			  JSONObject location = (JSONObject)place.get("location");
	        			  System.out.println("City is : "+location.get("city"));
	        			  String loccity = (String)location.get("city");
	        			  System.out.println("State is : "+location.get("state"));
	        			  String locstate = (String)location.get("state");
	        			  System.out.println("Country is : "+location.get("country"));
	        			  String loccountry = (String)location.get("country");
	        			  System.out.println("Type of Location is : "+location.get("type"));
	        			  String loctype =(String)location.get("type");
	        			  
	        		System.out.println("**********************************************");	
	        		 String query = "INSERT INTO checkin_history VALUES ( "+userid+ ", '"+locid+ "', '"+ locname + "','"+loctype+"','"+loccountry+"','"+locstate+"','"+loccity+"')";
	        		 stmt.executeUpdate(query);
	        	   System.out.println("Inserted into the db !!");
	        			  //}
	        		   
	        	   
	        	   }  
	        	  
	        	  
	        	   
	        	   
	        	   
	           }
	            
	    } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	
	
	
	
	
	
	
	
	
	
	
}
