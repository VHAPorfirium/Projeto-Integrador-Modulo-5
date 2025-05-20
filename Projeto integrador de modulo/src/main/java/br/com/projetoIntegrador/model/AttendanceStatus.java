package br.com.projetoIntegrador.model;

// Essa enum tem a funcionalidade de definir os possíveis estados de um atendimento.
public enum AttendanceStatus {
    AGUARDANDO,    // Aguardando chamada
    CHAMADO,     // Chamado, aguardando confirmação
    CONFIRMADO,  // Paciente confirmou presença
    NAO_COMPARECEU,    // Paciente não compareceu
    ATENDIDO    // Atendimento realizado
}
