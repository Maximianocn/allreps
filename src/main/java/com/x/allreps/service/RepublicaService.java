package com.x.allreps.service;


import com.x.allreps.model.Republica;
import com.x.allreps.model.User;
import com.x.allreps.repository.RepublicaRepository;
import com.x.allreps.utils.RepublicaSpecifications;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class RepublicaService {

    private final RepublicaRepository republicaRepository;

    public RepublicaService(RepublicaRepository republicaRepository) {
        this.republicaRepository = republicaRepository;
    }

    public Republica salvar(Republica republica) {
        return republicaRepository.save(republica);
    }

    public List<Republica> listarTodas() {
        return republicaRepository.findAll();
    }

    public Optional<Republica> buscarPorId(Long id) {
        return republicaRepository.findById(id);
    }

    public List<Republica> listarComFiltros(String cidade, Double valorMaximo, String generoPreferencial, Integer vagasDisponiveis, List<String> comodidades) {
        Specification<Republica> specs = Specification.where(null);

        if (cidade != null && !cidade.isEmpty()) {
            specs = specs.and(RepublicaSpecifications.hasCidade(cidade));
        }
        if (valorMaximo != null) {
            specs = specs.and(RepublicaSpecifications.hasValorMenorOuIgual(valorMaximo));
        }
        if (generoPreferencial != null && !generoPreferencial.isEmpty()) {
            specs = specs.and(RepublicaSpecifications.hasGeneroPreferencial(generoPreferencial));
        }
        if (vagasDisponiveis != null) {
            specs = specs.and(RepublicaSpecifications.hasVagasDisponiveis(vagasDisponiveis));
        }
        if (comodidades != null && !comodidades.isEmpty()) {
            specs = specs.and(RepublicaSpecifications.hasComodidades(comodidades));
        }

        return republicaRepository.findAll(specs);
    }

    public Optional<Republica> findByAnunciante(Optional<User> user){
        return republicaRepository.findByAnuncianteId(user.get().getId());
    }
    public void remover(Long id) {
        republicaRepository.deleteById(id);
    }

}
