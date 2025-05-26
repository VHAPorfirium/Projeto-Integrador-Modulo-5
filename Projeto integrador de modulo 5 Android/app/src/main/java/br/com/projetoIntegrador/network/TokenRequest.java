package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

/**
 * Equipe, esta é a classe `TokenRequest` do nosso app Android.
 * Ela espelha o DTO (Data Transfer Object) `br.com.projetoIntegrador.dto.TokenRequest` que existe na nossa API. [cite: 230]
 * Vamos usar esta classe para encapsular os dados que precisamos enviar ao backend
 * quando formos registrar um novo token de dispositivo (token FCM) no nosso sistema.
 */
public class TokenRequest {

    /**
     * Este campo representa o ID do paciente ao qual o token FCM pertence.
     * IMPORTANTE: No DTO `TokenRequest` da API, o campo `pacienteId` é definido como do tipo `Long`. [cite: 230, 232]
     * Precisamos garantir que estamos enviando um Long aqui para compatibilidade com o backend.
     */
    @SerializedName("pacienteId")
    private Long pacienteId;

    /**
     * Este campo armazena o token FCM (Firebase Cloud Messaging) propriamente dito.
     * É a string gerada pelo Firebase que identifica unicamente uma instância do app no dispositivo
     * e que será usada pelo backend para enviar notificações push. [cite: 231]
     */
    @SerializedName("token")
    private String token;

    /**
     * Pessoal, este é o construtor da classe `TokenRequest`.
     * Usem ele para criar uma nova instância do objeto, fornecendo o `pacienteId` (Long)
     * e o `token` (String) que será enviado ao backend.
     * @param pacienteId O ID do paciente (deve ser Long).
     * @param token O token FCM do dispositivo.
     */
    public TokenRequest(Long pacienteId, String token) {
        this.pacienteId = pacienteId;
        this.token = token;
    }

    // Abaixo estão os getters e setters para os campos da classe.
    // Eles são importantes para que bibliotecas como o Gson possam acessar e modificar
    // os valores dos campos durante a serialização (conversão do objeto para JSON)
    // e desserialização (se aplicável).

    public Long getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(Long pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}