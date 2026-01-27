package com.sesac.joinflex.domain.party.service;

import com.sesac.joinflex.domain.movie.entity.Movie;
import com.sesac.joinflex.domain.movie.repository.MovieRepository;
import com.sesac.joinflex.domain.party.dto.request.PartyRoomRequest;
import com.sesac.joinflex.domain.party.entity.PartyRoom;
import com.sesac.joinflex.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 영화입니다."));

        // Todo security 구현 시 제거 예정
        User host = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // Todo membership 검증 예정 (결제한 사람만 파티 생성 가능)

        PartyRoom savedRoom = partyRoomRepository.save(
            PartyRoom.create(request.roomName(), host, movie, request.isPublic(),
                request.hostControl(),
                request.passCode()));

        // 친구 초대
        partyInviteService.inviteUsers(savedRoom, host, request.invitedUserIds());

        return savedRoom.getId();
    }

}
