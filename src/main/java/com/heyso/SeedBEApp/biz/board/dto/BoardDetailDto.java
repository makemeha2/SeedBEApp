package com.heyso.SeedBEApp.biz.board.dto;

import com.heyso.SeedBEApp.biz.board.model.Board;
import com.heyso.SeedBEApp.biz.board.model.BoardFile;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDetailDto {
    private Board board;
    private List<BoardFile> files;
}
