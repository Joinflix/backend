package com.sesac.joinflex.domain.party.repository;

import com.sesac.joinflex.domain.party.entity.MemberStatus;
import com.sesac.joinflex.domain.party.entity.PartyMember;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.user.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {

    boolean existsByPartyRoomAndMemberAndStatus(PartyRoom partyRoom, User user,
        MemberStatus memberStatus);

    Optional<PartyMember> findByPartyRoomAndMemberAndStatus(PartyRoom partyRoom, User user, MemberStatus memberStatus);
}
