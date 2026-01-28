package com.sesac.joinflex.domain.user.repository;

import com.sesac.joinflex.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(
        """
                 select u from User u
                 where u.id in :userIds
                 and exists (select 1 from FriendRequest fr
                             where fr.isAccepted = true
                             and fr.sender = :host and fr.receiver = u
                             or fr.receiver = :host and fr.sender = u
                            )
            """
    )
    List<User> findFriendsByHostAndIds(@Param("host") User host,
        @Param("userIds") List<Long> userIds);
}
