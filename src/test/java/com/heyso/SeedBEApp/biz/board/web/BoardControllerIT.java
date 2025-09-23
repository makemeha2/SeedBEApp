package com.heyso.SeedBEApp.biz.board.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.heyso.SeedBEApp.support.BaseDbIT;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false) // ← 보안 비활성화(초기 고정용)
class BoardControllerIT extends BaseDbIT {
    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @Test
    @DisplayName("게시판 E2E: 생성(첨부) → 단건조회 → 파일목록 → 수정(파일추가/삭제) → 소프트삭제")
    void boardE2E() throws Exception {
        // 1) 생성 (multipart: board JSON + files[])
        var boardJson = """
          {"category":"NOTICE","title":"파일 테스트","contents":"본문","useYn":"Y","rgstId":"tester"}
        """;
        var boardPart = new MockMultipartFile("board", "board.json", "application/json", boardJson.getBytes());
        var file1 = new MockMultipartFile("files", "C:\\99_TEMP\\Temp\\분기보고서_2025_05.pdf", "application/pdf", "hi".getBytes());
        var file2 = new MockMultipartFile("files", "readme.md", "text/markdown", "# readme".getBytes());

        var createRes = mvc.perform(
                        multipart("/api/boards")
                                .file(boardPart)
                                .file(file1)
                                .file(file2)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn();

        var location = createRes.getResponse().getHeader("Location");
        assertThat(location).isNotBlank();
        var idStr = location.substring(location.lastIndexOf('/') + 1);
        long boardId = Long.parseLong(idStr);

        // 2) 단건 조회
        mvc.perform(get("/api/boards/{id}", boardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boardId").value(boardId))
                .andExpect(jsonPath("$.title").value("파일 테스트"))
                .andExpect(jsonPath("$.category").value("NOTICE"));

        // 3) 파일 목록 조회
        var filesRes = mvc.perform(get("/api/boards/{id}/files", boardId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andReturn();

        // 파일 목록 파싱(예: [{fileId:1,...},{fileId:2,...}])
        record FileDto(Long fileId, String orgFileNm, String storedFileNm, Long fileSize, String mimeType, String useYn){}
        List<FileDto> list = om.readValue(
                filesRes.getResponse().getContentAsByteArray(),
                om.getTypeFactory().constructCollectionType(List.class, FileDto.class)
        );
        assertThat(list).hasSizeGreaterThanOrEqualTo(1);
        // assertThat(list.size()).isGreaterThanOrEqualTo(1);
        Long fileIdToDelete = list.get(0).fileId();

        // 4) 수정(파일 추가 + 기존 파일 1개 삭제) — multipart PUT
        var updateJson = """
           {"title":"수정된 제목","contents":"수정된 본문","useYn":"Y","mdfcId":"tester2","deleteFileIds": [%d]}
        """.formatted(fileIdToDelete);
        var updatePart = new MockMultipartFile("board", "board.json", "application/json", updateJson.getBytes());
        var newFile = new MockMultipartFile("files", "new.txt", "text/plain", "new".getBytes());

        mvc.perform(
                        multipart("/api/boards/{id}/with-files", boardId)
                                .file(updatePart)
                                .file(newFile)
                                .with(req -> { req.setMethod("PUT"); return req; }) // multipart PUT
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"));

        // 5) (선택) 남은 파일 중 하나 직접 삭제 API 검증
        //    실제 구현에 맞게 엔드포인트/응답 형태 조정
        // mvc.perform(delete("/api/boards/files/{fileId}", anotherFileId))
        //    .andExpect(status().isOk())
        //    .andExpect(content().string("ok"));

        // 6) 소프트 삭제
        mvc.perform(delete("/api/boards/{id}", boardId))
                .andExpect(status().isNoContent());
    }
}