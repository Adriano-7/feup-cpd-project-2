package server;

import java.io.*;
import java.net.Socket;

public class ClientSession implements Runnable {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;

    public ClientSession(Socket clientSocket) {
        this.clientSocket = clientSocket;
        try {
            this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Exception creating reader and writer: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                System.out.println(inputLine);
                writer.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception handling client request: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Exception closing client socket: " + e.getMessage());
            }
        }
    }
}