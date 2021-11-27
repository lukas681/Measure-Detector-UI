package com.rs.detector.repository.rowmapper;

import com.rs.detector.domain.MeasureBox;
import com.rs.detector.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link MeasureBox}, with proper type conversions.
 */
@Service
public class MeasureBoxRowMapper implements BiFunction<Row, String, MeasureBox> {

    private final ColumnConverter converter;

    public MeasureBoxRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link MeasureBox} stored in the database.
     */
    @Override
    public MeasureBox apply(Row row, String prefix) {
        MeasureBox entity = new MeasureBox();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setUlx(converter.fromRow(row, prefix + "_ulx", Long.class));
        entity.setUly(converter.fromRow(row, prefix + "_uly", Long.class));
        entity.setLrx(converter.fromRow(row, prefix + "_lrx", Long.class));
        entity.setLry(converter.fromRow(row, prefix + "_lry", Long.class));
        entity.setMeasureCount(converter.fromRow(row, prefix + "_measure_count", Long.class));
        entity.setComment(converter.fromRow(row, prefix + "_comment", String.class));
        entity.setPageId(converter.fromRow(row, prefix + "_page_id", Long.class));
        return entity;
    }
}
