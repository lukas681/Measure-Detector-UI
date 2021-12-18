package com.rs.detector.repository;

import com.rs.detector.domain.MeasureBox;
import com.rs.detector.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the MeasureBox entity.
 */
@SuppressWarnings("unused")
@Repository
public interface MeasureBoxRepository extends R2dbcRepository<MeasureBox, Long>, MeasureBoxRepositoryInternal {
    Flux<MeasureBox> findAllBy(Pageable pageable);

    @Query("SELECT * FROM measure_box entity WHERE entity.page_id = :id")
    Flux<MeasureBox> findByPage(Long id);

    @Query("SELECT * FROM measure_box entity WHERE entity.page_id IS NULL")
    Flux<MeasureBox> findAllWherePageIsNull();

    Flux<MeasureBox> findAllByPage(Page p);

    // just to avoid having unambigous methods
    @Override
    Flux<MeasureBox> findAll();

    @Override
    Mono<MeasureBox> findById(Long id);

    @Override
    <S extends MeasureBox> Mono<S> save(S entity);
}

interface MeasureBoxRepositoryInternal {
    <S extends MeasureBox> Mono<S> insert(S entity);
    <S extends MeasureBox> Mono<S> save(S entity);
    Mono<Integer> update(MeasureBox entity);

    Flux<MeasureBox> findAll();
    Mono<MeasureBox> findById(Long id);
    Flux<MeasureBox> findAllBy(Pageable pageable);
    Flux<MeasureBox> findAllBy(Pageable pageable, Criteria criteria);
}
