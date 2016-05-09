package com.edu.spark.rest;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

import com.edu.util.HelperUtils;
import com.edu.util.SystemConstants;

import edu.data.model.Location;
import spark.TravelRecommendation;

public class ClientAPI extends ServerResource{	
	
	@Post
	public String writeMethod(String jsonStr) {
		JSONObject jObj = HelperUtils.toJSONObj(jsonStr);
		String country = (String) jObj.get(SystemConstants.COUNTRY);
		String state = (String) jObj.get(SystemConstants.STATE);		
		String type = (String) jObj.get(SystemConstants.TYPE);
		String key = (String) jObj.get(SystemConstants.USERID);
		int userId;
		if (key.equalsIgnoreCase("undefined")) {
			userId = 1;
		} else {
			userId = Integer.parseInt(key);
		}
				
		
		List<Location> products = TravelRecommendation.getInstance().getFilteredRecommendation(country, state, type, userId);
		JSONObject responseDetailsJson = new JSONObject();
	    JSONArray jsonArray = new JSONArray();
	    
	    for(Location p : products) {
	    	JSONObject jsonObject = HelperUtils.locationToJSONObj(p);
	    	jsonArray.add(jsonObject);
	    }
	    
	    responseDetailsJson.put("forms", jsonArray);//Here you can see the data in json format
	    String str = responseDetailsJson.toJSONString();
	    System.out.println();
	    return str;	    
	}
		
	@Delete
	public String deleteMethod() {
		return "Delete";
	}	
	
	@Get
	public String readMethod() throws Exception{
		String key = (String) this.getRequestAttributes().get(SystemConstants.KEY);
		int userId = 0;

		if (key.equalsIgnoreCase("undefined")) {
			userId = 1;
		} else {
			userId = Integer.parseInt(key);
		}
		List<Location> products = TravelRecommendation.getInstance().getRecommendationForUser(userId);		
		JSONObject responseDetailsJson = new JSONObject();
	    JSONArray jsonArray = new JSONArray();
	    
	    for(Location p : products) {
	    	JSONObject jsonObject = HelperUtils.locationToJSONObj(p);
	    	jsonArray.add(jsonObject);
	    
	    responseDetailsJson.put("forms", jsonArray);//Here you can see the data in json format
	    }	    

	    String str =  responseDetailsJson.toJSONString();
	    return str;	    
	}
	
	@Put
	public String updateMethod(String jStr) throws Exception {
		return "Put";
	}	
}
