package spark;

import java.io.Serializable;
import java.util.Map;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;

import scala.Tuple2;

public class DataService implements Serializable{
	private JavaRDD<Tuple2<Integer, Rating>> ratings = null;
	private Map<Integer, String> products = null;
	private JavaSparkContext sc = null;
	private MatrixFactorizationModel bestModel = null;
	
	private static DataService instance = null;
	
	public static DataService getInstance() {
		if (instance == null) {
			instance = new DataService();
		}
		return instance;
	}
	
	private DataService() {		
	}
	
	public JavaRDD<Tuple2<Integer, Rating>> getRatings() {
		return ratings;
	}
	
	public void setRatings(JavaRDD<Tuple2<Integer, Rating>> ratings) {
		this.ratings = ratings;
	}
	
	public Map<Integer, String> getProducts() {
		return products;
	}
	
	public void setProducts(Map<Integer, String> products) {
		this.products = products;
	}
	
	public JavaSparkContext getSc() {
		return sc;
	}
	
	public void setSc(JavaSparkContext sc) {
		this.sc = sc;
	}

	public MatrixFactorizationModel getBestModel() {
		return bestModel;
	}

	public void setBestModel(MatrixFactorizationModel bestModel) {
		this.bestModel = bestModel;
	}
	
}
