package com.example.kripto_projekat.controllers;

import com.example.kripto_projekat.HelloApplication;
import com.example.kripto_projekat.Singletons.UsernameSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.bouncycastle.crypto.StreamCipher;
import org.bouncycastle.crypto.io.CipherOutputStream;


import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

public class FilesViewController {
    @FXML
    private Button deleteButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button downloadButton;

    @FXML
    private ListView<String> filesListView;

    @FXML
    private Button uploadButton;

    public void initialize() {
        //System.out.println("test");
        // Load the list of documents and populate the ListView
        loadDocuments();
    }

    @FXML
    void downloadButtonPressed(ActionEvent event) {
        String selectedFileName = filesListView.getSelectionModel().getSelectedItem();

        if (selectedFileName != null) {
            // Open a FileChooser dialog to select the key file
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Key Files (*.ser)", "*.ser"));
            File keyFile = fileChooser.showOpenDialog(null);

            if (keyFile != null) {
                // Check if the key is valid and call decryptAndSaveImage with the correct arguments
                boolean success = decryptAndSaveText(selectedFileName, keyFile);
                if (success) {
                    // Show a success message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Download Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("File downloaded successfully.");
                    alert.showAndWait();
                } else {
                    // Show an error message if decryption fails
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Decryption Error");
                    alert.setHeaderText(null);
                    alert.setContentText("There was an error decrypting the file.");
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    void uploadButtonPressed(ActionEvent event) {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();

        // Set the file extension filters to restrict the user to image files
        FileChooser.ExtensionFilter textFilter = new FileChooser.ExtensionFilter(
                "Text Files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(textFilter);

        // Show the file chooser dialog
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            String fileName = selectedFile.getName();

            String username = getCurrentUsername();

            // Check if the username is not null or empty
            if (username != null && !username.isEmpty()) {
                // Define the base directory where files will be stored
                String baseDirectory = "files";

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
                Path destinationPath = Paths.get(userDir.getPath(), fileName);
                System.out.println(destinationPath);

                // Check if the file with the same name already exists
                if (Files.exists(destinationPath)) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("File already exists!");
                    alert.setHeaderText(null);
                    alert.setContentText("File with the same name already exists.\nPlease choose different file.");
                    alert.showAndWait();
                    // Handle the case where the file with the same name already exists
                   // System.out.println("File with the same name already exists. Please choose a different file.");
                } else {
                    try {
                        uploadFile(selectedFile, username);
                        filesListView.getItems().add(fileName);
                        System.out.println("File uploaded successfully to: " + destinationPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                // Handle the case where the username is not available or empty
                System.out.println("Username is not available.");
            }
        }
    }


    private String getCurrentUsername() {
        //System.out.println(username);
        return UsernameSingleton.getInstance().getUsername();
    }

    private void uploadFile(File selectedFile, String username) throws IOException {
        // Define the base directory where files will be stored
        String baseDirectory = "files";

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

        // Define the destination directory for the uploaded image
        String fileDirectory = userDir.getPath() + "/" + selectedFile.getName();

        // Create the image directory if it doesn't exist
        File fileDir = new File(fileDirectory);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        // Split the image into parts and save them in the image directory
        int numberOfParts = generateAndSaveFileParts(selectedFile, fileDir);

        // Add the file name to the ListView (if needed)
        // filesListView.getItems().add(selectedFile.getName());

        System.out.println("File uploaded successfully to: " + fileDirectory);
        System.out.println("File split into " + numberOfParts + " parts.");
    }

    @FXML
    void deleteButtonPressed(ActionEvent event) {
        // Get the selected file name from the filesListView
        String selectedFileName = filesListView.getSelectionModel().getSelectedItem();

        if (selectedFileName != null) {
            String username = getCurrentUsername();

            // Check if the username is not null or empty
            if (username != null && !username.isEmpty()) {
                // Define the base directory where files are stored
                String baseDirectory = "files";

                // Create the user's directory if it doesn't exist
                File userDir = new File(baseDirectory + "/" + username);
                if (userDir.exists()) {
                    // Define the selected directory path
                    Path selectedDirPath = Paths.get(userDir.getPath(), selectedFileName);
                    Path selectedDirPathKeys = Paths.get(userDir.getPath(),selectedFileName+".keys");
                    System.out.println(selectedDirPathKeys);

                    try {
                        // Recursive deletion of the selected directory and its contents
                        deleteDirectory(selectedDirPath);
                        deleteDirectory(selectedDirPathKeys);

                        // Remove the file name from the filesListView
                        filesListView.getItems().remove(selectedFileName);
                        System.out.println("Keys delete successfully: "+selectedDirPathKeys);
                        System.out.println("Selected directory and its contents deleted successfully: " + selectedDirPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Handle deletion errors here
                        System.err.println("Error deleting selected directory and its contents: " + selectedDirPath);
                    }
                } else {
                    // Handle the case where the user's directory doesn't exist
                    System.out.println("User directory does not exist.");
                }
            } else {
                // Handle the case where the username is not available or empty
                System.out.println("Username is not available.");
            }
        } else {
            // Handle the case where no file is selected
            System.out.println("No file selected for deletion.");
        }
    }

    // Recursive method to delete a directory and its contents
    private static void deleteDirectory(Path directory) throws IOException {
        if (Files.exists(directory)) {
            Files.walk(directory)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
    }

    @FXML
    void logoutButtonPressed(ActionEvent event) {
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
        Stage filesStage = (Stage) downloadButton.getScene().getWindow();
        filesStage.close();

    }




    public static List<byte[]> chunk(byte[] array, int chunkSize) {
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be greater than 0.");
        }

        int numOfChunks = (int) Math.ceil((double) array.length / chunkSize);
        List<byte[]> result = new ArrayList<>();

        for (int i = 0; i < numOfChunks; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, array.length);
            int length = end - start;
            byte[] chunk = new byte[length];
            System.arraycopy(array, start, chunk, 0, length);
            result.add(chunk);
        }

        return result;
    }

    private static int generateRandomNumber() {
        SecureRandom random = new SecureRandom();
        return random.nextInt((10 - 4) + 1) + 4;
    }


   private static byte[] encryptTextPart(byte[] textData, SecretKey encryptionKey) throws IOException {
       try {
           // Create an initialization vector (IV)
           SecureRandom random = new SecureRandom();
           byte[] iv = new byte[16];
           random.nextBytes(iv);

           // Initialize the cipher
           Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
           IvParameterSpec ivParams = new IvParameterSpec(iv);
           cipher.init(Cipher.ENCRYPT_MODE, encryptionKey, ivParams);

           // Encrypt the text bytes
           byte[] encryptedBytes = cipher.doFinal(textData);

           // Concatenate the IV and encrypted bytes
           byte[] encryptedPart = new byte[iv.length + encryptedBytes.length];
           System.arraycopy(iv, 0, encryptedPart, 0, iv.length);
           System.arraycopy(encryptedBytes, 0, encryptedPart, iv.length, encryptedBytes.length);

           return encryptedPart;
       } catch (Exception e) {
           throw new IOException("Failed to encrypt text part", e);
       }
   }


    private static int generateAndSaveFileParts(File selectedFile, File baseDir) throws IOException {
        // Generate a random number N (4 < N <= 200)
        int numberOfParts = generateRandomNumber();

        // Load the text content
        byte[] allTextBytes = Files.readAllBytes(selectedFile.toPath());
        List<byte[]> textParts = new ArrayList<>();

        // Create the user's directory if it doesn't exist
        File userDir = new File(baseDir.getPath());
        if (!userDir.exists()) {
            userDir.mkdirs();
        }

        File keysDir = new File(userDir.getPath() + ".keys");
        if (!keysDir.exists()) {
            keysDir.mkdirs();
        }

        Map<String, SecretKey> partKeyMap = new LinkedHashMap<>();
        int size = allTextBytes.length / numberOfParts + 1;
        List<byte[]> partText = chunk(allTextBytes, size);

        // Split the text into parts and save them in separate directories
        // Split the text into parts and save them in separate directories
        for (int i = 0; i < numberOfParts; i++) {
            // Create a directory for the current part
            String UUID = java.util.UUID.randomUUID().toString();
            File partDir = new File(userDir.getPath() + "/" + UUID);
            if (!partDir.exists()) {
                partDir.mkdirs();
            }

            SecretKey secretKey;
            try {
                secretKey = generateEncryptionKey();
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }

            // Ensure that partText has enough elements before accessing index i
            if (i < partText.size()) {
                partKeyMap.put(UUID, secretKey);
                byte[] encryptedPart = encryptTextPart(partText.get(i), secretKey);

                // Save the encrypted part as a Base64-encoded string
                String base64Part = Base64.getEncoder().encodeToString(encryptedPart);

                // Save the part text as a file in the part directory with a .txt extension
                File partFile = new File(partDir, UUID + ".txt");
                System.out.println(partFile);
                try (FileWriter writer = new FileWriter(partFile)) {
                    writer.write(base64Part);
                }
            } else {
                // Log a warning if i exceeds the size of partText
                System.out.println("Warning: Index " + i + " exceeds the size of partText.");
            }
        }

        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File(keysDir, "keyMapping.ser")))) {
            outputStream.writeObject(partKeyMap);
        }
        return numberOfParts;
    }


    public static SecretKey generateEncryptionKey() throws NoSuchAlgorithmException {
        // Create a KeyGenerator for AES (Advanced Encryption Standard)
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");

        // Set the key size (128, 192, or 256 bits)
        keyGenerator.init(256); // You can choose the key size based on your security requirements

        // Generate a random encryption key

        return keyGenerator.generateKey();
    }

    private void loadDocuments() {

        // Assuming you have a method to get the username
        String username = getCurrentUsername();
        //System.out.println(username);

        if (username != null && !username.isEmpty()) {
            // Define the directory where user's documents are stored
            String userDirectory = "files/" + username;
            //System.out.println(userDirectory);

            File directory = new File(userDirectory);
           // System.out.println(directory);
            if (directory.exists() && directory.isDirectory()) {
                // List all files in the user's directory and add them to the ListView
                File[] files = directory.listFiles();

                if (files != null) {

                    for (File file : files) {
                        if (file.isDirectory() && !file.getName().endsWith(".keys")) {

                            //System.out.println(file.getName());
                            filesListView.getItems().add(file.getName());
                        }
                    }
                }
            }
        }
    }




    private boolean decryptAndSaveText(String selectedFileName, File keyFile) {
        // Deserialize the key mapping from the key file
        Map<String, SecretKey> keyMapping = deserializeKeyMapping(keyFile);
        if (keyMapping == null) {
            System.out.println("keyMapping == null");
            return false;
        }
        try {
            // Create a Cipher instance for AES/CBC decryption
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");

            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Output Directory");
            File outputDirectory = directoryChooser.showDialog(null);

            File decryptedTextFile = new File(outputDirectory, selectedFileName + "_decrypted.txt");
            FileWriter decryptedTextWriter = new FileWriter(decryptedTextFile);
            String userName = UsernameSingleton.getInstance().getUsername();

            StringBuilder reconstructedText = new StringBuilder();

            // Iterate through each part of the text
            for (Map.Entry<String, SecretKey> entry : keyMapping.entrySet()) {
                String partUUID = entry.getKey();
                SecretKey secretKey = entry.getValue();

                // Read the Base64-encoded part from the file
                File partFile = new File("files" + "/" + userName + "/" + selectedFileName + "/" + partUUID, partUUID + ".txt");
                String base64Part = Files.readString(partFile.toPath());
                byte[] encryptedPart = Base64.getDecoder().decode(base64Part);

                // Separate the IV from the encrypted data
                byte[] iv = new byte[16];
                System.arraycopy(encryptedPart, 0, iv, 0, iv.length);

                byte[] encryptedBytes = new byte[encryptedPart.length - iv.length];
                System.arraycopy(encryptedPart, iv.length, encryptedBytes, 0, encryptedBytes.length);

                // Initialize the cipher for decryption using the part's key and IV
                cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iv));

                // Decrypt the part and append it to the reconstructed text data
                byte[] decryptedPart = cipher.doFinal(encryptedBytes);
                reconstructedText.append(new String(decryptedPart, StandardCharsets.UTF_8));
            }
            String reconstructedTextString = reconstructedText.toString();
            System.out.println("Reconstructed text data length: " + reconstructedTextString.length());
            System.out.println("Reconstructed text: " + reconstructedTextString);

            // Write the reconstructed text to the output file
            decryptedTextWriter.write(reconstructedTextString);
            decryptedTextWriter.close();

            System.out.println("Text decrypted and saved to: " + decryptedTextFile.getAbsolutePath());
            return true;
        } catch (Exception e) {
            System.out.println("decryption error exception");
            e.printStackTrace();
            // Handle decryption errors here
            return false;
        }
    }



    private Map<String, SecretKey> deserializeKeyMapping(File keyFile) {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(keyFile))) {
            // Read the serialized Map<String, SecretKey> from the key file
            Map<String, SecretKey> keyMapping = (Map<String, SecretKey>) objectInputStream.readObject();
            return keyMapping;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle deserialization errors here
            return null;
        }
    }


}
