package com.rs.detector.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import com.rs.detector.domain.Page;
import com.rs.detector.repository.rowmapper.EditionRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Page entity.
 */
@SuppressWarnings("unused")
class PageRepositoryInternalImpl implements PageRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EditionRowMapper editionMapper;
    private final PageRowMapper pageMapper;

    private static final Table entityTable = Table.aliased("page", EntityManager.ENTITY_ALIAS);
    private static final Table editionTable = Table.aliased("edition", "edition");

    public PageRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EditionRowMapper editionMapper,
        PageRowMapper pageMapper
    ) {
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.editionMapper = editionMapper;
        this.pageMapper = pageMapper;
    }

    @Override
    public Flux<Page> findAllBy(Pageable pageable) {
        return findAllBy(pageable, null);
    }

    @Override
    public Flux<Page> findAllBy(Pageable pageable, Criteria criteria) {
        return createQuery(pageable, criteria).all();
    }

    RowsFetchSpec<Page> createQuery(Pageable pageable, Criteria criteria) {
        List<Expression> columns = PageSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EditionSqlHelper.getColumns(editionTable, "edition"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(editionTable)
            .on(Column.create("edition_id", entityTable))
            .equals(Column.create("id", editionTable));

        String select = entityManager.createSelect(selectFrom, Page.class, pageable, criteria);
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
    public Flux<Page> findAll() {
        return findAllBy(null, null);
    }

    @Override
    public Mono<Page> findById(Long id) {
        return createQuery(null, where("id").is(id)).one();
    }

    private Page process(Row row, RowMetadata metadata) {
        Page entity = pageMapper.apply(row, "e");
        entity.setEdition(editionMapper.apply(row, "edition"));
        return entity;
    }

    @Override
    public <S extends Page> Mono<S> insert(S entity) {
        return entityManager.insert(entity);
    }

    @Override
    public <S extends Page> Mono<S> save(S entity) {
        if (entity.getId() == null) {
            return insert(entity);
        } else {
            return update(entity)
                .map(numberOfUpdates -> {
                    if (numberOfUpdates.intValue() <= 0) {
                        throw new IllegalStateException("Unable to update Page with id = " + entity.getId());
                    }
                    return entity;
                });
        }
    }

    @Override
    public Mono<Integer> update(Page entity) {
        //fixme is this the proper way?
        return r2dbcEntityTemplate.update(entity).thenReturn(1);
    }
}
