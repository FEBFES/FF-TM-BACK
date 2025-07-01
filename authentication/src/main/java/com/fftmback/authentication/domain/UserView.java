package com.fftmback.authentication.domain;

import com.febfes.fftmback.domain.abstracts.BaseView;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;

@Entity
@Table(name = "v_user")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class UserView extends BaseView {

    @Column(name = "\"email\"")
    private String email;

    @Column(name = "\"username\"")
    private String username;

    @Column(name = "\"encryptedPassword\"")
    private String encryptedPassword;

    @Column(name = "\"firstName\"")
    private String firstName;

    @Column(name = "\"lastName\"")
    private String lastName;

    @Column(name = "\"displayName\"")
    private String displayName;

    @Column(name = "\"userPicId\"")
    private Long userPicId;

    @Column(name = "\"userPicUrn\"")
    private String userPicUrn;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        UserView userView = (UserView) o;
        return getId() != null && Objects.equals(getId(), userView.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy proxy ? proxy.getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
