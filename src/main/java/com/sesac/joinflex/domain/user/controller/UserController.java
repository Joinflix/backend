package com.sesac.joinflex.domain.user.controller;

import com.sesac.joinflex.domain.user.dto.request.ProfileUpdateRequest;
import com.sesac.joinflex.domain.user.dto.response.UserProfileResponse;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USER)
public class UserController {

    private final UserService userService;

    // 사용자 프로필 조회
    @GetMapping(ApiPath.ID_PATH)
    public ResponseEntity<UserProfileResponse> getProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponse response = userService.getProfile(id, userDetails.getId());
        return ResponseEntity.ok(response);
    }

    // 사용자 프로필 수정
    @PutMapping(ApiPath.ID_PATH)
    public ResponseEntity<UserResponse> updateProfile(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody ProfileUpdateRequest request,
            HttpServletRequest httpRequest) {
        UserResponse response = userService.updateProfile(id, userDetails.getId(), request, httpRequest);
        return ResponseEntity.ok(response);
    }

    // 전체 사용자 조회
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    // 사용자 검색 (키워드로 이메일 또는 닉네임 검색)
    @GetMapping(ApiPath.SEARCH)
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String keyword) {
        List<UserResponse> response = userService.searchUsers(keyword);
        return ResponseEntity.ok(response);
    }
}
