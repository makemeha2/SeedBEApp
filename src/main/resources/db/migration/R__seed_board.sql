INSERT INTO TB_BOARD (CATEGORY, TITLE, CONTENTS, USE_YN, RGST_ID)
SELECT
  CASE FLOOR(RAND()*3)
    WHEN 0 THEN 'NOTICE'
    WHEN 1 THEN 'FREE'
    ELSE 'QNA'
  END AS CATEGORY,
  CONCAT('샘플 제목 ', seq.seq) AS TITLE,
  CONCAT('샘플 본문 내용입니다. 번호: ', seq.seq, '\n\n**마크다운** 지원됨') AS CONTENTS,
  'Y' AS USE_YN,
  CONCAT('user', LPAD(FLOOR(1 + RAND()*10), 2, '0')) AS RGST_ID
FROM (
  SELECT @rownum := @rownum + 1 AS seq
  FROM information_schema.tables t1,
       information_schema.tables t2,
       (SELECT @rownum := 0) r
  LIMIT 100
) seq;