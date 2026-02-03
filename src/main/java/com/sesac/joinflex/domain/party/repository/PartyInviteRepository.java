package com.sesac.joinflex.domain.party.repository;

import com.sesac.joinflex.domain.party.entity.PartyInvite;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyInviteRepository extends JpaRepository<PartyInvite, Long> {

    Optional<PartyInvite> findByPartyRoomAndGuest(PartyRoom partyRoom, User user);
}
