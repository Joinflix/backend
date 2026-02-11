package com.sesac.joinflex.domain.party.dto.response;

public record PartyLeaveResult(
    Boolean roomDeleted,
    Integer currentMemberCount
) {

    public static PartyLeaveResult deleted() {
        return new PartyLeaveResult(true, 0);
    }

    public static PartyLeaveResult left(int count) {
        return new PartyLeaveResult(false, count);
    }

}
