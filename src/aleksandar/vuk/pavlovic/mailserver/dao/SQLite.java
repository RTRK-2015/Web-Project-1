package aleksandar.vuk.pavlovic.mailserver.dao;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


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
	
	
	public static Connection createConnection() throws SQLException
	{
		Connection connection = DriverManager.getConnection("jdbc:sqlite:MailServer.db");
		connection.setAutoCommit(true);
		return connection;
	}
}
