package com.rs.detector.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class MeasureBoxSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("ulx", table, columnPrefix + "_ulx"));
        columns.add(Column.aliased("uly", table, columnPrefix + "_uly"));
        columns.add(Column.aliased("lrx", table, columnPrefix + "_lrx"));
        columns.add(Column.aliased("lry", table, columnPrefix + "_lry"));
        columns.add(Column.aliased("measure_count", table, columnPrefix + "_measure_count"));
        columns.add(Column.aliased("comment", table, columnPrefix + "_comment"));

        columns.add(Column.aliased("page_id", table, columnPrefix + "_page_id"));
        return columns;
    }
}
