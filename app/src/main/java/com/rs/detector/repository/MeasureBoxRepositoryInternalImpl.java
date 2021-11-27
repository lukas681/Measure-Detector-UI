package com.rs.detector.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.rs.detector.domain.MeasureBox;
import com.rs.detector.repository.rowmapper.MeasureBoxRowMapper;
import com.rs.detector.repository.rowmapper.PageRowMapper;
import com.rs.detector.service.EntityManager;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the MeasureBox entity.
 */
@SuppressWarnings("unused")
class MeasureBoxRepositoryInternalImpl implements MeasureBoxRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PageRowMapper pageMapper;
    private final MeasureBoxRowMapper measureboxMapper;

    private static final Table entityTable = Table.aliased("measure_box", EntityManager.ENTITY_ALIAS);
    private static final Table pageTable = Table.aliased("page", "page");

    public MeasureBoxRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PageRowMapper pageMapper,
        MeasureBoxRowMapper measureboxMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.pageMapper = pageMapper;
        this.measureboxMapper = measureboxMapper;
    }

    @Override
    public Flux<MeasureBox> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<MeasureBox> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<MeasureBox> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = MeasureBoxSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PageSqlHelper.getColumns(pageTable, "page"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(pageTable)
            .on(Column.create("page_id", entityTable))
            .equals(Column.create("id", pageTable));

        String select = entityManager.createSelect(selectFrom, MeasureBox.class, pageable, criteria);
        String alias = entityTable.getReferenceName().getReference();
        String selectWhere = Optional
            .ofNullable(criteria)
            .map(crit ->
                new StringBuilder(select)
                    .append(" ")
                    .append("WHERE")
                    .append(" ")
                    .append(alias)
                    .append(".")
                    .append(crit.toString())
                    .toString()
            )
            .orElse(select); // TODO remove once https://github.com/spring-projects/spring-data-jdbc/issues/907 will be fixed
        return db.sql(selectWhere).map(this::process);
    }

    @Override
    public Flux<MeasureBox> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<MeasureBox> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private MeasureBox process(Row row, RowMetadata metadata) {
        MeasureBox entity = measureboxMapper.apply(row, "e");
        entity.setPage(pageMapper.apply(row, "page"));
        return entity;
    }

    @Override
    public <S extends MeasureBox> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends MeasureBox> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update MeasureBox with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(MeasureBox entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
