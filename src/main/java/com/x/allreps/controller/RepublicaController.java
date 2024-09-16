package com.x.allreps.controller;

import com.x.allreps.exception.ResourceNotFoundException;
import com.x.allreps.model.Republica;
import com.x.allreps.service.RepublicaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

// Importações do Swagger
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.parameters.*;
import io.swagger.v3.oas.annotations.security.*;
import io.swagger.v3.oas.annotations.tags.*;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/republicas")
@Tag(name = "Repúblicas", description = "Endpoints para gerenciamento de repúblicas")
public class RepublicaController {

    private static final Logger logger = LoggerFactory.getLogger(RepublicaController.class);
    private final RepublicaService republicaService;

    public RepublicaController(RepublicaService republicaService) {
        this.republicaService = republicaService;
    }

    @CrossOrigin("*")
    @Operation(
            summary = "Criar uma nova república",
            description = "Cria uma nova república com os detalhes fornecidos.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "República criada com sucesso",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Republica.class))
            ),
            @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content),
            @ApiResponse(responseCode = "401", description = "Não autorizado", content = @Content)
    })
    @PostMapping("/create")
    public ResponseEntity<Republica> criarRepublica(
            @Parameter(
                    description = "Objeto República a ser criado",
                    required = true,
                    schema = @Schema(implementation = Republica.class)
            )
            @org.springframework.web.bind.annotation.RequestBody Republica republica) {

        logger.info("Recebida requisição para criar república com ID: {}", republica.getId());
        Republica novaRepublica = republicaService.salvar(republica);
        logger.info("República criada com sucesso: {}", republica.getId());
        return ResponseEntity.ok(novaRepublica);
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
            summary = "Listar todas as repúblicas",
            description = "Retorna uma lista de todas as repúblicas cadastradas.",
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
    public ResponseEntity<List<Republica>> listarRepublicas() {
        List<Republica> republicas = republicaService.listarTodas();
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
