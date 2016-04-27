package edu.data.mapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.data.model.Location;
import scala.Tuple2;
import scala.runtime.AbstractFunction1;

@SuppressWarnings("serial")
public class LocationMapper extends AbstractFunction1<ResultSet, Tuple2<String, Location>> implements Serializable{

	public Tuple2<String, Location> apply(ResultSet result) {
		try {
			return new Tuple2<String, Location>(result.getString(1), new Location(result.getString(2), result.getString(3)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
