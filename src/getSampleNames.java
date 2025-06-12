import java.io.*;
import java.util.*;

public class getSampleNames {

    public static List<String> getSampleNames(File file) {
        List<String> sampleNames = new ArrayList<>();
        boolean inTable = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.equals("!series_matrix_table_begin")) {
                    inTable = true;
                    continue;
                }

                if (inTable && line.startsWith("\"ID_REF\"")) {
                    String[] tokens = line.split("\t");
                    for (int i = 1; i < tokens.length; i++) {
                        sampleNames.add(tokens[i].replaceAll("^\"|\"$", ""));
                    }
                    break; // found header line, no need to continue
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return sampleNames;
    }
}

