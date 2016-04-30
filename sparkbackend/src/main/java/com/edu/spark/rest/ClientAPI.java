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

import edu.data.model.Location;
import spark.TravelRecommendation;

public class ClientAPI extends ServerResource{	
	
	@Post
	public String writeMethod(String jStr) {
		return "Post";
	}
		
	@Delete
	public String deleteMethod() {
		return "Delete";
	}	
	
	@Get
	public String readMethod() throws Exception{
		List<Location> products = TravelRecommendation.getInstance().getRecommendationForUser(2);
		JSONObject responseDetailsJson = new JSONObject();
	    JSONArray jsonArray = new JSONArray();
	    
	    for(Location p : products) {
	       jsonArray.add(HelperUtils.locationTOJSON(p));
	    }
	    responseDetailsJson.put("forms", jsonArray);//Here you can see the data in json format
	    return responseDetailsJson.toJSONString();	    
	}
	
	@Put
	public String updateMethod(String jStr) throws Exception {
		return "Put";
	}	
}
