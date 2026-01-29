package com.sesac.joinflex.domain.friend.repository;

import com.sesac.joinflex.domain.friend.entity.FriendRequest;
import com.sesac.joinflex.domain.friend.entity.FriendRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("""
        SELECT fr FROM FriendRequest fr
        JOIN FETCH fr.sender
        JOIN FETCH fr.receiver
        WHERE fr.status = :status
          AND (fr.sender.id = :userId OR fr.receiver.id = :userId)
        """)
    List<FriendRequest> findAcceptedFriends(@Param("userId") Long userId, @Param("status") FriendRequestStatus status);

    Optional<FriendRequest> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    @Query("""
            SELECT fr FROM FriendRequest fr
            JOIN FETCH fr.sender
            JOIN FETCH fr.receiver
            WHERE fr.receiver.id = :receiverId AND fr.status = :status
            """)
    List<FriendRequest> findByReceiverIdAndStatus(@Param("receiverId") Long receiverId,
            @Param("status") FriendRequestStatus status);

    @Query("""
            SELECT fr FROM FriendRequest fr
            JOIN FETCH fr.sender
            JOIN FETCH fr.receiver
            WHERE fr.sender.id = :senderId AND fr.status = :status
            """)
    List<FriendRequest> findBySenderIdAndStatus(@Param("senderId") Long senderId,
            @Param("status") FriendRequestStatus status);

    @Query("""
        SELECT COUNT(fr) > 0 FROM FriendRequest fr
        WHERE (fr.sender.id = :userId1 AND fr.receiver.id = :userId2 AND fr.status IN :statuses)
           OR (fr.sender.id = :userId2 AND fr.receiver.id = :userId1 AND fr.status IN :statuses)
        """)
    boolean existsBidirectionalRequest(@Param("userId1") Long userId1, @Param("userId2") Long userId2, @Param("statuses") List<FriendRequestStatus> statuses);

    @Query("""
        SELECT fr FROM FriendRequest fr
        WHERE fr.status = :status
          AND ((fr.sender.id = :userId AND fr.receiver.id = :friendId)
               OR (fr.sender.id = :friendId AND fr.receiver.id = :userId))
        """)
    Optional<FriendRequest> findAcceptedFriendship(@Param("userId") Long userId, @Param("friendId") Long friendId, @Param("status") FriendRequestStatus status);
}
