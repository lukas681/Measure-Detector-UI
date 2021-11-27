package com.rs.detector.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rs.detector.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MeasureBoxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(MeasureBox.class);
        MeasureBox measureBox1 = new MeasureBox();
        measureBox1.setId(1L);
        MeasureBox measureBox2 = new MeasureBox();
        measureBox2.setId(measureBox1.getId());
        assertThat(measureBox1).isEqualTo(measureBox2);
        measureBox2.setId(2L);
        assertThat(measureBox1).isNotEqualTo(measureBox2);
        measureBox1.setId(null);
        assertThat(measureBox1).isNotEqualTo(measureBox2);
    }
}
