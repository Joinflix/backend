package com.sesac.joinflex.domain.user.entity;

import com.sesac.joinflex.domain.membership.entity.Membership;
import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password") // 소셜 계정은 비밀번호 없을 수 있음
    private String password;

    @Column(name = "is_lock", nullable = false)
    private Boolean isLock;

    @Column(name = "is_social", nullable = false)
    private Boolean isSocial;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private UserRoleType roleType;

    @Column(name = "nickname", nullable = false, unique = true, length = 100)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String profileImageUrl;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;

    @Column(name = "signup_ip")
    private String signupIp;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membership_id")
    private Membership membership;

    @Column(name = "membership_expiry_date")
    private LocalDateTime membershipExpiryDate;

    @Builder
    private User(String email, String password, String nickname, String signupIp,
                 Boolean isLock, Boolean isSocial, SocialProviderType socialProviderType,
                 UserRoleType roleType, String profileImageUrl, Boolean isOnline) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.signupIp = signupIp;
        this.isLock = (isLock != null) ? isLock : false;
        this.isSocial = (isSocial != null) ? isSocial : false;
        this.roleType = (roleType != null) ? roleType : UserRoleType.USER;
        this.isOnline = (isOnline != null) ? isOnline : false;
        this.socialProviderType = socialProviderType;
        this.profileImageUrl = profileImageUrl;
    }

    // 로그인 시 호출할 메서드
    public void updateLoginInfo(String ip) {
        this.lastLoginIp = ip;
        this.isOnline = true;
    }

    // 자체 가입 유저가 소셜 로그인 시 연동 처리
    public void linkSocial(SocialProviderType providerType) {
        this.isSocial = true;
        this.socialProviderType = providerType;
    }

    // 멤버십 업데이트 (결제 완료 후 호출)
    public void updateMembership(Membership membership, Long months) {
        this.membership = membership;

        // 멤버십 회수(null) 시 처리
        if (membership == null) {
            this.membershipExpiryDate = null;
            return;
        }

        // 현재 멤버십이 남아있다면 그 날짜로부터 더하고, 없다면 지금부터 더함
        LocalDateTime baseDate = (this.membershipExpiryDate != null && this.membershipExpiryDate.isAfter(LocalDateTime.now()))
                ? this.membershipExpiryDate
                : LocalDateTime.now();

        this.membershipExpiryDate = baseDate.plusMonths(months);
    }

    // 서비스 이용 가능 여부 확인
    public Boolean canUseService() {
        return this.membership != null &&
                (membershipExpiryDate != null && membershipExpiryDate.isAfter(LocalDateTime.now()));
    }

    // 사용자 프로필 수정
    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null) this.nickname = nickname;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
    }

    // 사용자 상태 변경(온라인 -> 오프라인)
    public void updateOfflineStatus() {
        this.isOnline = false;
    }

    // 계정 잠금
    public void lockAccount() {
        this.isLock = true;
    }

    // 계정 잠금 해제
    public void unlockAccount() {
        this.isLock = false;
    }
}
