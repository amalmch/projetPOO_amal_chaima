/**
 * INTERFACE Distance
 * 
 * Définit le contrat pour toute mesure de similarité entre deux vecteurs de pixels.
 */
public interface Distance {
    /**
     * Calcule la distance entre deux vecteurs d'entiers.
     * @param v1 Premier vecteur (784 pixels)
     * @param v2 Second vecteur (784 pixels)
     * @return La distance (plus elle est petite, plus les vecteurs sont proches)
     */
    double calculate(int[] v1, int[] v2);
}
