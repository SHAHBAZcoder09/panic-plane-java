# Panic Plane

A simple 2D arcade game written in Java this is my first Java game, made as a school project.

You control a little plane and try to fly through the gaps between buildings without crashing. Think of it like Flappy Bird but with buildings instead of pipes.

---

## Gameplay

- Press SPACE to make the plane fly up (flap).
- The plane falls down because of gravity, so keep tapping.
- Fly through the gaps between the top and bottom buildings.
- Each building you pass = +1 score.
- If you hit a building, fly off the top (blocked) or fall below the screen → Game Over.

### Menu
- UP / DOWN arrows to move the selection.
- ENTER to choose what is selected.
- PLAY – start the game.
- SOUND – turn music and sound effects on or off.
- HIGH SCORE – shows your best score so far.
- QUIT – close the game.
- After Game Over, press SPACE to go back to the menu.

---

## How to Run

You need **Java (JDK 8 or newer)** installed. Check with:

```bash
java -version
javac -version
```

### Compile and run
From the project folder:

```bash
javac -d bin src/*.java
java -cp bin App
```

That's it. The window should open and the game starts on the menu screen.

> Tip: If you use an IDE like IntelliJ, Eclipse or VS Code, just open the folder and run `App.java`.

---

## Project Structure

```
Panic Plane/
├── src/                     # All the Java source code
│   ├── App.java             # Program entry point (main method)
│   ├── PanicPlane.java      # Main game class (the whole game loop and drawing)
│   ├── Plane.java           # The player's plane (position + gravity)
│   ├── BuildingPair.java    # One pair of top/bottom buildings (an obstacle)
│   ├── GameConfig.java      # All the game numbers (size, speed, gravity ...)
│   ├── Assets.java          # Loads images
│   ├── Sound.java           # Loads and plays sounds
│   └── assets/
│       ├── images/          # .png pictures (plane, buildings, background)
│       └── sounds/          # .wav sounds (tap, music, game over)
├── bin/                     # Compiled .class files (created when you compile)
└── README.md
```

---

## Java Libraries Used

This game only uses libraries that already come with Java (no extra downloads needed).

| Library | What it is used for |
|---|---|
| `javax.swing` | Builds the window and the game screen (`JFrame`, `JPanel`, `Timer`). |
| `java.awt` | Drawing graphics: shapes, images, colors, fonts (`Graphics`, `Color`, `Font`, `Rectangle`, `Image`). |
| `java.awt.event` | Handling keyboard input (`KeyAdapter`, `KeyEvent`). |
| `java.awt.geom` | Transforms like scaling/moving text (`AffineTransform`). |
| `javax.imageio` | Loading image files (`.png`) from disk. |
| `javax.sound.sampled` | Playing sound files (`.wav`) — `AudioSystem`, `Clip`. |
| `java.util` | Helper classes: `List`, `ArrayList`, `Random`, `Set`, `HashSet`. |
| `java.net` | Finding files inside the project (`URL`). |

---

## How the Code Works (simple version)

- **`App`** – Opens a window (`JFrame`) and puts the game panel inside it.
- **`PanicPlane`** – The heart of the game. It keeps the current state
  (`MENU`, `PLAYING`, `GAME_OVER`), runs a `Timer` as the game loop,
  checks key presses, moves everything, checks collisions, and draws the screen.
- **`Plane`** – Stores the plane's position and velocity. Gravity pulls it down,
  a flap pushes it up.
- **`BuildingPair`** – A top + bottom building with a gap in the middle.
  It scrolls left across the screen and knows if it hit the plane.
- **`GameConfig`** – One place for every game number, so the game is easy to
  tune (change speed, size, gravity etc. without hunting through the code).
- **`Assets`** – Loads images from `src/assets/images`.
- **`Sound`** – Loads and plays sounds from `src/assets/sounds`.

### Java rules / ideas used
- Every class is in its own file (except the inner `State` enum in `PanicPlane`).
- Uses **OOP**: each class has one clear job.
- Uses an **enum** (`State`) to track the game mode instead of messy booleans.
- Uses a **`Timer`** for the game loop (~60 times per second).
- Uses **inheritance**: `PanicPlane extends JPanel` and overrides `paintComponent` to draw.
- Uses **`final`** for constants and things that should not change.

---

## Changing the Game (for your own project)

Almost everything you'd want to tweak is in `src/GameConfig.java`:

| Setting | What it does |
|---|---|
| `BOARD_WIDTH` / `BOARD_HEIGHT` | Size of the game window. |
| `GRAVITY` / `FLAP_VELOCITY` | How fast the plane falls / jumps. |
| `SCROLL_SPEED` | How fast buildings move left. |
| `GAP_HEIGHT` | How tall the gap is (easier/harder). |
| `SPAWN_INTERVAL_TICKS` | How often new buildings appear. |
| `BUILDING_WIDTH` / `BUILDING_HEIGHT` | Size of the buildings. |

Want it easier? Make `GAP_HEIGHT` bigger. Want it harder? Make `SCROLL_SPEED` bigger or `GAP_HEIGHT` smaller.

---

## Notes

- This is a learning project, so the code is kept **simple and readable** on purpose.
- Feel free to download it, use it for your own school project, and change the graphics or numbers to make it your own.
- Made with Java + Swing. Have fun!
