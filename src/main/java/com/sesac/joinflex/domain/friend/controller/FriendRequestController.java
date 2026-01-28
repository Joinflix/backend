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
@RequestMapping("/api")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final CurrentUserResolver currentUserResolver;


    @PostMapping("/friend-requests")
    public ResponseEntity<FriendRequestResponse> createRequest(@Valid @RequestBody FriendRequestCreateRequest request, HttpServletRequest httpRequest) {
        Long senderId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.createRequest(senderId, request.receiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/friend-requests/incoming")
    public ResponseEntity<List<FriendRequestResponse>> getIncomingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getIncomingRequests(userId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/friend-requests/outgoing")
    public ResponseEntity<List<FriendRequestResponse>> getOutgoingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getOutgoingRequests(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/friend-requests/{requestId}/accept")
    public ResponseEntity<FriendRequestResponse> acceptRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.acceptRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/friend-requests/{requestId}/reject")
    public ResponseEntity<FriendRequestResponse> rejectRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.rejectRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/friend-requests/{requestId}/cancel")
    public ResponseEntity<FriendRequestResponse> cancelRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.cancelRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/friends")
    public ResponseEntity<List<FriendResponse>> getFriends(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendResponse> responses = friendRequestService.getFriends(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
