package com.x.allreps.service;

import com.x.allreps.exception.ResourceNotFoundException;
import com.x.allreps.exception.UsernameAlreadyExistsException;
import com.x.allreps.model.User;
import com.x.allreps.repository.UserRepository;
import com.x.allreps.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Método para carregar UserDetails
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.debug("Carregando usuário pelo nome de usuário: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado: {}", username);
                    return new ResourceNotFoundException("Usuário não encontrado: " + username);
                });
        logger.debug("Usuário encontrado: {}", username);
        return new UserDetailsImpl(user);
    }

    // Método para salvar um novo usuário
    public User save(User user) {
        logger.debug("Salvando novo usuário: {}", user.getUsername());
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            logger.warn("Usuário já existe: {}", user.getUsername());
            throw new UsernameAlreadyExistsException("Usuário já existe: " + user.getUsername());
        }
        User savedUser = userRepository.save(user);
        logger.info("Usuário salvo com sucesso: ID = {}", savedUser.getId());
        return savedUser;
    }

    // Método para buscar usuário por username
    public Optional<User> findByUsername(String username) {
        logger.debug("Buscando usuário pelo nome de usuário: {}", username);
        return userRepository.findByUsername(username);
    }
}

