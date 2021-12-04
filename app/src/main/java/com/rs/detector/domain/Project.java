package com.rs.detector.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Project.
 */
@Table("project")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("name")
    private String name;

    @Column("composer")
    private String composer;

    @Column("created_date")
    private Instant createdDate;

    @Transient
    @JsonIgnoreProperties(value = { "pages", "project" }, allowSetters = true)
//    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    private Set<Edition> editions = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Project id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Project name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComposer() {
        return this.composer;
    }

    public Project composer(String composer) {
        this.setComposer(composer);
        return this;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public Project createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Set<Edition> getEditions() {
        return this.editions;
    }

    public void setEditions(Set<Edition> editions) {
        if (this.editions != null) {
            this.editions.forEach(i -> i.setProject(null));
        }
        if (editions != null) {
            editions.forEach(i -> i.setProject(this));
        }
        this.editions = editions;
    }

    public Project editions(Set<Edition> editions) {
        this.setEditions(editions);
        return this;
    }

    public Project addEditions(Edition edition) {
        this.editions.add(edition);
        edition.setProject(this);
        return this;
    }

    public Project removeEditions(Edition edition) {
        this.editions.remove(edition);
        edition.setProject(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return id != null && id.equals(((Project) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", composer='" + getComposer() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            "}";
    }
}
