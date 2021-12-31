package com.rs.detector.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.rs.detector.IntegrationTest;
import com.rs.detector.domain.Page;
import com.rs.detector.repository.PageRepository;
import com.rs.detector.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link PageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class PageResourceIT {

    private static final Long DEFAULT_PAGE_NR = 1L;
    private static final Long UPDATED_PAGE_NR = 2L;

    private static final String DEFAULT_IMG_FILE_REFERENCE = "AAAAAAAAAA";
    private static final String UPDATED_IMG_FILE_REFERENCE = "BBBBBBBBBB";

    private static final Long DEFAULT_MEASURE_NUMBER_OFFSET = 1L;
    private static final Long UPDATED_MEASURE_NUMBER_OFFSET = 2L;

    private static final Long DEFAULT_NEXT_PAGE = 1L;
    private static final Long UPDATED_NEXT_PAGE = 2L;

    private static final String ENTITY_API_URL = "/api/pages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PageRepository pageRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Page page;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Page createEntity(EntityManager em) {
        Page page = new Page()
            .pageNr(DEFAULT_PAGE_NR)
            .imgFileReference(DEFAULT_IMG_FILE_REFERENCE)
            .measureNumberOffset(DEFAULT_MEASURE_NUMBER_OFFSET)
            .nextPage(DEFAULT_NEXT_PAGE);
        return page;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Page createUpdatedEntity(EntityManager em) {
        Page page = new Page()
            .pageNr(UPDATED_PAGE_NR)
            .imgFileReference(UPDATED_IMG_FILE_REFERENCE)
            .measureNumberOffset(UPDATED_MEASURE_NUMBER_OFFSET)
            .nextPage(UPDATED_NEXT_PAGE);
        return page;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Page.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        page = createEntity(em);
    }

    @Test
    void createPage() throws Exception {
        int databaseSizeBeforeCreate = pageRepository.findAll().collectList().block().size();
        // Create the Page
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeCreate + 1);
        Page testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getPageNr()).isEqualTo(DEFAULT_PAGE_NR);
        assertThat(testPage.getImgFileReference()).isEqualTo(DEFAULT_IMG_FILE_REFERENCE);
        assertThat(testPage.getMeasureNumberOffset()).isEqualTo(DEFAULT_MEASURE_NUMBER_OFFSET);
        assertThat(testPage.getNextPage()).isEqualTo(DEFAULT_NEXT_PAGE);
    }

    @Test
    void createPageWithExistingId() throws Exception {
        // Create the Page with an existing ID
        page.setId(1L);

        int databaseSizeBeforeCreate = pageRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkPageNrIsRequired() throws Exception {
        int databaseSizeBeforeTest = pageRepository.findAll().collectList().block().size();
        // set the field null
        page.setPageNr(null);

        // Create the Page, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllPages() {
        // Initialize the database
        pageRepository.save(page).block();

        // Get all the pageList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(page.getId().intValue()))
            .jsonPath("$.[*].pageNr")
            .value(hasItem(DEFAULT_PAGE_NR.intValue()))
            .jsonPath("$.[*].imgFileReference")
            .value(hasItem(DEFAULT_IMG_FILE_REFERENCE))
            .jsonPath("$.[*].measureNumberOffset")
            .value(hasItem(DEFAULT_MEASURE_NUMBER_OFFSET.intValue()))
            .jsonPath("$.[*].nextPage")
            .value(hasItem(DEFAULT_NEXT_PAGE.intValue()));
    }

    @Test
    void getPage() {
        // Initialize the database
        pageRepository.save(page).block();

        // Get the page
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, page.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(page.getId().intValue()))
            .jsonPath("$.pageNr")
            .value(is(DEFAULT_PAGE_NR.intValue()))
            .jsonPath("$.imgFileReference")
            .value(is(DEFAULT_IMG_FILE_REFERENCE))
            .jsonPath("$.measureNumberOffset")
            .value(is(DEFAULT_MEASURE_NUMBER_OFFSET.intValue()))
            .jsonPath("$.nextPage")
            .value(is(DEFAULT_NEXT_PAGE.intValue()));
    }

    @Test
    void getNonExistingPage() {
        // Get the page
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPage() throws Exception {
        // Initialize the database
        pageRepository.save(page).block();

        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();

        // Update the page
        Page updatedPage = pageRepository.findById(page.getId()).block();
        updatedPage
            .pageNr(UPDATED_PAGE_NR)
            .imgFileReference(UPDATED_IMG_FILE_REFERENCE)
            .measureNumberOffset(UPDATED_MEASURE_NUMBER_OFFSET)
            .nextPage(UPDATED_NEXT_PAGE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPage.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
        Page testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getPageNr()).isEqualTo(UPDATED_PAGE_NR);
        assertThat(testPage.getImgFileReference()).isEqualTo(UPDATED_IMG_FILE_REFERENCE);
        assertThat(testPage.getMeasureNumberOffset()).isEqualTo(UPDATED_MEASURE_NUMBER_OFFSET);
        assertThat(testPage.getNextPage()).isEqualTo(UPDATED_NEXT_PAGE);
    }

    @Test
    void putNonExistingPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();
        page.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, page.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();
        page.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();
        page.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePageWithPatch() throws Exception {
        // Initialize the database
        pageRepository.save(page).block();

        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();

        // Update the page using partial update
        Page partialUpdatedPage = new Page();
        partialUpdatedPage.setId(page.getId());

        partialUpdatedPage.imgFileReference(UPDATED_IMG_FILE_REFERENCE).measureNumberOffset(UPDATED_MEASURE_NUMBER_OFFSET);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
        Page testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getPageNr()).isEqualTo(DEFAULT_PAGE_NR);
        assertThat(testPage.getImgFileReference()).isEqualTo(UPDATED_IMG_FILE_REFERENCE);
        assertThat(testPage.getMeasureNumberOffset()).isEqualTo(UPDATED_MEASURE_NUMBER_OFFSET);
        assertThat(testPage.getNextPage()).isEqualTo(DEFAULT_NEXT_PAGE);
    }

    @Test
    void fullUpdatePageWithPatch() throws Exception {
        // Initialize the database
        pageRepository.save(page).block();

        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();

        // Update the page using partial update
        Page partialUpdatedPage = new Page();
        partialUpdatedPage.setId(page.getId());

        partialUpdatedPage
            .pageNr(UPDATED_PAGE_NR)
            .imgFileReference(UPDATED_IMG_FILE_REFERENCE)
            .measureNumberOffset(UPDATED_MEASURE_NUMBER_OFFSET)
            .nextPage(UPDATED_NEXT_PAGE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPage.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPage))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
        Page testPage = pageList.get(pageList.size() - 1);
        assertThat(testPage.getPageNr()).isEqualTo(UPDATED_PAGE_NR);
        assertThat(testPage.getImgFileReference()).isEqualTo(UPDATED_IMG_FILE_REFERENCE);
        assertThat(testPage.getMeasureNumberOffset()).isEqualTo(UPDATED_MEASURE_NUMBER_OFFSET);
        assertThat(testPage.getNextPage()).isEqualTo(UPDATED_NEXT_PAGE);
    }

    @Test
    void patchNonExistingPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();
        page.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, page.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();
        page.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPage() throws Exception {
        int databaseSizeBeforeUpdate = pageRepository.findAll().collectList().block().size();
        page.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(page))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Page in the database
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePage() {
        // Initialize the database
        pageRepository.save(page).block();

        int databaseSizeBeforeDelete = pageRepository.findAll().collectList().block().size();

        // Delete the page
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, page.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Page> pageList = pageRepository.findAll().collectList().block();
        assertThat(pageList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
