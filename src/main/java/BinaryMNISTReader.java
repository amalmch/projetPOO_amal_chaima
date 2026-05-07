import java.io.*;

/**
 * CLASSE BinaryMNISTReader
 *
 * Lit les fichiers binaires MNIST (format IDX) :
 *   - train-images.idx3-ubyte  → pixels des images
 *   - train-labels.idx1-ubyte  → labels (0-9)
 *
 * FORMAT IDX3 (images) :
 *   [magic 4 octets][nbImages 4 octets][rows 4 octets][cols 4 octets][pixels...]
 *
 * FORMAT IDX1 (labels) :
 *   [magic 4 octets][nbItems 4 octets][labels...]
 *
 * Les données sont stockées en BIG-ENDIAN (octet de poids fort en premier).
 */
public class BinaryMNISTReader extends MNISTProvider {

    // ── Attributs ────────────────────────────────────────────────────────────────

    /** Chemin vers le fichier d'images binaire */
    private String imagesPath;

    /** Chemin vers le fichier de labels binaire */
    private String labelsPath;

    /**
     * Tableau 2D : pixels[i] est le vecteur de 784 pixels de l'image i.
     * pixels[i][j] ∈ [0, 255]
     */
    private int[][] pixels;

    /**
     * labels[i] est le chiffre manuscrit de l'image i (0 à 9).
     */
    private int[] labels;

    // ── Constructeur ─────────────────────────────────────────────────────────────

    /**
     * @param imagesPath chemin vers train-images.idx3-ubyte
     * @param labelsPath chemin vers train-labels.idx1-ubyte
     */
    public BinaryMNISTReader(String imagesPath, String labelsPath) {
        super(); // appelle MNISTProvider() → width=28, height=28
        this.imagesPath = imagesPath;
        this.labelsPath = labelsPath;
    }

    // ── Implémentation de DataProcessor ─────────────────────────────────────────

    /**
     * Lit les deux fichiers binaires et remplit les tableaux pixels[] et labels[].
     *
     * @throws MNISTFileNotFoundException si un fichier est absent ou illisible
     */
    @Override
    public void load() throws MNISTFileNotFoundException {

        // ── Vérification existence des fichiers ──────────────────────────────────
        File imgFile = new File(imagesPath);
        File lblFile = new File(labelsPath);

        if (!imgFile.exists() || !imgFile.canRead()) {
            throw new MNISTFileNotFoundException(
                    "Fichier d'images introuvable ou illisible : " + imagesPath
            );
        }
        if (!lblFile.exists() || !lblFile.canRead()) {
            throw new MNISTFileNotFoundException(
                    "Fichier de labels introuvable ou illisible : " + labelsPath
            );
        }

        // ── Lecture des images ───────────────────────────────────────────────────
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(imgFile)))) {

            int magic    = dis.readInt();  // magic number (doit être 2051)
            int nbImg    = dis.readInt();  // nombre d'images
            int rows     = dis.readInt();  // nombre de lignes (28)
            int cols     = dis.readInt();  // nombre de colonnes (28)

            this.nbImages = nbImg;
            this.pixels   = new int[nbImg][rows * cols];

            // Lecture pixel par pixel (chaque pixel : 1 octet non signé 0-255)
            for (int i = 0; i < nbImg; i++) {
                for (int p = 0; p < rows * cols; p++) {
                    // readUnsignedByte() lit 1 octet et retourne [0, 255]
                    pixels[i][p] = dis.readUnsignedByte();
                }
            }

        } catch (IOException e) {
            throw new MNISTFileNotFoundException(
                    "Erreur lors de la lecture des images : " + imagesPath
            );
        }

        // ── Lecture des labels ───────────────────────────────────────────────────
        try (DataInputStream dis = new DataInputStream(
                new BufferedInputStream(new FileInputStream(lblFile)))) {

            int magic  = dis.readInt(); // magic number (doit être 2049)
            int nbLbl  = dis.readInt(); // nombre de labels

            this.labels = new int[nbLbl];

            for (int i = 0; i < nbLbl; i++) {
                labels[i] = dis.readUnsignedByte(); // label : chiffre 0-9
            }

        } catch (IOException e) {
            throw new MNISTFileNotFoundException(
                    "Erreur lors de la lecture des labels : " + labelsPath
            );
        }

        System.out.println("✅ Chargement OK : " + nbImages + " images lues.");
    }

    /**
     * Export non applicable ici (BinaryMNISTReader est en lecture seule).
     * La méthode est vide mais doit exister car DataProcessor l'impose.
     */
    @Override
    public void export() throws Exception {
        System.out.println("BinaryMNISTReader : export() non utilisé.");
    }

    // ── Méthodes utilitaires ─────────────────────────────────────────────────────

    /**
     * Retourne le vecteur de pixels de l'image à l'index donné.
     * @param index indice de l'image (0 basé)
     * @return tableau de 784 valeurs [0..255]
     */
    public int[] getPixels(int index) {
        return pixels[index];
    }

    /**
     * Retourne le label (chiffre 0-9) de l'image à l'index donné.
     */
    public int getLabel(int index) {
        return labels[index];
    }

    /**
     * Retourne tous les pixels (accès complet pour les autres classes).
     */
    public int[][] getAllPixels() {
        return pixels;
    }

    /**
     * Retourne tous les labels.
     */
    public int[] getAllLabels() {
        return labels;
    }
}