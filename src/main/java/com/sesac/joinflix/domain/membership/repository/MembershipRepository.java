package com.sesac.joinflix.domain.membership.repository;

import com.sesac.joinflix.domain.membership.entity.Membership;
import com.sesac.joinflix.domain.membership.entity.MembershipType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MembershipRepository extends JpaRepository<Membership,Long> {
    Boolean existsByType(MembershipType type);
    Optional<Membership> findByType(MembershipType type);
}
