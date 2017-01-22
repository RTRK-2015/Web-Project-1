package aleksandar.vuk.pavlovic.mailserver;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server
{
	public static final int PORT = 9000;


	public static void main(String[] args)
	{
		try (ServerSocket ss = new ServerSocket(PORT))
		{
			while (true)
			{
				Socket sock = ss.accept();
				new ServerThread(sock);
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
