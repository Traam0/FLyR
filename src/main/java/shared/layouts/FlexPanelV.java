package shared.layouts;

import shared.common.FlexAlignment;
import shared.common.MaterialColors;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;

public class FlexPanelV extends JPanel {
    private FlexAlignment alignment;
    private int gap;
    private final Map<Component, Integer> factors;


    public FlexPanelV() {
        this.alignment = FlexAlignment.TOP;
        this.gap = 0;
        this.factors = new HashMap<>();
        super.setLayout(new VerticalFlexLayout());
        this.setBackground(MaterialColors.WHITE);
    }

    public FlexPanelV(int gap) {
        this();
        this.gap = gap;
    }

    public FlexPanelV(int gap, FlexAlignment alignment) {
        this(gap);
        this.alignment = alignment;
    }

    public FlexPanelV(int gap, FlexAlignment alignment, Color background) {
        this(gap, alignment);
        this.setBackground(background);
    }

    public int getGap() {
        return this.gap;
    }

    public FlexAlignment getAlignment() {
        return this.alignment;
    }

    public void setGap(int value) {
        this.gap = value;
        this.revalidate();
        this.repaint();
    }

    public void setAlignment(FlexAlignment value) {
        this.alignment = value;
        this.revalidate();
        this.repaint();
    }

    public void setGrow(Component comp, int factor) {
        this.factors.put(comp, Math.max(factor, 0));
        revalidate();
        repaint();
    }

    @Override
    public void setLayout(LayoutManager mgr) {
        return;
    }

    //
    private class VerticalFlexLayout implements LayoutManager {
        @Override
        public void addLayoutComponent(String name, Component comp) {

        }

        @Override
        public void removeLayoutComponent(Component comp) {

        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return this.calculateSize(parent, Component::getPreferredSize);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return this.calculateSize(parent, Component::getMinimumSize);
        }

        @Override
        public void layoutContainer(Container parent) {
            synchronized (parent.getTreeLock()) {
                Insets insets = parent.getInsets();
                int mw = parent.getWidth() - (insets.left + insets.right);
                int mh = parent.getHeight() - (insets.top + insets.bottom);
                int x = insets.left;
                int y;

                Component[] components = parent.getComponents();
                if (components.length == 0) return;
                int[] hs = new int[components.length];
                int tGrow = 0;
                int tBaseHeight = 0;

                for (int i = 0; i < components.length; i++) {
                    hs[i] = components[i].getPreferredSize().height;
                    tBaseHeight += hs[i];
                    tGrow += FlexPanelV.this.factors.getOrDefault(components[i], 0);
                }

                int tGap = (components.length - 1) * FlexPanelV.this.gap;
                int remainingSpace = mh - (tBaseHeight + tGap);
                if (remainingSpace > 0 && tGap > 0) {
                    for (int i = 0; i < components.length; i++) {
                        int grow = FlexPanelV.this.factors.getOrDefault(components[i], 0);
                        if (grow == 0) continue;
//                        int extra =
                        hs[i] += (remainingSpace * grow) / tGrow; // += extra
                    }
                }

                int th = 0;
                th = IntStream.of(hs).reduce(0, Integer::sum);
                th += tGap;

                switch (alignment) {
                    case TOP:
                        y = insets.top;
                        for (int i = 0; i < components.length; i++) {
                            Component component = components[i];
                            component.setBounds(x, y, mw, hs[i]);
                            y += hs[i] + gap;
                        }
                        break;
                    case CENTER:
                        int contentHeight = th + (components.length - 1) * gap;
                        y = insets.top + (mh - contentHeight) / 2;
                        for (int i = 0; i < components.length; i++) {
                            Component comp = components[i];
                            comp.setBounds(x, y, mw, hs[i]);
                            y += hs[i] + gap;
                        }
                        break;
                    case BOTTOM:
                        contentHeight = th + (components.length - 1) * gap;
                        y = insets.top + mh - contentHeight;
                        for (int i = 0; i < components.length; i++) {
                            Component component = components[i];
                            component.setBounds(x, y, mw, hs[i]);
                            y += hs[i] + gap;
                        }
                        break;
                    case EVEN:
                        int totalContentHeight = th + (components.length - 1) * gap;
                        int availableSpace = mh - totalContentHeight;
                        if (availableSpace < 0) availableSpace = 0;
                        int pad = (components.length + 1) > 0 ? availableSpace / (components.length + 1) : 0;
                        y = insets.top + pad;
                        for (int i = 0; i < components.length; i++) {
                            Component comp = components[i];
                            comp.setBounds(x, y, mw, hs[i]);
                            y += hs[i] + gap;
                            if (i != components.length - 1) {
                                y += pad;
                            }
                        }
                        break;
                    case BETWEEN:
                        if (components.length == 1) {
                            components[0].setBounds(x, insets.top, mw, hs[0]);
                        } else {
                            totalContentHeight = th + (components.length - 1) * gap;
                            availableSpace = mh - totalContentHeight;
                            if (availableSpace < 0) availableSpace = 0;
                            int spacePerGap = availableSpace / (components.length - 1);
                            y = insets.top;
                            for (int i = 0; i < components.length; i++) {
                                Component comp = components[i];
                                comp.setBounds(x, y, mw, hs[i]);
                                if (i != components.length - 1) {
                                    y += hs[i] + gap + spacePerGap;
                                }
                            }
                        }
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + alignment);
                }

            }
        }

        private Dimension calculateSize(Container parent, Function<Component, Dimension> factory) {
            int w = 0;
            int h = 0;
            Component[] components = parent.getComponents();
            for (Component component : components) {
                Dimension dimension = factory.apply(component);
                w = Math.max(w, dimension.width);
                h += dimension.height;
            }

            if (components.length > 1) h += (components.length - 1) * FlexPanelV.this.gap;
            Insets insets = parent.getInsets();

            return new Dimension(
                    w + insets.left + insets.right,
                    h + insets.top + insets.bottom
            );
        }
    }
}