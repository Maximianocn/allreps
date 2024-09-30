package com.x.allreps.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public class PasswordResetCodeRequest {

    @Schema(description = "E-mail do usuário", example = "usuario@example.com")
    private String email;

    @Schema(description = "Código de redefinição recebido por e-mail", example = "123456")
    private String resetCode;

    @Schema(description = "Nova senha do usuário", example = "novaSenha123")
    private String newPassword;
    // Getters e setters
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getResetCode() {
        return resetCode;
    }
    public void setResetCode(String resetCode) {
        this.resetCode = resetCode;
    }
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
