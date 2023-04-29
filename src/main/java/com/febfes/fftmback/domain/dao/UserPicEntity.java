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
@Table(name = "user_pic")
public class UserPicEntity extends AppEntity {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_urn")
    private String fileUrn;

}
