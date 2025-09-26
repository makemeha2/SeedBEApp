/* =============================================================================
 *  SeedBEApp - Flyway Initial Migration
 *  - DBMS     : MariaDB
 *  - Charset  : utf8mb4
 *  - Collation: utf8mb4_general_ci
 *  - Purpose  : Initialize core schema for Board & Error Log
 *  - Note     : This script creates tables only (no seed data).
 *               Consider adding V2__seed.sql for sample rows.
 * ============================================================================= */

-- seedbeappdb.tb_board definition

CREATE TABLE `tb_board` (
  `BOARD_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '게시글 ID',
  `CATEGORY` varchar(10) NOT NULL COMMENT '카테고리',
  `TITLE` varchar(300) NOT NULL COMMENT '제목',
  `CONTENTS` mediumtext NOT NULL COMMENT '본문 (Markdown 지원)',
  `USE_YN` varchar(1) NOT NULL DEFAULT 'Y' COMMENT '사용 여부 (Y: 공개, N: 비공개)',
  `RGST_ID` varchar(30) NOT NULL COMMENT '등록자',
  `RGST_DTM` datetime NOT NULL DEFAULT current_timestamp() COMMENT '등록일시',
  `MDFC_ID` varchar(30) DEFAULT NULL COMMENT '수정자',
  `MDFC_DTM` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp() COMMENT '수정일시',
  PRIMARY KEY (`BOARD_ID`),
  KEY `IX_TB_BOARD_RGST_DTM` (`RGST_DTM` DESC),
  CONSTRAINT `CK_TB_BOARD_USE_YN` CHECK (`USE_YN` in ('Y','N'))
) ENGINE=InnoDB AUTO_INCREMENT=138 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='게시판 테이블';


-- seedbeappdb.tb_com_error_log definition

CREATE TABLE `tb_com_error_log` (
  `ID` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'PK',
  `OCCURRED_AT` datetime(3) NOT NULL COMMENT '발생일시',
  `TRACE_ID` varchar(50) NOT NULL COMMENT '트레이스ID',
  `STATUS` int(11) NOT NULL COMMENT 'HTTP 상태코드',
  `ERROR_CODE` varchar(100) DEFAULT NULL COMMENT '애플리케이션 에러 코드',
  `MESSAGE` varchar(1000) DEFAULT NULL COMMENT '예외 메시지',
  `PATH` varchar(255) DEFAULT NULL COMMENT '요청 경로',
  `HTTP_METHOD` varchar(10) DEFAULT NULL COMMENT 'HTTP 메서드',
  `QUERY_STRING` varchar(1000) DEFAULT NULL COMMENT '쿼리스트링',
  `HEADERS_JSON` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL COMMENT '요청 헤더 (JSON)' CHECK (json_valid(`HEADERS_JSON`)),
  `BODY_TEXT` mediumtext DEFAULT NULL COMMENT '요청 본문',
  `STACKTRACE` mediumtext DEFAULT NULL COMMENT '스택트레이스',
  PRIMARY KEY (`ID`),
  KEY `IDX_ERROR_LOG_TRACE_TIME` (`TRACE_ID`,`OCCURRED_AT`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- seedbeappdb.tb_com_role definition

CREATE TABLE `tb_com_role` (
  `ROLE_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `ROLE_KEY` varchar(50) NOT NULL,
  PRIMARY KEY (`ROLE_ID`),
  UNIQUE KEY `ROLE_KEY` (`ROLE_KEY`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- seedbeappdb.tb_com_user definition

CREATE TABLE `tb_com_user` (
  `USER_ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `USERNAME` varchar(100) NOT NULL,
  `PASSWORD_HASH` varchar(200) NOT NULL,
  `NAME` varchar(100) NOT NULL,
  `EMAIL` varchar(200) DEFAULT NULL,
  `USE_YN` char(1) DEFAULT 'Y',
  `CREATED_AT` datetime DEFAULT current_timestamp(),
  `UPDATED_AT` datetime DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`USER_ID`),
  UNIQUE KEY `USERNAME` (`USERNAME`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- seedbeappdb.user_test definition

CREATE TABLE `user_test` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `created_at` timestamp NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=27 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


-- seedbeappdb.tb_board_file definition

CREATE TABLE `tb_board_file` (
  `FILE_ID` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '파일 PK',
  `BOARD_ID` bigint(20) NOT NULL COMMENT '게시물 PK',
  `ORG_FILE_NM` varchar(255) NOT NULL COMMENT '원본 파일명',
  `STORED_FILE_NM` varchar(255) NOT NULL COMMENT '서버 저장 파일명(UUID)',
  `FILE_EXT` varchar(50) DEFAULT NULL COMMENT '확장자',
  `MIME_TYPE` varchar(100) DEFAULT NULL COMMENT 'MIME 타입',
  `FILE_SIZE` bigint(20) NOT NULL COMMENT '파일 크기(Byte)',
  `FILE_PATH` varchar(500) NOT NULL COMMENT '저장 경로',
  `USE_YN` char(1) NOT NULL DEFAULT 'Y',
  `RGST_ID` varchar(50) DEFAULT NULL,
  `RGST_DTM` datetime NOT NULL DEFAULT current_timestamp(),
  `MDFC_ID` varchar(50) DEFAULT NULL,
  `MDFC_DTM` datetime DEFAULT NULL ON UPDATE current_timestamp(),
  PRIMARY KEY (`FILE_ID`),
  KEY `FK_TB_BOARD_FILE__BOARD` (`BOARD_ID`),
  CONSTRAINT `FK_TB_BOARD_FILE__BOARD` FOREIGN KEY (`BOARD_ID`) REFERENCES `tb_board` (`BOARD_ID`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='게시물 첨부파일';


-- seedbeappdb.tb_com_user_role definition

CREATE TABLE `tb_com_user_role` (
  `USER_ID` bigint(20) NOT NULL,
  `ROLE_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`USER_ID`,`ROLE_ID`),
  KEY `ROLE_ID` (`ROLE_ID`),
  CONSTRAINT `tb_com_user_role_ibfk_1` FOREIGN KEY (`USER_ID`) REFERENCES `tb_com_user` (`USER_ID`),
  CONSTRAINT `tb_com_user_role_ibfk_2` FOREIGN KEY (`ROLE_ID`) REFERENCES `tb_com_role` (`ROLE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;