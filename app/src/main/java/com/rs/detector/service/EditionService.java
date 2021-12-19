package com.rs.detector.service;

import com.rs.detector.domain.Edition;
import com.rs.detector.repository.EditionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Edition}.
 */
@Service
@Transactional
public class EditionService {

    private final Logger log = LoggerFactory.getLogger(EditionService.class);

    private final EditionRepository editionRepository;

    public EditionService(EditionRepository editionRepository) {
        this.editionRepository = editionRepository;
    }

    /**
     * Save a edition.
     *
     * @param edition the entity to save.
     * @return the persisted entity.
     */
    public Mono<Edition> save(Edition edition) {
        log.debug("Request to save Edition : {}", edition);
        return editionRepository.save(edition);
    }

    /**
     * Partially update a edition.
     *
     * @param edition the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Edition> partialUpdate(Edition edition) {
        log.debug("Request to partially update Edition : {}", edition);

        return editionRepository
            .findById(edition.getId())
            .map(existingEdition -> {
                if (edition.getTitle() != null) {
                    existingEdition.setTitle(edition.getTitle());
                }
                if (edition.getCreatedDate() != null) {
                    existingEdition.setCreatedDate(edition.getCreatedDate());
                }
                if (edition.getType() != null) {
                    existingEdition.setType(edition.getType());
                }
                if (edition.getDescription() != null) {
                    existingEdition.setDescription(edition.getDescription());
                }
                if (edition.getpDFFileName() != null) {
                    existingEdition.setpDFFileName(edition.getpDFFileName());
                }

                return existingEdition;
            })
            .flatMap(editionRepository::save);
    }

    /**
     * Get all the editions.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Edition> findAll(Pageable pageable) {
        log.debug("Request to get all Editions");
        return editionRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of editions available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return editionRepository.count();
    }

    /**
     * Get one edition by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Edition> findOne(Long id) {
        log.debug("Request to get Edition : {}", id);
        return editionRepository.findById(id);
    }

    /**
     * Delete the edition by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Edition : {}", id);
        return editionRepository.deleteById(id);
    }

    /**
     *
     * Returns all the editions associated with a project
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Flux<Edition> findAllByProject(Long id) {
        log.debug("Request to get Edition : {}", id);
        return editionRepository.findByProject(id);
    }

}
