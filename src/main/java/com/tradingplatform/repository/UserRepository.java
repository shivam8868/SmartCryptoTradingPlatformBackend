package com.tradingplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tradingplatform.domain.USER_ROLE;
import com.tradingplatform.modal.User;
// import java.util.List;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    long countByRole(USER_ROLE role);
}
