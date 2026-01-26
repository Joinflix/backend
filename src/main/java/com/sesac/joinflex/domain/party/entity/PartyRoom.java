package com.sesac.joinflex.domain.party.entity;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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

    private PartyRoom(String roomName, User host, Movie movie, Boolean isPublic,
        Boolean hostControl, String passCode) {
        this.roomName = roomName;
        this.host = host;
        this.movie = movie;
        this.isPublic = isPublic;
        this.hostControl = hostControl;
        this.passCode = passCode;
    }

    public static PartyRoom create(String roomName, User host, Movie movie, Boolean isPublic, Boolean hostControl, String passCode) {
        if (isPublic) {
            return new PartyRoom(roomName, host, movie, true, true, null);
        } else {
            return new PartyRoom(roomName, host, movie, false, hostControl, passCode);
        }
    }

}
