package org.project.database;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;
import org.mindrot.jbcrypt.BCrypt;
import org.project.server.User;

import java.time.LocalDateTime;

public class DatabaseManager {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private static final String DATABASE_FILE = "src/main/java/org/project/database/database.csv";
    public User register(String username, String password) throws IOException {
        if (verifyUsername(username)) {
            return null;
        }

        String salt = BCrypt.gensalt();
        String encryptedPassword = BCrypt.hashpw(password, salt);
        String token = UUID.randomUUID().toString();
        LocalDateTime localDateTime = LocalDateTime.now();

        writeLock.lock();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE), StandardOpenOption.APPEND)) {
            writer.write(username + "," + 0 + "," + encryptedPassword + "," + salt+ "," + token + "," + localDateTime + "\n");
        } finally {
            writeLock.unlock();
        }
        return new User(username, 0, token);
    }

    public boolean verifyUsername(String username) {
        readLock.lock();
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(username)) {
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
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            readLock.unlock();
        }
        return false;
    }

public User verifyPassword(String username, String password) {
    readLock.lock();
    List<String> fileContent = new ArrayList<>();
    User user = null;
    try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATABASE_FILE))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts[0].equals(username)) {
                if(BCrypt.checkpw(password, parts[2])){
                    parts[4] = UUID.randomUUID().toString();
                    parts[5] = LocalDateTime.now().toString();
                    user = new User(username, Integer.parseInt(parts[1]), parts[4]);
                    line = String.join(",", parts);
                }
            }
            fileContent.add(line);
        }
    } catch (FileNotFoundException e) {
        System.out.println("The database file was not found: " + e.getMessage());
        e.printStackTrace();
    } catch (IOException e) {
        System.out.println("There was an issue reading the database file: " + e.getMessage());
        e.printStackTrace();
    } finally {
        readLock.unlock();
    }

    if (user != null) {
        writeLock.lock();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE))) {
            for (String fileLine : fileContent) {
                writer.write(fileLine);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("There was an issue writing to the database file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
    return user;
}
    public void updateLocalDateTime(String username) {
        writeLock.lock();
        try {
            List<String> fileContent = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATABASE_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(username)) {
                        parts[5] = LocalDateTime.now().toString();
                        line = String.join(",", parts);
                    }
                    fileContent.add(line);
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE))) {
                for (String fileLine : fileContent) {
                    writer.write(fileLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("There was an issue updating the localDateTime: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
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

    public int getRank(String username) throws IOException {
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

    public void updateClient(String username, int rank, LocalDateTime lastOnline) {
        writeLock.lock();
        try {
            List<String> fileContent = new ArrayList<>();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(DATABASE_FILE))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts[0].equals(username)) {
                        parts[1] = Integer.toString(rank);
                        parts[5] = lastOnline.toString();
                        line = String.join(",", parts);
                    }
                    fileContent.add(line);
                }
            }
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE))) {
                for (String fileLine : fileContent) {
                    writer.write(fileLine);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("There was an issue updating the client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }
    }
}