/****************************************************
 *
 * Author: Aidan Donohoe
 * Filename: server.java
 * Purpose: connect to client for CS321 project, to be used as a part of a
 * "Wordle" like program
 *
 *****************************************************/

import java.net.*;
import java.io.*;
import java.time.*;
import java.util.*;

public class server
{
    public static void main(String[] args) throws IOException
    {	
	if (args.length != 1)
	    {
		System.err.println("Usage: Java Server <port number>");
		System.exit(1);
	    } //end if

	int portNumber = Integer.parseInt(args[0]);
	ServerSocket serverSocket = new ServerSocket(portNumber);

	LocalDate currentDate = LocalDate.now();
	DayOfWeek dayOfWeek = currentDate.getDayOfWeek();
	int dayNumber = dayOfWeek.getValue();
	
	String[] words = {"Frankie(0)", "DeSales(1)", "Dooling(2)", "Shimkanon(3)", "Communication(4)", "HelloWorld(5)", "Student(6)"};
	String wordOfTheDay = (words[dayNumber]);
	
	System.out.println("Server live! Running on port: " + portNumber);
	System.out.println("Current Date : " + currentDate);
	System.out.println("TESTING - WOTD: " + wordOfTheDay);
	System.out.println("Ctrl-D + ENTER to shut down server");
	
	serverSocket.setSoTimeout(500);

	BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

	boolean running = true;

	try
	    {
		while(running)
		    {
			if (!console.ready())
			    {
				// keep waiting for input
			    }
			else
			    {
				String line = console.readLine(); //check to see what was input
				if (line == null) //EOF(control-D) entered
				    {
					System.out.println("Beginning server shutdown...");
					running = false;
					break;
				    }
			    }
			try // look for clients
			    {
				//spawn handler thread for client conn
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connection from: " + clientSocket.getInetAddress() +
						   ":" + clientSocket.getPort());
				new Handler(clientSocket).start();
			    } catch (SocketTimeoutException e)
			    {
				//accept() timed out
			    }
		    } // end while
	    } // end try
	finally
	    {
		serverSocket.close();
		System.out.println("Server shutdown complete. Goodbye.");
	    } //end finally
    } // end main
    /*********************************************************
     * A handler thread class to work with a single client.
     *********************************************************/
    private static class Handler extends Thread
    {
	private Socket socket;
	private PrintWriter out;
	private BufferedReader in;
	private String inputLine, outputLine;

	public Handler(Socket socket)
	{
	    this.socket = socket;
	}
    
	public void run()
	{
	    try
	    {
		out = new PrintWriter(socket.getOutputStream(), true);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

		inputLine = in.readLine();
		while (inputLine != null)
		{
			out.println(inputLine);
			inputLine = in.readLine();
		} //end while
	    } //end try
	catch (IOException e)
	{
	    System.out.println(e);
	}
	finally
	{
	    try
	    {
		System.out.println("Client disconnected from: " + socket.getInetAddress() + ":" + socket.getPort());
		socket.close();
       	    }
	    catch (IOException e)
	    {
	    }//end catch
        } //end finally
	} // end run
    } //end handler
} //end server
