package shared.layouts;

import shared.common.FlexAlignment;

import javax.swing.*;
import java.awt.*;

public class FlexPanelH extends JPanel {

    private FlexAlignment alignment = FlexAlignment.LEFT;
    private int gap = 0;

    public FlexPanelH() {
        setLayout(new HorizontalFlexLayout());
    }

    public FlexPanelH(int gap) {
        this();
        this.setGap(gap);
    }

    public FlexPanelH(FlexAlignment alignment) {
        this();
        this.setAlignment(alignment);
    }

    public FlexPanelH(int gap, FlexAlignment alignment) {
        this();
        this.setGap(gap);
        this.setAlignment(alignment);
    }

    public void setAlignment(FlexAlignment alignment) {
        this.alignment = alignment;
        revalidate();
        repaint();
    }

    public void setGap(int gap) {
        this.gap = gap;
        revalidate();
        repaint();
    }

    public int getGap() {
        return this.gap;
    }

    @Override
    public Component add(Component comp) {
        super.add(comp);
        revalidate();
        repaint();
        return comp;
    }

    @Override
    public void remove(Component comp) {
        super.remove(comp);
        revalidate();
        repaint();
    }

    private class HorizontalFlexLayout implements LayoutManager {
        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return calculateSize(parent, Component::getPreferredSize);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return calculateSize(parent, Component::getMinimumSize);
        }

        private Dimension calculateSize(Container parent,
                                        java.util.function.Function<Component, Dimension> sizeGetter) {
            int width = 0;
            int height = 0;
            Component[] components = parent.getComponents();

            for (Component comp : components) {
                Dimension d = sizeGetter.apply(comp);
                width += d.width;
                height = Math.max(height, d.height);
            }

            if (components.length > 0) {
                width += (components.length - 1) * gap;
            }

            Insets insets = parent.getInsets();
            return new Dimension(
                    width + insets.left + insets.right,
                    height + insets.top + insets.bottom
            );
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int maxWidth = parent.getWidth() - (insets.left + insets.right);
                int maxHeight = parent.getHeight() - (insets.top + insets.bottom);
                int y = insets.top;

                Component[] components = parent.getComponents();
                int numComponents = components.length;
                if (numComponents == 0) return;

                int[] widths = new int[numComponents];
                int totalWidth = 0;
                for (int i = 0; i < numComponents; i++) {
                    widths[i] = components[i].getPreferredSize().width;
                    totalWidth += widths[i];
                }

                int x;
                switch (alignment) {
                    case LEFT:
                        x = insets.left;
                        for (int i = 0; i < numComponents; i++) {
                            Component comp = components[i];
                            int height = Math.min(comp.getPreferredSize().height, maxHeight);
                            comp.setBounds(x, y, widths[i], height);
                            x += widths[i] + gap;
                        }
                        break;

                    case CENTER:
                        int contentWidth = totalWidth + (numComponents - 1) * gap;
                        x = insets.left + (maxWidth - contentWidth) / 2;
                        for (int i = 0; i < numComponents; i++) {
                            Component comp = components[i];
                            int height = Math.min(comp.getPreferredSize().height, maxHeight);
                            comp.setBounds(x, y, widths[i], height);
                            x += widths[i] + gap;
                        }
                        break;

                    case RIGHT:
                        contentWidth = totalWidth + (numComponents - 1) * gap;
                        x = insets.left + maxWidth - contentWidth;
                        for (int i = 0; i < numComponents; i++) {
                            Component comp = components[i];
                            int height = Math.min(comp.getPreferredSize().height, maxHeight);
                            comp.setBounds(x, y, widths[i], height);
                            x += widths[i] + gap;
                        }
                        break;

                    case EVEN:
                        int totalContentWidth = totalWidth + (numComponents - 1) * gap;
                        int availableSpace = maxWidth - totalContentWidth;
                        if (availableSpace < 0) availableSpace = 0;
                        int pad = (numComponents + 1) > 0 ? availableSpace / (numComponents + 1) : 0;
                        x = insets.left + pad;
                        for (int i = 0; i < numComponents; i++) {
                            Component comp = components[i];
                            int height = Math.min(comp.getPreferredSize().height, maxHeight);
                            comp.setBounds(x, y, widths[i], height);
                            x += widths[i] + gap;
                            if (i != numComponents - 1) {
                                x += pad;
                            }
                        }
                        break;

                    case BETWEEN:
                        if (numComponents == 1) {
                            components[0].setBounds(insets.left, y, widths[0],
                                    Math.min(components[0].getPreferredSize().height, maxHeight));
                        } else {
                            totalContentWidth = totalWidth + (numComponents - 1) * gap;
                            availableSpace = maxWidth - totalContentWidth;
                            if (availableSpace < 0) availableSpace = 0;
                            int spacePerGap = availableSpace / (numComponents - 1);
                            x = insets.left;
                            for (int i = 0; i < numComponents; i++) {
                                Component comp = components[i];
                                int height = Math.min(comp.getPreferredSize().height, maxHeight);
                                comp.setBounds(x, y, widths[i], height);
                                if (i != numComponents - 1) {
                                    x += widths[i] + gap + spacePerGap;
                                }
                            }
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + alignment);
                }
            }
        }
    }
}
