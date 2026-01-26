-- 1. 사용자 (Users)
-- 비밀번호 '1234'
INSERT INTO users (email, nickname, password, created_at, updated_at)
VALUES ('kim@joinflex.com', '김철수', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7qa8qX.2O',
        NOW(), NOW()), -- ID: 1
       ('lee@joinflex.com', '이영희', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7qa8qX.2O',
        NOW(), NOW()), -- ID: 2
       ('park@joinflex.com', '박민수', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7qa8qX.2O',
        NOW(), NOW()), -- ID: 3
       ('choi@joinflex.com', '최지은', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7qa8qX.2O',
        NOW(), NOW()), -- ID: 4
       ('jung@joinflex.com', '정우성', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOcd7qa8qX.2O',
        NOW(), NOW()); -- ID: 5

-- 2. 친구 요청 (Friend Requests)
INSERT INTO friend_requests (sender_id, receiver_id, is_accepted, created_at, updated_at)
VALUES (1, 2, true, NOW(), NOW()),
       (2, 3, true, NOW(), NOW()),
       (1, 5, false, NOW(), NOW()),
       (4, 1, false, NOW(), NOW());

-- 3. 영화 (Movies)
INSERT INTO movies (title, created_at, updated_at)
VALUES ('범죄도시4', NOW(), NOW()),
       ('기생충', NOW(), NOW()),
       ('서울의 봄', NOW(), NOW()),
       ('인사이드 아웃 2', NOW(), NOW()),
       ('파묘', NOW(), NOW());

-- 4. 파티방 (Party Rooms)
INSERT INTO party_rooms (host_id, movie_id, created_at, updated_at)
VALUES (1, 1, NOW(), NOW()), -- ID 1: 김철수 방
       (2, 3, NOW(), NOW()), -- ID 2: 이영희 방
       (5, 5, NOW(), NOW()); -- ID 3: 정우성 방

-- 5. 파티 초대 (Party Invites)
INSERT INTO party_invites (party_room_id, guest_id, created_at, updated_at)
VALUES (1, 2, NOW(), NOW()),
       (2, 3, NOW(), NOW()),
       (3, 1, NOW(), NOW());