package org.practice.grpcmaven.repo;

import org.practice.grpcmaven.models.entity.Orders;
import org.practice.grpcmaven.models.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdersRepo extends JpaRepository<Orders, Long> {
    Optional<Orders> findByUser(Users user);
}
