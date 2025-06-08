package br.com.projetoIntegrador.dto;

// Usando record para um DTO de resposta simples e imutável
public record LoginResponseDto(
        boolean success,
        String message,
        String userType,
        Long userId,
        String userName) {
}