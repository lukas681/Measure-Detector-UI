package com.rs.detector.repository;

import com.rs.detector.domain.Edition;
import com.rs.detector.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Spring Data SQL reactive repository for the Page entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PageRepository extends R2dbcRepository<Page, Long>, PageRepositoryInternal {
    Flux<Page> findAllBy(Pageable pageable);

    @Query("SELECT * FROM page entity WHERE entity.edition_id = :id")
    Flux<Page> findByEdition(Long id);

    @Query("SELECT * FROM page entity WHERE entity.edition_id IS NULL")
    Flux<Page> findAllWhereEditionIsNull();

    // just to avoid having unambigous methods
    @Override
    Flux<Page> findAll();

    Flux<Page> findAllByEditionId(Long l);

    Flux<Page> findAllByEditionIdAndPageNr(Long l, Long pageNr);

    @Override
    Mono<Page> findById(Long id);

    @Override
    <S extends Page> Mono<S> save(S entity);
}

interface PageRepositoryInternal {
    <S extends Page> Mono<S> insert(S entity);
    <S extends Page> Mono<S> save(S entity);
    Mono<Integer> update(Page entity);

    Flux<Page> findAll();
    Mono<Page> findById(Long id);
    Flux<Page> findAllBy(Pageable pageable);
    Flux<Page> findAllBy(Pageable pageable, Criteria criteria);
}
