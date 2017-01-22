package aleksandar.vuk.pavlovic.client;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.google.gson.Gson;

import aleksandar.vuk.pavlovic.model.MailFromServer;
import aleksandar.vuk.pavlovic.model.MailSnippet;
import aleksandar.vuk.pavlovic.model.MailToServer;


public class Client
{
	public static final int PORT = 9000;


	public static void main(String[] args)
	{
		logged = false;

		try (Scanner scanner = new Scanner(System.in);
				Socket sock = new Socket("127.0.0.1", PORT);
				InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(streamReader);
				OutputStreamWriter outputWriter = new OutputStreamWriter(sock.getOutputStream());
				BufferedWriter bufferedWriter = new BufferedWriter(outputWriter);
				PrintWriter printWriter = new PrintWriter(bufferedWriter);)
		{
			writer = printWriter;
			reader = bufferedReader;

			while (true)
			{
				printMenu();

				int choice = scanner.nextInt();
				scanner.nextLine();
				
				switch (choice)
				{
				case 0:
					return;

				case 1:
					register(scanner);
					break;

				case 2:
					if (logged)
						System.out.println("Already logged in!");
					else
						login(scanner);
					break;

				case 3:
					if (!logged)
						System.out.println("Not logged in!");
					else
						logoff();
					break;

				case 4:
					if (!logged)
						System.out.println("Not logged in!");
					else
						list();
					break;

				case 5:
					if (!logged)
						System.out.println("Not logged in!");
					else
						receive(scanner);
					break;

				case 6:
					if (!logged)
						System.out.println("Not logged in!");
					else
						send(scanner);
					break;
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}


	private static void printMenu()
	{
		System.out.println("0. Exit");
		System.out.println("1. Register");
		System.out.println("2. Login");
		System.out.println("3. Logoff");
		System.out.println("4. List");
		System.out.println("5. Receive");
		System.out.println("6. Send");
		System.out.println("Choice: ");
	}


	private static void register(Scanner scanner) throws IOException
	{
		System.out.print("Name: ");
		userName = scanner.nextLine();

		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "REGISTER");
		requestMap.put("userName", userName);

		String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		String response = reader.readLine();

		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
			System.out.println("Successfully registered");
		else
			System.out.println((String) responseMap.get("error"));
	}


	private static void login(Scanner scanner) throws IOException
	{
		System.out.print("Name: ");
		userName = scanner.nextLine();

		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "LOGIN");
		requestMap.put("userName", userName);

		String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		String response = reader.readLine();

		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
		{
			System.out.println("Successfully logged in");
			logged = true;
		}
		else
		{
			System.out.println((String) responseMap.get("error"));
		}
	}


	private static void logoff()
	{
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "LOGOFF");

		String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		logged = false;
	}


	private static void list() throws IOException
	{
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "LIST");

		String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		String response = reader.readLine();

		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
		{
			@SuppressWarnings("unchecked")
			Collection<String> snippetsJSON = (Collection<String>) responseMap.get("mails");
			for (String snippetJSON : snippetsJSON)
				System.out.println(new Gson().fromJson(snippetJSON, MailSnippet.class));
		}
		else
		{
			System.out.println((String) responseMap.get("error"));
		}
	}


	private static void receive(Scanner scanner) throws IOException
	{
		System.out.print("Id: ");
		Integer id = scanner.nextInt();
		scanner.nextLine();

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("command", "RECEIVE");
		requestMap.put("id", id);

		String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		String response = reader.readLine();

		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
		{
			String mailJSON = (String)responseMap.get("mail");
			MailFromServer mail = (MailFromServer)new Gson().fromJson(mailJSON, MailFromServer.class);
			
			System.out.println(mail);
		}
		else
		{
			System.out.println((String) responseMap.get("error"));
		}
	}


	private static void send(Scanner scanner) throws IOException
	{
		System.out.print("To: ");
		String to = scanner.nextLine();

		System.out.print("Subject: ");
		String subject = scanner.nextLine();

		System.out.print("Body: ");
		String body = scanner.nextLine();

		MailToServer mail = new MailToServer(userName, to, subject, body);
		String mailJSON = new Gson().toJson(mail, MailToServer.class);
		
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("command", "SEND");
		requestMap.put("mail", mailJSON);

		String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		String response = reader.readLine();

		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
		{
			System.out.println("Successfully sent");
		}
		else
		{
			System.out.println((String) responseMap.get("error"));
		}
	}

	private static String userName;
	private static PrintWriter writer;
	private static BufferedReader reader;
	private static boolean logged;
}
