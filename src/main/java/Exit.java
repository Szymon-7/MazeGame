import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Exit {

    public final int row;
    public final int col;
    public final Image exitSprite;

    private int currentFrame = 0;
    private double frameTime = 0;

    private final double FRAME_DURATION = 0.2;
    private final int FRAME_COUNT = 4;

    public Exit(int row, int col) {
        this.row = row;
        this.col = col;
        this.exitSprite = new Image(getClass().getResource("/sprites/hatch.png").toExternalForm());
    }

    public void draw(GraphicsContext gc, double x, double y, int cellSize) {
        gc.drawImage(
            exitSprite,             // spritesheet
            47, currentFrame * 48,  // top-left corner of the frame
            48, 48,                 // size of the frame
            x + cellSize * 0.15,    // where to draw on canvas
            y + cellSize * 0.15,
            cellSize * 0.7,
            cellSize * 0.7
        );
    }

    public void updateAnimation(double dt, boolean canExit) {
        frameTime += dt;

        if (frameTime >= FRAME_DURATION) {
            if (canExit && currentFrame < FRAME_COUNT - 1) {
                currentFrame++;
                frameTime = 0;
            }
            else if (!canExit && currentFrame > 0) {
                currentFrame--;
                frameTime = 0;
            }
        }
    }
}
