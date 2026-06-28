package com.alphaseries;

import java.sql.SQLException;
import java.util.List;

public interface Database {
    void execute(String sqlText) throws SQLException;

    List<List<Object>> query(String sqlText) throws SQLException;
}
