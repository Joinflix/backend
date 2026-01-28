package com.sesac.joinflex.global.security;

import com.sesac.joinflex.domain.user.entity.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String email;
    private final String nickName;
    private final String password;
    private final Boolean isLock;
    private final String role;
    private final Boolean hasMembership;

    public CustomUserDetails(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickName = user.getNickname();
        this.password = user.getPassword();
        this.isLock = user.getIsLock();
        this.role = user.getRoleType().name();
        this.hasMembership = user.canUseService();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 엔티티의 isLock 필드와 연동 (true면 잠긴 상태이므로 !isLock 반환)
    @Override
    public boolean isAccountNonLocked() {
        return !isLock;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 활성화 되어 있는지 여부 (기본 true)
    @Override
    public boolean isEnabled() {
        return true;
    }
}