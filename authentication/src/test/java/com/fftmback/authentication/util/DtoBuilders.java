package com.fftmback.authentication.util;

import com.fftmback.authentication.domain.UserEntity;
import lombok.experimental.UtilityClass;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.springframework.context.annotation.Profile;

import java.util.Collections;

import static org.instancio.Select.field;

@UtilityClass
@Profile("test")
public class DtoBuilders {

    public static final String PASSWORD = "password";
    public static final String EMAIL_PATTERN = "#a#a#a#a#a#a@example.com";

    public static UserEntity createUser() {
        return commonUser().create();
    }

    public static UserEntity createUser(String displayName) {
        return commonUser()
                .set(field(UserEntity::getDisplayName), displayName)
                .create();
    }

    private static InstancioApi<UserEntity> commonUser() {
        return Instancio.of(UserEntity.class)
                .generate(field(UserEntity::getEmail), gen -> gen.text().pattern(EMAIL_PATTERN))
                .set(field(UserEntity::getProjectRoles), Collections.emptyMap())
                .set(field(UserEntity::getEncryptedPassword), PASSWORD);
    }
}
