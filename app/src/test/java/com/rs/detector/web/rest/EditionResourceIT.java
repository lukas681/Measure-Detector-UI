package com.rs.detector.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.rs.detector.IntegrationTest;
import com.rs.detector.domain.Edition;
import com.rs.detector.domain.enumeration.EditionType;
import com.rs.detector.repository.EditionRepository;
import com.rs.detector.service.EntityManager;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link EditionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class EditionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final EditionType DEFAULT_TYPE = EditionType.SCORE;
    private static final EditionType UPDATED_TYPE = EditionType.SINGLEVOICE;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/editions";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EditionRepository editionRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Edition edition;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Edition createEntity(EntityManager em) {
        Edition edition = new Edition()
            .title(DEFAULT_TITLE)
            .createdDate(DEFAULT_CREATED_DATE)
            .type(DEFAULT_TYPE)
            .description(DEFAULT_DESCRIPTION);
        return edition;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Edition createUpdatedEntity(EntityManager em) {
        Edition edition = new Edition()
            .title(UPDATED_TITLE)
            .createdDate(UPDATED_CREATED_DATE)
            .type(UPDATED_TYPE)
            .description(UPDATED_DESCRIPTION);
        return edition;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Edition.class).block();
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
        edition = createEntity(em);
    }

    @Test
    void createEdition() throws Exception {
        int databaseSizeBeforeCreate = editionRepository.findAll().collectList().block().size();
        // Create the Edition
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeCreate + 1);
        Edition testEdition = editionList.get(editionList.size() - 1);
        assertThat(testEdition.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEdition.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testEdition.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testEdition.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void createEditionWithExistingId() throws Exception {
        // Create the Edition with an existing ID
        edition.setId(1L);

        int databaseSizeBeforeCreate = editionRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = editionRepository.findAll().collectList().block().size();
        // set the field null
        edition.setTitle(null);

        // Create the Edition, which fails.

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isBadRequest();

        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    void getAllEditions() {
        // Initialize the database
        editionRepository.save(edition).block();

        // Get all the editionList
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
            .value(hasItem(edition.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION));
    }

    @Test
    void getEdition() {
        // Initialize the database
        editionRepository.save(edition).block();

        // Get the edition
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, edition.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(edition.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION));
    }

    @Test
    void getNonExistingEdition() {
        // Get the edition
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEdition() throws Exception {
        // Initialize the database
        editionRepository.save(edition).block();

        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();

        // Update the edition
        Edition updatedEdition = editionRepository.findById(edition.getId()).block();
        updatedEdition.title(UPDATED_TITLE).createdDate(UPDATED_CREATED_DATE).type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEdition.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEdition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
        Edition testEdition = editionList.get(editionList.size() - 1);
        assertThat(testEdition.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEdition.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testEdition.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testEdition.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void putNonExistingEdition() throws Exception {
        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();
        edition.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, edition.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEdition() throws Exception {
        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();
        edition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEdition() throws Exception {
        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();
        edition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEditionWithPatch() throws Exception {
        // Initialize the database
        editionRepository.save(edition).block();

        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();

        // Update the edition using partial update
        Edition partialUpdatedEdition = new Edition();
        partialUpdatedEdition.setId(edition.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEdition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEdition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
        Edition testEdition = editionList.get(editionList.size() - 1);
        assertThat(testEdition.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testEdition.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testEdition.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testEdition.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    void fullUpdateEditionWithPatch() throws Exception {
        // Initialize the database
        editionRepository.save(edition).block();

        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();

        // Update the edition using partial update
        Edition partialUpdatedEdition = new Edition();
        partialUpdatedEdition.setId(edition.getId());

        partialUpdatedEdition.title(UPDATED_TITLE).createdDate(UPDATED_CREATED_DATE).type(UPDATED_TYPE).description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEdition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEdition))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
        Edition testEdition = editionList.get(editionList.size() - 1);
        assertThat(testEdition.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testEdition.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testEdition.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testEdition.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    void patchNonExistingEdition() throws Exception {
        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();
        edition.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, edition.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEdition() throws Exception {
        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();
        edition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEdition() throws Exception {
        int databaseSizeBeforeUpdate = editionRepository.findAll().collectList().block().size();
        edition.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(edition))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Edition in the database
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEdition() {
        // Initialize the database
        editionRepository.save(edition).block();

        int databaseSizeBeforeDelete = editionRepository.findAll().collectList().block().size();

        // Delete the edition
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, edition.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Edition> editionList = editionRepository.findAll().collectList().block();
        assertThat(editionList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
