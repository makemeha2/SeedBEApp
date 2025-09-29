package com.heyso.SeedBEApp.biz.board.web;

import com.heyso.SeedBEApp.biz.board.dto.BoardSearchReqDto;
import com.heyso.SeedBEApp.biz.board.model.BoardCategory;

public class BoardCategoryUtil {
    private BoardCategoryUtil() {}
    static void forceCategory(BoardSearchReqDto req, BoardCategory category) {
        if (req == null) return;
        req.setCategory(category); // BoardSearchReqDto 에 setCategory(String) 있다고 가정
    }
}
