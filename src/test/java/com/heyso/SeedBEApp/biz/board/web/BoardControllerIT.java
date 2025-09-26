package com.heyso.SeedBEApp.biz.board.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyso.SeedBEApp.biz.board.dto.BoardCreateReqDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardUpdateReqDto;
import com.heyso.SeedBEApp.biz.board.model.BoardCategory;
import com.heyso.SeedBEApp.support.BaseDbIT;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoardControllerIT extends BaseDbIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    private String accessToken;

    @BeforeEach
    void setup() throws Exception {
        // 1) 로그인 요청 JSON (AuthController에서 요구하는 필드명 확인 필요)
        String loginReq = """
            { "username": "admin", "password": "password" }
        """;

        var loginRes = mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginReq))
                .andExpect(status().isOk())
                .andReturn();

        // 2) 응답에서 accessToken 추출 (응답 JSON 구조 확인 필요!)
        String body = loginRes.getResponse().getContentAsString();
        this.accessToken = om.readTree(body).get("accessToken").asText();
    }

    private String bearer() {
        return "Bearer " + accessToken;
    }

    @Test
    @DisplayName("게시물: 생성(JSON) → 목록확인 → 상세조회")
    void create_list_detail_json() throws Exception {

        // 1) JSON 본문으로 생성 (첨부 없음)
        var req = new BoardCreateReqDto();
        req.setCategory(BoardCategory.NOTICE);
        req.setTitle("JSON 생성 제목");
        req.setContents("JSON 생성 본문");

        var createRes = mvc.perform(post("/api/boards")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.boardId").exists())
                .andExpect(jsonPath("$.title").value("JSON 생성 제목"))
                .andReturn();

        var createdId = om.readTree(createRes.getResponse().getContentAsString())
                .get("boardId").asLong();
        assertThat(createdId).isPositive();

        // 2) 목록 확인
        mvc.perform(get("/api/boards")
                        .header("Authorization", bearer())
                        .param("page", "1")
                        .param("pageSize", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.totalCount").isNumber());


        // 3) 상세 조회
        mvc.perform(get("/api/boards/{id}", createdId)
                        .header("Authorization", bearer())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.board.boardId").value(createdId))
                .andExpect(jsonPath("$.files").isArray());

    }

    @Test
    @DisplayName("게시물: 생성(파일첨부) → 상세조회(파일확인) → 파일개별삭제 → 업데이트(파일교체/삭제지정)")
    void create_with_files_then_update_and_delete_file() throws Exception {
        // 1) 멀티파트로 생성 (board JSON part + files)
        var createDto = new BoardCreateReqDto();
        createDto.setCategory(BoardCategory.NOTICE);
        createDto.setTitle("파일 생성 제목");
        createDto.setContents("파일 생성 본문");

        var boardPart = new MockMultipartFile(
                "board", "board.json", "application/json",
                om.writeValueAsBytes(createDto)
        );
        var file1 = new MockMultipartFile(
                "files", "hello.txt", "text/plain",
                "hello".getBytes(StandardCharsets.UTF_8)
        );
        var file2 = new MockMultipartFile(
                "files", "data.bin", "application/octet-stream",
                new byte[]{1, 2, 3, 4}
        );

        var createRes = mvc.perform(multipart("/api/boards")
                        .file(boardPart).file(file1).file(file2)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer()))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        // body를 문자열로 추출
        String body = createRes.getResponse().getContentAsString();

        // 숫자인지 검증
        assertThat(body).isNotBlank();
        assertThat(body).matches("\\d+"); // 정수 문자열인지 확인

        // 필요하다면 Long으로 변환
        long createdId = Long.parseLong(body);
        assertThat(createdId).isPositive();

        // 2) 상세에서 파일 2건 확인
        var detailRes = mvc.perform(get("/api/boards/{id}", createdId)
                        .header("Authorization", bearer())
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files").isArray())
                .andReturn();

        var filesNode = om.readTree(detailRes.getResponse().getContentAsString()).get("files");
        assertThat(filesNode.size()).isEqualTo(2);
        var firstFileId = filesNode.get(0).get("fileId").asLong();

        // 3) 파일 개별 삭제 API
        mvc.perform(delete("/api/boards/files/{fileId}", firstFileId)
                        .header("Authorization", bearer()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("ok"));

        // 4) 업데이트(제목 변경 + 파일 1건 추가 + deleteFileIds로 남은 기존 파일 1건 삭제)
        var update = new BoardUpdateReqDto();
        update.setCategory(BoardCategory.NOTICE);
        update.setTitle("수정된 제목");
        update.setContents("수정된 본문");
        update.setUseYn("Y");
        // 상세에서 남은 파일 id 수집 (방금 1건 삭제했으므로 1건 남아있음)
        var remainedFileId = filesNode.get(1).get("fileId").asLong();
        update.setDeleteFileIds(List.of(remainedFileId));

        var updatePart = new MockMultipartFile(
                "board", "update.json", "application/json",
                om.writeValueAsBytes(update)
        );
        var newFile = new MockMultipartFile(
                "files", "new.txt", "text/plain",
                "new".getBytes(StandardCharsets.UTF_8)
        );

        mvc.perform(multipart("/api/boards/{id}/with-files", createdId)
                        .file(updatePart).file(newFile)
                        .with(req -> { req.setMethod("PUT"); return req; })
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"));

        // 5) 다시 상세 확인: 기존 파일은 모두 정리되고 새 파일만 남았는지 확인 (파일 1건)
        mvc.perform(get("/api/boards/{id}", createdId)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", bearer()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files.length()").value(1))
                .andExpect(jsonPath("$.files[0].orgFileNm").value("new.txt"));
    }

    @Test
    @DisplayName("게시물: 소프트 삭제")
    void soft_delete_board() throws Exception {
        // 먼저 하나 생성
        var req = new BoardCreateReqDto();
        req.setCategory(BoardCategory.FREE);
        req.setTitle("삭제 대상");
        req.setContents("삭제 대상 본문");

        var createRes = mvc.perform(post("/api/boards")
                        .header("Authorization", bearer())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req))
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        var id = om.readTree(createRes.getResponse().getContentAsString()).get("boardId").asLong();

        // 존재 확인
        mvc.perform(get("/api/boards/{id}", id).header("Authorization", bearer())).andExpect(status().isOk());

        // 소프트 삭제 (컨트롤러의 매핑이 /api/boards/{id} 라고 가정)
        mvc.perform(delete("/api/boards/{id}", id).header("Authorization", bearer()))
                .andDo(print())
                .andExpect(status().isOk());

        // 삭제 후 상세는 4xx 또는 예외 메시지 응답일 수 있음(서비스에서 IllegalArgumentException 던짐)
        mvc.perform(get("/api/boards/{id}", id).header("Authorization", bearer()))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }
}