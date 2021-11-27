package com.rs.detector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MeasureBox.
 */
@Table("measure_box")
public class MeasureBox implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("ulx")
    private Long ulx;

    @Column("uly")
    private Long uly;

    @Column("lrx")
    private Long lrx;

    @Column("lry")
    private Long lry;

    @Column("measure_count")
    private Long measureCount;

    @Column("comment")
    private String comment;

    @Transient
    @JsonIgnoreProperties(value = { "measureBoxes", "edition" }, allowSetters = true)
    private Page page;

    @Column("page_id")
    private Long pageId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MeasureBox id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUlx() {
        return this.ulx;
    }

    public MeasureBox ulx(Long ulx) {
        this.setUlx(ulx);
        return this;
    }

    public void setUlx(Long ulx) {
        this.ulx = ulx;
    }

    public Long getUly() {
        return this.uly;
    }

    public MeasureBox uly(Long uly) {
        this.setUly(uly);
        return this;
    }

    public void setUly(Long uly) {
        this.uly = uly;
    }

    public Long getLrx() {
        return this.lrx;
    }

    public MeasureBox lrx(Long lrx) {
        this.setLrx(lrx);
        return this;
    }

    public void setLrx(Long lrx) {
        this.lrx = lrx;
    }

    public Long getLry() {
        return this.lry;
    }

    public MeasureBox lry(Long lry) {
        this.setLry(lry);
        return this;
    }

    public void setLry(Long lry) {
        this.lry = lry;
    }

    public Long getMeasureCount() {
        return this.measureCount;
    }

    public MeasureBox measureCount(Long measureCount) {
        this.setMeasureCount(measureCount);
        return this;
    }

    public void setMeasureCount(Long measureCount) {
        this.measureCount = measureCount;
    }

    public String getComment() {
        return this.comment;
    }

    public MeasureBox comment(String comment) {
        this.setComment(comment);
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Page getPage() {
        return this.page;
    }

    public void setPage(Page page) {
        this.page = page;
        this.pageId = page != null ? page.getId() : null;
    }

    public MeasureBox page(Page page) {
        this.setPage(page);
        return this;
    }

    public Long getPageId() {
        return this.pageId;
    }

    public void setPageId(Long page) {
        this.pageId = page;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MeasureBox)) {
            return false;
        }
        return id != null && id.equals(((MeasureBox) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MeasureBox{" +
            "id=" + getId() +
            ", ulx=" + getUlx() +
            ", uly=" + getUly() +
            ", lrx=" + getLrx() +
            ", lry=" + getLry() +
            ", measureCount=" + getMeasureCount() +
            ", comment='" + getComment() + "'" +
            "}";
    }
}
