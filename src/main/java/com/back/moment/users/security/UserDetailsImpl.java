package com.back.moment.users.security;

import com.back.moment.users.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final Users users;  // 인증 완료된 Users 객체
    private final String username;
    private final String password;

    public UserDetailsImpl(Users users, String email, String password) {
        this.users = users;
        this.username = email;
        this.password = password;
    }

    public Users getUsers() {
        return users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        RoleEnum role = users.getRole();
//        String authority = "USERS";
//
//        SimpleGrantedAuthority simpleGrantedAuthority = new SimpleGrantedAuthority(authority);
//        Collection<GrantedAuthority> authorities = new ArrayList<>();
//        authorities.add(simpleGrantedAuthority);
//
//        return authorities;
        return null;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
