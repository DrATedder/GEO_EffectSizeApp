import java.util.*;

public class EffectSizeCalculator {

    public static List<EffectResult> computeCohensD(Map<String, double[]> matrix, List<String> group1, List<String> group2) {
        List<EffectResult> results = new ArrayList<>();
        List<String> allSamples = new ArrayList<>();
        allSamples.addAll(group1);
        allSamples.addAll(group2);

        Map<String, Integer> sampleIndex = new LinkedHashMap<>();
        for (int i = 0; i < allSamples.size(); i++) {
            sampleIndex.put(allSamples.get(i), i);
        }

        for (Map.Entry<String, double[]> entry : matrix.entrySet()) {
            double[] values = entry.getValue();
            List<Double> vals1 = new ArrayList<>();
            List<Double> vals2 = new ArrayList<>();

            for (int i = 0; i < values.length && i < allSamples.size(); i++) {
                double val = values[i];
                if (Double.isNaN(val)) continue;
                String sampleName = allSamples.get(i);
                if (group1.contains(sampleName)) vals1.add(val);
                else if (group2.contains(sampleName)) vals2.add(val);
            }

            if (!vals1.isEmpty() && !vals2.isEmpty()) {
                double d = compute(vals1, vals2);
                results.add(new EffectResult(entry.getKey(), d));
            }
        }

        return results;
    }

    public static double averageEffectSize(List<EffectResult> results) {
        return results.stream()
                      .mapToDouble(EffectResult::getCohensD)
                      .average()
                      .orElse(Double.NaN);
    }

    public static double overallPooledSD(Map<String, double[]> matrix, List<String> group1, List<String> group2) {
        List<Double> allValues = new ArrayList<>();

        for (Map.Entry<String, double[]> entry : matrix.entrySet()) {
            double[] values = entry.getValue();

            for (int i = 0; i < values.length; i++) {
                double val = values[i];
                if (Double.isNaN(val)) continue;

                String sampleName = null;
                if (i < group1.size()) sampleName = group1.get(i);
                else if (i - group1.size() < group2.size()) sampleName = group2.get(i - group1.size());

                if (sampleName != null && (group1.contains(sampleName) || group2.contains(sampleName))) {
                    allValues.add(val);
                }
            }
        }

        if (allValues.size() < 2) return Double.NaN;

        double mean = allValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double variance = allValues.stream()
                .mapToDouble(val -> Math.pow(val - mean, 2))
                .sum() / (allValues.size() - 1);

        return Math.sqrt(variance);
    }

    private static double compute(List<Double> g1, List<Double> g2) {
        double mean1 = g1.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double mean2 = g2.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        double var1 = g1.stream().mapToDouble(val -> Math.pow(val - mean1, 2)).sum() / (g1.size() - 1);
        double var2 = g2.stream().mapToDouble(val -> Math.pow(val - mean2, 2)).sum() / (g2.size() - 1);

        double pooledSD = Math.sqrt(((g1.size() - 1) * var1 + (g2.size() - 1) * var2) / (g1.size() + g2.size() - 2));
        return (pooledSD == 0) ? 0 : (mean1 - mean2) / pooledSD;
    }
}


