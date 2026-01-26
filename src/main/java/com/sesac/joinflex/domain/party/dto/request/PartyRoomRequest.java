package com.sesac.joinflex.domain.party.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PartyRoomRequest(

    @NotNull(message = "movieId는 필수입니다.")
    @Positive(message = "movieId는 양수여야 합니다.")
    Long movieId,

    @Size(min = 1, max = 20, message = "roomName은 1~20자만 입력 가능합니다.")
    @NotBlank(message = "roomName은 필수입니다.")
    String roomName,

    @NotNull(message = "isPublic은 필수입니다.")
    Boolean isPublic,

    @NotNull(message = "hostControl은 필수입니다.")
    Boolean hostControl,

    @Size(min = 4, max = 4, message = "passCode 숫자 4자만 입력 가능합니다.")
    @Pattern(regexp = "\\d{4}")
    String passCode,

    List<Long> invitedUserIds
) {

    public PartyRoomRequest {
        invitedUserIds = (invitedUserIds == null) ? List.of() : List.copyOf(invitedUserIds);
    }
}
