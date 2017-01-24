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


	/**
	 * Prints the menu on the screen.
	 */
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


	/**
	 * Gets the user name from the scanner and tries to register it on the server.
	 * @param scanner Scanner to use for getting the user name.
	 * @throws IOException if scanner input throws.
	 */
	private static void register(Scanner scanner) throws IOException
	{
		System.out.print("Name: ");
		userName = scanner.nextLine();

		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "REGISTER");
		requestMap.put("userName", userName);

		final String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		final String response = reader.readLine();

		@SuppressWarnings("unchecked")
		final Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
			System.out.println("Successfully registered");
		else
			System.out.println((String) responseMap.get("error"));
	}


	/**
	 * Gets the user name from the scanner and tries to login with it on the server.
	 * @param scanner Scanner to use for getting the user name.
	 * @throws IOException if scanner input throws.
	 */
	private static void login(Scanner scanner) throws IOException
	{
		System.out.print("Name: ");
		userName = scanner.nextLine();

		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "LOGIN");
		requestMap.put("userName", userName);

		final String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		final String response = reader.readLine();

		@SuppressWarnings("unchecked")
		final Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

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


	/**
	 * Sends the log off command to the server.
	 */
	private static void logoff()
	{
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "LOGOFF");

		final String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		logged = false;
	}


	/**
	 * Gets the list of mail snippets for the current user from the server.
	 * @throws IOException if reading server response throws.
	 */
	private static void list() throws IOException
	{
		Map<String, String> requestMap = new HashMap<>();
		requestMap.put("command", "LIST");

		final String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		final String response = reader.readLine();

		@SuppressWarnings("unchecked")
		final Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
		{
			@SuppressWarnings("unchecked")
			final Collection<String> snippetsJSON = (Collection<String>) responseMap.get("mails");
			for (String snippetJSON : snippetsJSON)
				System.out.println(new Gson().fromJson(snippetJSON, MailSnippet.class));
		}
		else
		{
			System.out.println((String) responseMap.get("error"));
		}
	}


	/**
	 * Gets the mail with the user-supplied id from the server.
	 * @param scanner Scanner to use for getting the mail id.
	 * @throws IOException if reading server response throws.
	 */
	private static void receive(Scanner scanner) throws IOException
	{
		System.out.print("Id: ");
		final Integer id = scanner.nextInt();
		scanner.nextLine();

		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("command", "RECEIVE");
		requestMap.put("id", id);

		final String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		final String response = reader.readLine();

		@SuppressWarnings("unchecked")
		final Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

		if ((boolean) responseMap.get("success"))
		{
			final String mailJSON = (String)responseMap.get("mail");
			final MailFromServer mail = (MailFromServer)new Gson().fromJson(mailJSON, MailFromServer.class);
			
			System.out.println(mail);
		}
		else
		{
			System.out.println((String) responseMap.get("error"));
		}
	}


	/**
	 * Reads the user-supplied mail information and requests that server send it.
	 * @param scanner Scanner to use for getting the mail data.
	 * @throws IOException if reading server response throws.
	 */
	private static void send(Scanner scanner) throws IOException
	{
		System.out.print("To: ");
		final String to = scanner.nextLine();

		System.out.print("Subject: ");
		final String subject = scanner.nextLine();

		System.out.print("Body: ");
		final String body = scanner.nextLine();

		MailToServer mail = new MailToServer(userName, to, subject, body);
		final String mailJSON = new Gson().toJson(mail, MailToServer.class);
		
		Map<String, Object> requestMap = new HashMap<>();
		requestMap.put("command", "SEND");
		requestMap.put("mail", mailJSON);

		final String request = new Gson().toJson(requestMap, Map.class);
		writer.println(request);
		writer.flush();

		while (!reader.ready())
			;
		final String response = reader.readLine();

		@SuppressWarnings("unchecked")
		final Map<String, Object> responseMap = new Gson().fromJson(response, Map.class);

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
