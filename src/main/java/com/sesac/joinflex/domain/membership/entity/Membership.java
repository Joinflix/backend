package com.sesac.joinflex.domain.membership.entity;

import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Table(name = "memberships")
@NoArgsConstructor
public class Membership extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private MembershipType type;

    private String displayName;
    private String description;
    private Integer price;
    private String resolution;
    private Integer maxConcurrent; // 동시 접속 제한 수치

    @Builder
    private Membership(Integer maxConcurrent, String resolution, Integer price, String description, String displayName, MembershipType type) {
        this.maxConcurrent = maxConcurrent;
        this.resolution = resolution;
        this.price = price;
        this.description = description;
        this.displayName = displayName;
        this.type = type;
    }
}
