package com.smn.restapitool.persistence;

import com.smn.restapitool.model.User;
import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import com.fasterxml.jackson.core.type.TypeReference;

@Component
public class UserRepository {

    private static final String JAVA_WORK_DIR = "JAVA_WORK_DIR"; // Environment variable for Java working directory
    private static final String filePath = "users.json"; // Path to the persisted UserRepository JSON file

    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            String javaWorkDir = System.getenv(JAVA_WORK_DIR);
            if (javaWorkDir == null) {
                System.out.println("Environment variable " + JAVA_WORK_DIR + " is not set.");
            } else {
                System.out.println("Java working directory: " + javaWorkDir);

                String jsonFilePath = new File(javaWorkDir, filePath).getAbsolutePath();
                File userFile = new File(jsonFilePath);

                if (userFile.exists()) {
                    this.loadFromJsonFile();

                } else {}
                    User admin = new User();
                    admin.setNameFirst("Steve");
                    admin.setNameLast("Nies");
                    admin.setCompany("Self");
                    admin.setEmail("steveniesfl@gmail.com");
                    admin.setAccessToken("smn01311959");

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, 100);
                    Date futureDate = cal.getTime();
                    admin.setAccessExpiration(futureDate);

                    this.addUser(admin);
                    this.saveToJsonFile();
            }
            System.out.println("UserRepository initialized!");

        } catch (Throwable t) {
            System.err.println("Error initializing UserRepository: " + t.getMessage());
            t.printStackTrace();
        }
    }
    
    public int numUsers() {
        return userMap.size();
    }

    public User addUser(User user) throws IllegalArgumentException {
        if (user == null || user.getAccessToken() == null) {
            throw new IllegalArgumentException("User and access token must not be null");
        }
        userMap.put(user.getAccessToken(), user);
        return user;
    }

    public void removeUser(User user) throws IllegalArgumentException {
        if (user == null || user.getAccessToken() == null) {
            throw new IllegalArgumentException("User and access token must not be null");
        }
        userMap.remove(user.getEmail());
    }

    public void removeAllUsers() {
        userMap.clear();
    }

    public List<User> findAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public boolean userExists(String accessToken) {
        return userMap.containsKey(accessToken);
    }

    public User findUserByAccessToken(String accessToken) {
        return userMap.get(accessToken);
    }

    public File getUserStorageDir(User user) {

        // Synthesize a directory for this user.
        String dirName = user.getCompany() + "-" + user.getNameFirst() + "-" + user.getNameLast();
        dirName = dirName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        dirName = dirName.replaceAll("_+", "_");
        File userDir = new File(System.getenv(JAVA_WORK_DIR), dirName);

        // Create the user's storage directory if it doesn't exist.
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        return userDir;
    }

    private void loadFromJsonFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<User> users;

        String javaWorkDir = System.getenv(JAVA_WORK_DIR);
        String jsonFilePath = new File(javaWorkDir, filePath).getAbsolutePath();
        try (FileReader reader = new FileReader(jsonFilePath)) {
            users = mapper.readValue(reader, new TypeReference<List<User>>() {
            });
        }
        for (User user : users) {
            this.addUser(user);
        }
    }

    public void saveToJsonFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        List<User> users = this.findAllUsers();
        users.sort((u1, u2) -> u1.getEmail().compareTo(u2.getEmail()));

        String javaWorkDir = System.getenv(JAVA_WORK_DIR);
        String jsonFilePath = new File(javaWorkDir, filePath).getAbsolutePath();
        try (FileWriter writer = new FileWriter(jsonFilePath)) {
            mapper.writeValue(writer, users);
        }
    }

}