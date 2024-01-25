module com.example.kripto_projekat {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.bouncycastle.provider;
    requires org.bouncycastle.pkix;
    requires java.desktop;
    requires java.logging;


    opens com.example.kripto_projekat to javafx.fxml;
    exports com.example.kripto_projekat;
    exports com.example.kripto_projekat.controllers;
    opens com.example.kripto_projekat.controllers to javafx.fxml;
}