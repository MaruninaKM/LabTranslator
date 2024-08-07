package com.example.labtranslator.repository;

import com.example.labtranslator.model.LabRequest;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

//---------- Класс - репозиторий - компонент для доступа к данным ----------
@Repository
public class LabRepository {
    // Для базы данных использовать только JDBC
    private final JdbcTemplate jdbcTemplate;

    //----- Конструктор для JDBC -----
    public LabRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //----- Методы -----
    //--- Метод для сохранения в БД ---
    public void save(LabRequest request) {
        // SQL запрос для добавления записи в таблицу perevod
        String sql = "INSERT INTO perevod (ip, input_string, output_string) VALUES (?, ?, ?)";
        // Выполнение через JDBC
        jdbcTemplate.update(sql, request.getIp(), request.getInputText(), request.getOutputText());
    }

    //--- Метод для получения записей из таблицы perevod ---
    public List<LabRequest> toRes() {
        return jdbcTemplate.query("SELECT * FROM perevod", this::result);
    }

    //--- Метод для возвращения объекта LabRequest ---
    private LabRequest result(ResultSet rs, int rowNum) throws SQLException {
        LabRequest request = new LabRequest();
        request.setId(rs.getLong("id"));
        request.setIp(rs.getString("ip"));
        request.setInputText(rs.getString("input_string"));
        request.setOutputText(rs.getString("output_string"));
        return request;
    }
}