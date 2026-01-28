package com.sesac.joinflex.domain.notification.service;
import com.sesac.joinflex.domain.friend.service.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Map;


@Component
@RequiredArgsConstructor
public class SseNotificationListener {

    private final SseService sseService;
    private final FriendRequestService friendRequestService;

    // ===== 이벤트 타입 상수 =====
    // 프론트엔드의 eventSource.addEventListener('이벤트명', ...)에서 사용
    private static final String EVENT_FRIEND_REQUEST_RECEIVED = "friend.request.received";
    private static final String EVENT_FRIEND_REQUEST_ACCEPTED = "friend.request.accepted";
    private static final String EVENT_PRESENCE_ONLINE = "presence.online";
    private static final String EVENT_PRESENCE_OFFLINE = "presence.offline";


    /**
     * 친구 요청 생성 시 receiver에게 알림을 보냅니다.
     *
     * [ 트리거 ]
     * FriendRequestService.createRequest()에서 이벤트 발행
     *
     * [ SSE 이벤트 ]
     * event: friend.request.received
     * data: {"eventId":"...","at":"...","data":{"requestId":1,"senderId":2}}
     *
     * [ 프론트엔드 사용 예시 ]
     * eventSource.addEventListener('friend.request.received', (e) => {
     *   const { requestId, senderId } = JSON.parse(e.data).data;
     *   showNotification('새로운 친구 요청이 있습니다!');
     * });
     *
     * @param event 친구 요청 생성 이벤트 (FriendRequest 포함)
     */
    @EventListener
    public void onFriendRequestCreated(FriendRequestService.FriendRequestCreatedEvent event) {
        Long receiverId = event.request().getReceiver().getId();
        Long senderId = event.request().getSender().getId();
        Long requestId = event.request().getId();

        sseService.send(receiverId, EVENT_FRIEND_REQUEST_RECEIVED, Map.of(
            "requestId", requestId,
            "senderId", senderId
        ));

    }

    /**
     * 친구 요청 수락 시 sender에게 알림을 보냅니다.
     *
     * [ 트리거 ]
     * FriendRequestService.acceptRequest()에서 이벤트 발행
     *
     * [ SSE 이벤트 ]
     * event: friend.request.accepted
     * data: {"eventId":"...","at":"...","data":{"requestId":1,"acceptedBy":3}}
     *
     * [ 프론트엔드 사용 예시 ]
     * eventSource.addEventListener('friend.request.accepted', (e) => {
     *   const { acceptedBy } = JSON.parse(e.data).data;
     *   showNotification('친구 요청이 수락되었습니다!');
     *   refetchFriendList();  // 친구 목록 갱신
     * });
     *
     * @param event 친구 요청 수락 이벤트 (FriendRequest 포함)
     */
    @EventListener
    public void onFriendRequestAccepted(FriendRequestService.FriendRequestAcceptedEvent event) {
        Long senderId = event.request().getSender().getId();
        Long receiverId = event.request().getReceiver().getId();
        Long requestId = event.request().getId();

        sseService.send(senderId, EVENT_FRIEND_REQUEST_ACCEPTED, Map.of(
            "requestId", requestId,
            "acceptedBy", receiverId
        ));

    }


    /**
     * 사용자를 온라인으로 표시하고 친구들에게 알림을 보냅니다.
     *
     * [ 호출 시점 ]
     * SseController.subscribe()에서 연결 성공 후 호출
     *
     * [ SSE 이벤트 ]
     * event: presence.online
     * data: {"eventId":"...","at":"...","data":{"userId":5}}
     *
     * [ 대상 ]
     * 해당 유저의 모든 친구에게 전송
     *
     * [ 프론트엔드 사용 예시 ]
     * eventSource.addEventListener('presence.online', (e) => {
     *   const { userId } = JSON.parse(e.data).data;
     *   updateFriendOnlineStatus(userId, true);  // 친구 목록에서 온라인 표시
     * });
     *
     * @param userId 온라인이 된 사용자 ID
     */
    public void markOnline(Long userId) {
        List<Long> friendIds = friendRequestService.getFriendIds(userId);
        if (!friendIds.isEmpty()) {
            sseService.sendToMany(friendIds, EVENT_PRESENCE_ONLINE, Map.of("userId", userId));
        }
    }

    /**
     * 사용자를 오프라인으로 표시하고 친구들에게 알림을 보냅니다.
     *
     * [ 호출 시점 ]
     * SseController의 onDisconnect 핸들러에서 호출
     * (마지막 SSE 연결이 끊어질 때만)
     *
     * [ SSE 이벤트 ]
     * event: presence.offline
     * data: {"eventId":"...","at":"...","data":{"userId":5}}
     *
     * [ 대상 ]
     * 해당 유저의 모든 친구에게 전송
     *
     * [ 프론트엔드 사용 예시 ]
     * eventSource.addEventListener('presence.offline', (e) => {
     *   const { userId } = JSON.parse(e.data).data;
     *   updateFriendOnlineStatus(userId, false);  // 친구 목록에서 오프라인 표시
     * });
     *
     * @param userId 오프라인이 된 사용자 ID
     */
    public void markOffline(Long userId) {
        List<Long> friendIds = friendRequestService.getFriendIds(userId);
        if (!friendIds.isEmpty()) {
            sseService.sendToMany(friendIds, EVENT_PRESENCE_OFFLINE, Map.of("userId", userId));
        }
    }
}
