import java.awt.Rectangle;
import java.util.Random;

final class BuildingPair {
    int x = GameConfig.BOARD_WIDTH;
    final int gapTop;
    boolean passed;

    BuildingPair(int gapTop) {
        this.gapTop = gapTop;
    }

    static BuildingPair random(Random random) {
        int min = GameConfig.GAP_MARGIN;
        int max = GameConfig.BOARD_HEIGHT - GameConfig.GAP_HEIGHT - GameConfig.GAP_MARGIN;
        return new BuildingPair(min + random.nextInt(max - min + 1));
    }

    int gapBottom() {
        return gapTop + GameConfig.GAP_HEIGHT;
    }

    void update() {
        x -= GameConfig.SCROLL_SPEED;
    }

    boolean isPassedBy(Plane plane) {
        return plane.x > x + GameConfig.BUILDING_WIDTH;
    }

    boolean isOffScreen() {
        return x + GameConfig.BUILDING_WIDTH < 0;
    }

    boolean collidesWith(Rectangle r) {
        return r.intersects(bounds(0, gapTop))
                || r.intersects(bounds(gapBottom(), GameConfig.BOARD_HEIGHT - gapBottom()));
    }

    private Rectangle bounds(int y, int h) {
        return new Rectangle(x, y, GameConfig.BUILDING_WIDTH, h);
    }
}
