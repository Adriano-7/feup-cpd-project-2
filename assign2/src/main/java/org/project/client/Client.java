package org.project.client;

import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Scanner;

public class Client {
    private SSLSocket echoSocket;
    BufferedWriter writer;
    BufferedReader reader;

    public Client(String hostName, int portNumber) throws IOException {
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

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            echoSocket = (SSLSocket) sslSocketFactory.createSocket(hostName, portNumber);

            this.writer = new BufferedWriter(new OutputStreamWriter(echoSocket.getOutputStream()));
            this.reader = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
        } catch(NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException e){
            e.printStackTrace();
        }
    }

    public void start() throws IOException {
        // Create a virtual thread for output
        Thread outputThread = Thread.ofVirtual().start(() -> outputLoop());

        // Use the main thread for input
        inputLoop();
    }

    private void inputLoop() throws IOException {
        String userInput;
        while (this.echoSocket.isConnected()) {
            try{
                userInput = reader.readLine();
                if (userInput != null) {
                    System.out.println(userInput);
                }
            } catch (IOException e) {
                System.err.println("Error reading server output");
            }
        }
    }

    private void outputLoop() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (this.echoSocket.isConnected()) {
                String message = scanner.nextLine();
                if (message.equals("bye")) {
                    this.echoSocket.close();
                    break;
                }
                writer.write(message + "\n");
                writer.flush();
            }

        } catch (IOException e) {
            System.err.println("Error reading server output");
        }
    }
}