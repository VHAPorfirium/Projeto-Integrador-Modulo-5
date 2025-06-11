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

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        // Carrega o JSON da sua conta de serviço
        ClassPathResource serviceAccount = new ClassPathResource("firebase-service-account.json");

        // Monta as options com projectId explícito
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .setProjectId("fcmnotificationsdb")
                .build();

        // Inicializa (ou recupera) um FirebaseApp com essas opções
        String appName = "projeto-integrador-app";
        FirebaseApp app = FirebaseApp.getApps().stream()
                .filter(a -> a.getName().equals(appName))
                .findFirst()
                .orElseGet(() -> FirebaseApp.initializeApp(options, appName));

        // Retorna o bean de FirebaseMessaging vinculado ao app
        return FirebaseMessaging.getInstance(app);
    }
}
