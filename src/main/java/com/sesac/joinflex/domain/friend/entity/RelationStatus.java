package com.sesac.joinflex.domain.friend.entity;

public enum RelationStatus {
    NONE,
    SENT_PENDING, //내가 보냄 '대기중'
    RECEIVED_PENDING, //상대가 보냄 '수락/거절'
    FRIEND //친구 상태
}
