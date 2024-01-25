package com.example.kripto_projekat.certs;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;

public class CertificateUtils {

    public static boolean verifyCertificateValidity(String certificateFileName, String username) {
        try {
            Security.addProvider(new BouncyCastleProvider());

            Path path = Paths.get(certificateFileName);

            // Read the certificate file as text
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                PEMParser pemParser = new PEMParser(reader);

                X509CertificateHolder certificateHolder = (X509CertificateHolder) pemParser.readObject();
                pemParser.close();
                System.out.println("test3");

                // Print some information about the loaded certificate
                System.out.println("Issuer: " + certificateHolder.getIssuer());
                System.out.println("Subject: " + certificateHolder.getSubject());
                String subject = certificateHolder.getSubject().toString().trim();

                X500Name subjectName = new X500Name(String.valueOf(certificateHolder.getSubject()));
                String cnValue = subjectName.getRDNs(BCStyle.CN)[0].getFirst().getValue().toString();
                System.out.println(username);
                //if (subject.length() > 3)
                   // subject = subject.substring(3);
                // Check if the subject matches the provided username
                if (!cnValue.equalsIgnoreCase(username)) {
                    System.out.println("Subject does not match username.");
                    return false;
                }


                X509Certificate certificate = new JcaX509CertificateConverter().getCertificate(certificateHolder);

                certificate.checkValidity();


                // Additional checks if needed (e.g., issuer, purpose, etc.)

                return true; // Certificate is valid
            } catch (CertificateException | IOException e) {
                e.printStackTrace();
                return false; // Certificate is not valid
            }
        }catch (Exception e) {
            e.printStackTrace();
            return false; // Certificate is not valid
        }

    }



}



