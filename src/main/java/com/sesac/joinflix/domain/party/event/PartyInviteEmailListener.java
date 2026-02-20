package com.sesac.joinflix.domain.party.event;

import com.sesac.joinflix.domain.notification.message.NotificationMessageTemplate;
import com.sesac.joinflix.global.infra.mail.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PartyInviteEmailListener {

    private final EmailService emailService;

    // 트랜잭션이 커밋되면 발송
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handlePartyInviteEvent(PartyInviteEvent event) {
        String subject = NotificationMessageTemplate.emailSubject();

        String message = NotificationMessageTemplate.emailBody(event.hostNickname(),
            event.roomName(), event.joinUrl());

        emailService.sendEmail(event.guestEmail(), subject, message);
    }
}
