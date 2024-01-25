package com.example.kripto_projekat.controllers;

import com.example.kripto_projekat.HelloApplication;
import com.example.kripto_projekat.Singletons.UsernameSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ChooseAlgorithmController {

    @FXML
    private Button myszkowskiButton;

    @FXML
    private Button playfairButton;

    @FXML
    private Button railFenceButton;

    @FXML
    private Button viewHistoryButton;

    @FXML
    void myszkowskiButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TextKeyInputView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Myszkowski");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) myszkowskiButton.getScene().getWindow();
        loginRegisterStage.close();

    }

    @FXML
    void playfairButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TextKeyInputView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Playfair");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) playfairButton.getScene().getWindow();
        loginRegisterStage.close();

    }

    @FXML
    void railFenceButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("TextKeyInputView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Rail fence");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) railFenceButton.getScene().getWindow();
        loginRegisterStage.close();

    }

    @FXML
    void viewHistoryButtonPressed(ActionEvent event) {
        try {
            String baseDirectory = "files";
            String username = getCurrentUsername();

            // Check if the username is not null or empty
            if (username != null && !username.isEmpty()) {
                // Define the base directory where files will be stored
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
                Path historyFilePath = Paths.get(userDir.getPath(), "history.txt");

                // Check if the history file exists
                if (Files.exists(historyFilePath)) {
                    // Load the content of history.txt
                    List<String> lines = Files.readAllLines(historyFilePath);

                    // Create a TextArea to display the content
                    TextArea textArea = new TextArea(String.join("\n", lines));
                    textArea.setEditable(false);
                    textArea.setWrapText(true);

                    // Create a ScrollPane to contain the TextArea
                    ScrollPane scrollPane = new ScrollPane(textArea);
                    scrollPane.setFitToHeight(true);
                    scrollPane.setFitToWidth(true);

                    // Create an Alert to display the content
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.initStyle(StageStyle.UTILITY);
                    alert.setTitle("History");
                    alert.setHeaderText(null);
                    alert.getDialogPane().setContent(scrollPane);

                    // Show the Alert
                    alert.showAndWait();
                } else {
                    // Show an alert that history doesn't exist
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("History");
                    alert.setHeaderText(null);
                    alert.setContentText("History does not exist for the user.");
                    alert.showAndWait();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception (e.g., show an error message)
        }
    }

    private String getCurrentUsername() {
            //System.out.println(username);
            return UsernameSingleton.getInstance().getUsername();
        }

}
