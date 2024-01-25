package com.example.kripto_projekat.controllers;

import com.example.kripto_projekat.HelloApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.bouncycastle.asn1.x500.X500Name;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.openssl.PEMWriter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class RegisterController {

    @FXML
    private Button backButton;

    @FXML
    private TextField countryTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField organizationTextField;

    @FXML
    private TextField organizationUnitTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button registerButton;

    @FXML
    private TextField stateTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    void registerButtonPressed(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username.length() < 3 || password.length() < 3) {
            // Show an alert for invalid username or password length
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Username and password must be at least 3 characters long.");
            alert.setContentText("Please enter valid username and password.");
            alert.showAndWait();
            return; // Exit the registration process
        }

        if (password.contains(";")) {
            // Show an alert for invalid password
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText("Password cannot contain the ';' character.");
            alert.setContentText("Please choose a valid password.");
            alert.showAndWait();
            return; // Exit the registration process
        }

        // Create a new account file for the user
        String filePath = "accounts/" + username + ".txt";
        if (Files.exists(Paths.get(filePath))) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Account Exists");
            alert.setHeaderText("An account with this username already exists.");
            alert.setContentText("Please choose a different username.");
            alert.showAndWait();
            return;
        }
        try {
            Files.write(Paths.get(filePath), Collections.singletonList(username + ";" + password+";"+"0"));
            //System.out.println("Account created for: " + username);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Account Created");
            alert.setHeaderText("New account successfully created.");
            alert.setContentText("Account created for: " + username);
            alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair userKeyPair = keyPairGenerator.generateKeyPair();
            String organizationUnit = organizationUnitTextField.getText();
            String organization = organizationTextField.getText();
            String locality = countryTextField.getText();
            String state = stateTextField.getText();
            String country = countryTextField.getText(); // You can set a default value or read from a text field
            String email = emailTextField.getText();

            // Create a new X.500 Name with the specified fields
            X500Name subject = new X500Name("CN=" + username +
                    ", OU=" + organizationUnit +
                    ", O=" + organization +
                    ", L=" + locality +
                    ", ST=" + state +
                    ", C=" + country +
                    ", E=" + email);
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    new X500Name("CN=Milos Skobic"), // Replace with your CA's DN
                    new BigInteger(128, new SecureRandom()),
                    new Date(),
                    new Date(System.currentTimeMillis() + 6L * 30 * 24 * 60 * 60 * 1000), // 6 months validity
                    subject,
                    userKeyPair.getPublic()
            );

            ContentSigner signer = new JcaContentSignerBuilder("SHA256withRSA").build(userKeyPair.getPrivate());
            X509CertificateHolder certHolder = certBuilder.build(signer);

            // Write the certificate to a PEM file
            try (Writer certWriter = new FileWriter("certificates/certs/" + username + "_cert.crt");
                 JcaPEMWriter pemWriter = new JcaPEMWriter(certWriter)) {
                pemWriter.writeObject(certHolder);
            }



            System.out.println("User certificate generated for: " + username);
            System.out.println("Path to cert: ./certificates/certs/"+username+"_cert.crt");

            // Write the private key to a PEM file
            try {
                // Ensure the directory exists
                Files.createDirectories(Paths.get("certificates/keys/" + username));

                try (Writer privateKeyWriter = new FileWriter("certificates/keys/" + username + "/" + username + "_private.pem");
                     JcaPEMWriter pemWriter = new JcaPEMWriter(privateKeyWriter)) {
                    pemWriter.writeObject(userKeyPair.getPrivate());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

// Write the public key to a PEM file
            try {
                // Ensure the directory exists
                Files.createDirectories(Paths.get("certificates/keys/" + username));

                try (Writer publicKeyWriter = new FileWriter("certificates/keys/" + username + "/" + username + "_public.pem");
                     JcaPEMWriter pemWriter = new JcaPEMWriter(publicKeyWriter)) {
                    pemWriter.writeObject(userKeyPair.getPublic());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("RSA keys generated and saved for: " + username);
            System.out.println("Path to RSA keys: ./certificates/keys/"+username);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

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

        // Close the LoginRegisterView
        Stage loginRegisterStage = (Stage) registerButton.getScene().getWindow();
        loginRegisterStage.close();
    }

}
