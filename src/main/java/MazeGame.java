import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.image.Image;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MazeGame extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;

    private double playerX = 367.5;
    private double playerY = 367.5;
    private int playerSize = 20;
    private long lastTime = 0;
    private static final double PLAYER_SPEED = 100;

    private int coins = 0;

    private boolean moveUp, moveDown, moveLeft, moveRight;
    public void setMoveUp(boolean value) { moveUp = value; }
    public void setMoveDown(boolean value) { moveDown = value; }
    public void setMoveLeft(boolean value) { moveLeft = value; }
    public void setMoveRight(boolean value) { moveRight = value; }

    private int rows = 15;
    private int cols = 15;
    Cell[][] grid = new Cell[rows][cols];
    private int cellSize = 50;
    private int wallThickness = 5;
    private Image floorTexture;

    public int getWallThickness() { return wallThickness; }

    private Shop shop;
    private boolean inShop = false;
    private boolean canEnterShop = false;

    public void toggleShop() {
        if (inShop) inShop = false;
        else if (canEnterShop) inShop = true;
    }

    public boolean isInShop() { return inShop; }

    private void centerCanvas() {
        double x = (getWidth() - canvas.getWidth()) / 2;
        double y = (getHeight() - canvas.getHeight()) / 2;
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

    public MazeGame() {
        canvas = new Canvas(750 + wallThickness, 750 + wallThickness);
        gc = canvas.getGraphicsContext2D();

        floorTexture = new Image(getClass().getResource("/textures/floor.png").toExternalForm());

        getChildren().add(canvas);

        // Center the canvas within this Pane
        widthProperty().addListener((obs, oldW, newW) -> centerCanvas());
        heightProperty().addListener((obs, oldH, newH) -> centerCanvas());
        centerCanvas();

        initGrid();
        generateMazeDFS(grid[0][0]);
        generateCoins(10);
        placeShop();
    }

    public void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                double deltaSeconds = (now - lastTime) / 1_000_000_000.0;
                lastTime = now;

                update(deltaSeconds);
                render();
            }
        };
        timer.start();
    }

    private void update(double dt) {

        if (inShop) return;

        double distance = PLAYER_SPEED * dt;

        if (moveUp && canMove(0, -distance, playerSize, cellSize)) playerY -= distance;
        if (moveDown && canMove(0, distance, playerSize, cellSize)) playerY += distance;
        if (moveLeft && canMove(-distance, 0, playerSize, cellSize)) playerX -= distance;
        if (moveRight && canMove(distance, 0, playerSize, cellSize)) playerX += distance;

        checkCoinCollisions();
        checkShopCollision();
    }

    private void render() {
        double offsetX = (canvas.getWidth() / 2) - (playerX + playerSize / 2);
        double offsetY = (canvas.getHeight() / 2) - (playerY + playerSize / 2);

        gc.clearRect(0, 0, 750 + wallThickness, 750 + wallThickness);

        drawWorld(offsetX, offsetY);
        drawUI();
    }

    private void drawWorld(double offsetX, double offsetY) {

        drawCellBackground(offsetX, offsetY, 3, 3);
        
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                Cell cell = grid[row][col];
                double x = col * cellSize + wallThickness / 2 + offsetX;
                double y = row * cellSize + wallThickness / 2 + offsetY;

                // Dark gray
                gc.setStroke(Color.rgb(20, 20, 20));
                gc.setLineWidth(wallThickness);

                if(cell.top) gc.strokeLine(x, y, x + cellSize, y);
                if(cell.bottom) gc.strokeLine(x, y + cellSize, x + cellSize, y + cellSize);
                if(cell.left) gc.strokeLine(x, y, x, y + cellSize);
                if(cell.right) gc.strokeLine(x + cellSize, y, x + cellSize, y + cellSize);

                gc.setFill(Color.GOLD);
                if(cell.hasCoin) gc.fillOval(x + cellSize/3, y + cellSize/3, cellSize/3, cellSize/3);
            }
        }

        if (shop != null) {
            double x = shop.col * cellSize + wallThickness / 2 + offsetX;
            double y = shop.row * cellSize + wallThickness / 2 + offsetY;

            shop.draw(gc, x, y, cellSize);
        }

        gc.setFill(Color.RED);
        gc.fillRect(playerX + offsetX, playerY + offsetY, playerSize, playerSize);

        drawFog(gc, canvas.getWidth() / 2, canvas.getHeight() / 2, 50);
    }

    private void drawUI() {

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Verdana", 20));
        gc.fillText("COINS: " + coins, canvas.getWidth() / 2 - 50, canvas.getHeight() - 25);

        if (canEnterShop && !inShop) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Press E to enter", 310, 425);
        }

        if (inShop) {
            drawShopUI();
        }
    }

    private void drawShopUI() {
        gc.setFill(Color.rgb(0, 0, 0, 0.85));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Verdana", 28));
        gc.fillText("SHOP", canvas.getWidth() / 2 - 50, 100);

        gc.setFont(Font.font("Verdana", 18));
        gc.fillText("Press E or ESC to leave", 255, 140);

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

    private void drawFog(GraphicsContext gc, double centerX, double centerY, double radius) {
        RadialGradient gradient = new RadialGradient(
            0, 0,             // focusAngle, focusDistance
            centerX, centerY,  // center X, Y
            radius,            // radius
            false,             // proportional (false because we use pixels)
            CycleMethod.NO_CYCLE,
            new Stop(0, Color.rgb(0, 0, 0, 0)),       // fully transparent in center
            new Stop(1, Color.rgb(0, 0, 0, 1))     // mostly opaque at edges
        );

        gc.setFill(gradient);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void generateCoins(int maxCoins) {
        Random random = new Random();
        int numOfCoins = random.nextInt(maxCoins + 1);    // Random num between 0 and max (coins)
        int row, col;

        for(int i = 0; i < numOfCoins; i++) {
            do {
                row = random.nextInt(15);   // Random num between 0 and 14 (rows & cols)
                col = random.nextInt(15); 
            } while (grid[row][col].hasCoin == true);   // No repeats/overlap

            grid[row][col].hasCoin = true;
        }
    }

    private void checkCoinCollisions() {
        double playerLeft = playerX;
        double playerRight = playerX + playerSize;
        double playerTop = playerY;
        double playerBottom = playerY + playerSize;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                Cell cell = grid[row][col];
                if (!cell.hasCoin) continue;

                // Coin position & radius
                double coinX = col * cellSize + cellSize / 3.0;
                double coinY = row * cellSize + cellSize / 3.0;
                double coinRadius = cellSize / 6.0;

                // Closest point on player to coin center
                double closestX = Math.max(playerLeft, Math.min(coinX + coinRadius, playerRight));
                double closestY = Math.max(playerTop, Math.min(coinY + coinRadius, playerBottom));

                // Distance from coin center
                double dx = (coinX + coinRadius) - closestX;
                double dy = (coinY + coinRadius) - closestY;

                // Coin pickup with efficient formula
                if (dx * dx + dy * dy < coinRadius * coinRadius) {
                    cell.hasCoin = false;
                    coins++;
                }
            }
        }
    }

    private void checkShopCollision() {
        canEnterShop = false;

        if (shop == null) return;

        int playerRow = (int)((playerY + playerSize / 2) / cellSize);
        int playerCol = (int)((playerX + playerSize / 2) / cellSize);

        if (playerRow == shop.row && playerCol == shop.col) {
            canEnterShop = true;
        }
    }

    private void drawCellBackground(double offsetX, double offsetY, int cellsWide, int cellsHigh) {
        double tileWidth = cellSize * cellsWide;
        double tileHeight = cellSize * cellsHigh;

        for(int row = 0; row < rows; row += cellsHigh) {
            for(int col = 0; col < cols; col += cellsWide) {
                double x = col * cellSize + wallThickness / 2 + offsetX;
                double y = row * cellSize + wallThickness / 2 + offsetY;

                gc.drawImage(floorTexture, (int)x, (int)y, (int)tileWidth, (int)tileHeight);
            }
        }
    }

    private void placeShop() {
        Random random = new Random();
        int r, c;

        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (grid[r][c].hasCoin); // avoid coin overlap

        shop = new Shop(r, c);
    }
}
