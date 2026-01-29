package com.sesac.joinflex.domain.friend.controller;

import com.sesac.joinflex.domain.friend.dto.request.FriendRequestCreateRequest;
import com.sesac.joinflex.domain.friend.dto.response.FriendRequestResponse;
import com.sesac.joinflex.domain.friend.dto.response.FriendResponse;
import com.sesac.joinflex.domain.friend.service.FriendRequestService;
import com.sesac.joinflex.global.common.constants.ApiPath;
import com.sesac.joinflex.global.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping(ApiPath.FRIEND)
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final CurrentUserResolver currentUserResolver;

    // 친구 신청
    // http://localhost:8080/api/friends/requests
    @PostMapping(ApiPath.FRIEND_REQUESTS)
    public ResponseEntity<FriendRequestResponse> createRequest(@Valid @RequestBody FriendRequestCreateRequest request, HttpServletRequest httpRequest) {
        Long senderId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.createRequest(senderId, request.receiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 받은 친구 요청 목록
    // http://localhost:8080/api/friends/requests/incoming
    @GetMapping(ApiPath.FRIEND_REQUESTS_INCOMING)
    public ResponseEntity<List<FriendRequestResponse>> getIncomingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getIncomingRequests(userId);
        return ResponseEntity.ok(responses);
    }
    // 보낸 친구 요청 목록
    // http://localhost:8080/api/friends/requests/outgoing
    @GetMapping(ApiPath.FRIEND_REQUESTS_OUTGOING)
    public ResponseEntity<List<FriendRequestResponse>> getOutgoingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getOutgoingRequests(userId);
        return ResponseEntity.ok(responses);
    }
    // 친구 요청 수락
    // http://localhost:8080/api/friends/requests/{requestId}/accept
    @PostMapping(ApiPath.FRIEND_REQUESTS_ACCEPT)
    public ResponseEntity<FriendRequestResponse> acceptRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.acceptRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }
    //친구 요청 거절
    // http://localhost:8080/api/friends/requests/{requestId}/reject
    @PostMapping(ApiPath.FRIEND_REQUESTS_REJECT)
    public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.rejectRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }
    // 친구 요청 취소
    // http://localhost:8080/api/friends/requests/{requestId}/cancel
    @PostMapping(ApiPath.FRIEND_REQUESTS_CANCEL)
    public ResponseEntity<Void> cancelRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.cancelRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }
    //친구 목록
    // http://localhost:8080/api/friends
    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriends(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendResponse> responses = friendRequestService.getFriends(userId);
        return ResponseEntity.ok(responses);
    }
    //친구 삭제
    // http://localhost:8080/api/friends/{friendId}
    @DeleteMapping(ApiPath.FRIEND_DELETE)
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
