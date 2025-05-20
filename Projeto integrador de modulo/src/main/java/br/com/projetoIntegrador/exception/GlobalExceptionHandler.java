package br.com.projetoIntegrador.exception;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Essa classe tem a funcionalidade de tratar exceções globais da aplicação e retornar respostas HTTP adequadas.
@ControllerAdvice
public class GlobalExceptionHandler {

    // Trata exceções relacionadas ao envio de notificações via Firebase.
    @ExceptionHandler(FirebaseMessagingException.class)
    public ResponseEntity<Map<String, String>> handleFirebase(
            FirebaseMessagingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(Map.of(
                        "error", "Erro ao enviar notificação",
                        "details", ex.getMessage()
                ));
    }

    // Trata exceções de parâmetros inválidos lançadas pela aplicação.
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleBad(
            IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", ex.getMessage()));
    }

    // Trata quaisquer outras exceções não previstas pela aplicação.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAll(
            Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Erro inesperado",
                        "details", ex.getMessage()
                ));
    }

}
