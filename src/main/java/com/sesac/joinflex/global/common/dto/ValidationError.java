package com.sesac.joinflex.global.common.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ValidationError {

    private final String field;
    private final String message;

}
