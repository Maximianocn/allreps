package com.x.allreps.controller;

import com.x.allreps.exception.ErrorResponse;
import com.x.allreps.model.User;
import com.x.allreps.model.dto.request.LoginRequest;

import com.x.allreps.model.dto.request.SignUpRequest;

import com.x.allreps.model.dto.response.LoginResponse;
import com.x.allreps.model.dto.response.UserResponse;
import com.x.allreps.security.JwtUtil;
import com.x.allreps.security.UserDetailsImpl;
import com.x.allreps.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

// Importações do Swagger
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints para autenticação e registro de usuários")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @CrossOrigin("*")
    @Operation(
            summary = "Autenticar usuário",
            description = "Autentica um usuário com as credenciais fornecidas e retorna um token JWT."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Autenticação bem-sucedida",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(
            @Parameter(
                    description = "Credenciais de login",
                    required = true,
                    schema = @Schema(implementation = LoginRequest.class)
            )
            @Valid @RequestBody LoginRequest loginRequest,
            BindingResult bindingResult) {

        logger.info("Tentativa de autenticação para o usuário: {}", loginRequest.getEmail());


        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(), loginRequest.getPassword())
            );

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            String jwt = jwtUtil.generateJwtToken(userDetails);

            logger.info("Usuário autenticado com sucesso: {}", loginRequest.getEmail());

            // Obter o usuário a partir do UserDetails
            Optional<User> user = userService.findByEmail(userDetails.getUsername());
            UserResponse userResponse = new UserResponse(user);

            // Criar a resposta de login
            LoginResponse loginResponse = new LoginResponse(jwt, userResponse);

            return ResponseEntity.ok(loginResponse);
        } catch (AuthenticationException e) {
            logger.error("Falha na autenticação para o usuário: {}", loginRequest.getEmail(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse(401, "Credenciais inválidas", null, LocalDateTime.now()));
        }
    }

    @CrossOrigin("*")
    @PostMapping("/register")
    @Operation(
            summary = "Registrar novo usuário",
            description = "Registra um novo usuário com as informações fornecidas."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Usuário registrado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
            ),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> registerUser(
            @Parameter(
                    description = "Dados para registro do usuário",
                    required = true,
                    schema = @Schema(implementation = SignUpRequest.class)
            )
            @Valid @RequestBody SignUpRequest signUpRequest,
            BindingResult bindingResult) {

        logger.info("Tentativa de registro para o usuário: {}", signUpRequest.getEmail());


        // Verificar se o email já está registrado
        if (userService.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Email já registrado.", null, LocalDateTime.now()));
        }

        // Verificar se o número de telefone já está registrado
        if (userService.existsByPhoneNumber(signUpRequest.getPhoneNumber())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(400, "Número de celular já registrado.", null, LocalDateTime.now()));
        }

        // Criar novo usuário
        User user = new User();
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(signUpRequest.getPassword());
        user.setUsername(signUpRequest.getUsername());
        user.setName(signUpRequest.getName());
        user.setSurname(signUpRequest.getSurname());
        user.setPhoneNumber(signUpRequest.getPhoneNumber());
        userService.save(user);

        logger.info("Usuário registrado com sucesso: {}", signUpRequest.getEmail());

        // Criar a resposta do usuário
        UserResponse userResponse = new UserResponse(Optional.of(user));

        return ResponseEntity.ok(userResponse);
    }
}
