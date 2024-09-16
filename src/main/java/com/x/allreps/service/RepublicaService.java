package com.x.allreps.service;


import com.x.allreps.model.Republica;
import com.x.allreps.repository.RepublicaRepository;
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

    public void remover(Long id) {
        republicaRepository.deleteById(id);
    }

}
