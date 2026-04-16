package com.library.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataFileHandler {
    private static final String DATA_DIR = "data/";

    public static <T> void saveToFile(String fileName, List<T> data) {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) dir.mkdirs();

        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_DIR + fileName))) {
            oos.writeObject(data);
        } catch (IOException e) {
            System.err.println("Error saving data to " + fileName + ": " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> List<T> loadFromFile(String fileName) {
        File file = new File(DATA_DIR + fileName);
        if (!file.exists()) return new ArrayList<>();

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading data from " + fileName + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
