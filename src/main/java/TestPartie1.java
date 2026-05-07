public class TestPartie1 {

    private static final String IMAGES_PATH = "data/train-images.idx3-ubyte";
    private static final String LABELS_PATH = "data/train-labels.idx1-ubyte";

    public static void main(String[] args) {

        System.out.println("==============================================");
        System.out.println("  TEST PARTIE 1 — Architecture OO MNIST");
        System.out.println("==============================================\n");

        // ── ÉTAPE 1 : Chargement des fichiers binaires MNIST ─────────────────────
        System.out.println("── Étape 1 : Lecture des fichiers binaires MNIST ──");

        BinaryMNISTReader reader = new BinaryMNISTReader(IMAGES_PATH, LABELS_PATH);

        try {
            reader.load();
            System.out.println("  " + reader);
        } catch (MNISTFileNotFoundException e) {
            System.out.println("❌ Erreur : " + e.getMessage());
            System.out.println("   → Vérifiez les chemins IMAGES_PATH et LABELS_PATH.");
            return;
        } catch (Exception e) {
            System.out.println("❌ Erreur inattendue : " + e.getMessage());
            return;
        }

        // ── ÉTAPE 2 : createTextFile(50) ─────────────────────────────────────────
        System.out.println("\n── Étape 2 : createTextFile(50) ──");

        TextFileHandler handler = new TextFileHandler("chiffres.txt", reader);

        try {
            handler.createTextFile(50);
            int lignes = compterLignes("chiffres.txt");
            System.out.println("  Nombre de lignes dans chiffres.txt : " + lignes);
            System.out.println("  Attendu : 100 (50 trois + 50 cinq)");
        } catch (MNISTFileNotFoundException e) {
            System.out.println("❌ Erreur fichier MNIST : " + e.getMessage());
        } catch (java.io.IOException e) {
            System.out.println("❌ Erreur d'écriture : " + e.getMessage());
        }

        // ── GÉNÉRATION DE L'IMAGE DE TEST (depuis MNIST) ──────────────────────────
        System.out.println("\n── Génération de l'image de test depuis MNIST ──");
        try {
            exporterImageTest(reader);
        } catch (Exception e) {
            System.out.println("⚠️  Impossible de générer l'image test : " + e.getMessage());
        }

        // ── ÉTAPE 3 : imageToFile + fileToImage ──────────────────────────────────
        System.out.println("\n── Étape 3 : imageToFile + fileToImage ──");

        String testImagePath = "data/sample_28x28.png";

        try {
            // PNG 28×28 → fichier CSV texte
            handler.imageToFile(testImagePath);

            // Fichier CSV texte → PNG reconstitué
            String csvOutput = testImagePath.replaceAll("\\.[^.]+$", "") + ".txt";
            handler.fileToImage(csvOutput);

        } catch (InvalidDimensionsException e) {
            System.out.println("❌ Dimensions incorrectes : " + e.getMessage());
        } catch (DataFormatMismatchException e) {
            System.out.println("❌ Format invalide ligne " + e.getLineNumber()
                    + " : " + e.getProblematicValue());
        } catch (java.io.IOException e) {
            System.out.println("⚠️  Erreur image : " + e.getMessage());
        }

        // ── ÉTAPE 4 : createExcelFile ─────────────────────────────────────────────
        System.out.println("\n── Étape 4 : createExcelFile ──");

        ExcelExporter exporter = new ExcelExporter("chiffres.txt", "chiffres.xlsx");

        try {
            exporter.createExcelFile("chiffres.xlsx");
            System.out.println("  Lignes de données dans Excel : " + exporter.getNbImages());
        } catch (DataFormatMismatchException e) {
            System.out.println("❌ Format CSV invalide ligne " + e.getLineNumber()
                    + " : " + e.getProblematicValue());
        } catch (java.io.IOException e) {
            System.out.println("❌ Erreur Excel : " + e.getMessage());
        }

        // ── BILAN ─────────────────────────────────────────────────────────────────
        System.out.println("\n==============================================");
        System.out.println("  Tests Partie 1 terminés.");
        System.out.println("==============================================");
    }

    // ── Exporte la 1ère image "trois" de MNIST comme PNG 28×28 ───────────────────

    private static void exporterImageTest(BinaryMNISTReader reader) throws Exception {
        int[]   allLabels = reader.getAllLabels();
        int[][] allPixels = reader.getAllPixels();

        for (int i = 0; i < allLabels.length; i++) {
            if (allLabels[i] == 3) {
                java.awt.image.BufferedImage img =
                        new java.awt.image.BufferedImage(28, 28,
                                java.awt.image.BufferedImage.TYPE_BYTE_GRAY);

                for (int y = 0; y < 28; y++) {
                    for (int x = 0; x < 28; x++) {
                        int v = allPixels[i][y * 28 + x];
                        img.setRGB(x, y, (v << 16) | (v << 8) | v);
                    }
                }

                new java.io.File("data").mkdirs();
                javax.imageio.ImageIO.write(img, "PNG",
                        new java.io.File("data/sample_28x28.png"));
                System.out.println("✅ Image test exportée : data/sample_28x28.png");
                return;
            }
        }
        System.out.println("⚠️  Aucun 'trois' trouvé dans MNIST.");
    }

    // ── Compte les lignes d'un fichier texte ──────────────────────────────────────

    private static int compterLignes(String path) {
        int count = 0;
        try (java.io.BufferedReader br =
                     new java.io.BufferedReader(new java.io.FileReader(path))) {
            while (br.readLine() != null) count++;
        } catch (java.io.IOException e) {
            System.out.println("Impossible de compter les lignes : " + e.getMessage());
        }
        return count;
    }
}