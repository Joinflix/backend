package com.sesac.joinflex.domain.party.service;

import com.sesac.joinflex.domain.notification.message.NotificationMessageTemplate;
import com.sesac.joinflex.domain.notification.service.NotificationService;
import com.sesac.joinflex.domain.notification.type.NotificationType;
import com.sesac.joinflex.domain.party.entity.PartyInvite;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.party.repository.PartyInviteRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.sesac.joinflex.global.infra.mail.EmailService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyInviteService {

    private final PartyInviteRepository partyInviteRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Value("${app.domain-url}")
    private String domainUrl;

    public void inviteUsers(PartyRoom room, User host, List<Long> userIds) {
        List<User> guests = userRepository.findFriendsByHostAndIds(host, userIds);

        if (guests.size() != userIds.size()) {
            throw new CustomException(ErrorCode.INVALID_PARTY_INVITE_TARGET);
        }

        for (User guest : guests) {
            partyInviteRepository.save(PartyInvite.create(room, guest));
            // 이메일 발송
            sendInviteEmail(guest, room);
            String notificationMessage = NotificationMessageTemplate.notification(
                room.getHost().getNickname(), room.getRoomName(), guest.getNickname());
            notificationService.sendAndSave(guest.getId(), notificationMessage, NotificationType.PARTY_INVITE);

        }
    }

    public void validateInvitation(PartyRoom partyRoom, User user) {
        partyInviteRepository.findByPartyRoomAndGuest(partyRoom, user)
            .orElseThrow(() -> new CustomException(ErrorCode.PARTY_ACCESS_DENIED));
    }

    private void sendInviteEmail(User guest, PartyRoom room) {
        String subject = "[JoinFlex] 파티 초대장이 도착했습니다!";

        String joinUrl = String.format("%s/parties/%d", domainUrl, room.getId());

        String message = NotificationMessageTemplate.emailBody(room, joinUrl);

        emailService.sendEmail(guest.getEmail(), subject, message);
    }
}
