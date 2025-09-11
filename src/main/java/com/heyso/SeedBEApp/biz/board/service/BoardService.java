package com.heyso.SeedBEApp.biz.board.service;

import com.heyso.SeedBEApp.biz.board.dao.BoardMapper;
import com.heyso.SeedBEApp.biz.board.dto.BoardCreateReqDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardListDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardSearchReqDto;
import com.heyso.SeedBEApp.biz.board.dto.BoardUpdateReqDto;
import com.heyso.SeedBEApp.biz.board.model.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;

    @Transactional(readOnly = true)
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

    @Transactional
    public Board createBoard(BoardCreateReqDto req) {
        Board vo = Board.builder()
                .category(req.getCategory())
                .title(req.getTitle())
                .contents(req.getContents())
                .rgstId("testnam01")
                .build();

        int inserted = boardMapper.insertBoard(vo);
        if (inserted != 1 || vo.getBoardId() == null) {
            throw new IllegalStateException("게시글 등록 실패(키 반환 실패)");
        }

        Board created = boardMapper.selectById(vo.getBoardId());
        if (created == null) {
            throw new IllegalStateException("등록 후 재조회 실패"); // → 여기서 예외면 롤백 O
        }
        return created;
    }

    @Transactional
    public Board updateBoard(Long boardId, BoardUpdateReqDto req) {
        Board exists = boardMapper.selectById(boardId);
        if (exists == null) throw new IllegalArgumentException("존재하지 않는 게시글");

        Board toUpdate = Board.builder()
                .boardId(boardId)
                .category(req.getCategory())
                .title(req.getTitle())
                .contents(req.getContents())
                .useYn(req.getUseYn()) // 필요 시 'Y'/'N' 검증 로직 추가
                .mdfcId("testnam01")
                .build();

        int updated = boardMapper.updateBoard(toUpdate);
        if (updated < 1) throw new IllegalStateException("게시글 수정 실패");

        return boardMapper.selectById(boardId);
    }

    @Transactional
    public void deleteSoft(Long boardId, String mdfcId) {
        int updated = boardMapper.deleteSoft(boardId, mdfcId != null ? mdfcId : "testnam01");
        if (updated < 1) throw new IllegalStateException("소프트 삭제 실패");
    }

    @Transactional
    public void deleteHard(Long boardId) {
        int deleted = boardMapper.deleteHard(boardId);
        if (deleted < 1) throw new IllegalStateException("하드 삭제 실패");
    }
}
