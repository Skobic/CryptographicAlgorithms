package com.example.kripto_projekat.controllers;

import com.example.kripto_projekat.FileEncryption;
import com.example.kripto_projekat.HelloApplication;
import com.example.kripto_projekat.Singletons.UsernameSingleton;
import com.example.kripto_projekat.algorithms.Playfair;
import com.example.kripto_projekat.algorithms.RailFence;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Locale;

public class TextKeyInputController {
    @FXML
    private Button backButton;

    @FXML
    private TextField keyInput;

    @FXML
    private Button saveButton;

    @FXML
    private TextArea textInput;


    @FXML
    void saveButtonPressed(ActionEvent event) {
        Stage stage = (Stage) keyInput.getScene().getWindow();
        String title = stage.getTitle();

        if (title.equals("Playfair")) {
            String key = keyInput.getText();
            String text = textInput.getText();

            // Validate input
            if (key.isEmpty() || text.isEmpty()) {
                // Show an alert or handle invalid input
                return;
            }
            Playfair pfc1 = new Playfair(key, text);
            pfc1.cleanPlayFairKey();
            pfc1.generateCipherKey();
            String encText1 = pfc1.encryptMessage();
            System.out.println("Cipher Text is: " + encText1);

            String baseDirectory = "files";

// Create the base directory if it doesn't exist
            String username = getCurrentUsername();

// Check if the username is not null or empty
            if (username != null && !username.isEmpty()) {
                // Define the base directory where files will be stored

                // Create the base directory if it doesn't exist
                File baseDir = new File(baseDirectory);
                if (!baseDir.exists()) {
                    baseDir.mkdir();
                }

                // Create the user's directory if it doesn't exist
                File userDir = new File(baseDirectory + "/" + username);
                if (!userDir.exists()) {
                    userDir.mkdir();
                }

                // Define the destination file path within the user's directory
                Path destinationPath = Paths.get(userDir.getPath(), "history.txt");
                System.out.println(destinationPath);

                // Write to history.txt
                try (BufferedWriter writer = Files.newBufferedWriter(destinationPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    // Format the information and write to the file
                    String historyEntry = String.format("%s|%s|%s|%s", text, "PLAYFAIR", key, encText1);
                    writer.write(historyEntry);
                    writer.newLine();  // Add a new line for the next entry
                    System.out.println("Entry written to history.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception, e.g., show an alert or log the error
                }
                Path originalFilePath = destinationPath;
                Path publicKeyPath = Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_public.pem");
                Path privateKeyPath= Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_private.pem");
                System.out.println(publicKeyPath);
                Path encryptedFilePath = Paths.get(userDir.getPath(), "history.enc");
                FileEncryption fileEncryption=new FileEncryption();
                fileEncryption.encryptAndHashFile(originalFilePath, publicKeyPath, encryptedFilePath);
            }


        }
        if (title.equals("Myszkowski")) {
            String key = keyInput.getText();
            String text = textInput.getText();

            // Validate input
            if (key.isEmpty() || text.isEmpty()) {
                // Show an alert or handle invalid input
                return;
            }

            // Perform Playfair encryption
            text=text.toUpperCase(Locale.ROOT);
            key=key.toUpperCase(Locale.ROOT);
            String encryptedText = myszkowskiEncrypt(text, key);

            // Display or use the encrypted text as needed
            System.out.println("Encrypted Text: " + encryptedText);
            String baseDirectory = "files";

// Create the base directory if it doesn't exist
            String username = getCurrentUsername();

// Check if the username is not null or empty
            if (username != null && !username.isEmpty()) {
                // Define the base directory where files will be stored

                // Create the base directory if it doesn't exist
                File baseDir = new File(baseDirectory);
                if (!baseDir.exists()) {
                    baseDir.mkdir();
                }

                // Create the user's directory if it doesn't exist
                File userDir = new File(baseDirectory + "/" + username);
                if (!userDir.exists()) {
                    userDir.mkdir();
                }

                // Define the destination file path within the user's directory
                Path destinationPath = Paths.get(userDir.getPath(), "history.txt");
                System.out.println(destinationPath);

                // Write to history.txt
                try (BufferedWriter writer = Files.newBufferedWriter(destinationPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    // Format the information and write to the file
                    String historyEntry = String.format("%s|%s|%s|%s", text, "MYSZKOWSKI", key, encryptedText);
                    writer.write(historyEntry);
                    writer.newLine();  // Add a new line for the next entry
                    System.out.println("Entry written to history.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception, e.g., show an alert or log the error
                }
                Path originalFilePath = destinationPath;
                Path publicKeyPath = Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_public.pem");
                Path privateKeyPath = Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_private.pem");
                System.out.println(publicKeyPath);
                Path encryptedFilePath = Paths.get(userDir.getPath(), "history.enc");
                FileEncryption fileEncryption=new FileEncryption();
                fileEncryption.encryptAndHashFile(originalFilePath, publicKeyPath, encryptedFilePath);
            }
        }
        if (title.equals("Rail fence")) {
            String key = keyInput.getText();
            String text = textInput.getText();
            int intKey;

            // Validate input
            if (key.isEmpty() || text.isEmpty()) {
                // Show an alert or handle invalid input
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Key and text cannot be empty");
                alert.setContentText("Please enter both key and text.");
                alert.showAndWait();
                return;  // Exit the method if there is invalid input
            }

            try {
                // Try to parse the key as an integer
                intKey = Integer.parseInt(key);

                // Your further logic using intKey goes here

            } catch (NumberFormatException e) {
                // If parsing fails, show a popup message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Key");
                alert.setHeaderText("Key must be an integer");
                alert.setContentText("Please enter a valid integer key.");
                alert.showAndWait();
                return;  // Exit the method if there is an invalid key
            }

            // Continue with the rest of your code
            RailFence rf = new RailFence();
            String encryptedText = rf.encryptRailFence(text, intKey);

// Display or use the encrypted text as needed
            System.out.println("Encrypted Text: " + encryptedText);
            String baseDirectory = "files";

// Create the base directory if it doesn't exist
            String username = getCurrentUsername();

// Check if the username is not null or empty
            if (username != null && !username.isEmpty()) {
                // Define the base directory where files will be stored

                // Create the base directory if it doesn't exist
                File baseDir = new File(baseDirectory);
                if (!baseDir.exists()) {
                    baseDir.mkdir();
                }

                // Create the user's directory if it doesn't exist
                File userDir = new File(baseDirectory + "/" + username);
                if (!userDir.exists()) {
                    userDir.mkdir();
                }

                // Define the destination file path within the user's directory
                Path destinationPath = Paths.get(userDir.getPath(), "history.txt");
                System.out.println(destinationPath);

                // Write to history.txt
                try (BufferedWriter writer = Files.newBufferedWriter(destinationPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                    // Format the information and write to the file
                    String historyEntry = String.format("%s|%s|%s|%s", text, "RAIL FENCE", key, encryptedText);
                    writer.write(historyEntry);
                    writer.newLine();  // Add a new line for the next entry
                    System.out.println("Entry written to history.txt");
                } catch (IOException e) {
                    e.printStackTrace();
                    // Handle the exception, e.g., show an alert or log the error
                }
                Path originalFilePath = destinationPath;
                Path publicKeyPath = Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_public.pem");
                Path privateKeyPath = Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_private.pem");
                System.out.println(publicKeyPath);
                Path encryptedFilePath = Paths.get(userDir.getPath(), "history.enc");
                FileEncryption fileEncryption=new FileEncryption();
                fileEncryption.encryptAndHashFile(originalFilePath, publicKeyPath, encryptedFilePath);
            }


        }
    }

    private String getCurrentUsername() {
        //System.out.println(username);
        return UsernameSingleton.getInstance().getUsername();
    }
    private String myszkowskiEncrypt(String text, String key) {
        // Remove spaces from the key
        key = key.replaceAll("\\s", "");

        // Determine the number of columns based on the length of the key
        int columns = key.length();

        // Determine the number of rows based on the length of the text
        int rows = (int) Math.ceil((double) text.length() / columns);

        // Create a 2D array to represent the transposition grid
        char[][] grid = new char[rows][columns];

        // Fill the grid with characters from the text
        int index = 0;
        for (int col = 0; col < columns; col++) {
            for (int row = 0; row < rows; row++) {
                if (index < text.length()) {
                    grid[row][col] = text.charAt(index);
                    index++;
                } else {
                    // If we've reached the end of the text, fill the remaining cells with 'X'
                    grid[row][col] = 'X';
                }
            }
        }

        // Create a StringBuilder to store the encrypted text
        StringBuilder encryptedText = new StringBuilder();

        // Sort the key characters to determine the order of columns
        char[] sortedKey = key.toCharArray();
        Arrays.sort(sortedKey);

        // Iterate over the sorted key to append columns to the encrypted text
        for (char k : sortedKey) {
            // Find the index of the key character in the original key
            int originalIndex = key.indexOf(k);

            // Append the characters in the column to the encrypted text
            for (int row = 0; row < rows; row++) {
                encryptedText.append(grid[row][originalIndex]);
            }
        }

        return encryptedText.toString();
    }



    @FXML
    void backButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ChooseAlghorithmView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Choose Algorithm");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) keyInput.getScene().getWindow();
        loginRegisterStage.close();

    }
}
