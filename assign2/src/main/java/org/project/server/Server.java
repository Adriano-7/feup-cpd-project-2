package org.project.server;

import org.project.database.DatabaseManager;

import java.io.*;
import java.net.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;

public class Server {
    public SSLServerSocket serverSocket;
    public DatabaseManager databaseManager;
    public Server(int portNumber) {
        char[] passphrase = "changeit".toCharArray();//keystore password

        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("src/main/java/org/project/certificate/keystore.jks"), passphrase);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, passphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
            this.serverSocket = (SSLServerSocket) ssf.createServerSocket(portNumber);
            this.databaseManager = new DatabaseManager();
        }
        catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
        catch (IOException | KeyStoreException | CertificateException | NoSuchAlgorithmException | KeyManagementException | UnrecoverableKeyException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
    public void start() throws IOException {
        MatchmakingPool matchmakingPool = new MatchmakingPool();
        Thread.ofVirtual().start(matchmakingPool);

        while(!this.serverSocket.isClosed()){
            SSLSocket clientSocket = (SSLSocket) this.serverSocket.accept();
            ClientSession clientSession = new ClientSession(clientSocket, matchmakingPool, this);
            Thread clientThread = new Thread (clientSession);
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