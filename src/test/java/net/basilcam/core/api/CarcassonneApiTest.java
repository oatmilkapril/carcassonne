package net.basilcam.core.api;

import net.basilcam.core.Player;
import net.basilcam.core.tiles.TestTileManager;
import net.basilcam.core.tiles.Tile;
import net.basilcam.core.tiles.TileSection;
import net.basilcam.gui.PlayerColor;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CarcassonneApiTest {
    public CarcassonneApi api;
    public CarcassonneHandler handler = mock(CarcassonneHandler.class);
    public TestTileManager tileManager;

    @BeforeEach
    public void beforeEach() {
        tileManager = new TestTileManager();
        api = new CarcassonneApi(tileManager.getTileManager());
        api.register(handler);
    }

    @Test
    public void shouldNotAddPlayer_tooManyPlayers() {
        for (int i = 0; i < CarcassonneApi.MAX_PLAYERS; i++) {
            api.addPlayer("cam" + i, PlayerColor.values()[i]);
        }

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> api.addPlayer("cam", PlayerColor.BLACK));

        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.ADD_PLAYER_TOO_MANY);
    }

    @Test
    public void shouldNotRemovePlayer_gameIsNotInSetupPhase() {
        api.addPlayer("cam", PlayerColor.RED);
        Player mina = api.addPlayer("mina", PlayerColor.YELLOW); // my cat's name =^._.^=
        api.startGame();

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> api.removePlayer(mina));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.REMOVE_PLAYER_WRONG_PHASE);
    }

    @Test
    public void shouldRemovePlayer() {
        api.addPlayer("cam", PlayerColor.RED);
        Player mina = api.addPlayer("mina", PlayerColor.YELLOW);

        api.removePlayer(mina);
    }

    @Test
    public void shouldNotStartGame_tooFewPlayers() {
        api.addPlayer("cam", PlayerColor.RED);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> api.startGame());
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.START_GAME_WRONG_PLAYER_COUNT);
    }

    @Test
    public void shouldStartGame_validNumberOfPlayers() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();
    }

    @Test
    public void shouldNotTakeTurn_gameIsNotInPlayingPhase() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> api.nextTurn());
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.NEXT_TURN_WRONG_PHASE);
    }

    @Test
    public void shouldCycleThroughTurnForEachPlayer() {
        Player cam = api.addPlayer("cam", PlayerColor.RED);
        Player mina = api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(handler).turnStarted(captor.capture(), any());
        assertThat(captor.getValue()).isEqualTo(cam);

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        assertThat(api.placeTile(tile10, 1, 0)).isTrue();
        api.scoreFeatures();

        api.nextTurn();
        verify(handler, times(2)).turnStarted(captor.capture(), any());
        assertThat(captor.getValue()).isEqualTo(mina);
    }

    @Test
    public void shouldNotPlaceTile_gameNotInPlayingPhase() {
        api.addPlayer("cam", PlayerColor.RED);

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.placeTile(tile10, 1, 0));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.PLACE_TILE_WRONG_PHASE);
    }

    @Test
    public void shouldNotPlaceTile_tileAlreadyPlacedForTurn() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        assertThat(api.placeTile(tile10, 1, 0)).isTrue();

        Tile tile22 = tileManager.drawTileById(22);
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.placeTile(tile22, 2, 0));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.PLACE_TILE_ALREADY_PLACED);
    }

    @Test
    public void shouldNotPlaceTile_tilePlacementInvalid() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        assertThat(api.placeTile(tile10, 1, 0)).isFalse();
    }

    @Test
    public void shouldPlaceTile_previousPlacementInvalid_currentPlacementValid() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        assertThat(api.placeTile(tile10, 1, 0)).isFalse();

        tile10.rotateClockwise();
        assertThat(api.placeTile(tile10, 1, 0)).isTrue();
    }

    @Test
    public void shouldNotPlaceMeeple_gameNotInPlayingPhase() {
        api.addPlayer("cam", PlayerColor.RED);

        Tile tile10 = tileManager.drawTileById(10);
        TileSection tileSection = tile10.getTopSection();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.placeMeeple(tile10, tileSection));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.PLACE_MEEPLE_WRONG_PHASE);
    }

    @Test
    public void shouldNotPlaceMeeple_meepleAlreadyPlacedForTurn() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();

        assertThat(api.placeTile(tile10, 1, 0)).isTrue();

        assertThat(api.placeMeeple(tile10, tile10.getLeftSection())).isTrue();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.placeMeeple(tile10, tile10.getRightSection()));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.PLACE_MEEPLE_ALREADY_PLACED);
    }

    @Test
    public void shouldNotPlaceMeeple_tileNotJustPlaced() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        assertThat(api.placeTile(tile10, 1, 0)).isTrue();

        api.scoreFeatures();
        api.nextTurn();

        Tile tile15 = tileManager.drawTileById(15);
        assertThat(api.placeTile(tile15, 2, 0)).isTrue();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.placeMeeple(tile10, tile10.getLeftSection()));
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.PLACE_MEEPLE_NO_TILE);
    }

    @Test
    public void shouldNotPlaceMeeple_featureAlreadyHasMeeple() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        assertThat(api.placeTile(tile10, 1, 0)).isTrue();
        assertThat(api.placeMeeple(tile10, tile10.getLeftSection())).isTrue();

        api.scoreFeatures();
        api.nextTurn();

        Tile tile15 = tileManager.drawTileById(15);
        assertThat(api.placeTile(tile15, 2, 0)).isTrue();

        assertThat(api.placeMeeple(tile15, tile15.getLeftSection())).isFalse();
    }

    @Test
    public void shouldPlaceMeeple() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile10 = tileManager.drawTileById(10);
        tile10.rotateClockwise();
        assertThat(api.placeTile(tile10, 1, 0)).isTrue();
        assertThat(api.placeMeeple(tile10, tile10.getLeftSection())).isTrue();
    }

    @Test
    public void shouldNotScoreFeatures_gameNotInPlayingPhase() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.scoreFeatures());
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.SCORE_WRONG_PHASE);
    }

    @Test
    public void shouldNotScoreFeatures_tileHasNotBeenPlacedThisTurn() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> api.scoreFeatures());
        assertThat(exception.getMessage()).isEqualTo(ErrorMessages.SCORE_NO_TILE_PLACED);
    }

    @Test
    public void shouldScoreFeatures_noMeeplePlaced() {
        api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();

        Tile tile20 = tileManager.drawTileById(20);
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        assertThat(api.placeTile(tile20, 0, 1)).isTrue();

        api.scoreFeatures();
        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(this.handler, times(2)).scoreUpdate(captor.capture());
        List<Player> players = captor.getAllValues();
        assertPlayerScore(players, "cam", 0);
        assertPlayerScore(players, "mina", 0);
    }

    @Test
    public void shouldScoreFeatures_meeplePlaced() {
        Player cam = api.addPlayer("cam", PlayerColor.RED);
        api.addPlayer("mina", PlayerColor.YELLOW);
        api.startGame();
        verify(handler).turnStarted(eq(cam), any());

        Tile tile20 = tileManager.drawTileById(20);
        tile20.rotateClockwise();
        tile20.rotateClockwise();
        assertThat(api.placeTile(tile20, 0, 1)).isTrue();
        api.placeMeeple(tile20, tile20.getBottomSection());

        api.scoreFeatures();
        ArgumentCaptor<Player> captor = ArgumentCaptor.forClass(Player.class);
        verify(this.handler, times(2)).scoreUpdate(captor.capture());
        List<Player> players = captor.getAllValues();
        assertPlayerScore(players, "cam", 4);
        assertPlayerScore(players, "mina", 0);
    }

    private void assertPlayerScore(List<Player> players, String name, int score) {
        assertThat(players).areExactly(1, new Condition<>(
                player -> player.getName().equals(name) && player.getScore() == score,
                ""));
    }
}