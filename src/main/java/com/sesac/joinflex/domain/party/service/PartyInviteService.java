package com.sesac.joinflex.domain.party.service;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyInviteService {

    private final String DOMAIN_URL = "http://localhost:8080";

    private final PartyInviteRepository partyInviteRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;


    public void inviteUsers(PartyRoom room, User host, List<Long> userIds) {
        List<User> guests = userRepository.findFriendsByHostAndIds(host, userIds);

        if (guests.size() != userIds.size()) {
            throw new CustomException(ErrorCode.INVALID_PARTY_INVITE_TARGET);
        }

        for (User guest : guests) {
            partyInviteRepository.save(PartyInvite.create(room, guest));
            // 이메일 발송
            sendInviteEmail(guest, room);
        }
    }

    private void sendInviteEmail(User guest, PartyRoom room) {
        String subject = "[JoinFlex] 파티 초대장이 도착했습니다!";

        String joinUrl = String.format("%s/parties/%d", DOMAIN_URL, room.getId());

        String message = String.format("""
                %s님이 '%s' 파티에 초대했습니다.
                링크를 클릭해서 입장하세요: %s
                """,
            room.getHost().getNickname(),
            room.getRoomName(),
            joinUrl);

        emailService.sendEmail(guest.getEmail(), subject, message);
    }

}
