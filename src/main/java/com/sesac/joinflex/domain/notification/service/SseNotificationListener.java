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


    private static final String EVENT_FRIEND_REQUEST_RECEIVED = "friend.request.received";
    private static final String EVENT_FRIEND_REQUEST_ACCEPTED = "friend.request.accepted";
    private static final String EVENT_PRESENCE_ONLINE = "presence.online";
    private static final String EVENT_PRESENCE_OFFLINE = "presence.offline";


    // 친구 요청 생성 시 receiver에게 알림
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

    // 친구 요청 수락 시 sender에게 알림
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



    public void markOnline(Long userId) {
        List<Long> friendIds = friendRequestService.getFriendIds(userId);
        if (!friendIds.isEmpty()) {
            sseService.sendToMany(friendIds, EVENT_PRESENCE_ONLINE, Map.of("userId", userId));
        }
    }


    public void markOffline(Long userId) {
        List<Long> friendIds = friendRequestService.getFriendIds(userId);
        if (!friendIds.isEmpty()) {
            sseService.sendToMany(friendIds, EVENT_PRESENCE_OFFLINE, Map.of("userId", userId));
        }
    }
}
