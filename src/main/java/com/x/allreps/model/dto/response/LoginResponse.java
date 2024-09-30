package com.x.allreps.model.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "Resposta de login contendo o token JWT e os dados do usuário")
@Data
public class LoginResponse {

    @Schema(description = "Token JWT para autenticação", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6...")
    private String token;

    @Schema(description = "Dados do usuário autenticado")
    private UserResponse user;

    public LoginResponse(String token, UserResponse user) {
        this.token = token;
        this.user = user;
    }

    // Getters e Setters
}
