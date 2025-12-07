package com.lonework.corners.files.model;

import com.lonework.corners.common.model.EntityStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.StringJoiner;


@Entity
@Table(name = "cornerfile")
public class CornerFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String url;

    @Column
    private String createdBy;

    @Column
    @Enumerated(EnumType.STRING)
    private EntityStatus entityStatus;

    @Column
    private ZonedDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public EntityStatus getEntityStatus() {
        return entityStatus;
    }

    public void setEntityStatus(EntityStatus entityStatus) {
        this.entityStatus = entityStatus;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(ZonedDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CornerFile file)) {return false;}
        return Objects.equals(id, file.id) && Objects.equals(name, file.name) && Objects.equals(url, file.url) && Objects.equals(createdBy,
                file.createdBy) && entityStatus == file.entityStatus && Objects.equals(createdAt, file.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, url, createdBy, entityStatus, createdAt);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CornerFile.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("name='" + name + "'")
                .add("url='" + url + "'")
                .add("createdBy='" + createdBy + "'")
                .add("entityStatus=" + entityStatus)
                .add("createdAt=" + createdAt)
                .toString();
    }
}
