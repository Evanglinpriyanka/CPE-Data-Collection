package com.example.securin.services;

import com.example.securin.models.CpeEntry;
import com.example.securin.repositories.CpeRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CpeParserService {

    @Autowired
    private CpeRepository cpeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional // Ensures database consistency during the long parse
    public void parseAndSaveXml(String fileName) {
        try {
            // 1. Locate the file in src/main/resources
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream inputStream = resource.getInputStream();

            // 2. Setup the Parser with Security Fixes for Large Files
            XMLInputFactory factory = XMLInputFactory.newInstance();
            // This line specifically fixes the "jdk.xml.maxGeneralEntitySizeLimit" error
            factory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
            factory.setProperty("javax.xml.stream.supportDTD", false);

            XMLStreamReader reader = factory.createXMLStreamReader(inputStream);

            CpeEntry currentEntry = null;
            List<String> currentLinks = null;
            int count = 0;

            System.out.println(">>> Parsing started... this may take a while for large datasets.");

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String tagName = reader.getLocalName();

                    if ("cpe-item".equals(tagName)) {
                        currentEntry = new CpeEntry();
                        currentLinks = new ArrayList<>();

                        // Extract CPE 2.2 URI
                        currentEntry.setCpe22Uri(reader.getAttributeValue(null, "name"));

                        // Extract Deprecation Date
                        String dep22 = reader.getAttributeValue(null, "deprecation_date");
                        if (dep22 != null && dep22.length() >= 10) {
                            currentEntry.setCpe22DeprecationDate(LocalDate.parse(dep22.substring(0, 10)));
                        }

                    } else if ("title".equals(tagName) && currentEntry != null) {
                        currentEntry.setCpeTitle(reader.getElementText());

                    } else if ("cpe23-item".equals(tagName) && currentEntry != null) {
                        currentEntry.setCpe23Uri(reader.getAttributeValue(null, "name"));

                    } else if ("reference".equals(tagName) && currentEntry != null) {
                        String link = reader.getAttributeValue(null, "href");
                        if (link != null) currentLinks.add(link);
                    }
                }

                if (event == XMLStreamConstants.END_ELEMENT && "cpe-item".equals(reader.getLocalName())) {
                    if (currentEntry != null) {
                        currentEntry.setReferenceLinks(currentLinks);
                        cpeRepository.save(currentEntry);
                        count++;

                        // Progress Tracker & Memory Management
                        if (count % 1000 == 0) {
                            System.out.println(">>> Processed " + count + " entries...");
                            // This clears the "memory cache" so your RAM doesn't fill up
                            entityManager.flush();
                            entityManager.clear();
                        }
                    }
                }
            }
            System.out.println(">>> Successfully saved " + count + " total entries to the database!");

        } catch (java.io.FileNotFoundException e) {
            System.err.println(">>> ERROR: File '" + fileName + "' not found in src/main/resources.");
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            System.err.println(">>> ERROR: Data too long! Ensure @Column(length=2000) is in CpeEntry.java");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println(">>> ERROR: An unexpected error occurred during parsing.");
            e.printStackTrace();
        }
    }
}