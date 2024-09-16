package com.x.allreps.controller;

import com.x.allreps.exception.InvalidCredentialsException;
import com.x.allreps.model.User;
import com.x.allreps.model.dto.LoginRequest;
import com.x.allreps.model.dto.SignUpRequest;
import com.x.allreps.security.JwtUtil;
import com.x.allreps.security.UserDetailsImpl;
import com.x.allreps.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// Importações do Swagger
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.parameters.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          UserService userService,
                          JwtUtil jwtUtil,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Autenticar usuário",
            description = "Autentica um usuário com as credenciais fornecidas e retorna um token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticação bem-sucedida",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6..."))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content)
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(
                    description = "Credenciais de login",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @RequestBody LoginRequest loginRequest) {

        logger.info("Tentativa de autenticação para o usuário: {}", loginRequest.getUsername());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(), loginRequest.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            String jwt = jwtUtil.generateJwtToken(userDetails);

            logger.info("Usuário autenticado com sucesso: {}", loginRequest.getUsername());

            return ResponseEntity.ok(jwt);
        } catch (AuthenticationException e) {
            logger.error("Falha na autenticação para o usuário: {}", loginRequest.getUsername());
            throw new InvalidCredentialsException("Credenciais inválidas");
        }
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Registra um novo usuário com as informações fornecidas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "Usuário registrado com sucesso!"))
            ),
            @ApiResponse(responseCode = "400", description = "Usuário já existe ou dados inválidos", content = @Content)
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @Parameter(
                    description = "Dados para registro do usuário",
                    required = true,
                    schema = @Schema(implementation = SignUpRequest.class)
            )
            @org.springframework.web.bind.annotation.RequestBody SignUpRequest signUpRequest) {

        logger.info("Tentativa de registro para o usuário: {}", signUpRequest.getUsername());

        // O método userService.save(user) lançará UsernameAlreadyExistsException se o usuário já existir
        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        userService.save(user);

        logger.info("Usuário registrado com sucesso: {}", signUpRequest.getUsername());

        return ResponseEntity.ok("Usuário registrado com sucesso!");
    }
}
