package net.basilcam.gui;

import javax.swing.*;
import java.awt.*;

public class AboutFrame extends JFrame {
    private static final String NAME = "Carcassonne - About";

    AboutFrame() {
        super(NAME);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());




        pack();
        setVisible(true);
    }
}
