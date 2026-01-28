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


    @PostMapping("/requests")
    public ResponseEntity<FriendRequestResponse> createRequest(@Valid @RequestBody FriendRequestCreateRequest request, HttpServletRequest httpRequest) {
        Long senderId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.createRequest(senderId, request.receiverId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/requests/incoming")
    public ResponseEntity<List<FriendRequestResponse>> getIncomingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getIncomingRequests(userId);
        return ResponseEntity.ok(responses);
    }


    @GetMapping("/requests/outgoing")
    public ResponseEntity<List<FriendRequestResponse>> getOutgoingRequests(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendRequestResponse> responses = friendRequestService.getOutgoingRequests(userId);
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/requests/{requestId}/accept")
    public ResponseEntity<FriendRequestResponse> acceptRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        FriendRequestResponse response = friendRequestService.acceptRequest(userId, requestId);
        return ResponseEntity.ok(response);
    }


    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.rejectRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/requests/{requestId}/cancel")
    public ResponseEntity<Void> cancelRequest(@PathVariable Long requestId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.cancelRequest(userId, requestId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<FriendResponse>> getFriends(HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        List<FriendResponse> responses = friendRequestService.getFriends(userId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long friendId, HttpServletRequest httpRequest) {
        Long userId = currentUserResolver.resolve(httpRequest);
        friendRequestService.deleteFriend(userId, friendId);
        return ResponseEntity.noContent().build();
    }
}
