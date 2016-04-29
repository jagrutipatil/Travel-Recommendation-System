package com.edu.spark.rest;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.Protocol;
import org.restlet.routing.Router;

public class RecommendationRestService extends Application{

	@Override
    public synchronized Restlet createInboundRoot() {
		Router clientRouter = new Router(getContext());
		clientRouter.attach("/test", ClientAPI.class);
		clientRouter.attach("/test/{key}", ClientAPI.class);
		return clientRouter;
	}

	public static void main(String args[]) {                  
        try {
                Component clientComponent = new Component();    
                clientComponent.getServers().add(Protocol.HTTP, 8081);  
                clientComponent.getDefaultHost().attach("/restlet", new RecommendationRestService());
    			clientComponent.start();
		} catch (Exception e) {
			e.printStackTrace();
		}  
	}
}
