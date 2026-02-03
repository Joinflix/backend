package com.sesac.joinflex.domain.user.repository;

import com.sesac.joinflex.domain.user.entity.User;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);

    Optional < User > findByEmailAndIsLockAndIsSocial ( String  email , Boolean  isLock , Boolean  isSocial );

    long countBySignupIpAndCreatedAtAfter(String signupIp, LocalDateTime dateTime);

    @Query(
        """
                 select u from User u
                 where u.id in :userIds
                 and exists (select 1 from FriendRequest fr
                             where fr.status = 'ACCEPTED'
                             and fr.sender = :host and fr.receiver = u
                             or fr.receiver = :host and fr.sender = u
                            )
            """
    )
    List<User> findFriendsByHostAndIds(@Param("host") User host,
        @Param("userIds") List<Long> userIds);

    @Query("SELECT u FROM User u WHERE u.membership IS NOT NULL AND u.membershipExpiryDate < CURRENT_TIMESTAMP")
    List<User> findAllByMembershipExpiryDateBeforeAndMembershipIsNotNull();

    @Query("select u from User u where u.id < :cursorId order by u.id desc")
    Slice<User> findUsers(@Param("cursorId") Long cursorId, Pageable pageable);

    List<User> findTop10ByNicknameContainingIgnoreCase(String nickname);

    List<User> findTop10ByEmailContainingIgnoreCase(String email);
}
