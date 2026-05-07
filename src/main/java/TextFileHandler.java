import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

/**
 * CLASSE TextFileHandler
 *
 * Responsabilités :
 *  1. createTextFile(n)      → lit MNIST binaire, génère chiffres.txt (CSV)
 *  2. imageToFile(nomImage)  → PNG 28×28 → ligne CSV dans un fichier texte
 *  3. fileToImage(nomFichier)→ ligne CSV → image PNG 28×28
 *  4. csvToArff(src, dst)    → fichier CSV → fichier ARFF (pour Weka, Partie 3)
 */
public class TextFileHandler extends MNISTProvider {

    // ── Attributs ────────────────────────────────────────────────────────────────

    /** Chemin du fichier texte CSV à lire ou écrire */
    private String filePath;

    /** Référence au lecteur binaire pour accéder aux données MNIST brutes */
    private BinaryMNISTReader reader;

    // ── Constructeur ─────────────────────────────────────────────────────────────

    /**
     * @param filePath chemin du fichier texte CSV (ex: "chiffres.txt")
     * @param reader   instance de BinaryMNISTReader déjà chargée
     */
    public TextFileHandler(String filePath, BinaryMNISTReader reader) {
        super();
        this.filePath = filePath;
        this.reader   = reader;
    }

    // ── Implémentation de DataProcessor ─────────────────────────────────────────

    /**
     * load() : ici on lit le fichier CSV déjà existant.
     * Utilisé pour vérifier / recharger des données texte.
     */
    @Override
    public void load() throws DataFormatMismatchException {
        File f = new File(filePath);
        if (!f.exists()) {
            System.out.println("Fichier " + filePath + " non trouvé.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            int lineNumber = 0;
            String line;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                String[] parts = line.split(",");

                // Chaque ligne doit avoir 785 champs : 784 pixels + 1 label
                if (parts.length != 785) {
                    throw new DataFormatMismatchException(
                            lineNumber,
                            "attendu 785 champs, trouvé " + parts.length
                    );
                }

                // Vérifier que les 784 premiers champs sont bien des entiers
                for (int i = 0; i < 784; i++) {
                    try {
                        Integer.parseInt(parts[i].trim());
                    } catch (NumberFormatException e) {
                        throw new DataFormatMismatchException(
                                lineNumber,
                                "valeur non entière à la colonne " + (i + 1) + " : '" + parts[i] + "'"
                        );
                    }
                }
            }
            this.nbImages = lineNumber;
            System.out.println("✅ Fichier CSV valide : " + lineNumber + " lignes.");

        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
        }
    }

    /**
     * export() : déclenche l'écriture du fichier CSV (alias de createTextFile avec n par défaut).
     */
    @Override
    public void export() throws Exception {
        System.out.println("Utilisez createTextFile(n) pour exporter.");
    }

    // ── MÉTHODE 1 : createTextFile(int n) ───────────────────────────────────────

    /**
     * Génère chiffres.txt avec les n premiers "trois" et les n premiers "cinq".
     *
     * Format de chaque ligne :
     *   pixel0,pixel1,...,pixel783,label
     * où label est "trois" ou "cinq".
     *
     * Le fichier contient donc 2n lignes au total.
     *
     * @param n nombre d'exemples de chaque classe (trois / cinq)
     * @throws MNISTFileNotFoundException si reader n'a pas chargé les données
     * @throws IOException si l'écriture du fichier échoue
     */
    public void createTextFile(int n) throws MNISTFileNotFoundException, IOException {

        if (reader == null) {
            throw new MNISTFileNotFoundException("BinaryMNISTReader non initialisé.");
        }

        int[][] allPixels = reader.getAllPixels();
        int[]   allLabels = reader.getAllLabels();
        int     total     = allLabels.length;

        // Compteurs : combien de "trois" et "cinq" on a déjà écrits
        int countTrois = 0;
        int countCinq  = 0;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {

            // On parcourt toutes les images jusqu'à avoir n de chaque
            for (int i = 0; i < total; i++) {

                int label = allLabels[i];
                String labelStr;

                if (label == 3 && countTrois < n) {
                    labelStr = "trois";
                    countTrois++;
                } else if (label == 5 && countCinq < n) {
                    labelStr = "cinq";
                    countCinq++;
                } else {
                    continue; // on saute les autres chiffres
                }

                // Écriture des 784 pixels séparés par des virgules
                int[] px = allPixels[i];
                StringBuilder sb = new StringBuilder();
                for (int p = 0; p < 784; p++) {
                    sb.append(px[p]);
                    sb.append(',');
                }
                // Ajout du label en fin de ligne
                sb.append(labelStr);
                bw.write(sb.toString());
                bw.newLine();

                // Arrêt dès qu'on a les n exemples de chaque classe
                if (countTrois >= n && countCinq >= n) {
                    break;
                }
            }
        }

        this.nbImages = countTrois + countCinq;
        System.out.println("✅ chiffres.txt créé : " + nbImages + " lignes ("
                + countTrois + " trois, " + countCinq + " cinq).");
    }

    // ── MÉTHODE 2 : imageToFile(String nomImage) ─────────────────────────────────

    /**
     * Lit une image PNG 28×28 pixels et écrit ses 784 valeurs d'intensité en CSV
     * dans un fichier texte portant le même nom (avec extension .txt).
     *
     * @param nomImage chemin vers l'image PNG (ex: "sample.png")
     * @throws InvalidDimensionsException si l'image n'est pas 28×28
     * @throws IOException                si lecture/écriture échoue
     */
    public void imageToFile(String nomImage)
            throws InvalidDimensionsException, IOException {

        // ── Chargement de l'image ────────────────────────────────────────────────
        File imgFile = new File(nomImage);
        BufferedImage img = ImageIO.read(imgFile);

        if (img == null) {
            throw new IOException("Impossible de lire l'image : " + nomImage);
        }

        int imgW = img.getWidth();
        int imgH = img.getHeight();

        // Vérification des dimensions
        if (imgW != 28 || imgH != 28) {
            throw new InvalidDimensionsException(imgW, imgH);
        }

        // ── Extraction des pixels ────────────────────────────────────────────────
        // Détermination du nom du fichier de sortie (même nom, extension .txt)
        String outPath = nomImage.replaceAll("\\.[^.]+$", "") + ".txt";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outPath))) {
            StringBuilder sb = new StringBuilder();

            for (int y = 0; y < 28; y++) {
                for (int x = 0; x < 28; x++) {
                    int rgb = img.getRGB(x, y);

                    // Extraction de la composante rouge (=verte=bleue pour niveaux de gris)
                    // rgb format : 0xAARRGGBB
                    int r = (rgb >> 16) & 0xFF;

                    // MNIST : 0=noir, 255=blanc
                    // Les images dessinées sont blanc sur noir → on inverse si nécessaire
                    int intensity = r;

                    sb.append(intensity);
                    if (!(y == 27 && x == 27)) sb.append(',');
                }
            }

            bw.write(sb.toString());
        }

        System.out.println("✅ imageToFile : " + outPath + " créé.");
    }

    // ── MÉTHODE 3 : fileToImage(String nomFichier) ───────────────────────────────

    /**
     * Recrée une image PNG 28×28 à partir d'un fichier CSV de 784 valeurs.
     *
     * @param nomFichier chemin vers le fichier CSV (ex: "sample.txt")
     * @throws DataFormatMismatchException si le fichier CSV est malformé
     * @throws IOException                 si lecture/écriture échoue
     */
    public void fileToImage(String nomFichier)
            throws DataFormatMismatchException, IOException {

        // ── Lecture des 784 valeurs ──────────────────────────────────────────────
        int[] pixelValues = new int[784];

        try (BufferedReader br = new BufferedReader(new FileReader(nomFichier))) {
            String line = br.readLine();
            if (line == null) {
                throw new DataFormatMismatchException(1, "fichier vide");
            }

            String[] parts = line.split(",");

            // On accepte 784 (sans label) ou 785 (avec label)
            if (parts.length < 784) {
                throw new DataFormatMismatchException(1,
                        "attendu au moins 784 valeurs, trouvé " + parts.length);
            }

            for (int i = 0; i < 784; i++) {
                try {
                    pixelValues[i] = Integer.parseInt(parts[i].trim());
                } catch (NumberFormatException e) {
                    throw new DataFormatMismatchException(1,
                            "valeur non entière à la position " + i + " : '" + parts[i] + "'");
                }
            }
        }

        // ── Construction de l'image PNG ──────────────────────────────────────────
        BufferedImage img = new BufferedImage(28, 28, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                int intensity = pixelValues[y * 28 + x];
                // Reconstitution RGB en niveaux de gris
                int rgb = (intensity << 16) | (intensity << 8) | intensity;
                img.setRGB(x, y, rgb);
            }
        }

        // Nom du fichier de sortie (même nom, extension .png)
        String outPath = nomFichier.replaceAll("\\.[^.]+$", "") + "_reconstitue.png";
        ImageIO.write(img, "PNG", new File(outPath));

        System.out.println("✅ fileToImage : " + outPath + " créé.");
    }

    // ── MÉTHODE 4 : csvToArff (utilisée en Partie 3) ────────────────────────────

    /**
     * Convertit un fichier CSV (format createTextFile) en fichier ARFF pour Weka.
     *
     * FORMAT ARFF :
     *   @relation mnist_3_5
     *   @attribute pixel0 NUMERIC
     *   ...
     *   @attribute pixel783 NUMERIC
     *   @attribute class {trois,cinq}
     *   @data
     *   0,0,...,255,trois
     *
     * @param src chemin du fichier CSV source
     * @param dst chemin du fichier ARFF de destination
     * @throws IOException si lecture/écriture échoue
     */
    public void csvToArff(String src, String dst) throws IOException {

        try (BufferedReader br = new BufferedReader(new FileReader(src));
             BufferedWriter bw = new BufferedWriter(new FileWriter(dst))) {

            // ── En-tête ARFF ─────────────────────────────────────────────────────
            bw.write("@relation mnist_3_5\n\n");

            // 784 attributs numériques (un par pixel)
            for (int i = 0; i < 784; i++) {
                bw.write("@attribute pixel" + i + " NUMERIC\n");
            }

            // Attribut de classe (binaire : trois ou cinq)
            bw.write("@attribute class {trois,cinq}\n\n");
            bw.write("@data\n");

            // ── Données ───────────────────────────────────────────────────────────
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line); // même format que CSV
                bw.newLine();
            }
        }

        System.out.println("✅ csvToArff : " + dst + " créé.");
    }

    // ── Getter ───────────────────────────────────────────────────────────────────

    public String getFilePath() { return filePath; }
}