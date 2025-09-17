package com.heyso.SeedBEApp.biz.board.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFileResDto {
    private Long fileId;
    private String orgFileNm;
    private Long fileSize;
    private String downloadUrl;
}
