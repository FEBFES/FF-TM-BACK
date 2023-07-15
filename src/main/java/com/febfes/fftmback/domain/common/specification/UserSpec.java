package com.febfes.fftmback.domain.common.specification;

import com.febfes.fftmback.domain.dao.UserEntity;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;


@And({
        @Spec(path = "id", params = "id", spec = Equal.class),
        @Spec(path = "email", params = "email", spec = LikeIgnoreCase.class),
        @Spec(path = "username", params = "username", spec = LikeIgnoreCase.class),
        @Spec(path = "firstName", params = "firstName", spec = LikeIgnoreCase.class),
        @Spec(path = "lastName", params = "lastName", spec = LikeIgnoreCase.class),
        @Spec(path = "displayName", params = "displayName", spec = LikeIgnoreCase.class)
})
public interface UserSpec extends Specification<UserEntity> {
}
