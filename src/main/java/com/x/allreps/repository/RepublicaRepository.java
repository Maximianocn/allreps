package com.x.allreps.repository;

import com.x.allreps.model.Republica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RepublicaRepository extends JpaRepository<Republica, Long>, JpaSpecificationExecutor<Republica> {

    Optional<Republica> findByAnuncianteId(Long id);
    List<Republica> findByLocalizacaoCidade(String cidade);

    List<Republica> findByValorLessThanEqual(Double valor);
    List<Republica> findByGeneroPreferencial(String genero);

}
