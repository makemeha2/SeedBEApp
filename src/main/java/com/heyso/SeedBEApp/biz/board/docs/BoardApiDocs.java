package com.heyso.SeedBEApp.biz.board.docs;

import com.heyso.SeedBEApp.biz.board.dto.*;
import com.heyso.SeedBEApp.biz.board.model.Board;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Tag(name = "Board", description = "게시판 API")
@RequestMapping("/api/boards")
public interface BoardApiDocs {

    @Operation(
            summary = "게시글 생성 (JSON)",
            description = "첨부파일 없이 JSON 본문으로 게시글을 생성합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BoardCreateReqDto.class),
                            examples = @ExampleObject(
                                    name = "createBoard",
                                    value = """
                            {
                              "category": "NOTICE",
                              "title": "첫 공지",
                              "contents": "마크다운 **지원**",
                              "rgstId": "admin"
                            }
                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "생성됨",
                            content = @Content(schema = @Schema(implementation = Board.class))),
                    @ApiResponse(responseCode = "400", description = "검증 오류")
            }
    )
    @PostMapping
    ResponseEntity<Board> createBoard(
            @Valid @RequestBody BoardCreateReqDto req,
            UriComponentsBuilder uriBuilder
    );

    @Operation(
            summary = "게시글 수정",
            description = "게시글을 부분/전체 업데이트합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = BoardUpdateReqDto.class),
                            examples = @ExampleObject(
                                    name = "updateBoard",
                                    value = """
                            {
                              "title": "제목 수정",
                              "contents": "본문 수정",
                              "mdfcId": "editor1"
                            }
                            """
                            )
                    )
            )
    )
    @PutMapping("/{id}")
    Board update(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id,
            @RequestBody @Valid BoardUpdateReqDto req
    );

    @Operation(
            summary = "게시글 삭제(소프트)",
            description = "`USE_YN` 등을 이용해 소프트 삭제합니다."
    )
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteSoft(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id,
            @Parameter(description = "수정자 ID") @RequestParam(value = "mdfcId", required = false) String mdfcId
    );

    @Operation(
            summary = "게시글 삭제(하드)",
            description = "데이터 및 관련 리소스를 실제로 삭제합니다. 주의해서 사용하세요."
    )
    @DeleteMapping("/{id}/hard")
    ResponseEntity<Void> deleteHard(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id
    );

    @Operation(
            summary = "게시글 목록 조회",
            description = "카테고리/검색어/페이지 정보로 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(schema = @Schema(implementation = BoardListDto.class)))
            }
    )
    @GetMapping
    BoardListDto list(
            @Parameter(description = "검색 조건 DTO", required = false)
            BoardSearchReqDto req
    );

    @Operation(
            summary = "게시글 생성 + 파일 업로드(Multipart)",
            description = "JSON(`board`)과 파일(`files`)을 함께 업로드합니다.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = {
                            @Content(
                                    mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                                    schema = @Schema(type = "object"),
                                    examples = @ExampleObject(
                                            name = "multipartExample",
                                            summary = "board(JSON) + files",
                                            value = """
                                ----WebKitFormBoundary
                                Content-Disposition: form-data; name="board"
                                Content-Type: application/json
                                
                                { "category":"NOTICE", "title":"파일 포함 글", "contents":"본문", "rgstId":"admin" }
                                ----WebKitFormBoundary
                                Content-Disposition: form-data; name="files"; filename="a.txt"
                                Content-Type: text/plain
                                
                                (file bytes)
                                ----WebKitFormBoundary--
                                """
                                    )
                            )
                    }
            )
    )
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<Long> createWithFiles(
            @RequestPart("board")  @Valid BoardCreateReqDto req,
            @RequestPart(name = "files", required = false) List<MultipartFile> files,
            UriComponentsBuilder uriBuilder
    ) throws Exception;

    @Operation(
            summary = "게시글 첨부파일 목록",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OK",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = BoardFileResDto.class))))
            }
    )
    @GetMapping("/{id}/files")
    ResponseEntity<List<BoardFileResDto>> listFiles(
            @Parameter(description = "게시글 ID") @PathVariable("id") Long id
    );

    @Operation(
            summary = "첨부파일 삭제"
    )
    @DeleteMapping("/files/{fileId}")
    ResponseEntity<Void> deleteFile(
            @Parameter(description = "파일 ID") @PathVariable Long fileId
    ) throws Exception;
}
