package br.com.projetoIntegrador.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

// Essa classe tem a funcionalidade de configurar o Firebase e expor beans para envio de notificações.
@Configuration
public class FirebaseConfig {

    // Cria e inicializa o FirebaseApp com as credenciais do serviço.
    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        var serviceAccount = new ClassPathResource("firebase-service-account.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                .build();
        return FirebaseApp.initializeApp(options);
    }

    // Fornece o bean de FirebaseMessaging vinculado ao FirebaseApp inicializado.
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
        return FirebaseMessaging.getInstance(app);
    }
}
