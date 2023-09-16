package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.common.EntityType;
import com.febfes.fftmback.domain.dao.abstracts.BaseEntity;
import jakarta.persistence.*;
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
    @Enumerated(EnumType.STRING)
    private EntityType entityType;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_urn")
    private String fileUrn;
}
