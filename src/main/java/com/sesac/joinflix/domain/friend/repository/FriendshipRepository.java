package com.sesac.joinflix.domain.friend.repository;

import com.sesac.joinflix.domain.friend.entity.FriendRequest;
import com.sesac.joinflix.domain.friend.entity.Friendship;
import com.sesac.joinflix.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {

    boolean existsByUserAndFriend(User user, User friend);

    // 친구 목록 조회 (검색어 포함)
    @Query("SELECT f FROM Friendship f " +
            "JOIN FETCH f.friend " +
            "WHERE f.user.id = :myId " +
            "AND (:word IS NULL OR :word = '' OR f.friend.nickname LIKE %:word% OR f.friend.email LIKE %:word%)")
    List<Friendship> findAllByUserIdWithSearch(@Param("myId") Long myId, @Param("word") String word);

    // 내가 보낸 신청 (상대방은 Receiver의 닉네임/이메일 검색)
    @Query("SELECT fr FROM FriendRequest fr JOIN FETCH fr.receiver " +
            "WHERE fr.sender.id = :myId AND fr.status = 'PENDING' " +
            "AND (:word IS NULL OR :word = '' OR fr.receiver.nickname LIKE %:word% OR fr.receiver.email LIKE %:word%)")
    List<FriendRequest> findAllSentPending(@Param("myId") Long myId, @Param("word") String word);

    void deleteByUserIdAndFriendId(Long userId, Long friendId);

}
