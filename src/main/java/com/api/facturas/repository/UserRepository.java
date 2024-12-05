package com.api.facturas.repository;

import com.api.facturas.pojo.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(@Param(("email")) String email);
}
