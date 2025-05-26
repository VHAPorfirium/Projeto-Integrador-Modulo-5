package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;

/**
 * Equipe, esta classe `DeviceToken` é o nosso DTO (Data Transfer Object) no Android.
 * Ela serve para representar o objeto `br.com.projetoIntegrador.model.DeviceToken` que recebemos da API,
 * especialmente quando registramos um token ou listamos os tokens de um paciente. [cite: 290]
 * Usem esta classe para que o Gson consiga desserializar a resposta JSON da API corretamente.
 */
public class DeviceToken {

    /**
     * Este é o ID único do registro do `DeviceToken` no banco de dados da API. [cite: 290]
     * Ele é gerado pelo backend.
     */
    @SerializedName("id")
    private Long id;

    /**
     * O modelo `DeviceToken` na nossa API tem um campo `Paciente paciente`. [cite: 291]
     * Quando a API serializa isso para JSON, o objeto `Paciente` associado pode vir
     * como um objeto JSON aninhado (contendo alguns ou todos os campos do paciente)
     * ou, em alguns casos, apenas o ID do paciente.
     * Para representar essa estrutura aninhada, criei a classe interna `PacienteReferenceInDeviceToken`.
     * A anotação `@SerializedName("paciente")` mapeia o campo JSON "paciente" para este objeto.
     * Renomeei para `PacienteReferenceInDeviceToken` para evitar conflitos de nome se vocês também tiverem
     * uma classe `PacienteDto` sendo usada diretamente em outros contextos.
     */
    @SerializedName("paciente")
    private PacienteReferenceInDeviceToken paciente;

    /**
     * Este campo armazena o próprio token FCM (Firebase Cloud Messaging) do dispositivo. [cite: 292]
     * É a string longa que o Firebase gera para identificar unicamente uma instância do app.
     */
    @SerializedName("token")
    private String token;

    /**
     * Este campo representa a data e hora em que o registro do token foi criado no backend.
     * A API usa o tipo `Instant` para este campo. [cite: 293] Para simplificar a desserialização com Gson no Android
     * (especialmente se estivermos lidando com diferentes níveis de API do Android),
     * estamos tratando este campo como uma `String`.
     * No código do app, vocês precisarão converter esta String para um objeto de data/hora utilizável
     * (ex: `java.time.Instant` com desugaring, ou `java.util.Date`).
     */
    @SerializedName("createdAt")
    private String createdAt;

    // Getters e Setters padrão para todos os campos.
    // Certifiquem-se de que o Gson consiga acessá-los (geralmente são public).
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PacienteReferenceInDeviceToken getPaciente() {
        return paciente;
    }

    public void setPaciente(PacienteReferenceInDeviceToken paciente) {
        this.paciente = paciente;
    }

    /**
     * Pessoal, este é um método auxiliar que criei para facilitar a obtenção do `pacienteId`
     * diretamente do objeto `paciente` aninhado.
     * O DTO Android original `DeviceToken.java` que vocês tinham usava um campo `userId`.
     * No entanto, o modelo da API (`br.com.projetoIntegrador.model.DeviceToken`) tem um objeto `Paciente` completo. [cite: 291]
     * Este DTO agora está mais alinhado com a estrutura retornada pela API.
     * @return O ID do paciente se o objeto `paciente` e seu ID não forem nulos, caso contrário, retorna `null`.
     */
    public Long getPacienteId() {
        if (paciente != null) {
            return paciente.getId();
        }
        return null;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Equipe, esta é uma classe interna estática. Ela serve para representar o objeto `Paciente`
     * da forma como ele vem aninhado dentro do JSON do `DeviceToken` retornado pela API.
     * Tipicamente, para este contexto específico do `DeviceToken`, o objeto `Paciente` aninhado
     * pode conter apenas o ID do paciente ou alguns campos essenciais, e não todos os campos
     * do modelo `Paciente` completo da API (que tem muitos campos, como endereço, alergias, etc.).
     * Por enquanto, estou assumindo que o ID (`@SerializedName("id")`) é a principal (ou única)
     * informação do paciente que vem serializada aqui.
     * Se a API incluir mais campos do paciente dentro deste objeto aninhado (ex: "fullName"),
     * vocês precisarão adicionar esses campos aqui nesta classe interna também, com a respectiva
     * anotação `@SerializedName`.
     */
    public static class PacienteReferenceInDeviceToken {
        @SerializedName("id")
        private Long id;

        // Se o JSON do paciente aninhado dentro do DeviceToken também incluir outros campos,
        // como o nome, por exemplo:
        // @SerializedName("fullName")
        // private String fullName;
        // Lembrem-se de adicionar getters e setters para eles também.

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        // Exemplo de getter para fullName, se adicionado:
        // public String getFullName() { return fullName; }
        // public void setFullName(String fullName) { this.fullName = fullName; }
    }
}