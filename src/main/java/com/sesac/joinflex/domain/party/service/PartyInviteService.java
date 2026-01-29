package com.sesac.joinflex.domain.party.service;

import com.sesac.joinflex.domain.notification2.message.InviteMessageTemplate;
import com.sesac.joinflex.domain.notification2.service.NotificationService;
import com.sesac.joinflex.domain.party.entity.PartyInvite;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.party.repository.PartyInviteRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
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
            throw new IllegalArgumentException("친구 관계가 아니거나 존재하지 않는 사용자가 포함되어 있습니다.");
        }

        for (User guest : guests) {
            partyInviteRepository.save(PartyInvite.create(room, guest));
            // 이메일 발송
            sendInviteEmail(guest, room);
            String notificationMessage = InviteMessageTemplate.notification(
                room.getHost().getNickname(), room.getRoomName(), guest.getNickname());
            notificationService.send(guest.getId(), notificationMessage);

        }
    }

    private void sendInviteEmail(User guest, PartyRoom room) {
        String subject = "[JoinFlex] 파티 초대장이 도착했습니다!";

        String joinUrl = String.format("%s/parties/%d", domainUrl, room.getId());

        String message = InviteMessageTemplate.emailBody(room, joinUrl);

        emailService.sendEmail(guest.getEmail(), subject, message);
    }

}
