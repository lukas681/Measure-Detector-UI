package com.rs.detector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rs.detector.domain.enumeration.EditionType;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Edition.
 */
@Table("edition")
public class Edition implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Pattern(regexp = "^[A-Z-a-z\\d]+$")
    @Column("title")
    private String title;

    @Column("created_date")
    private Instant createdDate;

    @Column("type")
    private EditionType type;

    @Column("description")
    private String description;

    @Column("p_df_file_name")
    private String pDFFileName;

    @Transient
    @JsonIgnoreProperties(value = { "measureBoxes", "edition" }, allowSetters = true)
    private Set<Page> pages = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "editions" }, allowSetters = true)
    private Project project;

    @Column("project_id")
    private Long projectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Edition id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Edition title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Edition createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public EditionType getType() {
        return this.type;
    }

    public Edition type(EditionType type) {
        this.setType(type);
        return this;
    }

    public void setType(EditionType type) {
        this.type = type;
    }

    public String getDescription() {
        return this.description;
    }

    public Edition description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getpDFFileName() {
        return this.pDFFileName;
    }

    public Edition pDFFileName(String pDFFileName) {
        this.setpDFFileName(pDFFileName);
        return this;
    }

    public void setpDFFileName(String pDFFileName) {
        this.pDFFileName = pDFFileName;
    }

    public Set<Page> getPages() {
        return this.pages;
    }

    public void setPages(Set<Page> pages) {
        if (this.pages != null) {
            this.pages.forEach(i -> i.setEdition(null));
        }
        if (pages != null) {
            pages.forEach(i -> i.setEdition(this));
        }
        this.pages = pages;
    }

    public Edition pages(Set<Page> pages) {
        this.setPages(pages);
        return this;
    }

    public Edition addPages(Page page) {
        this.pages.add(page);
        page.setEdition(this);
        return this;
    }

    public Edition removePages(Page page) {
        this.pages.remove(page);
        page.setEdition(null);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public Edition project(Project project) {
        this.setProject(project);
        return this;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Edition)) {
            return false;
        }
        return id != null && id.equals(((Edition) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Edition{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", type='" + getType() + "'" +
            ", description='" + getDescription() + "'" +
            ", pDFFileName='" + getpDFFileName() + "'" +
            "}";
    }
}
