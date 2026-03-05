package com.example.securin.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component // This tells Spring to manage this class
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CpeParserService cpeParserService;

    @Override
    public void run(String... args) throws Exception {

        System.out.println(">>> Waiting for database to initialize...");
        Thread.sleep(2000);
        System.setProperty("jdk.xml.maxGeneralEntitySizeLimit", "0");
        System.setProperty("jdk.xml.totalEntitySizeLimit", "0");
        System.setProperty("jdk.xml.entityExpansionLimit", "0");

        System.out.println(">>> Starting XML Data Collection...");

        // Update this path to where your actual XML file is stored!
        // Example: ""


        cpeParserService.parseAndSaveXml("official-cpe-dictionary_v2.3.xml");

        System.out.println(">>> Data Collection Complete! API is ready.");
    }
}