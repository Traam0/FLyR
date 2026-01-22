package core.abstraction;

import core.mvvm.View;
import shared.common.MaterialColors;

import javax.swing.*;

public abstract class ViewBase extends JPanel implements View {
    public ViewBase() {
        this.setBackground(MaterialColors.WHITE);
    }
}
