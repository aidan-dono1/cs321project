/**********************************************************/
/*                                                        */
/* Author: Michael Arculeo                                */
/* Filename: Client_v3.java                               */
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
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client_v3 
{
    public static void main(String[] args)
    {
        // Ensures the correct command line
        if (args.length != 2)
        {
            System.err.println("Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        // Try Block 
        // automatically closes socket and input/output streams
        try (Socket s = new Socket(hostName, portNumber);
             InputStream in = s.getInputStream();
             OutputStream out = s.getOutputStream();
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in)))
        {
            byte[] buf = new byte[1024];  // Buffer for the server responses
            int n; // Universal for all

            // Establishing a Connection
            System.out.println("Connecting to server...");
            out.write("CONNECT\n");
            out.flush();

            n = in.read(buf);
            System.out.println("Server: " + new String(buf, 0, n));

            // Request for the Word of the Day
            out.write("REQUEST WOTD\n");
            out.flush();

            n = in.read(buf);
            String wordOfTheDay = new String(buf, 0, n).trim().toUpperCase();
            System.out.println("Word of the Day received.");

            // Wordle Guessing Loop
            int maxGuesses = 6;
            boolean winCondition = false;

            for (int numOfAttempts = 1; numOfAttempts <= maxGuesses; numOfAttempts++)
            {
                System.out.print("Guess #" + numOfAttempts + ": ");
                String userGuess = userIn.readLine().trim().toUpperCase();

                // Validate the guess length
                if (userGuess.length() != 5)
                {
                    System.out.println("ERROR. Guess for WOTD must be exactly 5 letters long.");
                    numOfAttempts--; // Do not count the invalid guess, negate
                    continue;
                }

                // Compare guess to WOTD and print result pattern
                String summary = checkGuess(userGuess, wordOfTheDay);
                System.out.println("Result: " + summary);

                // Check win condition
                if (userGuess.equals(wordOfTheDay))
                {
                    winCondition = true;
                    break;
                }
            }

            // Send the Final Result to Server
            String playerOutcome = winCondition ? "WIN" : "LOSE";
            String resultMessage;

            switch (playerOutcome) 
            {
                case "WIN":
                    System.out.println("Result: WIN!\n");
                    resultMessage = "RESULT WIN\n";
                    break;

                case "LOSE":
                    System.out.println("Result: LOSE :(\n");
                    resultMessage = "RESULT LOSE\n";
                    break;

                default:
                    System.out.println("Result: UNKNOWN?\n");
                    resultMessage = "RESULT UNKNOWN\n";
                    break;
            }

            out.write(resultMessage);
            out.flush();

            // Received the acknowledgement from the server
            n = in.read(buf);
            System.out.println("Server: " + new String(buf, 0, n));

            // Disconnection From Server
            System.out.println("Disconnecting from server...");
            out.write("DISCONNECT\n");
            out.flush();

            n = in.read(buf);
            System.out.println("Server: " + new String(buf, 0, n));

            System.out.println("Connection to " + hostName + " is now closing...");
        }

        // Handle possible connection errors
        catch (UnknownHostException e)
        {
            System.err.println("Don't know host: " + hostName);
        }
        catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to " + hostName);
        }
    }

    /******************************************************************
     * Function: checkGuess
     * Paramters: String guess
     *            String word
     * Purpose:
     * Compares a user's guess to whatever the Word of the Day is.
     * Also, returns a 5 character string with the... 
     *      KEY:
     *       G = correct letter in the correct position.
     *       Y = correct letter in the wrong position.
     *       B = letter not in the word.
     ******************************************************************/
    private static String checkGuess(String guess, String word)
    {
        char[] WOTDresult = new char[5];

        for (int i = 0; i < 5; i++)
        {
            if (guess.charAt(i) == word.charAt(i))
            {
                WOTDresult[i] = 'G';
            }
            else if (word.indexOf(guess.charAt(i)) >= 0)
            {
                WOTDresult[i] = 'Y';
            }
            else
            {
                WOTDresult[i] = 'B';
            }
        }

        return new String(WOTDresult);
    }
}