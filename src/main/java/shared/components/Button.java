package shared.components;

import shared.adapters.HoverMouseAdapter;

import javax.swing.*;
import java.awt.*;

public class Button extends JButton {
    private int radius;

    public Button(String text) {
        super(text);
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.radius = 20;
        this.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        this.addMouseListener(new HoverMouseAdapter());
    }

    public Button(String text, Color background) {
        this(text);
        this.setBackground(background);
    }

    public Button(String text, int px, int py) {
        this(text);
        this.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    }

    public Button(String text, Color background, int px, int py) {
        this(text, background);
        this.setBorder(BorderFactory.createEmptyBorder(py, px, py, px));
    }

    public Button(String text, Color background, Color foreground, int px, int py) {
        this(text, background, px, py);
        this.setForeground(foreground);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(this.getBackground());
        g2d.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), this.radius, this.radius);

        g2d.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
    }

    @Override
    public boolean contains(int x, int y) {
        return new java.awt.geom.RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), this.radius, this.radius).contains(x, y);
    }

    public void setRadius(int value) {
        if (value == this.radius) return;
        this.radius = value;
    }
}
