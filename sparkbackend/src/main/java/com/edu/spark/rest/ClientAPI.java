package com.edu.spark.rest;
import org.restlet.resource.Delete;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.Put;
import org.restlet.resource.ServerResource;

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
		return "Get";
	}
	
	@Put
	public String updateMethod(String jStr) throws Exception {
		return "Put";
	}	
}
