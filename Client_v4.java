import java.net.Socket;
import java.net.UnknownHostException;
import java.io.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Client_v4
{
    public static void main(String[] args)
    {
        if (args.length != 2)
        {
            System.err.println("Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (Socket s = new Socket(hostName, portNumber);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in)))
        {
            String line;

            System.out.println("Connecting to server...");
            out.println("CONNECT");
            System.out.println("Server: " + in.readLine());

            out.println("REQUEST WOTD");
            String wordOfTheDay = in.readLine().trim().toUpperCase();
            System.out.println("Word of the Day received.");

            int maxGuesses = 6;
            boolean winCondition = false;

            for (int i = 1; i <= maxGuesses; i++)
            {
                System.out.print("Guess #" + i + ": ");
                String guess = userIn.readLine().trim().toUpperCase();

                if (guess.length() != 5)
                {
                    System.out.println("ERROR: Must be 5 letters.");
                    i--;
                    continue;
                }

                String result = checkGuessPython(guess, wordOfTheDay);
                System.out.println("Result: " + result);

                if (guess.equals(wordOfTheDay))
                {
                    winCondition = true;
                    break;
                }
            }

            String outcome = winCondition ? "WIN" : "LOSE";
            out.println("RESULT " + outcome);
            System.out.println("Server: " + in.readLine());

            out.println("DISCONNECT");
            System.out.println("Server: " + in.readLine());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static String checkGuessPython(String guess, String word)
    {
        try
        {
            ProcessBuilder pb = new ProcessBuilder(
                "python3",
                "wordle_lib3.py",
                "GUESS",
                guess,
                word
            );

            Process p = pb.start();

            BufferedReader reader = new BufferedReader(
                new InputStreamReader(p.getInputStream())
            );

            return reader.readLine().trim();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "BBBBB";
        }
    }
}