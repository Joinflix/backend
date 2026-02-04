package com.sesac.joinflex.domain.friend.service;

import com.sesac.joinflex.domain.friend.dto.response.FriendRequestResponse;
import com.sesac.joinflex.domain.friend.dto.response.FriendResponse;
import com.sesac.joinflex.domain.friend.entity.FriendRequest;
import com.sesac.joinflex.domain.friend.entity.FriendRequestStatus;
import com.sesac.joinflex.domain.friend.repository.FriendRequestRepository;
import com.sesac.joinflex.domain.notification.message.NotificationMessageTemplate;
import com.sesac.joinflex.domain.notification.service.NotificationService;
import com.sesac.joinflex.domain.notification.type.NotificationType;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Transactional
    public FriendRequestResponse createRequest(Long senderId, Long receiverId) {

        if (senderId.equals(receiverId)) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_SELF);
        }

        // 양방향 중복 체크: A→B 또는 B→A가 PENDING/ACCEPTED면 신규 생성 금지
        List<FriendRequestStatus> blockingStatuses = List.of(FriendRequestStatus.PENDING,
            FriendRequestStatus.ACCEPTED);
        if (friendRequestRepository.existsBidirectionalRequest(senderId, receiverId,
            blockingStatuses)) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_ALREADY_EXISTS);
        }

        User sender = findUserById(senderId);
        User receiver = findUserById(receiverId);

        FriendRequest request = FriendRequest.create(sender, receiver);
        FriendRequest saved = friendRequestRepository.save(request);

        sendNotification(receiverId,
            NotificationMessageTemplate.friendRequest(sender.getNickname()),
            NotificationType.FRIEND_REQUEST);

        return FriendRequestResponse.from(saved);
    }

    @Transactional
    public FriendRequestResponse acceptRequest(Long userId, Long requestId) {
        FriendRequest request = findRequestById(requestId);
        validateAccess(request, userId, false);
        validatePendingState(request);

        request.accept();

        sendNotification(request.getSender().getId(),
            NotificationMessageTemplate.friendAccept(request.getReceiver().getNickname()),
            NotificationType.FRIEND_ACCEPT);

        return FriendRequestResponse.from(request);
    }

    @Transactional
    public void rejectRequest(Long userId, Long requestId) {
        FriendRequest request = findRequestById(requestId);
        validateAccess(request, userId, false);
        validatePendingState(request);

        sendNotification(request.getSender().getId(),
            NotificationMessageTemplate.friendReject(request.getReceiver().getNickname()),
            NotificationType.FRIEND_REJECT);

        friendRequestRepository.delete(request);
    }

    @Transactional
    public void cancelRequest(Long userId, Long requestId) {
        FriendRequest request = findRequestById(requestId);
        validateAccess(request, userId, true);
        validatePendingState(request);

        friendRequestRepository.delete(request);
    }

    @Transactional
    public void deleteFriend(Long userId, Long friendId) {
        FriendRequest request = friendRequestRepository
            .findAcceptedFriendship(userId, friendId, FriendRequestStatus.ACCEPTED)
            .orElseThrow(() -> new CustomException(ErrorCode.FRIEND_NOT_FOUND));

        friendRequestRepository.delete(request);
    }

    public List<FriendRequestResponse> getIncomingRequests(Long userId) {
        return friendRequestRepository.findByReceiverIdAndStatus(userId,
                FriendRequestStatus.PENDING)
            .stream()
            .map(FriendRequestResponse::from)
            .toList();
    }


    public List<FriendRequestResponse> getOutgoingRequests(Long userId) {
        return friendRequestRepository.findBySenderIdAndStatus(userId, FriendRequestStatus.PENDING)
            .stream()
            .map(FriendRequestResponse::from)
            .toList();
    }

    public List<FriendResponse> getFriends(Long userId) {
        return getFriendUsers(userId).stream()
            .map(FriendResponse::from)
            .toList();
    }

    public List<FriendResponse> getOnlineFriends(Long userId) {
        return getFriendUsers(userId).stream()
            .filter(User::getIsOnline)
            .map(FriendResponse::from)
            .toList();
    }

    private List<User> getFriendUsers(Long userId) {
        return friendRequestRepository.findAcceptedFriends(userId, FriendRequestStatus.ACCEPTED)
            .stream()
            .map(request -> extractFriend(request, userId))
            .toList();
    }

    private User extractFriend(FriendRequest request, Long userId) {
        // 내가 sender면 receiver가 친구, 내가 receiver면 sender가 친구
        return request.getSender().getId().equals(userId) ? request.getReceiver()
            : request.getSender();
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private FriendRequest findRequestById(Long requestId) {
        return friendRequestRepository.findById(requestId)
            .orElseThrow(() -> new CustomException(ErrorCode.FRIEND_REQUEST_NOT_FOUND));
    }

    private void validateAccess(FriendRequest request, Long userId, boolean isSender) {
        Long targetId = isSender ? request.getSender().getId() : request.getReceiver().getId();
        if (!targetId.equals(userId)) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_NOT_AUTHORIZED);
        }
    }

    private void validatePendingState(FriendRequest request) {
        if (!request.isPending()) {
            throw new CustomException(ErrorCode.FRIEND_REQUEST_INVALID_STATE);
        }
    }

    private void sendNotification(Long userId, String message, NotificationType type) {
        try {
            notificationService.sendAndSave(userId, message, type);
        } catch (Exception e) {
            switch (type) {
                // SSE 메시지 관련 예외 처리가 필요할 때 추가할 예정
                case FRIEND_REQUEST -> { /* 친구 요청 알림 실패 시 처리 */ }
                case FRIEND_ACCEPT -> { /* 친구 수락 알림 실패 시 처리 */ }
                case FRIEND_REJECT -> { /* 친구 거절 알림 실패 시 처리 */ }
            }
        }
    }

}
