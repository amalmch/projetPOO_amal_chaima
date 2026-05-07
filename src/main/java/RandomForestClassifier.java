import weka.classifiers.trees.RandomForest;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/**
 * CLASSE RandomForestClassifier
 *
 * Encapsule l'algorithme Random Forest de Weka.
 * Random Forest construit plusieurs arbres de décision en parallèle
 * et vote pour la classe majoritaire → plus robuste que Naive Bayes.
 *
 * Même contrat que NaiveBayesClassifier (implements DigitClassifier).
 */
public class RandomForestClassifier implements DigitClassifier {

    private RandomForest model;

    /** Structure des données (schéma ARFF) */
    private Instances dataStructure;

    // ── Constructeur ─────────────────────────────────────────────────────────────

    /**
     * Charge le fichier ARFF, entraîne le modèle Random Forest.
     *
     * @param arffPath chemin vers train-data.arff
     * @throws Exception si le fichier est invalide ou l'entraînement échoue
     */
    public RandomForestClassifier(String arffPath) throws Exception {

        // ── Chargement du fichier ARFF ───────────────────────────────────────────
        DataSource source = new DataSource(arffPath);
        Instances data = source.getDataSet();

        // Dernière colonne = classe à prédire
        data.setClassIndex(data.numAttributes() - 1);

        // Copie de la structure vide pour créer des instances de test
        this.dataStructure = new Instances(data, 0);

        // ── Entraînement ─────────────────────────────────────────────────────────
        this.model = new RandomForest();
        this.model.setNumIterations(50); // 50 arbres (bon équilibre vitesse/précision)
        this.model.buildClassifier(data);

        System.out.println("✅ RandomForest entraîné sur : " + arffPath);
    }

    // ── Implémentation de DigitClassifier ────────────────────────────────────────

    /**
     * Prédit le label ("trois" ou "cinq") pour un vecteur de 784 pixels.
     *
     * @param pixelVector tableau de 784 valeurs [0..255]
     * @return "trois" ou "cinq"
     * @throws Exception si la prédiction échoue
     */
    @Override
    public String predict(int[] pixelVector) throws Exception {
        Instance instance = createInstance(pixelVector);
        double classIndex = model.classifyInstance(instance);
        return instance.classAttribute().value((int) classIndex);
    }

    /**
     * Retourne la distribution de probabilités pour chaque classe.
     * Utile pour afficher la confiance du modèle (Partie 4 GUI).
     *
     * @param pixelVector tableau de 784 valeurs [0..255]
     * @return tableau [P(trois), P(cinq)]
     */
    public double[] getProbabilities(int[] pixelVector) throws Exception {
        Instance instance = createInstance(pixelVector);
        return model.distributionForInstance(instance);
    }

    // ── Méthode utilitaire privée ─────────────────────────────────────────────────

    /**
     * Convertit un vecteur int[] en Instance Weka.
     */
    private Instance createInstance(int[] pixelVector) {
        Instance instance = new DenseInstance(dataStructure.numAttributes());
        instance.setDataset(dataStructure);

        for (int i = 0; i < 784; i++) {
            instance.setValue(i, pixelVector[i]);
        }

        instance.setClassMissing();
        return instance;
    }
}
