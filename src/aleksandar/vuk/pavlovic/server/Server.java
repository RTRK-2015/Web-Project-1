package aleksandar.vuk.pavlovic.server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Server
{
	public static void main(String[] args)
	{
		final int PORT = 9000;
		
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
