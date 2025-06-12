import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private JFrame frame;
    private JTable resultTable;
    private JFileChooser fileChooser;
    private File loadedFile;
    private JPanel group1Panel, group2Panel;
    private JLabel summaryLabel;
    private List<String> sampleNames;
    private Map<String, JCheckBox> sampleCheckboxes = new HashMap<>();
    private List<EffectResult> results;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Main().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Cohen's d from GEO Series Matrix");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        JButton loadButton = new JButton("Load GEO Matrix File");
        JButton computeButton = new JButton("Compute Cohen's d");
        JButton exportButton = new JButton("Export Top Genes");

        resultTable = new JTable();

        group1Panel = new JPanel();
        group1Panel.setLayout(new BoxLayout(group1Panel, BoxLayout.Y_AXIS));
        group1Panel.setBorder(BorderFactory.createTitledBorder("Group 1"));

        group2Panel = new JPanel();
        group2Panel.setLayout(new BoxLayout(group2Panel, BoxLayout.Y_AXIS));
        group2Panel.setBorder(BorderFactory.createTitledBorder("Group 2"));

        JScrollPane scroll1 = new JScrollPane(group1Panel);
        scroll1.setPreferredSize(new Dimension(200, 400));
        JScrollPane scroll2 = new JScrollPane(group2Panel);
        scroll2.setPreferredSize(new Dimension(200, 400));

        JPanel groupPanel = new JPanel(new GridLayout(1, 2));
        groupPanel.add(scroll1);
        groupPanel.add(scroll2);

        summaryLabel = new JLabel("Top genes by Cohen’s d");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loadButton);
        buttonPanel.add(computeButton);
        buttonPanel.add(exportButton);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, groupPanel, new JScrollPane(resultTable));
        splitPane.setDividerLocation(400);

        frame.getContentPane().add(buttonPanel, BorderLayout.NORTH);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);
        frame.getContentPane().add(summaryLabel, BorderLayout.SOUTH);

        fileChooser = new JFileChooser();

        loadButton.addActionListener(e -> loadFile());
        computeButton.addActionListener(e -> computeEffects());
        exportButton.addActionListener(e -> exportTopGenes());

        frame.setVisible(true);
    }

    private void loadFile() {
        int returnVal = fileChooser.showOpenDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            loadedFile = fileChooser.getSelectedFile();
            Map<String, double[]> matrix = loadMatrix.loadMatrix(loadedFile);
            if (matrix.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Failed to load data.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            sampleNames = getSampleNames.getSampleNames(loadedFile);
            if (sampleNames.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No sample names found in matrix file.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            sampleCheckboxes.clear();
            group1Panel.removeAll();
            group2Panel.removeAll();

            for (String sample : sampleNames) {
                JCheckBox cb1 = new JCheckBox(sample);
                JCheckBox cb2 = new JCheckBox(sample);
                sampleCheckboxes.put(sample + "_1", cb1);
                sampleCheckboxes.put(sample + "_2", cb2);
                group1Panel.add(cb1);
                group2Panel.add(cb2);
            }

            group1Panel.revalidate();
            group1Panel.repaint();
            group2Panel.revalidate();
            group2Panel.repaint();
        }
    }

    private void computeEffects() {
        if (loadedFile == null) return;

        List<String> group1 = new ArrayList<>();
        List<String> group2 = new ArrayList<>();

        for (String sample : sampleNames) {
            if (sampleCheckboxes.get(sample + "_1").isSelected()) {
                group1.add(sample);
            }
            if (sampleCheckboxes.get(sample + "_2").isSelected()) {
                group2.add(sample);
            }
        }

        if (group1.isEmpty() || group2.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Select at least one sample for each group.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, double[]> matrix = loadMatrix.loadMatrix(loadedFile);
        results = EffectSizeCalculator.computeCohensD(matrix, group1, group2);

        // Sort results by Cohen’s d descending
        results.sort((a, b) -> Double.compare(Math.abs(b.getCohensD()), Math.abs(a.getCohensD())));

        resultTable.setModel(new EffectTableModel(results));

        summaryLabel.setText("Results sorted by descending effect size (Cohen's d)");
    }

    private void exportTopGenes() {
        if (results == null || results.isEmpty()) return;

        String input = JOptionPane.showInputDialog(frame, "Enter number of top genes to export:", "Top N Genes", JOptionPane.PLAIN_MESSAGE);
        if (input == null || input.trim().isEmpty()) return;

        int topN;
        try {
            topN = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid number entered.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (topN <= 0) return;

        int returnVal = fileChooser.showSaveDialog(frame);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<EffectResult> topResults = results.stream().limit(topN).collect(Collectors.toList());
            CSVUtils.exportResults(file, topResults);
        }
    }
}

