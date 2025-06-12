import java.io.*;
import java.util.*;

public class CSVUtils {

    public static Map<String, double[]> loadMatrix(File file) {
        Map<String, double[]> matrix = new LinkedHashMap<>();
        List<String> sampleHeaders = new ArrayList<>();
        boolean readingData = false;
        int numSamples = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("!")) continue;

                String[] parts = line.split("\t");

                // Detect header
                if (!readingData && (line.startsWith("ID_REF") || line.startsWith("ID"))) {
                    sampleHeaders = Arrays.asList(parts).subList(1, parts.length);
                    numSamples = sampleHeaders.size();
                    readingData = true;
                    continue;
                }

                // Parse data rows
                if (readingData && parts.length >= (numSamples + 1)) {
                    String probe = parts[0];
                    double[] values = new double[numSamples];
                    for (int i = 0; i < numSamples; i++) {
                        try {
                            values[i] = Double.parseDouble(parts[i + 1]);
                        } catch (Exception e) {
                            values[i] = Double.NaN;
                        }
                    }
                    matrix.put(probe, values);
                }
            }

            if (matrix.isEmpty()) {
                System.err.println("⚠️ No data rows found. Ensure header line is followed by numeric data.");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }

    public static List<String> getSampleNames(File file) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("!")) continue;
                if (line.startsWith("ID_REF") || line.startsWith("ID")) {
                    String[] tokens = line.trim().split("\t");
                    return Arrays.asList(tokens).subList(1, tokens.length);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static void exportResults(File file, List<EffectResult> results) {
        try (PrintWriter out = new PrintWriter(file)) {
            out.println("ProbeID,Cohens_d,Category");
            for (EffectResult res : results) {
                out.printf("%s,%.5f,%s%n", res.getProbeID(), res.getCohensD(), res.getCategory());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

