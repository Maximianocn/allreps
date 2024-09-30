package com.x.allreps.model.dto.response;

import com.x.allreps.model.Localizacao;
import com.x.allreps.model.Republica;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RepublicaResponse {

    private Long id;
    private String nome;
    private String descricao;
    private String regras;
    private Double valor;
    private Integer vagasDisponiveis;
    private String generoPreferencial;
    private LocalDateTime dataAnuncio;
    private List<String> comodidades;
    private Localizacao localizacao;
    private Long anuncianteId;

    public RepublicaResponse(Republica republica) {
        this.id = republica.getId();
        this.nome = republica.getNome();
        this.descricao = republica.getDescricao();
        this.regras = republica.getRegras();
        this.valor = republica.getValor();
        this.vagasDisponiveis = republica.getVagasDisponiveis();
        this.generoPreferencial = republica.getGeneroPreferencial();
        this.dataAnuncio = republica.getDataAnuncio();
        this.comodidades = republica.getComodidades();
        this.localizacao = republica.getLocalizacao();
        this.anuncianteId = republica.getAnunciante().getId();
    }

    // Getters e Setters
}
