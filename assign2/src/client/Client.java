package client;

import java.net.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.*;
import java.security.cert.CertificateException;

public class Client {
    public String username;

    public static void main(String[] args) throws IOException, InterruptedException{
        String hostName = "localhost";
        int portNumber = 8080;
        char[] passphrase = "changeit".toCharArray();//keystore password
        SSLSocket echoSocket = null;

        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream("certificate/keystore.jks"), passphrase);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, passphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            echoSocket = (SSLSocket) sslSocketFactory.createSocket(hostName, portNumber);

            PrintWriter out = new PrintWriter(echoSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

            // Create a virtual thread for output
            Thread outputThread = Thread.ofVirtual().start(() -> outputLoop(in));
            
            // Use the main thread for input
            inputLoop(new BufferedReader(new InputStreamReader(System.in)), out); 
            
        } catch(NoSuchAlgorithmException | KeyManagementException | KeyStoreException | CertificateException | UnrecoverableKeyException e){ 
            e.printStackTrace();
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
            while (true) {
                serverOutput = in.readLine();
                System.out.println(serverOutput);
            }
        } catch (IOException e) {
            System.err.println("Error reading server output");
        }
    }
}