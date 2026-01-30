package com.sesac.joinflex.domain.party.service;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
import com.sesac.joinflex.domain.party.dto.request.PartyRoomRequest;
import com.sesac.joinflex.domain.party.dto.response.PartyRoomResponse;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
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
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final PartyInviteService partyInviteService;

    @Transactional
    public Long createPartyRoom(PartyRoomRequest request, Long userId) {
        Movie movie = movieRepository.findById(request.movieId())
            .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        User host = userRepository.findById(userId)
            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
            room.getRoomName(),
            room.getHost().getNickname(),
            room.getCurrentMemberCount()
        ));
    }
}
