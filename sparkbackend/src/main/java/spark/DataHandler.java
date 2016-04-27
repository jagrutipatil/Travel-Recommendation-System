package spark;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.mllib.recommendation.Rating;

import scala.Tuple2;

public class DataHandler implements Serializable{
	private Properties properties = new Properties();
	private String dataSetPath = "";
	
	public void loadData() {
		SparkConf conf = new SparkConf().setAppName("MovieRecommendation").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);

		//Reading Data
		final JavaRDD<String> ratingData = sc.textFile(dataSetPath);
		
		JavaRDD<Tuple2<Integer, Rating>> ratings = ratingData.map(new Function<String, Tuple2<Integer, Rating>>(){
            public Tuple2<Integer, Rating> call(String s) throws Exception {
                String[] row = s.split("::");
                Integer cacheStamp = Integer.parseInt(row[3]) % 10;
                Rating rating = new Rating(Integer.parseInt(row[0]), Integer.parseInt(row[1]), Double.parseDouble(row[2]));
                return new Tuple2<Integer, Rating>(cacheStamp, rating);
            }
		});
		
//		long userCount = ratings.map(
//		        new Function<Tuple2<Integer, LocRating>, Object>() {
//		            public Object call(Tuple2<Integer, LocRating> tuple) throws Exception {
//		                return tuple._2().user();
//		            }
//		        }
//		).distinct().count();
		
		
		JavaRDD<Tuple2<Integer, Rating>> training = ratings.filter(
		        new Function<Tuple2<Integer, Rating>, Boolean>() {
		            public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
		                return tuple._2.rating() < 3.5;
		            }
		        }
		);
		
		List<Tuple2<Integer, Rating>> list = training.collect();
		
		for (Tuple2<Integer, Rating> tuple : list) {
			System.out.println(tuple);
		}
		
		
		
		
//		System.out.println("User Count : " + userCount);		
	}
	
	public void readProperties(String filePath) {
		InputStream is;
		try {
			is = new FileInputStream(filePath);
			properties.load(is);			
			dataSetPath = (String) properties.get("datasetPath");

		} catch (IOException e) {
			e.printStackTrace();
		}	    
	}
	
	public static void main(String args[]) {
		DataHandler data = new DataHandler();
		data.readProperties(args[0]);
		data.loadData();
	}
}
