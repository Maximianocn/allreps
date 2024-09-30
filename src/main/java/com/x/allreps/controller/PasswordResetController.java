package com.x.allreps.controller;

import com.x.allreps.model.dto.request.PasswordResetCodeRequest;
import com.x.allreps.model.dto.request.PasswordResetRequest;
import com.x.allreps.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Reset de Senha", description = "Endpoints para solicitar e redefinir senha de usuário")
public class PasswordResetController {

    private final UserService userService;

    public PasswordResetController(UserService userService) {
        this.userService = userService;
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Solicitar redefinição de senha",
            description = "Envia um código de redefinição de senha para o e-mail fornecido."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Código de redefinição de senha enviado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Código de redefinição de senha enviado para o e-mail."))
            ),
            @ApiResponse(responseCode = "400", description = "E-mail não encontrado ou dados inválidos", content = @Content)
    })
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody PasswordResetRequest request) {
        userService.generatePasswordResetCode(request.getEmail());
        return ResponseEntity.ok("Código de redefinição de senha enviado para o e-mail.");
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Redefinir senha",
            description = "Redefine a senha do usuário usando o código de redefinição."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Senha redefinida com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Senha redefinida com sucesso."))
            ),
            @ApiResponse(responseCode = "400", description = "Código de redefinição inválido ou dados inválidos", content = @Content)
    })
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetCodeRequest request) {
        userService.resetPassword(request.getEmail(), request.getResetCode(), request.getNewPassword());
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}
