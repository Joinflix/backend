package com.sesac.joinflex.domain.party.repository;

import com.sesac.joinflex.domain.party.entity.PartyInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyInviteRepository extends JpaRepository<PartyInvite, Long> {

}
