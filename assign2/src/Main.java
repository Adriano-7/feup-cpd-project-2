import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select an option:");
        System.out.println("1. Run Server");
        System.out.println("2. Run Client");
        System.out.print("Enter your choice (1/2): ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        if (choice == 1) {
            System.out.print("Enter port number: ");
            int port = scanner.nextInt();
            server.TimeServer.main(new String[]{String.valueOf(port)});
        }
        else if (choice == 2) {
            System.out.print("Enter server hostname: ");
            String host = scanner.nextLine();
            System.out.print("Enter port number: ");
            int port = scanner.nextInt();
            client.TimeClient.main(new String[]{host, String.valueOf(port)});
        } else {
            System.out.println("Invalid choice. Exiting.");
        }
    }
}