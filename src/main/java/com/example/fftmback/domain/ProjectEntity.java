package com.example.fftmback.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "projects")
@Getter
@Setter
public class ProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "create_date")
    private Date createDate;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;


}
