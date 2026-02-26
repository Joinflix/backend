package com.sesac.joinflix.domain.friend.entity;

import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "friendships",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "friend_id"})})
public class Friendship extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "friend_id", nullable = false)
    private User friend;

    private Friendship(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }

    public static Friendship create(User user, User friend) {
        return new Friendship(user, friend);
    }
}