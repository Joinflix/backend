package com.sesac.joinflex.domain.party.dto.response;

public record PartyRoomResponse(
    Long id,
    String movieTitle,
    String backdrop,
    Boolean isPublic,
    String roomName,
    String hostNickname,
    Integer currentMemberCount
) {

}
