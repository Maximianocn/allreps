package com.x.allreps.model.dto.response;

import com.x.allreps.model.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Optional;

@Schema(description = "Dados do usuário")
@Data
public class UserResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Email do usuário", example = "usuario@example.com")
    private String email;

    @Schema(description = "Nome de usuário", example = "usuario123")
    private String username;

    @Schema(description = "Nome", example = "João")
    private String name;

    @Schema(description = "Sobrenome", example = "Silva")
    private String surname;

    @Schema(description = "Número de telefone", example = "11999999999")
    private String phoneNumber;

    public UserResponse(Optional<User> user) {
        this.id = user.get().getId();
        this.email = user.get().getEmail();
        this.username = user.get().getUsername();
        this.name = user.get().getName();
        this.surname = user.get().getSurname();
        this.phoneNumber = user.get().getPhoneNumber();
    }
}
