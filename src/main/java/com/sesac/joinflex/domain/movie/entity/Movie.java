package com.sesac.joinflex.domain.movie.entity;

import com.sesac.joinflex.global.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "movies")
@Entity
public class Movie extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String poster;

    private String backdrop;

    @Column(columnDefinition = "TEXT")
    private String description;

}
