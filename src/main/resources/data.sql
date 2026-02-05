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
VALUES ('test@test.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', 'test', '127.0.0.1', 'USER', false, false, false, 1,  NOW(), NOW(), NOW()),
       ('kim@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '김철수', '127.0.0.2', 'USER', false, false, false, 1,  NOW(), NOW(), NOW()),
       ('lee@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '이영희', '127.0.0.3', 'USER', false, false, false, 1,  NOW(), NOW(), NOW()),
       ('park@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '박민수', '127.0.0.4', 'USER', false, false, false, 1,  NOW(), NOW(), NOW()),
       ('choi@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '최지은', '127.0.0.5', 'USER', false, false, false, 1,  NOW(), NOW(), NOW()),
       ('jung@joinflex.com', '$2a$10$hpAUduYxqdeK8L9Ft0Ud7.6Mf89EhSSiyrnuNeBjynJrq64sNZ5Hq', '정우성', '127.0.0.6', 'USER', false, false, false, 1,  NOW(), NOW(), NOW());

-- 3. 친구 요청 (Friend Requests)
INSERT INTO friend_requests (sender_id, receiver_id, status, created_at, updated_at)
VALUES (1, 2, 'ACCEPTED', NOW(), NOW()),
       (2, 3, 'ACCEPTED', NOW(), NOW()),
       (1, 5, 'PENDING', NOW(), NOW()),
       (4, 1, 'PENDING', NOW(), NOW());

-- 4. 영화 (Movies)
INSERT INTO movies (title, created_at, updated_at)
VALUES ('범죄도시4', NOW(), NOW()),
       ('기생충', NOW(), NOW()),
       ('서울의 봄', NOW(), NOW()),
       ('인사이드 아웃 2', NOW(), NOW()),
       ('파묘', NOW(), NOW());

-- 5. 파티방 (Party Rooms)
INSERT INTO party_rooms (host_id, movie_id, room_name, is_public, host_control, max_count, current_member_count, created_at, updated_at)
VALUES (1, 1, '흥미진진', false, true, 4, 2, NOW(), NOW()), -- ID 1: 김철수 방
       (2, 3, '재밌겠다', false, true, 4, 2, NOW(), NOW()), -- ID 2: 이영희 방
       (5, 5, '같이 영화봐요', false, true, 4, 2,NOW(), NOW()); -- ID 3: 정우성 방

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