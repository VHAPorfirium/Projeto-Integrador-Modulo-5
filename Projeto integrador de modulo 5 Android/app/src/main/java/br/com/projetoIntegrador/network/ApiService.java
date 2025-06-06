package br.com.projetoIntegrador.network;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query; // Adicionado para query params como em /pacientes?cpf=...

public interface ApiService {

    // Endpoints do TokenController
    @POST("tokens")
    Call<DeviceToken> registerToken(@Body TokenRequest req); // API retorna DeviceToken

    @GET("tokens/pacientes/{pacienteId}")
    Call<List<DeviceToken>> listTokensByPaciente(@Path("pacienteId") Long pacienteId); // Caminho da API

    @DELETE("tokens/{id}")
    Call<Void> deleteToken(@Path("id") Long tokenId);

    // Endpoints do NotificationController
    @POST("notificacoes") // Caminho da API é /notificacoes
    Call<String> sendNotification(@Body NotificationRequest req); // API retorna String

    // Endpoints do PacienteController
    @POST("pacientes")
    Call<PacienteDto> createPaciente(@Body PacienteDto pacienteDto);

    @GET("pacientes")
    Call<List<PacienteDto>> listAllPacientes();

    // Adicionado para buscar paciente por CPF (para auto-complete na recepção)
    // A API backend em PacienteRepository tem findByCpf, mas não há endpoint GET /pacientes?cpf=... no Controller.
    // Supondo que você adicionaria um endpoint assim no backend:
    // @GetMapping(params = "cpf")
    // public ResponseEntity<PacienteDto> getPacienteByCpf(@RequestParam String cpf) { ... }
    // OU se o endpoint listAllPacientes() for usado e filtrado no cliente (menos eficiente).
    // Por enquanto, vou manter o getPacienteById e o listAllPacientes.
    // Se precisar de busca por CPF, o ideal é ter um endpoint específico.
    // Para o exemplo, vamos assumir que a busca por CPF pode ser feita filtrando a lista completa
    // ou adaptando a UI para buscar por ID se o CPF for conhecido após uma busca inicial.

    @GET("pacientes/{id}")
    Call<PacienteDto> getPacienteById(@Path("id") Long pacienteId);

    @PUT("pacientes/{id}")
    Call<PacienteDto> updatePaciente(@Path("id") Long pacienteId, @Body PacienteDto pacienteDto);

    @DELETE("pacientes/{id}")
    Call<Void> deletePaciente(@Path("id") Long pacienteId);

    // Endpoints do AttendanceEntryController (Fila)
    // Caminho da API é /entradasAtendimento
    @GET("entradasAtendimento")
    Call<List<AttendanceEntryDto>> listAllAttendanceEntries();

    // Endpoint para buscar a fila de um paciente específico (necessário para "Sua Posição")
    // A API backend atual não parece ter um endpoint direto para "minha posição na fila" ou "fila por paciente".
    // O frontend.pdf não especifica esse endpoint.
    // Seria necessário adicionar no backend, por exemplo: GET /entradasAtendimento/paciente/{pacienteId}
    // Ou GET /entradasAtendimento/posicao/{pacienteId}/especialidade/{specialtyId}
    // Por enquanto, o listAllAttendanceEntries() pode ser usado e filtrado no cliente, ou adaptado.

    @GET("entradasAtendimento/{id}")
    Call<AttendanceEntryDto> getAttendanceEntryById(@Path("id") Long entryId);

    @POST("entradasAtendimento")
    Call<AttendanceEntryDto> createAttendanceEntry(@Body AttendanceEntryDto attendanceEntryDto);

    // Endpoint para confirmação na fila (sugerido na descrição da tela 5.2)
    // POST /fila/confirmacao -> Isso implicaria um endpoint como /entradasAtendimento/{id}/confirmar
    @POST("entradasAtendimento/{id}/confirmar") // Endpoint hipotético
    Call<AttendanceEntryDto> confirmAttendance(@Path("id") Long entryId);

    // Endpoint para rechamada/não comparecimento (sugerido na tela 5.2)
    // POST /fila/rechamada -> Isso implicaria um endpoint como /entradasAtendimento/{id}/rechamada ou /entradasAtendimento/{id}/naoCompareceu
    @POST("entradasAtendimento/{id}/naoCompareceu") // Endpoint hipotético
    Call<AttendanceEntryDto> markAsNoShow(@Path("id") Long entryId);


    @PUT("entradasAtendimento/{id}")
    Call<AttendanceEntryDto> updateAttendanceEntry(@Path("id") Long entryId, @Body AttendanceEntryDto attendanceEntryDto);

    @DELETE("entradasAtendimento/{id}")
    Call<Void> deleteAttendanceEntry(@Path("id") Long entryId);

    // Endpoints do FollowUpController (Retornos)
    // Caminho da API é /retornos
    @GET("retornos")
    Call<List<FollowUpDto>> listAllFollowUps();

    @GET("retornos/pacientes/{pacienteId}")
    Call<List<FollowUpDto>> listFollowUpsByPaciente(@Path("pacienteId") Long pacienteId); // Caminho da API

    // Endpoint para horários disponíveis (sugerido na tela 5.3)
    // GET /retornos/disponiveis -> precisaria ser adicionado ao backend
    // Poderia receber data como query param: /retornos/disponiveis?data=YYYY-MM-DD
    @GET("retornos/disponiveis") // Endpoint hipotético
    Call<List<String>> getAvailableFollowUpSlots(@Query("data") String date);


    @POST("retornos")
    Call<FollowUpDto> createFollowUp(@Body FollowUpDto followUpDto);

    @PUT("retornos/{id}")
    Call<FollowUpDto> updateFollowUp(@Path("id") Long followUpId, @Body FollowUpDto followUpDto);

    @DELETE("retornos/{id}")
    Call<Void> deleteFollowUp(@Path("id") Long followUpId);

    // Endpoints de Especialidade (Assumidos com base na estrutura e necessidades da API)
    // A documentação da API fornecida não lista explicitamente um SpecialtyController,
    // mas SpecialtyDto e o modelo/repositório Specialty existem.
    // Estes são endpoints comuns. Se não estiverem disponíveis, estas chamadas falharão.
    @GET("specialties") // Nome do endpoint assumido
    Call<List<SpecialtyDto>> listAllSpecialties();

    @GET("specialties/{id}") // Nome do endpoint assumido
    Call<SpecialtyDto> getSpecialtyById(@Path("id") Long specialtyId);

    // Endpoints de Autenticação (com base no AuthController do backend )
    @POST("api/auth/login/paciente")
    Call<LoginResponseDto> loginPaciente(@Body LoginRequestDto loginRequestDto);
    @POST("api/auth/login/funcionario")
    Call<LoginResponseDto> loginFuncionario(@Body LoginRequestDto loginRequestDto);

    // Endpoint para Indicadores (AdministradorActivity) - Hipotético
    // GET /indicadores
    // A API backend não possui este controller/endpoint. Seria necessário criá-lo.
    // O DTO de resposta dependeria dos dados dos indicadores. Ex: Map<String, Object> ou um DTO específico.
    @GET("indicadores") // Endpoint hipotético
    Call<Map<String, Object>> getIndicadores();
}