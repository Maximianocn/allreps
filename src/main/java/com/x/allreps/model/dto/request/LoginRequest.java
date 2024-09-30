package com.x.allreps.model.dto.request;

import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para requisição de login")
@Data
public class LoginRequest {

    @Schema(description = "Email do usuário", example = "usuario@gmail.com", required = true)
    private String email;

    @Schema(description = "Senha do usuário", example = "senha123", required = true)
    private String password;
}

