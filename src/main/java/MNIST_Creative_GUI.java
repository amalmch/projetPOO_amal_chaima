import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * MNIST_Creative_GUI
 * 
 * Fenêtre principale premium pour la reconnaissance de chiffres 3 et 5.
 * Combine design moderne, animations et machine learning.
 */
public class MNIST_Creative_GUI extends JFrame {

    private DrawingArea drawingArea;
    private ModernUIComponents.ProbabilityBar barTrois;
    private ModernUIComponents.ProbabilityBar barCinq;
    private JLabel resultLabel;
    private JLabel predDigit;
    private JLabel confidenceLabel;
    private ModernUIComponents.StatusIndicator statusDot;
    private DigitClassifier classifier;

    public MNIST_Creative_GUI() {
        setTitle("MNIST — Reconnaissance 3 vs 5");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        getContentPane().setBackground(ModernUIComponents.COLOR_BG);
        setLayout(new BorderLayout(0, 0));

        initUI();
        setSize(920, 680);
        setLocationRelativeTo(null);

        loadModelAsync();
    }

    private void initUI() {

        // ══════════════════════════════════════════════════════════════════════════
        //  HEADER — Gradient title bar
        // ══════════════════════════════════════════════════════════════════════════
        JPanel header = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle gradient line at bottom
                GradientPaint gp = new GradientPaint(0, getHeight() - 2, ModernUIComponents.GRAD_START,
                        getWidth(), getHeight() - 2, ModernUIComponents.GRAD_END);
                g2.setPaint(gp);
                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                g2.dispose();
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(18, 28, 14, 28));

        // Left: title + subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("✦  Smart Digit Recognition");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(ModernUIComponents.COLOR_TEXT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(title);

        JLabel subtitle = new JLabel("Classification binaire — Trois vs Cinq");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(ModernUIComponents.COLOR_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        titlePanel.add(subtitle);

        header.add(titlePanel, BorderLayout.WEST);

        // Right: status indicator
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        statusPanel.setOpaque(false);
        statusDot = new ModernUIComponents.StatusIndicator();
        resultLabel = new JLabel("Chargement du modèle...");
        resultLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        resultLabel.setForeground(ModernUIComponents.COLOR_WARN);
        statusPanel.add(statusDot);
        statusPanel.add(resultLabel);
        header.add(statusPanel, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // ══════════════════════════════════════════════════════════════════════════
        //  MAIN CONTENT
        // ══════════════════════════════════════════════════════════════════════════
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 28, 10, 28));
        GridBagConstraints gbc = new GridBagConstraints();

        // ── LEFT: Drawing Area ──────────────────────────────────────────────────
        ModernUIComponents.RoundedPanel drawingCard = new ModernUIComponents.RoundedPanel(20, true, ModernUIComponents.COLOR_ACCENT);
        drawingCard.setLayout(new BorderLayout(0, 0));
        drawingCard.setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JLabel drawLabel = new JLabel("Zone de Dessin");
        drawLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        drawLabel.setForeground(ModernUIComponents.COLOR_MUTED);
        drawLabel.setBorder(BorderFactory.createEmptyBorder(0, 4, 8, 0));
        drawingCard.add(drawLabel, BorderLayout.NORTH);

        drawingArea = new DrawingArea();
        drawingCard.add(drawingArea, BorderLayout.CENTER);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 20);
        gbc.anchor = GridBagConstraints.NORTH;
        mainContent.add(drawingCard, gbc);

        // ── RIGHT: Control Panel ────────────────────────────────────────────────
        JPanel rightPanel = new JPanel();
        rightPanel.setOpaque(false);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setPreferredSize(new Dimension(340, 420));

        // ── Prediction result card ──
        ModernUIComponents.RoundedPanel resultCard = new ModernUIComponents.RoundedPanel(16);
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        resultCard.setMaximumSize(new Dimension(340, 150));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel predTitle = new JLabel("Prédiction");
        predTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        predTitle.setForeground(ModernUIComponents.COLOR_MUTED);
        predTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultCard.add(predTitle);
        resultCard.add(Box.createVerticalStrut(4));

        // Big prediction digit
        JPanel predRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        predRow.setOpaque(false);
        predRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        predDigit = new JLabel("?");
        predDigit.setFont(new Font("Segoe UI", Font.BOLD, 72));
        predDigit.setForeground(ModernUIComponents.COLOR_ACCENT2);
        predRow.add(predDigit);

        confidenceLabel = new JLabel("   Dessinez un chiffre");
        confidenceLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        confidenceLabel.setForeground(ModernUIComponents.COLOR_MUTED);
        predRow.add(confidenceLabel);

        resultCard.add(predRow);
        rightPanel.add(resultCard);
        rightPanel.add(Box.createVerticalStrut(16));

        // ── Probability bars card ──
        ModernUIComponents.RoundedPanel probCard = new ModernUIComponents.RoundedPanel(16);
        probCard.setLayout(new BoxLayout(probCard, BoxLayout.Y_AXIS));
        probCard.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        probCard.setMaximumSize(new Dimension(340, 170));
        probCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel probTitle = new JLabel("Probabilités");
        probTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        probTitle.setForeground(ModernUIComponents.COLOR_MUTED);
        probTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        probCard.add(probTitle);
        probCard.add(Box.createVerticalStrut(8));

        // Bar for digit 3 (Trois)
        barTrois = new ModernUIComponents.ProbabilityBar("Trois",  "3️⃣",
                new Color(99, 102, 241), new Color(139, 92, 246));
        barTrois.setAlignmentX(Component.LEFT_ALIGNMENT);
        barTrois.setMaximumSize(new Dimension(300, 56));
        probCard.add(barTrois);
        probCard.add(Box.createVerticalStrut(6));

        // Bar for digit 5 (Cinq)
        barCinq = new ModernUIComponents.ProbabilityBar("Cinq",   "5️⃣",
                new Color(14, 165, 233), new Color(56, 189, 248));
        barCinq.setAlignmentX(Component.LEFT_ALIGNMENT);
        barCinq.setMaximumSize(new Dimension(300, 56));
        probCard.add(barCinq);

        rightPanel.add(probCard);
        rightPanel.add(Box.createVerticalStrut(20));

        // ── Buttons ──
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(340, 48));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        ModernUIComponents.AnimatedButton btnPredict = new ModernUIComponents.AnimatedButton("⚡  ANALYSER", true);
        ModernUIComponents.AnimatedButton btnClear   = new ModernUIComponents.AnimatedButton("✕  EFFACER", false);

        btnPredict.addActionListener(e -> performPrediction());
        btnClear.addActionListener(e -> {
            drawingArea.clear();
            resetUI();
        });

        buttonPanel.add(btnPredict);
        buttonPanel.add(btnClear);
        rightPanel.add(buttonPanel);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTH;
        mainContent.add(rightPanel, gbc);

        add(mainContent, BorderLayout.CENTER);

        // ══════════════════════════════════════════════════════════════════════════
        //  FOOTER
        // ══════════════════════════════════════════════════════════════════════════
        JPanel footerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                // Top gradient line
                GradientPaint gp = new GradientPaint(0, 0, ModernUIComponents.GRAD_START,
                        getWidth(), 0, ModernUIComponents.GRAD_END);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), 1);
                g2.dispose();
            }
        };
        footerPanel.setOpaque(false);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(8, 28, 10, 28));

        JLabel footerLeft = new JLabel("Projet POO — MNIST 3 vs 5");
        footerLeft.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerLeft.setForeground(new Color(71, 85, 105));
        footerPanel.add(footerLeft, BorderLayout.WEST);

        JLabel footerRight = new JLabel("RandomForest • Weka");
        footerRight.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footerRight.setForeground(new Color(71, 85, 105));
        footerPanel.add(footerRight, BorderLayout.EAST);

        add(footerPanel, BorderLayout.SOUTH);
    }

    // ── Model Loading ────────────────────────────────────────────────────────────

    private void loadModelAsync() {
        new Thread(() -> {
            try {
                classifier = new RandomForestClassifier("data/train-data.arff");
                SwingUtilities.invokeLater(() -> {
                    resultLabel.setText("Modèle prêt");
                    resultLabel.setForeground(ModernUIComponents.COLOR_SUCCESS);
                    statusDot.setStatus(ModernUIComponents.COLOR_SUCCESS, false);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultLabel.setText("⚠ Erreur modèle");
                    resultLabel.setForeground(new Color(239, 68, 68));
                    statusDot.setStatus(new Color(239, 68, 68), true);
                });
            }
        }).start();
    }

    // ── Prediction ───────────────────────────────────────────────────────────────

    private void performPrediction() {
        if (classifier == null) return;
        try {
            int[] pixels = drawingArea.getMnistPixels();
            String prediction = classifier.predict(pixels);

            double[] probs = null;
            if (classifier instanceof RandomForestClassifier) {
                probs = ((RandomForestClassifier) classifier).getProbabilities(pixels);
            }

            final double[] finalProbs = probs;
            SwingUtilities.invokeLater(() -> {
                // Update prediction display
                String displayDigit;
                if (prediction.equalsIgnoreCase("trois") || prediction.equals("3")) {
                    displayDigit = "3";
                } else if (prediction.equalsIgnoreCase("cinq") || prediction.equals("5")) {
                    displayDigit = "5";
                } else {
                    displayDigit = prediction;
                }

                predDigit.setText(displayDigit);
                predDigit.setForeground(ModernUIComponents.COLOR_ACCENT2);

                if (finalProbs != null && finalProbs.length >= 2) {
                    double pTrois = finalProbs[0];
                    double pCinq  = finalProbs[1];

                    barTrois.setValue(pTrois);
                    barCinq.setValue(pCinq);

                    // Highlight winner
                    barTrois.setWinner(pTrois >= pCinq);
                    barCinq.setWinner(pCinq > pTrois);

                    double maxProb = Math.max(pTrois, pCinq);
                    confidenceLabel.setText(String.format("   Confiance: %.1f%%", maxProb * 100));
                    confidenceLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    confidenceLabel.setForeground(ModernUIComponents.COLOR_SUCCESS);
                } else {
                    confidenceLabel.setText("   " + prediction);
                    confidenceLabel.setForeground(ModernUIComponents.COLOR_MUTED);
                }

                resultLabel.setText("Analyse terminée");
                resultLabel.setForeground(ModernUIComponents.COLOR_SUCCESS);
            });
        } catch (Exception e) {
            e.printStackTrace();
            resultLabel.setText("Erreur d'analyse");
            resultLabel.setForeground(new Color(239, 68, 68));
        }
    }

    // ── Reset ────────────────────────────────────────────────────────────────────

    private void resetUI() {
        predDigit.setText("?");
        predDigit.setForeground(ModernUIComponents.COLOR_ACCENT2);
        confidenceLabel.setText("   Dessinez un chiffre");
        confidenceLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        confidenceLabel.setForeground(ModernUIComponents.COLOR_MUTED);
        barTrois.setValue(0);
        barCinq.setValue(0);
        barTrois.setWinner(false);
        barCinq.setWinner(false);
        resultLabel.setText("Modèle prêt");
        resultLabel.setForeground(ModernUIComponents.COLOR_SUCCESS);
    }

    // ── Main ─────────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        // Anti-aliasing global
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        SwingUtilities.invokeLater(() -> {
            new MNIST_Creative_GUI().setVisible(true);
        });
    }
}
