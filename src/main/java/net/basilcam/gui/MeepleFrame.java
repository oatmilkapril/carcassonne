package net.basilcam.gui;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import net.basilcam.core.Meeple;
import net.basilcam.core.api.CarcassonneApi;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.core.tiles.TileSectionLocation;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class MeepleFrame extends JFrame {
    private static final String NAME = "Carcassonne";

    private CarcassonneApi api;
    private Tile tile;

    private TileSection selectedSection;
    private TileSectionLocation selectedSectionLocation;
    private JButton confirmButton;
    private JButton cancelButton;


    public MeepleFrame(CarcassonneApi api, Tile tile, BiConsumer<TileSection, TileSectionLocation> me) {
        super(NAME);
        this.api = api;
        this.tile = tile;

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(TileSectionLocation.values().length, 1));
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "select meeple location"));

        ButtonGroup buttons = new ButtonGroup();
        for (Map.Entry<TileSectionLocation, TileSection> entry : getSupportedSections().entries()) {
            if (api.canPlaceMeeple(tile, entry.getValue())) {
                JRadioButton button = new JRadioButton(entry.getKey().name().toLowerCase() + ", " + entry.getValue().getType());
                button.addActionListener(event -> {
                    selectedSectionLocation = entry.getKey();
                    selectedSection = entry.getValue();
                    confirmButton.setEnabled(true);
                });
                buttons.add(button);
                panel.add(button);
            }
        }

        JPanel buttonPanel = new JPanel();
        this.confirmButton = new JButton("confirm");
        this.confirmButton.addActionListener(event -> {
            me.accept(this.selectedSection, this.selectedSectionLocation);
            this.dispose();
        });
        this.confirmButton.setEnabled(false);
        buttonPanel.add(this.confirmButton);
        this.cancelButton = new JButton("cancel");
        this.cancelButton.addActionListener(event -> {
            this.dispose();
        });
        buttonPanel.add(this.cancelButton);
        panel.add(buttonPanel);

        add(panel);
        pack();
        setVisible(true);
    }

    public Multimap<TileSectionLocation, TileSection> getSupportedSections() {
        Multimap<TileSectionLocation, TileSection> sections = tile.getSections();
        Multimap<TileSectionLocation, TileSection> supportedSections = ArrayListMultimap.create();

        for (Map.Entry<TileSectionLocation, TileSection> entry : sections.entries()) {
            if (Meeple.SUPPORTED_TYPES.contains(entry.getValue().getType())) {
                supportedSections.put(entry.getKey(), entry.getValue());
            }
        }

        return supportedSections;
    }
}
