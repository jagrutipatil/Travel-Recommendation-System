package edu.data.mapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import edu.data.model.User;
import scala.Tuple2;
import scala.runtime.AbstractFunction1;

@SuppressWarnings("serial")
public class UserMapper extends AbstractFunction1<ResultSet, Tuple2<String, User>> implements Serializable{

	public Tuple2<String, User> apply(ResultSet result) {		
		try {
			return new Tuple2<String, User>(result.getString(1), new User(result.getString(2), result.getString(3)));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
