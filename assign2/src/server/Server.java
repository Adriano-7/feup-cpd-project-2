package server;

import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    public Server(int portNumber) {
        try {
            serverSocket = new ServerSocket(portNumber);
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server(8080);
        while(!server.serverSocket.isClosed()){
            Socket clientSocket = server.serverSocket.accept();
            ClientSession clientSession = new ClientSession(clientSocket);
            Thread.ofVirtual().start(clientSession);
        }
    }

    public void shutdown() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.out.println("Exception caught when trying to close the server socket");
            System.out.println(e.getMessage());
        }
    }
}