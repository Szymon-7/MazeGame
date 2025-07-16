import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;

import java.util.Random;

public class MazeGame extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    Random random = new Random();

    private double playerX = random.nextInt(730 - 0 + 1) + 0;
    private double playerY = random.nextInt(730 - 0 + 1) + 0;
    private int playerSize = 20;

    private boolean moveUp, moveDown, moveLeft, moveRight;
    public void setMoveUp(boolean value) { moveUp = value; }
    public void setMoveDown(boolean value) { moveDown = value; }
    public void setMoveLeft(boolean value) { moveLeft = value; }
    public void setMoveRight(boolean value) { moveRight = value; }

    private int rows = 15;
    private int cols = 15;
    Cell[][] grid = new Cell[rows][cols];
    private int cellSize = 50;
    public int wallThickness = 5;

    private void centerCanvas() {
        double x = (getWidth() - canvas.getWidth()) / 2;
        double y = (getHeight() - canvas.getHeight()) / 2;
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }


    public MazeGame() {
        canvas = new Canvas(750 + wallThickness, 750 + wallThickness);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);

        // Center the canvas within this Pane
        widthProperty().addListener((obs, oldW, newW) -> centerCanvas());
        heightProperty().addListener((obs, oldH, newH) -> centerCanvas());
        centerCanvas();

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                grid[row][col] = new Cell();
            }
        }

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                if(row % 2 != 0 && col % 2 != 0) {
                    grid[row][col].top = false;
                    grid[row][col].bottom = false;
                    grid[row][col].left = false;
                    grid[row][col].right = false;

                    if (row > 0) grid[row - 1][col].bottom = false;
                    if (row < rows - 1) grid[row + 1][col].top = false;
                    if (col > 0) grid[row][col - 1].right = false;
                    if (col < cols - 1) grid[row][col + 1].left = false;
                }
            }
        }
    }

    public void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            public void handle(long now) {
                update();
                render();
            }
        };
        timer.start();
    }

    private void update() {
        if(moveUp && canMove(0, -1, playerSize, cellSize)) playerY -= 1;
        if(moveDown && canMove(0, 1, playerSize, cellSize)) playerY += 1;
        if(moveLeft && canMove(-1, 0, playerSize, cellSize)) playerX -= 1;
        if(moveRight && canMove(1, 0, playerSize, cellSize)) playerX += 1;
    }

    private void render() {
        gc.clearRect(0, 0, 750 + wallThickness, 750 + wallThickness);

        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, 750 + wallThickness, 750 + wallThickness);

        gc.setFill(Color.RED);
        gc.fillRect(playerX, playerY, playerSize, playerSize);

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Cell cell = grid[row][col];
                int x = col * cellSize + wallThickness/2;
                int y = row * cellSize + wallThickness/2;

                gc.setStroke(Color.GREEN);
                gc.setLineWidth(wallThickness);

                if(cell.top) gc.strokeLine(x, y, x + cellSize, y);
                if(cell.bottom) gc.strokeLine(x, y + cellSize, x + cellSize, y + cellSize);
                if(cell.left) gc.strokeLine(x, y, x, y + cellSize);
                if(cell.right) gc.strokeLine(x + cellSize, y, x + cellSize, y + cellSize);
            }
        }
    }

    public boolean canMove(double dx, double dy, int playerSize, int cellSize) {
        double nextX = playerX + dx;
        double nextY = playerY + dy;

        double left = nextX;
        double right = nextX + playerSize;
        double top = nextY;
        double bottom = nextY + playerSize;

        int minRow = (int)(top / cellSize);
        int maxRow = (int)((bottom - 0.001) / cellSize);
        int minCol = (int)(left / cellSize);
        int maxCol = (int)((right - 0.001) / cellSize);

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                Cell cell = grid[row][col];

                double cellLeft = col * cellSize;
                double cellRight = cellLeft + cellSize;
                double cellTop = row * cellSize;
                double cellBottom = cellTop + cellSize;

                if (checkWallCollision(cell, top, bottom, left, right, cellTop, cellBottom, cellLeft, cellRight)) {
                    return false;
                }

                if (checkCornerCollision(row, col, top, left, cellSize)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkWallCollision(Cell cell, double top, double bottom, double left, double right,
                                    double cellTop, double cellBottom, double cellLeft, double cellRight) {
        if (cell.top && top < cellTop + wallThickness && bottom > cellTop) return true;
        if (cell.bottom && bottom > cellBottom - 1 && top < cellBottom) return true;
        if (cell.left && left < cellLeft + wallThickness && right > cellLeft) return true;
        if (cell.right && right > cellRight - 1 && left < cellRight) return true;
        return false;
    }

    private boolean checkCornerCollision(int row, int col, double top, double left, int cellSize) {
        if (row > 0 && col > 0) {
            Cell above = grid[row - 1][col];
            Cell leftCell = grid[row][col - 1];
            Cell corner = grid[row - 1][col - 1];

            boolean topWall = above.bottom;
            boolean leftWall = leftCell.right;
            boolean cornerWall = corner.bottom && corner.right;

            boolean touchingCorner = top < (row * cellSize) + wallThickness && left < (col * cellSize) + wallThickness;

            return (topWall && leftWall || cornerWall) && touchingCorner;
        }
        return false;
    }
}
