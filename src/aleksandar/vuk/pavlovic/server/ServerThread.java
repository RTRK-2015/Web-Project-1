package aleksandar.vuk.pavlovic.server;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import aleksandar.vuk.pavlovic.model.MailFromServer;
import aleksandar.vuk.pavlovic.model.MailSnippet;
import aleksandar.vuk.pavlovic.model.MailToServer;
import aleksandar.vuk.pavlovic.model.User;
import aleksandar.vuk.pavlovic.server.dao.MailDAO;
import aleksandar.vuk.pavlovic.server.dao.UserDAO;


/**
 * Represents a thread that responds to a single client.
 */
public class ServerThread extends Thread
{
	/**
	 * Spawns a new thread that responds to the client over the specified socket.
	 * @param sock client {@link Socket}.
	 */
	public ServerThread(Socket sock)
	{
		this.sock = sock;
		logged = false;

		start();
	}


	/**
	 * Actually runs the thread. You don't call this, so move along.
	 */
	@Override
	public void run()
	{
		try (InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
				BufferedReader reader = new BufferedReader(streamReader);
				OutputStreamWriter outputWriter = new OutputStreamWriter(sock.getOutputStream());
				BufferedWriter bufferedWriter = new BufferedWriter(outputWriter);
				PrintWriter writer = new PrintWriter(bufferedWriter);)
		{
			while (!sock.isClosed())
			{
				while (!reader.ready())
					;

				final String request = reader.readLine();
				System.out.println("Received: " + request);

				@SuppressWarnings("unchecked")
				final Map<String, Object> requestMap = new Gson().fromJson(request, Map.class);
				final String command = (String) requestMap.get("command");

				String response = null;
				
				switch (command)
				{
				case "REGISTER":
					response = respondRegister(requestMap, writer);
					break;

				case "LOGIN":
					response = respondLogin(requestMap, writer);
					break;

				case "LOGOFF":
					respondLogoff();
					break;

				case "LIST":
					response = respondList(requestMap, writer);
					break;

				case "RECEIVE":
					response = respondReceive(requestMap, writer);
					break;

				case "SEND":
					response = respondSend(requestMap, writer);
					break;
				}
				
				if (response != null)
				{
					System.out.println("Sending " + response);
					writer.println(response);
					writer.flush();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}


	/**
	 * Responds to a register request from the client.
	 * @param requestMap {@link Map} that contains the client's request.
	 * @param writer {@link PrintWriter} of the client's socket.
	 * @return A {@link String} containing the server's response.
	 * @throws Exception if accessing user data (see {@link UserDAO}) fails.
	 */
	private String respondRegister(Map<String, Object> requestMap, PrintWriter writer) throws Exception
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (!logged)
		{
			final String userName = (String) requestMap.get("userName");
			UserDAO instance = UserDAO.getInstance();

			if (instance.addUser(new User(userName)))
			{
				responseMap.put("success", true);
			}
			else
			{
				responseMap.put("success", false);
				responseMap.put("error", "User already exists!");
			}
		}
		else
		{
			responseMap.put("success", false);
			responseMap.put("error", "You are already logged in, idiot!");
		}

		return new Gson().toJson(responseMap, Map.class);
	}


	/**
	 * Responds to a login request from the client.
	 * @param requestMap {@link Map} that contains the client's request.
	 * @param writer {@link PrintWriter} of the client's socket.
	 * @return A {@link String} containing the server's response.
	 * @throws Exception if accessing user data (see {@link UserDAO}) fails.
	 */
	private String respondLogin(Map<String, Object> requestMap, PrintWriter writer) throws Exception
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (!logged)
		{
			final String userName = (String) requestMap.get("userName");
			UserDAO instance = UserDAO.getInstance();

			if (instance.existsUser(userName))
			{
				responseMap.put("success", true);
				logged = true;
				this.userName = userName;
			}
			else
			{
				responseMap.put("success", false);
				responseMap.put("error", "User does not exist!");
			}
		}
		else
		{
			responseMap.put("success", false);
			responseMap.put("error", "You are already logged in, idiot!");
		}

		return new Gson().toJson(responseMap);
	}


	/**
	 * "Responds" to logoff request from the client.
	 */
	private void respondLogoff()
	{
		logged = false;
	}


	/**
	 * Responds to a list mails request from the client.
	 * @param requestMap {@link Map} that contains the client's request.
	 * @param writer {@link PrintWriter} of the client's socket.
	 * @return A {@link String} containing the server's response.
	 * @throws Exception if accessing user data (see {@link MailDAO}) fails.
	 */
	private String respondList(Map<String, Object> requestMap, PrintWriter writer) throws Exception
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (logged)
		{
			MailDAO instance = MailDAO.getInstance();

			final ArrayList<MailSnippet> snippets = new ArrayList<>(instance.getSnippetsTo(userName));
			ArrayList<String> snippetsJSON = new ArrayList<>();
			for (MailSnippet snippet : snippets)
				snippetsJSON.add(new Gson().toJson(snippet, MailSnippet.class));
				
			responseMap.put("success", true);
			responseMap.put("mails", snippetsJSON);
		}
		else
		{
			responseMap.put("success", false);
			responseMap.put("error", "You are not logged in!");
		}

		return new Gson().toJson(responseMap);
	}


	/**
	 * Responds to client's request to receive a mail.
	 * @param requestMap {@link Map} that contains the client's request.
	 * @param writer {@link PrintWriter} of the client's socket.
	 * @return A {@link String} containing the server's response.
	 * @throws Exception if accessing user data (see {@link MailDAO}) fails.
	 */
	private String respondReceive(Map<String, Object> requestMap, PrintWriter writer) throws Exception
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (logged)
		{
			final double idD = (Double)requestMap.get("id");
			final int id = (int)idD;
			MailDAO instance = MailDAO.getInstance();
			final MailFromServer mail = instance.getMail(userName, id);

			if (mail != null)
			{
				final String mailJSON = new Gson().toJson(mail, MailFromServer.class);
				
				responseMap.put("success", true);
				responseMap.put("mail", mailJSON);
			}
			else
			{
				responseMap.put("success", false);
				responseMap.put("error", "Mail with that id does not exist!");
			}
		}
		else
		{
			responseMap.put("success", true);
			responseMap.put("error", "You are not logged in!");
		}

		return new Gson().toJson(responseMap);
	}


	/**
	 * Responds to client's request to send a mail.
	 * @param requestMap {@link Map} that contains the client's request.
	 * @param writer {@link PrintWriter} of the client's socket.
	 * @return A {@link String} containing the server's response.
	 * @throws Exception if accessing user data (see {@link MailDAO}) fails.
	 */
	private String respondSend(Map<String, Object> requestMap, PrintWriter writer) throws Exception
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (logged)
		{
			final String mailJSON = (String)requestMap.get("mail");
			final MailToServer mail = (MailToServer)new Gson().fromJson(mailJSON, MailToServer.class);
			
			MailDAO instance = MailDAO.getInstance();

			if (instance.addMail(mail))
			{
				responseMap.put("success", true);
			}
			else
			{
				responseMap.put("success", false);
				responseMap.put("error", "That user does not exist!");
			}
		}
		else
		{
			responseMap.put("success", false);
			responseMap.put("error", "You are not logged in!");
		}

		return new Gson().toJson(responseMap);
	}

	private boolean logged;
	private String userName;
	private Socket sock;
}
