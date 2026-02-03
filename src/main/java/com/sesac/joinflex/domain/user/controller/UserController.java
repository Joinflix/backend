package com.sesac.joinflex.domain.user.controller;

import com.sesac.joinflex.domain.user.dto.request.ProfileUpdateRequest;
import com.sesac.joinflex.domain.user.dto.response.UserProfileResponse;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.dto.response.UserSearchResponse;
import com.sesac.joinflex.domain.user.service.UserService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.USER)
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
    // 전체 유저 조회
    // http://localhost:8080/api/users?cursorId={cursorId}&size={size}
    @GetMapping
    public ResponseEntity<Slice<UserResponse>> getAllUsers(
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable) {
        Slice<UserResponse> response = userService.getAllUsers(cursorId, pageable);
        return ResponseEntity.ok(response);
    }

    // 닉네임으로 유저 조회
    // http://localhost:8080/api/users/nickname?nickname={nickname}
    @GetMapping(ApiPath.NICKNAME)
    public ResponseEntity<List<UserResponse>> getUsersByNickname(@RequestParam String nickname) {
        List<UserResponse> response = userService.getUsersByNickname(nickname.trim());
        return ResponseEntity.ok(response);
    }

    // 이메일로 유저 조회
    // http://localhost:8080/api/users/email?email={email}
    @GetMapping(ApiPath.EMAIL)
    public ResponseEntity<List<UserResponse>> getUsersByEmail(@RequestParam String email) {
        List<UserResponse> response = userService.getUsersByEmail(email.trim());
        return ResponseEntity.ok(response);
    }

    @GetMapping(ApiPath.SEARCH)
    public ResponseEntity<Slice<UserSearchResponse>> getAllUsersWithRelationStatus(
            @RequestParam(required = false) Long cursorId,
            @PageableDefault(size = 10) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Slice<UserSearchResponse> response = userService.getAllUsersWithRelationStatus(userDetails.getId(), cursorId, pageable);
        return ResponseEntity.ok(response);
    }
}
