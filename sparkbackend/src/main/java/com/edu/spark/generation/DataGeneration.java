package com.edu.spark.generation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

public class DataGeneration implements Serializable{
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	static final String DB_URL = "jdbc:mysql://localhost:3310/cmpe239";
	static final String USER = "root";
	static final String PASS = "Aparna";
	static final String[] countries = { "Germany", "Greece", "India", "USA"};
	static final String[] types = {"Historical", "Hill Station", "Adventure", "Scenery", "Sports" };
	long count = 1000;
	static String[] states = {};
	
	
	public void addStates() {
		states = new String[5];
		states[0]="";
		states[1]="Baden-Wuerttemberg|Bayern|Berlin|Brandenburg|Bremen|Hamburg|Hessen|Mecklenburg-Vorpommern|Niedersachsen|Nordrhein-Westfalen|Rheinland-Pfalz|Saarland|Sachsen|Sachsen-Anhalt|Schleswig-Holstein|Thueringen";
		states[2]="Aitolia kai Akarnania|Akhaia|Argolis|Arkadhia|Arta|Attiki|Ayion Oros (Mt. Athos)|Dhodhekanisos|Drama|Evritania|Evros|Evvoia|Florina|Fokis|Fthiotis|Grevena|Ilia|Imathia|Ioannina|Irakleion|Kardhitsa|Kastoria|Kavala|Kefallinia|Kerkyra|Khalkidhiki|Khania|Khios|Kikladhes|Kilkis|Korinthia|Kozani|Lakonia|Larisa|Lasithi|Lesvos|Levkas|Magnisia|Messinia|Pella|Pieria|Preveza|Rethimni|Rodhopi|Samos|Serrai|Thesprotia|Thessaloniki|Trikala|Voiotia|Xanthi|Zakinthos";
		states[3]="Andaman and Nicobar Islands|Andhra Pradesh|Arunachal Pradesh|Assam|Bihar|Chandigarh|Chhattisgarh|Dadra and Nagar Haveli|Daman and Diu|Delhi|Goa|Gujarat|Haryana|Himachal Pradesh|Jammu and Kashmir|Jharkhand|Karnataka|Kerala|Lakshadweep|Madhya Pradesh|Maharashtra|Manipur|Meghalaya|Mizoram|Nagaland|Orissa|Pondicherry|Punjab|Rajasthan|Sikkim|Tamil Nadu|Tripura|Uttar Pradesh|Uttaranchal|West Bengal";
		states[4]="Alabama|Alaska|Arizona|Arkansas|California|Colorado|Connecticut|Delaware|District of Columbia|Florida|Georgia|Hawaii|Idaho|Illinois|Indiana|Iowa|Kansas|Kentucky|Louisiana|Maine|Maryland|Massachusetts|Michigan|Minnesota|Mississippi|Missouri|Montana|Nebraska|Nevada|New Hampshire|New Jersey|New Mexico|New York|North Carolina|North Dakota|Ohio|Oklahoma|Oregon|Pennsylvania|Rhode Island|South Carolina|South Dakota|Tennessee|Texas|Utah|Vermont|Virginia|Washington|West Virginia|Wisconsin|Wyoming";
	}
	
	public String[] populateStates(int selectedCountryIndex){
		String text = states[selectedCountryIndex];
		String[] state =  text.split("\\|");
		return state;
	}

//	public void populateCountries(countryElementId, stateElementId){
		// given the id of the <select> tag as function argument, it inserts <option> tags
//		var countryElement = document.getElementById(countryElementId);
//		countryElement.length=0;
//		countryElement.options[0] = new Option('Select Country','-1');
//		countryElement.selectedIndex = 0;

//		for (var i=0; i<country_arr.length; i++) {
//			countryElement.options[countryElement.length] = new Option(country_arr[i],country_arr[i]);
//		}
// 		Assigned all countries. Now assign event listener for the states.
//	}
	
	public void writeToFile(String [] args) throws IOException {

        // The name of the file to open.
		String fileName = "temp.txt";
        FileWriter fileWriter = new FileWriter(fileName);

        try {
            // Assume default encoding.

            // Always wrap FileWriter in BufferedWriter.
            BufferedWriter bufferedWriter =
                new BufferedWriter(fileWriter);

            bufferedWriter.write("Hello there,");
            bufferedWriter.newLine();

            // Always close files.
            bufferedWriter.close();
        }
        catch(IOException ex) {
            System.out.println("Error writing to file '"+ fileName + "'");
        }
    }
	
	public int getRandomIntInclusive(int min, int max) {
		  return (int) (Math.floor(Math.random() * (max - min + 1)) + min);
	}
	
	public void writeTocationSQLToFiles() throws SQLException, IOException {
		int count = 2552;
		NameGenerator gen = new NameGenerator();
		String fileName = "sql2.txt";
        FileWriter fileWriter = new FileWriter(fileName);
        
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);        
        addStates();
        int i = 86;
        
        	String country = countries[i];        			
			for (String state: populateStates(i+1)) {
				for (String type: types) {
					for (int k = 0; k < 5; k++) {
						System.out.println(country + " " + state);
						String name = gen.getName();
						int minPrice = getRandomIntInclusive(500, 9000);
						int minTemp = getRandomIntInclusive(10, 20);
						bufferedWriter.write(generateLocationSQL(count++, name, type + " " + country, type, country, state, name + " , " + state + " , " + country, minTemp, minTemp + getRandomIntInclusive(10, 60), minPrice, minPrice + getRandomIntInclusive(10000, 20000), "INR"));
			            bufferedWriter.newLine();					
					}
				}
//			}
		}
		
        bufferedWriter.close();
	}
	
	public void printCountryStates() {
		addStates();
		for (int i = 0; i < countries.length; i++) {
			for (String state: populateStates(i+1)) {
				System.out.println(i + " " + countries[i] + " " + state);
			}
		}	
	}
	
			
	public void insertLocationData() {
		int count = 31;
		GooglePlaces client = new GooglePlaces("AIzaSyD5AG9O2Nscqkz-LzZhhcrPKHNzXqVRFLw");
		
		addStates();
		try {
			try {
				List<Place> places = client.getPlacesByQuery("places in India", 100);
				for (Place place : places) {						
						generateSQLFromPlace(place, "historical", "India", "Andhra Pradesh", count++);
				}
			} catch (Exception e) {
			}
			} catch (Exception se) {
				se.printStackTrace();
			}
	}		
	
	public void generateSQLFromPlace(Place place, String type, String country, String state, int count) throws SQLException {
			generateLocationSQL(count, place.getName(), type + " " + country, type, country, state, place.getAddress(), getRandomIntInclusive(10, 20), getRandomIntInclusive(30, 40), 3000, 15000, "INR");		
	}
	
	public String generateLocationSQL(int locationID, String name, String description, String type, 
			String country, String state, String address, double minTemp, double maxTemp, double minPrice, double maxPrice, String currency) throws SQLException {
			
		String insertQuery = "INSERT INTO location VALUES ("+ addValue(locationID) + addComma() + addString(name) + addComma() 
			   + addString(description) + addComma() + addString(type) + addComma() 
			   + addString(country) + addComma() + addString(state)  + addComma() 
			   + addString(address) + addComma() + addValue(minTemp) + addComma()
			   + addValue(maxTemp) + addComma() + addValue(minPrice) + addComma() 
			   + addValue(maxPrice) + addComma() 
			   + addString(currency) + ");";
		System.out.println(insertQuery);
		return insertQuery;
	}
	
		
	public String addComma() {
		return " , ";
	}
	
	public String addString(String str) {
		return "'"+ str +"'";
	}
	

	public String addValue(Object integer) {
		return ""+ integer +"";
	}
	
	public void insertUserData() {
//		clearDb("users");
//		clearDb("location");
//		clearDb("ratings");
		Connection conn = null;
		Statement stmt = null;
		
		String[] fNames = {"Jagruti", "Prashant", "Vijay", "Pooja", "Mangesh", "Gauri","Aparna","Tanvi","Ketki"};
		String[] lNames = {"Patil", "Raut", "Sanap", "Pawara", "Taklikar", "Bhamre","Shukla","Kulkarni","Gawande"};
		
		String[] location = {"Himalaya", "Kerla", "Kashmir", "Pune", "Mumbai", "Lakhnau"};
		String[] locType = {"Hill_Station", "Historical", "Shopping", "WeekendGateway", "SpringVacation", "Island"};
		int count =6040;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			stmt = conn.createStatement();
			
//			for (int i = 0; i < count; i++) {
//				generateUserSQL(stmt, i+1, fNames[(int) (i % location.length)], lNames[(int) (i % location.length)]);
//			}
	    	
//	    	generateLocationSQL(stmt, i+1, location[(int) (i % location.length)], locType[(int) (i % locType.length)]);

//		    for (long i = 0; i < count; i++) {
//		    	for (long j = 0; j < count; j++) {
//		    		generateRatingSQL(stmt, i+1, j+1, getRandomIntInclusive(1, 10), System.currentTimeMillis() + (i %10));
//		    	}		    	
//		    }
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
		String username=firstname+"."+lastname+"@gmail.com";
		String password="abc";
		stmnt.addBatch("INSERT INTO users VALUES ( "+ userid + ", '"+ firstname+ "', '"+ lastname + "', '"+ username+"', '"+password+ "'); \n");
	}

	public void generateRatingSQL(Statement stmnt, long userId, long locationId, int rating, long timestamp) throws SQLException {
		stmnt.addBatch("INSERT INTO ratings VALUES (" + userId + ", " + locationId + ", "+ rating + " , "+ timestamp + "); \n");
	}

	public void generateLocationSQL(Statement stmnt, long locationid, String locationName, String locationType) throws SQLException {
		stmnt.addBatch("INSERT INTO location VALUES ("+ locationid + " , '"+ locationName+ "', '"+ locationType + "'); \n");
	}
	
	public static void main(String args[]) {
		DataGeneration db = new  DataGeneration();
		try {
			db.writeTocationSQLToFiles();
//			db.insertUserData();
//			db.printCountryStates();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
//		db.insertData();
//		db.insertLocationData();
//		db.printCountryStates();
	}
}
