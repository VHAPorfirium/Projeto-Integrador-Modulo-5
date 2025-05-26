package br.com.projetoIntegrador.network;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Pessoal, esta é a nossa interface `ApiService`.
 * Aqui nós definimos todos os endpoints da nossa API REST que o aplicativo Android vai consumir.
 * Cada método aqui corresponde a uma operação da API.
 * Usem as anotações do Retrofit para configurar cada chamada:
 * - `@GET`, `@POST`, `@PUT`, `@DELETE`: Indicam o método HTTP.
 * - `@Path`: Para substituir placeholders na URL (ex: `/pacientes/{id}`).
 * - `@Body`: Para enviar um objeto no corpo da requisição (geralmente em POST e PUT).
 * - `Call<T>`: O tipo de objeto que esperamos como resposta da API (onde T é o nosso DTO).
 */
public interface ApiService {

    // --- Endpoints do TokenController ---
    // Documentação da API relevante: [cite: 112, 115, 118, 122]

    /**
     * Equipe, este método envia um `TokenRequest` (contendo pacienteId e o token FCM) para o endpoint "tokens"
     * para registrar um novo token de dispositivo no backend.
     * A API, conforme o `TokenController`, retorna o `DeviceToken` que foi criado. [cite: 116]
     * @param req O objeto `TokenRequest` com os dados para registro.
     * @return Um `Call` que, quando executado, resultará no `DeviceToken` criado.
     */
    @POST("tokens")
    Call<DeviceToken> registerToken(@Body TokenRequest req);

    /**
     * Usem este método para buscar a lista de `DeviceToken`s associados a um `pacienteId` específico.
     * O endpoint na API é "tokens/pacientes/{pacienteId}". [cite: 119, 120]
     * Lembrem-se que o `pacienteId` que passamos aqui deve ser um Long.
     * @param pacienteId O ID do paciente cujos tokens queremos listar.
     * @return Um `Call` que resultará numa lista de `DeviceToken`.
     */
    @GET("tokens/pacientes/{pacienteId}")
    Call<List<DeviceToken>> listTokensByPaciente(@Path("pacienteId") Long pacienteId);

    /**
     * Este método serve para remover/deletar um token de dispositivo específico pelo seu ID.
     * O endpoint na API é "tokens/{id}". [cite: 123]
     * @param tokenId O ID do `DeviceToken` a ser deletado.
     * @return Um `Call<Void>` indicando que não esperamos um corpo na resposta de sucesso.
     */
    @DELETE("tokens/{id}")
    Call<Void> deleteToken(@Path("id") Long tokenId);


    // --- Endpoints do NotificationController ---
    // Documentação da API relevante: [cite: 79, 80, 83]

    /**
     * Pessoal, este método envia uma `NotificationRequest` para o endpoint "notificacoes" do backend.
     * O backend então usará o Firebase para disparar a notificação para o(s) dispositivo(s) do paciente.
     * A API, conforme o `NotificationController`, retorna uma String (ex: "Notificações enviadas: X"). [cite: 84]
     * @param req O objeto `NotificationRequest` contendo os detalhes da notificação.
     * @return Um `Call` que resultará na String de resposta da API.
     */
    @POST("notificacoes")
    Call<String> sendNotification(@Body NotificationRequest req);


    // --- Endpoints do PacienteController ---
    // Documentação da API relevante: [cite: 89, 90, 92, 96, 99, 102, 106, 109]

    /**
     * Para criar um novo paciente no sistema. Enviamos um `PacienteDto` no corpo da requisição.
     * A API retorna o `PacienteDto` criado, incluindo o ID gerado. [cite: 93, 94]
     * @param pacienteDto O DTO do paciente a ser criado.
     * @return `Call<PacienteDto>` com o paciente criado.
     */
    @POST("pacientes")
    Call<PacienteDto> createPaciente(@Body PacienteDto pacienteDto);

    /**
     * Para listar todos os pacientes cadastrados no sistema. [cite: 97, 98]
     * @return `Call<List<PacienteDto>>` com a lista de todos os pacientes.
     */
    @GET("pacientes")
    Call<List<PacienteDto>> listAllPacientes();

    /**
     * Para buscar os dados de um paciente específico pelo seu ID. [cite: 100, 101]
     * @param pacienteId O ID do paciente a ser buscado.
     * @return `Call<PacienteDto>` com os dados do paciente.
     */
    @GET("pacientes/{id}")
    Call<PacienteDto> getPacienteById(@Path("id") Long pacienteId);

    /**
     * Para atualizar os dados de um paciente existente. Precisamos do ID do paciente na URL
     * e do `PacienteDto` com os novos dados no corpo da requisição. [cite: 103, 104]
     * @param pacienteId O ID do paciente a ser atualizado.
     * @param pacienteDto O DTO com os dados atualizados do paciente.
     * @return `Call<PacienteDto>` com o paciente atualizado.
     */
    @PUT("pacientes/{id}")
    Call<PacienteDto> updatePaciente(@Path("id") Long pacienteId, @Body PacienteDto pacienteDto);

    /**
     * Para remover um paciente do sistema pelo seu ID. [cite: 107]
     * @param pacienteId O ID do paciente a ser deletado.
     * @return `Call<Void>` já que não há corpo na resposta de sucesso.
     */
    @DELETE("pacientes/{id}")
    Call<Void> deletePaciente(@Path("id") Long pacienteId);


    // --- Endpoints do AttendanceEntryController (Entradas de Atendimento / "Fila") ---
    // Documentação da API relevante: [cite: 34, 37, 39, 43, 47, 51]

    /**
     * Lista todas as entradas de atendimento (registros de check-in, status na fila, etc.). [cite: 37, 38]
     * O DTO correspondente no Android é `AttendanceEntryDto`.
     * @return `Call<List<AttendanceEntryDto>>`.
     */
    @GET("entradasAtendimento")
    Call<List<AttendanceEntryDto>> listAllAttendanceEntries();

    /**
     * Busca uma entrada de atendimento específica pelo seu ID. [cite: 40, 41]
     * @param entryId O ID da entrada de atendimento.
     * @return `Call<AttendanceEntryDto>`.
     */
    @GET("entradasAtendimento/{id}")
    Call<AttendanceEntryDto> getAttendanceEntryById(@Path("id") Long entryId);

    /**
     * Cria uma nova entrada de atendimento (ex: um paciente fez check-in). [cite: 44, 45]
     * @param attendanceEntryDto O DTO da entrada de atendimento a ser criada.
     * @return `Call<AttendanceEntryDto>` com a entrada criada.
     */
    @POST("entradasAtendimento")
    Call<AttendanceEntryDto> createAttendanceEntry(@Body AttendanceEntryDto attendanceEntryDto);

    /**
     * Atualiza uma entrada de atendimento existente (ex: mudar o status). [cite: 48, 49]
     * @param entryId O ID da entrada a ser atualizada.
     * @param attendanceEntryDto O DTO com os dados atualizados.
     * @return `Call<AttendanceEntryDto>` com a entrada atualizada.
     */
    @PUT("entradasAtendimento/{id}")
    Call<AttendanceEntryDto> updateAttendanceEntry(@Path("id") Long entryId, @Body AttendanceEntryDto attendanceEntryDto);

    /**
     * Remove uma entrada de atendimento. [cite: 52]
     * @param entryId O ID da entrada a ser deletada.
     * @return `Call<Void>`.
     */
    @DELETE("entradasAtendimento/{id}")
    Call<Void> deleteAttendanceEntry(@Path("id") Long entryId);


    // --- Endpoints do FollowUpController (Retornos) ---
    // Documentação da API relevante: [cite: 57, 60, 62, 66, 70, 74]

    /**
     * Lista todos os agendamentos de follow-up (retornos). [cite: 60, 61]
     * O DTO no Android é `FollowUpDto`.
     * @return `Call<List<FollowUpDto>>`.
     */
    @GET("retornos")
    Call<List<FollowUpDto>> listAllFollowUps();

    /**
     * Lista os follow-ups de um paciente específico. [cite: 63, 64]
     * @param pacienteId O ID do paciente.
     * @return `Call<List<FollowUpDto>>`.
     */
    @GET("retornos/pacientes/{pacienteId}")
    Call<List<FollowUpDto>> listFollowUpsByPaciente(@Path("pacienteId") Long pacienteId);

    /**
     * Cria um novo agendamento de follow-up. [cite: 67, 68]
     * @param followUpDto O DTO do follow-up a ser criado.
     * @return `Call<FollowUpDto>` com o follow-up criado.
     */
    @POST("retornos")
    Call<FollowUpDto> createFollowUp(@Body FollowUpDto followUpDto);

    /**
     * Atualiza um follow-up existente. [cite: 71, 72]
     * @param followUpId O ID do follow-up a ser atualizado.
     * @param followUpDto O DTO com os dados atualizados.
     * @return `Call<FollowUpDto>` com o follow-up atualizado.
     */
    @PUT("retornos/{id}")
    Call<FollowUpDto> updateFollowUp(@Path("id") Long followUpId, @Body FollowUpDto followUpDto);

    /**
     * Remove um follow-up. [cite: 75]
     * @param followUpId O ID do follow-up a ser deletado.
     * @return `Call<Void>`.
     */
    @DELETE("retornos/{id}")
    Call<Void> deleteFollowUp(@Path("id") Long followUpId);


    // --- Endpoints para Specialty (Especialidades) ---
    // Equipe, ATENÇÃO: A descrição da API que recebi não listava um `SpecialtyController` explicitamente.
    // No entanto, o `AttendanceEntry` usa `Specialty`[cite: 249], e temos `SpecialtyDto` [cite: 222] e `SpecialtyRepository` na API. [cite: 413]
    // Portanto, estou ASSUMINDO que existem endpoints como `/specialties` para buscar esses dados.
    // Se esses endpoints não existirem no backend, estas chamadas aqui vão falhar. Verifiquem com o time de backend!

    /**
     * Lista todas as especialidades médicas cadastradas.
     * (Endpoint assumido como "specialties", verifiquem com o backend).
     * @return `Call<List<SpecialtyDto>>`.
     */
    @GET("specialties")
    Call<List<SpecialtyDto>> listAllSpecialties();

    /**
     * Busca uma especialidade médica específica pelo seu ID.
     * (Endpoint assumido como "specialties/{id}", verifiquem com o backend).
     * @param specialtyId O ID da especialidade.
     * @return `Call<SpecialtyDto>`.
     */
    @GET("specialties/{id}")
    Call<SpecialtyDto> getSpecialtyById(@Path("id") Long specialtyId);
}