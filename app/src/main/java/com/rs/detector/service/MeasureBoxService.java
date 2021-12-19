package com.rs.detector.service;

import com.rs.detector.domain.MeasureBox;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.MeasureBoxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.*;

/**
 * Service Implementation for managing {@link MeasureBox}.
 */
@Service
@Transactional
public class MeasureBoxService {

    private final Logger log = LoggerFactory.getLogger(MeasureBoxService.class);

    private final MeasureBoxRepository measureBoxRepository;

    public MeasureBoxService(MeasureBoxRepository measureBoxRepository) {
        this.measureBoxRepository = measureBoxRepository;
    }

    /**
     * Save a measureBox.
     *
     * @param measureBox the entity to save.
     * @return the persisted entity.
     */
    public Mono<MeasureBox> save(MeasureBox measureBox) {
        log.debug("Request to save MeasureBox : {}", measureBox);
        return measureBoxRepository.save(measureBox);
    }
    /**
     * Saved a collection of Measure Boxes .
     *
     * @param measureBox the entity to save.
     * @return the persisted entity.
     */
    public Flux<MeasureBox> saveAll(List<MeasureBox> measureBox) {
        log.debug("Request to save the following MeasureBoxes : {}", measureBox);
        return measureBoxRepository.saveAll(measureBox);
    }

    @Transactional(readOnly = true)
    public Flux<MeasureBox> findAllByPageId(Long pageId) {
        assert(pageId != null);
        log.debug("Finding all by Page Id: " + pageId);
        return measureBoxRepository.findByPage(pageId);
    }
    /**
     * Partially update a measureBox.
     *
     * @param measureBox the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<MeasureBox> partialUpdate(MeasureBox measureBox) {
        log.debug("Request to partially update MeasureBox : {}", measureBox);

        return measureBoxRepository
            .findById(measureBox.getId())
            .map(existingMeasureBox -> {
                if (measureBox.getUlx() != null) {
                    existingMeasureBox.setUlx(measureBox.getUlx());
                }
                if (measureBox.getUly() != null) {
                    existingMeasureBox.setUly(measureBox.getUly());
                }
                if (measureBox.getLrx() != null) {
                    existingMeasureBox.setLrx(measureBox.getLrx());
                }
                if (measureBox.getLry() != null) {
                    existingMeasureBox.setLry(measureBox.getLry());
                }
                if (measureBox.getMeasureCount() != null) {
                    existingMeasureBox.setMeasureCount(measureBox.getMeasureCount());
                }
                if (measureBox.getComment() != null) {
                    existingMeasureBox.setComment(measureBox.getComment());
                }

                return existingMeasureBox;
            })
            .flatMap(measureBoxRepository::save);
    }

    /**
     * Get all the measureBoxes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<MeasureBox> findAll(Pageable pageable) {
        log.debug("Request to get all MeasureBoxes");
        return measureBoxRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of measureBoxes available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return measureBoxRepository.count();
    }

    /**
     * Get one measureBox by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<MeasureBox> findOne(Long id) {
        log.debug("Request to get MeasureBox : {}", id);
        return measureBoxRepository.findById(id);
    }

    /**
     * Delete the measureBox by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete MeasureBox : {}", id);
        return measureBoxRepository.deleteById(id);
    }

    public Mono<Void> deleteAllById(List<Long> ids) {
        log.debug("Deleting the following boxes: " + ids);
        return measureBoxRepository.deleteAllById(ids);
    }

}
