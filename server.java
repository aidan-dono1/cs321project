/****************************************************
 *
 * Author: Aidan Donohoe
 * Filename: server.java
 * Purpose: connect to client for CS321 project, to be used as a part of a
 * "Wordle" like program
 *  Version: 2.0
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
        }

    int portNumber = Integer.parseInt(args[0]);
    ServerSocket serverSocket = new ServerSocket(portNumber);

    LocalDate currentDate = LocalDate.now();
        
    System.out.println("Server live! Running on port: " + portNumber);

    try
    {
        while(true)
        {
        try
        {
            Socket clientSocket = serverSocket.accept();
            System.out.println("Client connection from: " +
            clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            new Handler(clientSocket).start();
        }
        catch (SocketTimeoutException e)
        {
            // accept() timed out
        }
        }
    }
    finally
    {
        serverSocket.close();
    }
    }

    /*********************************************************
     * A handler thread class to work with a single client.
     *********************************************************/
    private static class Handler extends Thread
    {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public Handler(Socket socket)
    {
        this.socket = socket;
    }
    
    @Override
    public void run()
    {
        try
        {
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        String input;
        String wordOfTheDay = "APPLE"; // Placeholder for the actual WOTD logic
            
        while ((input = in.readLine()) != null)
        {
            if ("CONNECT".equals(input))
            {
            out.println("OK CONNECTED");
            }
            else if ("REQUEST WOTD".equals(input))
            {
            out.println(wordOfTheDay);
            }
            else if (input.startsWith("GUESS "))
            {
            String guess = input.substring(6).trim().toUpperCase();
            out.println("PATTERN " + checkGuess(guess, wordOfTheDay));
            }
            else if (input.startsWith("RESULT"))
            {
            out.println("ACK RESULT");
            }
            else if (input.equals("DISCONNECT"))
            {
            out.println("OK BYE");
            break;
            }
            else
            {
            out.println("ERROR UNKNOWN COMMAND");
            }
        }
        }
        catch (IOException e)
        {
        System.out.println(e);
        }
        finally
        {
        try
        {
            System.out.println("Client disconnected from: " +
            socket.getInetAddress() + ":" + socket.getPort());
            socket.close();
        }
        catch (IOException e)
        {
        }
        }
    }

    private String checkGuess(String guess, String word) //Checking guess inside of the handler thread, since it is only relevant to that client
    {
        char[] result = new char[5];

        for (int i = 0; i < 5; i++)
        {
        if (guess.charAt(i) == word.charAt(i))
        {
            result[i] = 'G';
        }
        else if (word.indexOf(guess.charAt(i)) >= 0)
        {
            result[i] = 'Y';
        }
        else
        {
            result[i] = 'B';
        }
        }

        return new String(result);
    }
    }
}
