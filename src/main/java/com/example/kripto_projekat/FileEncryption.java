package com.example.kripto_projekat;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import javax.crypto.Cipher;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class FileEncryption {

    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    private static byte[] readPemFile(Path filePath) throws Exception {
        try (PemReader pemReader = new PemReader(new FileReader(filePath.toFile()))) {
            PemObject pemObject = pemReader.readPemObject();
            return pemObject.getContent();
        }
    }

    public static PublicKey loadPublicKey(Path publicKeyPath) throws Exception {
        byte[] keyBytes = readPemFile(publicKeyPath);
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey loadPrivateKey(Path privateKeyPath) throws Exception {
        byte[] keyBytes = readPemFile(privateKeyPath);
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static void encryptAndHashFile(Path filePath, Path publicKeyPath, Path encryptedFilePath) {
        try {
            // Read the public key
            PublicKey publicKey = loadPublicKey(publicKeyPath);

            // Read the file content
            byte[] fileBytes = Files.readAllBytes(filePath);

            // Hash the file content
            byte[] fileHash = hash(fileBytes);

            // Print the hashed content
            System.out.println("Hashed content before encryption: " + Base64.getEncoder().encodeToString(fileHash));

            // Concatenate hash and content
            byte[] concatenatedData = concatenate(fileHash, fileBytes);

            // Encrypt the concatenated data with RSA public key
            byte[] encryptedData = encrypt(concatenatedData, publicKey);

            // Write the encrypted data to a file
            Files.write(encryptedFilePath, encryptedData, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("File encrypted and hashed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String decryptAndVerifyFile(Path encryptedFilePath, Path privateKeyPath) {
        try {
            if (!Files.exists(encryptedFilePath)) {
                System.out.println("Encrypted file does not exist.");
                return null;
            }
            // Read the private key
            PrivateKey privateKey = loadPrivateKey(privateKeyPath);

            // Read the encrypted file
            byte[] encryptedData = Files.readAllBytes(encryptedFilePath);

            // Decrypt the data with RSA private key
            byte[] decryptedData = decrypt(encryptedData, privateKey);

            // Separate the hash and content
            int hashLength = 32; // Assuming a fixed hash length for simplicity
            byte[] decryptedHash = new byte[hashLength];
            byte[] decryptedContent = new byte[decryptedData.length - hashLength];
            System.arraycopy(decryptedData, 0, decryptedHash, 0, hashLength);
            System.arraycopy(decryptedData, hashLength, decryptedContent, 0, decryptedContent.length);

            // Hash the decrypted content
            byte[] recalculatedHash = hash(decryptedContent);

            // Compare the recalculated hash with the decrypted hash
            if (MessageDigest.isEqual(decryptedHash, recalculatedHash)) {
                // Print the hashed content after decryption
                System.out.println("Hashed content after decryption: " + Base64.getEncoder().encodeToString(recalculatedHash));
                return Base64.getEncoder().encodeToString(recalculatedHash);

                // Save the decrypted file content to a new file
                //Path decryptedFilePath = encryptedFilePath.resolveSibling("decrypted_" + encryptedFilePath.getFileName());
                //Files.write(decryptedFilePath, decryptedContent, StandardOpenOption.CREATE);
                //System.out.println("File decrypted and verified successfully.");
                //return decryptedFilePath.toString();
            } else {
                System.out.println("Hash verification failed. File may be tampered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] concatenate(byte[] array1, byte[] array2) {
        byte[] result = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, result, 0, array1.length);
        System.arraycopy(array2, 0, result, array1.length, array2.length);
        return result;
    }

    private static byte[] hash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(data);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] encrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] decrypt(byte[] data, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
