package net.basilcam.gui;

import net.basilcam.core.Meeple;
import net.basilcam.core.Player;
import net.basilcam.core.TurnState;
import net.basilcam.core.api.CarcassonneApi;
import net.basilcam.core.api.CarcassonneHandler;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSectionLocation;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GameFrame extends JFrame implements CarcassonneHandler {
    private static final String[] COLUMN_NAMES = {"player", "meeples left", "score"};
    private static final int PLAYER_COLUMN_INDEX = 0;
    private static final int MEEPLES_COLUMN_INDEX = 1;
    private static final int SCORE_INDEX = 2;
    private static final int BOARD_SIZE = ImageProvider.TILE_SIZE * 22;
    private static final int BOARD_SCROLL_SIZE = 720;
    private static final String NAME = "Carcassonne";

    private CarcassonneApi api;
    private ImageProvider imageProvider;

    private Player player;
    private TurnState turnState;

    private JScrollPane boardScrollPane;
    private JPanel board;
    private JTable table;
    private JLabel currentPlayerMeepleImage;
    private JLabel currentPlayerName;
    private JLabel currentTileImage;
    private JButton rotateTileButton;
    private JButton placeMeepleButton;
    private JButton endTurnButton;

    private Map<Tile, JButton> tiles;

    public GameFrame(CarcassonneApi api) {
        super(NAME);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
//        setResizable(false);

        this.api = api;
        this.imageProvider = new ImageProvider();
        this.tiles = new HashMap<>();

        createBoard();
        this.boardScrollPane = new JScrollPane(this.board);
        this.boardScrollPane.setPreferredSize(new Dimension(BOARD_SCROLL_SIZE, BOARD_SCROLL_SIZE));
        this.boardScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        this.boardScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(this.boardScrollPane);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout());
        createTable(infoPanel);
        createRotateTileButton(infoPanel);
        createPlaceMeepleButton(infoPanel);
        createEndTurnButton(infoPanel);
        createCurrentTileImage(infoPanel);
        createCurrentPlayerPanel(infoPanel);
        add(infoPanel);

        this.api.register(this);

        pack();
        setVisible(true);

        Tile startTile = this.api.getStartTile();
        plotTile(startTile, 0, 0);

        this.api.startGame();
    }

    void centerScrollBars() {
        Rectangle bounds = this.boardScrollPane.getViewportBorderBounds();
        JScrollBar horizontalScrollBar = this.boardScrollPane.getHorizontalScrollBar();
        horizontalScrollBar.setValue((horizontalScrollBar.getMaximum() - (int) bounds.getWidth()) / 2);
        JScrollBar verticalScrollBar = this.boardScrollPane.getVerticalScrollBar();
        verticalScrollBar.setValue((verticalScrollBar.getMaximum() - (int) bounds.getHeight()) / 2);
    }

    @Override
    public void turnStarted(Player player, TurnState turnState) {
        this.player = player;
        this.turnState = turnState;

        this.currentPlayerName.setText(player.getName());
        this.currentPlayerMeepleImage.setIcon(new ImageIcon(this.imageProvider.getMeepleImage(player.getColor())));

        Image image = this.imageProvider.getTileImage(turnState.getTile());
        ImageIcon icon = new ImageIcon(image);
        this.currentTileImage.setIcon(icon);

        this.rotateTileButton.setEnabled(true);
        this.placeMeepleButton.setEnabled(false);
        this.endTurnButton.setEnabled(false);
    }

    @Override
    public void scoreUpdate(Player player) {
        updateTable();
    }

    @Override
    public void gameEnded() {
        JFrame frame = new JFrame("Carcassonne -- Game Over");
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        int highScore = 0;
        Player winner = null;
        for (Player player : this.api.getPlayers()) {
            if (player.getScore() > highScore) {
                 highScore = player.getScore();
                 winner = player;
            }
        }

        if (winner == null) {
            JLabel label = new JLabel("the game had no winners...");
            label.setFont(new Font("Courier New", Font.PLAIN, 30));
            frame.add(label);
        } else {
            JLabel label = new JLabel(player.getName() + " has won the game!");
            label.setFont(new Font("Courier New", Font.PLAIN, 30));
            frame.add(label);
        }

        frame.pack();
        frame.setVisible(true);

        this.dispose();
    }

    private void createBoard() {
        this.board = new JPanel();
        this.board.setPreferredSize(new Dimension(BOARD_SIZE, BOARD_SIZE));
        this.board.setLayout(null);
        this.board.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (GameFrame.this.turnState.hasPlacedTile()) {
                    return;
                }

                Tile tile = GameFrame.this.turnState.getTile();
                int xPosition = convertPixelToXCoordinate(e.getX());
                int yPosition = convertPixelToYCoordinate(e.getY());

                if (!api.placeTile(tile, xPosition, yPosition)) {
                    return;
                }

                plotTile(tile, xPosition, yPosition);

                GameFrame.this.rotateTileButton.setEnabled(false);
                GameFrame.this.placeMeepleButton.setEnabled(true);
                GameFrame.this.endTurnButton.setEnabled(true);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
    }

    private void plotTile(Tile tile, int xPosition, int yPosition) {
        int xPixel = convertXCoordinateToPixel(xPosition);
        int yPixel = convertYCoordinateToPixel(yPosition);

        BufferedImage tileImage = this.imageProvider.getTileImage(tile);
        ImageIcon icon = new ImageIcon(tileImage);

        JButton button = new JButton(icon);
        button.setBounds(xPixel, yPixel, ImageProvider.TILE_SIZE, ImageProvider.TILE_SIZE);
        button.setIcon(icon);

        this.tiles.put(tile, button);
        this.board.add(button);
        this.board.repaint();

        this.endTurnButton.setEnabled(true);
    }

    private void createTable(JPanel panel) {
        this.table = new JTable();
        updateTable();
        this.table.setPreferredScrollableViewportSize(this.table.getPreferredSize());
        this.table.setFillsViewportHeight(true);
        this.table.setRowSelectionAllowed(false);
        this.table.setCellSelectionEnabled(false);

        JScrollPane scrollPane = new JScrollPane(this.table);
        panel.add(scrollPane);
    }

    private void updateTable() {
        Object[][] data = new Object[api.getPlayers().size()][4];
        int i = 0;
        for (Player player : api.getPlayers()) {
            data[i][0] = player.getName();
            data[i][1] = player.getNumberOfUnplacedMeeples();
            data[i][2] = player.getScore();
            i++;
        }

        DefaultTableModel model = (DefaultTableModel) this.table.getModel();
        model.setDataVector(data, COLUMN_NAMES);

        this.table.getColumn(COLUMN_NAMES[PLAYER_COLUMN_INDEX]).setMaxWidth(100);
        this.table.getColumn(COLUMN_NAMES[MEEPLES_COLUMN_INDEX]).setMaxWidth(100);
        this.table.getColumn(COLUMN_NAMES[SCORE_INDEX]).setMaxWidth(100);
    }

    private void createCurrentPlayerPanel(JPanel panel) {
        this.currentPlayerMeepleImage = new JLabel();
        this.currentPlayerMeepleImage.setPreferredSize(new Dimension(50, 50));
        panel.add(this.currentPlayerMeepleImage);

        this.currentPlayerName = new JLabel(getLongestName());
        this.currentPlayerName.setFont(new Font("Courier New", Font.PLAIN, 30));
        panel.add(this.currentPlayerName);
        // todo: truncate name if longer than label size
    }

    private String getLongestName() {
        int maxLength = 0;
        String longestName = "";
        for (Player player : this.api.getPlayers()) {
            if (player.getName().length() > maxLength) {
                maxLength = player.getName().length();
                longestName = player.getName();
            }
        }
        return longestName;
    }

    private void createCurrentTileImage(JPanel panel) {
        this.currentTileImage = new JLabel();
        panel.add(this.currentTileImage);
    }

    private void createRotateTileButton(JPanel panel) {
        this.rotateTileButton = new JButton("rotate tile");
        this.rotateTileButton.setFont(new Font("Courier New", Font.PLAIN, 30));
        this.rotateTileButton.addActionListener(event -> {
            this.turnState.getTile().rotateClockwise();
            Image image = this.imageProvider.getTileImage(this.turnState.getTile());
            ImageIcon icon = new ImageIcon(image);
            this.currentTileImage.setIcon(icon);
        });
        panel.add(this.rotateTileButton);
    }

    private void createPlaceMeepleButton(JPanel panel) {
        this.placeMeepleButton = new JButton("place meeple");
        this.placeMeepleButton.setFont(new Font("Courier New", Font.PLAIN, 30));
        this.placeMeepleButton.addActionListener(event -> {
            Optional<Meeple> maybeMeeple = this.player.getMeeple();
            assert maybeMeeple.isPresent();
            new MeepleFrame(this.api, this.turnState.getTile(), (tileSection, tileSectionLocation) -> {
                this.api.placeMeeple(this.turnState.getTile(), tileSection);
                plotMeeple(this.turnState.getTile(), tileSectionLocation);
                this.placeMeepleButton.setEnabled(false);
                this.endTurnButton.setEnabled(true);
                updateTable();
            });
        });
        panel.add(this.placeMeepleButton);
    }

    private void plotMeeple(Tile tile, TileSectionLocation tileSectionLocation) {
        JButton button = this.tiles.get(tile);
        BufferedImage image = this.imageProvider.getTileImageWithMeeple(tile, tileSectionLocation, this.player.getColor());
        ImageIcon icon = new ImageIcon(image);
        button.setIcon(icon);

        this.board.repaint();
    }

    private void createEndTurnButton(JPanel panel) {
        this.endTurnButton = new JButton("end turn");
        this.endTurnButton.setFont(new Font("Courier New", Font.PLAIN, 30));
        this.endTurnButton.addActionListener(event -> {
            this.api.scoreFeatures();
            this.api.nextTurn();
        });
        panel.add(this.endTurnButton);
    }

    private static int convertXCoordinateToPixel(int xPosition) {
        return BOARD_SIZE / 2 + xPosition * ImageProvider.TILE_SIZE;
    }

    private static int convertYCoordinateToPixel(int yPosition) {
        return BOARD_SIZE / 2 - yPosition * ImageProvider.TILE_SIZE;
    }

    private static int convertPixelToXCoordinate(int xPixel) {
        return (int) Math.floor((xPixel - (BOARD_SIZE / 2.0)) / (double) ImageProvider.TILE_SIZE);
    }

    private static int convertPixelToYCoordinate(int yPixel) {
        return (int) - Math.floor((yPixel - (BOARD_SIZE / 2.0)) / (double) ImageProvider.TILE_SIZE);
    }
}




































