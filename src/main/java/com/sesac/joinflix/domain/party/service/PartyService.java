package com.sesac.joinflix.domain.party.service;

import com.sesac.joinflix.domain.chat.dto.request.LeaveRequest;
import com.sesac.joinflix.domain.chat.dto.request.VideoSyncRequest;
import com.sesac.joinflix.domain.movie.entity.Movie;
import com.sesac.joinflix.domain.movie.repository.MovieRepository;
import com.sesac.joinflix.domain.party.dto.request.PartyJoinRequest;
import com.sesac.joinflix.domain.party.dto.request.PartyRoomRequest;
import com.sesac.joinflix.domain.party.dto.response.MemberResponse;
import com.sesac.joinflix.domain.party.dto.response.PartyRoomResponse;
import com.sesac.joinflix.domain.party.dto.response.VideoStatus;
import com.sesac.joinflix.domain.party.entity.MemberRole;
import com.sesac.joinflix.domain.party.entity.MemberStatus;
import com.sesac.joinflix.domain.party.entity.PartyMember;
import com.sesac.joinflix.domain.party.entity.PartyRoom;
import com.sesac.joinflix.domain.party.repository.PartyMemberRepository;
import com.sesac.joinflix.domain.party.repository.PartyRoomRepository;
import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.domain.user.repository.UserRepository;
import com.sesac.joinflix.global.exception.CustomException;
import com.sesac.joinflix.global.exception.ErrorCode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PartyService {

    private static final String VIDEO_KEY_PREFIX = "party:";
    private static final String VIDEO_KEY_SUFFIX = ":video";
    private static final String REDIS_FIELD_CURRENT_TIME = "currentTime";
    private static final String REDIS_FIELD_PAUSED = "paused";
    private static final String REDIS_FIELD_UPDATED_AT = "updatedAt";

    private final PartyRoomRepository partyRoomRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final PartyInviteService partyInviteService;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public Long createPartyRoom(PartyRoomRequest request, Long userId) {
        Movie movie = movieRepository.findById(request.movieId())
            .orElseThrow(() -> new CustomException(ErrorCode.MOVIE_NOT_FOUND));

        User host = getUser(userId);
        host.validatePaidMembership();

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

        return rooms.map(PartyRoomResponse::of);
    }


    @Transactional
    public PartyRoomResponse joinParty(Long partyId, PartyJoinRequest request, Long userId) {
        // 파티방 존재 여부 검증
        PartyRoom partyRoom = getPartyRoom(partyId);

        // 사용자 검증
        User user = getUser(userId);
        user.validatePaidMembership();

        validateNotJoined(partyRoom, user);

        processEntry(partyRoom, user, request);

        VideoStatus videoStatus = getCalculatedVideoStatus(partyId);

        return PartyRoomResponse.of(partyRoom, videoStatus);
    }

    @Transactional
    public Optional<Integer> leavePartyRoom(Long partyId, Long userId,
        LeaveRequest request) {
        PartyRoom partyRoom = getPartyRoom(partyId);
        User user = getUser(userId);

        PartyMember member = partyMemberRepository.findByPartyRoomAndMemberAndStatus(partyRoom,
                user, MemberStatus.JOINED)
            .orElse(null);

        if (member == null) {
            return Optional.empty();
        }

        // 일반 사용자
        if (!member.isHost()) {
            return leaveAsGuest(partyRoom, member);
        }

        // 방장 && 공개방
        if (partyRoom.isPublicRoom()) {
            return deleteRoomAndReturn(partyRoom);
        }

        // 방장 && 비공개방
        Long targetId = (request != null) ? request.targetMemberId() : null;
        if (targetId != null) {
            return transferHostAndLeave(partyRoom, member, targetId);

        }

        deletePartyRoom(partyRoom);
        return Optional.of(0);
    }

    private Optional<Integer> leaveAsGuest(PartyRoom partyRoom, PartyMember member) {
        member.leave();
        partyRoom.leaveMember();
        return Optional.of(partyRoom.getCurrentMemberCount());
    }

    private Optional<Integer> deleteRoomAndReturn(PartyRoom partyRoom) {
        deletePartyRoom(partyRoom);
        return Optional.of(0);
    }

    private Optional<Integer> transferHostAndLeave(PartyRoom partyRoom,
        PartyMember currentHost, Long targetId) {
        delegateHost(partyRoom, getUser(targetId));
        currentHost.leave();
        partyRoom.leaveMember();
        return Optional.of(partyRoom.getCurrentMemberCount());
    }

    private void deletePartyRoom(PartyRoom partyRoom) {
        partyInviteService.deleteAllByPartyRoom(partyRoom);
        partyMemberRepository.deleteAllByPartyRoom(partyRoom);
        partyRoomRepository.delete(partyRoom);
    }

    private void delegateHost(PartyRoom partyRoom, User targetMember) {
        PartyMember nextHost = partyMemberRepository.findByPartyRoomAndMemberAndStatus(partyRoom,
            targetMember, MemberStatus.JOINED).orElseThrow(() -> new RuntimeException());

        // 방장 위임
        nextHost.changeRole();
        partyRoom.changeHost(targetMember);
    }

    public PartyRoomResponse getPartyRoomResponse(Long partyId) {
        PartyRoom partyRoom = getPartyRoom(partyId);
        return PartyRoomResponse.of(partyRoom);
    }

    public List<MemberResponse> getMembers(Long partyId, Long userId) {
        PartyRoom partyRoom = getPartyRoom(partyId);
        List<PartyMember> members = partyMemberRepository.findOtherMembersWithFetch(userId,
            partyRoom, MemberStatus.JOINED);

        return members.stream().map(member -> new MemberResponse(member.getMember().getId(),
            member.getMember().getNickname())).toList();
    }

    public boolean canControlVideo(Long partyId, Long userId) {
        PartyRoom partyRoom = getPartyRoom(partyId);
        User user = getUser(userId);

        PartyMember member = partyMemberRepository.findByPartyRoomAndMemberAndStatus(partyRoom,
                user, MemberStatus.JOINED)
            .orElse(null);

        if (member == null) {
            return false;
        }

        return !partyRoom.getHostControl() || member.isHost();
    }

    public void saveVideoStatus(Long partyId, VideoSyncRequest request) {
        String key = VIDEO_KEY_PREFIX + partyId + VIDEO_KEY_SUFFIX;

        Map<String, String> videoStatus = new HashMap<>();
        videoStatus.put(REDIS_FIELD_CURRENT_TIME, String.valueOf(request.currentTime()));
        videoStatus.put(REDIS_FIELD_PAUSED, String.valueOf(request.paused()));
        videoStatus.put(REDIS_FIELD_UPDATED_AT, String.valueOf(System.currentTimeMillis()));

        redisTemplate.opsForHash().putAll(key, videoStatus);

        // Todo redis 만료 시간
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

    private VideoStatus getCalculatedVideoStatus(Long partyId) {
        String key = VIDEO_KEY_PREFIX + partyId + VIDEO_KEY_SUFFIX;

        Map<String, String> status = redisTemplate.<String, String>opsForHash().entries(key);

        if (status.isEmpty()) {
            return new VideoStatus(0.0, true); // 0초, 일시정지 상태
        }

        double savedTime = Double.parseDouble(status.get(REDIS_FIELD_CURRENT_TIME));
        boolean isPaused = Boolean.parseBoolean(status.get(REDIS_FIELD_PAUSED));
        long updatedAt = Long.parseLong(status.get(REDIS_FIELD_UPDATED_AT));

        double finalTime = savedTime;
        if (!isPaused) {
            // 재생 중이면 현재시간 - 저장 시점 시간
            double diff = (System.currentTimeMillis() - updatedAt) / 1000.0;
            finalTime += diff;
        }

        return new VideoStatus(finalTime, isPaused);
    }
}
