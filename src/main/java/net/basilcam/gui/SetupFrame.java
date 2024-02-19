package net.basilcam.gui;

import net.basilcam.core.api.CarcassonneApi;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class SetupFrame extends JFrame {
    public static final String NAME = "Carcassonne - Setup";
    private static final String LOGO_FILENAME = "/logo_small.png";
    private Map<PlayerColor, JTextField> playerFields = new HashMap<>();
    private ImageProvider imageProvider = new ImageProvider();

    SetupFrame() {
        super(NAME);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints logoConstraints = new GridBagConstraints();
        logoConstraints.gridwidth = 3;
        logoConstraints.gridx = 0;
        logoConstraints.gridy = 0;
        logoConstraints.fill = GridBagConstraints.HORIZONTAL;
        logoConstraints.anchor = GridBagConstraints.PAGE_START;
        logoConstraints.insets = new Insets(50, 50, 50, 50);
        add(createLogo(), logoConstraints);

        GridBagConstraints enterPlayerConstraints = new GridBagConstraints();
        enterPlayerConstraints.gridx = 0;
        enterPlayerConstraints.gridy = 1;
        enterPlayerConstraints.fill = GridBagConstraints.HORIZONTAL;
        enterPlayerConstraints.anchor = GridBagConstraints.LINE_START;
        enterPlayerConstraints.insets = new Insets(0, 20, 0, 20);
        add(createEnterPlayerLabel(), enterPlayerConstraints);

        GridBagConstraints playerPanelConstraints = new GridBagConstraints();
        playerPanelConstraints.gridx = 0;
        playerPanelConstraints.gridy = 2;
        playerPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        playerPanelConstraints.anchor = GridBagConstraints.LINE_START;
        playerPanelConstraints.insets = new Insets(20, 20, 20, 20);
        add(createPlayerFields(), playerPanelConstraints);

        GridBagConstraints startButtonConstraints = new GridBagConstraints();
        startButtonConstraints.gridx = 0;
        startButtonConstraints.gridy = 3;
        startButtonConstraints.fill = GridBagConstraints.HORIZONTAL;
        startButtonConstraints.anchor = GridBagConstraints.PAGE_END;
        startButtonConstraints.insets = new Insets(10, 10, 10, 10);
        add(createStartButton(), startButtonConstraints);

        pack();
        setVisible(true);
    }

    private JButton createStartButton() {
        JButton button = new JButton("start game");
        button.setFont(new Font("Courier New", Font.PLAIN, 30));
        button.addActionListener(event -> {
            CarcassonneApi api = new CarcassonneApi();

            for (PlayerColor color : PlayerColor.values()) {
                JTextField textField = this.playerFields.get(color);
                if (!textField.getText().isEmpty()) {
                    api.addPlayer(textField.getText(), color);
                }
            }

            this.dispose();
            GameFrame frame = new GameFrame(api);
            frame.centerScrollBars();
        });
        return button;
    }

    private JLabel createLogo() {
        try {
            URL stream = getClass().getResource(LOGO_FILENAME);
            assert stream != null;
            BufferedImage logo = ImageIO.read(stream);
            return new JLabel(new ImageIcon(logo));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private JLabel createEnterPlayerLabel() {
        JLabel label = new JLabel("enter players...");
        label.setFont(new Font("Courier New", Font.PLAIN, 30));
        return label;
    }

    private JPanel createPlayerFields() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        int i = 0;
        for (PlayerColor playerColor : PlayerColor.values()) {
            GridBagConstraints constraints = new GridBagConstraints();
            constraints.gridx = 0;
            constraints.gridy = i;
            constraints.insets = new Insets(0, 0, 10, 0);
            JTextField playerField = new JTextField();
            playerField.setPreferredSize(new Dimension(300, 50));
            playerField.setFont(new Font("Courier New", Font.PLAIN, 25));
            panel.add(playerField, constraints);

            constraints = new GridBagConstraints();
            constraints.gridx = 1;
            constraints.gridy = i;
            constraints.insets = new Insets(0, 10, 10, 0);
            JLabel meeple = new JLabel(new ImageIcon(imageProvider.getMeepleImage(playerColor)));
            panel.add(meeple, constraints);

            this.playerFields.put(playerColor, playerField);

            i++;
        }

        return panel;
    }
}
