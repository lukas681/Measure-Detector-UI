package com.rs.detector.web.rest;

import com.rs.detector.domain.Edition;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.service.EditionService;
import com.rs.detector.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.rs.detector.domain.Edition}.
 */
@RestController
@RequestMapping("/api")
public class EditionResource {

    private final Logger log = LoggerFactory.getLogger(EditionResource.class);

    private static final String ENTITY_NAME = "edition";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EditionService editionService;

    private final EditionRepository editionRepository;

    public EditionResource(EditionService editionService, EditionRepository editionRepository) {
        this.editionService = editionService;
        this.editionRepository = editionRepository;
    }

    /**
     * {@code POST  /editions} : Create a new edition.
     *
     * @param edition the edition to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new edition, or with status {@code 400 (Bad Request)} if the edition has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/editions")
    public Mono<ResponseEntity<Edition>> createEdition(@Valid @RequestBody Edition edition) throws URISyntaxException {
        log.debug("REST request to save Edition : {}", edition);
        if (edition.getId() != null) {
            throw new BadRequestAlertException("A new edition cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return editionService
            .save(edition)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/editions/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /editions/:id} : Updates an existing edition.
     *
     * @param id the id of the edition to save.
     * @param edition the edition to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated edition,
     * or with status {@code 400 (Bad Request)} if the edition is not valid,
     * or with status {@code 500 (Internal Server Error)} if the edition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/editions/{id}")
    public Mono<ResponseEntity<Edition>> updateEdition(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Edition edition
    ) throws URISyntaxException {
        log.debug("REST request to update Edition : {}, {}", id, edition);
        if (edition.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, edition.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return editionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return editionService
                    .save(edition)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /editions/:id} : Partial updates given fields of an existing edition, field will ignore if it is null
     *
     * @param id the id of the edition to save.
     * @param edition the edition to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated edition,
     * or with status {@code 400 (Bad Request)} if the edition is not valid,
     * or with status {@code 404 (Not Found)} if the edition is not found,
     * or with status {@code 500 (Internal Server Error)} if the edition couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/editions/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Edition>> partialUpdateEdition(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Edition edition
    ) throws URISyntaxException {
        log.debug("REST request to partial update Edition partially : {}, {}", id, edition);
        if (edition.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, edition.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return editionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Edition> result = editionService.partialUpdate(edition);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /editions} : get all the editions.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of editions in body.
     */
    @GetMapping("/editions")
    public Mono<ResponseEntity<List<Edition>>> getAllEditions(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of Editions");
        return editionService
            .countAll()
            .zipWith(editionService.findAll(pageable).collectList())
            .map(countWithEntities -> {
                return ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2());
            });
    }

    /**
     * {@code GET  /editions/:id} : get the "id" edition.
     *
     * @param id the id of the edition to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the edition, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/editions/{id}")
    public Mono<ResponseEntity<Edition>> getEdition(@PathVariable Long id) {
        log.debug("REST request to get Edition : {}", id);
        Mono<Edition> edition = editionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(edition);
    }

    /**
     * {@code DELETE  /editions/:id} : delete the "id" edition.
     *
     * @param id the id of the edition to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/editions/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEdition(@PathVariable Long id) {
        log.debug("REST request to delete Edition : {}", id);
        return editionService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
