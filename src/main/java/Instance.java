/**
 * CLASSE Instance
 * 
 * Représente une donnée d'entraînement ou de test.
 * Contient le vecteur de pixels et le label associé.
 */
public class Instance {
    private final int[] pixels;
    private final String label;

    public Instance(int[] pixels, String label) {
        this.pixels = pixels;
        this.label = label;
    }

    public int[] getPixels() {
        return pixels;
    }

    public String getLabel() {
        return label;
    }
}
