import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

/**
 * DrawingArea
 * 
 * Composant de dessin interactif optimisé pour le modèle MNIST.
 * Utilise un effet d'encre douce pour une meilleure reconnaissance.
 */
public class DrawingArea extends JPanel {

    private BufferedImage image;
    private Graphics2D g2;
    private final int SIZE = 420; // Taille affichée
    private final int MNIST_SIZE = 28; // Taille réelle modèle

    public DrawingArea() {
        setPreferredSize(new Dimension(SIZE, SIZE));
        setBackground(Color.BLACK);
        
        // Initialisation de l'image (Noire au départ)
        image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
        g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        clear();

        // Gestion de la souris
        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                draw(e.getX(), e.getY());
            }
            @Override
            public void mousePressed(MouseEvent e) {
                draw(e.getX(), e.getY());
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    private void draw(int x, int y) {
        // Pinceau "doux" (gradient radial pour simuler une plume)
        int brushSize = 20;
        RadialGradientPaint rgp = new RadialGradientPaint(
            x, y, brushSize / 2f,
            new float[]{0.0f, 1.0f},
            new Color[]{Color.WHITE, new Color(255, 255, 255, 0)}
        );
        
        g2.setPaint(rgp);
        g2.fillOval(x - brushSize/2, y - brushSize/2, brushSize, brushSize);
        repaint();
    }

    public void clear() {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, SIZE, SIZE);
        repaint();
    }

    /**
     * Convertit le dessin 280x280 en un vecteur de 784 pixels (28x28).
     * Ajoute une étape de centrage (bounding box) pour améliorer la précision.
     */
    public int[] getMnistPixels() {
        // 1. Trouver la bounding box du dessin
        int minX = SIZE, minY = SIZE, maxX = 0, maxY = 0;
        boolean empty = true;
        
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if ((image.getRGB(x, y) & 0xFF) > 10) { // Si le pixel n'est pas noir
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                    empty = false;
                }
            }
        }

        if (empty) return new int[784];

        // 2. Extraire la zone utile et la centrer dans un carré
        int width = maxX - minX + 1;
        int height = maxY - minY + 1;
        int size = Math.max(width, height) + 20; // Ajouter un peu de marge
        
        BufferedImage centered = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D gC = centered.createGraphics();
        gC.setColor(Color.BLACK);
        gC.fillRect(0, 0, size, size);
        
        // Centrage de la bounding box dans le nouveau carré
        int offsetX = (size - width) / 2;
        int offsetY = (size - height) / 2;
        gC.drawImage(image.getSubimage(minX, minY, width, height), offsetX, offsetY, null);
        gC.dispose();

        // 3. Redimensionner vers 28x28
        Image scaled = centered.getScaledInstance(MNIST_SIZE, MNIST_SIZE, Image.SCALE_SMOOTH);
        BufferedImage smallImage = new BufferedImage(MNIST_SIZE, MNIST_SIZE, BufferedImage.TYPE_INT_RGB);
        Graphics2D gSmall = smallImage.createGraphics();
        gSmall.drawImage(scaled, 0, 0, null);
        gSmall.dispose();

        // 4. Extraction des valeurs
        int[] pixels = new int[784];
        for (int y = 0; y < 28; y++) {
            for (int x = 0; x < 28; x++) {
                pixels[y * 28 + x] = (smallImage.getRGB(x, y) >> 16) & 0xFF;
            }
        }
        return pixels;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dessin de l'image avec bordure arrondie gérée par le conteneur
        g.drawImage(image, 0, 0, null);
    }
}
