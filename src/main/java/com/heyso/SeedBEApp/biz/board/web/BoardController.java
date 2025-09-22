package com.heyso.SeedBEApp.biz.board.web;

import com.heyso.SeedBEApp.biz.board.docs.BoardApiDocs;
import com.heyso.SeedBEApp.biz.board.dto.*;
import com.heyso.SeedBEApp.biz.board.model.Board;
import com.heyso.SeedBEApp.biz.board.model.BoardFile;
import com.heyso.SeedBEApp.biz.board.service.BoardFileService;
import com.heyso.SeedBEApp.biz.board.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Validated
// public class BoardController implements BoardApiDocs {
public class BoardController {
    private final BoardService boardService;
    private final BoardFileService boardFileService;

    @Value("${app.upload.uriPrefix:/files}")
    private String uriPrefix;

    @PostMapping
    public ResponseEntity<Board> createBoard(@Valid @RequestBody BoardCreateReqDto req,
                                               UriComponentsBuilder uriBuilder) {
        Board created = boardService.createBoard(req);

        return ResponseEntity.created(
                uriBuilder.path("/api/boards/{id}")
                        .buildAndExpand(created.getBoardId())
                        .toUri()
        ).body(created);
    }

    @PutMapping("/{id}")
    public Board update(@PathVariable("id") Long id,
                        @RequestBody @Valid BoardUpdateReqDto req) {
        return boardService.updateBoard(id, req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSoft(@PathVariable("id") Long id,
                                           @RequestParam(value="mdfcId", required=false) String mdfcId) {
        boardService.deleteSoft(id, mdfcId);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<String> deleteHard(@PathVariable("id") Long id) {
        boardService.deleteHard(id);
        return ResponseEntity.ok("ok");
    }

    @GetMapping
    public BoardListDto list(BoardSearchReqDto req) {
        // 쿼리스트링 예: /api/boards?category=NOTICE&searchText=hello&page=1&pageSize=20
        return boardService.getBoardList(req);
    }

    /* ---------------------------------------------------
        첨부파일 관련
    -----------------------------------------------------*/


    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Long> createWithFiles(
            @RequestPart("board") @Valid BoardCreateReqDto req,
            @RequestPart(name = "files", required = false) List<MultipartFile> files,
            HttpServletRequest request,
            UriComponentsBuilder uriBuilder
    ) throws Exception {
        Board created = boardService.createBoard(req);
        Long id = created.getBoardId();

        if(id > 0)
            boardFileService.saveFiles(id, files, created.getRgstId());

        return ResponseEntity.created(
                        uriBuilder.path("/api/boards/{id}")
                                .buildAndExpand(id)
                                .toUri())
                .body(id);
    }

    @PutMapping(value = "/{id}/with-files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Board updateWithFiles(@PathVariable("id") Long id,
                                 @RequestPart("board") BoardUpdateReqDto board,
                                 @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws Exception {
        return boardService.updateBoardWithFiles(id, board, files);
    }

    @GetMapping("/{id}/files")
    public ResponseEntity<List<BoardFileResDto>> listFiles(@PathVariable("id") Long id) {
        List<BoardFile> files = boardFileService.getFiles(id);
        List<BoardFileResDto> res = files.stream()
                .map(f -> BoardFileResDto.builder()
                        .fileId(f.getFileId())
                        .orgFileNm(f.getOrgFileNm())
                        .fileSize(f.getFileSize())
                        .downloadUrl(uriPrefix + "/" + f.getStoredFileNm())
                        .build())
                .toList();

        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) throws Exception {
        boardFileService.deleteFile(fileId);
        return ResponseEntity.ok("ok");
    }
}
