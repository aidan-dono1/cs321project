/**********************************************
 *
 *Authot: Aidan Donohoe
 *Filename: clientTemp.java
 *Purpose: to use as a temporary testing client for development
 *of a server for the cleint server project
 *
 **********************************************/




import java.io.*;
import java.net.*;

public class clientTemp {
    public static void main(String[] args) throws IOException {

        if (args.length != 2) {
            System.err.println("Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }   // end if usage clause

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
             Socket sock = new Socket(hostName, portNumber);
             PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
             )  // end try

        {
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            String serverInput;
            System.out.println("Connected! Hello!");
            String inLine = stdIn.readLine();


            while (inLine != null) {
                out.println(inLine);
                serverInput = in.readLine();
                System.out.println("You said: " + serverInput);

                System.out.println("Testing");
                inLine = stdIn.readLine();
            }  // end while
	    
	    System.out.println("Connection closed. Goodbye!");
	    
            } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);

        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                               hostName);
            System.exit(1);
        }  // end catch

    }  // end function main
}   // end class clientTemp
