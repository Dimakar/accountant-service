package ru.dimakar.security;

import ru.dimakar.model.UserModel;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

public class User implements UserDetails {
    @Getter
    private String username;
    @Getter
    private String password;
    @Getter
    private List<GrantedAuthority> authorities;

    public User(UserModel userModel) {
        this.username = userModel.getEmail();
        this.password = userModel.getPassword();
        this.authorities = userModel.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList());
    }

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
