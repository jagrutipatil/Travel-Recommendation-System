package edu.data.mapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.data.model.LocRating;
import scala.Tuple2;
import scala.runtime.AbstractFunction1;

@SuppressWarnings("serial")
public class RatingMapper extends AbstractFunction1<ResultSet, Tuple2<Long, LocRating>> implements Serializable{

	public Tuple2<Long, LocRating> apply(ResultSet result) {
		try {
			return new Tuple2<Long, LocRating>(result.getLong(1), new LocRating(result.getString(2), result.getString(3), result.getDouble(4)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
}
