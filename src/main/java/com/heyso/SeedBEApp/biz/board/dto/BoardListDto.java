package com.heyso.SeedBEApp.biz.board.dto;

import com.heyso.SeedBEApp.biz.board.model.Board;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class BoardListDto {
    private List<Board> items;      // 결과 목록
    private Long totalCount;        // 총 건수 (페이징 미사용 시 null 가능)
    private Integer page;           // 현재 페이지 (페이징 미사용 시 null)
    private Integer pageSize;       // 페이지 크기 (페이징 미사용 시 null)
    private Integer totalPages;     // 총 페이지 수 (페이징 미사용 시 null)

    public static BoardListDto ofNoPaging(List<Board> items) {
        return BoardListDto.builder()
                .items(items)
                .totalCount((long)items.size())
                .page(null).pageSize(null).totalPages(null)
                .build();
    }
}
