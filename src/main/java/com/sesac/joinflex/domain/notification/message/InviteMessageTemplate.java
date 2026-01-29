package com.sesac.joinflex.domain.notification.message;

import com.sesac.joinflex.domain.party.entity.PartyRoom;

public class InviteMessageTemplate {

    public static String emailBody(PartyRoom room, String joinUrl) {
        return String.format("""
                %s님이 '%s' 파티에 초대했습니다.
                링크를 클릭해서 입장하세요: %s
                """,
            room.getHost().getNickname(),
            room.getRoomName(),
            joinUrl);
    }

    public static String notification(String host, String roomName, String guest) {
        return String.format("%s님이 %s님을 '%s' 파티에 초대했습니다.", host, guest, roomName);
    }

}
