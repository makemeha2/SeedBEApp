package com.heyso.SeedBEApp.biz.board.web;

import com.heyso.SeedBEApp.biz.board.dto.*;
import com.heyso.SeedBEApp.biz.board.model.Board;
import com.heyso.SeedBEApp.biz.board.model.BoardCategory;
import com.heyso.SeedBEApp.biz.board.service.BoardFileService;
import com.heyso.SeedBEApp.biz.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@Tag(name = "Notice Board")
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeBoardController {
    private static final BoardCategory CATEGORY = BoardCategory.NOTICE;
    private final BoardService boardService;
    private final BoardFileService boardFileService;

    @Operation(summary = "공지 목록 조회")
    @GetMapping
    public BoardListDto list(BoardSearchReqDto req) {
        req.setCategory(CATEGORY);
        return boardService.getBoardList(req);
    }

    @Operation(summary = "공지 상세 조회")
    @GetMapping("/{id}")
    public BoardDetailDto detail(@PathVariable Long id) {
        return boardService.getBoardDetail(id);
    }

    @Operation(summary = "공지 등록 (JSON)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> create(@RequestBody BoardCreateReqDto req, UriComponentsBuilder uriBuilder) {
        req.setCategory(CATEGORY);
        Board board = boardService.createBoard(req); // 반환 Long 기준

        URI location = uriBuilder.path("/api/notice/{id}").buildAndExpand(board.getBoardId()).toUri();
        return ResponseEntity.created(location).body(board.getBoardId()); // 혹은 body(dto)
    }

    @Operation(summary = "공지 등록 (파일 포함)")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    @PostMapping(path = "/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createWithFiles(
            @RequestPart("board") BoardCreateReqDto req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files,
            UriComponentsBuilder uriBuilder
    ) throws IOException {
        req.setCategory(CATEGORY);
        Board board = boardService.createBoard(req);

        Long boardId = Optional.ofNullable(board)
                .map(Board::getBoardId)
                .orElse(0L);

        if(boardId > 0)
            boardFileService.saveFiles(boardId, files, board.getRgstId());

        URI location = uriBuilder.path("/api/notice/{id}").buildAndExpand(boardId).toUri();

        return ResponseEntity.created(location).body(boardId);
    }

    @Operation(summary = "공지 수정 (JSON)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody BoardUpdateReqDto req) {
        req.setCategory(CATEGORY);
        boardService.updateBoard(id, req);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공지 수정 (파일 포함)")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping(path = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateWithFiles(
            @PathVariable Long id,
            @RequestPart("board") BoardUpdateReqDto req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        req.setCategory(CATEGORY);
        boardService.updateBoardWithFiles(id, req, files);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공지 삭제")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.deleteSoft(id);
        return ResponseEntity.noContent().build();
    }
}
