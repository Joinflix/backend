package com.sesac.joinflix.domain.friend.service;

import com.sesac.joinflix.domain.friend.entity.FriendRequest;
import com.sesac.joinflix.domain.friend.entity.Friendship;
import com.sesac.joinflix.domain.friend.repository.FriendRequestRepository;
import com.sesac.joinflix.domain.friend.repository.FriendshipRepository;
import com.sesac.joinflix.domain.user.dto.response.UserSearchResponse;
import com.sesac.joinflix.domain.user.entity.User;
import com.sesac.joinflix.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FriendshipService {

    private final FriendRequestRepository friendRequestRepository;
    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;

    public Slice<UserSearchResponse> getAllFriendList(Long myId,
                                                      String primaryFilter,
                                                      String secondaryFilter,
                                                      String searchWord,
                                                      Pageable pageable) {
        List<UserSearchResponse> all = new ArrayList<>();

        // 1. 나에게 온 신청 (REQUESTED)
        if (primaryFilter.equals("ALL") || (primaryFilter.equals("FRIEND") && (secondaryFilter.equals("ALL") || secondaryFilter.equals("REQUESTED")))) {
            List<FriendRequest> requested = friendRequestRepository.findAllReceivedPending(myId, searchWord);
            all.addAll(requested.stream()
                    .map(r -> UserSearchResponse.builder()
                            .userId(r.getSender().getId())
                            .nickname(r.getSender().getNickname())
                            .email(r.getSender().getEmail())
                            .profileImageUrl(r.getSender().getProfileImageUrl())
                            .status("RECEIVED_PENDING")
                            .requestId(r.getId()) // 수락/거절을 위해 requestId 필요
                            .priority(1).build())
                    .toList());
        }

        // 2. 내가 보낸 신청 (PENDING)
        if (primaryFilter.equals("ALL") || (primaryFilter.equals("FRIEND") && (secondaryFilter.equals("ALL") || secondaryFilter.equals("PENDING")))) {
            List<FriendRequest> pending = friendRequestRepository.findAllSentPending(myId, searchWord);
            all.addAll(pending.stream()
                    .map(p -> UserSearchResponse.builder()
                            .userId(p.getReceiver().getId())
                            .nickname(p.getReceiver().getNickname())
                            .email(p.getReceiver().getEmail())
                            .profileImageUrl(p.getReceiver().getProfileImageUrl())
                            .status("SENT_PENDING")
                            .requestId(p.getId()) // 취소를 위해 requestId 필요
                            .priority(2).build())
                    .toList());
        }

        // 3. 친구 (ACCEPTED)
        if (primaryFilter.equals("ALL") || (primaryFilter.equals("FRIEND") && (secondaryFilter.equals("ALL") || secondaryFilter.equals("FRIEND")))) {

            // Map<상대방ID, 요청ID>
            List<FriendRequest> acceptedRequests = friendRequestRepository.findAllAcceptedByUserId(myId);
            Map<Long, Long> requestMap = acceptedRequests.stream()
                    .collect(Collectors.toMap(
                            r -> r.getSender().getId().equals(myId) ? r.getReceiver().getId() : r.getSender().getId(),
                            FriendRequest::getId,
                            (existing, replacement) -> existing // 중복 방지
                    ));

            List<Friendship> friends = friendshipRepository.findAllByUserIdWithSearch(myId, searchWord);
            all.addAll(friends.stream()
                    .map(f -> {
                        Long friendId = f.getFriend().getId();
                        return UserSearchResponse.builder()
                                .userId(friendId)
                                .nickname(f.getFriend().getNickname())
                                .email(f.getFriend().getEmail())
                                .profileImageUrl(f.getFriend().getProfileImageUrl())
                                .status("FRIEND")
                                .requestId(requestMap.get(friendId)) // Map에서 바로 꺼내기 (O(1))
                                .priority(3).build();
                    })
                    .toList());
        }

        // 4. 관계 없는 사람 (NONE)
        if (primaryFilter.equals("ALL")) {
            List<Long> excludeIds = new ArrayList<>(all.stream().map(UserSearchResponse::getUserId).toList());
            excludeIds.add(myId);

            List<User> strangers = userRepository.findAllExcludingIds(searchWord, excludeIds);
            all.addAll(strangers.stream()
                    .map(s -> UserSearchResponse.builder()
                            .userId(s.getId())
                            .nickname(s.getNickname())
                            .email(s.getEmail())
                            .profileImageUrl(s.getProfileImageUrl())
                            .status("NONE")
                            .priority(4).build())
                    .toList());
        }

        // 정렬 (Priority 순 -> 닉네임 순)
        all.sort(Comparator.comparing(UserSearchResponse::getPriority)
                .thenComparing(UserSearchResponse::getNickname));

        return toSlice(all, pageable);
    }

    private Slice<UserSearchResponse> toSlice(List<UserSearchResponse> allContents, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allContents.size());

        // 시작 지점이 리스트 크기를 넘어버리면 빈 리스트 반환
        if (start > allContents.size()) {
            return new SliceImpl<>(Collections.emptyList(), pageable, false);
        }

        // 다음 페이지가 있는지 확인 (현재 끝 인덱스가 전체 리스트 크기보다 작은지 체크)
        boolean hasNext = start + pageable.getPageSize() < allContents.size();

        // 현재 페이지에 해당하는 부분만 자르기
        List<UserSearchResponse> content = allContents.subList(start, end);

        return new SliceImpl<>(content, pageable, hasNext);
    }
}
