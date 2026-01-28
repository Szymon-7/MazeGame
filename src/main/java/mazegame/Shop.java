package mazegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Shop {

    public final int row;
    public final int col;
    public final Image shopSprite;

    public Shop(int row, int col) {
        this.row = row;
        this.col = col;
        this.shopSprite = new Image(getClass().getResource("/sprites/shop.png").toExternalForm());
    }

    public void draw(GraphicsContext gc, double x, double y, int cellSize) {
        gc.drawImage(
            shopSprite,             // spritesheet
            x + cellSize * 0.15,    // where to draw on canvas
            y + cellSize * 0.15,
            cellSize * 0.7,
            cellSize * 0.7
        );
    }
}
