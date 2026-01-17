package shared.components;

import shared.common.MaterialColors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.geom.RoundRectangle2D;

public class PasswordInput extends JPasswordField {
    private Color borderColor = MaterialColors.PURPLE_400_ACCENT;
    private Color focusColor = new Color(0, 120, 215);
    private int borderRadius = 8;
    private boolean isHovered = false;

    public PasswordInput() {
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Add focus listener for color changes
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        // Add hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHovered = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Create rounded rectangle background
        RoundRectangle2D roundedRect = new RoundRectangle2D.Double(
                0, 0, getWidth() - 1.0, getHeight() - 1.0,
                borderRadius, borderRadius
        );

        // Draw background
        g2.setColor(getBackground());
        g2.fill(roundedRect);

        // Draw border
        if (isFocusOwner()) {
            g2.setColor(focusColor);
        } else if (isHovered) {
            g2.setColor(borderColor.brighter());
        } else {
            g2.setColor(borderColor);
        }
        g2.draw(roundedRect);

        g2.dispose();
        super.paintComponent(g);
    }

    // Custom properties
    public void setBorderRadius(int borderRadius) {
        this.borderRadius = borderRadius;
        repaint();
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
        repaint();
    }

    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
        repaint();
    }
}