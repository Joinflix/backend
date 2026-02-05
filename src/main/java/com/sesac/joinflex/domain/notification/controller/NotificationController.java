package com.sesac.joinflex.domain.notification.controller;

import com.sesac.joinflex.domain.notification.dto.request.NotificationReadRequest;
import com.sesac.joinflex.domain.notification.service.NotificationService;
import com.sesac.joinflex.global.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return notificationService.subscribe(customUserDetails.getId());
    }
    // POST http://localhost:8080/api/notifications/last-read-at
    @PostMapping("/last-read-at")
    public ResponseEntity<Void> updateLastReadTimestamp(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody NotificationReadRequest request
    ) {
        notificationService.updateLastNotificationReadAt(customUserDetails.getId(), request.getClickedAt());
        return ResponseEntity.noContent().build();
    }
}
