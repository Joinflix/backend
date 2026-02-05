package com.sesac.joinflex.domain.notification.message;

import com.sesac.joinflex.domain.party.entity.PartyRoom;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMessageTemplate {

    public String emailBody(PartyRoom room, String joinUrl) {
        return String.format("""
                %s님이 '%s' 파티에 초대했습니다.
                링크를 클릭해서 입장하세요: %s
                """,
            room.getHost().getNickname(),
            room.getRoomName(),
            joinUrl);
    }

    public String notification(String host, String roomName, String guest) {
        return String.format("%s님이 %s님을 '%s' 파티에 초대했습니다.", host, guest, roomName);
    }

    public String friendRequest(String senderNickname) {
        return String.format("%s님이 친구 신청을 하였습니다.", senderNickname);
    }

    public String friendAccept(String receiverNickname) {
        return String.format("%s님이 친구 신청을 수락하였습니다.", receiverNickname);
    }

    public String eventReject() {
        return "친구 신청 거절 이벤트 발생";
    }

    public String eventCancel() {
        return "친구 신청 취소 이벤트 발생";
    }
    public String eventDelete() {
        return "친구 삭제 이벤트 발생";
    }


}
