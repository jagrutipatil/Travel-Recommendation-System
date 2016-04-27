package spark;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import scala.runtime.AbstractFunction0;

public class DatabaseConnection extends AbstractFunction0<Connection> implements Serializable{
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
