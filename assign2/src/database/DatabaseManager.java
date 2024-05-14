package database;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class DatabaseManager {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private static final String DATABASE_FILE = "C:\\Users\\adria\\Desktop\\Projetos\\CPD-2\\assign2\\src\\database\\database.csv";
    public void register(String username, String password) throws IOException {
        writeLock.lock();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE), StandardOpenOption.APPEND)) {
            writer.write(username + "," + 0 + "," + password + "\n");
        } finally {
            writeLock.unlock();
        }
    }

    public boolean verifyPassword(String username, String password) {

        readLock.lock();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username) && parts[2].equals(password)) {
                    return true;
                }
            }
            return false;
        } catch (FileNotFoundException e) {
            System.out.println("The database file was not found: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("There was an issue reading the database file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            readLock.unlock();
        }
        return false;
    }
    public int getUserPoints(String username) throws IOException {
        readLock.lock();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
                    return Integer.parseInt(parts[1]);
                }
            }
            return -1;
        } finally {
            readLock.unlock();
        }
    }
}