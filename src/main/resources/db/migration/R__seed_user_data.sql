-- =======================
-- SEED DATA FOR AUTH TEST
-- =======================

-- 1. ROLE 데이터 삽입
INSERT IGNORE INTO tb_com_role (ROLE_KEY) VALUES
('ROLE_ADMIN'),
('ROLE_USER'),
('ROLE_MANAGER');

-- 2. USER 데이터 삽입
-- 비밀번호 해시는 예시: "password123" → bcrypt (Spring Security에서 사용 가능)
-- 예: $2a$10$Dow1V3j6UwDJN4o7OphhQuxO4POhx7rs6C5WTO3Wf4BBsJPqlU1nq
INSERT IGNORE INTO tb_com_user (USERNAME, PASSWORD_HASH, NAME, EMAIL, USE_YN)
VALUES
('admin',  '$2a$10$iCjIjvCrq9G2V2Gk5u3QO.IfsLbq6KWNX.EdgxP93AQz/6XnQGwOO', '관리자', 'admin@seedbeapp.com', 'Y'),
('user1',  '$2a$10$iCjIjvCrq9G2V2Gk5u3QO.IfsLbq6KWNX.EdgxP93AQz/6XnQGwOO', '사용자1', 'user1@seedbeapp.com', 'Y'),
('user2',  '$2a$10$iCjIjvCrq9G2V2Gk5u3QO.IfsLbq6KWNX.EdgxP93AQz/6XnQGwOO', '사용자2', 'user2@seedbeapp.com', 'Y'),
('manager','$2a$10$iCjIjvCrq9G2V2Gk5u3QO.IfsLbq6KWNX.EdgxP93AQz/6XnQGwOO', '매니저', 'manager@seedbeapp.com', 'Y');



-- 3. USER_ROLE 매핑 데이터 삽입
-- ROLE_ID와 USER_ID는 AUTO_INCREMENT라, 실제 DB에 들어간 값 기준으로 맞춰야 합니다.
-- 보통 ROLE_ID는 1=ADMIN, 2=USER, 3=MANAGER 순서로 들어갑니다.
-- USER_ID는 삽입 순서에 따라 1=admin, 2=user1, 3=user2, 4=manager 예상됩니다.

INSERT IGNORE INTO tb_com_user_role (USER_ID, ROLE_ID) VALUES
(1, 1), -- admin → ROLE_ADMIN
(1, 2), -- admin → ROLE_USER (관리자도 일반 사용자 권한 포함)
(2, 2), -- user1 → ROLE_USER
(3, 2), -- user2 → ROLE_USER
(4, 3), -- manager → ROLE_MANAGER
(4, 2); -- manager → ROLE_USER (매니저도 일반 사용자 권한 포함)
