package com.x.allreps.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "republicas")
@Data
@Schema(description = "Modelo que representa uma república")
public class Republica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único da república", example = "1")
    private Long id;

    @Schema(description = "Nome da república", example = "República Estudantil Alpha", required = true)
    private String nome;

    @Column(length = 2000)
    @Schema(description = "Descrição detalhada da república", example = "República próxima à universidade com ambiente tranquilo.", required = true)
    private String descricao;

    @Column(length = 1000)
    @Schema(description = "Regras da república", example = "Não é permitido festas após as 22h", required = true)
    private String regras;

    @Schema(description = "Valor mensal da vaga", example = "800.00", required = true)
    private Double valor;

    @Schema(description = "Número de vagas disponíveis", example = "2", required = true)
    private Integer vagasDisponiveis;

    @Schema(description = "Gênero preferencial dos moradores", example = "Misto", required = true)
    private String generoPreferencial;

    @Schema(description = "Data de publicação do anúncio", example = "2023-10-01T14:30:00")
    private LocalDateTime dataAnuncio;

    @ElementCollection
    @Schema(description = "Lista de comodidades oferecidas", example = "[\"Wi-Fi\", \"Lavanderia\", \"Cozinha equipada\"]")
    private List<String> comodidades;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "localizacao_id")
    @Schema(description = "Localização da república", required = true)
    private Localizacao localizacao;

    @ManyToOne
    @JoinColumn(name = "anunciante_id")
    @Schema(description = "Usuário que anunciou a república")
    @JsonIgnore
    private User anunciante;

    @ElementCollection
    @CollectionTable(name = "republica_fotos", joinColumns = @JoinColumn(name = "republica_id"))
    @Column(name = "foto_url")
    private List<String> fotos = new ArrayList<>();
}