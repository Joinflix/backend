package com.sesac.joinflex.global.config;

import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.domain.membership.entity.MembershipType;
import com.sesac.joinflex.domain.membership.repository.MembershipRepository;
import com.sesac.joinflex.domain.user.entity.User;
import com.sesac.joinflex.domain.user.entity.UserRoleType;
import com.sesac.joinflex.domain.user.repository.UserRepository;
import com.sesac.joinflex.global.exception.CustomException;
import com.sesac.joinflex.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 멤버십 데이터 초기화 (없는 것만 생성)
        initializeMemberships();
        Membership membership = membershipRepository.findByType(MembershipType.FREE)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBERSHIP_NOT_FOUND));

        // 2. 사용자 생성 (이미 존재하지 않는 경우에만 생성하도록 체크하는 것이 좋음)
        if (userRepository.count() == 0) {
            String encodedPassword = passwordEncoder.encode("test1234");

            User test = userRepository.save(
                    User.builder()
                            .email("test@test.com")
                            .password(encodedPassword)
                            .signupIp("127.0.0.1")
                            .nickname("test")
                            .roleType(UserRoleType.USER)
                            .isSocial(false)
                            .isLock(false)
                            .build()
            );

            test.updateMembership(membership, 0);
        }
    }

    private void initializeMemberships() {
        createMembershipIfNotFound(
                MembershipType.FREE,
                "무료 체험",
                "기본적인 서비스 탐색이 가능한 무료 플랜입니다.",
                0,
                "480p",
                1
        );
        createMembershipIfNotFound(
                MembershipType.STANDARD_WITH_ADS,
                "광고형 스탠다드",
                "광고와 함께 즐기는 합리적인 가격의 플랜입니다. 일부 콘텐츠 제외.",
                5500,
                "1080p",
                2
        );
        createMembershipIfNotFound(
                MembershipType.STANDARD,
                "스탠다드",
                "무광고로 즐기는 표준 플랜입니다. 두 대의 기기에서 동시 시청 가능.",
                13500,
                "1080p",
                2
        );
        createMembershipIfNotFound(
                MembershipType.PREMIUM,
                "프리미엄",
                "최고의 화질과 공간 음향을 제공합니다. 최대 4대 기기 동시 시청.",
                17000,
                "4K + HDR",
                4
        );
    }

    private void createMembershipIfNotFound(MembershipType type, String displayName, String description, Integer price, String resolution, Integer maxConcurrent) {
        // MembershipRepository에 findByType 또는 existsByType 메서드가 필요합니다.
        if (!membershipRepository.existsByType(type)) {
            Membership membership = Membership.builder()
                    .type(type)
                    .displayName(displayName)
                    .description(description)
                    .price(price)
                    .resolution(resolution)
                    .maxConcurrent(maxConcurrent)
                    .build();
            membershipRepository.save(membership);
        }
    }
}