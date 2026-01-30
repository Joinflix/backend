package com.sesac.joinflex.domain.party.controller;

import com.sesac.joinflex.domain.party.dto.request.PartyRoomRequest;
import com.sesac.joinflex.domain.party.dto.response.PartyRoomResponse;
import com.sesac.joinflex.domain.party.service.PartyService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.PARTY)
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;

    @PostMapping
    public ResponseEntity<Long> createPartyRoom(@Valid @RequestBody PartyRoomRequest request,
        @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(partyService.createPartyRoom(request, userDetails.getId()));
    }

    @GetMapping
    public ResponseEntity<Slice<PartyRoomResponse>> getPartyRooms(
        @RequestParam(required = false) Long cursorId,
        @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(partyService.getPartyRooms(cursorId, pageable));
    }
}
