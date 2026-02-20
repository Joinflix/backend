package com.sesac.joinflix.domain.notification.message;

import lombok.experimental.UtilityClass;

@UtilityClass
public class NotificationMessageTemplate {

    public String emailSubject() {
        return "[JoinFlix] 파티 초대장이 도착했습니다!";
    }

    public String emailBody(String hostNickname, String roomName, String joinUrl) {
        return String.format("""
                %s님이 '%s' 파티에 초대했습니다.
                링크를 클릭해서 입장하세요: %s
                """,
            hostNickname, roomName, joinUrl);
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
