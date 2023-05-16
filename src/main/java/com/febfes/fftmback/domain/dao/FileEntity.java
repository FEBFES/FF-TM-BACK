package com.febfes.fftmback.domain.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "file_entity")
public class FileEntity extends BaseEntity {

    public static final String ENTITY_NAME = "File";

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "name")
    private String name;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_urn")
    private String fileUrn;
}
