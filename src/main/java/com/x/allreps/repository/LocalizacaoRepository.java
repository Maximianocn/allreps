package com.x.allreps.repository;

import com.x.allreps.model.Localizacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalizacaoRepository extends JpaRepository<Localizacao, Long> {
    // Métodos de consulta personalizados, se necessário
}

