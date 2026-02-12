package com.sesac.joinflix.domain.party.repository;

import com.sesac.joinflix.domain.party.entity.MemberStatus;
import com.sesac.joinflix.domain.party.entity.PartyMember;
import com.sesac.joinflix.domain.party.entity.PartyRoom;
import com.sesac.joinflix.domain.user.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {

    boolean existsByPartyRoomAndMemberAndStatus(PartyRoom partyRoom, User user,
        MemberStatus memberStatus);

    Optional<PartyMember> findByPartyRoomAndMemberAndStatus(PartyRoom partyRoom, User user,
        MemberStatus memberStatus);

    @Query("""
            select pm from PartyMember pm 
            join fetch pm.member 
            where pm.partyRoom = :partyRoom 
              and pm.status = :status 
              and pm.member.id <> :memberId
        """)
    List<PartyMember> findOtherMembersWithFetch(@Param("memberId") Long memberId,
        @Param("partyRoom") PartyRoom partyRoom,
        @Param("status") MemberStatus status);

    void deleteAllByPartyRoom(PartyRoom partyRoom);

}
