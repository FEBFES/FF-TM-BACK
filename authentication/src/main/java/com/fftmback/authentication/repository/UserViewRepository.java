package com.fftmback.authentication.repository;

import com.fftmback.authentication.domain.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserViewRepository extends JpaRepository<UserView, Long>, JpaSpecificationExecutor<UserView> {
}
