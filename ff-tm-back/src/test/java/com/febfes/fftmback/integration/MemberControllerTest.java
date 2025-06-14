package com.febfes.fftmback.integration;

import com.febfes.fftmback.domain.projection.ProjectProjection;
import com.febfes.fftmback.dto.MemberDto;
import com.febfes.fftmback.mapper.UserMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import lombok.NonNull;
import org.apache.commons.compress.utils.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Optional;

import static com.febfes.fftmback.integration.ProjectControllerTest.PATH_TO_PROJECTS_API;

class MemberControllerTest extends BasicTestClass {

    @Autowired
    private TransactionTemplate txTemplate;
    @Autowired
    private UserMapper userMapper;

    @Test
    void successfulAddNewMembersTest() {
        Long secondCreatedUserId = createNewUser();
        Long thirdCreatedUserId = createNewUser();
        Long createdProjectId = createNewProject();
        addMembers(List.of(secondCreatedUserId, thirdCreatedUserId), createdProjectId);

        // it's to avoid org.hibernate.LazyInitializationException
        txTemplate.execute(new TransactionCallbackWithoutResult() {

            @Override
            protected void doInTransactionWithoutResult(@NonNull TransactionStatus status) {
                List<MemberDto> members = userMapper.memberProjectionToMemberDto(
                        userService.getProjectMembersWithRole(createdProjectId)
                );
                // as owner is also a member
                Assertions.assertThat(members).hasSize(3);
                List<ProjectProjection> secondUserProjects =
                        projectMemberService.getProjectsForUser(secondCreatedUserId, Lists.newArrayList());
                Assertions.assertThat(secondUserProjects).hasSize(1);
                List<ProjectProjection> thirdUserProjects =
                        projectMemberService.getProjectsForUser(thirdCreatedUserId, Lists.newArrayList());
                Assertions.assertThat(thirdUserProjects).hasSize(1);
            }
        });
    }

    @Test
    void successfulRemoveMemberTest() {
        Long secondCreatedUserId = createNewUser();
        Long createdProjectId = createNewProject();
        addMembers(List.of(secondCreatedUserId), createdProjectId);
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .delete("%s/{id}/members/{memberId}".formatted(PATH_TO_PROJECTS_API), createdProjectId, secondCreatedUserId)
                .then()
                .statusCode(HttpStatus.SC_OK);

        List<MemberDto> members = userMapper.memberProjectionToMemberDto(
                userService.getProjectMembersWithRole(createdProjectId)
        );
        Assertions.assertThat(members).hasSize(1);
        List<ProjectProjection> secondMemberProjects = projectMemberService.getProjectsForUser(secondCreatedUserId, Lists.newArrayList());
        Assertions.assertThat(secondMemberProjects).isEmpty();
    }

    @Test
    void successfulGetMembersTest() {
        Long secondCreatedUserId = createNewUser();
        Long createdProjectId = createNewProject();
        addMembers(List.of(secondCreatedUserId), createdProjectId);
        List<MemberDto> projectMembers = requestWithBearerToken()
                .contentType(ContentType.JSON)
                .when()
                .get("%s/{id}/members".formatted(PATH_TO_PROJECTS_API), createdProjectId)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .response()
                .as(new TypeRef<>() {
                });
        Assertions.assertThat(projectMembers).hasSize(2);
        Optional<MemberDto> projectMember = projectMembers.stream().findFirst();
        Assertions.assertThat(projectMember)
                .isNotEmpty();
        Assertions.assertThat(projectMember.get().roleOnProject()).isNotNull();
    }

    private void addMembers(List<Long> memberIds, Long projectId) {
        requestWithBearerToken()
                .contentType(ContentType.JSON)
                .body(memberIds)
                .when()
                .post("%s/{id}/members".formatted(PATH_TO_PROJECTS_API), projectId)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }
}
