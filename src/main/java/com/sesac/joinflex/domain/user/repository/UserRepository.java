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

    // 전체 사용자 조회 (페이지네이션)
    Slice<User> findAllBy(Pageable pageable);

    Boolean existsByEmail(String email);

    Boolean existsByNickname(String nickname);

    Optional<User> findByEmail(String email);


    // 특정 IP로 특정 시간 이후에 가입한 사용자 수 조회
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

    // 키워드로 이메일 또는 닉네임 검색
    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
           OR LOWER(u.nickname) LIKE LOWER(CONCAT('%', :keyword, '%'))
        """)
    Slice<User> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
