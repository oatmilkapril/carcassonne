package net.basilcam.core.api;

import net.basilcam.core.Player;
import net.basilcam.core.TurnState;

public interface CarcassonneHandler {
    void turnStarted(Player player, TurnState turnState);

    void scoreUpdate(Player player);

    void gameEnded();

}
