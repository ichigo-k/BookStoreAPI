package com.kephas.bookstoreapi.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="categories")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;


    private String description="";

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    private List<Book> books = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
