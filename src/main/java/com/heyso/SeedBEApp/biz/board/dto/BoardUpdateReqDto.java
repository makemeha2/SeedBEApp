package com.heyso.SeedBEApp.biz.board.dto;

import com.heyso.SeedBEApp.biz.board.model.BoardCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter @Setter @ToString
public class BoardUpdateReqDto {
    @NotNull
    private BoardCategory category;
    @Size(max = 300)
    private String title;
    private String contents;
    @Size(max = 1)
    private String useYn;
    private List<Long> deleteFileIds;
}