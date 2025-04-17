package com.planify.app.security;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.*;


@Configuration
public class FirebaseConfig {

//    @PostConstruct
//    public void init() throws IOException {
//        File firebaseConfigFile = writeFirebaseConfigToFile();
//
//        FileInputStream serviceAccount = new FileInputStream(firebaseConfigFile);
//
//        if (serviceAccount == null) {
//            throw new IOException("No se encontró el archivo de credenciales de Firebase en resources.");
//        }
//
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .build();
//
//        FirebaseApp.initializeApp(options);
//    }
//
//    public File writeFirebaseConfigToFile() throws IOException {
//
//        if (firebaseJson == null || firebaseJson.isBlank()) {
//            throw new IllegalStateException("La variable de entorno FIREBASE_CONFIG no está definida.");
//        }
//
//        // Ruta temporal o personalizada
//        File tempFile = File.createTempFile("planify-f6ab2-firebase-adminsdk-fbsvc-e5ddb58e3b", ".json");
//
//        try (FileWriter writer = new FileWriter(tempFile)) {
//            writer.write(firebaseJson);
//        }
//        return tempFile;
//    }
}
