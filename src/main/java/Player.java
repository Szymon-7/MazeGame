import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Player {

    private double x, y;
    private static final int size = 20;
    private double speed = 75;

    private int coins = 0;
    private int speedLevel = 1;
    private int lanternLevel = 1;
    private int pickaxes = 0;

    private int currentFrame = 0;
    private int lastFrame = 0;
    private double frameTime = 0;

    private Image upSprite;
    private Image downSprite;
    private Image leftSprite;
    private Image rightSprite;
    private Image currentSprite;

    private final double FRAME_DURATION = 0.2;
    private final int FRAME_COUNT = 6;

    private boolean footstep = false;

    public Player() {
        this.upSprite = loadImage("/sprites/moveUp.png");
        this.downSprite = loadImage("/sprites/moveDown.png");
        this.leftSprite = loadImage("/sprites/moveLeft.png");
        this.rightSprite = loadImage("/sprites/moveRight.png");
        this.currentSprite = downSprite;
    }

    // Helper for loading sprites above
    private Image loadImage(String path) {
        return new Image(getClass().getResource(path).toExternalForm());
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public int getSize() { return size; }
    public double getSpeed() { return speed; }

    public int getCoins() { return coins; }
    public void addCoins(int num) { coins += num; }

    public int getLanternLevel() { return lanternLevel; }
    public void upgradeLantern() { lanternLevel++; }
    public int getSpeedLevel() { return speedLevel; }
    public void upgradeSpeed() { speedLevel++; }

    public int getPickaxes() { return pickaxes; }
    public boolean addPickaxe() { 
        if (pickaxes == 0) {
            pickaxes++; 
            return true;
        }
        else return false;
    }

    public void render(GraphicsContext gc, double offsetX, double offsetY) {
        gc.drawImage(
            currentSprite,          // spritesheet for that direction
            currentFrame * 20, 0,   // top-left corner of the frame in the sheet
            20, 20,                 // size of the frame in the sheet
            x + offsetX,            // where to draw on canvas
            y + offsetY,
            size,                   // scale to player size width & height
            size
        );
    }

    public void moveUp(double distance) {
        y -= distance;
        currentSprite = upSprite;
    }

    public void moveDown(double distance) {
        y += distance;
        currentSprite = downSprite;
    }

    public void moveLeft(double distance) {
        x -= distance;
        currentSprite = leftSprite;
    }

    public void moveRight(double distance) {
        x += distance;
        currentSprite = rightSprite;
    }

    public void updateAnimation(double dt, boolean isMoving) {
        footstep = false;
        frameTime += dt;

        if (isMoving) {
            if (currentFrame < 2) {
                currentFrame = 2;
                frameTime = 0;
            }

            if (frameTime >= FRAME_DURATION) {
                lastFrame = currentFrame;
                currentFrame = 2 + ((currentFrame - 2 + 1) % (FRAME_COUNT - 2)); // Sprite frames 2-5 (walk)
                frameTime = 0;

                if ((currentFrame == 2 || currentFrame == 4) && currentFrame != lastFrame) {
                    footstep = true;
                }
            }
        }
        else {
            if (currentFrame > 1) {
                currentFrame = 0;
                frameTime = 0;
            }

            // Idle animation frametime as 3x the walk feels fine
            if (frameTime >= FRAME_DURATION * 3) {
                currentFrame = (currentFrame == 0) ? 1 : 0; // Sprite frame 0-1 (idle)
                frameTime = 0;
            }
        }
    }

    public boolean shouldMakeFootstep() {
        if (footstep) {
            footstep = false;
            return true;
        }

        return false;
    }

    public void pickaxeWall(Maze maze, AudioManager audio) {
        if (pickaxes <= 0) return;

        int row = (int)((y + size / 2) / maze.getCellSize());
        int col = (int)((x + size / 2) / maze.getCellSize());

        Cell[][] grid = maze.getGrid();
        Cell current = grid[row][col];

        // Sprite facing direction with 2 edge cases (if actually in bounds & if wall is actually there)
        if (currentSprite == upSprite && row > 0 && current.top) {
            maze.removeWall(current, grid[row - 1][col]);
            pickaxes--;
            audio.playWallBreak();
        }
        else if (currentSprite == downSprite && row < (maze.getRows() - 1) && current.bottom) {
            maze.removeWall(current, grid[row + 1][col]);
            pickaxes--;
            audio.playWallBreak();
        }
        else if (currentSprite == leftSprite && col > 0 && current.left) {
            maze.removeWall(current, grid[row][col - 1]);
            pickaxes--;
            audio.playWallBreak();
        }
        else if (currentSprite == rightSprite && col < maze.getCols() - 1 && current.right) {
            maze.removeWall(current, grid[row][col + 1]);
            pickaxes--;
            audio.playWallBreak();
        }
    }
}
