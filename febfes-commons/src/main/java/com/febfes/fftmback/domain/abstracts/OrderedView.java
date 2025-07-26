package com.febfes.fftmback.domain.abstracts;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public abstract class OrderedView extends BaseView {

    @Column(name = "\"entityOrder\"")
    private Integer entityOrder;
}
