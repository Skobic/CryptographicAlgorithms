package com.example.kripto_projekat.controllers;

import com.example.kripto_projekat.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginRegisterController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    void loginButtonPressed(ActionEvent event) {

        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ImportCertificateView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Import Certificate");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) loginButton.getScene().getWindow();
        loginRegisterStage.close();
    }

    @FXML
    void registerButtonPressed(ActionEvent event) {
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("RegisterView.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage.setTitle("Register");
        stage.setScene(scene);
        stage.show();

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) loginButton.getScene().getWindow();
        loginRegisterStage.close();
    }

}
