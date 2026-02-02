package com.sesac.joinflex.domain.user.service;

import com.sesac.joinflex.domain.user.dto.request.ProfileUpdateRequest;
import com.sesac.joinflex.domain.user.dto.response.UserProfileResponse;
import com.sesac.joinflex.domain.user.dto.response.UserResponse;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.domain.userhistory.entity.UserAction;
import com.sesac.joinflex.domain.userhistory.service.UserHistoryService;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import com.sesac.joinflex.global.util.NetworkUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserHistoryService userHistoryService;

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    public User save(User user){
        return userRepository.save(user);
    }

    // 인증 시 유저 조회 공통 메서드
    public User findByEmailForAuth(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 닉네임 중복 체크
    public void validateNickname(String nickname){
        if(userRepository.existsByNickname(nickname)){
            throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
        }
    }

    // 사용자 유효성 체크
    public void validateNewUser(String email, String nickname, String ip) {
        // IP 기반 가입 횟수 제한 (24시간 내 5회)
        // 테스트 시 24시간 -> 1분, 5회 -> 2회로 설정
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        long signupCount = userRepository.countBySignupIpAndCreatedAtAfter(ip, oneDayAgo);

        if (signupCount >= 5) {
            throw new CustomException(ErrorCode.TOO_MANY_REGISTRATION_ATTEMPTS);
        }

        if (userRepository.existsByEmail(email)) throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        if (userRepository.existsByNickname(nickname)) throw new CustomException(ErrorCode.DUPLICATE_NICKNAME);
    }

    // 사용자 프로필 조회
    public UserProfileResponse getProfile(Long id, Long currentUserId) {
        validateUser(id, currentUserId);
        return UserProfileResponse.of(getUser(id));
    }

    // 사용자 프로필 수정
    public UserResponse updateProfile(Long id, Long currentUserId, ProfileUpdateRequest request, HttpServletRequest httpRequest){
        validateUser(id, currentUserId);
        User user = getUser(id);
        String ip = NetworkUtil.getClientIp(httpRequest);
        String ua = NetworkUtil.getUserAgent(httpRequest);

        String oldNickname = user.getNickname();
        user.updateProfile(request.getNickname(), request.getProfileImageUrl());

        // 닉네임 변경 시 기록
        if (!oldNickname.equals(request.getNickname())) {
            userHistoryService.saveLog(user.getEmail(), UserAction.NICKNAME_CHG, ip, ua, true, "닉네임 변경: " + oldNickname + " -> " + request.getNickname());
        }
        return UserResponse.from(user);
    }

    // 본인 확인
    private void validateUser(Long id, Long currentUserId) {
        if(!id.equals(currentUserId))
            throw new CustomException(ErrorCode.NOT_OWNER);
    }

    // 사용자 조회
    private User getUser(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    // 멤버쉽 만료 유저 조회
    public List<User> findAllByMembershipExpiryDateBefore(LocalDateTime now) {
        return userRepository.findAllByMembershipExpiryDateBefore(now);
    }
}
