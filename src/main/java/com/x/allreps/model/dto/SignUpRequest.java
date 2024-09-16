package com.x.allreps.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para requisição de registro de novo usuário")
@Data
public class SignUpRequest {

    @Schema(description = "Nome de usuário", example = "novo_usuario", required = true)
    @JsonProperty("username")
    private String username;

    @Schema(description = "Senha do usuário", example = "senha123", required = true)
    @JsonProperty("password")
    private String password;

}