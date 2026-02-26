package com.sesac.joinflix.domain.party.service;

import com.sesac.joinflix.domain.notification.message.NotificationMessageTemplate;
import com.sesac.joinflix.domain.notification.service.NotificationService;
import com.sesac.joinflix.domain.notification.type.NotificationType;
import com.sesac.joinflix.domain.party.entity.PartyInvite;
import com.sesac.joinflix.domain.party.entity.PartyRoom;
import com.sesac.joinflix.domain.party.event.PartyInviteEvent;
import com.sesac.joinflix.domain.party.repository.PartyInviteRepository;
import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.domain.user.repository.UserRepository;
import com.sesac.joinflix.global.exception.CustomException;
import com.sesac.joinflix.global.exception.ErrorCode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyInviteService {

    private final PartyInviteRepository partyInviteRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    @Value("${app.domain-url}")
    private String domainUrl;

    @Transactional
    public void inviteUsers(PartyRoom room, User host, List<Long> userIds) {
        saveInvites(room, host, userIds);
        sendInviteNotifications(room);
    }

    @Transactional
    public void saveInvites(PartyRoom room, User host, List<Long> userIds) {
        if (userIds.isEmpty()) {
            return;
        }

        List<User> guests = userRepository.findFriendsByHostAndIds(host, userIds);

        if (guests.size() != userIds.size()) {
            throw new CustomException(ErrorCode.INVALID_PARTY_INVITE_TARGET);
        }

        List<PartyInvite> invites = guests.stream()
            .map(guest -> PartyInvite.create(room, guest))
            .toList();

        partyInviteRepository.saveAll(invites);
    }

    public void sendInviteNotifications(PartyRoom room) {
        List<PartyInvite> invites = partyInviteRepository.findByPartyRoom(room);

        for (PartyInvite invite : invites) {
            User guest = invite.getGuest();

            // 파티 초대 이벤트 발행
            eventPublisher.publishEvent(
                new PartyInviteEvent(guest.getEmail(), room.getHost().getNickname(),
                    room.getRoomName(), String.format("%s/watch/party/%d", domainUrl, room.getId())
                ));
            String notificationMessage = NotificationMessageTemplate.notification(
                room.getHost().getNickname(), room.getRoomName(), guest.getNickname());
            notificationService.sendAndSave(guest.getId(), notificationMessage,
                NotificationType.PARTY_INVITE,
                null, null, room.getId());
        }
    }

    public void validateInvitation(PartyRoom partyRoom, User user) {
        partyInviteRepository.findByPartyRoomAndGuest(partyRoom, user)
            .orElseThrow(() -> new CustomException(ErrorCode.PARTY_ACCESS_DENIED));
    }

    public void deleteAllByPartyRoom(PartyRoom partyRoom) {
        partyInviteRepository.deleteAllByPartyRoom(partyRoom);
    }
}
