package com.heyso.SeedBEApp.common.typehandler;

import com.heyso.SeedBEApp.biz.board.model.BoardCategory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BoardCategoryTypeHandler extends BaseTypeHandler<BoardCategory> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, BoardCategory parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.name()); // Enum → 문자열 (ex: NOTICE)
    }

    @Override
    public BoardCategory getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? BoardCategory.valueOf(value) : null;
    }

    @Override
    public BoardCategory getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? BoardCategory.valueOf(value) : null;
    }

    @Override
    public BoardCategory getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? BoardCategory.valueOf(value) : null;
    }
}
