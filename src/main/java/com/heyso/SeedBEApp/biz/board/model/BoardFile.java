package com.heyso.SeedBEApp.biz.board.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardFile {
    private Long fileId;
    private Long boardId;
    private String orgFileNm;
    private String storedFileNm;
    private String fileExt;
    private String mimeType;
    private Long fileSize;
    private String filePath;
    private String useYn;
    private Long rgstId;          // RGST_ID
    private String rgstUsername;
    private LocalDateTime rgstDtm;  // RGST_DTM
    private Long mdfcId;          // MDFC_ID
    private String mdfcUsername;
    private LocalDateTime mdfcDtm;  // MDFC_DTM
}
