package com.heyso.SeedBEApp.biz.board.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardSearchReqDto {
    /** 카테고리(선택) - null/빈문자면 전체 */
    private String category;

    /** 검색어(선택) - 제목/본문 LIKE 검색 or FULLTEXT로 확장 가능 */
    private String searchText;

    /** 페이지 번호(선택) - 1부터 시작, null이면 페이징 없음 */
    private Integer page;

    /** 페이지 크기(선택) - null이면 페이징 없음, 기본값/상한은 서비스에서 보정 */
    private Integer pageSize;

    /** 페이징 사용 여부 */
    @JsonIgnore
    public boolean isPagingEnabled() {
        return page != null && pageSize != null && page > 0 && pageSize > 0;
    }
}
