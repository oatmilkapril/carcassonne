package net.basilcam.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class StartFrame extends JFrame {
    private static final String NAME = "Carcassonne - Start";
    private static final String LOGO_FILENAME = "/logo.png";

    StartFrame() {
        super(NAME);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints logoConstraints = new GridBagConstraints();
        logoConstraints.gridx = 0;
        logoConstraints.gridy = 0;
        logoConstraints.anchor = GridBagConstraints.PAGE_START;
        logoConstraints.fill = GridBagConstraints.HORIZONTAL;
        logoConstraints.insets = new Insets(10, 20, 10, 20);
        add(createLogo(), logoConstraints);

        GridBagConstraints buttonsConstraints = new GridBagConstraints();
        buttonsConstraints.gridx = 0;
        buttonsConstraints.gridy = 1;
        buttonsConstraints.anchor = GridBagConstraints.PAGE_START;
        buttonsConstraints.fill = GridBagConstraints.HORIZONTAL;
        buttonsConstraints.insets = new Insets(0, 0, 0, 0);
        add(createStartPanel(), buttonsConstraints);

        pack();
        setVisible(true);
    }

    public JLabel createLogo() {
        try {
            URL stream = getClass().getResource(LOGO_FILENAME);
            assert stream != null;
            BufferedImage logo = ImageIO.read(stream);
            return new JLabel(new ImageIcon(logo));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public JPanel createStartPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setLayout(new GridBagLayout());

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.ipadx = 20;
        constraints.ipady = 10;
        constraints.insets = new Insets(10, 0, 0, 0);
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.fill = GridBagConstraints.HORIZONTAL;

        constraints.gridy = 1;
        JButton startButton = new JButton("start game");
        startButton.setFont(new Font("Courier New", Font.PLAIN, 20));
        startButton.addActionListener(event -> startGame());
        panel.add(startButton, constraints);

        constraints.gridy = 2;
        JButton aboutButton = new JButton("about");
        aboutButton.setFont(new Font("Courier New", Font.PLAIN, 20));
        aboutButton.addActionListener(event -> about());
        panel.add(aboutButton, constraints);

        constraints.gridy = 3;
        JButton exitButton = new JButton("exit");
        exitButton.setFont(new Font("Courier New", Font.PLAIN, 20));
        exitButton.addActionListener(event -> exit());
        panel.add(exitButton, constraints);

        return panel;
    }

    private void startGame() {
        this.dispose();
        new SetupFrame();
    }

    private void about() {
        new AboutFrame();
    }

    private void exit() {
        this.dispose();
        System.exit(0);
    }
}
