package com.jimmy.etfquant.repository;

import com.jimmy.etfquant.dto.EtfResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class EtfRepository {

    private final JdbcTemplate jdbcTemplate;

    public EtfRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EtfResponse> findAllEtfs() {
        String sql = "SELECT symbol, name, category, description FROM sp_get_all_etfs()";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new EtfResponse(
                rs.getString("symbol"),
                rs.getString("name"),
                rs.getString("category"),
                rs.getString("description")
        ));
    }
}