import client.Client;
import server.Server;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select an option:");
        System.out.println("1. Run Server");
        System.out.println("2. Run Client");
        System.out.print("Enter your choice (1/2): ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            Server.main(args);
        }
        else if (choice == 2) {
            Client.main(args);
        } else {
            System.out.println("Invalid choice. Exiting.");
        }
    }
}