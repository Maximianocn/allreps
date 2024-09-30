package com.x.allreps.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Resposta de erro contendo detalhes sobre o problema ocorrido")
public class ErrorResponse {

    @Schema(description = "Código de status HTTP", example = "400")
    private int statusCode;

    @Schema(description = "Mensagem de erro", example = "Erro de validação")
    private String message;

    @Schema(description = "Detalhes adicionais sobre o erro")
    private List<String> details;

    @Schema(description = "Timestamp do erro", example = "2023-09-30T15:00:00")
    private LocalDateTime timestamp;

    public ErrorResponse(int statusCode, String message, List<String> details, LocalDateTime timestamp) {
        this.statusCode = statusCode;
        this.message = message;
        this.details = details;
        this.timestamp = timestamp;
    }

    // Getters e Setters
}
