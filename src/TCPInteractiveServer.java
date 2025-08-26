import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.*;

public class TCPInteractiveServer {
	private static final Logger LOGGER = Logger.getLogger(TCPInteractiveServer.class.getName());

	public static void main(String[] args) {
		ServerSocket listeningSocket = null;
		Socket clientSocket = null;
		
		// Read server configuration from properties file
		// need to change to use args instead of properties file
		Properties prop = new Properties();
		String propFileName = "res/TCPServer.properties";
		try (FileInputStream fis = new FileInputStream(propFileName)) {
			prop.load(fis);
		} catch (IOException ex) {
			System.out.println(ex.getMessage());
		}

		// Set up logging
		try {
			FileHandler fh = new FileHandler(prop.getProperty("server.log.file"), true);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}

		try {
			//Create a server socket listening on port defined in server config
			listeningSocket = new ServerSocket(Integer.parseInt(prop.getProperty("server.port")));
			int i = 0; //counter to keep track of the number of clients

			//Listen for incoming connections for ever
			while (true) 
			{
				LOGGER.info("Server listening on port " + prop.getProperty("server.port") + " for a connection");
				//Accept an incoming client connection request 
				clientSocket = listeningSocket.accept(); //This method will block until a connection request is received
				i++;
				LOGGER.info("Accepted connection " + i + " from " + clientSocket.getInetAddress().getHostAddress());
				//System.out.println("Client conection number " + i + " accepted:");
				//System.out.println("Remote Port: " + clientSocket.getPort());
				System.out.println("Remote Hostname: " + clientSocket.getInetAddress().getHostName());
				System.out.println("Local Port: " + clientSocket.getLocalPort());
				
				//Get the input/output streams for reading/writing data from/to the socket
				BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

				//Read the message from the client and reply
				//Notice that no other connection can be accepted and processed until the last line of 
				//code of this loop is executed, incoming connections have to wait until the current
				//one is processed unless...we use threads!
				String clientMsg = null;
				try 
				{
					while((clientMsg = in.readLine()) != null) 
					{
						System.out.println("Message from client " + i + ": " + clientMsg);
						out.write("Server Ack " + clientMsg + "\n");
						out.flush();
						System.out.println("Response sent");
					}
					System.out.println("Server closed the client connection!!!!! - received null");
				}
				
				catch(SocketException e)
				{
					System.out.println("closed...");
				}
				// close the client connection
				clientSocket.close();
			}
		} 
		catch (SocketException ex)
		{
			ex.printStackTrace();
		}
		catch (IOException e)
		{
			System.out.println("here");
			e.printStackTrace();
		} 
		finally
		{
			if(listeningSocket != null)
			{
				try
				{
					// close the server socket
					listeningSocket.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
