/* =============================================================================
 *  SeedBEApp - Flyway Initial Migration
 *  - DBMS     : MariaDB
 *  - Charset  : utf8mb4
 *  - Collation: utf8mb4_general_ci
 *  - Purpose  : Initialize core schema for Board & Error Log
 *  - Note     : This script creates tables only (no seed data).
 *               Consider adding V2__seed.sql for sample rows.
 * ============================================================================= */

/* -- (선택) 테스트 DB를 아직 만들지 않았다면 주석 해제
CREATE DATABASE IF NOT EXISTS seedbeapp_test_db
  DEFAULT CHARACTER SET utf8mb4
  COLLATE utf8mb4_general_ci;
USE seedbeapp_test_db;
*/

/* ============================================
 * TB_BOARD : 게시글 본문
 * --------------------------------------------
 * - 소프트삭제: USE_YN ('Y'/'N')
 * - 기본 정렬: RGST_DTM desc
 * - 카테고리/사용여부/등록일 인덱스
 * ============================================ */
CREATE TABLE IF NOT EXISTS TB_BOARD (
  BOARD_ID       BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'PK',
  CATEGORY       VARCHAR(20)  NOT NULL                COMMENT '분류(NOTICE 등)',
  TITLE          VARCHAR(300) NOT NULL                COMMENT '제목',
  CONTENTS       MEDIUMTEXT   NOT NULL                COMMENT '본문',
  USE_YN         CHAR(1)      NOT NULL DEFAULT 'Y'    COMMENT '사용(Y)/삭제(N)',
  VIEW_CNT       BIGINT       NOT NULL DEFAULT 0      COMMENT '조회수',
  RGST_ID        VARCHAR(30)  NOT NULL                COMMENT '등록자',
  RGST_DTM       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  MDFC_ID        VARCHAR(30)           DEFAULT NULL   COMMENT '수정자',
  MDFC_DTM       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP
                                           ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  CONSTRAINT PK_TB_BOARD PRIMARY KEY (BOARD_ID),
  CONSTRAINT CK_TB_BOARD_USE_YN CHECK (USE_YN IN ('Y','N'))
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '게시판 본문';

CREATE INDEX IX_TB_BOARD_CATEGORY  ON TB_BOARD (CATEGORY);
CREATE INDEX IX_TB_BOARD_USE_YN    ON TB_BOARD (USE_YN);
CREATE INDEX IX_TB_BOARD_RGST_DTM  ON TB_BOARD (RGST_DTM);

/* ============================================
 * TB_BOARD_FILE : 게시글 첨부파일 메타
 * --------------------------------------------
 * - FK: BOARD_ID → TB_BOARD.BOARD_ID
 * - 소프트삭제: USE_YN ('Y'/'N')
 * - 파일삭제 정책은 애플리케이션 로직에서 제어(ON DELETE RESTRICT)
 * ============================================ */
CREATE TABLE IF NOT EXISTS TB_BOARD_FILE (
  FILE_ID        BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  BOARD_ID       BIGINT        NOT NULL                COMMENT 'TB_BOARD.BOARD_ID',
  ORG_FILE_NM    VARCHAR(255)  NOT NULL                COMMENT '원본 파일명',
  STORED_FILE_NM VARCHAR(255)  NOT NULL                COMMENT '저장 파일명(UUID 등)',
  FILE_SIZE      BIGINT        NOT NULL                COMMENT '파일 크기(Byte)',
  MIME_TYPE      VARCHAR(100)           DEFAULT NULL   COMMENT 'MIME',
  USE_YN         CHAR(1)       NOT NULL DEFAULT 'Y'    COMMENT '사용(Y)/삭제(N)',
  RGST_ID        VARCHAR(30)   NOT NULL                COMMENT '등록자',
  RGST_DTM       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '등록일시',
  MDFC_ID        VARCHAR(30)            DEFAULT NULL   COMMENT '수정자',
  MDFC_DTM       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP
                                            ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
  CONSTRAINT PK_TB_BOARD_FILE PRIMARY KEY (FILE_ID),
  CONSTRAINT FK_BOARD_FILE_BOARD FOREIGN KEY (BOARD_ID)
      REFERENCES TB_BOARD (BOARD_ID)
      ON UPDATE RESTRICT
      ON DELETE RESTRICT,
  CONSTRAINT CK_TB_BOARD_FILE_USE_YN CHECK (USE_YN IN ('Y','N'))
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '게시판 첨부파일';

CREATE INDEX IX_TB_BOARD_FILE_BOARD_ID ON TB_BOARD_FILE (BOARD_ID);
CREATE INDEX IX_TB_BOARD_FILE_USE_YN   ON TB_BOARD_FILE (USE_YN);

/* ============================================
 * TB_COM_ERROR_LOG : 애플리케이션 예외/오류 로그
 * --------------------------------------------
 * - API 예외 핸들러에서 INSERT
 * - 민감정보(토큰/비번/주민번호 등)는 애플리케이션에서 마스킹 후 저장 권장
 * ============================================ */
CREATE TABLE IF NOT EXISTS TB_COM_ERROR_LOG (
  ERROR_ID       BIGINT        NOT NULL AUTO_INCREMENT COMMENT 'PK',
  OCCURRED_AT    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '발생시각',
  LEVEL          VARCHAR(10)   NOT NULL DEFAULT 'ERROR'          COMMENT '레벨(ERROR/WARN/INFO)',
  TRACE_ID       VARCHAR(64)            DEFAULT NULL             COMMENT '트레이스ID(분산추적)',
  REQUEST_ID     VARCHAR(64)            DEFAULT NULL             COMMENT '요청ID(코릴레이션)',
  METHOD         VARCHAR(10)            DEFAULT NULL             COMMENT 'HTTP 메서드',
  PATH           VARCHAR(500)           DEFAULT NULL             COMMENT '요청 경로',
  QUERY_STRING   VARCHAR(1000)          DEFAULT NULL             COMMENT '쿼리스트링',
  STATUS_CODE    INT                     DEFAULT NULL            COMMENT 'HTTP 상태코드',
  CLIENT_IP      VARCHAR(64)            DEFAULT NULL             COMMENT '클라이언트IP',
  USER_AGENT     VARCHAR(500)           DEFAULT NULL             COMMENT 'User-Agent',
  PRINCIPAL      VARCHAR(100)           DEFAULT NULL             COMMENT '사용자 식별자',
  EX_CLASS       VARCHAR(300)           DEFAULT NULL             COMMENT '예외 클래스',
  MESSAGE        TEXT                    DEFAULT NULL            COMMENT '예외 메시지(마스킹)',
  STACKTRACE     LONGTEXT                DEFAULT NULL            COMMENT '스택트레이스',
  HEADERS        MEDIUMTEXT              DEFAULT NULL            COMMENT '요청 헤더(마스킹)',
  REQUEST_BODY   MEDIUMTEXT              DEFAULT NULL            COMMENT '요청 바디(마스킹)',
  ETC_JSON       MEDIUMTEXT              DEFAULT NULL            COMMENT '기타 정보(JSON)',
  CONSTRAINT PK_TB_COM_ERROR_LOG PRIMARY KEY (ERROR_ID)
) ENGINE=InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci
  COMMENT = '공통 오류 로그(민감정보는 마스킹 후 저장)';

CREATE INDEX IX_TB_COM_ERROR_LOG_TIME   ON TB_COM_ERROR_LOG (OCCURRED_AT);
CREATE INDEX IX_TB_COM_ERROR_LOG_PATH   ON TB_COM_ERROR_LOG (PATH);
CREATE INDEX IX_TB_COM_ERROR_LOG_LEVEL  ON TB_COM_ERROR_LOG (LEVEL);
CREATE INDEX IX_TB_COM_ERROR_LOG_STATUS ON TB_COM_ERROR_LOG (STATUS_CODE);

/* ============================================
 * 옵션 인덱스/추후 확장 포인트
 * --------------------------------------------
 * - 게시판 검색 최적화: TITLE/CONTENTS 전문검색(Fulltext) 필요 시
 *   -> InnoDB Fulltext (MariaDB 10.0+): 아래 주석 해제 고려
 * CREATE FULLTEXT INDEX FT_TB_BOARD_TITLE_CONTENTS ON TB_BOARD (TITLE, CONTENTS);
 * ============================================ */


