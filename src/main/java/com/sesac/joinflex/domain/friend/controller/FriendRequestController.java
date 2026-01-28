package com.sesac.joinflex.domain.friend.controller;

import com.sesac.joinflex.domain.friend.dto.request.FriendRequestCreateRequest;
import com.sesac.joinflex.domain.friend.dto.response.FriendRequestResponse;
import com.sesac.joinflex.domain.friend.dto.response.FriendResponse;
import com.sesac.joinflex.domain.friend.service.FriendRequestService;
import com.sesac.joinflex.global.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final CurrentUserResolver currentUserResolver;

    /**
     * 친구 신청
     * POST /api/friend/requests
     *
     * [ 요청 ]
     * { "receiverId": 2 }
     *
     * [ 응답 ]
     * 201 Created
     * { "requestId": 1, "status": "PENDING", "senderId": 1, "receiverId": 2, ... }
     *
     */
    @PostMapping("/requests")
    public ResponseEntity<FriendRequestResponse> createRequest(@Valid @RequestBody FriendRequestCreateRequest request, HttpServletRequest httpRequest) {
        Long senderId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.createRequest(senderId, request.receiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 받은 친구 요청 목록
     * GET /api/friend/requests/incoming
     *
     * [ 응답 ]
     * 200 OK
     * [ { "requestId": 1, "status": "PENDING", "senderId": 2, "receiverId": 1, ... }, ... ]
     */
    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendRequestResponse>> getIncomingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getIncomingRequests(userId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 보낸 친구 요청 목록
     * GET /api/friend/requests/outgoing
     *
     * [ 응답 ]
     * 200 OK
     * [ { "requestId": 1, "status": "PENDING", "senderId": 1, "receiverId": 2, ... }, ... ]
     */
    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendRequestResponse>> getOutgoingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getOutgoingRequests(userId);
        return ResponseEntity.ok(responses);
    }
    /**
     * 친구 요청 수락
     * POST /api/friend/requests/{requestId}/accept
     *
     * [ 권한 ]
     * receiver만 호출 가능
     */
    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<FriendRequestResponse> acceptRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.acceptRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }

    /**
     * 친구 요청 거절
     * POST /api/friend/requests/{requestId}/reject
     *
     * [ 권한 ]
     * receiver만 호출 가능
     *
     * [ 응답 ]
     * 204 No Content (row 삭제됨, sender가 다시 신청 가능)
     */
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.rejectRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 친구 요청 취소
     * POST /api/friend/requests/{requestId}/cancel
     *
     * [ 권한 ]
     * sender만 호출 가능
     *
     * [ 응답 ]
     * 204 No Content (row 삭제됨, 다시 신청 가능)
     */
    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.cancelRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 친구 목록
     * GET /api/friend
     *
     * [ 응답 ]
     * 200 OK
     * [ { "userId": 2, "nickname": "user2", "email": "..." }, ... ]
     */
    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriends(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendResponse> responses = friendRequestService.getFriends(userId);
        return ResponseEntity.ok(responses);
    }
    /**
     * 친구 삭제
     * DELETE /api/friend/{friendId}
     *
     * [ 권한 ]
     * sender & receiver 양방향 삭제 가능
     *
     * [ 응답 ]
     * 204 No Content (본문 없음)
     */
    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
