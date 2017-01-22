package aleksandar.vuk.pavlovic.mailserver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import aleksandar.vuk.pavlovic.mailserver.dao.MailDAO;
import aleksandar.vuk.pavlovic.mailserver.dao.UserDAO;
import aleksandar.vuk.pavlovic.mailserver.model.User;
import aleksandar.vuk.pavlovic.model.MailFromServer;
import aleksandar.vuk.pavlovic.model.MailSnippet;
import aleksandar.vuk.pavlovic.model.MailToServer;


public class ServerThread extends Thread
{
	public ServerThread(Socket sock) throws IOException
	{
		this.sock = sock;
		logged = false;

		start();
	}


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

				String request = reader.readLine();
				System.out.println("Received: " + request);

				@SuppressWarnings("unchecked")
				Map<String, Object> requestMap = new Gson().fromJson(request, Map.class);
				String command = (String) requestMap.get("command");

				switch (command)
				{
				case "REGISTER":
					respondRegister(requestMap, writer);
					break;

				case "LOGIN":
					respondLogin(requestMap, writer);
					break;

				case "LOGOFF":
					respondLogoff(requestMap, writer);
					break;

				case "LIST":
					respondList(requestMap, writer);
					break;

				case "RECEIVE":
					respondReceive(requestMap, writer);
					break;

				case "SEND":
					respondSend(requestMap, writer);
					break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	private void respondRegister(Map<String, Object> requestMap, PrintWriter writer)
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (!logged)
		{
			String userName = (String) requestMap.get("userName");
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

		String response = new Gson().toJson(responseMap, Map.class);
		writer.println(response);
		writer.flush();
	}


	private void respondLogin(Map<String, Object> requestMap, PrintWriter writer)
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (!logged)
		{
			String userName = (String) requestMap.get("userName");
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

		String response = new Gson().toJson(responseMap);
		writer.println(response);
		writer.flush();
	}


	private void respondLogoff(Map<String, Object> requestMap, PrintWriter writer)
	{
		logged = false;
	}


	private void respondList(Map<String, Object> requestMap, PrintWriter writer)
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (logged)
		{
			MailDAO instance = MailDAO.getInstance();

			ArrayList<MailSnippet> snippets = new ArrayList<>(instance.getSnippetsTo(userName));
			ArrayList<String> mailsJSON = new ArrayList<>();
			for (MailSnippet snippet : snippets)
				mailsJSON.add(new Gson().toJson(snippet, MailSnippet.class));
				
			responseMap.put("success", true);
			responseMap.put("mails", mailsJSON);
		}
		else
		{
			responseMap.put("success", false);
			responseMap.put("error", "You are not logged in!");
		}

		String response = new Gson().toJson(responseMap);
		writer.println(response);
		writer.flush();
	}


	private void respondReceive(Map<String, Object> requestMap, PrintWriter writer)
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (logged)
		{
			double idD = (Double)requestMap.get("id");
			int id = (int)idD;
			MailDAO instance = MailDAO.getInstance();
			MailFromServer mail = instance.getMail(userName, id);

			if (mail != null)
			{
				String mailJSON = new Gson().toJson(mail, MailFromServer.class);
				
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

		String response = new Gson().toJson(responseMap);
		writer.println(response);
		writer.flush();
	}


	private void respondSend(Map<String, Object> requestMap, PrintWriter writer)
	{
		Map<String, Object> responseMap = new HashMap<>();

		if (logged)
		{
			String mailJSON = (String)requestMap.get("mail");
			MailToServer mail = (MailToServer)new Gson().fromJson(mailJSON, MailToServer.class);
			
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

		String response = new Gson().toJson(responseMap);
		writer.println(response);
		writer.flush();
	}

	private boolean logged;
	private String userName;
	private Socket sock;
}
