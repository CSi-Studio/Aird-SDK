package net.csibio.aird.examples;

import net.csibio.aird.bean.DDAMs;
import net.csibio.aird.bean.common.Spectrum;
import net.csibio.aird.parser.DDAParser;

import java.util.List;

/**
 * Basic DDA Parser Example
 * 
 * This example demonstrates the basic usage of DDAParser for reading DDA mass spectrometry data.
 */
public class BasicDDAParserExample {
    
    public static void main(String[] args) {
        // Replace with your actual Aird index file path
        String indexFilePath = "path/to/your/dda_index.json";
        
        try {
            // 1. Create DDAParser instance
            DDAParser parser = new DDAParser(indexFilePath);
            
            // 2. Get basic file information
            System.out.println("File Information:");
            System.out.println("Total spectra count: " + parser.getAirdInfo().getTotalCount());
            System.out.println("MS1 spectra count: " + parser.getAirdInfo().getMs1Count());
            System.out.println("MS2 spectra count: " + parser.getAirdInfo().getMs2Count());
            
            // 3. Read all data into memory
            System.out.println("\nReading all data into memory...");
            List<DDAMs> allSpectra = parser.readAllToMemory();
            
            // 4. Process and display data
            System.out.println("\nProcessing spectra data:");
            int ms1Count = 0;
            int ms2Count = 0;
            
            for (DDAMs spectrum : allSpectra) {
                if (spectrum.getMsLevel() == 1) {
                    ms1Count++;
                    System.out.println("MS1 - RT: " + spectrum.getRt() + 
                                     ", Points: " + spectrum.getMzArray().length);
                    
                    // Check if this MS1 has associated MS2 spectra
                    if (spectrum.getMs2List() != null && !spectrum.getMs2List().isEmpty()) {
                        ms2Count += spectrum.getMs2List().size();
                        System.out.println("  Has " + spectrum.getMs2List().size() + " MS2 spectra");
                    }
                }
            }
            
            System.out.println("\nSummary:");
            System.out.println("Total MS1 spectra: " + ms1Count);
            System.out.println("Total MS2 spectra: " + ms2Count);
            
            // 5. Example: Get MS1 spectra map
            System.out.println("\nGetting MS1 spectra map...");
            var ms1Map = parser.getMs1SpectraMap();
            System.out.println("MS1 spectra count in map: " + ms1Map.size());
            
            // 6. Example: Query by retention time range
            System.out.println("\nQuerying spectra in RT range 10.0-20.0 minutes...");
            List<DDAMs> spectraInRange = parser.getSpectraByRtRange(10.0, 20.0, true);
            if (spectraInRange != null) {
                System.out.println("Found " + spectraInRange.size() + " spectra in range");
            }
            
            // 7. Close the parser to release resources
            parser.close();
            System.out.println("\nParser closed successfully.");
            
        } catch (Exception e) {
            System.err.println("Error processing Aird file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}