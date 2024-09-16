package com.x.allreps.repository;

import com.x.allreps.model.Republica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RepublicaRepository extends JpaRepository<Republica, Long> {
    List<Republica> findByLocalizacaoCidade(String cidade);

    List<Republica> findByValorLessThanEqual(Double valor);
    List<Republica> findByGeneroPreferencial(String genero);

}
