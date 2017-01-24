package aleksandar.vuk.pavlovic.server.dao;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import aleksandar.vuk.pavlovic.model.User;


/**
 * A singleton class used for accessing the user information.
 */
public class UserDAO
{
	/**
	 * Gets the user data access object instance.
	 */
	public static UserDAO getInstance()
	{
		synchronized (lock)
		{
			if (instance == null)
			{
				try
				{
					instance = new UserDAO();
				}
				catch (Exception e)
				{
					e.printStackTrace();
					System.exit(1);
				}
			}

			return instance;
		}
	}


	/**
	 * Checks whether the user with the specified name exists.
	 * @param name The name of the user.
	 * @return true, if the user exists.
	 * @throws Exception if accessing data throws.
	 */
	public boolean existsUser(String name) throws Exception
	{
		try (Connection connection = SQLite.createConnection();
				PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM Users WHERE name = ?;");)
		{
			stmt.setString(1, name);

			try (ResultSet rs = stmt.executeQuery())
			{
				rs.next();

				return rs.getInt(1) != 0;
			}
		}
	}


	/**
	 * Retrieves all registered users.
	 * @return List of registered users.
	 * @throws Exception if accessing data throws.
	 */
	public Collection<User> getUsers() throws Exception
	{
		ArrayList<User> list = new ArrayList<>();
		
		try (Connection connection = SQLite.createConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Users;");)
		{
			while (rs.next())
			{
				final String name = rs.getString("name"); // absolutely disgusting
				list.add(new User(name));
			}
		}
		
		return list;
	}


	/**
	 * Tries to register a new user.
	 * @param user User to register.
	 * @return true if the user was successfully registered.
	 * @throws Exception if accessing data throws.
	 */
	public boolean addUser(User user) throws Exception
	{
		try (Connection connection = SQLite.createConnection();
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO Users (name) VALUES (?);");)
		{
			stmt.setString(1, user.name);
			stmt.executeUpdate();
			return true;
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() != SQLite.PRIMARY_KEY_FAILED)
				return false;
			else
				throw e;
		}	
	}


	/**
	 * Constructs the singleton instance.
	 * @throws Exception if construction of data access object fails.
	 */
	private UserDAO() throws Exception
	{
		try (Connection connection = SQLite.createConnection();)
		{
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet rs = meta.getTables(null, null, "Users", null);

			if (!rs.next())
			{
				try (Statement stmt = connection.createStatement())
				{
					stmt.execute("CREATE TABLE Users(name TEXT PRIMARY KEY);");
				}
			}
		}
	}

	private static Object lock = new Object();
	private static UserDAO instance;
}
