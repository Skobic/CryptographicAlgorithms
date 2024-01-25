package com.example.kripto_projekat.Singletons;

public class PathSingleton {
    private static PathSingleton instance;
    private String selectedCertificatePath;

    private PathSingleton() {}

    public static PathSingleton getInstance() {
        if (instance == null) {
            instance = new PathSingleton();
        }
        return instance;
    }

    public String getSelectedCertificatePath() {
        return selectedCertificatePath;
    }

    public void setSelectedCertificatePath(String selectedCertificatePath) {
        this.selectedCertificatePath = selectedCertificatePath;
    }
}