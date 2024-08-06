package org.practice.grpcmaven.models.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;
import protobuf.clothes.Payment;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Table("clothes")
@Builder
@Entity
public class Clothes {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    private Payment paymentMethod;

    @Column(nullable = false)
    private String isbn;

}
