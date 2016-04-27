package spark;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.mllib.recommendation.ALS;
import org.apache.spark.mllib.recommendation.MatrixFactorizationModel;
import org.apache.spark.mllib.recommendation.Rating;
import org.apache.spark.rdd.JdbcRDD;
import org.apache.spark.storage.StorageLevel;

import edu.data.model.LocRating;
import edu.data.model.Location;
import edu.data.model.User;
import scala.Tuple2;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;

@SuppressWarnings("serial")
public class TravelRecommendation implements Serializable{
	private Properties properties = new Properties();
	private String dataSetPath = "";

	public void init() {
	}
	
	
	public void getRecommendation() {
		SparkConf conf = new SparkConf().setAppName("MovieRecommendation").setMaster("local");
		JavaSparkContext sc = new JavaSparkContext(conf);

		//Reading Data
		final JavaRDD<String> ratingRDD = sc.textFile(dataSetPath);
		final JavaRDD<String> productData = sc.textFile("/home/jagruti/workspace/Vacation-Recommendation-System/dataset/ml-1m/movies.dat");
		
		JavaRDD<Tuple2<Integer, Rating>> ratings = ratingRDD.map(new Function<String, Tuple2<Integer, Rating>>(){
            public Tuple2<Integer, Rating> call(String s) throws Exception {
                String[] row = s.split("::");
                Integer cacheStamp = Integer.parseInt(row[3]) % 10;
                Rating rating = new Rating(Integer.parseInt(row[0]), Integer.parseInt(row[1]), Double.parseDouble(row[2]));
                return new Tuple2<Integer, Rating>(cacheStamp, rating);
            }
		});
		
		List<Tuple2<Integer, Rating>> data = ratings.collect();
		
//		Map<Integer, String> products = productData.mapToPair(
//		        new PairFunction<String, Integer, String>() {
//		            public Tuple2<Integer, String> call(String s) throws Exception {
//		                String[] sarray = s.split("::");
//		                return new Tuple2<Integer, String>(Integer.parseInt(sarray[0]), sarray[1]);
//		            }
//		        }
//		).collectAsMap();
//		
//        long ratingCount = ratingRDD.count();
//        long userCount = ratings.map(
//                new Function<Tuple2<Integer, Rating>, Object>() {
//                    public Object call(Tuple2<Integer, Rating> tuple) throws Exception {
//                        return tuple._2.user();
//                    }
//                }
//        ).distinct().count();
//        
//        long locationCount = ratings.map(
//                new Function<Tuple2<Integer, Rating>, Object>() {
//                    public Object call(Tuple2<Integer, Rating> tuple) throws Exception {
//                        return tuple._2.product();
//                    }
//                }
//        ).distinct().count();
        
//        System.out.println("Got "+ ratingCount+ " ratings from " + userCount + " users for " + locationCount + " locations");
        
        //SPLITTING DATA
        int numPartitions = 10;
        
		// Divide data into training data, validation data, test data
        //TRAINING DATASET
        JavaRDD<Rating> training = ratings.filter(
                new Function<Tuple2<Integer, Rating>, Boolean>() {
                    public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._1() < 6;
                    }
                }
        ).map(
                new Function<Tuple2<Integer, Rating>, Rating>() {
                    public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2();
                    }
                }
        ).repartition(numPartitions).cache();

        StorageLevel storageLevel = new StorageLevel();

        //VALIDATION DATASET
        JavaRDD<Rating> validation = ratings.filter(
                new Function<Tuple2<Integer, Rating>, Boolean>() {
                    public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._1() >= 6 && tuple._1() < 8;
                    }
                }
        ).map(
                new Function<Tuple2<Integer, Rating>, Rating>() {
                    public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2();
                    }
                }
        ).repartition(numPartitions).persist(storageLevel);

        //test data set
        JavaRDD<Rating> test = ratings.filter(
                new Function<Tuple2<Integer, Rating>, Boolean>() {
                    public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._1() >= 8;
                    }
                }
        ).map(
                new Function<Tuple2<Integer, Rating>, Rating>() {
                    public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2();
                    }
                }
        ).persist(storageLevel);

        long numTraining = training.count();
        long numValidation = validation.count();
        long numTest = test.count();

        System.out.println("Training: " + numTraining + ", validation: " + numValidation + ", test: " + numTest);

        
        //TRAINING MODEL
        int[] ranks = {8, 12};
        float[] lambdas = {0.1f, 10.0f};
        int[] numIters = {10, 20};

        double bestValidationRmse = Double.MAX_VALUE;
        int bestRank = 0;
        float bestLambda = -1.0f;
        int bestNumIter = -1;
        MatrixFactorizationModel bestModel = null;

        for (int currentRank : ranks) {
            for (float currentLambda : lambdas) {
                for (int currentNumIter : numIters) {
                    MatrixFactorizationModel model = ALS.train(JavaRDD.toRDD(training), currentRank, currentNumIter, currentLambda);

                    Double validationRmse = computeRMSE(model, validation);

                    if (validationRmse < bestValidationRmse) {
                        bestModel = model;
                        bestValidationRmse = validationRmse;
                        bestRank = currentRank;
                        bestLambda = currentLambda;
                        bestNumIter = currentNumIter;
                    }
                }
            }
        }
        
        System.out.println("RMSE (validation) = " + bestValidationRmse + " for the model trained with rank = "
                + bestRank + ", lambda = " + bestLambda + ", and numIter = " + bestNumIter + ".");

        
        //Computing Root Mean Square Error in the test dataset
        Double testRmse = computeRMSE(bestModel, test);
        System.out.println("The best model was trained with rank = " + bestRank + " and lambda = " + bestLambda
                + ", and numIter = " + bestNumIter + ", and its RMSE on the test set is " + testRmse + ".");
        
        
        //GIVE RECOMMENDATION FROM NEW USER
        
        final int userId = 2;
        
        List<Rating> recommendations;
        
        //Getting the users ratings
        JavaRDD<Rating> userRatings = ratings.filter(
                new Function<Tuple2<Integer, Rating>, Boolean>() {
                    public Boolean call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2().user() == userId;
                    }
                }
        ).map(
                new Function<Tuple2<Integer, Rating>, Rating>() {
                    public Rating call(Tuple2<Integer, Rating> tuple) throws Exception {
                        return tuple._2();
                    }
                }
        );
        
        //Getting the product ID's of the products that user rated
        JavaRDD<Tuple2<Object, Object>> userProducts = userRatings.map(
                new Function<Rating, Tuple2<Object, Object>>() {
                    public Tuple2<Object, Object> call(Rating r) {
                        return new Tuple2<Object, Object>(r.user(), r.product());
                    }
                }
        );
        
        List<Integer> productSet = new ArrayList<Integer>();
        productSet.addAll(products.keySet());
        
        Iterator<Tuple2<Object, Object>> productIterator = userProducts.toLocalIterator();
        
        //Removing the user watched (rated) set from the all product set
        while(productIterator.hasNext()) {
            Integer movieId = (Integer)productIterator.next()._2();
            if(productSet.contains(movieId)){
                productSet.remove(movieId);
            }
        }
        
        JavaRDD<Integer> candidates = sc.parallelize(productSet);
        
        JavaRDD<Tuple2<Integer, Integer>> userCandidates = candidates.map(
                new Function<Integer, Tuple2<Integer, Integer>>() {
                    public Tuple2<Integer, Integer> call(Integer integer) throws Exception {
                        return new Tuple2<Integer, Integer>(userId, integer);
                    }
                }
        );
        
        //Predict recommendations for the given user
        recommendations = bestModel.predict(JavaPairRDD.fromJavaRDD(userCandidates)).collect();
        
        //Sorting the recommended products and sort them according to the rating
        Collections.sort(recommendations, new Comparator<Rating>() {
            public int compare(Rating r1, Rating r2) {
                return r1.rating() < r2.rating() ? -1 : r1.rating() > r2.rating() ? 1 : 0;
            }
        });
        
        //get top 50 from the recommended products.
        recommendations = recommendations.subList(0, 50);
        
        System.out.println("Recommendations for user: " + userId);
        for (Rating r : recommendations) {
        	System.out.println("Product: " + r.product() + "\tRating:" + r.rating());
        }        
	}
	
	
//	private static List<Rating> getRecommendations(final int userId, MatrixFactorizationModel model, JavaRDD<Tuple2<Integer, Rating>> ratings, Map<Integer, String> products) {
//}
	
	public void loadFromDB() {
//		SparkConf conf = new SparkConf().setAppName("TravelRecommendation").setMaster("local");
//		JavaSparkContext sc = new JavaSparkContext(conf);		
//		DatabaseConnection conn = new DatabaseConnection("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/cmpe239", "root", "linux2015");
//	
//		JdbcRDD<Object[]> usersJdbcRDD = new JdbcRDD<>(sc.sc(), conn, "select * from users where users.userid > ? and users.userid < ?", -1,
//                              499999999, 10, new MapResult(), ClassManifestFactory$.MODULE$.fromClass(Object[].class));
//
//        JavaRDD<Object[]> usersRDD = JavaRDD.fromRDD(usersJdbcRDD, ClassManifestFactory$.MODULE$.fromClass(Object[].class));
//        
//		JdbcRDD<Object[]> locationJdbcRDD = new JdbcRDD<>(sc.sc(), conn, "select * from location where location.locationid > ? and location.locationid < ?", -1,
//              499999999, 10, new MapResult(), ClassManifestFactory$.MODULE$.fromClass(Object[].class));
//
//		JavaRDD<Object[]> locationRDD = JavaRDD.fromRDD(locationJdbcRDD, ClassManifestFactory$.MODULE$.fromClass(Object[].class));
//
//		JdbcRDD<Object[]> ratingsJdbcRDD = new JdbcRDD<>(sc.sc(), conn, "select * from ratings where ratings.userid > ? and ratings.userid < ?", -1,
//              499999999, 10, new MapResult(), ClassManifestFactory$.MODULE$.fromClass(Object[].class));
//
//		JavaRDD<Object[]> ratingRDD = JavaRDD.fromRDD(ratingsJdbcRDD, ClassManifestFactory$.MODULE$.fromClass(Object[].class));
		

//      printUsers(usersRDD);
//		printLocation(locationRDD);
//		printRatings(ratingRDD);
		
//        JavaRDD<Tuple2<Integer, Rating>> ratings = ratingRDD.map(new Function<Object[], Tuple2<Integer, Rating>>() {
//            @Override
//            public Tuple2<Integer, Rating> call(final Object[] record) throws Exception {
//            	Long timestamp = Long.parseLong(record[3] + "") ;
//                return new Tuple2<Integer, Rating>((int)(timestamp % 10), new Rating(Integer.parseInt(record[0] + ""), Integer.parseInt(record[1] + ""), Double.parseDouble(record[2] + "")));
//            }
//        });
//
//        Map<Integer, Location> locations = locationRDD.mapToPair(new PairFunction<Object[], Integer, Location>() {
//            @Override
//            public Tuple2<Integer, Location> call(final Object[] record) throws Exception {
//                return new Tuple2<Integer, Location>(Integer.parseInt(record[0] + ""), new Location(record[1] + "", record[2] + ""));
//            }
//        }).collectAsMap();

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

	
	public static Double computeRMSE(MatrixFactorizationModel model, JavaRDD<Rating> data) {
		JavaRDD<Tuple2<Object, Object>> userProducts = data.map(
		        new Function<Rating, Tuple2<Object, Object>>() {
		            public Tuple2<Object, Object> call(Rating r) {
		                return new Tuple2<Object, Object>(r.user(), r.product());
		            }
		        }
		);

		JavaPairRDD<Tuple2<Integer, Integer>, Double> predictions = JavaPairRDD.fromJavaRDD(
		        model.predict(JavaRDD.toRDD(userProducts)).toJavaRDD().map(
		                new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
		                    public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r) {
		                        return new Tuple2<Tuple2<Integer, Integer>, Double>(
		                                new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
		                    }
		                }
		        ));
		JavaRDD<Tuple2<Double, Double>> predictionsAndRatings =
		        JavaPairRDD.fromJavaRDD(data.map(
		                new Function<Rating, Tuple2<Tuple2<Integer, Integer>, Double>>() {
		                    public Tuple2<Tuple2<Integer, Integer>, Double> call(Rating r) {
		                        return new Tuple2<Tuple2<Integer, Integer>, Double>(
		                                new Tuple2<Integer, Integer>(r.user(), r.product()), r.rating());
		                    }
		                }
		        )).join(predictions).values();

		double mse =  JavaDoubleRDD.fromRDD(predictionsAndRatings.map(
		        new Function<Tuple2<Double, Double>, Object>() {
		            public Object call(Tuple2<Double, Double> pair) {
		                Double err = pair._1() - pair._2();
		                return err * err;
		            }
		        }
		).rdd()).mean();

		return Math.sqrt(mse);
	}
	
	
	public void todo() {						
		
		// train the training data, get the model, compute RMSE for each model with different factors lamda, no of iternations,
		
		// save the model with less RMSE
		
		//get the ratings given by specific user
		
		//
	}
	
	public void printUsers(JavaRDD<Object[]> usersRDD) {
		System.out.println("Total Users: " + usersRDD.count());
		
        List<Tuple2<Integer, User>> users = usersRDD.map(new Function<Object[], Tuple2<Integer, User>>() {
            @Override
            public Tuple2<Integer, User> call(final Object[] record) throws Exception {
                return new Tuple2<Integer, User>(Integer.parseInt(record[0] + ""), new User(record[1] + "", record[2] + ""));
            }
        }).collect();

        for (Tuple2<Integer, User> user: users) {
        	System.out.println(user._1() + " " + user._2.getFirstName() + " " + user._2.getLastName());
        }        
	}
	
	public void printLocation(JavaRDD<Object[]> locationRDD) {
		System.out.println("Total Location: " + locationRDD.count());
        List<Tuple2<Integer, Location>> locations = locationRDD.map(new Function<Object[], Tuple2<Integer, Location>>() {
            @Override
            public Tuple2<Integer, Location> call(final Object[] record) throws Exception {
                return new Tuple2<Integer, Location>(Integer.parseInt(record[0] + ""), new Location(record[1] + "", record[2] + ""));
            }
        }).collect();

        for (Tuple2<Integer, Location> location: locations) {
        	System.out.println(location._1() + " " + location._2.getLocationName() + " " + location._2.getLocationType());
        }       
	}


	public void printRatings(JavaRDD<Object[]> ratingsRDD) {
		System.out.println("Total Ratings: " + ratingsRDD.count());
        List<Tuple2<Long, LocRating>> ratings = ratingsRDD.map(new Function<Object[], Tuple2<Long, LocRating>>() {
            @Override
            public Tuple2<Long, LocRating> call(final Object[] record) throws Exception {
                return new Tuple2<Long, LocRating>(Long.parseLong(record[3] + "") % 10, new LocRating(record[0] + "", record[1] + "", Double.parseDouble(record[2] + "")));
            }
        }).collect();

        for (Tuple2<Long, LocRating> rating: ratings) {
        	System.out.println(rating._1() + " " + rating._2.getUserId() + " " + rating._2.getLocationId() + " " + rating._2.getRating());
        }       
	}

	public static void main(String args[]) {
		TravelRecommendation recomm = new TravelRecommendation();
		recomm.readProperties(args[0]);
		recomm.init();
		recomm.getRecommendation();
	}
	
	static class MapResult extends AbstractFunction1<ResultSet, Object[]> implements Serializable {
        public Object[] apply(ResultSet row) {        	
            return JdbcRDD.resultSetToObjectArray(row);
        }
    }
	

	static class DatabaseConnection extends AbstractFunction0<Connection> implements Serializable{
		private String driverClassName;
		private String connectionUrl;
		private String username;
		private String password;
		
		
		public DatabaseConnection(String driverClassName, String connectionUrl, String username, String password) {
			this.driverClassName = driverClassName;
			this.connectionUrl = connectionUrl;
			this.username = username;
			this.password = password;
		}
		
		@Override
		public Connection apply() {
			try {
				Class.forName(driverClassName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			Properties props = new Properties();
			props.setProperty("user", username);
			props.setProperty("password", password);
			Connection conn = null;

			try {
				conn = DriverManager.getConnection(connectionUrl, props);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return conn;
		}						
	}

	public static class UserMapper extends AbstractFunction1<ResultSet, Tuple2<String, User>> implements Serializable{

		public Tuple2<String, User> apply(ResultSet result) {		
			try {
				return new Tuple2<String, User>(result.getString(1), new User(result.getString(2), result.getString(3)));
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}

	}

}

