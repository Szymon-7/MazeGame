import javafx.scene.canvas.*;
import javafx.scene.paint.*;
import javafx.scene.image.*;
import javafx.scene.text.*;
import javafx.animation.*;

import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;

public class Game extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;

    private Maze maze;
    private Player player;
    private CollisionSystem collision;

    private long lastTime = 0;

    private boolean moveUp, moveDown, moveLeft, moveRight;
    public void setMoveUp(boolean value) { moveUp = value; }
    public void setMoveDown(boolean value) { moveDown = value; }
    public void setMoveLeft(boolean value) { moveLeft = value; }
    public void setMoveRight(boolean value) { moveRight = value; }

    private Image floorTexture;

    private boolean paused = false;
    private StackPane pauseOverlay;

    private boolean canExit = false;
    private boolean inShop = false;
    private boolean canEnterShop = false;

    public Maze getMaze() { return maze; }
    public boolean inShop() { return inShop; }

    public void interact() {
        if (canExit) {
            reset();
            return;
        }

        if (canEnterShop) toggleShop();
    }

    public void reset() {

        maze.resetMaze();

        player.setX(maze.getCenter() - player.getSize() / 2);
        player.setY(maze.getCenter() - player.getSize() / 2);
        moveUp = moveDown = moveLeft = moveRight = false;
        shopOverlay.setVisible(false);

        player.moveDown(0); // Set sprite to down on reset

        inShop = false;
        canEnterShop = false;
        canExit = false;

        lastTime = 0;
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

    private StackPane shopOverlay;
    private Button buyLanternButton;
    private Button buySpeedButton;

    private void centerCanvas() {
        double x = (getWidth() - canvas.getWidth()) / 2;
        double y = (getHeight() - canvas.getHeight()) / 2;
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

    public Game() {
        maze = new Maze();

        canvas = new Canvas(750 + maze.getWallThickness(), 750 + maze.getWallThickness());
        gc = canvas.getGraphicsContext2D();

        floorTexture = new Image(getClass().getResource("/textures/floor.png").toExternalForm());

        player = new Player();

        collision = new CollisionSystem(maze, player);

        getChildren().add(canvas);
        initShopUI();
        initPauseUI();

        // Center the canvas within this Pane
        widthProperty().addListener((obs, oldW, newW) -> centerCanvas());
        heightProperty().addListener((obs, oldH, newH) -> centerCanvas());
        centerCanvas();

        reset();
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

        double distance = (player.getSpeed() + player.getSpeedLevel() * 25) * dt;
        boolean isMoving = false;

        if (moveUp && collision.canMove(0, -distance)) {
            player.moveUp(distance);
            isMoving = true;
        }
        if (moveDown && collision.canMove(0, distance)) {
            player.moveDown(distance);
            isMoving = true;
        }
        if (moveLeft && collision.canMove(-distance, 0)) {
            player.moveLeft(distance);
            isMoving = true;
        }
        if (moveRight && collision.canMove(distance, 0)) {
            player.moveRight(distance);
            isMoving = true;
        }

        player.updateAnimation(dt, isMoving);

        collision.checkCoinCollisions();
        canEnterShop = collision.isPlayerOnShop();
        canExit = collision.isPlayerOnExit();
    }

    private void render() {
        double offsetX = (canvas.getWidth() / 2) - (player.getX() + player.getSize() / 2);
        double offsetY = (canvas.getHeight() / 2) - (player.getY() + player.getSize() / 2);

        gc.clearRect(0, 0, 750 + maze.getWallThickness(), 750 + maze.getWallThickness());

        drawWorld(offsetX, offsetY);
        player.render(gc, offsetX, offsetY);
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

                gc.setFill(Color.GOLD);
                if(cell.hasCoin) gc.fillOval(x + maze.getCellSize() / 3, y + maze.getCellSize() / 3, maze.getCellSize() / 3, maze.getCellSize() / 3);
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

        drawFog(gc, canvas.getWidth() / 2, canvas.getHeight() / 2, 25 + (player.getLanternLevel() * 25));
    }

    private void drawUI() {
        gc.save();

        gc.setFill(Color.GOLD);
        gc.setFont(Font.font("Verdana", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("COINS: " + player.getCoins(), canvas.getWidth() / 2, canvas.getHeight() - 25);

        if (canEnterShop && !inShop) {
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Verdana", 16));
            gc.fillText("Press E to enter shop", canvas.getWidth() / 2, 425);
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
            if (player.getCoins() >= 1) {
                player.addCoins(-1);
                player.upgradeLantern();
            }
        });

        buySpeedButton = new Button("Shoes Upgrade (+Speed) - 1 Coin");
        buySpeedButton.setFont(Font.font("Verdana", 18));

        buySpeedButton.setOnAction(e -> {
            if (player.getCoins() >= 1) {
                player.addCoins(-1);
                player.upgradeSpeed();
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

    private void drawCellBackground(double offsetX, double offsetY, int cellsWide, int cellsHigh) {
        double tileWidth = maze.getCellSize() * cellsWide;
        double tileHeight = maze.getCellSize() * cellsHigh;

        for(int row = 0; row < maze.getRows(); row += cellsHigh) {
            for(int col = 0; col < maze.getCols(); col += cellsWide) {
                double x = col * maze.getCellSize() + maze.getWallThickness() / 2 + offsetX;
                double y = row * maze.getCellSize() + maze.getWallThickness() / 2 + offsetY;

                gc.drawImage(floorTexture, (int)x, (int)y, (int)tileWidth, (int)tileHeight);
            }
        }
    }
}
