package com.heyso.SeedBEApp.biz.board.service;

import com.heyso.SeedBEApp.biz.board.dao.BoardMapper;
import com.heyso.SeedBEApp.biz.board.dto.BoardListDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardSearchReqDto;
import com.heyso.SeedBEApp.biz.board.model.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    public BoardListDto getBoardList(BoardSearchReqDto req) {
        // 페이징 보정
        final int DEFAULT_SIZE = 10, MAX_SIZE = 100;
        Integer offset = null, limit = null;
        Integer page = null, pageSize = null;

        if (req.isPagingEnabled()) {
            page = Math.max(req.getPage(), 1);
            pageSize = Math.min(Math.max(req.getPageSize(), 1), MAX_SIZE);
            offset = (page - 1) * pageSize;
            limit = pageSize;
        }

        List<Board> items = boardMapper.selectBoardList(req, offset, limit);

        if (offset == null) { // 페이징 미사용
            return BoardListDto.ofNoPaging(items);
        }

        long total = boardMapper.selectBoardCount(req);
        int totalPages = (int) Math.ceil((double) total / pageSize);

        return BoardListDto.builder()
                .items(items)
                .totalCount(total)
                .page(page)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .build();
    }
}
