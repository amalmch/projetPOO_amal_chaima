import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * ModernUIComponents
 * 
 * Centralise les éléments graphiques stylisés (boutons, panels, couleurs)
 * pour une interface professionnelle et créative.
 */
public class ModernUIComponents {

    // ── Palette de couleurs premium ──────────────────────────────────────────────
    public static final Color COLOR_BG         = new Color(13, 17, 28);    // Deep navy
    public static final Color COLOR_CARD       = new Color(22, 28, 45);    // Card background
    public static final Color COLOR_CARD_LIGHT = new Color(30, 38, 58);    // Lighter card
    public static final Color COLOR_ACCENT     = new Color(99, 102, 241);  // Indigo 500
    public static final Color COLOR_ACCENT2    = new Color(56, 189, 248);  // Sky 400
    public static final Color COLOR_SUCCESS    = new Color(52, 211, 153);  // Emerald 400
    public static final Color COLOR_WARN       = new Color(251, 191, 36);  // Amber 400
    public static final Color COLOR_TEXT       = new Color(241, 245, 249); // Slate 100
    public static final Color COLOR_MUTED      = new Color(148, 163, 184); // Slate 400
    public static final Color COLOR_BORDER     = new Color(51, 65, 85);    // Slate 700

    // Gradient colors
    public static final Color GRAD_START = new Color(99, 102, 241);   // Indigo
    public static final Color GRAD_END   = new Color(56, 189, 248);   // Cyan

    /**
     * Panneau avec coins arrondis, bordure élégante et ombre subtile.
     */
    public static class RoundedPanel extends JPanel {
        private int radius;
        private boolean hasGlow;
        private Color glowColor;

        public RoundedPanel(int radius) {
            this(radius, false, null);
        }

        public RoundedPanel(int radius, boolean hasGlow, Color glowColor) {
            this.radius = radius;
            this.hasGlow = hasGlow;
            this.glowColor = glowColor;
            setOpaque(false);
            setBackground(COLOR_CARD);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Glow effect (outer shadow)
            if (hasGlow && glowColor != null) {
                for (int i = 4; i > 0; i--) {
                    g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(), glowColor.getBlue(), 8 * i));
                    g2.fill(new RoundRectangle2D.Double(-i, -i, getWidth() + 2*i, getHeight() + 2*i, radius + i, radius + i));
                }
            }

            // Main fill
            g2.setColor(getBackground());
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

            // Subtle top highlight (glassmorphism feel)
            GradientPaint topShine = new GradientPaint(0, 0, new Color(255, 255, 255, 12), 0, getHeight() / 3, new Color(255, 255, 255, 0));
            g2.setPaint(topShine);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), radius, radius));

            // Border
            g2.setColor(COLOR_BORDER);
            g2.setStroke(new BasicStroke(1));
            g2.draw(new RoundRectangle2D.Double(0, 0, getWidth()-1, getHeight()-1, radius, radius));
            g2.dispose();
        }
    }

    /**
     * Bouton animé avec gradient, transition de couleur fluide, et effet press.
     */
    public static class AnimatedButton extends JButton {
        private float hoverAlpha = 0.0f;
        private Timer timer;
        private boolean primary;

        public AnimatedButton(String text, boolean primary) {
            super(text);
            this.primary = primary;
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setForeground(COLOR_TEXT);
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(140, 44));

            timer = new Timer(15, e -> {
                if (getModel().isRollover()) {
                    hoverAlpha = Math.min(1.0f, hoverAlpha + 0.1f);
                } else {
                    hoverAlpha = Math.max(0.0f, hoverAlpha - 0.1f);
                }
                repaint();
                if (!getModel().isRollover() && hoverAlpha <= 0) timer.stop();
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { timer.start(); }
                @Override
                public void mouseExited(MouseEvent e)  { /* timer continues until alpha=0 */ }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth(), h = getHeight();
            int arc = 12;

            if (primary) {
                // Gradient fill for primary
                GradientPaint gp = new GradientPaint(0, 0, GRAD_START, w, 0, GRAD_END);
                g2.setPaint(gp);
            } else {
                g2.setColor(COLOR_CARD_LIGHT);
            }
            g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));

            // Non-primary border
            if (!primary) {
                g2.setColor(COLOR_BORDER);
                g2.setStroke(new BasicStroke(1));
                g2.draw(new RoundRectangle2D.Double(0, 0, w-1, h-1, arc, arc));
            }

            // Hover glow
            if (hoverAlpha > 0) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, hoverAlpha * 0.25f));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
            }

            // Press effect
            if (getModel().isPressed()) {
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.15f));
                g2.setColor(Color.BLACK);
                g2.fill(new RoundRectangle2D.Double(0, 0, w, h, arc, arc));
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Barre de probabilité premium avec gradient, label intégré et animation fluide.
     * Supporte un mode "winner" avec highlight.
     */
    public static class ProbabilityBar extends JComponent {
        private double value = 0;
        private double displayValue = 0;
        private String label;
        private String digitEmoji;
        private boolean isWinner = false;
        private Timer animTimer;
        private Color barColorStart;
        private Color barColorEnd;

        public ProbabilityBar(String label, String digitEmoji, Color colorStart, Color colorEnd) {
            this.label = label;
            this.digitEmoji = digitEmoji;
            this.barColorStart = colorStart;
            this.barColorEnd = colorEnd;
            setPreferredSize(new Dimension(280, 56));
            animTimer = new Timer(15, e -> {
                if (Math.abs(displayValue - value) > 0.005) {
                    displayValue += (value - displayValue) * 0.12;
                    repaint();
                } else {
                    displayValue = value;
                    repaint();
                    animTimer.stop();
                }
            });
        }

        public void setValue(double val) {
            this.value = val;
            animTimer.start();
        }

        public void setWinner(boolean winner) {
            this.isWinner = winner;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int w = getWidth();
            int h = getHeight();
            int barH = 14;
            int barY = h - barH - 4;

            // ── Digit emoji + label ──
            g2.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
            g2.setColor(isWinner ? COLOR_TEXT : COLOR_MUTED);
            g2.drawString(digitEmoji, 2, 22);

            g2.setFont(new Font("Segoe UI", isWinner ? Font.BOLD : Font.PLAIN, 14));
            g2.setColor(isWinner ? COLOR_TEXT : COLOR_MUTED);
            g2.drawString(label, 28, 21);

            // ── Percentage ──
            String pctStr = String.format("%.1f%%", displayValue * 100);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            FontMetrics fm = g2.getFontMetrics();
            int pctW = fm.stringWidth(pctStr);
            g2.setColor(isWinner ? barColorEnd : COLOR_MUTED);
            g2.drawString(pctStr, w - pctW - 4, 21);

            // ── Background bar ──
            g2.setColor(new Color(40, 50, 70));
            g2.fill(new RoundRectangle2D.Double(0, barY, w, barH, barH, barH));

            // ── Foreground bar with gradient ──
            int barW = (int)(w * displayValue);
            if (barW > 2) {
                GradientPaint gp = new GradientPaint(0, barY, barColorStart, barW, barY, barColorEnd);
                g2.setPaint(gp);
                g2.fill(new RoundRectangle2D.Double(0, barY, barW, barH, barH, barH));

                // Shine on top of bar
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, barY, barW, barH / 2, barH, barH));
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            // ── Winner glow ──
            if (isWinner && barW > 2) {
                for (int i = 3; i > 0; i--) {
                    g2.setColor(new Color(barColorEnd.getRed(), barColorEnd.getGreen(), barColorEnd.getBlue(), 15 * i));
                    g2.fill(new RoundRectangle2D.Double(-i, barY - i, barW + 2*i, barH + 2*i, barH + i, barH + i));
                }
            }

            g2.dispose();
        }
    }

    /**
     * Indicateur de statut animé (petit point coloré avec pulse).
     */
    public static class StatusIndicator extends JComponent {
        private Color statusColor = COLOR_WARN;
        private float pulse = 0f;
        private boolean pulsing = true;
        private Timer pulseTimer;

        public StatusIndicator() {
            setPreferredSize(new Dimension(16, 16));
            pulseTimer = new Timer(40, e -> {
                if (pulsing) {
                    pulse += 0.08f;
                    if (pulse > 2 * Math.PI) pulse = 0;
                    repaint();
                }
            });
            pulseTimer.start();
        }

        public void setStatus(Color color, boolean animate) {
            this.statusColor = color;
            this.pulsing = animate;
            if (!animate) pulse = 0;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cx = getWidth() / 2;
            int cy = getHeight() / 2;
            int r = 5;

            // Pulse ring
            if (pulsing) {
                float alpha = (float)(0.3 + 0.2 * Math.sin(pulse));
                int pr = (int)(r + 3 + 2 * Math.sin(pulse));
                g2.setColor(new Color(statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue(), (int)(alpha * 255)));
                g2.fillOval(cx - pr, cy - pr, pr * 2, pr * 2);
            }

            // Core dot
            g2.setColor(statusColor);
            g2.fillOval(cx - r, cy - r, r * 2, r * 2);

            // Inner highlight
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillOval(cx - r + 1, cy - r + 1, r, r);

            g2.dispose();
        }
    }
}
