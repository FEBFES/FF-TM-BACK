package com.febfes.fftmback.domain.dao;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.*;

@Entity
@Table(name = "user_entity")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserEntity extends BaseEntity implements UserDetails {

    public static final String ENTITY_NAME = "User";

    @Serial
    private static final long serialVersionUID = 7365026142938847634L;

    @Column(name = "email")
    private String email;

    @Column(name = "username")
    private String username;

    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "display_name")
    private String displayName;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private List<TaskEntity> taskEntityList;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private List<ProjectEntity> projectEntityList;

    @ManyToMany(fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.PERSIST,
                    CascadeType.MERGE
            })
    @JoinTable(name = "user_project",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "project_id")})
    @ToString.Exclude
    @Builder.Default
    private Set<ProjectEntity> projects = new HashSet<>();

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinTable(name = "project_user_role",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id", referencedColumnName = "id")})
    @MapKeyJoinColumn(name = "project_id")
    @ToString.Exclude
    @Builder.Default
    private Map<ProjectEntity, RoleEntity> projectRoles = new HashMap<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new HashSet<>();
    }

    @Override
    public String getPassword() {
        return encryptedPassword;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
