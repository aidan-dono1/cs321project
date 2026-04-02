/**********************************************************/
/*                                                        */
/* Author: Michael Arculeo                                */
/* Filename: Client.java                                  */
/* Purpose: Connects to server.java for CS321 final       */
/*          project, to be used as a part of a            */
/*          "Wordle" game/program.                        */
/*                                                        */
/**********************************************************/
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Client 
{
    public static void main(String[] args) throws IOException
    {
        if (args.length != 2)
        {
            System.err.println("Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);    

        try (Socket s = new Socket(hostName, portNumber);
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream();)
        {
            byte[] buf = new byte[1024];

            // Send HELLO messge
            out.write("Connected. Hello!\n".getBytes());
            out.flush();

            int n = in.read(buf);
            System.out.println("Connected to " + hostName + " ... (server says: " + new String(buf, 0, n) + ")");

            // Send BYE message
            out.write("Connection closed. Goodbye!\n".getBytes());
            out.flush();

            n = in.read(buf);
            System.out.println("Server says: " + new String(buf, 0, n));

            System.out.println("Connection to " + hostName + " is now closing...");
            s.close();
        }
        
        catch (UnknownHostException e) 
        {
            System.err.println("Don't know about host: " + hostName);
            System.exit(1);
        }
        catch (IOException e) 
        {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}