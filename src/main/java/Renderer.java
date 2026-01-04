import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

public class Renderer {

    private final GraphicsContext gc;
    private final Canvas canvas;
    private final Maze maze;
    private final Player player;
    private final Image floorTexture;
    private final Coin coin;

    private final double SCALE = 2.0;

    // UI state (changes every frame via render)
    private boolean canExit;
    private boolean inShop;
    private boolean canEnterShop;

    public Renderer(Game game) {
        this.gc = game.getGc();
        this.canvas = game.getCanvas();
        this.maze = game.getMaze();
        this.player = game.getPlayer();
        this.coin = game.getCoin();
        this.floorTexture = new Image(getClass().getResource("/textures/floor.png").toExternalForm());
    }

    public void render(boolean canExit, boolean inShop, boolean canEnterShop) {
        this.canExit = canExit;
        this.inShop = inShop;
        this.canEnterShop = canEnterShop;

        double offsetX = ((canvas.getWidth() / SCALE) / 2) - (player.getX() + player.getSize() / 2);
        double offsetY = ((canvas.getHeight() / SCALE) / 2) - (player.getY() + player.getSize() / 2);

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.save();
        gc.scale(SCALE, SCALE);

        drawWorld(offsetX, offsetY);
        player.render(gc, offsetX, offsetY);

        gc.restore();

        drawFog(gc, canvas.getWidth() / 2, canvas.getHeight() / 2, 50 + (player.getLanternLevel() * 25));
        drawUI();
    }

    private void drawWorld(double offsetX, double offsetY) {

        drawCellBackground(offsetX, offsetY, 3, 3);

        for(int row = 0; row < maze.getRows(); row++) {
            for(int col = 0; col < maze.getCols(); col++) {
                Cell cell = maze.getGrid()[row][col];
                double x = col * maze.getCellSize() + maze.getWallThickness() / 2 + offsetX;
                double y = row * maze.getCellSize() + maze.getWallThickness() / 2 + offsetY;

                // Dark gray
                gc.setStroke(Color.rgb(20, 20, 20));
                gc.setLineWidth(maze.getWallThickness());

                if(cell.top) gc.strokeLine(x, y, x + maze.getCellSize(), y);
                if(cell.bottom) gc.strokeLine(x, y + maze.getCellSize(), x + maze.getCellSize(), y + maze.getCellSize());
                if(cell.left) gc.strokeLine(x, y, x, y + maze.getCellSize());
                if(cell.right) gc.strokeLine(x + maze.getCellSize(), y, x + maze.getCellSize(), y + maze.getCellSize());

                if (cell.hasCoin) {
                    coin.render(gc, x + (maze.getCellSize() - 8) / 2, y + (maze.getCellSize() - 8) / 2);
                }
            }
        }

        if (maze.getShop() != null) {
            double x = maze.getShop().col * maze.getCellSize() + maze.getWallThickness() / 2 + offsetX;
            double y = maze.getShop().row * maze.getCellSize() + maze.getWallThickness() / 2 + offsetY;

            maze.getShop().draw(gc, x, y, maze.getCellSize());
        }

        if (maze.getExit() != null) {
            double x = maze.getExit().col * maze.getCellSize() + maze.getWallThickness() / 2 + offsetX;
            double y = maze.getExit().row * maze.getCellSize() + maze.getWallThickness() / 2 + offsetY;

            maze.getExit().draw(gc, x, y, maze.getCellSize());
        }
    }

    private void drawCellBackground(double offsetX, double offsetY, int cellsWide, int cellsHigh) {
        double tileWidth = maze.getCellSize() * cellsWide;
        double tileHeight = maze.getCellSize() * cellsHigh;

        for(int row = 0; row < maze.getRows(); row += cellsHigh) {
            for(int col = 0; col < maze.getCols(); col += cellsWide) {
                double x = col * maze.getCellSize() + maze.getWallThickness() / 2 + offsetX;
                double y = row * maze.getCellSize() + maze.getWallThickness() / 2 + offsetY;

                // Casting to int fixes separation lines in the floor
                gc.drawImage(floorTexture, (int)x, (int)y, (int)tileWidth, (int)tileHeight);
            }
        }
    }

    private void drawFog(GraphicsContext gc, double centerX, double centerY, double radius) {
        RadialGradient gradient = new RadialGradient(
            0, 0,              // focusAngle, focusDistance
            centerX, centerY,  // center X, Y
            radius,            // radius
            false,             // proportional (false because we use pixels)
            CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(0, 0, 0, 0)), // fully transparent in center
            new Stop(1, Color.rgb(0, 0, 0, 1))  // mostly opaque at edges
        );

        gc.setFill(gradient);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawUI() {
        gc.save();

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Verdana", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("COINS: " + player.getCoins(), canvas.getWidth() / 2, canvas.getHeight() / 2 + 350);

        if (canEnterShop && !inShop) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Press E to enter shop", canvas.getWidth() / 2, canvas.getHeight() / 2 + 50);
        }

        if (canExit) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Press E to exit maze", canvas.getWidth() / 2, canvas.getHeight() / 2 + 50);
        }

        gc.restore();
    }
}
