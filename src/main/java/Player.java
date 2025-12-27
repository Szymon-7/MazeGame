import javafx.scene.image.Image;
import javafx.scene.canvas.GraphicsContext;

public class Player {

    private double x, y;
    private static final int size = 30;
    private double speed = 75;

    private int coins = 0;
    private int speedLevel = 1;
    private int lanternLevel = 1;

    private int currentFrame = 0;
    private double frameTime = 0;

    private Image upSprite;
    private Image downSprite;
    private Image leftSprite;
    private Image rightSprite;
    private Image currentSprite;

    private final double FRAME_DURATION = 0.2;
    private final int FRAME_COUNT = 4;

    public Player(double x, double y) {
        this.x = x;
        this.y = y;

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
        if (isMoving) {
            frameTime += dt;
            if (frameTime >= FRAME_DURATION) {
                currentFrame = (currentFrame + 1) % FRAME_COUNT; // sprite frames 0-3
                frameTime = 0;
            }
        } else {
            currentFrame = 0; // idle
            frameTime = 0;
        }
    }
}
