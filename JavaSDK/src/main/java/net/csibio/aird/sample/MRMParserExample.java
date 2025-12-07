package net.csibio.aird.sample;

import net.csibio.aird.bean.common.MrmPair;
import net.csibio.aird.parser.MRMParser;

import java.util.List;

/**
 * MRM Parser Example
 * 
 * This example demonstrates the usage of MRMParser for processing MRM chromatography data.
 */
public class MRMParserExample {
    
    public static void main(String[] args) {
        // Replace with your actual Aird index file path
        String indexFilePath = "path/to/your/mrm_index.json";
        
        try {
            // 1. Create MRMParser instance
            MRMParser parser = new MRMParser(indexFilePath);
            
            // 2. Get chromatogram index information
            var chromIndex = parser.getChromatogramIndex();
            if (chromIndex != null) {
                System.out.println("Chromatogram Information:");
                System.out.println("Number of transitions: " + chromIndex.getIds().size());
                System.out.println("Data start position: " + chromIndex.getStartPtr());
                System.out.println("Data end position: " + chromIndex.getEndPtr());
            }
            
            // 3. Get all MRM ion pairs
            System.out.println("\nGetting all MRM ion pairs...");
            List<MrmPair> mrmPairs = parser.getAllMrmPairs();
            
            if (mrmPairs != null && !mrmPairs.isEmpty()) {
                System.out.println("Found " + mrmPairs.size() + " MRM ion pairs");
                
                // 4. Process and display MRM pairs
                System.out.println("\nMRM Ion Pairs:");
                for (int i = 0; i < Math.min(5, mrmPairs.size()); i++) { // Show first 5 pairs
                    MrmPair pair = mrmPairs.get(i);
                    
                    System.out.println("Pair " + (i + 1) + ":");
                    System.out.println("  ID: " + pair.getId());
                    System.out.println("  Precursor m/z: " + pair.getPrecursor().getMz());
                    System.out.println("  Product m/z: " + pair.getProduct().getMz());
                    System.out.println("  Polarity: " + pair.getPolarity());
                    
                    // Display chromatogram data
                    double[] rts = pair.getRts();
                    double[] intensities = pair.getInts();
                    
                    if (rts != null && intensities != null && rts.length > 0) {
                        System.out.println("  Chromatogram points: " + rts.length);
                        
                        // Show first few data points
                        System.out.println("  First 3 data points:");
                        for (int j = 0; j < Math.min(3, rts.length); j++) {
                            System.out.println("    RT: " + String.format("%.2f", rts[j]) + 
                                             ", Intensity: " + String.format("%.2f", intensities[j]));
                        }
                        
                        // Calculate and display peak information
                        double maxIntensity = 0;
                        double maxRt = 0;
                        double totalArea = 0;
                        
                        for (int j = 0; j < rts.length; j++) {
                            if (intensities[j] > maxIntensity) {
                                maxIntensity = intensities[j];
                                maxRt = rts[j];
                            }
                            
                            // Simple trapezoidal integration for area calculation
                            if (j > 0) {
                                double deltaRt = rts[j] - rts[j-1];
                                double avgIntensity = (intensities[j] + intensities[j-1]) / 2.0;
                                totalArea += deltaRt * avgIntensity;
                            }
                        }
                        
                        System.out.println("  Peak Information:");
                        System.out.println("    Max Intensity: " + String.format("%.2f", maxIntensity));
                        System.out.println("    Peak RT: " + String.format("%.2f", maxRt));
                        System.out.println("    Peak Area: " + String.format("%.2f", totalArea));
                    }
                    System.out.println();
                }
                
                // 5. Example: Find specific transition by precursor and product m/z
                System.out.println("\nSearching for specific transition (precursor 445.3, product 366.2)...");
                MrmPair targetPair = findTransitionByMz(mrmPairs, 445.3, 366.2);
                if (targetPair != null) {
                    System.out.println("Found target transition: " + targetPair.getId());
                } else {
                    System.out.println("Target transition not found.");
                }
                
            } else {
                System.out.println("No MRM ion pairs found in the file.");
            }
            
            // 6. Close the parser to release resources
            parser.close();
            System.out.println("\nParser closed successfully.");
            
        } catch (Exception e) {
            System.err.println("Error processing MRM file: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to find MRM transition by precursor and product m/z values
     */
    private static MrmPair findTransitionByMz(List<MrmPair> pairs, double precursorMz, double productMz) {
        double tolerance = 0.1; // m/z tolerance
        
        for (MrmPair pair : pairs) {
            if (Math.abs(pair.getPrecursor().getMz() - precursorMz) < tolerance &&
                Math.abs(pair.getProduct().getMz() - productMz) < tolerance) {
                return pair;
            }
        }
        return null;
    }
}