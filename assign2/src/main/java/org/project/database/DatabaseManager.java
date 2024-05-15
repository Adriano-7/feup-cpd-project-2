package org.project.database;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.*;

public class DatabaseManager {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static final Lock readLock = lock.readLock();
    private static final Lock writeLock = lock.writeLock();

    private static final String DATABASE_FILE = "src/main/java/org/project/database/database.csv";
    public void register(String username, String password) throws IOException {
        writeLock.lock();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE), StandardOpenOption.APPEND)) {
            writer.write(username + "," + 0 + "," + password + "\n");
        } finally {
            writeLock.unlock();
        }
    }

    public void addUsername(String username) {
        writeLock.lock();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(DATABASE_FILE), StandardOpenOption.APPEND)) {
            writer.write(username + "\n");
        }
        catch (FileNotFoundException e) {
            System.out.println("The database file was not found: " + e.getMessage());
            e.printStackTrace();
        }
        catch (IOException e) {
            System.out.println("There was an issue writing to the database file: " + e.getMessage());
            e.printStackTrace();
        }
        catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            writeLock.unlock();
        }
    }

    public boolean usernameExists(String username) {
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
        } finally {
            readLock.unlock();
        }
        return false;
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