package com.sesac.joinflex.domain.user.controller;

import com.sesac.joinflex.domain.user.dto.request.ProfileUpdateRequest;
import com.sesac.joinflex.domain.user.dto.response.UserProfileResponse;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USER)
@Validated
public class UserController {

    private final UserService userService;

    // 사용자 프로필 조회
    // http://localhost:8080/api/users/{id}
    @GetMapping(ApiPath.ID_PATH)
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse response = userService.getProfile(id, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    // 사용자 프로필 수정
    // http://localhost:8080/api/users/{id}
    @PutMapping(ApiPath.ID_PATH)
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request,
            HttpServletRequest httpRequest) {
        UserResponse response = userService.updateProfile(id, userDetails.getId(), request, httpRequest);
        return ResponseEntity.ok(response);
    }

    // 사용자 목록 조회 (검색 포함)
    // http://localhost:8080/api/users?keyword={keyword}&page=0&size=20&sort=id,asc
    @GetMapping
    public ResponseEntity<Slice<UserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        Slice<UserResponse> response = userService.getUsers(keyword, pageable);
        return ResponseEntity.ok(response);
    }
}
