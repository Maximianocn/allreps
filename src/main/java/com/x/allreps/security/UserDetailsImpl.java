package com.x.allreps.security;

import  com.x.allreps.model.User;
import lombok.Getter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    // Método para acessar o objeto User original
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    // Métodos de UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Sem roles por enquanto
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // Certifique-se de que getPassword() existe na classe User
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // Certifique-se de que getUsername() existe na classe User
    }

    // Outros métodos podem retornar true ou false conforme a necessidade
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
