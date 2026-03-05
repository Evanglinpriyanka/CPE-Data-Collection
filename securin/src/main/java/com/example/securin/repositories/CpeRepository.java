package com.example.securin.repositories;

import com.example.securin.models.CpeEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

    @Repository
    public interface CpeRepository extends JpaRepository<CpeEntry, Long> {

        // Spring generates the logic for these based on the method names!

        // 1. Search by Title (ignores case, looks for partial matches)
        List<CpeEntry> findByCpeTitleContainingIgnoreCase(String title);

        // 2. Search by CPE 2.2 URI
        List<CpeEntry> findByCpe22Uri(String uri);

        // 3. Search by CPE 2.3 URI
        List<CpeEntry> findByCpe23Uri(String uri);

        // 4. Find anything deprecated BEFORE a certain date (Requirement #4)
        List<CpeEntry> findByCpe22DeprecationDateBeforeOrCpe23DeprecationDateBefore(LocalDate date1, LocalDate date2);
    }

