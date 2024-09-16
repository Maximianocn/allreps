package com.x.allreps.model;

import jakarta.persistence.*;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

@Entity
@Table(name = "localizacoes")
@Data
@Schema(description = "Modelo que representa a localização de uma república")
public class Localizacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único da localização", example = "1")
    private Long id;

    @Schema(description = "Endereço completo", example = "Rua das Flores, 123", required = true)
    private String endereco;

    @Schema(description = "Bairro", example = "Centro", required = true)
    private String bairro;

    @Schema(description = "Cidade", example = "São Paulo", required = true)
    private String cidade;

    @Schema(description = "Estado", example = "SP", required = true)
    private String estado;

    @Schema(description = "CEP", example = "01000-000", required = true)
    private String cep;

    @Schema(description = "Latitude geográfica", example = "-23.550520")
    private Double latitude;

    @Schema(description = "Longitude geográfica", example = "-46.633308")
    private Double longitude;

    @Schema(description = "Ponto de referência", example = "Próximo ao metrô Sé")
    private String referencia;
}