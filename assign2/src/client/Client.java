package client;

import java.net.*;
import java.io.*;

public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        String hostName = "localhost";
        int portNumber = 8080;
        Socket echoSocket = new Socket(hostName, portNumber);
        PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

        try {
            // Create a virtual thread for output
            Thread outputThread = Thread.ofVirtual().start(() -> outputLoop(in));

            // Use the main thread for input
            inputLoop(new BufferedReader(new InputStreamReader(System.in)), out);
        } finally {
            echoSocket.close();
        }
    }

    private static void inputLoop(BufferedReader stdIn, PrintWriter out) throws IOException {
        String userInput;
        while ((userInput = stdIn.readLine()) != null) {
            out.println(userInput);
            if (userInput.equals("bye")) break;
        }
    }

    private static void outputLoop(BufferedReader in) {
        try {
            String serverOutput;
            while ((serverOutput = in.readLine()) != null) {
                System.out.println(serverOutput);
            }
        } catch (IOException e) {
            System.err.println("Error reading server output");
        }
    }
}