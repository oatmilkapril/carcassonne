package net.basilcam.core.api;

import com.google.common.collect.ImmutableList;
import net.basilcam.core.*;
import net.basilcam.core.features.CompositeFeatureManager;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileManager;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.gui.PlayerColor;
import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarcassonneApi {
    public static final int MAX_PLAYERS = PlayerColor.values().length;
    public static final int MIN_PLAYERS = 2;

    private final List<CarcassonneHandler> handlers;

    private final PlayerManager playerManager;
    private final TileManager tileManager;
    private final Board board;
    private final CompositeFeatureManager featureManager;

    private GamePhase gamePhase;
    private TurnState turnState;

    public CarcassonneApi() {
        this(new TileManager());
    }

    @TestOnly
    public CarcassonneApi(TileManager tileManager) {
        this.handlers = new ArrayList<>();
        this.playerManager = new PlayerManager();
        this.tileManager = tileManager;
        this.board = new Board(this.tileManager.getStartTile());
        this.gamePhase = GamePhase.SETUP;
        this.featureManager = new CompositeFeatureManager(this.playerManager, this.tileManager, this.board);
        this.turnState = null;
    }

    public void register(CarcassonneHandler handler) {
        this.handlers.add(handler);
    }

    public Player addPlayer(String name, PlayerColor color) {
        if (this.gamePhase != GamePhase.SETUP) {
            throw new IllegalStateException(ErrorMessages.ADD_PLAYER_WRONG_PHASE);
        }
        if (this.playerManager.getNumberPlayers() >= MAX_PLAYERS) {
            throw new IllegalStateException(ErrorMessages.ADD_PLAYER_TOO_MANY);
        }

        return this.playerManager.addPlayer(name, color);
    }

    public void removePlayer(Player player) {
        if (this.gamePhase != GamePhase.SETUP) {
            throw new IllegalStateException(ErrorMessages.REMOVE_PLAYER_WRONG_PHASE);
        }

        this.playerManager.removePlayer(player);
    }

    public ImmutableList<Player> getPlayers() {
        return this.playerManager.getPlayers();
    }

    public void startGame() {
        if (this.gamePhase != GamePhase.SETUP) {
            throw new IllegalStateException(ErrorMessages.START_GAME_WRONG_PHASE);
        }
        if (this.playerManager.getNumberPlayers() < MIN_PLAYERS || this.playerManager.getNumberPlayers() > MAX_PLAYERS) {
            throw new IllegalStateException(ErrorMessages.START_GAME_WRONG_PLAYER_COUNT);
        }

        this.gamePhase = GamePhase.PLAYING;
        this.turnState = new TurnState(this.tileManager.drawTile());
        this.handlers.forEach(handler -> handler.turnStarted(this.playerManager.getCurrentPlayer(), this.turnState));
    }

    public void nextTurn() {
        if (this.gamePhase != GamePhase.PLAYING) {
            throw new IllegalStateException(ErrorMessages.NEXT_TURN_WRONG_PHASE);
        }
        if (!this.turnState.hasPlacedTile()) {
            throw new IllegalStateException(ErrorMessages.NEXT_TURN_NO_TILE_PLACED);
        }
        if (!this.turnState.hasScored()) {
            throw new IllegalStateException(ErrorMessages.NEXT_TURN_NOT_SCORED);
        }
        if (!this.tileManager.hasMoreTiles()) {
            this.gamePhase = GamePhase.ENDED;
            this.handlers.forEach(CarcassonneHandler::gameEnded);
            return;
        }

        this.turnState = new TurnState(this.tileManager.drawTile());
        this.playerManager.nextTurn();
        this.handlers.forEach(handler -> handler.turnStarted(this.playerManager.getCurrentPlayer(), this.turnState));
    }

    public Tile getStartTile() {
        return this.tileManager.getStartTile();
    }

    public boolean placeTile(Tile tile, int xPosition, int yPosition) {
        if (this.gamePhase != GamePhase.PLAYING) {
            throw new IllegalStateException(ErrorMessages.PLACE_TILE_WRONG_PHASE);
        }
        if (this.turnState.hasPlacedTile()) {
            throw new IllegalStateException(ErrorMessages.PLACE_TILE_ALREADY_PLACED);
        }
        if (!this.turnState.getTile().equals(tile)) {
            throw new IllegalStateException(ErrorMessages.PLACE_TILE_NOT_DRAWN_THIS_TURN);
        }

        if (!PlacementValidator.isValid(this.board, xPosition, yPosition, tile)) {
            return false;
        }

        this.turnState.placedTile();

        this.board.placeTile(tile, xPosition, yPosition);

        this.featureManager.updateFeatures(tile, xPosition, yPosition);

        return true;
    }

    public boolean placeMeeple(final Tile tile, final TileSection tileSection) {
        if (this.gamePhase != GamePhase.PLAYING) {
            throw new IllegalStateException(ErrorMessages.PLACE_MEEPLE_WRONG_PHASE);
        }
        if (!this.turnState.hasPlacedTile() || !this.turnState.getTile().equals(tile)) {
            throw new IllegalStateException(ErrorMessages.PLACE_MEEPLE_NO_TILE);
        }
        if (this.turnState.hasPlacedMeeple()) {
            throw new IllegalStateException(ErrorMessages.PLACE_MEEPLE_ALREADY_PLACED);
        }
        if (this.turnState.hasScored()) {
            throw new IllegalStateException(ErrorMessages.PLACE_MEEPLE_ALREADY_SCORED);
        }

        Optional<Meeple> meeple = this.playerManager.getCurrentPlayer().getMeeple();
        if (meeple.isEmpty()) {
            throw new IllegalStateException(ErrorMessages.PLACE_MEEPLE_NO_MORE);
        }

        if (!this.featureManager.canPlaceMeeple(tile, tileSection)) {
            return false;
        }

        this.turnState.placedMeeple();
        tileSection.placeMeeple(meeple.get());

        return true;
    }

    public boolean canPlaceMeeple(Tile tile, TileSection tileSection) {
        return this.featureManager.canPlaceMeeple(tile, tileSection);
    }

    public void scoreFeatures() {
        if (this.gamePhase != GamePhase.PLAYING) {
            throw new IllegalStateException(ErrorMessages.SCORE_WRONG_PHASE);
        }
        if (!this.turnState.hasPlacedTile()) {
            throw new IllegalStateException(ErrorMessages.SCORE_NO_TILE_PLACED);
        }

        this.featureManager.scoreFeatures();

        this.turnState.scored();

        for (Player player : this.playerManager.getPlayers()) {
            this.handlers.forEach(handler -> handler.scoreUpdate(player));
        }
    }
}
