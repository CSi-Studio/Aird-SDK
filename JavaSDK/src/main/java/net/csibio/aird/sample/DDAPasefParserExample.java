package net.csibio.aird.sample;

import net.csibio.aird.bean.DDAPasefMs;
import net.csibio.aird.parser.DDAPasefParser;

import java.util.List;

/**
 * DDA-PASEF Parser Example
 * <p>
 * This example demonstrates the usage of DDAPasefParser for processing DDA-PASEF data
 * with ion mobility information.
 */
public class DDAPasefParserExample {

    public static void main(String[] args) {
        // Replace with your actual Aird index file path
        String indexFilePath = "path/to/your/dda_pasef_index.json";

        try {
            // 1. Create DDAPasefParser instance
            DDAPasefParser parser = new DDAPasefParser(indexFilePath);

            // 2. Get basic file information
            System.out.println("DDA-PASEF File Information:");
            System.out.println("Total spectra count: " + parser.getAirdInfo().getTotalCount());
            System.out.println("Contains ion mobility data: " +
                    (parser.getAirdInfo().getMobiInfo() != null));

            // 3. Read all data into memory (including binary data)
            System.out.println("\nReading all data into memory with binary data...");
            List<DDAPasefMs> allSpectra = parser.readAllToMemory(true);

            // 4. Process and display data
            System.out.println("\nProcessing DDA-PASEF spectra data:");
            int ms1Count = 0;
            int ms2Count = 0;
            int spectraWithMobility = 0;

            for (DDAPasefMs spectrum : allSpectra) {

                ms1Count++;

                // Check for ion mobility data
                if (spectrum.getSpectrum().getMobilities() != null && spectrum.getSpectrum().getMobilities().length > 0) {
                    spectraWithMobility++;
                    System.out.println("MS1 - RT: " + spectrum.getRt() +
                            ", Mobility points: " + spectrum.getSpectrum().getMobilities().length +
                            ", m/z points: " + spectrum.getSpectrum().getMzs().length);
                } else {
                    System.out.println("MS1 - RT: " + spectrum.getRt() +
                            ", m/z points: " + spectrum.getSpectrum().getMzs().length);
                }

                // Check if this MS1 has associated MS2 spectra
                if (spectrum.getMs2List() != null && !spectrum.getMs2List().isEmpty()) {
                    ms2Count += spectrum.getMs2List().size();
                    System.out.println("  Has " + spectrum.getMs2List().size() + " MS2 spectra");

                    // Show mobility information for first MS2 spectrum
                    if (!spectrum.getMs2List().isEmpty()) {
                        DDAPasefMs firstMs2 = spectrum.getMs2List().get(0);
                        if (firstMs2.getSpectrum().getMobilities() != null && firstMs2.getSpectrum().getMobilities().length > 0) {
                            System.out.println(" First MS2 has " + firstMs2.getSpectrum().getMobilities().length +
                                    " mobility points");
                        }
                    }
                }
            }

            System.out.println("\nSummary:");
            System.out.println("Total MS1 spectra: " + ms1Count);
            System.out.println("Total MS2 spectra: " + ms2Count);
            System.out.println("Spectra with mobility data: " + spectraWithMobility);

            // 5. Example: Query by retention time range
            System.out.println("\nQuerying spectra in RT range 15.0-25.0 minutes...");
            List<DDAPasefMs> spectraInRange = parser.getSpectraByRtRange(15.0, 25.0, true);
            if (spectraInRange != null) {
                System.out.println("Found " + spectraInRange.size() + " spectra in range");

                // Show mobility information for first spectrum in range
                if (!spectraInRange.isEmpty()) {
                    DDAPasefMs firstSpectrum = spectraInRange.get(0);
                    if (firstSpectrum.getSpectrum().getMobilities() != null) {
                        System.out.println("First spectrum has " + firstSpectrum.getSpectrum().getMobilities().length +
                                " mobility data points");

                        // Show first few mobility values
                        System.out.println("First 3 mobility values:");
                        for (int i = 0; i < Math.min(3, firstSpectrum.getSpectrum().getMobilities().length); i++) {
                            System.out.println("  " + String.format("%.4f", firstSpectrum.getSpectrum().getMobilities()[i]));
                        }
                    }
                }
            }

            // 6. Example: Extract mobility-m/z heatmap data for a specific spectrum
            System.out.println("\nExtracting mobility-m/z data for analysis...");
            if (!allSpectra.isEmpty()) {
                DDAPasefMs sampleSpectrum = allSpectra.get(0);
                if (sampleSpectrum.getSpectrum().getMobilities() != null && sampleSpectrum.getSpectrum().getMobilities().length > 0) {
                    System.out.println("Sample spectrum mobility-m/z matrix dimensions:");
                    System.out.println("  Mobility points: " + sampleSpectrum.getSpectrum().getMobilities().length);
                    System.out.println("  m/z points: " + sampleSpectrum.getSpectrum().getMzs().length);

                    // Calculate basic statistics
                    double minMobility = Double.MAX_VALUE;
                    double maxMobility = Double.MIN_VALUE;

                    for (double mobility : sampleSpectrum.getSpectrum().getMobilities()) {
                        if (mobility < minMobility) minMobility = mobility;
                        if (mobility > maxMobility) maxMobility = mobility;
                    }

                    System.out.println("  Mobility range: " + String.format("%.4f", minMobility) +
                            " - " + String.format("%.4f", maxMobility));
                }
            }

            // 7. Close the parser to release resources
            parser.close();
            System.out.println("\nParser closed successfully.");

        } catch (Exception e) {
            System.err.println("Error processing DDA-PASEF file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}