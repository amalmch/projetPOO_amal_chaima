import java.io.*;

public class ModelComparator {

    // ── Chemins des fichiers ──────────────────────────────────────────────────────
    private static final String IMAGES_PATH    = "data/train-images.idx3-ubyte";
    private static final String LABELS_PATH    = "data/train-labels.idx1-ubyte";
    private static final String TRAIN_CSV      = "data/train-data.csv";
    private static final String TEST_CSV       = "data/test-data.csv";
    private static final String TRAIN_ARFF     = "data/train-data.arff";
    private static final String TEST_ARFF      = "data/test-data.arff";


    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("  PARTIE 3 — Comparaison des modèles ML");
        System.out.println("  Classification binaire : 3 (trois) vs 5 (cinq)");
        System.out.println("==============================================\n");

        System.out.println("── Étape 1 : Chargement MNIST ──");
        BinaryMNISTReader reader = new BinaryMNISTReader(IMAGES_PATH, LABELS_PATH);
        try {
            reader.load();
            System.out.println("  " + reader);
        } catch (Exception e) {
            System.out.println("❌ " + e.getMessage());
            return;
        }

        System.out.println("\n── Étape 2 : Génération des fichiers CSV (3 vs 5) ──");
        try {
            genererCSV(reader, TRAIN_CSV, 400, 0);
            System.out.println("  ✅ train-data.csv : 800 lignes (400 trois + 400 cinq)");

            genererCSV(reader, TEST_CSV, 50, 400);
            System.out.println("  ✅ test-data.csv  : 100 lignes (50 trois + 50 cinq)");
        } catch (Exception e) {
            System.out.println("❌ Erreur CSV : " + e.getMessage());
            return;
        }

        System.out.println("\n── Étape 3 : Conversion CSV → ARFF ──");
        try {
            TextFileHandler handler = new TextFileHandler(TRAIN_CSV, reader);
            handler.csvToArff(TRAIN_CSV, TRAIN_ARFF);
            handler.csvToArff(TEST_CSV,  TEST_ARFF);
        } catch (IOException e) {
            System.out.println("❌ Erreur ARFF : " + e.getMessage());
            return;
        }

        System.out.println("\n── Étape 4 : Entraînement des modèles (3 vs 5) ──");
        System.out.println("  ⏳ Naive Bayes en cours...");

        NaiveBayesClassifier nb = null;
        RandomForestClassifier rf = null;

        try {
            nb = new NaiveBayesClassifier(TRAIN_ARFF);
        } catch (Exception e) {
            System.out.println("❌ NaiveBayes : " + e.getMessage());
            return;
        }

        System.out.println("  ⏳ Random Forest en cours...");
        try {
            rf = new RandomForestClassifier(TRAIN_ARFF);
        } catch (Exception e) {
            System.out.println("❌ RandomForest : " + e.getMessage());
            return;
        }

        System.out.println("\n── Étape 5 : Évaluation sur 100 images de test ──");
        int[][] testPixels = lirePixelsCSV(TEST_CSV);
        String[] testLabels = lireLabelsCSV(TEST_CSV);

        int correctNB = 0;
        int correctRF = 0;
        int total = testLabels.length;

        for (int i = 0; i < total; i++) {
            try {
                if (nb.predict(testPixels[i]).equals(testLabels[i])) correctNB++;
                if (rf.predict(testPixels[i]).equals(testLabels[i])) correctRF++;
            } catch (Exception ignored) {}
        }

        System.out.printf("  Précision Naive Bayes   : %.2f%%%n", (double)correctNB/total*100);
        System.out.printf("  Précision Random Forest : %.2f%%%n", (double)correctRF/total*100);
        System.out.println("\n==============================================");
    }

    private static void genererCSV(BinaryMNISTReader reader, String outPath, int n, int skip)
            throws IOException, MNISTFileNotFoundException {

        int[][] allPixels = reader.getAllPixels();
        int[]   allLabels = reader.getAllLabels();

        int countTrois = 0;
        int countCinq  = 0;
        int skippedTrois = 0;
        int skippedCinq  = 0;

        new File("data").mkdirs();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outPath))) {
            for (int i = 0; i < allLabels.length; i++) {
                int label = allLabels[i];

                if (label == 3) {
                    if (skippedTrois < skip) {
                        skippedTrois++;
                        continue;
                    }
                    if (countTrois < n) {
                        ecrireLigne(bw, allPixels[i], "trois");
                        countTrois++;
                    }
                } else if (label == 5) {
                    if (skippedCinq < skip) {
                        skippedCinq++;
                        continue;
                    }
                    if (countCinq < n) {
                        ecrireLigne(bw, allPixels[i], "cinq");
                        countCinq++;
                    }
                }

                if (countTrois >= n && countCinq >= n) break;
            }
        }
    }

    private static void ecrireLigne(BufferedWriter bw, int[] pixels, String label)
            throws IOException {
        StringBuilder sb = new StringBuilder();
        for (int p = 0; p < 784; p++) {
            sb.append(pixels[p]).append(',');
        }
        sb.append(label);
        bw.write(sb.toString());
        bw.newLine();
    }


    private static int[][] lirePixelsCSV(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            java.util.List<int[]> list = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int[] pixels = new int[784];
                for (int i = 0; i < 784; i++) {
                    pixels[i] = Integer.parseInt(parts[i].trim());
                }
                list.add(pixels);
            }
            return list.toArray(new int[0][]);
        } catch (IOException e) {
            System.out.println("❌ Lecture pixels : " + e.getMessage());
            return null;
        }
    }

    private static String[] lireLabelsCSV(String path) {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            java.util.List<String> list = new java.util.ArrayList<>();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                list.add(parts[784].trim());
            }
            return list.toArray(new String[0]);
        } catch (IOException e) {
            System.out.println("❌ Lecture labels : " + e.getMessage());
            return null;
        }
    }
}
