import java.net.*;
import java.io.*;
import java.time.*;

public class server
{
    public static String wordOfTheDay;

    public static void main(String[] args) throws IOException
    {
        if (args.length != 1)
        {
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);

        wordOfTheDay = getWOTD();

        System.out.println("Server live on port " + port);
        System.out.println("WOTD: " + wordOfTheDay);

        while (true)
        {
            Socket client = serverSocket.accept();
            new Handler(client).start();
        }
    }

    private static class Handler extends Thread
    {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;

        public Handler(Socket socket)
        {
            this.socket = socket;
        }

        public void run()
        {
            try
            {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                String input;

                while ((input = in.readLine()) != null)
                {
                    if (input.equals("CONNECT"))
                    {
                        out.println("OK CONNECTED");
                    }
                    else if (input.equals("REQUEST WOTD"))
                    {
                        out.println(wordOfTheDay);
                    }
                    else if (input.startsWith("GUESS "))
                    {
                        String guess = input.substring(6).trim().toUpperCase();
                        out.println("PATTERN " + checkGuessPython(guess, wordOfTheDay));
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

                socket.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    private static String getWOTD()
    {
        try
        {
            ProcessBuilder pb = new ProcessBuilder(
                "python3",
                "wordle_lib3.py",
                "WOTD"
            );

            Process p = pb.start();

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));

            return reader.readLine().trim().toUpperCase();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "APPLE";
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

            BufferedReader reader =
                new BufferedReader(new InputStreamReader(p.getInputStream()));

            return reader.readLine().trim();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "BBBBB";
        }
    }
}