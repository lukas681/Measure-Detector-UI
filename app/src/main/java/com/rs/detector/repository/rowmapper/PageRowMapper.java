package com.rs.detector.repository.rowmapper;

import com.rs.detector.domain.Page;
import com.rs.detector.service.ColumnConverter;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Page}, with proper type conversions.
 */
@Service
public class PageRowMapper implements BiFunction<Row, String, Page> {

    private final ColumnConverter converter;

    public PageRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Page} stored in the database.
     */
    @Override
    public Page apply(Row row, String prefix) {
        Page entity = new Page();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setPageNr(converter.fromRow(row, prefix + "_page_nr", Long.class));
        entity.setImgFileReference(converter.fromRow(row, prefix + "_img_file_reference", String.class));
        entity.setMeasureNumberOffset(converter.fromRow(row, prefix + "_measure_number_offset", Long.class));
        entity.setNextPage(converter.fromRow(row, prefix + "_next_page", Long.class));
        entity.setEditionId(converter.fromRow(row, prefix + "_edition_id", Long.class));
        return entity;
    }
}
