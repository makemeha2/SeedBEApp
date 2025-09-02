package com.heyso.SeedBEApp.biz.board.dao;

import com.heyso.SeedBEApp.biz.board.dto.BoardSearchReqDto;
import com.heyso.SeedBEApp.biz.board.model.Board;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {
    List<Board> selectBoardList(@Param("req") BoardSearchReqDto req,
                                @Param("offset") Integer offset,
                                @Param("limit") Integer limit);

    Long selectBoardCount(@Param("req") BoardSearchReqDto req);
}
