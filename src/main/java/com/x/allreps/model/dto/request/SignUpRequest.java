package com.x.allreps.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Modelo para requisição de registro de novo usuário")
@Data
public class SignUpRequest {

    @NotBlank(message = "O email é obrigatório.")
    @Email(message = "Email inválido.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String password;

    @NotBlank(message = "O nome de usuário é obrigatório.")
    private String username;

    @NotBlank(message = "O nome é obrigatório.")
    private String name;

    @NotBlank(message = "O sobrenome é obrigatório.")
    private String surname;

    @NotBlank(message = "O número de telefone é obrigatório.")
    @Pattern(regexp = "\\d{10,11}", message = "Número de telefone inválido.")
    private String phoneNumber;

}
