package com.sesac.joinflex.domain.party.repository;

import com.sesac.joinflex.domain.party.entity.PartyRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {

    @Query(
        """
              select pr from PartyRoom pr
              where pr.id < :cursorId
              order by pr.id desc
            """
    )
    Slice<PartyRoom> findPartyRooms(@Param("cursorId") Long cursorId, Pageable pageable);
}
