package com.rs.detector.web.rest;

import com.rs.detector.domain.MeasureBox;
import com.rs.detector.repository.MeasureBoxRepository;
import com.rs.detector.service.MeasureBoxService;
import com.rs.detector.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
 * REST controller for managing {@link com.rs.detector.domain.MeasureBox}.
 */
@RestController
@RequestMapping("/api")
public class MeasureBoxResource {

    private final Logger log = LoggerFactory.getLogger(MeasureBoxResource.class);

    private static final String ENTITY_NAME = "measureBox";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final MeasureBoxService measureBoxService;

    private final MeasureBoxRepository measureBoxRepository;

    public MeasureBoxResource(MeasureBoxService measureBoxService, MeasureBoxRepository measureBoxRepository) {
        this.measureBoxService = measureBoxService;
        this.measureBoxRepository = measureBoxRepository;
    }

    /**
     * {@code POST  /measure-boxes} : Create a new measureBox.
     *
     * @param measureBox the measureBox to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new measureBox, or with status {@code 400 (Bad Request)} if the measureBox has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/measure-boxes")
    public Mono<ResponseEntity<MeasureBox>> createMeasureBox(@RequestBody MeasureBox measureBox) throws URISyntaxException {
        log.debug("REST request to save MeasureBox : {}", measureBox);
        if (measureBox.getId() != null) {
            throw new BadRequestAlertException("A new measureBox cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return measureBoxService
            .save(measureBox)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/measure-boxes/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /measure-boxes/:id} : Updates an existing measureBox.
     *
     * @param id the id of the measureBox to save.
     * @param measureBox the measureBox to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated measureBox,
     * or with status {@code 400 (Bad Request)} if the measureBox is not valid,
     * or with status {@code 500 (Internal Server Error)} if the measureBox couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/measure-boxes/{id}")
    public Mono<ResponseEntity<MeasureBox>> updateMeasureBox(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MeasureBox measureBox
    ) throws URISyntaxException {
        log.debug("REST request to update MeasureBox : {}, {}", id, measureBox);
        if (measureBox.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, measureBox.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return measureBoxRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return measureBoxService
                    .save(measureBox)
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
     * {@code PATCH  /measure-boxes/:id} : Partial updates given fields of an existing measureBox, field will ignore if it is null
     *
     * @param id the id of the measureBox to save.
     * @param measureBox the measureBox to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated measureBox,
     * or with status {@code 400 (Bad Request)} if the measureBox is not valid,
     * or with status {@code 404 (Not Found)} if the measureBox is not found,
     * or with status {@code 500 (Internal Server Error)} if the measureBox couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/measure-boxes/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<MeasureBox>> partialUpdateMeasureBox(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody MeasureBox measureBox
    ) throws URISyntaxException {
        log.debug("REST request to partial update MeasureBox partially : {}, {}", id, measureBox);
        if (measureBox.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, measureBox.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return measureBoxRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<MeasureBox> result = measureBoxService.partialUpdate(measureBox);

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
     * {@code GET  /measure-boxes} : get all the measureBoxes.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of measureBoxes in body.
     */
    @GetMapping("/measure-boxes")
    public Mono<ResponseEntity<List<MeasureBox>>> getAllMeasureBoxes(Pageable pageable, ServerHttpRequest request) {
        log.debug("REST request to get a page of MeasureBoxes");
        return measureBoxService
            .countAll()
            .zipWith(measureBoxService.findAll(pageable).collectList())
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
     * {@code GET  /measure-boxes/:id} : get the "id" measureBox.
     *
     * @param id the id of the measureBox to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the measureBox, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/measure-boxes/{id}")
    public Mono<ResponseEntity<MeasureBox>> getMeasureBox(@PathVariable Long id) {
        log.debug("REST request to get MeasureBox : {}", id);
        Mono<MeasureBox> measureBox = measureBoxService.findOne(id);
        return ResponseUtil.wrapOrNotFound(measureBox);
    }

    /**
     * {@code DELETE  /measure-boxes/:id} : delete the "id" measureBox.
     *
     * @param id the id of the measureBox to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/measure-boxes/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteMeasureBox(@PathVariable Long id) {
        log.debug("REST request to delete MeasureBox : {}", id);
        return measureBoxService
            .delete(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
