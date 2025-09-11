package com.heyso.SeedBEApp.biz.board.dto;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @ToString
public class BoardUpdateReqDto {
    @Size(max = 10)  private String category;
    @Size(max = 300) private String title;
    private String contents;
    @Size(max = 1)   private String useYn;
}