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

import com.edu.util.ConfigurationService;
import com.edu.util.HelperUtils;

import edu.data.model.LocRating;
import edu.data.model.Location;
import edu.data.model.User;
import scala.Tuple2;
import scala.reflect.ClassManifestFactory$;
import scala.runtime.AbstractFunction0;
import scala.runtime.AbstractFunction1;


@SuppressWarnings("serial")
public class TravelRecommendation implements Serializable{
	private Properties properties = new Properties();
	private String dataSetPath = "";
	private static TravelRecommendation instance = null;
	private DatabaseConnection conn = null; 
	public static TravelRecommendation getInstance() {
		if (instance == null) {
			instance = new TravelRecommendation();
		}
		return instance;
	}
	
	private TravelRecommendation() {
		init();
	}
	
	
	public void init() {
		conn = new DatabaseConnection("com.mysql.jdbc.Driver", ConfigurationService.getInstance().getUrl(), ConfigurationService.getInstance().getUsername(), ConfigurationService.getInstance().getPassword());
		loadLocationFromDB();
		loadUsersFromDB();
		loadRatingFromFile();
		getRecommendationModel();
	}	
	
	public List<Location> getRecommendationForUser(int user) {
		final int userId = user;
        List<Rating> recommendations;
        JavaRDD<Tuple2<Integer, Rating>> ratings = DataService.getInstance().getRatings();        
        JavaSparkContext sc = DataService.getInstance().getSc();
        Map<Integer, Location> products = DataService.getInstance().getProducts().collectAsMap();
        
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
        
        MatrixFactorizationModel bestModel = DataService.getInstance().getBestModel();
        recommendations = bestModel.predict(JavaPairRDD.fromJavaRDD(userCandidates)).collect();        
        
        //Sorting the recommended products and sort them according to the rating
        Collections.sort(recommendations, new Comparator<Rating>() {
            public int compare(Rating r1, Rating r2) {
                return r1.rating() < r2.rating() ? -1 : r1.rating() > r2.rating() ? 1 : 0;
            }
        });
        
        //get top 50 from the recommended products.
        recommendations = recommendations.subList(0, 8);
        List<Location> locations = new ArrayList<Location>();

        //TODO fetch product ID Information
        System.out.println("Recommendations for user: " + userId);
        for (Rating r : recommendations) {
        	locations.add(products.get(r.product()));
        }        
        return locations;
	}

	public List<Location> getFilteredRecommendation(String inCountry, String inState, String inType, int user) {
		final int userId = user;
		final String country = inCountry;
		final String state = inState;
		final String type = inType;
		
        List<Rating> recommendations;
        JavaRDD<Tuple2<Integer, Rating>> ratings = DataService.getInstance().getRatings();        
        JavaSparkContext sc = DataService.getInstance().getSc();
        JavaPairRDD<Integer, Location> productRDD = DataService.getInstance().getProducts();
        
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
        JavaPairRDD<Integer, Location> filteredProducts = productRDD.filter(
                new Function<Tuple2<Integer, Location>, Boolean>() {
                    public Boolean call(Tuple2<Integer, Location> tuple) throws Exception {
                        return tuple._2().getCountry().equalsIgnoreCase(country) && tuple._2().getState().equalsIgnoreCase(state) &&
                        		tuple._2().getType().equalsIgnoreCase(type);
                    }
                }
        );
        
        Map<Integer, Location> products = filteredProducts.collectAsMap();
        
        productSet.addAll(products.keySet());        
        Iterator<Tuple2<Object, Object>> productIterator = userProducts.toLocalIterator();
                
		while(productIterator.hasNext()) {
            Integer locId = (Integer)productIterator.next()._2();
            if(productSet.contains(locId)){
                productSet.remove(locId);
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
        
        MatrixFactorizationModel bestModel = DataService.getInstance().getBestModel();
        recommendations = bestModel.predict(JavaPairRDD.fromJavaRDD(userCandidates)).collect();        
        
        //Sorting the recommended products and sort them according to the rating
        Collections.sort(recommendations, new Comparator<Rating>() {
            public int compare(Rating r1, Rating r2) {
                return r1.rating() < r2.rating() ? -1 : r1.rating() > r2.rating() ? 1 : 0;
            }
        });
        
        //get top 50 from the recommended products.
        recommendations = recommendations.subList(0, 8);
        List<Location> locations = new ArrayList<Location>();

        //TODO fetch product ID Information
        System.out.println("Recommendations for user: " + userId);
        for (Rating r : recommendations) {
        	locations.add(products.get(r.product()));
        }        
        return locations;
	}

	public void printDataCount(JavaRDD<Tuple2<Integer, Rating>> ratings) {
      long ratingCount = ratings.count();
      long userCount = ratings.map(
              new Function<Tuple2<Integer, Rating>, Object>() {
                  public Object call(Tuple2<Integer, Rating> tuple) throws Exception {
                      return tuple._2.user();
                  }
              }
      ).distinct().count();
      
      long locationCount = ratings.map(
              new Function<Tuple2<Integer, Rating>, Object>() {
                  public Object call(Tuple2<Integer, Rating> tuple) throws Exception {
                      return tuple._2.product();
                  }
              }
      ).distinct().count();        
      System.out.println("Got "+ ratingCount+ " ratings from " + userCount + " users for " + locationCount + " locations");
	}

	
	
	public void getRecommendationModel() {
		JavaRDD<Tuple2<Integer, Rating>> ratings = DataService.getInstance().getRatings();
        
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
        
        DataService.getInstance().setBestModel(bestModel);
        System.out.println("RMSE (validation) = " + bestValidationRmse + " for the model trained with rank = "
                + bestRank + ", lambda = " + bestLambda + ", and numIter = " + bestNumIter + ".");

        
//		  Computing Root Mean Square Error in the test dataset
//        Double testRmse = computeRMSE(bestModel, test);
//        System.out.println("The best model was trained with rank = " + bestRank + " and lambda = " + bestLambda
//                + ", and numIter = " + bestNumIter + ", and its RMSE on the test set is " + testRmse + ".");               
	}
	
	public Map<Integer, Location> loadFilteredLocationFromDB(String country, String state, String type) {		
		JdbcRDD<Object[]> locationJdbcRDD = new JdbcRDD<>(DataService.getInstance().getSc().sc(), conn, "select * from location where location.locationid > ? and location.locationid < ? and location.country = ' "+ country + "' and location.state = '"+ state +"' and location.type = '"+ type +"' ", -1,
                499999999, 10, new MapResult(), ClassManifestFactory$.MODULE$.fromClass(Object[].class));
		JavaRDD<Object[]> locationRDD = JavaRDD.fromRDD(locationJdbcRDD, ClassManifestFactory$.MODULE$.fromClass(Object[].class));

		Map<Integer, Location> locations = locationRDD.mapToPair(
		        new PairFunction<Object[], Integer, Location>() {
		            public Tuple2<Integer, Location> call(final Object[] record) throws Exception {
		            	Location loc = new Location(Integer.parseInt(record[0] + ""), record[1] + "", record[2] + "", record[3] + "", record[4] + "", record[5] + "", record[6] + "", Double.parseDouble(record[7] + ""), Double.parseDouble(record[8] + ""), Double.parseDouble(record[9] + ""), Double.parseDouble(record[10] + ""), "" + record[11]);
		            	String str = HelperUtils.locationTOJSON(loc);
		                	return new Tuple2<Integer, Location>(Integer.parseInt(record[0] + ""), loc);
		                }
		        }
		).collectAsMap();
		return locations;
	}

	public void loadLocationFromDB() {		
		JdbcRDD<Object[]> locationJdbcRDD = new JdbcRDD<>(DataService.getInstance().getSc().sc(), conn, "select * from location where location.locationid > ? and location.locationid < ?", -1,
                499999999, 10, new MapResult(), ClassManifestFactory$.MODULE$.fromClass(Object[].class));
		JavaRDD<Object[]> locationRDD = JavaRDD.fromRDD(locationJdbcRDD, ClassManifestFactory$.MODULE$.fromClass(Object[].class));

		JavaPairRDD<Integer, Location> locations = locationRDD.mapToPair(
		        new PairFunction<Object[], Integer, Location>() {
		            public Tuple2<Integer, Location> call(final Object[] record) throws Exception {
		            	Location loc = new Location(Integer.parseInt(record[0] + ""), record[1] + "", record[2] + "", record[3] + "", record[4] + "", record[5] + "", record[6] + "", Double.parseDouble(record[7] + ""), Double.parseDouble(record[8] + ""), Double.parseDouble(record[9] + ""), Double.parseDouble(record[10] + ""), "" + record[11]);
		            	String str = HelperUtils.locationTOJSON(loc);
		                	return new Tuple2<Integer, Location>(Integer.parseInt(record[0] + ""), loc);
		                }
		        }
		);
		
		DataService.getInstance().setProducts(locations);
		System.out.println("Loaded Location Data");
		printLocation(locations);
	}
	
	public void loadRatingFromFile() {
		final JavaRDD<String> ratingRDD = DataService.getInstance().getSc().textFile(ConfigurationService.getInstance().getRatingsFile());
		
		JavaRDD<Tuple2<Integer, Rating>> ratings = ratingRDD.map(new Function<String, Tuple2<Integer, Rating>>(){
            public Tuple2<Integer, Rating> call(String s) throws Exception {
                String[] row = s.split("::");
                Integer cacheStamp = Integer.parseInt(row[3]) % 10;
                Rating rating = new Rating(Integer.parseInt(row[0]), Integer.parseInt(row[1]), Double.parseDouble(row[2]));
                return new Tuple2<Integer, Rating>(cacheStamp, rating);
            }
		});
		List<Tuple2<Integer, Rating>> data = ratings.collect();
		DataService.getInstance().setRatings(ratings);		
	}
	
	public void loadUsersFromDB() {		
		JdbcRDD<Object[]> usersJdbcRDD = new JdbcRDD<>(DataService.getInstance().getSc().sc(), conn, "select * from users where users.userid > ? and users.userid < ?", -1,
                499999999, 10, new MapResult(), ClassManifestFactory$.MODULE$.fromClass(Object[].class));
        JavaRDD<Object[]> usersRDD = JavaRDD.fromRDD(usersJdbcRDD, ClassManifestFactory$.MODULE$.fromClass(Object[].class));

        List<Tuple2<Integer, String>> users = usersRDD.map(new Function<Object[], Tuple2<Integer, String>>() {
            @Override
            public Tuple2<Integer, String> call(final Object[] record) throws Exception {
            	String json = HelperUtils.userTOJSON(new User(Integer.parseInt(record[0] + ""), record[1] + "", record[2] + ""));
                return new Tuple2<Integer, String>(Integer.parseInt(record[0] + ""), json);
            }
        }).collect();
	}
	

	public void loadFromDB() {		

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
	
		
	public void printUsers(List<Tuple2<Integer, String>> users) {
		System.out.println("Total Users: " + users.size());
		
        for (Tuple2<Integer, String> user: users) {
        	System.out.println(user._1() + " " + user._2());
        }        
	}

	
	public void printLocation(JavaPairRDD<Integer, Location> locationRDD) {
		Map<Integer, Location> locations = locationRDD.collectAsMap();
		System.out.println("Total Location: " + locations.size());
		
        for (Map.Entry<Integer, Location> location: locations.entrySet()) {
        	System.out.println(location.getKey() + " " + location.getValue().getLocationName());
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
		if (args.length < 1) {
			printUsage();
		}
		
		ConfigurationService.getInstance().readProperties(args[0]);
		TravelRecommendation.getInstance().getRecommendationForUser(2);
	}
	
	public static void printUsage() {
		System.out.println("Usage: <configuration-file>");
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
}