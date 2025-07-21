import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MazeGame extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    Random random = new Random();

    private double playerX = 367.5;
    private double playerY = 367.5;
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

        initGrid();
        generateMazeDFS(grid[0][0]);
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

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, 750 + wallThickness, 750 + wallThickness);

        gc.setFill(Color.RED);
        gc.fillRect(playerX, playerY, playerSize, playerSize);

        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Cell cell = grid[row][col];
                int x = col * cellSize + wallThickness/2;
                int y = row * cellSize + wallThickness/2;

                gc.setStroke(Color.LIMEGREEN);
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
        if (cell.top && top < cellTop + wallThickness - 1 && bottom > cellTop) return true;
        if (cell.bottom && bottom > cellBottom && top < cellBottom) return true;
        if (cell.left && left < cellLeft + wallThickness - 1 && right > cellLeft) return true;
        if (cell.right && right > cellRight && left < cellRight) return true;
        return false;
    }

    private boolean checkCornerCollision(int row, int col, double top, double left, int cellSize) {
        if (row > 0 && col > 0) {
            Cell above = grid[row - 1][col];
            Cell leftCell = grid[row][col - 1];
            Cell corner = grid[row - 1][col - 1];

            boolean topWall = above.bottom;
            boolean leftWall = leftCell.right;
            boolean cornerWall = corner.bottom || corner.right;

            boolean touchingCorner = top < (row * cellSize) + wallThickness - 1 && left < (col * cellSize) + wallThickness - 1;

            return (topWall && leftWall || cornerWall) && touchingCorner;
        }
        return false;
        
    }

    private void initGrid() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }

    private void removeWall(Cell current, Cell next) {
        if (next.row < current.row) {           // Next is above
            current.top = false;
            next.bottom = false;
        } else if (next.row > current.row) {    // Next is below
            current.bottom = false;
            next.top = false;
        } else if (next.col < current.col) {    // Next is left
            current.left = false;
            next.right = false;
        } else if (next.col > current.col) {    // Next is right
            current.right = false;
            next.left = false;
        }
    }

    private List<Cell> getShuffledNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();

        if (cell.row > 0) neighbors.add(grid[cell.row - 1][cell.col]);            // Above neighbor
        if (cell.row < rows - 1) neighbors.add(grid[cell.row + 1][cell.col]);     // Below neighbor
        if (cell.col > 0) neighbors.add(grid[cell.row][cell.col - 1]);            // Left neighbor
        if (cell.col < cols - 1) neighbors.add(grid[cell.row][cell.col + 1]);     // Right neighbor

        Collections.shuffle(neighbors);
        return neighbors;
    }

    private void generateMazeDFS(Cell cell) {
        cell.visited = true;

        for (Cell neighbor : getShuffledNeighbors(cell)) {
            if (!neighbor.visited) {
                removeWall(cell, neighbor);
                generateMazeDFS(neighbor);
            }
        }
    }
}
