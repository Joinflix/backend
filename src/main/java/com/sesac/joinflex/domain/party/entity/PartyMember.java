package com.sesac.joinflex.domain.party.entity;

import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.global.common.entity.BaseEntity;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "partyMembers")
@Entity
public class PartyMember extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_room_id", nullable = false)
    private PartyRoom partyRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private User member;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    private PartyMember(PartyRoom partyRoom, User member, MemberRole role, MemberStatus status) {
        this.partyRoom = partyRoom;
        this.member = member;
        this.role = role;
        this.status = status;
    }

    public static PartyMember create(PartyRoom partyRoom, User member, MemberRole role) {
        return new PartyMember(partyRoom, member, role, MemberStatus.JOINED);
    }

}
