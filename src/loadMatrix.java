import java.io.*;
import java.util.*;

public class loadMatrix {

    public static Map<String, double[]> loadMatrix(File file) {
        Map<String, double[]> matrix = new LinkedHashMap<>();
        boolean inTable = false;
        int numSamples = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("!series_matrix_table_begin")) {
                    inTable = true;
                    continue;
                }

                if (line.equals("!series_matrix_table_end")) {
                    break;
                }

                if (!inTable) continue;

                String[] parts = line.split("\t");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].replaceAll("^\"|\"$", ""); // remove quotes
                }

                if (parts[0].equals("ID_REF")) {
                    numSamples = parts.length - 1;
                    continue; // skip header row
                }

                if (parts.length != numSamples + 1) {
                    continue; // skip malformed lines
                }

                String geneId = parts[0];
                double[] values = new double[numSamples];
                for (int i = 1; i <= numSamples; i++) {
                    try {
                        values[i - 1] = Double.parseDouble(parts[i]);
                    } catch (NumberFormatException e) {
                        values[i - 1] = Double.NaN; // handle missing/invalid values
                    }
                }
                matrix.put(geneId, values);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }
}

