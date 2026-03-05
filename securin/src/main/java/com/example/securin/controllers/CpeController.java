package com.example.securin.controllers;

import com.example.securin.models.CpeEntry;
import com.example.securin.repositories.CpeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

    @RestController // Tells Spring this class handles API requests
    @RequestMapping("/api/cpes") // Sets the base URL to /api/cpes
    public class CpeController {

        @Autowired
        private CpeRepository cpeRepository;

        // 1. Get All CPEs with Pagination
        // URL: /api/cpes?page=0&limit=10
        @GetMapping
        public Page<CpeEntry> getAllCpes(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int limit) {

            return cpeRepository.findAll(PageRequest.of(page, limit));
        }

        // 2. Search CPEs
        // URL: /api/cpes/search?cpeTitle=example&deprecationDate=2024-01-01
        @GetMapping("/search")
        public List<CpeEntry> searchCpes(
                @RequestParam(required = false) String cpeTitle,
                @RequestParam(required = false) String deprecationDate) {

            if (cpeTitle != null) {
                return cpeRepository.findByCpeTitleContainingIgnoreCase(cpeTitle);
            }

            if (deprecationDate != null) {
                LocalDate date = LocalDate.parse(deprecationDate);
                return cpeRepository.findByCpe22DeprecationDateBeforeOrCpe23DeprecationDateBefore(date, date);
            }

            return cpeRepository.findAll();
        }
    }

