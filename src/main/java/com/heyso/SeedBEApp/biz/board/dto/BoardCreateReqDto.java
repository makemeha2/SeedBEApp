package com.heyso.SeedBEApp.biz.board.dto;


import com.heyso.SeedBEApp.biz.board.model.BoardCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class BoardCreateReqDto {
    @NotNull
    private BoardCategory category;

    @NotBlank(message = "제목은 필수입니다.")
    @Size(max = 300, message = "제목은 최대 300자입니다.")
    private String title;

    @NotBlank(message = "본문은 필수입니다.")
    private String contents;   // Markdown 텍스트

//    @Size(max = 1, message = "USE_YN은 'Y' 또는 'N' 이어야 합니다.")
//    private String useYn;      // 생략 가능 (기본값 'Y'로 보정)
//
//    @NotBlank(message = "등록자(RGST_ID)는 필수입니다.")
//    @Size(max = 30, message = "등록자는 최대 30자입니다.")
//    private String rgstId;
}
