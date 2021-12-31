package com.rs.detector.service;

import com.rs.detector.domain.Page;
import com.rs.detector.repository.MeasureBoxRepository;
import com.rs.detector.repository.PageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Page}.
 */
@Service
@Transactional
public class PageService {

    private final Logger log = LoggerFactory.getLogger(PageService.class);

    private final PageRepository pageRepository;

    private final MeasureBoxService measureBoxService;

    private final MeasureBoxRepository measureBoxRepository;

    public PageService(PageRepository pageRepository, MeasureBoxService measureBoxService, MeasureBoxRepository measureBoxRepository) {
        this.pageRepository = pageRepository;
        this.measureBoxService = measureBoxService;
        this.measureBoxRepository = measureBoxRepository;
    }

    /**
     * Save a page.
     *
     * @param page the entity to save.
     * @return the persisted entity.
     */
    public Mono<Page> save(Page page) {
        log.debug("Request to save Page : {}", page);
        return pageRepository.save(page);
    }

    /**
     * Partially update a page.
     *
     * @param page the entity to update partially.
     * @return the persisted entity.
     */
    public Mono<Page> partialUpdate(Page page) {
        log.debug("Request to partially update Page : {}", page);

        return pageRepository
            .findById(page.getId())
            .map(existingPage -> {
                if (page.getPageNr() != null) {
                    existingPage.setPageNr(page.getPageNr());
                }
                if (page.getImgFileReference() != null) {
                    existingPage.setImgFileReference(page.getImgFileReference());
                }
                if (page.getMeasureNumberOffset() != null) {
                    existingPage.setMeasureNumberOffset(page.getMeasureNumberOffset());
                }
                if (page.getNextPage() != null) {
                    existingPage.setNextPage(page.getNextPage());
                }

                return existingPage;
            })
            .flatMap(pageRepository::save);
    }

    /**
     * Get all the pages.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Flux<Page> findAll(Pageable pageable) {
        log.debug("Request to get all Pages");
        return pageRepository.findAllBy(pageable);
    }

    /**
     * Returns the number of pages available.
     * @return the number of entities in the database.
     *
     */
    public Mono<Long> countAll() {
        return pageRepository.count();
    }

    /**
     * Get one page by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Mono<Page> findOne(Long id) {
        log.debug("Request to get Page : {}", id);
        return pageRepository.findById(id);
    }

    /**
     * Delete the page by id.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    public Mono<Void> delete(Long id) {
        log.debug("Request to delete Page : {}", id);
        return pageRepository.deleteById(id);
    }


    /**
     * Deletes all boxes attached to one page
     * @param pageId
     * @return
     */
    public Flux<Void> deleteAllMeasureBoxes(Long pageId) {
        log.debug("Fetching all Measure Boxes");

        return measureBoxRepository.findByPageId(pageId)
          .flatMap(x->
              measureBoxRepository.deleteById(x.getId())
          );
    }
}
