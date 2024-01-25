package com.example.kripto_projekat.controllers;

import com.example.kripto_projekat.HelloApplication;
import com.example.kripto_projekat.Singletons.PathSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class ImportCertificateController {

    @FXML
    private Button importCertificateButton;

    @FXML
    private Label labelPath;

    @FXML
    private Button loginButton;
    @FXML
    private Button backButton;

    @FXML
    void backButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginRegisterView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        // Close the FilesView
        Stage importCertificateStage = (Stage) importCertificateButton.getScene().getWindow();
        importCertificateStage.close();
    }

    @FXML
    void importCertificateButtonPressed(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Certificate File");

        // Set the initial directory (customize this path)
        String customInitialDirectoryPath = "C:\\Users\\milos\\Desktop\\KRIPTO_PROJEKAT\\certificates\\certs";
        File customInitialDirectory = new File(customInitialDirectoryPath);
        if (customInitialDirectory.exists() && customInitialDirectory.isDirectory()) {
            fileChooser.setInitialDirectory(customInitialDirectory);
        } else {
            // Set the user's desktop as the initial directory
            String userDesktopPath = System.getProperty("user.home") + "/Desktop";
            File userDesktop = new File(userDesktopPath);
            fileChooser.setInitialDirectory(userDesktop);
        }


        // Set extension filters (optional, adjust to your needs)
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Certificate Files",  "*.cer", "*.crt");
        fileChooser.getExtensionFilters().add(filter);

        // Show the FileChooser dialog
        Stage stage = (Stage) importCertificateButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        // Update label with the selected file path
        if (selectedFile != null) {
            String fullPath = selectedFile.getAbsolutePath();
            PathSingleton.getInstance().setSelectedCertificatePath(fullPath);
            labelPath.setText("Path: " + fullPath);
            labelPath.setTooltip(new Tooltip(fullPath)); // Display full path as tooltip
            loginButton.setDisable(false);
        }
    }
    @FXML
    void loginButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("LoginView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) loginButton.getScene().getWindow();
        loginRegisterStage.close();

    }

}
