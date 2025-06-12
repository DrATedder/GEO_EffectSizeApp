public class EffectResult {
    private final String probeID;
    private final double cohensD;
    private final String category;

    public EffectResult(String probeID, double cohensD) {
        this.probeID = probeID;
        this.cohensD = cohensD;
        this.category = categorize(cohensD);
    }

    private String categorize(double d) {
        double absD = Math.abs(d);
        if (absD < 0.2) return "Negligible";
        else if (absD < 0.5) return "Small";
        else if (absD < 0.8) return "Medium";
        else return "Large";
    }

    public String getProbeID() { return probeID; }
    public double getCohensD() { return cohensD; }
    public String getCategory() { return category; }
}

