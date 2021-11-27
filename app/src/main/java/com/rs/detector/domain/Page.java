package com.rs.detector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Page.
 */
@Table("page")
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("page_nr")
    private Long pageNr;

    @Column("img_file_reference")
    private String imgFileReference;

    @Column("next_page")
    private Long nextPage;

    @Transient
    @JsonIgnoreProperties(value = { "page" }, allowSetters = true)
    private Set<MeasureBox> measureBoxes = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "pages", "project" }, allowSetters = true)
    private Edition edition;

    @Column("edition_id")
    private Long editionId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Page id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPageNr() {
        return this.pageNr;
    }

    public Page pageNr(Long pageNr) {
        this.setPageNr(pageNr);
        return this;
    }

    public void setPageNr(Long pageNr) {
        this.pageNr = pageNr;
    }

    public String getImgFileReference() {
        return this.imgFileReference;
    }

    public Page imgFileReference(String imgFileReference) {
        this.setImgFileReference(imgFileReference);
        return this;
    }

    public void setImgFileReference(String imgFileReference) {
        this.imgFileReference = imgFileReference;
    }

    public Long getNextPage() {
        return this.nextPage;
    }

    public Page nextPage(Long nextPage) {
        this.setNextPage(nextPage);
        return this;
    }

    public void setNextPage(Long nextPage) {
        this.nextPage = nextPage;
    }

    public Set<MeasureBox> getMeasureBoxes() {
        return this.measureBoxes;
    }

    public void setMeasureBoxes(Set<MeasureBox> measureBoxes) {
        if (this.measureBoxes != null) {
            this.measureBoxes.forEach(i -> i.setPage(null));
        }
        if (measureBoxes != null) {
            measureBoxes.forEach(i -> i.setPage(this));
        }
        this.measureBoxes = measureBoxes;
    }

    public Page measureBoxes(Set<MeasureBox> measureBoxes) {
        this.setMeasureBoxes(measureBoxes);
        return this;
    }

    public Page addMeasureBoxes(MeasureBox measureBox) {
        this.measureBoxes.add(measureBox);
        measureBox.setPage(this);
        return this;
    }

    public Page removeMeasureBoxes(MeasureBox measureBox) {
        this.measureBoxes.remove(measureBox);
        measureBox.setPage(null);
        return this;
    }

    public Edition getEdition() {
        return this.edition;
    }

    public void setEdition(Edition edition) {
        this.edition = edition;
        this.editionId = edition != null ? edition.getId() : null;
    }

    public Page edition(Edition edition) {
        this.setEdition(edition);
        return this;
    }

    public Long getEditionId() {
        return this.editionId;
    }

    public void setEditionId(Long edition) {
        this.editionId = edition;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Page)) {
            return false;
        }
        return id != null && id.equals(((Page) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Page{" +
            "id=" + getId() +
            ", pageNr=" + getPageNr() +
            ", imgFileReference='" + getImgFileReference() + "'" +
            ", nextPage=" + getNextPage() +
            "}";
    }
}
