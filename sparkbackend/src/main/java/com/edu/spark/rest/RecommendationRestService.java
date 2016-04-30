package com.edu.spark.rest;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

public class RecommendationRestService  extends Application{

	@Override
    public synchronized Restlet createInboundRoot() {
		Router clientRouter = new Router(getContext());
		clientRouter.attach("/test", ClientAPI.class);
		clientRouter.attach("/test/{key}", ClientAPI.class);
		return clientRouter;
	}

}
