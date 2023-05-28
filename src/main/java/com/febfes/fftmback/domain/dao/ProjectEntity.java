package com.febfes.fftmback.domain.dao;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "project")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString(callSuper = true)
public class ProjectEntity extends BaseEntity {

    public static final String ENTITY_NAME = "Project";

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private List<TaskColumnEntity> taskColumnEntityList;
    //TODO problem when project was deleted

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "project_id")
    @ToString.Exclude
    private List<TaskEntity> taskEntityList;
    //TODO problem when project was deleted

    @Column(name = "owner_id")
    private Long ownerId;

    @Transient
    private Boolean isFavourite;

    @ManyToMany(mappedBy = "projects")
    @JsonIgnoreProperties(value = "projects")
    @ToString.Exclude
    private Set<UserEntity> members = new HashSet<>();

    public void addMember(UserEntity member) {
        this.members.add(member);
        member.getProjects().add(this);
    }

    public void removeMember(Long memberId) {
        Optional<UserEntity> member = this.members.stream()
                .filter(m -> m.getId().equals(memberId))
                .findFirst();
        if (member.isPresent()) {
            this.members.remove(member.get());
            member.get().getProjects().remove(this);
        }
    }
}
