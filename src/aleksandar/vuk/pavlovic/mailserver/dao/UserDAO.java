package aleksandar.vuk.pavlovic.mailserver.dao;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import aleksandar.vuk.pavlovic.mailserver.model.User;


public class UserDAO
{
	public static UserDAO getInstance()
	{
		synchronized (lock)
		{
			if (instance == null)
				instance = new UserDAO();

			return instance;
		}
	}


	public boolean existsUser(String name)
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
		catch (SQLException e)
		{
			e.printStackTrace();

			return false;
		}
	}


	public Collection<User> getUsers()
	{
		try (Connection connection = SQLite.createConnection();
				Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM Users;");)
		{
			ArrayList<User> list = new ArrayList<>();

			while (rs.next())
			{
				String name = rs.getString("name"); // absolutely disgusting
				list.add(new User(name));
			}

			return list;
		}
		catch (SQLException e)
		{
			return new ArrayList<>();
		}
	}


	public boolean addUser(User user)
	{
		try (Connection connection = SQLite.createConnection();
				PreparedStatement stmt = connection.prepareStatement("INSERT INTO Users (name) VALUES (?);");)
		{
			stmt.setString(1, user.name);
			stmt.executeUpdate();
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == SQLite.PRIMARY_KEY_FAILED)
				return false;
			else
				e.printStackTrace();
		}

		return true;
	}


	private UserDAO()
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
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static Object lock = new Object();
	private static UserDAO instance;
}
