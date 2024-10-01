package com.x.allreps.service;

import com.x.allreps.config.AppConfig;
import com.x.allreps.exception.InvalidResetCodeException;
import com.x.allreps.exception.ResourceNotFoundException;
import com.x.allreps.exception.UsernameAlreadyExistsException;
import com.x.allreps.model.User;
import com.x.allreps.repository.UserRepository;
import com.x.allreps.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, EmailService emailService) {
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Carregando usuário pelo email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado: {}", email);
                    return new ResourceNotFoundException("Usuário não encontrado: " + email);
                });
        logger.debug("Usuário encontrado: {}", email);
        return new UserDetailsImpl(user);
    }

    public User save(User user) {
        logger.debug("Salvando novo usuário: {}", user.getEmail());

        // Verificar se já existe um usuário com o mesmo email ou username
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            logger.warn("Email já registrado: {}", user.getEmail());
            throw new UsernameAlreadyExistsException("Email já registrado: " + user.getEmail());
        }

        if (userRepository.findByUsername(user.getUsername()) != null) {
            logger.warn("Username já existe: {}", user.getUsername());
            throw new UsernameAlreadyExistsException("Username já existe: " + user.getUsername());
        }

        // Codificar a senha antes de salvar
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        logger.info("Senha codificada a ser salva: {}", encodedPassword);
        user.setPassword(encodedPassword);

        User savedUser = userRepository.save(user);
        logger.info("Usuário salvo com sucesso: ID = {}", savedUser.getId());
        return savedUser;
    }

    public void generatePasswordResetCode(String email) {
        logger.info("Solicitação de redefinição de senha para o e-mail: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado: " + email));

        // Gerar um código aleatório de 6 dígitos
        String resetCode = String.format("%06d", new Random().nextInt(999999));

        // Definir a expiração para 15 minutos a partir de agora
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(15);

        user.setResetCode(resetCode);
        user.setResetCodeExpiration(expirationTime);

        userRepository.save(user);

        // Enviar e-mail com o código
        emailService.sendResetCodeEmail(email, resetCode);
    }

    public void resetPassword(String email, String code, String newPassword) {
        logger.info("Tentativa de redefinição de senha para o e-mail: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Email não encontrado: " + email));

        // Verificar se o código corresponde e não expirou
        if (user.getResetCode() == null || !user.getResetCode().equals(code)) {
            throw new InvalidResetCodeException("Código de redefinição inválido.");
        }

        if (user.getResetCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new InvalidResetCodeException("O código de redefinição expirou.");
        }

        // Atualizar a senha
        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);

        // Limpar o código de redefinição e sua expiração
        user.setResetCode(code);
        user.setResetCodeExpiration(null);

        userRepository.save(user);
    }

    public User findByUsername(String username) {
        logger.debug("Buscando usuário pelo nome de usuário: {}", username);
        return userRepository.findByUsername(username);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + userId));
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(Long userId) {
        User user = findById(userId);
        userRepository.delete(user);
    }

    public Optional<User> findByEmail(String email) {
        return Optional.ofNullable(userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Email não encontrado: {}", email);
                    return new ResourceNotFoundException("Email não encontrado: " + email);
                }));
    }

    public Optional<User> findByPhoneNumber(String email) {
        return Optional.ofNullable(userRepository.findByPhoneNumber(email)
                .orElseThrow(() -> {
                    logger.warn("Numero não encontrado: {}", email);
                    return new ResourceNotFoundException("Numero não encontrado: " + email);
                }));
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }
}
