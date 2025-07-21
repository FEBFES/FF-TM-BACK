package com.febfes.fftmback.unit.role;

import com.febfes.fftmback.domain.RoleName;
import com.febfes.fftmback.domain.dao.RoleEntity;
import com.febfes.fftmback.exception.UserRoleChangeException;
import com.febfes.fftmback.repository.RoleRepository;
import com.febfes.fftmback.service.impl.RoleServiceImpl;
import com.febfes.fftmback.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class ChangeRoleTest extends BaseUnitTest {

    private static final Long PROJECT_ID = 1L;
    private static final Long USER_ID = 2L;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    @Test
    void changeUserRoleOnProjectFailed() {
        RoleEntity role = RoleEntity.builder()
                .id(1L)
                .name(RoleName.MEMBER_PLUS)
                .build();

        when(roleRepository.findByName(RoleName.MEMBER_PLUS)).thenReturn(Optional.of(role));
        when(roleRepository.changeUserRoleOnProject(PROJECT_ID, USER_ID, role.getName().name())).thenReturn(0);

        assertThrows(UserRoleChangeException.class,
                () -> roleService.changeUserRoleOnProject(PROJECT_ID, USER_ID, RoleName.MEMBER_PLUS));
    }
}
