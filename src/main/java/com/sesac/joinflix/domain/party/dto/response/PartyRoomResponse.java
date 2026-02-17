package com.sesac.joinflix.domain.party.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sesac.joinflix.domain.party.entity.PartyRoom;

public record PartyRoomResponse(
    Long id,
    String movieTitle,
    String backdrop,
    Boolean isPublic,
    String roomName,
    String hostNickname,
    Integer currentMemberCount,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    VideoStatus videoStatus
) {

    public static PartyRoomResponse of(PartyRoom partyRoom, VideoStatus videoStatus) {
        return new PartyRoomResponse(
            partyRoom.getId(), partyRoom.getMovie().getTitle(),
            partyRoom.getMovie().getBackdrop(), partyRoom.getIsPublic(),
            partyRoom.getRoomName(), partyRoom.getHost().getNickname(),
            partyRoom.getCurrentMemberCount(), videoStatus
        );
    }

    public static PartyRoomResponse of(PartyRoom partyRoom) {
        return new PartyRoomResponse(
            partyRoom.getId(), partyRoom.getMovie().getTitle(),
            partyRoom.getMovie().getBackdrop(), partyRoom.getIsPublic(),
            partyRoom.getRoomName(), partyRoom.getHost().getNickname(),
            partyRoom.getCurrentMemberCount(), null
        );
    }

}
