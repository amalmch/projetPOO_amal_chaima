// ════════════════════════════════════════════════════════════════════════════════
// PARTIE 2 — EXCEPTIONS PERSONNALISÉES
// Toutes les 3 exceptions sont dans ce fichier pour la clarté.
// En Java, chaque classe publique doit normalement être dans son propre fichier.
// Si ton prof exige des fichiers séparés, découpe ce fichier en 3.
// ════════════════════════════════════════════════════════════════════════════════

// ── Exception 1 ─────────────────────────────────────────────────────────────────

/**
 * InvalidDimensionsException
 *
 * Levée par imageToFile() quand une image PNG fournie
 * n'a pas exactement les dimensions 28×28 pixels.
 *
 * Elle hérite de Exception (checked exception) → le compilateur
 * OBLIGE l'appelant à la gérer (try/catch ou throws).
 */
class InvalidDimensionsException extends Exception {

    private final int actualWidth;
    private final int actualHeight;

    /**
     * @param actualWidth  largeur réelle de l'image
     * @param actualHeight hauteur réelle de l'image
     */
    public InvalidDimensionsException(int actualWidth, int actualHeight) {
        // super() appelle le constructeur de Exception avec le message
        super("Dimensions invalides : l'image doit être 28×28 pixels. "
                + "Dimensions détectées : " + actualWidth + "×" + actualHeight + " pixels.");
        this.actualWidth  = actualWidth;
        this.actualHeight = actualHeight;
    }

    public int getActualWidth()  { return actualWidth; }
    public int getActualHeight() { return actualHeight; }
}


// ── Exception 2 ─────────────────────────────────────────────────────────────────

/**
 * MNISTFileNotFoundException
 *
 * Levée par BinaryMNISTReader quand train-images ou train-labels
 * sont absents ou illisibles.
 *
 * On utilise le "cause chaining" : on peut enchaîner une IOException
 * pour conserver la cause racine.
 */
class MNISTFileNotFoundException extends Exception {

    private final String missingPath;

    /**
     * @param missingPath chemin du fichier introuvable
     */
    public MNISTFileNotFoundException(String missingPath) {
        super("Fichier MNIST introuvable ou illisible : " + missingPath);
        this.missingPath = missingPath;
    }

    /**
     * Constructeur avec cause racine (cause chaining).
     * @param missingPath chemin du fichier
     * @param cause       exception originale (ex: IOException)
     */
    public MNISTFileNotFoundException(String missingPath, Throwable cause) {
        super("Fichier MNIST introuvable ou illisible : " + missingPath, cause);
        this.missingPath = missingPath;
    }

    public String getMissingPath() { return missingPath; }
}


// ── Exception 3 ─────────────────────────────────────────────────────────────────

/**
 * DataFormatMismatchException
 *
 * Levée par TextFileHandler quand une ligne CSV est malformée :
 *  - Pas exactement 785 champs (784 entiers + 1 label)
 *  - Une valeur n'est pas convertible en entier
 */
class DataFormatMismatchException extends Exception {

    private final int    lineNumber;
    private final String problematicValue;

    /**
     * @param lineNumber       numéro de la ligne fautive (1-basé)
     * @param problematicValue description du problème ou valeur incorrecte
     */
    public DataFormatMismatchException(int lineNumber, String problematicValue) {
        super("Format de données incorrect à la ligne " + lineNumber
                + " : " + problematicValue);
        this.lineNumber       = lineNumber;
        this.problematicValue = problematicValue;
    }

    public int    getLineNumber()       { return lineNumber; }
    public String getProblematicValue() { return problematicValue; }
}