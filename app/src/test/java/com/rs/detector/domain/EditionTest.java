package com.rs.detector.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.rs.detector.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EditionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Edition.class);
        Edition edition1 = new Edition();
        edition1.setId(1L);
        Edition edition2 = new Edition();
        edition2.setId(edition1.getId());
        assertThat(edition1).isEqualTo(edition2);
        edition2.setId(2L);
        assertThat(edition1).isNotEqualTo(edition2);
        edition1.setId(null);
        assertThat(edition1).isNotEqualTo(edition2);
    }
}
