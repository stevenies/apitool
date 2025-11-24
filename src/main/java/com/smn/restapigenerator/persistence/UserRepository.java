package com.smn.restapigenerator.persistence;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.smn.restapigenerator.exception.ExceptionUserDoesntExist;
import com.smn.restapigenerator.model.User;
import com.smn.restapigenerator.model.User.EmailStatus;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class UserRepository {

    private static final String JAVA_USER_DIR = "JAVA_USER_DIR"; // Environment variable for Java working directory
    private static final String TOOL_DIR = "RestApiGenerator"; // Environment variable for the working directory used by the REST API Generator tool
    private static final String USERS_FILENAME = "users.json"; // Path to the persisted UserRepository JSON file

    private final Map<String, User> userMap = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        try {
            String javaUserDir = System.getenv(JAVA_USER_DIR);
            if (javaUserDir == null) {
                System.out.println("Environment variable " + JAVA_USER_DIR + " is not set.");
            } else {
                System.out.println("Java user directory: " + javaUserDir);

                File restApiGenDir = new File(javaUserDir, TOOL_DIR);
                File usersFile = new File(restApiGenDir, USERS_FILENAME);

                if (usersFile.exists()) {
                    this.loadFromJsonFile();

                } else {
                    if (!restApiGenDir.exists()) {
                        restApiGenDir.mkdirs();
                    }

                    User admin = new User();
                    admin.setEmail("steveniesfl@gmail.com");
                    admin.setEmailStatus(EmailStatus.VERIFIED);
                    admin.setCompany("Self");
                    admin.setNameFirst("Steve");
                    admin.setNameLast("Nies");
                    admin.setPassword("smn01311959");

                    Calendar cal = Calendar.getInstance();
                    cal.add(Calendar.YEAR, 50);
                    admin.setAccessExpiryDate(cal.getTime());

                    this.addUser(admin);
                    this.saveToJsonFile();
                }
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
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and email must not be null");
        }
        userMap.put(user.getEmail(), user);
        return user;
    }

    public void removeUser(User user) throws IllegalArgumentException {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and email must not be null");
        }
        userMap.remove(user.getEmail());
    }

    public void removeAllUsers() {
        userMap.clear();
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(userMap.values());
    }

    public boolean userExists(String email) {
        return userMap.containsKey(email);
    }

    public User findUserById(long id) throws ExceptionUserDoesntExist {
        User user = null;
        for (User u : userMap.values()) {
            if (u.getId() == id) {
                user = u;
            }
        }
        if (user == null) {
            throw new ExceptionUserDoesntExist();
        }
        return user;
    }

    public User findUserByEmail(String email) throws ExceptionUserDoesntExist {
        User user = userMap.get(email);
        if (user == null) {
            throw new ExceptionUserDoesntExist();
        }
        return user;
    }

    public File getUserStorageDir(User user) {

        // Synthesize a directory for this user.
        String javaUserDir = System.getenv(JAVA_USER_DIR);
        File restApiGenDir = new File(javaUserDir, TOOL_DIR);
        String userDirName = user.getCompany() + "-" + user.getNameFirst() + "-" + user.getNameLast();
        userDirName = userDirName.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
        userDirName = userDirName.replaceAll("_+", "_");
        File userDir = new File(restApiGenDir, userDirName);

        // Create the user's storage directory if it doesn't exist.
        if (!userDir.exists()) {
            userDir.mkdirs();
        }
        return userDir;
    }

    private void loadFromJsonFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<User> users;

        String javaUserDir = System.getenv(JAVA_USER_DIR);
        File restApiGenDir = new File(javaUserDir, TOOL_DIR);
        File usersFile = new File(restApiGenDir, USERS_FILENAME);
        try (FileReader reader = new FileReader(usersFile)) {
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

        List<User> users = this.getAllUsers();
        users.sort((u1, u2) -> u1.getEmail().compareTo(u2.getEmail()));

        String javaUserDir = System.getenv(JAVA_USER_DIR);
        File restApiGenDir = new File(javaUserDir, TOOL_DIR);
        File usersFile = new File(restApiGenDir, USERS_FILENAME);
        try (FileWriter writer = new FileWriter(usersFile)) {
            mapper.writeValue(writer, users);
        }
    }

}