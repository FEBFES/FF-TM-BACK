package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.common.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user_entity")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserEntity extends AppEntity implements UserDetails {

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

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private List<TaskEntity> taskEntityList;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "owner_id")
    @ToString.Exclude
    private List<ProjectEntity> projectEntityList;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
