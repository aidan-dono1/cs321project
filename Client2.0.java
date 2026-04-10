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

            // WORDLE guessing loop
            int wordleMaxGuess = 6;
            boolean winCondition = false;

            for (int wordleAttempt = 1; wordleAttempt <= wordleMaxGuess; wordleAttempt++)
            {
                System.out.print("Guess #" + wordleAttempt + ": ");
                String userGuess = userIn.readline().trim().toUpperCase();

                if (userGuess.length() != 5)
                {
                    System.out.println("ERROR. Guess must be at least 5 letters.");
                    wordleAttempt--;
                    continue;
                }

                // Compare guess to the Word Of The Day
                String feedback = chechGuess(guess, wordOfTheDay);
                System.out.println("Result:" + feedback);

                if (userGuess.equals(wordOfTheDay))
                {
                    winCondition = true;
                    break;
                }
            }

            // Send Results of User Guess to Server
            String resultSum = "RESULT" + (winCondition ? "WIN" : "LOSE") + "\n";
            out.write(resultMsg.getBytes);
            out.flush();

            // Recieve Server Ack
            n = inread(buf);
            System.out.println("Server:" + new String(buf, 0, n));

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

    // Wordle Comparsion
    private static String checkGuess(String guess, String word)
    {
        char[] result = new char[5];

        for (int i = 0; i < 5; i++)
        {
            if (guess.charAt(i) == word.charAt(i))
            {
                result[i] = "G"; // "G" = Letter is in WOTD and in correct letter postion
            }
            else if (word.indexof(guess.charAt(i)) >= 0)
            {
                result[i] = "Y"; // "Y" = Letter is in WOTD, but wrong postion
            }
            else
            {
                result[i] = "B"; // "B" = Leter is not in the WOTD
            }
        }

        return new String(result);
    }
}