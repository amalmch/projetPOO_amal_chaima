/**
 * INTERFACE DigitClassifier
 *
 * Contrat commun pour tous les classifieurs.
 * Grâce au polymorphisme, on peut switcher entre
 * NaiveBayes et RandomForest sans changer le code client.
 */
public interface DigitClassifier {

    /**
     * Prédit le label d'une image à partir de son vecteur de pixels.
     * @param pixelVector tableau de 784 valeurs [0..255]
     * @return "trois" ou "cinq"
     * @throws Exception si le modèle n'est pas entraîné ou si erreur Weka
     */
    String predict(int[] pixelVector) throws Exception;
}
