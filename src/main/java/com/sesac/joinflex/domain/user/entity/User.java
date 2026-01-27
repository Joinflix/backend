package com.sesac.joinflex.domain.user.entity;

import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "password") // 소셜 계정은 비밀번호 없을 수 있음
    private String password;

    @Builder.Default
    @Column(name = "is_lock", nullable = false)
    private Boolean isLock = false;

    @Builder.Default
    @Column(name = "is_social", nullable = false)
    private Boolean isSocial = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "role_type", nullable = false)
    private UserRoleType roleType = UserRoleType.USER;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    private String profileImageUrl;

    @Column(name = "signup_ip")
    private String signupIp;

    @Column(name = "last_login_ip")
    private String lastLoginIp;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "membership_id")
//    private Membership membership;

    @Column(name = "membership_expiry_date")
    private LocalDateTime membershipExpiryDate;

    // 로그인 시 호출할 메서드
    public void updateLoginInfo(String ip) {
        this.lastLoginIp = ip;
    }

    // 자체 가입 유저가 소셜 로그인 시 연동 처리
    public void linkSocial(SocialProviderType providerType) {
        this.isSocial = true;
        this.socialProviderType = providerType;
    }

//    // 멤버십 업데이트 (결제 완료 후 호출)
//    public void updateMembership(Membership membership, int days) {
//        this.membership = membership;
//        if(days <= 0) {this.membershipExpiryDate = null;}
//        else{this.membershipExpiryDate = LocalDateTime.now().plusDays(days);}
//    }
//
//    // 서비스 이용 가능 여부 확인
//    public boolean canUseService() {
//        return this.membership != null &&
//                (membershipExpiryDate != null && membershipExpiryDate.isAfter(LocalDateTime.now()));
//    }

    // 사용자 프로필 수정
    public void updateProfile(String nickname, String profileImageUrl) {
        if (nickname != null) this.nickname = nickname;
        if (profileImageUrl != null) this.profileImageUrl = profileImageUrl;
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
