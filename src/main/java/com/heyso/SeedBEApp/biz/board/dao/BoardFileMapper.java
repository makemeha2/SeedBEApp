package com.heyso.SeedBEApp.biz.board.dao;

import com.heyso.SeedBEApp.biz.board.model.BoardFile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BoardFileMapper {
    int insertBoardFiles(List<BoardFile> files);
    List<BoardFile> selectFilesByBoardId(Long boardId);
    BoardFile selectFileById(Long fileId);
    int deleteFileById(Long fileId);
}
