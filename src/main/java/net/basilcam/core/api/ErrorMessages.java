package net.basilcam.core.api;

public enum ErrorMessages {
    ;

    private static final String API_ERROR = "api error: ";

    public static final String ADD_PLAYER_WRONG_PHASE = API_ERROR + "can only add players in setup phase";
    public static final String ADD_PLAYER_TOO_MANY = API_ERROR + "can not add more than " + CarcassonneApi.MAX_PLAYERS
            + " players";
    public static final String ADD_PLAYER_COLOR_USED = API_ERROR + "can not use same color twice";

    public static final String REMOVE_PLAYER_WRONG_PHASE = API_ERROR + "can only remove players in setup phase";

    public static final String START_GAME_WRONG_PHASE = API_ERROR + "can only start game in setup phase";
    public static final String START_GAME_WRONG_PLAYER_COUNT = API_ERROR + "can only start game with 2-5 players";

    public static final String DRAW_TILE_WRONG_PHASE = API_ERROR + "can only draw game in playing phase";
    public static final String DRAW_TILE_NO_TILES = API_ERROR + "no more tiles left";

    public static final String NEXT_TURN_WRONG_PHASE = API_ERROR + "can only take next turn in playing phase";
    public static final String NEXT_TURN_NO_TILE_PLACED = API_ERROR + "must place tile before taking next turn";
    public static final String NEXT_TURN_NOT_SCORED = API_ERROR
            + "must score potential features before taking next turn";

    public static final String PLACE_TILE_WRONG_PHASE = API_ERROR + "can only place tile in playing phase";
    public static final String PLACE_TILE_ALREADY_PLACED = API_ERROR + "can only place 1 tile per turn";
    public static final String PLACE_TILE_NOT_DRAWN_THIS_TURN = API_ERROR + "can only place tile drawn this turn";

    public static final String PLACE_MEEPLE_WRONG_PHASE = API_ERROR + "can only place meeple in playing phase";
    public static final String PLACE_MEEPLE_ALREADY_PLACED = API_ERROR + "can only place 1 meeple per turn";
    public static final String PLACE_MEEPLE_NO_TILE = API_ERROR + "can only place meeple on just placed tile";
    public static final String PLACE_MEEPLE_ALREADY_SCORED = API_ERROR + "can only place meeple before scoring";
    public static final String PLACE_MEEPLE_NO_MORE = API_ERROR + "no available meeples for player";

    public static final String SCORE_WRONG_PHASE = API_ERROR + "can only score features in playing phase";
    public static final String SCORE_NO_TILE_PLACED = API_ERROR + "can only score features after placing tile";

}
