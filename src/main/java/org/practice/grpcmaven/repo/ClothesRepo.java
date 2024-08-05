package org.practice.grpcmaven.repo;

import org.practice.grpcmaven.models.entity.Cloth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClothesRepo extends JpaRepository<Cloth, String> {
    Optional<Cloth> findByIsbn(String isbn);
}
