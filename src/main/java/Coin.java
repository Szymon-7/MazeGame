import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Coin {
    private Image coinSprite;
    private int currentFrame = 0;
    private double frameTime = 0;
    private double FRAME_DURATION = 0.2;
    private int FRAME_COUNT = 6;

    public Coin() {
        this.coinSprite = new Image(getClass().getResource("/sprites/coin.png").toExternalForm());
    }

    public void render(GraphicsContext gc, double x, double y) {
        gc.drawImage(
            coinSprite,
            currentFrame * 16, 0,
            16, 16,
            x,
            y,
            8, 8
        );
    }

    public void updateAnimation(double dt) {
        frameTime += dt;
        if (frameTime >= FRAME_DURATION) {
            currentFrame = (currentFrame + 1) % FRAME_COUNT;
            frameTime = 0;
        }
    }
}
