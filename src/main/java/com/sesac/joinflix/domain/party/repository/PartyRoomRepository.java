package com.sesac.joinflix.domain.party.repository;

import com.sesac.joinflix.domain.party.entity.PartyRoom;
import com.sesac.joinflix.domain.party.entity.PartyStatus;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartyRoomRepository extends JpaRepository<PartyRoom, Long> {

    @Query(
        """
              select pr from PartyRoom pr
              join fetch pr.movie
              join fetch pr.host
              where pr.id < :cursorId
              order by pr.id desc
            """
    )
    Slice<PartyRoom> findPartyRooms(@Param("cursorId") Long cursorId, Pageable pageable);

    // 비관적 락
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query(
        """
                select p from PartyRoom p
                where p.id = :partyId
            """
    )
    Optional<PartyRoom> findByIdWithLock(@Param("partyId") Long partyId);

    List<PartyRoom> findByStatusAndScheduledAtLessThanEqual(PartyStatus status,
        LocalDateTime dateTime);
}
