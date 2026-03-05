package com.example.securin.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

    @Entity // This tells Spring Boot to create a table in the database
    @Table(name = "cpes")
    @Data   // This is from Lombok - it automatically creates Getters and Setters
    public class CpeEntry {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String cpeTitle;

        @Column(columnDefinition = "TEXT")
        private String cpe22Uri;

        @Column(columnDefinition = "TEXT")
        private String cpe23Uri;

        // We store links as a List of strings
        @ElementCollection
        @CollectionTable(name = "cpe_reference_links", joinColumns = @JoinColumn(name = "cpe_id"))
        @Column(name = "link", length = 200000)
        private List<String> referenceLinks;

        private LocalDate cpe22DeprecationDate;

        private LocalDate cpe23DeprecationDate;
    }

