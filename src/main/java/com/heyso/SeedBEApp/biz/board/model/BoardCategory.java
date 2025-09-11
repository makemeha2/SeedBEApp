package com.heyso.SeedBEApp.biz.board.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum BoardCategory {
    NOTICE("NOTICE", "공지사항", "NTS"),
    FREE("FREE", "자유게시판", "FREE"),
    QNA("Q&A", "질의게시판", "QNA");

    private final String key;
    private final String Desc;
    private final String DbVal;
}
