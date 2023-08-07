package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.common.UserProjectId;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "user_project")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class UserProject {

    @EmbeddedId
    private UserProjectId id;
}
