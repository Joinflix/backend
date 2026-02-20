package com.sesac.joinflix.domain.party.event;

public record PartyInviteEvent(
    String guestEmail,
    String hostNickname,
    String roomName,
    String joinUrl
) {

}
