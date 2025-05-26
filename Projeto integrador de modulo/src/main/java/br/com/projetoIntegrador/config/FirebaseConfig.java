package br.com.projetoIntegrador.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Optional;

// Essa classe tem a funcionalidade de configurar o Firebase e expor beans para envio de notificações.
@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        ClassPathResource serviceAccountResource = new ClassPathResource("firebase-service-account.json");

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountResource.getInputStream()))
                // .setDatabaseUrl("https://<>.firebaseio.com/") ID do projeto Firebase
                .build();

        final String appName = "my-app-projeto-integrador"; 
        FirebaseApp firebaseApp;

        Optional<FirebaseApp> existingApp = FirebaseApp.getApps().stream()
                .filter(app -> app.getName().equals(appName))
                .findFirst();

        if (existingApp.isPresent()) {
            firebaseApp = existingApp.get();
        } else {
            firebaseApp = FirebaseApp.initializeApp(options, appName);
        }

        return FirebaseMessaging.getInstance(firebaseApp);
    }
}