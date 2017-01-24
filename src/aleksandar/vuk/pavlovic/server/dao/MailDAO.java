package aleksandar.vuk.pavlovic.server.dao;


import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;

import aleksandar.vuk.pavlovic.model.MailToServer;
import aleksandar.vuk.pavlovic.model.MailFromServer;
import aleksandar.vuk.pavlovic.model.MailSnippet;


/**
 * A singleton class used for accessing the mail information.
 */
public class MailDAO
{
	/**
	 * Gets the mail data access object instance.
	 */
	public static MailDAO getInstance()
	{
		synchronized (lock)
		{
			if (instance == null)
			{
				try
				{
					instance = new MailDAO();
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
	 * Gets mail snippets indended for the specified user.
	 * @param to The recipient of the mails.
	 * @return List of snippets.
	 * @throws Exception if accessing data throws.
	 */
	public Collection<MailSnippet> getSnippetsTo(String to) throws Exception
	{
		ArrayList<MailSnippet> list = new ArrayList<>();
		
		try (Connection connection = SQLite.createConnection();
				PreparedStatement stmt = connection
						.prepareStatement("SELECT from_, subject, id FROM Mails WHERE to_ = ?;");)
		{
			stmt.setString(1, to);

			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					final String from = rs.getString("from_");
					final String subject = rs.getString("subject");
					final int id = rs.getInt("id");
					list.add(new MailSnippet(from, subject, id));
				}		
			}
		}
		
		return list;
	}


	/**
	 * Gets the mail with the specified id for the specified user.
	 * @param to The recipient of the mails.
	 * @param id The id (ordinal number) of the mail.
	 * @return The desired mail, or null if it cannot be found.
	 * @throws Exception if accessing data throws.
	 */
	public MailFromServer getMail(String to, int id) throws Exception
	{
		try (Connection connection = SQLite.createConnection();
				PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Mails WHERE to_ = ? AND id = ?;");)
		{
			stmt.setString(1, to);
			stmt.setInt(2, id);

			try (ResultSet rs = stmt.executeQuery())
			{
				if (rs.next())
				{
					final String from = rs.getString("from_");
					final String subject = rs.getString("subject");
					final String body = rs.getString("body");

					return new MailFromServer(from, to, subject, body, id);
				}
				else
				{
					return null;
				}
			}
		}
	}


	/**
	 * Sends mail to the user specified in the mail itself.
	 * @param mail Mail to send
	 * @return true, if the mail was successfully sent.
	 * @throws Exception if accessing data throws.
	 */
	public boolean addMail(MailToServer mail) throws Exception
	{
		if (!UserDAO.getInstance().existsUser(mail.to))
			return false;

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

		return true;
	}


	/**
	 * Constructs the {@link MailDAO} singleton instance.
	 * @throws Exception if creating the data access object fails.
	 */
	private MailDAO() throws Exception
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
	}

	private static Object lock = new Object();
	private static MailDAO instance;
}
