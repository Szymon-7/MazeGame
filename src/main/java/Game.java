import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class Game extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private Maze maze;
    private Player player;
    private Renderer renderer;
    private CollisionSystem collision;
    private AudioManager audio;

    private boolean moveUp, moveDown, moveLeft, moveRight;

    private boolean paused = false;
    private StackPane pauseOverlay;

    private boolean canExit = false;
    private boolean inShop = false;
    private boolean canEnterShop = false;

    private long lastTime = 0;

    private StackPane shopOverlay;
    private Button buyLanternButton;
    private Button buySpeedButton;

    public Game(double width, double height) {
        maze = new Maze();

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();

        player = new Player();

        renderer = new Renderer(gc, canvas, maze, player);

        audio = new AudioManager();

        collision = new CollisionSystem(maze, player, audio);

        getChildren().add(canvas);
        initShopUI();
        initPauseUI();

        // Center the canvas within this Pane
        widthProperty().addListener((obs, oldW, newW) -> centerCanvas());
        heightProperty().addListener((obs, oldH, newH) -> centerCanvas());
        centerCanvas();

        reset();
    }

    public Maze getMaze() { return maze; }
    public boolean inShop() { return inShop; }

    public void setMoveUp(boolean value) { moveUp = value; }
    public void setMoveDown(boolean value) { moveDown = value; }
    public void setMoveLeft(boolean value) { moveLeft = value; }
    public void setMoveRight(boolean value) { moveRight = value; }

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
        content.setAlignment(Pos.CENTER);
        content.setTranslateY(-375);

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

    public void togglePause() {
        if (inShop) return;

        paused = !paused;
        pauseOverlay.setVisible(paused);

        moveUp = moveDown = moveLeft = moveRight = false;

        if (paused) audio.pauseBackground();
        else audio.playBackground();
    }

    private void centerCanvas() {
        double x = (getWidth() - canvas.getWidth()) / 2;
        double y = (getHeight() - canvas.getHeight()) / 2;
        canvas.setLayoutX(x);
        canvas.setLayoutY(y);
    }

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

    public void startGameLoop() {
        audio.playBackground();

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
                renderer.render(canExit, inShop, canEnterShop);
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
        if (player.shouldMakeFootstep()) { audio.playFootstep(); }

        collision.checkCoinCollisions();
        canEnterShop = collision.isPlayerOnShop();
        canExit = collision.isPlayerOnExit();
    }
}
