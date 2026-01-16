package core.abstraction;

import core.mvvm.View;

public abstract class ViewBase implements View {
    public ViewBase() {
        this.initComponents();
        this.bind();
    }
}
