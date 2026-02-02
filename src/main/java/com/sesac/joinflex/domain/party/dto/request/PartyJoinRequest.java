package com.sesac.joinflex.domain.party.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PartyJoinRequest(
    @Size(min = 4, max = 4, message = "passCode 숫자 4자만 입력 가능합니다.")
    @Pattern(regexp = "\\d{4}")
    String passCode
) {

}
