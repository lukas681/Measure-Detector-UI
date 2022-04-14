package com.rs.detector.repository;

import com.rs.detector.domain.Edition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Edition entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EditionRepository extends R2dbcRepository<Edition, Long>, EditionRepositoryInternal {
    Flux<Edition> findAllBy(Pageable pageable);

    @Query("SELECT * FROM edition entity WHERE entity.project_id = :id")
    Flux<Edition> findByProject(Long id);

    @Query("SELECT * FROM edition entity WHERE entity.project_id IS NULL")
    Flux<Edition> findAllWhereProjectIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Edition> findAll();

    @Override
    Mono<Edition> findById(Long id);

    @Override
    <S extends Edition> Mono<S> save(S entity);
}

interface EditionRepositoryInternal {
    <S extends Edition> Mono<S> insert(S entity);
    <S extends Edition> Mono<S> save(S entity);
    Mono<Integer> update(Edition entity);

    Flux<Edition> findAll();
    // Mono<Edition> findById(Long id);
    Flux<Edition> findAllBy(Pageable pageable);
    Flux<Edition> findAllBy(Pageable pageable, Criteria criteria);
}
