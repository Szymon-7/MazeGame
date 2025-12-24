import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.animation.*;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

import java.util.*;

public class MazeGame extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;

    private double playerX;
    private double playerY;
    private int playerSize = 20;
    private long lastTime = 0;
    private double PLAYER_SPEED = 75;
    private int speedLevel = 1;

    private int coins = 0;

    private boolean moveUp, moveDown, moveLeft, moveRight;
    public void setMoveUp(boolean value) { moveUp = value; }
    public void setMoveDown(boolean value) { moveDown = value; }
    public void setMoveLeft(boolean value) { moveLeft = value; }
    public void setMoveRight(boolean value) { moveRight = value; }

    private Image playerUp;
    private Image playerDown;
    private Image playerLeft;
    private Image playerRight;

    private Image currentPlayerSprite;

    private int rows = 3;
    private int cols = 3;
    private int mazeLevel = -1;
    Cell[][] grid;
    private int cellSize = 50;
    private int wallThickness = 5;
    private Image floorTexture;

    public int getWallThickness() { return wallThickness; }

    private Shop shop;
    private boolean inShop = false;
    private boolean canEnterShop = false;

    private boolean paused = false;
    private StackPane pauseOverlay;

    public void interact() {
        if (canExit) {
            resetMaze();
            return;
        }

        if (canEnterShop) toggleShop();
    }

    public void toggleShop() {
        if (inShop) { 
            inShop = false;
            shopOverlay.setVisible(false);
        }
        else if (canEnterShop) { 
            inShop = true;
            shopOverlay.setVisible(true);
        }
    }

    public void togglePause() {
        if (inShop) return;
    
        paused = !paused;
        pauseOverlay.setVisible(paused);
    
        moveUp = moveDown = moveLeft = moveRight = false;
    }

    public boolean isInShop() { return inShop; }

    private StackPane shopOverlay;
    private int lanternLevel = 1;
    private Button buyLanternButton;
    private Button buySpeedButton;

    private Exit exit;
    private boolean canExit = false;

    private Random random = new Random();

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

        playerUp = new Image(getClass().getResource("/sprites/moveUp.png").toExternalForm());
        playerDown = new Image(getClass().getResource("/sprites/moveDown.png").toExternalForm());
        playerLeft = new Image(getClass().getResource("/sprites/moveLeft.png").toExternalForm());
        playerRight = new Image(getClass().getResource("/sprites/moveRight.png").toExternalForm());

        getChildren().add(canvas);
        initShopUI();
        initPauseUI();

        // Center the canvas within this Pane
        widthProperty().addListener((obs, oldW, newW) -> centerCanvas());
        heightProperty().addListener((obs, oldH, newH) -> centerCanvas());
        centerCanvas();

        resetMaze(); 
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

        if (inShop || paused) return;

        double distance = (PLAYER_SPEED + speedLevel * 25) * dt;

        if (moveUp && canMove(0, -distance, playerSize, cellSize)) {
            playerY -= distance;
            currentPlayerSprite = playerUp;
        }
        if (moveDown && canMove(0, distance, playerSize, cellSize)) {
            playerY += distance;
            currentPlayerSprite = playerDown;
        }
        if (moveLeft && canMove(-distance, 0, playerSize, cellSize)) {
            playerX -= distance;
            currentPlayerSprite = playerLeft;
        }
        if (moveRight && canMove(distance, 0, playerSize, cellSize)) {
            playerX += distance;
            currentPlayerSprite = playerRight;
        };

        checkCoinCollisions();
        checkShopCollision();
        checkExitCollision();
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

        if (exit != null) {
            double x = exit.col * cellSize + wallThickness / 2 + offsetX;
            double y = exit.row * cellSize + wallThickness / 2 + offsetY;

            exit.draw(gc, x, y, cellSize);
        }

        gc.drawImage(
            currentPlayerSprite, // full spritesheet Image
            5, 4,                // top-left corner of the frame in the sheet
            14, 16,              // size of the frame in the sheet
            playerX + offsetX,   // where to draw on canvas
            playerY + offsetY,
            playerSize,          // scale to playerSize width
            playerSize           // scale to playerSize height
        );

        drawFog(gc, canvas.getWidth() / 2, canvas.getHeight() / 2, 25 + (lanternLevel * 25));
    }

    private void drawUI() {
        gc.save();

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Verdana", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("COINS: " + coins, canvas.getWidth() / 2, canvas.getHeight() - 25);

        if (canEnterShop && !inShop) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Press E to enter", canvas.getWidth() / 2, 425);
        }

        if (canExit) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Press E to exit maze", canvas.getWidth() / 2, 425);
        }

        gc.restore();
    }

    private void initShopUI() {
        Label title = new Label("SHOP");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Verdana", 32));

        Label exitHint = new Label("Press E or ESC to leave");
        exitHint.setTextFill(Color.LIGHTGRAY);
        exitHint.setFont(Font.font("Verdana", 16));

        buyLanternButton = new Button("Lantern Upgrade (+Vision) - 1 Coin");
        buyLanternButton.setFont(Font.font("Verdana", 18));

        buyLanternButton.setOnAction(e -> {
            if (coins >= 1) {
                coins--;
                lanternLevel++;
            }
        });

        buySpeedButton = new Button("Shoes Upgrade (+Speed) - 1 Coin");
        buySpeedButton.setFont(Font.font("Verdana", 18));

        buySpeedButton.setOnAction(e -> {
            if (coins >= 1) {
                coins--;
                speedLevel++;
            }
        });

        VBox content = new VBox(25, title, exitHint, buyLanternButton, buySpeedButton);
        content.setAlignment(Pos.TOP_CENTER);
        content.setTranslateY(100);

        shopOverlay = new StackPane(content);
        shopOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        shopOverlay.setVisible(false);

        shopOverlay.prefWidthProperty().bind(widthProperty());
        shopOverlay.prefHeightProperty().bind(heightProperty());

        getChildren().add(shopOverlay);
    }

    private void initPauseUI() {
        Label title = new Label("PAUSED");
        title.setTextFill(Color.WHITE);
        title.setFont(Font.font("Verdana", 36));
    
        Button resumeButton = new Button("Resume");
        resumeButton.setFont(Font.font("Verdana", 18));
        resumeButton.setOnAction(e -> togglePause());
    
        Button exitButton = new Button("Exit Game");
        exitButton.setFont(Font.font("Verdana", 18));
        exitButton.setOnAction(e -> { javafx.application.Platform.exit(); });
    
        VBox content = new VBox(30, title, resumeButton, exitButton);
        content.setAlignment(Pos.CENTER);
    
        pauseOverlay = new StackPane(content);
        pauseOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.85);");
        pauseOverlay.setVisible(false);
    
        pauseOverlay.prefWidthProperty().bind(widthProperty());
        pauseOverlay.prefHeightProperty().bind(heightProperty());
    
        getChildren().add(pauseOverlay);
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
        int numOfCoins = random.nextInt(maxCoins + 1);    // Random num between 0 and max (coins)
        int row, col;

        for(int i = 0; i < numOfCoins; i++) {
            do {
                row = random.nextInt(rows);   // Random num between 0 and 14 (rows & cols)
                col = random.nextInt(cols); 
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

    private void checkExitCollision() {
        canExit = false;

        if (exit == null) return;

        int playerRow = (int)((playerY + playerSize / 2) / cellSize);
        int playerCol = (int)((playerX + playerSize / 2) / cellSize);

        if (playerRow == exit.row && playerCol == exit.col) {
            canExit = true;
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
        int r, c;

        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (grid[r][c].hasCoin); // avoid coin overlap

        shop = new Shop(r, c);
        grid[r][c].hasShop = true;
    }

    private void placeExit() {
        int r, c;

        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (grid[r][c].hasCoin || grid[r][c].hasShop);

        exit = new Exit(r, c);
        grid[r][c].hasExit = true;
    }

    public void resetMaze() {
        mazeLevel++;
        rows = 3 + mazeLevel * 6;
        cols = 3 + mazeLevel * 6;
        
        playerX = (cols * cellSize + wallThickness) / 2 - playerSize / 2;
        playerY = (rows * cellSize + wallThickness) / 2 - playerSize / 2;

        grid = new Cell[rows][cols];

        moveUp = moveDown = moveLeft = moveRight = false;

        initGrid();
        generateMazeDFS(grid[0][0]);

        generateCoins((rows * cols) / 6);
        placeShop();
        placeExit();

        inShop = false;
        canEnterShop = false;
        canExit = false;

        shopOverlay.setVisible(false);

        currentPlayerSprite = playerDown;

        lastTime = 0;
    }
}
