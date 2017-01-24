package aleksandar.vuk.pavlovic.server.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**
 * A helper class for initializing and using SQLite.
 */
public class SQLite
{
	public static final int PRIMARY_KEY_FAILED = 19;


	static
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	/**
	 * Creates a connection to the database, which is used for all operations on the database.
	 * @return connection to database.
	 * @throws SQLException if establishing connection fails.
	 */
	public static Connection createConnection() throws SQLException
	{
		Connection connection = DriverManager.getConnection("jdbc:sqlite:MailServer.db");
		connection.setAutoCommit(true);
		return connection;
	}
}
