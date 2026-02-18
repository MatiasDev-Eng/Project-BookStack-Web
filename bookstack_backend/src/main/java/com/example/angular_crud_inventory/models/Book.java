package com.example.angular_crud_inventory.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    //publisher id (who published it)

    // name
    @NotBlank
    @Column(name = "title")
    private String title;

    // author
    @NotBlank
    @Column(name = "author")
    private String author;

    // price
    @NotBlank
    @Min(0)
    @Max(999)
    @Column(name = "price", precision=2)
    private BigDecimal price;

    // Rating
    @NotBlank
    @Min(0)
    @Max(5)
    @Column(name = "rating")
    private Float rating;

    // condition

    // Year
    @NotBlank
    @Min(0)
    @Max(2026)
    private Integer year;

    // TODO: add condition enum, add status enum (pending until admin decides), add owner_id
}
