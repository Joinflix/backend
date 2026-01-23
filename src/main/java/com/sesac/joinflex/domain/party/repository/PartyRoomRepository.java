package com.sesac.joinflex.domain.party.repository;

import com.sesac.joinflex.domain.party.entity.PartyRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {

}
