package com.sesac.joinflix.domain.party.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
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

    List<Long> invitedUserIds,

    LocalDateTime scheduledAt
) {

    public PartyRoomRequest {
        invitedUserIds = (invitedUserIds == null) ? List.of() : List.copyOf(invitedUserIds);
    }

    @AssertTrue(message = "비공개 방은 비밀번호(passCode)가 필수입니다.")
    private boolean isPassCodeValid() {
        if (Boolean.TRUE.equals(isPublic)) {
            return true;
        }

        return passCode != null && !passCode.isBlank();
    }

    @AssertTrue(message = "예약 시간은 30분 단위의 미래 시간이어야 합니다.")
    private boolean isScheduledAtValid() {
        if (scheduledAt == null) {
            return true;
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekLater = now.plusWeeks(1);

        return scheduledAt.isAfter(now) && scheduledAt.isBefore(oneWeekLater)
            && scheduledAt.getMinute() % 30 == 0
            && scheduledAt.getSecond() == 0
            && scheduledAt.getNano() == 0;
    }
}
