package com.heyso.SeedBEApp.biz.board.web;

import com.heyso.SeedBEApp.biz.board.dto.BoardCreateReqDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardListDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardSearchReqDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardUpdateReqDto;
import com.heyso.SeedBEApp.biz.board.model.Board;
import com.heyso.SeedBEApp.biz.board.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
@Validated
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public BoardListDto list(BoardSearchReqDto req) {
        // 쿼리스트링 예: /api/boards?category=NOTICE&searchText=hello&page=1&pageSize=20
        return boardService.getBoardList(req);
    }

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
    public ResponseEntity<Void> deleteSoft(@PathVariable("id") Long id,
                                           @RequestParam(value="mdfcId", required=false) String mdfcId) {
        boardService.deleteSoft(id, mdfcId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/hard")
    public ResponseEntity<Void> deleteHard(@PathVariable("id") Long id) {
        boardService.deleteHard(id);
        return ResponseEntity.noContent().build();
    }
}
