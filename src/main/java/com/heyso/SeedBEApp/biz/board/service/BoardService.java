package com.heyso.SeedBEApp.biz.board.service;

import com.heyso.SeedBEApp.biz.board.dao.BoardMapper;
import com.heyso.SeedBEApp.biz.board.dto.*;
import com.heyso.SeedBEApp.biz.board.model.Board;
import com.heyso.SeedBEApp.biz.board.model.BoardFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;
    private final BoardFileService boardFileService;

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

    @Transactional(readOnly = true)
    public BoardDetailDto getBoardDetail(Long boardId) {
        Board board = boardMapper.selectById(boardId);
        if (board == null) {
            throw new IllegalArgumentException("존재하지 않는 게시글: " + boardId);
        }

        List<BoardFile> files = boardFileService.getFiles(boardId);
        return BoardDetailDto.builder()
                .board(board)
                .files(files)
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
    public Board updateBoardWithFiles(Long boardId,
                                      BoardUpdateReqDto meta,
                                      List<MultipartFile> newFiles) throws Exception {
        // 1) 메타 수정 (기존 메서드 재사용 → 중복 제거)
        Board updated = this.updateBoard(boardId, meta);

        // 2) 파일 삭제
        if (meta.getDeleteFileIds() != null) {
            for (Long fileId : meta.getDeleteFileIds()) {
                boardFileService.deleteFile(fileId);
            }
        }

        // 3) 파일 추가
        if (newFiles != null && !newFiles.isEmpty()) {
            // 수정자 ID는 meta.mdfcId가 있으면 쓰고, 없으면 기존 updateBoard에서 사용한 값과 동일 정책 사용
            String actorId = "testnam01"; // TODO: Security 컨텍스트로 대체
            boardFileService.saveFiles(boardId, newFiles, actorId);
        }

        // 4) 최종 상태 리턴
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
