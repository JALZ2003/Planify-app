package com.planify.app.security;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream("planify-f6ab2-firebase-adminsdk-fbsvc-e5ddb58e3b.json");

        if (serviceAccount == null) {
            throw new IOException("No se encontró el archivo de credenciales de Firebase en resources.");
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
