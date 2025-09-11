package com.heyso.SeedBEApp.biz.board.model;

import lombok.*;
import org.apache.ibatis.type.Alias;

import java.time.LocalDateTime;

@Getter @Setter @ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Alias("board")
public class Board {
    private Long boardId;           // BOARD_ID
    private String category;        // CATEGORY
    private String title;           // TITLE
    private String contents;        // CONTENTS (Markdown)
    private String useYn;           // USE_YN ('Y' or 'N')
    private String rgstId;          // RGST_ID
    private LocalDateTime rgstDtm;  // RGST_DTM
    private String mdfcId;          // MDFC_ID
    private LocalDateTime mdfcDtm;  // MDFC_DTM
}
