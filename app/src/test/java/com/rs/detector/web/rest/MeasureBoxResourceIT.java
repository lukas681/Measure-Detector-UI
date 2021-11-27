package com.rs.detector.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.rs.detector.IntegrationTest;
import com.rs.detector.domain.MeasureBox;
import com.rs.detector.repository.MeasureBoxRepository;
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
 * Integration tests for the {@link MeasureBoxResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class MeasureBoxResourceIT {

    private static final Long DEFAULT_ULX = 1L;
    private static final Long UPDATED_ULX = 2L;

    private static final Long DEFAULT_ULY = 1L;
    private static final Long UPDATED_ULY = 2L;

    private static final Long DEFAULT_LRX = 1L;
    private static final Long UPDATED_LRX = 2L;

    private static final Long DEFAULT_LRY = 1L;
    private static final Long UPDATED_LRY = 2L;

    private static final Long DEFAULT_MEASURE_COUNT = 1L;
    private static final Long UPDATED_MEASURE_COUNT = 2L;

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/measure-boxes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MeasureBoxRepository measureBoxRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private MeasureBox measureBox;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MeasureBox createEntity(EntityManager em) {
        MeasureBox measureBox = new MeasureBox()
            .ulx(DEFAULT_ULX)
            .uly(DEFAULT_ULY)
            .lrx(DEFAULT_LRX)
            .lry(DEFAULT_LRY)
            .measureCount(DEFAULT_MEASURE_COUNT)
            .comment(DEFAULT_COMMENT);
        return measureBox;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static MeasureBox createUpdatedEntity(EntityManager em) {
        MeasureBox measureBox = new MeasureBox()
            .ulx(UPDATED_ULX)
            .uly(UPDATED_ULY)
            .lrx(UPDATED_LRX)
            .lry(UPDATED_LRY)
            .measureCount(UPDATED_MEASURE_COUNT)
            .comment(UPDATED_COMMENT);
        return measureBox;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(MeasureBox.class).block();
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
        measureBox = createEntity(em);
    }

    @Test
    void createMeasureBox() throws Exception {
        int databaseSizeBeforeCreate = measureBoxRepository.findAll().collectList().block().size();
        // Create the MeasureBox
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeCreate + 1);
        MeasureBox testMeasureBox = measureBoxList.get(measureBoxList.size() - 1);
        assertThat(testMeasureBox.getUlx()).isEqualTo(DEFAULT_ULX);
        assertThat(testMeasureBox.getUly()).isEqualTo(DEFAULT_ULY);
        assertThat(testMeasureBox.getLrx()).isEqualTo(DEFAULT_LRX);
        assertThat(testMeasureBox.getLry()).isEqualTo(DEFAULT_LRY);
        assertThat(testMeasureBox.getMeasureCount()).isEqualTo(DEFAULT_MEASURE_COUNT);
        assertThat(testMeasureBox.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    void createMeasureBoxWithExistingId() throws Exception {
        // Create the MeasureBox with an existing ID
        measureBox.setId(1L);

        int databaseSizeBeforeCreate = measureBoxRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMeasureBoxes() {
        // Initialize the database
        measureBoxRepository.save(measureBox).block();

        // Get all the measureBoxList
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
            .value(hasItem(measureBox.getId().intValue()))
            .jsonPath("$.[*].ulx")
            .value(hasItem(DEFAULT_ULX.intValue()))
            .jsonPath("$.[*].uly")
            .value(hasItem(DEFAULT_ULY.intValue()))
            .jsonPath("$.[*].lrx")
            .value(hasItem(DEFAULT_LRX.intValue()))
            .jsonPath("$.[*].lry")
            .value(hasItem(DEFAULT_LRY.intValue()))
            .jsonPath("$.[*].measureCount")
            .value(hasItem(DEFAULT_MEASURE_COUNT.intValue()))
            .jsonPath("$.[*].comment")
            .value(hasItem(DEFAULT_COMMENT));
    }

    @Test
    void getMeasureBox() {
        // Initialize the database
        measureBoxRepository.save(measureBox).block();

        // Get the measureBox
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, measureBox.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(measureBox.getId().intValue()))
            .jsonPath("$.ulx")
            .value(is(DEFAULT_ULX.intValue()))
            .jsonPath("$.uly")
            .value(is(DEFAULT_ULY.intValue()))
            .jsonPath("$.lrx")
            .value(is(DEFAULT_LRX.intValue()))
            .jsonPath("$.lry")
            .value(is(DEFAULT_LRY.intValue()))
            .jsonPath("$.measureCount")
            .value(is(DEFAULT_MEASURE_COUNT.intValue()))
            .jsonPath("$.comment")
            .value(is(DEFAULT_COMMENT));
    }

    @Test
    void getNonExistingMeasureBox() {
        // Get the measureBox
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMeasureBox() throws Exception {
        // Initialize the database
        measureBoxRepository.save(measureBox).block();

        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();

        // Update the measureBox
        MeasureBox updatedMeasureBox = measureBoxRepository.findById(measureBox.getId()).block();
        updatedMeasureBox
            .ulx(UPDATED_ULX)
            .uly(UPDATED_ULY)
            .lrx(UPDATED_LRX)
            .lry(UPDATED_LRY)
            .measureCount(UPDATED_MEASURE_COUNT)
            .comment(UPDATED_COMMENT);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMeasureBox.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMeasureBox))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
        MeasureBox testMeasureBox = measureBoxList.get(measureBoxList.size() - 1);
        assertThat(testMeasureBox.getUlx()).isEqualTo(UPDATED_ULX);
        assertThat(testMeasureBox.getUly()).isEqualTo(UPDATED_ULY);
        assertThat(testMeasureBox.getLrx()).isEqualTo(UPDATED_LRX);
        assertThat(testMeasureBox.getLry()).isEqualTo(UPDATED_LRY);
        assertThat(testMeasureBox.getMeasureCount()).isEqualTo(UPDATED_MEASURE_COUNT);
        assertThat(testMeasureBox.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void putNonExistingMeasureBox() throws Exception {
        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();
        measureBox.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, measureBox.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMeasureBox() throws Exception {
        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();
        measureBox.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMeasureBox() throws Exception {
        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();
        measureBox.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMeasureBoxWithPatch() throws Exception {
        // Initialize the database
        measureBoxRepository.save(measureBox).block();

        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();

        // Update the measureBox using partial update
        MeasureBox partialUpdatedMeasureBox = new MeasureBox();
        partialUpdatedMeasureBox.setId(measureBox.getId());

        partialUpdatedMeasureBox.lrx(UPDATED_LRX).lry(UPDATED_LRY).measureCount(UPDATED_MEASURE_COUNT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMeasureBox.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMeasureBox))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
        MeasureBox testMeasureBox = measureBoxList.get(measureBoxList.size() - 1);
        assertThat(testMeasureBox.getUlx()).isEqualTo(DEFAULT_ULX);
        assertThat(testMeasureBox.getUly()).isEqualTo(DEFAULT_ULY);
        assertThat(testMeasureBox.getLrx()).isEqualTo(UPDATED_LRX);
        assertThat(testMeasureBox.getLry()).isEqualTo(UPDATED_LRY);
        assertThat(testMeasureBox.getMeasureCount()).isEqualTo(UPDATED_MEASURE_COUNT);
        assertThat(testMeasureBox.getComment()).isEqualTo(DEFAULT_COMMENT);
    }

    @Test
    void fullUpdateMeasureBoxWithPatch() throws Exception {
        // Initialize the database
        measureBoxRepository.save(measureBox).block();

        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();

        // Update the measureBox using partial update
        MeasureBox partialUpdatedMeasureBox = new MeasureBox();
        partialUpdatedMeasureBox.setId(measureBox.getId());

        partialUpdatedMeasureBox
            .ulx(UPDATED_ULX)
            .uly(UPDATED_ULY)
            .lrx(UPDATED_LRX)
            .lry(UPDATED_LRY)
            .measureCount(UPDATED_MEASURE_COUNT)
            .comment(UPDATED_COMMENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMeasureBox.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMeasureBox))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
        MeasureBox testMeasureBox = measureBoxList.get(measureBoxList.size() - 1);
        assertThat(testMeasureBox.getUlx()).isEqualTo(UPDATED_ULX);
        assertThat(testMeasureBox.getUly()).isEqualTo(UPDATED_ULY);
        assertThat(testMeasureBox.getLrx()).isEqualTo(UPDATED_LRX);
        assertThat(testMeasureBox.getLry()).isEqualTo(UPDATED_LRY);
        assertThat(testMeasureBox.getMeasureCount()).isEqualTo(UPDATED_MEASURE_COUNT);
        assertThat(testMeasureBox.getComment()).isEqualTo(UPDATED_COMMENT);
    }

    @Test
    void patchNonExistingMeasureBox() throws Exception {
        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();
        measureBox.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, measureBox.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMeasureBox() throws Exception {
        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();
        measureBox.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMeasureBox() throws Exception {
        int databaseSizeBeforeUpdate = measureBoxRepository.findAll().collectList().block().size();
        measureBox.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(measureBox))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the MeasureBox in the database
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMeasureBox() {
        // Initialize the database
        measureBoxRepository.save(measureBox).block();

        int databaseSizeBeforeDelete = measureBoxRepository.findAll().collectList().block().size();

        // Delete the measureBox
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, measureBox.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<MeasureBox> measureBoxList = measureBoxRepository.findAll().collectList().block();
        assertThat(measureBoxList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
