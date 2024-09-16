package com.x.allreps.repository;

import com.x.allreps.model.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
    // Métodos de consulta personalizados, se necessário
}

