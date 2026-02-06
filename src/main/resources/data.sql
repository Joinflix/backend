-- 1. 멤버십 데이터 (Memberships)
-- 순서: type, display_name, description, price, resolution, max_concurrent
INSERT INTO memberships (type, display_name, description, price, resolution, max_concurrent, created_at, updated_at)
VALUES ('FREE', '무료 체험', '기본적인 서비스 탐색이 가능한 무료 플랜입니다.', 0, '480p', 1, NOW(), NOW()),
       ('STANDARD_WITH_ADS', '광고형 스탠다드', '광고와 함께 즐기는 합리적인 가격의 플랜입니다. 일부 콘텐츠 제외.', 100, '1080p', 2, NOW(), NOW()),
       ('STANDARD', '스탠다드', '무광고로 즐기는 표준 플랜입니다. 두 대의 기기에서 동시 시청 가능.', 200, '1080p', 2, NOW(), NOW()),
       ('PREMIUM', '프리미엄', '최고의 화질과 공간 음향을 제공합니다. 최대 4대 기기 동시 시청.', 300, '4K + HDR', 4, NOW(), NOW());

-- 2. 사용자 데이터 (Users)
-- 비밀번호: 'test1234' (BCrypt 암호화)
-- 멤버십 ID 1번(FREE)을 기본 할당한다고 가정
INSERT INTO users (email, password, nickname, signup_ip, role_type, is_social, is_lock, is_online, membership_id, last_notification_read_at, created_at, updated_at)
VALUES ('test@test.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', 'test', '127.0.0.1', 'USER', false, false, false, 1,  null, NOW(), NOW()),
       ('kim@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '김철수', '127.0.0.2', 'USER', false, false, false, 1,  null, NOW(), NOW()),
       ('lee@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '이영희', '127.0.0.3', 'USER', false, false, false, 1,  null, NOW(), NOW()),
       ('park@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '박민수', '127.0.0.4', 'USER', false, false, false, 1,  null, NOW(), NOW()),
       ('choi@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '최지은', '127.0.0.5', 'USER', false, false, false, 1,  null, NOW(), NOW()),
       ('jung@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '정우성', '127.0.0.6', 'USER', false, false, false, 1,  null, NOW(), NOW());

-- 3. 친구 요청 (Friend Requests)
INSERT INTO friend_requests (sender_id, receiver_id, status, created_at, updated_at)
VALUES (1, 2, 'ACCEPTED', NOW(), NOW()),
       (2, 3, 'ACCEPTED', NOW(), NOW()),
       (1, 5, 'PENDING', NOW(), NOW()),
       (4, 1, 'PENDING', NOW(), NOW());

-- 4. 영화 (Movies)
INSERT INTO movies (title, poster, backdrop, description, created_at, updated_at)
VALUES
    ('A Trip to the Moon (1902)',
     'https://m.media-amazon.com/images/M/MV5BODM2ODk2OTgtYzEwMy00MDVlLWE0Y2ItMDFjNGEwNjYzZWViXkEyXkFqcGc@._V1_FMjpg_UX902_.jpg',
     'https://m.media-amazon.com/images/M/MV5BMTQ1NDk1MTAwMF5BMl5BanBnXkFtZTcwODQ3NzMyNQ@@._V1_.jpg',
     'Georges Méliès'' imaginative masterpiece is one of the earliest examples of science fiction on film. Known for its iconic shot of a rocket landing in the eye of the Man in the Moon, it pioneered innovative special effects, theatrical set design, and narrative fantasy.',
     NOW(), NOW()),

    ('Battleship Potemkin (1925)',
     'https://m.media-amazon.com/images/M/MV5BZmY5ZGQwMTEtM2E0ZC00NzVkLTgyYzktZDYxMjhiMzA5N2MzXkEyXkFqcGc@._V1_FMjpg_UX857_.jpg',
     'https://m.media-amazon.com/images/M/MV5BNTE5NDA5MTgzNl5BMl5BanBnXkFtZTgwMzA5OTQwMjE@._V1_.jpg',
     'Sergei Eisenstein''s revolutionary silent film that pioneered montage theory. The Odessa Steps sequence remains a masterclass in tension, rhythm, and political storytelling through editing, shaping how cinema conveys emotion and narrative.',
     NOW(), NOW()),

    ('Un Chien Andalou (1929)',
     'https://m.media-amazon.com/images/M/MV5BNmIwNjhlZGMtN2JmNy00MmQ0LTljOGItYTNiMjI1ZWZlMGRiXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg',
     'https://m.media-amazon.com/images/M/MV5BMGE3NGU2NzMtNzMyOC00YjI3LWJjZTktM2VjNjFlOTk1NTI3XkEyXkFqcGc@._V1_.jpg',
     'Luis Buñuel and Salvador Dalí''s surrealist short film defies traditional narrative, creating dreamlike logic and shocking imagery. Its radical editing and symbolism reshaped the possibilities of visual storytelling.',
     NOW(), NOW()),

    ('Metropolis (1927)',
     'https://m.media-amazon.com/images/M/MV5BMjhjMGYyMjAtMzJkYy00NzhlLWIwY2MtMWQ2ODIxZDUyOGYyXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg',
     'https://m.media-amazon.com/images/M/MV5BMjE1MzEwMTU1M15BMl5BanBnXkFtZTgwNTk2NDkwMDE@._V1_.jpg',
     'Fritz Lang''s expressionist sci-fi epic set the standard for futuristic cityscapes and cinematic spectacle. Its monumental set design, social allegory, and visual effects influence filmmakers in both genre and art cinema.',
     NOW(), NOW()),

    ('Nosferatu (1922)',
     'https://m.media-amazon.com/images/M/MV5BOWE0NjI3OWUtNDFhNS00MDY5LWJlNzctMmUwZDIxMGRjZGNlXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg',
     'https://m.media-amazon.com/images/M/MV5BMTU0MDI5ODU5MF5BMl5BanBnXkFtZTgwMzU1MDM5MTE@._V1_.jpg',
     'F.W. Murnau''s unauthorized Dracula adaptation defined horror aesthetics with its haunting shadows and eerie atmosphere. The film''s use of negative space, lighting, and camera movement remains essential for understanding visual storytelling in genre cinema.',
     NOW(), NOW()),

    ('The Phantom of the Opera (1925)',
     'https://m.media-amazon.com/images/M/MV5BMGE4MDQ0ZjEtMWIwNi00YWVlLTk3NTQtMzViMGRlYjA4NzdlXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg',
     'https://m.media-amazon.com/images/M/MV5BOTFkM2MxNjktNGI0YS00ZDIwLWI0ZDctMTc5YTAyYmNlNzNlXkEyXkFqcGc@._V1_.jpg',
     'Lon Chaney''s legendary performance and makeup artistry brought horror and melodrama to vivid life. The film showcases early Hollywood spectacle, intricate set design, and expressionistic visuals, making it a masterclass in silent-era cinematic craft.',
     NOW(), NOW()),

    ('The General (1926)',
     'https://m.media-amazon.com/images/M/MV5BMTVhM2Y1MDUtMDkxYi00Y2UxLWI2MTMtZjMzYTY4ODM4MGIzXkEyXkFqcGc@._V1_FMjpg_UX1000_.jpg',
     'https://m.media-amazon.com/images/M/MV5BMTQ1MjIxMDY4OF5BMl5BanBnXkFtZTcwMTM0MDMzNA@@._V1_.jpg',
     'Buster Keaton''s silent comedy masterpiece blends physical stunts with precise visual storytelling. Renowned for its timing, spatial logic, and action choreography, it remains a benchmark for comedy, pacing, and cinematic ingenuity.',
     NOW(), NOW()),

    ('Steamboat Willie (1928)',
     'https://m.media-amazon.com/images/M/MV5BODhlNGQyZTAtNGE0ZS00OWVhLTkzODQtOTBiMmQ2ODZjNzI2XkEyXkFqcGc@._V1_.jpg',
     'https://m.media-amazon.com/images/M/MV5BY2ZjMGE0NzItNjEyNi00YjJhLWI1N2YtNWYzOGQ5NzQzODA2XkEyXkFqcGc@._V1_.jpg',
     'The historic debut of Mickey Mouse, this short film revolutionized the industry as one of the first cartoons with fully synchronized sound. Its playful use of music and rhythm transformed animation into a multi-sensory experience, launching a global cultural phenomenon.',
     NOW(), NOW());

-- 5. 파티방 (Party Rooms)
INSERT INTO party_rooms (host_id, movie_id, room_name, is_public, host_control, max_count, current_member_count, created_at, updated_at)
VALUES (4, 4, '재밌겠다', true, true, 4, 2, NOW(), NOW()), -- ID 4: 박민수 방
       (2, 5, '리메이크 기념', false, true, 4, 2, NOW(), NOW()), -- ID 2: 이영희 방
       (2, 7, '키튼', true, true, 4, 2, NOW(), NOW()), -- ID 2: 이영희 방
        (1, 8, '귀여워', false, true, 4, 2, NOW(), NOW()), -- ID 1: 김철수 방
       (1, 3, '징그러워', true, true, 4, 2, NOW(), NOW()), -- ID 1: 김철수 방
       (2, 2, '오데사', false, true, 4, 2, NOW(), NOW()), -- ID 2: 이영희 방
       (3, 6, '흥미진진', true, true, 4, 2, NOW(), NOW()), -- ID 3: 정우성 방
       (5, 1, '같이 영화봐요', true, true, 4, 2,NOW(), NOW()); -- ID 5:  최지은 방

-- 6. 파티 초대 (Party Invites)
INSERT INTO party_invites (party_room_id, guest_id, created_at, updated_at)
VALUES (1, 2,  NOW(), NOW()),
       (2, 3, NOW(), NOW()),
       (3, 1, NOW(), NOW());

-- 7. 알림 (Notifications)

INSERT INTO notifications (user_id, message, notification_type, created_at, updated_at)
VALUES
-- 친구 신청 알림
(2, 'test님이 친구 신청을 하였습니다.', 'FRIEND_REQUEST', NOW(), NOW()),          -- 1 → 2
(1, '박민수님이 친구 신청을 하였습니다.', 'FRIEND_REQUEST', NOW(), NOW()),        -- 4 → 1

-- 친구 수락 알림
(1, '김철수님이 친구 신청을 수락하였습니다.', 'FRIEND_ACCEPT', NOW(), NOW()),     -- 2 → 1
(2, '이영희님이 친구 신청을 수락하였습니다.', 'FRIEND_ACCEPT', NOW(), NOW()),     -- 3 → 2

-- 파티 초대 알림
(2, 'test님이 김철수님을 \'흥미진진\' 파티에 초대했습니다.', 'PARTY_INVITE', NOW(), NOW()),   -- 방 1
(3, '김철수님이 이영희님을 \'재밌겠다\' 파티에 초대했습니다.', 'PARTY_INVITE', NOW(), NOW()), -- 방 2
(1, '정우성님이 test님을 \'같이 영화봐요\' 파티에 초대했습니다.', 'PARTY_INVITE', NOW(), NOW()); -- 방 3

-- 8. 리뷰 데이터 (Reviews)
-- 순서: user_id, movie_id, star_rating, content
INSERT INTO reviews (user_id, movie_id, star_rating, content)
VALUES (1, 1, 5, '마동석 액션은 역시 시원하네요!'),
       (1, 2, 5, '볼 때마다 새로운 명작입니다.'),
       (2, 1, 4, '아는 맛이지만 그래서 더 맛있네요.'),
       (2, 3, 5, '보는 내내 긴장감이 넘쳤습니다.'),
       (3, 4, 5, '아이들과 같이 봤는데 어른이 더 감동받았어요.'),
       (4, 5, 4, '오컬트 장르의 새로운 지평을 열었네요.'),
       (5, 2, 4, '상징성이 대단한 영화입니다.'),
       (6, 3, 5, '연기력이 압권입니다.'),
       (3, 1, 4, '전작보다 나은 것 같아요.'),
       (3, 2, 5, '한국 영화의 자부심!'),
       (4, 1, 3, '매번 비슷해서 조금 아쉽지만 볼만해요.'),
       (4, 3, 4, '몰입감이 엄청나네요.'),
       (5, 4, 3, '색감이 너무 예뻐요.'),
       (5, 5, 5, '무서운데 계속 보게 됩니다.'),
       (6, 1, 4, '가볍게 보기 딱 좋습니다.'),
       (6, 2, 5, '다시 봐도 소름 돋는 연출.'),
       (1, 3, 5, '실제 역사라 더 가슴 아프네요.'),
       (1, 4, 4, '기분 좋아지는 영화입니다.');