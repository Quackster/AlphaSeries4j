package com.alphaseries.dao.mysql;

import com.alphaseries.db.Database;

import java.sql.SQLException;
import java.util.List;

public final class HelpDao {
    private final Database database;

    public HelpDao(Database database) {
        this.database = database;
    }

    public List<FaqNameRow> searchFaqs(String searchText) throws SQLException {
        return database.query(
            "SELECT id,name FROM faq WHERE name LIKE ? LIMIT 25",
            resultSet -> new FaqNameRow(resultSet.getLong(1), String.valueOf(resultSet.getString(2))),
            "%" + searchText + "%");
    }

    public List<FaqNameRow> importantFaqRows(long importanceLevel) throws SQLException {
        return database.query(
            "SELECT id,name FROM faq WHERE is_important=? ORDER BY id DESC LIMIT 1",
            resultSet -> new FaqNameRow(resultSet.getLong(1), resultSet.getString(2)),
            importanceLevel);
    }

    public long maxCategoryId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM faq_categories",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<FaqNameRow> categoryRows() throws SQLException {
        return database.query(
            "SELECT id,name FROM faq_categories",
            resultSet -> new FaqNameRow(resultSet.getLong(1), resultSet.getString(2)));
    }

    public List<FaqNameRow> faqRowsByCategory(long categoryId) throws SQLException {
        return database.query(
            "SELECT id,name FROM faq WHERE id_category=?",
            resultSet -> new FaqNameRow(resultSet.getLong(1), resultSet.getString(2)),
            categoryId);
    }

    public long maxFaqId() throws SQLException {
        return database.queryOne(
            "SELECT MAX(id) FROM faq",
            resultSet -> resultSet.getLong(1))
            .orElse(0L);
    }

    public List<FaqDescriptionRow> descriptionRows() throws SQLException {
        return database.query(
            "SELECT id,description FROM faq",
            resultSet -> new FaqDescriptionRow(resultSet.getLong(1), resultSet.getString(2)));
    }

    public record FaqNameRow(long id, String name) {
    }

    public record FaqDescriptionRow(long id, String description) {
    }
}
