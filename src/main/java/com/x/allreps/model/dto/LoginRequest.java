package com.x.allreps.model.dto;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para requisição de login")
@Data
public class LoginRequest {

    @Schema(description = "Nome de usuário", example = "usuario_exemplo", required = true)
    private String username;

    @Schema(description = "Senha do usuário", example = "senha123", required = true)
    private String password;
}

