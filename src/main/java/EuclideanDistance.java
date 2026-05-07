/**
 * CLASSE EuclideanDistance
 * 
 * Implémente la distance Euclidienne (norme L2).
 * Formule : sqrt( sum( (v1[i] - v2[i])^2 ) )
 */
public class EuclideanDistance implements Distance {
    @Override
    public double calculate(int[] v1, int[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            double diff = v1[i] - v2[i];
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
}
