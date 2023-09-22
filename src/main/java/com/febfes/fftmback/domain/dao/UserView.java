package com.febfes.fftmback.domain.dao;

import com.febfes.fftmback.domain.dao.abstracts.BaseView;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "v_user")
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
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

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "\"userPicId\"", referencedColumnName = "id")
    private FileEntity userPic;

}
