/**
 * CLASSE ManhattanDistance
 * 
 * Implémente la distance de Manhattan (norme L1).
 * Formule : sum( abs( v1[i] - v2[i] ) )
 */
public class ManhattanDistance implements Distance {
    @Override
    public double calculate(int[] v1, int[] v2) {
        double sum = 0;
        for (int i = 0; i < v1.length; i++) {
            sum += Math.abs(v1[i] - v2[i]);
        }
        return sum;
    }
}
