/**
 * CLASSE ABSTRAITE MNISTProvider
 *
 * Une classe abstraite est à mi-chemin entre une interface et une classe normale :
 * - Elle PEUT avoir des attributs (variables) communs à toutes ses sous-classes
 * - Elle PEUT avoir des méthodes concrètes (avec un corps) ET des méthodes abstraites
 * - On ne peut PAS l'instancier directement (pas de "new MNISTProvider()")
 *
 * Ici, elle implémente DataProcessor mais laisse load() et export() aux sous-classes.
 */
public abstract class MNISTProvider implements DataProcessor {

    // ── Attributs communs à tous les fournisseurs de données MNIST ──────────────

    /** Nombre d'images chargées en mémoire */
    protected int nbImages;

    /** Largeur d'une image MNIST en pixels (toujours 28) */
    protected int width;

    /** Hauteur d'une image MNIST en pixels (toujours 28) */
    protected int height;

    // ── Constructeur ────────────────────────────────────────────────────────────

    /**
     * Initialise la résolution standard MNIST : 28 × 28 pixels.
     * Les sous-classes appellent ce constructeur via super().
     */
    public MNISTProvider() {
        this.width  = 28;
        this.height = 28;
        this.nbImages = 0;
    }

    // ── Getters ─────────────────────────────────────────────────────────────────

    public int getNbImages() { return nbImages; }
    public int getWidth()    { return width;    }
    public int getHeight()   { return height;   }

    // ── Méthode abstraite héritée de DataProcessor ───────────────────────────────
    // load() et export() restent abstraites → chaque sous-classe les définit à sa façon

    @Override
    public abstract void load() throws Exception;

    @Override
    public abstract void export() throws Exception;

    // ── Méthode concrète commune (utilitaire) ────────────────────────────────────

    /**
     * Retourne une description lisible de ce fournisseur.
     * Toutes les sous-classes héritent de cette méthode sans la réécrire.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName()
                + " [nbImages=" + nbImages
                + ", résolution=" + width + "×" + height + "]";
    }
}