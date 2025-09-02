package com.heyso.SeedBEApp.biz.board.web;

import com.heyso.SeedBEApp.biz.board.dto.BoardListDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardSearchReqDto;
import com.heyso.SeedBEApp.biz.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
