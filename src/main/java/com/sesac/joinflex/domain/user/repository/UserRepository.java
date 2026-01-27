package com.sesac.joinflex.domain.user.repository;

import com.sesac.joinflex.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);
    Boolean existsByNickname(String nickname);
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsLockAndIsSocial(String email, Boolean isLock, Boolean isSocial);
    // 특정 IP로 특정 시간 이후에 가입한 사용자 수 조회
    long countBySignupIpAndCreatedAtAfter(String signupIp, LocalDateTime dateTime);
}
