package com.sesac.joinflex.domain.party.service;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
import com.sesac.joinflex.domain.party.dto.request.PartyJoinRequest;
import com.sesac.joinflex.domain.party.dto.request.PartyRoomRequest;
import com.sesac.joinflex.domain.party.dto.response.PartyRoomResponse;
import com.sesac.joinflex.domain.party.entity.MemberRole;
import com.sesac.joinflex.domain.party.entity.MemberStatus;
import com.sesac.joinflex.domain.party.entity.PartyMember;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.party.repository.PartyMemberRepository;
import com.sesac.joinflex.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private final PartyRoomRepository partyRoomRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final PartyInviteService partyInviteService;

    @Transactional
    public Long createPartyRoom(PartyRoomRequest request, Long userId) {
        Movie movie = movieRepository.findById(request.movieId())
            .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        User host = getUser(userId);

        // Todo membership 검증 예정 (결제한 사람만 파티 생성 가능)

        PartyRoom savedRoom = partyRoomRepository.save(
            PartyRoom.create(request.roomName(), host, movie, request.isPublic(),
                request.hostControl(),
                request.passCode()));

        // 친구 초대
        partyInviteService.inviteUsers(savedRoom, host, request.invitedUserIds());

        return savedRoom.getId();
    }


    public Slice<PartyRoomResponse> getPartyRooms(Long cursorId, Pageable pageable) {

        Slice<PartyRoom> rooms = partyRoomRepository.findPartyRooms(
            cursorId == null ? Long.MAX_VALUE : cursorId, pageable);

        return rooms.map(room -> new PartyRoomResponse(
            room.getId(),
            room.getMovie().getTitle(),
            room.getMovie().getBackdrop(),
            room.getIsPublic(),
            room.getRoomName(),
            room.getHost().getNickname(),
            room.getCurrentMemberCount()
        ));
    }


    @Transactional
    public PartyRoomResponse joinParty(Long partyId, PartyJoinRequest request, Long userId) {
        // 파티방 존재 여부 검증
        PartyRoom partyRoom = getPartyRoom(partyId);

        // 사용자 검증
        User user = getUser(userId);

        // Todo membership 검증 예정 (결제한 사람만 파티 참여 가능)

        validateNotJoined(partyRoom, user);

        processEntry(partyRoom, user, request);

        return new PartyRoomResponse(partyRoom.getId(), partyRoom.getMovie().getTitle(), partyRoom.getMovie().getBackdrop(), partyRoom.getIsPublic(),
                partyRoom.getRoomName(), partyRoom.getHost().getNickname(), partyRoom.getCurrentMemberCount());
    }

    @Transactional
    public Optional<Integer> leavePartyRoom(Long partyId, Long userId) {
        PartyRoom partyRoom = getPartyRoom(partyId);
        User user = getUser(userId);

        return partyMemberRepository.findByPartyRoomAndMemberAndStatus(partyRoom, user, MemberStatus.JOINED)
            .map(member -> {
                member.leave();
                partyRoom.leaveMember();
                return partyRoom.getCurrentMemberCount(); // 실제 처리가 되었을 때만 인원수 반환
            });
    }

    private PartyRoom getPartyRoom(Long partyId) {
        return partyRoomRepository.findById(partyId)
            .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private void validateNotJoined(PartyRoom partyRoom, User user) {
        if (partyMemberRepository.existsByPartyRoomAndMemberAndStatus(partyRoom, user,
            MemberStatus.JOINED)) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_PARTY);
        }
    }

    private void processEntry(PartyRoom partyRoom, User user, PartyJoinRequest request) {
        // 방장이면 바로 입장
        if (partyRoom.isHost(user)) {
            addMember(partyRoom, user, MemberRole.HOST);
            return;
        }

        // 공개방이면 바로 입장
        if (partyRoom.isPublicRoom()) {
            addMember(partyRoom, user, MemberRole.GUEST);
            return;
        }

        // 비공개방 입장
        joinPrivateRoom(partyRoom, request, user);
    }

    private void joinPrivateRoom(PartyRoom partyRoom, PartyJoinRequest request, User user) {
        // 초대받은 사용자인지 검증
        partyInviteService.validateInvitation(partyRoom, user);

        // 비밀번호 일치 여부
        if (!partyRoom.isPasswordMatch(request.passCode())) {
            throw new CustomException(ErrorCode.INVALID_PARTY_PASSWORD);
        }

        addMember(partyRoom, user, MemberRole.GUEST);
    }

    private void addMember(PartyRoom partyRoom, User user, MemberRole role) {
        partyMemberRepository.save(PartyMember.create(partyRoom, user, role));
        partyRoom.addMember();
    }

}
