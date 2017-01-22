package aleksandar.vuk.pavlovic.mailserver.dao;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import aleksandar.vuk.pavlovic.model.MailToServer;
import aleksandar.vuk.pavlovic.model.MailFromServer;
import aleksandar.vuk.pavlovic.model.MailSnippet;


public class MailDAO
{
	public static MailDAO getInstance()
	{
		synchronized (lock)
		{
			if (instance == null)
				instance = new MailDAO();

			return instance;
		}
	}


	public Collection<MailSnippet> getSnippetsTo(String to)
	{
		try (Connection connection = SQLite.createConnection();
				PreparedStatement stmt = connection.prepareStatement("SELECT from_, subject, id FROM Mails WHERE to_ = ?;");)
		{
			stmt.setString(1, to);

			try (ResultSet rs = stmt.executeQuery())
			{
				ArrayList<MailSnippet> list = new ArrayList<>();

				while (rs.next())
				{
					String from = rs.getString("from_"); // absolutely disgusting
					String subject = rs.getString("subject"); // absolutely
																// disgusting
					int id = rs.getInt("id"); // absolutely disgusting
					list.add(new MailSnippet(from, subject, id));
				}

				return list;
			}
		}
		catch (SQLException e)
		{
			return new ArrayList<>();
		}
	}
	
	
	public MailFromServer getMail(String to, int id)
	{
		try
		(Connection connection = SQLite.createConnection();
		PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Mails WHERE to_ = ? AND id = ?;");)
		{
			stmt.setString(1, to);
			stmt.setInt(2, id);
			
			try (ResultSet rs = stmt.executeQuery())
			{
				if (rs.next())
				{
					String from = rs.getString("from_");
					String subject = rs.getString("subject");
					String body = rs.getString("body");
					
					return new MailFromServer(from, to, subject, body, id);
				}
				else
				{
					return null;
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
			return null;
		}
	}


	public boolean addMail(MailToServer mail)
	{
		try (Connection connection = SQLite.createConnection();)
		{
			int count;
			
			try (PreparedStatement stmt = connection.prepareStatement("SELECT COUNT(*) FROM Mails WHERE to_ = ?;");)
			{
				stmt.setString(1, mail.to);
				
				try (ResultSet rs = stmt.executeQuery();)
				{
					rs.next();
					count = rs.getInt(1);
				}
			}

			try (PreparedStatement stmt = connection
					.prepareStatement("INSERT INTO Mails (from_, to_, subject, body, id) VALUES (?, ?, ?, ?, ?);");)
			{
				stmt.setString(1, mail.from);
				stmt.setString(2, mail.to);
				stmt.setString(3, mail.subject);
				stmt.setString(4, mail.body);
				stmt.setInt(5, count + 1);
				stmt.executeUpdate();
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return true;
	}


	private MailDAO()
	{
		try (Connection connection = SQLite.createConnection())
		{
			DatabaseMetaData meta = connection.getMetaData();
			ResultSet rs = meta.getTables(null, null, "Mails", null);

			if (!rs.next())
			{
				try (Statement stmt = connection.createStatement())
				{
					stmt.execute("CREATE TABLE Mails(from_ TEXT, to_ TEXT, subject TEXT, body TEXT, id INTEGER,"
							+ "FOREIGN KEY(from_) REFERENCES Users(name),"
							+ "FOREIGN KEY(to_) REFERENCES Users(name));");
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	private static Object lock = new Object();
	private static MailDAO instance;
}
