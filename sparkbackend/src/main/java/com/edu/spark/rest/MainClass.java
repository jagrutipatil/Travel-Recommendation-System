package com.edu.spark.rest;

import org.restlet.Component;
import org.restlet.data.Protocol;

import com.edu.util.ConfigurationService;

public class MainClass {

	public static void main(String args[]) {
		try {
			System.setProperty("java.io.tmpdir", "/home/jagruti/softwares/spark/tmp");
			ConfigurationService.getInstance().readProperties(args[0]);
			if (args.length > 1) {
				printUsage();
			}
			
			Component clientComponent = new Component();
			clientComponent.getServers().add(Protocol.HTTP, 8081);
			clientComponent.getDefaultHost().attach("/restlet", new RecommendationRestService());
			clientComponent.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void printUsage() {
		System.out.println("Usage: <configuration-file>");
	}

}
