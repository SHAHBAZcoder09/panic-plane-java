final class GameConfig {
    private GameConfig() {}

    // Board
    static final int BOARD_WIDTH = 360;
    static final int BOARD_HEIGHT = 640;
    static final int FPS = 60;
    static final int FRAME_DELAY_MS = 1000 / FPS;

    // Plane
    static final int PLANE_WIDTH = 40;
    static final int PLANE_HEIGHT = 28;
    static final int PLANE_START_X = BOARD_WIDTH / 8;
    static final int PLANE_START_Y = BOARD_WIDTH / 2;
    static final int GRAVITY = 1;
    static final int FLAP_VELOCITY = -9;

    // Buildings
    static final int BUILDING_WIDTH = 64;
    static final int BUILDING_HEIGHT = 512;
    static final int SCROLL_SPEED = 4;
    static final int GAP_HEIGHT = BOARD_HEIGHT / 4;
    static final int GAP_MARGIN = 48;
    static final int SPAWN_INTERVAL_TICKS = 55;
    static final int FIRST_SPAWN_TICKS = 20;
}

