package shared.layouts;


import java.awt.*;

public class AutoGridLayout implements LayoutManager2 {
    private int cols;
    private Dimension cellSize;
    private int hGap, vGap;

    public AutoGridLayout(int cols, Dimension cellSize, int hGap, int vGap) {
        if (cols <= 0) throw new IllegalArgumentException("number of columns must be strictly positive");
        this.cols = cols;
        this.cellSize = cellSize;
        this.hGap = hGap;
        this.vGap = vGap;
    }


    @Override
    public void addLayoutComponent(Component comp, Object constraints) {

    }

    @Override
    public Dimension maximumLayoutSize(Container target) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0.5f;
    }

    @Override
    public void invalidateLayout(Container target) {

    }

    @Override
    public void addLayoutComponent(String name, Component comp) {

    }

    @Override
    public void removeLayoutComponent(Component comp) {

    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        Insets insets = parent.getInsets();
        int rows = (int) Math.ceil((double) parent.getComponentCount() / this.cols);
        int w = (this.cols * this.cellSize.width) + ((this.cols - 1) * this.hGap) + (insets.left + insets.right);
        int h = (this.cols * this.cellSize.height) + ((rows - 1) * this.vGap) + (insets.top + insets.right);
        return new Dimension(w, h);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return preferredLayoutSize(parent);
    }

    @Override
    public void layoutContainer(Container parent) {
        Insets insets = parent.getInsets();
        int availableWidth = parent.getWidth() - insets.left - insets.right;
        int x = insets.left;
        int y = insets.top;
        for (Component component : parent.getComponents()) {
            if (!component.isVisible()) continue;
            if (x + cellSize.width > availableWidth + insets.left) {
                x = insets.left;
                y += cellSize.height + this.vGap;
            }
            component.setBounds(x, y, this.cellSize.width, this.cellSize.height);
            x += this.cellSize.width + this.hGap;
        }
    }
}