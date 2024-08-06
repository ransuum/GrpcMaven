package org.practice.grpcmaven.repo;

import org.practice.grpcmaven.models.entity.Clothes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClothesRepo extends JpaRepository<Clothes, String> {
    Optional<Clothes> findByIsbn(String isbn);
}
