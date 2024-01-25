package com.example.kripto_projekat.controllers;

        import com.example.kripto_projekat.FileEncryption;
        import com.example.kripto_projekat.HelloApplication;
        import com.example.kripto_projekat.Singletons.PathSingleton;
        import com.example.kripto_projekat.Singletons.UsernameSingleton;
        import com.example.kripto_projekat.certs.CertificateUtils;
        import javafx.event.ActionEvent;
        import javafx.fxml.FXML;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Parent;
        import javafx.scene.Scene;
        import javafx.scene.control.*;
        import javafx.stage.Stage;
        import org.bouncycastle.asn1.x500.X500Name;

        import java.io.*;
        import java.math.BigInteger;
        import java.nio.charset.StandardCharsets;
        import java.nio.file.Files;
        import java.nio.file.Path;
        import java.nio.file.Paths;
        import java.security.*;
        import java.security.cert.X509Certificate;
        import java.util.Base64;
        import java.util.Collections;
        import java.util.Date;
        import org.bouncycastle.asn1.x500.X500Name;
        import org.bouncycastle.cert.X509CertificateHolder;
        import org.bouncycastle.cert.X509v3CertificateBuilder;
        import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
        import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
        import org.bouncycastle.operator.ContentSigner;
        import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
        import org.bouncycastle.util.io.pem.PemObject;
        import org.bouncycastle.util.io.pem.PemWriter;
        import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class LoginController {
    @FXML
    private Label labelPath;

    @FXML
    private Button backButton;
    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button loginButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    public void initialize() {
        String selectedCertificatePath = PathSingleton.getInstance().getSelectedCertificatePath();
        if (selectedCertificatePath != null) {
            labelPath.setText("Path: " + selectedCertificatePath);
            labelPath.setTooltip(new Tooltip(labelPath.getText()));
        }
    }

    @FXML
    void loginButtonPressed(ActionEvent event) {
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
        else {
            // Check if the user's account file exists
            File userAccountFile = new File("accounts/" + username + ".txt");
            System.out.println(userAccountFile);
            if (!userAccountFile.exists()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Username");
                alert.setHeaderText("Username that you entered is not valid!");
                alert.setContentText("Please try again.");
                alert.showAndWait();

            } else {
                try {

                    String storedAccountInfo = Files.readString(Paths.get(userAccountFile.getPath()));
                    String[] accountParts = storedAccountInfo.split(";");
                    System.out.println(accountParts[0]);
                    System.out.println(accountParts[1]);
                    if (accountParts.length != 3) {
                        // Invalid account format, show an alert or handle accordingly
                        // ...
                        System.out.println(accountParts);
                    } else {
                        String storedPassword = accountParts[1].trim(); // Extract the password part
                        if (!password.equals(storedPassword)) {

                            int failedAttempts = loadFailedLoginAttempts(username);
                            addFailedLoginAttempts(username);
                            System.out.println(failedAttempts);

                            if (failedAttempts >= 3) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Certificate Suspended");
                                alert.setHeaderText("Certificate for this user is suspended");
                                alert.setContentText("Please try to login again or register new account.");
                                alert.showAndWait();
                                return;

                            }
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Invalid Password");
                            alert.setHeaderText("Password that you entered is not valid!");
                            alert.setContentText("Please try again.");
                            alert.showAndWait();

                        } else {
                            String certificateFileName = labelPath.getText().trim();
                            if (certificateFileName.length() > 6) {
                                certificateFileName = certificateFileName.substring(6);
                            }
                            //String certificateFileName = labelPath.getText().replace("\\", "\\\\");
                            boolean isValidCertificate = CertificateUtils.verifyCertificateValidity(certificateFileName,username);






                            if (isValidCertificate) {
                                UsernameSingleton.getInstance().setUsername(username);

                                Stage stage = new Stage();
                                FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("ChooseAlghorithmView.fxml"));

                                // Set the controller
                               // FilesViewController controller = new FilesViewController();
                                //fxmlLoader.setController(controller);
                    //            Parent root = fxmlLoader.load();

                               // controller.initialize();
                                Scene scene = null;
                                try {
                                    scene = new Scene(fxmlLoader.load());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                //Scene scene = new Scene(root); // Use the loaded root directly
                                //stage.setTitle("Files - "+username);
                                stage.setScene(scene);
                                stage.show();

                                // Close the LoginRegisterView
                                Stage loginRegisterStage = (Stage) loginButton.getScene().getWindow();
                                loginRegisterStage.close();
                                if(!isHistoryValid())
                                {
                                    showAlert("History File Modification", "Your history file has been modified by an unauthorized user.");
                                }
                                //PathSingleton.getInstance().setSelectedCertificatePath(fullPath);


                            } else {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setTitle("Invalid Certificate");
                                alert.setHeaderText("Certificate that you entered is not valid!");
                                alert.setContentText("Please try again.");
                                alert.showAndWait();
                            }

                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    // Show an alert for error
                    // ...
                }
            }

            // Create a new account file for the user


        }
    }
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private boolean isHistoryValid()
    {
        String baseDirectory = "files";


        String username = getCurrentUsername();
        File userDir = new File(baseDirectory + "/" + username);
        FileEncryption fe = new FileEncryption();
        Path privateKeyPath = Path.of("certificates/keys/"+getCurrentUsername()+"/"+getCurrentUsername()+"_private.pem");
        Path encryptedFilePath = Paths.get(userDir.getPath(), "history.enc");
        String decryptedHash=fe.decryptAndVerifyFile(encryptedFilePath,privateKeyPath);

        Path historyFilePath=Paths.get(userDir.getPath(),"history.txt");
        if(decryptedHash==null)
            return true;
        String newHash = hash(historyFilePath);
        if(decryptedHash.equals(newHash))
        return true;
        else
            return false;
    }

    public static String hash(Path filePath) {
        try {
            byte[] fileContent = Files.readAllBytes(filePath);
            return hashBytes(fileContent);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String hashBytes(byte[] input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input);
        return Base64.getEncoder().encodeToString(hash);
    }
    private String getCurrentUsername() {
        //System.out.println(username);
        return UsernameSingleton.getInstance().getUsername();
    }

    private void addFailedLoginAttempts(String username) {
        // Define the path to the user's account file
        String filePath = "accounts/" + username + ".txt";

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read the existing content of the file
            String line = reader.readLine();
            if (line != null) {
                // Split the line into parts (name;password;attempts)
                String[] parts = line.split(";");

                // Ensure that there are three parts (name, password, attempts)
                if (parts.length == 3) {
                    String name = parts[0];
                    String password = parts[1];
                    int attempts = Integer.parseInt(parts[2]);

                    // Increment the number of failed login attempts
                    attempts++;

                    // Update the line with the new attempts count
                    String updatedLine = name + ";" + password + ";" + attempts;

                    // Write the updated line back to the file
                    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                        writer.write(updatedLine);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle the IOException (e.g., log it or throw it)
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the IOException (e.g., log it or throw it)
        }
    }

    private int loadFailedLoginAttempts(String username) {
        // Define the path to the accounts directory
        String accountsDirectory = "accounts/";

        try (BufferedReader br = new BufferedReader(new FileReader(accountsDirectory + username + ".txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Split the line by semicolon to extract the failed attempts
                String[] parts = line.split(";");
                if (parts.length == 3) {
                    // Assuming the format is "name;password;failedAttempts"
                    String name = parts[0].trim();
                    String password = parts[1].trim();
                    int failedAttempts = Integer.parseInt(parts[2].trim());
                    return failedAttempts;
                }
            }
        } catch (IOException e) {
            // Handle any exceptions (e.g., file not found)
            e.printStackTrace();
        }

        // Return -1 if the username was not found or there was an error
        return -1;
    }






    @FXML
    void backButtonPressed(ActionEvent event) {
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

}
