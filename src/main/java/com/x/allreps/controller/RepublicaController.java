package com.x.allreps.controller;

import com.x.allreps.exception.ErrorResponse;
import com.x.allreps.exception.ResourceNotFoundException;
import com.x.allreps.model.Republica;
import com.x.allreps.model.User;
import com.x.allreps.model.dto.response.RepublicaResponse;
import com.x.allreps.service.ImageUploadService;
import com.x.allreps.service.RepublicaService;
import com.x.allreps.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Importações do Swagger
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/republicas")
@Tag(name = "Repúblicas", description = "Endpoints para gerenciamento de repúblicas")
public class RepublicaController {

    private static final Logger logger = LoggerFactory.getLogger(RepublicaController.class);
    private final RepublicaService republicaService;
    private final ImageUploadService imageUploadService;

    private final UserService userService;

    public RepublicaController(RepublicaService republicaService, ImageUploadService imageUploadService, UserService userService) {
        this.republicaService = republicaService;
        this.imageUploadService = imageUploadService;
        this.userService = userService;
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Upload de imagens da república",
            description = "Faz o upload de imagens para a república especificada.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Imagens carregadas com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(type = "string", example = "http://url-da-imagem")))),
            @ApiResponse(responseCode = "404", description = "República não encontrada", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping(value = "/{id}/upload-images", consumes = "multipart/form-data")
    public ResponseEntity<List<String>> uploadRepublicaImages(
            @Parameter(description = "ID da república", required = true) @PathVariable Long id,
            @RequestParam("images") MultipartFile[] images) throws IOException {

        Optional<Republica> republica = republicaService.buscarPorId(id);

        if (republica.isPresent()) {
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile image : images) {
                String imageUrl = imageUploadService.uploadImage(image);
                imageUrls.add(imageUrl);
            }

            republica.get().getFotos().addAll(imageUrls);
            republicaService.salvar(republica.get());

            return ResponseEntity.ok(imageUrls);
        }
        return ResponseEntity.notFound().build();
    }



    @CrossOrigin("*")
    @PostMapping("/create")
    @Operation(
            summary = "Criar uma nova república",
            description = "Cria uma nova república com os detalhes fornecidos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "República criada com sucesso",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(responseCode = "400", description = "Erro na validação dos dados", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    public ResponseEntity<?> criarRepublica(
            @Parameter(
                    description = "Dados da república a ser criada",
                    required = true,
                    schema = @Schema(implementation = Republica.class)
            )
            @Valid @RequestBody Republica republicaRequest,
            BindingResult bindingResult,
            @AuthenticationPrincipal UserDetails currentUserDetails) {

        logger.info("Recebida requisição para criar república: {}", republicaRequest.getNome());


        // Obter o usuário autenticado
        Optional<User> anunciante = userService.findByEmail(currentUserDetails.getUsername());

        // Verificar se o usuário já possui uma república cadastrada
        Optional<Republica> existingRepublica = republicaService.findByAnunciante(anunciante);

        if (existingRepublica.isPresent()) {
            logger.warn("Usuário {} já possui uma república cadastrada.", anunciante.get().getEmail());
            return ResponseEntity.badRequest().body(new ErrorResponse(400, "O usuário já possui uma república cadastrada.", null, LocalDateTime.now()));
        }

        // Mapear o DTO para a entidade
        Republica republica = new Republica();
        republica.setNome(republicaRequest.getNome());
        republica.setDescricao(republicaRequest.getDescricao());
        republica.setRegras(republicaRequest.getRegras());
        republica.setValor(republicaRequest.getValor());
        republica.setVagasDisponiveis(republicaRequest.getVagasDisponiveis());
        republica.setGeneroPreferencial(republicaRequest.getGeneroPreferencial());
        republica.setDataAnuncio(LocalDateTime.now());
        republica.setComodidades(republicaRequest.getComodidades());
        republica.setLocalizacao(republicaRequest.getLocalizacao());
        republica.setAnunciante(anunciante.get());

        Republica novaRepublica = republicaService.salvar(republica);
        logger.info("República criada com sucesso: {}", novaRepublica.getId());

        // Mapear a entidade para um DTO de resposta
        RepublicaResponse republicaResponse = new RepublicaResponse(novaRepublica);

        return ResponseEntity.ok(republicaResponse);
    }


    @CrossOrigin("*")
    @Operation(
            summary = "Atualizar uma república",
            description = "Atualiza os detalhes de uma república existente.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "República atualizada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Republica.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "República não encontrada", content = @Content)
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Republica> atualizarRepublica(
            @Parameter(description = "ID da república a ser atualizada", required = true)
            @PathVariable Long id,
            @Parameter(
                    description = "Objeto República com os novos dados",
                    required = true,
                    schema = @Schema(implementation = Republica.class)
            )
            @RequestBody Republica republicaAtualizada) {

        logger.info("Recebida requisição para atualizar a república com ID: {}", id);
        Republica republicaExistente = republicaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("República não encontrada com ID: " + id));

        republicaAtualizada.setId(republicaExistente.getId());
        Republica republicaSalva = republicaService.salvar(republicaAtualizada);
        logger.info("República atualizada com sucesso: ID = {}", republicaSalva.getId());
        return ResponseEntity.ok(republicaSalva);
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Listar repúblicas com filtros",
            description = "Retorna uma lista de repúblicas cadastradas, permitindo aplicar filtros por cidade, valor, gênero preferencial, número de vagas disponíveis e comodidades.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista de repúblicas obtida com sucesso",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = Republica.class)))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @GetMapping
    public ResponseEntity<List<Republica>> listarRepublicas(
            @Parameter(description = "Cidade da república para filtrar", example = "São Paulo")
            @RequestParam(value = "cidade", required = false) String cidade,
            @Parameter(description = "Valor máximo da mensalidade para filtrar", example = "1000.00")
            @RequestParam(value = "valorMaximo", required = false) Double valorMaximo,
            @Parameter(description = "Gênero preferencial da república para filtrar", example = "Feminino")
            @RequestParam(value = "generoPreferencial", required = false) String generoPreferencial,
            @Parameter(description = "Número mínimo de vagas disponíveis", example = "1")
            @RequestParam(value = "vagasDisponiveis", required = false) Integer vagasDisponiveis,
            @Parameter(description = "Comodidades desejadas (separadas por vírgula)", example = "Wi-Fi,Lavanderia")
            @RequestParam(value = "comodidades", required = false) List<String> comodidades
    ) {
        logger.info("Listando repúblicas com filtros - Cidade: {}, Valor Máximo: {}, Gênero Preferencial: {}, Vagas Disponíveis: {}, Comodidades: {}",
                cidade, valorMaximo, generoPreferencial, vagasDisponiveis, comodidades);

        List<Republica> republicas = republicaService.listarComFiltros(cidade, valorMaximo, generoPreferencial, vagasDisponiveis, comodidades);
        return ResponseEntity.ok(republicas);
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Obter detalhes de uma república",
            description = "Retorna os detalhes de uma república específica.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "República obtida com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Republica.class))
            ),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "República não encontrada", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Republica> obterRepublica(
            @Parameter(description = "ID da república a ser obtida", required = true)
            @PathVariable Long id) {

        logger.info("Recebida requisição para obter a república com ID: {}", id);
        Republica republica = republicaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("República não encontrada com ID: " + id));

        logger.info("República encontrada: ID = {}", id);
        return ResponseEntity.ok(republica);
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Remover uma república",
            description = "Remove uma república específica.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "República removida com sucesso", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "República não encontrada", content = @Content)
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> removerRepublica(
            @Parameter(description = "ID da república a ser removida", required = true)
            @PathVariable Long id) {

        logger.info("Recebida requisição para remover a república com ID: {}", id);
        Republica republica = republicaService.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("República não encontrada com ID: " + id));

        republicaService.remover(id);
        logger.info("República removida com sucesso: ID = {}", id);
        return ResponseEntity.ok().build();
    }

}
