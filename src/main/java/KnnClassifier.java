import java.util.*;

/**
 * CLASSE KnnClassifier
 * 
 * Implémente l'algorithme des K-Plus Proches Voisins.
 */
public class KnnClassifier {
    private final List<Instance> trainData;
    private final Distance distanceMetric;
    private final int k;

    public KnnClassifier(List<Instance> trainData, Distance distanceMetric, int k) {
        this.trainData = trainData;
        this.distanceMetric = distanceMetric;
        this.k = k;
    }

    /**
     * Prédit le label pour une instance donnée.
     * @param query L'instance à classifier
     * @return Le label prédit (le plus fréquent parmi les k voisins)
     */
    public String predict(Instance query) {
        // Liste pour stocker les distances avec chaque instance d'entraînement
        List<Neighbor> neighbors = new ArrayList<>();

        for (Instance trainInstance : trainData) {
            double dist = distanceMetric.calculate(query.getPixels(), trainInstance.getPixels());
            neighbors.add(new Neighbor(trainInstance.getLabel(), dist));
        }

        // Trier par distance croissante
        Collections.sort(neighbors);

        // Compter les occurrences des labels parmi les k premiers voisins
        Map<String, Integer> counts = new HashMap<>();
        for (int i = 0; i < Math.min(k, neighbors.size()); i++) {
            String label = neighbors.get(i).label;
            counts.put(label, counts.getOrDefault(label, 0) + 1);
        }

        // Retourner le label majoritaire
        return Collections.max(counts.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * Classe interne pour représenter un voisin et sa distance.
     */
    private static class Neighbor implements Comparable<Neighbor> {
        String label;
        double distance;

        Neighbor(String label, double distance) {
            this.label = label;
            this.distance = distance;
        }

        @Override
        public int compareTo(Neighbor other) {
            return Double.compare(this.distance, other.distance);
        }
    }
}
