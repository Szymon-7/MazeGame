import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Shop {

    public final int row;
    public final int col;

    public Shop(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void draw(GraphicsContext gc, double x, double y, int cellSize) {
        gc.setFill(Color.rgb(0, 150, 255)); // Blue
        gc.fillRect(
            x + cellSize * 0.15,
            y + cellSize * 0.15,
            cellSize * 0.7,
            cellSize * 0.7
        );
    }
}
