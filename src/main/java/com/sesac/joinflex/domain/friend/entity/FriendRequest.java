package com.sesac.joinflex.domain.friend.entity;

import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "friend_requests",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"sender_id", "receiver_id"})})
@Entity
public class FriendRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private FriendRequestStatus status;

    private FriendRequest(User sender, User receiver) {
        this.sender = sender;
        this.receiver = receiver;
        this.status = FriendRequestStatus.PENDING;
    }

    //생성 & 상태변경 메소드

    public static FriendRequest create(User sender, User receiver) {
        return new FriendRequest(sender, receiver);
    }


    public void accept(){
        this.status = FriendRequestStatus.ACCEPTED;
    }

    public boolean isPending() {
        return this.status == FriendRequestStatus.PENDING;
    }

}
