package com.sesac.joinflix.domain.party.scheduler;

import com.sesac.joinflix.domain.party.entity.PartyRoom;
import com.sesac.joinflix.domain.party.entity.PartyStatus;
import com.sesac.joinflix.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflix.domain.party.service.PartyInviteService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PartyScheduler {

    private final PartyRoomRepository partyRoomRepository;
    private final PartyInviteService partyInviteService;

    @Scheduled(cron = "0 0,30 * * * *")  // 매시 정각, 30분마다
    @Transactional
    public void activateScheduledParties() {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1);

        List<PartyRoom> partyRooms = partyRoomRepository.findByStatusAndScheduledAtLessThanEqual(
            PartyStatus.SCHEDULED, now);

        if (partyRooms.isEmpty()) {
            return;
        }

        for (PartyRoom room : partyRooms) {
            room.activate();
            partyInviteService.sendInviteNotifications(room);
        }
    }
}
