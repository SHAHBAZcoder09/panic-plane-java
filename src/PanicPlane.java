import static java.awt.event.KeyEvent.VK_SPACE;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.Timer;

public class PanicPlane extends JPanel {
    private enum State { MENU, PLAYING, GAME_OVER }

    private static final Font TITLE_FONT = new Font("Impact", Font.PLAIN, 58);
    private static final Font FONT = new Font("Impact", Font.PLAIN, 40);
    private static final Font SMALL_FONT = new Font("Impact", Font.PLAIN, 22);

    private static final int TEXT_ANIM_FRAMES = 18;
    private static final int MENU_COUNT = 4;
    private static final int MENU_START_Y = 360;
    private static final int MENU_ITEM_HEIGHT = 62;
    private static final Color MENU_SELECTED = Color.YELLOW;
    private static final Color MENU_NORMAL = Color.WHITE;
    private static final Color MENU_HINT = Color.LIGHT_GRAY;

    private final Image backgroundImg = Assets.image("Background.png");
    private final Image planeImg = Assets.image("PanicPlane.png");
    private final Image topBuildingImg = Assets.image("TopBuilding.png");
    private final Image bottomBuildingImg = Assets.image("BottomBuilding.png");
    private final Sound tapSound = Sound.load("TapTap.wav");
    private final Sound music = Sound.load("music.wav");
    private final Sound gameOverSound = Sound.load("gameover.wav");

    private final Plane plane = new Plane();
    private final List<BuildingPair> buildings = new ArrayList<>();
    private final List<BuildingPair> menuBuildings = new ArrayList<>();
    private final Random random = new Random();
    private final Set<Integer> pressedKeys = new HashSet<>();
    private final Timer gameLoop = new Timer(GameConfig.FRAME_DELAY_MS, e -> tick());
    private final Timer animLoop = new Timer(GameConfig.FRAME_DELAY_MS, e -> animTick());

    private State state = State.MENU;
    private int score;
    private int highScore;
    private int spawnCountdown = GameConfig.FIRST_SPAWN_TICKS;
    private int menuSpawnCountdown;
    private int menuSelection;
    private int animFrame;
    private boolean soundOn = true;

    public PanicPlane() {
        setPreferredSize(new Dimension(GameConfig.BOARD_WIDTH, GameConfig.BOARD_HEIGHT));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
        music.loop();
        animLoop.start();
    }

    private void animTick() {
        if (state == State.PLAYING) {
            return;
        }
        if (state == State.MENU) {
            updateMenuBackground();
        }
        animFrame++;
        repaint();
    }

    private void updateMenuBackground() {
        if (--menuSpawnCountdown <= 0) {
            menuBuildings.add(BuildingPair.random(random));
            menuSpawnCountdown = GameConfig.SPAWN_INTERVAL_TICKS;
        }
        menuBuildings.removeIf(BuildingPair::isOffScreen);
        menuBuildings.forEach(BuildingPair::update);

        BuildingPair next = menuBuildings.stream()
                .filter(p -> p.x + GameConfig.BUILDING_WIDTH > plane.x)
                .min((a, b) -> Integer.compare(a.x, b.x))
                .orElse(null);
        int targetY = next == null ? GameConfig.BOARD_HEIGHT / 2 : next.gapTop + GameConfig.GAP_HEIGHT / 2;
        int desired = targetY - GameConfig.PLANE_HEIGHT / 2;
        plane.y += Math.signum(desired - plane.y) * Math.min(3, Math.abs(desired - plane.y));
    }

    private void onKeyPressed(KeyEvent e) {
        if (!pressedKeys.add(e.getKeyCode())) {
            return;
        }
        int key = e.getKeyCode();
        switch (state) {
            case MENU -> handleMenuKey(key);
            case PLAYING -> {
                if (key == VK_SPACE) {
                    flap();
                }
            }
            case GAME_OVER -> {
                if (key == VK_SPACE) {
                    enterMenu();
                }
            }
        }
    }

    private void handleMenuKey(int key) {
        switch (key) {
            case KeyEvent.VK_UP -> menuSelection = (menuSelection + MENU_COUNT - 1) % MENU_COUNT;
            case KeyEvent.VK_DOWN -> menuSelection = (menuSelection + 1) % MENU_COUNT;
            case KeyEvent.VK_ENTER -> confirmMenu();
            default -> {
                return;
            }
        }
        repaint();
    }

    private void confirmMenu() {
        switch (menuSelection) {
            case 0 -> startGame();
            case 1 -> toggleSound();
            case 3 -> System.exit(0);
        }
    }

    private void toggleSound() {
        soundOn = !soundOn;
        if (soundOn) {
            music.loop();
        } else {
            music.stop();
        }
        repaint();
    }

    private void startGame() {
        buildings.clear();
        menuBuildings.clear();
        plane.reset();
        score = 0;
        spawnCountdown = GameConfig.FIRST_SPAWN_TICKS;
        state = State.PLAYING;
        if (soundOn) {
            music.loop();
        }
        gameLoop.start();
    }

    private void enterMenu() {
        state = State.MENU;
        animFrame = 0;
        menuSpawnCountdown = 0;
        plane.reset();
        menuBuildings.clear();
        if (soundOn) {
            music.loop();
        }
        repaint();
    }

    private void flap() {
        plane.flap();
        if (soundOn) {
            tapSound.play();
        }
    }

    private void endGame() {
        state = State.GAME_OVER;
        animFrame = 0;
        highScore = Math.max(highScore, score);
        gameLoop.stop();
        if (soundOn) {
            music.stop();
            gameOverSound.play();
        }
        repaint();
    }

    private void tick() {
        if (state != State.PLAYING) {
            return;
        }
        update();
        repaint();
    }

    private void update() {
        plane.update();

        if (--spawnCountdown <= 0) {
            buildings.add(BuildingPair.random(random));
            spawnCountdown = GameConfig.SPAWN_INTERVAL_TICKS;
        }

        for (BuildingPair pair : buildings) {
            pair.update();
            if (!pair.passed && pair.isPassedBy(plane)) {
                pair.passed = true;
                score++;
            }
            if (pair.collidesWith(plane.bounds())) {
                endGame();
                return;
            }
        }
        buildings.removeIf(BuildingPair::isOffScreen);

        if (plane.isBelowScreen()) {
            endGame();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImg, 0, 0, GameConfig.BOARD_WIDTH, GameConfig.BOARD_HEIGHT, null);
        g.setFont(FONT);

        switch (state) {
            case MENU -> drawMenu(g);
            case PLAYING -> drawPlaying(g);
            case GAME_OVER -> drawGameOver(g);
        }
    }

    private void drawMenu(Graphics g) {
        menuBuildings.forEach(pair -> drawPair(g, pair));
        g.drawImage(planeImg, plane.x, plane.y, GameConfig.PLANE_WIDTH, GameConfig.PLANE_HEIGHT, null);
        drawAnimatedCentered(g, "PANIC PLANE", TITLE_FONT, 190, Color.WHITE);

        for (int i = 0; i < MENU_COUNT; i++) {
            Color color = i == menuSelection ? MENU_SELECTED : MENU_NORMAL;
            drawAnimatedCentered(g, menuLabel(i), FONT, MENU_START_Y + i * MENU_ITEM_HEIGHT,
                    color, i == menuSelection, 0.06f);
        }
    }

    private String menuLabel(int index) {
        return switch (index) {
            case 0 -> "PLAY";
            case 1 -> "SOUND: " + (soundOn ? "ON" : "OFF");
            case 2 -> "HIGH SCORE: " + highScore;
            case 3 -> "QUIT";
            default -> "";
        };
    }

    private void drawPlaying(Graphics g) {
        drawWorld(g);
        drawCentered(g, "Score: " + score, 45, Color.WHITE);
    }

    private void drawGameOver(Graphics g) {
        drawWorld(g);
        drawAnimatedCentered(g, "GAME OVER", TITLE_FONT, 240, Color.WHITE, true, 0.05f);
        drawAnimatedCentered(g, "Score: " + score, FONT, 300, Color.WHITE);
        drawAnimatedCentered(g, "High Score: " + highScore, FONT, 350, Color.WHITE);
        drawAnimatedCentered(g, "Press SPACE for Menu", SMALL_FONT, 420, MENU_HINT, true, 0.05f);
    }

    private void drawWorld(Graphics g) {
        g.drawImage(planeImg, plane.x, plane.y, GameConfig.PLANE_WIDTH, GameConfig.PLANE_HEIGHT, null);
        buildings.forEach(pair -> drawPair(g, pair));
    }

    private void drawPair(Graphics g, BuildingPair pair) {
        g.drawImage(topBuildingImg, pair.x, pair.gapTop - GameConfig.BUILDING_HEIGHT,
                GameConfig.BUILDING_WIDTH, GameConfig.BUILDING_HEIGHT, null);
        g.drawImage(bottomBuildingImg, pair.x, pair.gapBottom(),
                GameConfig.BUILDING_WIDTH,
                GameConfig.BOARD_HEIGHT - pair.gapBottom() + GameConfig.BUILDING_HEIGHT, null);
    }

    private void drawCentered(Graphics g, String text, int baselineY, Color color) {
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, (GameConfig.BOARD_WIDTH - fm.stringWidth(text)) / 2, baselineY);
    }

    private void drawAnimatedCentered(Graphics g, String text, Font font, int baselineY, Color color) {
        drawAnimatedCentered(g, text, font, baselineY, color, false, 0f);
    }

    private void drawAnimatedCentered(Graphics g, String text, Font font, int baselineY,
            Color color, boolean pulse, float pulseAmount) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setFont(font);

        FontMetrics fm = g2.getFontMetrics(font);
        int textWidth = fm.stringWidth(text);

        float t = Math.min(1f, animFrame / (float) TEXT_ANIM_FRAMES);
        float ease = 1f - (1f - t) * (1f - t);
        float scale = 0.4f + 0.6f * ease;
        int alpha = (int) (255 * Math.min(1f, t * 1.5f));

        float phase = animFrame * 0.15f;
        if (t >= 1f && pulse) {
            scale *= 1f + pulseAmount * (float) Math.sin(phase);
        }

        float cx = GameConfig.BOARD_WIDTH / 2f;
        float cy = baselineY - fm.getAscent() / 2f;

        AffineTransform old = g2.getTransform();
        g2.translate(cx, cy + (int) (4 * Math.sin(phase)));
        g2.scale(scale, scale);
        g2.translate(-cx, -cy);
        g2.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(),
                Math.max(0, Math.min(255, alpha))));
        g2.drawString(text, cx - textWidth / 2f, baselineY);
        g2.setTransform(old);
        g2.dispose();
    }
}