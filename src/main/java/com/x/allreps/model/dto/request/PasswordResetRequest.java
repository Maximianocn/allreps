package com.x.allreps.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class PasswordResetRequest {
    @Schema(description = "E-mail do usuário que solicitou a redefinição de senha", example = "usuario@example.com")
    private String email;

    // Getters e setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
}
