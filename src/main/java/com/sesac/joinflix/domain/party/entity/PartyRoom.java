package com.sesac.joinflix.domain.party.entity;

import com.sesac.joinflix.domain.movie.entity.Movie;
import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.global.common.entity.BaseEntity;
import com.sesac.joinflix.global.exception.CustomException;
import com.sesac.joinflix.global.exception.ErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "partyRooms")
@Entity
public class PartyRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User host;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @Column(nullable = false)
    private String roomName;

    @Column(nullable = false)
    private Boolean isPublic;

    @Column(nullable = false)
    private Boolean hostControl;

    private String passCode;

    @Column(nullable = false)
    private Integer maxCount;

    private Integer currentMemberCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private PartyStatus status;

    private LocalDateTime scheduledAt;

    private PartyRoom(String roomName, User host, Movie movie, Boolean isPublic,
        Boolean hostControl, String passCode, PartyStatus status, LocalDateTime scheduledAt) {
        this.roomName = roomName;
        this.host = host;
        this.movie = movie;
        this.isPublic = isPublic;
        this.hostControl = hostControl;
        this.passCode = passCode;
        this.currentMemberCount = 0;
        this.maxCount = 4;
        this.status = status;
        this.scheduledAt = scheduledAt;
    }

    public static PartyRoom create(String roomName, User host, Movie movie, Boolean isPublic,
        Boolean hostControl, String passCode, LocalDateTime scheduledAt) {

        PartyStatus partyStatus = scheduledAt == null ? PartyStatus.ACTIVE : PartyStatus.SCHEDULED;

        if (isPublic) {
            return new PartyRoom(roomName, host, movie, true, true, null, partyStatus, scheduledAt);
        } else {
            return new PartyRoom(roomName, host, movie, false, hostControl, passCode, partyStatus,
                scheduledAt);
        }
    }

    public boolean isPublicRoom() {
        return this.isPublic;
    }

    public boolean isHost(User user) {
        return this.host.getId().equals(user.getId());
    }

    public void addMember() {
        if (isFull()) {
            throw new CustomException(ErrorCode.PARTY_ROOM_FULL);
        }

        this.currentMemberCount++;
    }

    public void leaveMember() {
        this.currentMemberCount--;
    }

    public void changeHost(User targetMember) {
        this.host = targetMember;
    }

    public boolean isPasswordMatch(String passCode) {
        return this.passCode.equals(passCode);
    }

    private boolean isFull() {
        return currentMemberCount >= maxCount;
    }
}
