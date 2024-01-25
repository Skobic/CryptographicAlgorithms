package com.example.kripto_projekat.Singletons;

public class UsernameSingleton {
    private static UsernameSingleton instance = null;
    private String username;

    // Private constructor to prevent instantiation
    private UsernameSingleton() {
    }

    // Method to get the instance of the Singleton
    public static UsernameSingleton getInstance() {
        if (instance == null) {
            instance = new UsernameSingleton();
        }
        return instance;
    }

    // Getter and setter for the username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}