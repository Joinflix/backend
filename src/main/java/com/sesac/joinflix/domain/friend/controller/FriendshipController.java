package com.sesac.joinflix.domain.friend.controller;

import com.sesac.joinflix.domain.friend.service.FriendshipService;
import com.sesac.joinflix.domain.user.dto.response.UserSearchResponse;
import com.sesac.joinflix.global.common.constants.ApiPath;
import com.sesac.joinflix.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPath.FRIENDSHIP) // /api/friendships
@RequiredArgsConstructor
public class FriendshipController {

    private final FriendshipService friendshipService;

    // 친구 관련 통합 리스트 조회 (무한 스크롤)
    // /api/friendships?primaryFilter=ALL&searchWord=민수&page=0&size=20
    @GetMapping
    public ResponseEntity<Slice<UserSearchResponse>> getFriendList(
            @RequestParam(required = false, defaultValue = "ALL") String primaryFilter,
            @RequestParam(required = false, defaultValue = "ALL") String secondaryFilter,
            @RequestParam(required = false) String searchWord,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PageableDefault(size = 10) Pageable pageable) {

        Slice<UserSearchResponse> responses = friendshipService.getAllFriendList(
                userDetails.getId(),
                primaryFilter,
                secondaryFilter,
                searchWord,
                pageable
        );

        return ResponseEntity.ok(responses);
    }

}