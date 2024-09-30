package com.x.allreps.controller;

import com.x.allreps.exception.ResourceNotFoundException;
import com.x.allreps.model.User;
import com.x.allreps.repository.UserRepository;
import com.x.allreps.service.EmailService;
import com.x.allreps.service.ImageUploadService;
import com.x.allreps.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/user-profile")
@Tag(name = "Perfil do Usuário", description = "Endpoints para gerenciamento do perfil do usuário")
public class UserProfileController {

    private final UserService userService;
    private final ImageUploadService imageUploadService;

    private final UserRepository userRepository;

    private final EmailService emailService;
    public UserProfileController(UserService userService, ImageUploadService imageUploadService, UserRepository userRepository, EmailService emailService) {
        this.userService = userService;
        this.imageUploadService = imageUploadService;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }
    @CrossOrigin("*")
    @Operation(
            summary = "Obter perfil do usuário por ID",
            description = "Retorna o perfil do usuário especificado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil do usuário obtido com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @GetMapping("/{userId}")
    public ResponseEntity<User> getUserProfileById(@PathVariable Long userId) {
        User user = userService.findById(userId);
        return ResponseEntity.ok(user);
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Upload de imagem de perfil",
            description = "Faz o upload de uma nova imagem de perfil para o usuário autenticado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Imagem de perfil carregada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(type = "string", example = "http://url-da-imagem"))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Erro no upload da imagem", content = @Content)
    })
    @PostMapping("/upload-profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @RequestParam("image") MultipartFile image,
            @AuthenticationPrincipal UserDetails currentUserDetails) throws IOException {

        Optional<User> user = userService.findByEmail(currentUserDetails.getUsername());

        if (user.isPresent()) {
            String imageUrl = imageUploadService.uploadImage(image);
            user.get().setProfileImageUrl(imageUrl);
            userService.updateUser(user.get());

            return ResponseEntity.ok(imageUrl);
        }
        return ResponseEntity.notFound().build();
    }
    @CrossOrigin("*")
    @Operation(
            summary = "Atualizar perfil do usuário",
            description = "Atualiza as informações do perfil do usuário autenticado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Perfil do usuário atualizado com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = User.class))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content)
    })
    @PutMapping("/update")
    public ResponseEntity<User> updateUserProfile(@RequestBody User updatedUser, @AuthenticationPrincipal UserDetails currentUserDetails) {
        // Agora buscamos o usuário existente pelo email
        Optional<User> existingUser = userService.findByEmail(currentUserDetails.getUsername()); // Certifique-se de que este método está buscando pelo email

        if (existingUser.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado: " + currentUserDetails.getUsername());
        }

        // Atualizar os campos do usuário existente com os dados fornecidos
        existingUser.get().setEmail(updatedUser.getEmail());
        existingUser.get().setName(updatedUser.getName());
        existingUser.get().setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.get().setUsername(updatedUser.getUsername());
        existingUser.get().setSurname(updatedUser.getSurname());
        existingUser.get().setPassword(updatedUser.getPassword());

        User savedUser = userService.updateUser(existingUser.get());
        return ResponseEntity.ok(savedUser);
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Excluir perfil do usuário",
            description = "Exclui o perfil do usuário autenticado.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Perfil do usuário excluído com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteUserProfile(@AuthenticationPrincipal UserDetails currentUserDetails) {
        // Buscar o usuário pelo email do usuário autenticado
        Optional<User> user = userService.findByEmail(currentUserDetails.getUsername()); // Aqui `getUsername()` retorna o email


        // Deletar o usuário
        userService.deleteUser(user.get().getId());
        return ResponseEntity.noContent().build();
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Listar todos os usuários",
            description = "Retorna uma lista de todos os usuários cadastrados.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de usuários obtida com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = User.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userRepository.findAll(); // Certifique-se de que o método findAll() está implementado no UserService
        return ResponseEntity.ok(users);
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> sendEmail(@RequestBody String email) {
        emailService.sendEmail(email);
        return ResponseEntity.ok().body("Email enviado com sucesso.");
    }
}
