import weka.classifiers.bayes.NaiveBayes;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class NaiveBayesClassifier implements DigitClassifier {


    private NaiveBayes model;

    private Instances dataStructure;

    // ── Constructeur ─────────────────────────────────────────────────────────────

    /**
     * Charge le fichier ARFF, entraîne le modèle Naive Bayes.
     *
     * @param arffPath chemin vers train-data.arff
     * @throws Exception si le fichier est invalide ou l'entraînement échoue
     */
    public NaiveBayesClassifier(String arffPath) throws Exception {


        DataSource source = new DataSource(arffPath);
        Instances data = source.getDataSet();

        data.setClassIndex(data.numAttributes() - 1);

        this.dataStructure = new Instances(data, 0);

        this.model = new NaiveBayes();
        this.model.buildClassifier(data);

        System.out.println("✅ NaiveBayes entraîné sur : " + arffPath);
    }


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
