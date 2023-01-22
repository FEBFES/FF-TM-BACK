package com.example.fftmback.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "columns")
@Getter
@Setter
public class ColumnEntity extends AppEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "\"projectId\"")
    private Long projectId;

    @Column(name = "description")
    private String description;

    @Column(name = "\"columnOrder\"")
    private Integer columnOrder;

}
