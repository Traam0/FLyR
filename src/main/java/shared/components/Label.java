package shared.components;

import javax.swing.*;
import java.awt.*;

public class Label extends JLabel {

    public Label(String text) {
        super(text);
    }


    public Label(String text, Color foregroundColor) {
        this(text);
        this.setForeground(foregroundColor);
    }
}
