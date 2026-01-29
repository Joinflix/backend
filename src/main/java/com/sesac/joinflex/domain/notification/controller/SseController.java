package com.sesac.joinflex.domain.notification.controller;

import com.sesac.joinflex.domain.friend.service.FriendRequestService;
import com.sesac.joinflex.domain.notification.service.SseNotificationListener;
import com.sesac.joinflex.domain.notification.service.SseService;
import com.sesac.joinflex.global.security.CurrentUserResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sse")
@RequiredArgsConstructor
public class SseController {
    //SSE 연결 타임아웃: 30분
    private static final long SSE_TIMEOUT = 30 * 60 * 1000L; // 30분

    private final SseService sseService;
    private final SseNotificationListener notificationListener;
    private final CurrentUserResolver currentUserResolver;
    private final FriendRequestService friendRequestService;

    /**
     * SSE 구독 엔드포인트
     * GET /api/sse/subscribe
     *
     * [ 처리 흐름 ]
     * 1. 현재 사용자 ID 추출
     * 2. SseEmitter 생성 (30분 타임아웃)
     * 3. 레지스트리에 등록 (멀티탭/멀티디바이스 지원)
     * 4. 연결 종료 핸들러 설정 (정상 종료, 타임아웃, 에러)
     * 5. 연결 성공 이벤트 전송 (현재 온라인 친구 목록 포함)
     * 6. 친구들에게 온라인 알림 전송
     *
     * [ 응답 형식 ]
     * Content-Type: text/event-stream
     *
     * [ 초기 이벤트 ]
     * event: open
     * data: {"message":"connected","onlineFriends":[2,3,5]}
     *
     * @param request HTTP 요청 (userId 추출용)
     * @return SseEmitter (스트리밍 응답)
     */
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(HttpServletRequest request) {
        Long userId = currentUserResolver.resolve(request);

        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT);

        sseService.register(userId, emitter);


        Runnable onDisconnect = createDisconnectHandler(userId, emitter);
        emitter.onCompletion(onDisconnect);
        emitter.onTimeout(onDisconnect);
        emitter.onError(e -> {
            log.warn("SSE error for userId: {}", userId, e);
            onDisconnect.run();
        });

        // 연결 성공 이벤트 전송
        if (!sendOpenEvent(emitter, userId)) {
            return emitter;
        }

        // 온라인 상태 알림
        notificationListener.markOnline(userId);

        log.info("SSE subscribed: userId={}", userId);
        return emitter;
    }
    /**
     * 연결 종료 핸들러를 생성합니다.
     *
     * [ 실행 시점 ]
     * - 클라이언트가 연결을 끊을 때 (탭 닫기, 페이지 이동 등)
     * - 타임아웃 발생 시
     * - 에러 발생 시
     *
     * [ 처리 내용 ]
     * 1. 레지스트리에서 emitter 제거
     * 2. 해당 유저의 다른 연결이 없으면 오프라인으로 표시
     *    → 친구들에게 "유저 X가 오프라인이 되었습니다" 알림
     */
    private Runnable createDisconnectHandler(Long userId, SseEmitter emitter) {
        return () -> {
            sseService.remove(userId, emitter);
            if (!sseService.isOnline(userId)) {
                notificationListener.markOffline(userId);
            }
        };
    }
    /**
     * 연결 성공 이벤트(open)를 전송합니다.
     *
     * [ 이벤트 형식 ]
     * event: open
     * data: {"message":"connected","onlineFriends":[2,3,5]}
     *
     * [ onlineFriends ]
     * 현재 SSE 연결되어 있는 (온라인인) 친구 ID 목록
     * → 프론트에서 친구 목록 UI에 온라인 표시에 활용
     *
     * @param emitter 전송할 emitter
     * @param userId  사용자 ID
     * @return 성공 시 true, 실패 시 false
     */
    private boolean sendOpenEvent(SseEmitter emitter, Long userId) {
        try {
            List<Long> friendIds = friendRequestService.getFriendIds(userId);
            List<Long> onlineFriends = sseService.getOnlineUserIds(friendIds);

            // 오프라인 동안 받은 PENDING 친구 요청 조회
            List<Map<String, Object>> pendingRequests = friendRequestService.getIncomingRequests(userId)
                .stream()
                .map(req -> Map.<String, Object>of(
                    "requestId", req.requestId(),
                    "senderId", req.senderId(),
                    "createdAt", req.createdAt().toString()
                ))
                .toList();

            Map<String, Object> openData = new java.util.HashMap<>();
            openData.put("message", "connected");
            openData.put("onlineFriends", onlineFriends);
            openData.put("pendingRequests", pendingRequests);  // 오프라인 동안 받은 요청

            emitter.send(SseEmitter.event()
                .name("open") //이벤트 이름
                .data(openData) // 이벤트 데이터
                .reconnectTime(3000)); //재연결 대기시간

            log.info("SSE open event sent: userId={}, pendingRequests={}", userId, pendingRequests.size());

            return true;
        } catch (IOException e) {
            log.error("Failed to send open event for userId: {}", userId, e);
            sseService.remove(userId, emitter);
            return false;
        }
    }
}
