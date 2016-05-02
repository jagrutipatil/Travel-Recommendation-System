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
		int userId = Integer.parseInt(key);		
		
		List<Location> products = TravelRecommendation.getInstance().getFilteredRecommendation(country, state, type, userId);
		JSONObject responseDetailsJson = new JSONObject();
	    JSONArray jsonArray = new JSONArray();
	    
	    for(Location p : products) {
	       jsonArray.add(p);
	    }
	    
	    responseDetailsJson.put("forms", jsonArray);//Here you can see the data in json format
	    return responseDetailsJson.toJSONString();	    
	}
		
	@Delete
	public String deleteMethod() {
		return "Delete";
	}	
	
	@Get
	public String readMethod() throws Exception{
		String requestedKey = (String) this.getRequestAttributes().get(SystemConstants.KEY);		
		int userId = Integer.parseInt(requestedKey);
		List<Location> products = TravelRecommendation.getInstance().getRecommendationForUser(userId);
		JSONObject responseDetailsJson = new JSONObject();
	    JSONArray jsonArray = new JSONArray();
	    
	    for(Location p : products) {
	    	JSONObject jsonObject = new JSONObject();
	    	jsonArray.add(jsonObject);
	    }	    
	    
	    responseDetailsJson.put("forms", jsonArray);//Here you can see the data in json format

	    return responseDetailsJson.toJSONString();	    
	}
	
	@Put
	public String updateMethod(String jStr) throws Exception {
		return "Put";
	}	
}
