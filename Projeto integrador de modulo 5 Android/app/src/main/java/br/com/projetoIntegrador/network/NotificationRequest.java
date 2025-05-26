package br.com.projetoIntegrador.network;

import com.google.gson.annotations.SerializedName;
import java.util.Map;

/**
 * Pessoal, este é o nosso DTO `NotificationRequest` para o Android.
 * Ele corresponde exatamente ao `br.com.projetoIntegrador.dto.NotificationRequest` que temos na API. [cite: 173]
 * Vamos usar esta classe para montar o corpo (payload) da requisição HTTP
 * quando quisermos que o nosso backend envie uma notificação via Firebase.
 */
public class NotificationRequest {

    /**
     * Este campo é o ID do paciente para quem a notificação deve ser enviada.
     * ATENÇÃO: Na API, o DTO `NotificationRequest` define `pacienteId` como uma String. [cite: 173, 175]
     * Precisamos manter essa consistência aqui.
     */
    @SerializedName("pacienteId")
    private String pacienteId;

    /**
     * Este campo representa o título da notificação que será exibida no dispositivo do usuário. [cite: 174]
     */
    @SerializedName("title")
    private String title;

    /**
     * Este é o corpo principal da mensagem da notificação. [cite: 174]
     */
    @SerializedName("body")
    private String body;

    /**
     * Este campo é um mapa de Strings chave-valor. Ele permite enviar dados adicionais
     * junto com a notificação (o chamado "data payload" do FCM). [cite: 174]
     * Esses dados não são exibidos diretamente na notificação, mas o app pode
     * recebê-los e usá-los para alguma lógica customizada (ex: abrir uma tela específica,
     * passar um ID de um item, etc.).
     */
    @SerializedName("data")
    private Map<String, String> data;

    /**
     * Equipe, usem este construtor para criar uma nova instância do `NotificationRequest`.
     * Ele exige que todos os campos sejam fornecidos.
     * @param pacienteId O ID do paciente (String).
     * @param title O título da notificação.
     * @param body O corpo da notificação.
     * @param data Um mapa de dados adicionais (pode ser nulo ou vazio se não houver dados).
     */
    public NotificationRequest(String pacienteId, String title, String body, Map<String, String> data) {
        this.pacienteId = pacienteId;
        this.title = title;
        this.body = body;
        this.data = data;
    }

    // Getters e Setters para todos os campos.
    // Eles são necessários para que o Gson (ou outra biblioteca de serialização) possa
    // ler os valores dos campos ao converter o objeto para JSON.

    public String getPacienteId() {
        return pacienteId;
    }

    public void setPacienteId(String pacienteId) {
        this.pacienteId = pacienteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}