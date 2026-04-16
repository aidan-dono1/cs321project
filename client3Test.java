//TEST VERSION - FOR TEMP USE

import java.net.*;
import java.io.*;

public class client3Test {

    public static void main(String[] args) {

        if (args.length != 2) {
            System.err.println("Usage: java client3Test <host> <port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader user = new BufferedReader(new InputStreamReader(System.in))) {

            // CONNECT
            out.println("CONNECT");
            System.out.println("Server: " + in.readLine());

            // REQUEST WOTD
            out.println("REQUEST WOTD");
            String wotd = in.readLine().trim().toUpperCase();
            System.out.println("Word of the Day received.");

            boolean win = false;

            for (int attempt = 1; attempt <= 6; attempt++) {
                System.out.print("Guess #" + attempt + ": ");
                String guess = user.readLine().trim().toUpperCase();

                if (guess.length() != 5) {
                    System.out.println("Guess must be 5 letters.");
                    attempt--;
                    continue;
                }

                out.println("GUESS " + guess);
                String response = in.readLine();

                if (response.startsWith("PATTERN ")) {
                    String pattern = response.substring(8);
                    System.out.println("Result: " + pattern);

                    if (guess.equals(wotd)) {
                        win = true;
                        break;
                    }
                }
            }

            // SEND RESULT
            out.println("RESULT " + (win ? "WIN" : "LOSE"));
            System.out.println("Server: " + in.readLine());

            // DISCONNECT
            out.println("DISCONNECT");
            System.out.println("Server: " + in.readLine());

        } catch (IOException e) {
            System.out.println("Client error: " + e);
        }
    }
}
