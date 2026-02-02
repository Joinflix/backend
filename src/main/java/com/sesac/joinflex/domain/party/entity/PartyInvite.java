package com.sesac.joinflex.domain.party.entity;

import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
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
@Table(name = "partyInvites")
@Entity
public class PartyInvite extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_room_id", nullable = false)
    private PartyRoom partyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id", nullable = false)
    private User guest;

    private PartyInvite(PartyRoom partyRoom, User guest) {
        this.partyRoom = partyRoom;
        this.guest = guest;
    }

    public static PartyInvite create(PartyRoom partyRoom, User guest) {
        return new PartyInvite(partyRoom, guest);
    }
}
