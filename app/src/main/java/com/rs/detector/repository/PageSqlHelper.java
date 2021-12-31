package com.rs.detector.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PageSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("page_nr", table, columnPrefix + "_page_nr"));
        columns.add(Column.aliased("img_file_reference", table, columnPrefix + "_img_file_reference"));
        columns.add(Column.aliased("measure_number_offset", table, columnPrefix + "_measure_number_offset"));
        columns.add(Column.aliased("next_page", table, columnPrefix + "_next_page"));

        columns.add(Column.aliased("edition_id", table, columnPrefix + "_edition_id"));
        return columns;
    }
}
