package com.rs.detector.repository.rowmapper;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.time.Instant;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Edition}, with proper type conversions.
 */
@Service
public class EditionRowMapper implements BiFunction<Row, String, Edition> {

    private final ColumnConverter converter;

    public EditionRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Edition} stored in the database.
     */
    @Override
    public Edition apply(Row row, String prefix) {
        Edition entity = new Edition();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTitle(converter.fromRow(row, prefix + "_title", String.class));
        entity.setCreatedDate(converter.fromRow(row, prefix + "_created_date", Instant.class));
        entity.setType(converter.fromRow(row, prefix + "_type", EditionType.class));
        entity.setDescription(converter.fromRow(row, prefix + "_description", String.class));
        entity.setProjectId(converter.fromRow(row, prefix + "_project_id", Long.class));
        return entity;
    }
}
