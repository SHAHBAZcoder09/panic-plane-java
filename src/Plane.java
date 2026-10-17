import java.awt.Rectangle;

final class Plane {
    int x;
    int y;
    private int velocityY;

    Plane() {
        reset();
    }

    void reset() {
        x = GameConfig.PLANE_START_X;
        y = GameConfig.PLANE_START_Y;
        velocityY = 0;
    }

    void flap() {
        velocityY = GameConfig.FLAP_VELOCITY;
    }

    void update() {
        velocityY += GameConfig.GRAVITY;
        y = Math.max(y + velocityY, 0);
    }

    boolean isBelowScreen() {
        return y > GameConfig.BOARD_HEIGHT;
    }

    Rectangle bounds() {
        return new Rectangle(x, y, GameConfig.PLANE_WIDTH, GameConfig.PLANE_HEIGHT);
    }
}
