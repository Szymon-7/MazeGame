import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Game extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private Maze maze;
    private Player player;
    private Coin coin;
    private Renderer renderer;
    private CollisionSystem collision;
    private AudioManager audio;

    private boolean moveUp, moveDown, moveLeft, moveRight;

    private boolean paused = false;
    private StackPane pauseOverlay;
    private Label mazeLevelLabel;
    private double timePlayed = 0;
    private Label timerLabel;
    private Rectangle fadeOverlay;

    private boolean canExit = false;
    private boolean inShop = false;
    private boolean canEnterShop = false;

    private long lastTime = 0;

    private StackPane shopOverlay;
    private Button buyLanternButton;
    private Button buySpeedButton;
    private Button buyPickaxeButton;
    private Label coinsLabel;

    public Game(double width, double height) {
        maze = new Maze();

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        player = new Player();

        coin = new Coin();

        renderer = new Renderer(this);

        audio = new AudioManager();

        collision = new CollisionSystem(maze, player, audio);

        getChildren().add(canvas);
        initShopUI();
        initPauseUI();

        // Center the canvas within this Pane
        widthProperty().addListener((obs, oldW, newW) -> centerCanvas());
        heightProperty().addListener((obs, oldH, newH) -> centerCanvas());
        centerCanvas();

        fadeOverlay = new Rectangle();
        fadeOverlay.setFill(Color.BLACK);
        fadeOverlay.setOpacity(0);
        fadeOverlay.setMouseTransparent(true);
        fadeOverlay.widthProperty().bind(widthProperty());
        fadeOverlay.heightProperty().bind(heightProperty());
        getChildren().add(fadeOverlay);

        reset();
    }

    public GraphicsContext getGc() { return gc; }
    public Canvas getCanvas() { return canvas; }
    public Maze getMaze() { return maze; }
    public Player getPlayer() { return player; }
    public Coin getCoin() { return coin; }
    public AudioManager getAudio() { return audio; }
    public boolean inShop() { return inShop; }

    public void setMoveUp(boolean value) { moveUp = value; }
    public void setMoveDown(boolean value) { moveDown = value; }
    public void setMoveLeft(boolean value) { moveLeft = value; }
    public void setMoveRight(boolean value) { moveRight = value; }

    private void initShopUI() {
        getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        Label title = new Label("SHOP");
        title.getStyleClass().add("shop-title");

        Label exitHint = new Label("Press E or ESC to leave");
        exitHint.getStyleClass().add("shop-exit-hint");

        coinsLabel = new Label("Coins: " + player.getCoins());
        coinsLabel.getStyleClass().add("coins-label");

        buyLanternButton = createShopButton("Lantern Upgrade (+Vision)", 5, e -> {
            if (player.getCoins() >= 5) {
                player.addCoins(-5);
                player.upgradeLantern();
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        buySpeedButton = createShopButton("Shoes Upgrade (+Speed)", 5, e -> {
            if (player.getCoins() >= 5) {
                player.addCoins(-5);
                player.upgradeSpeed();
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        buyPickaxeButton = createShopButton("Pickaxe (Knock down walls - 1 use)", 10, e -> {
            if (player.getCoins() >= 10 && player.addPickaxe()) {
                player.addCoins(-10);
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        VBox content = new VBox(25, title, coinsLabel, exitHint, buyLanternButton, buySpeedButton, buyPickaxeButton);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("shop-content");

        shopOverlay = new StackPane(content);
        shopOverlay.getStyleClass().add("shop-overlay");
        shopOverlay.setVisible(false);

        shopOverlay.prefWidthProperty().bind(widthProperty());
        shopOverlay.prefHeightProperty().bind(heightProperty());

        getChildren().add(shopOverlay);
    }

    private Button createShopButton(String text, int price, EventHandler<ActionEvent> handler) {
        Button button = new Button(text + " - " + price + " Coins");
        button.getStyleClass().add("shop-button");
        button.setOnAction(handler);
        return button;
    }

    private void initPauseUI() {
        Label title = new Label("PAUSED");
        title.getStyleClass().add("pause-title");

        timerLabel = new Label("Time played: 00:00");
        timerLabel.getStyleClass().add("timer-label");

        mazeLevelLabel = new Label("Maze 0");
        mazeLevelLabel.getStyleClass().add("maze-level-label");

        Button resumeButton = new Button("Resume");
        resumeButton.getStyleClass().add("shop-button");
        resumeButton.setOnAction(e -> togglePause());

        Button exitButton = new Button("Exit Game");
        exitButton.getStyleClass().add("shop-button");
        exitButton.setOnAction(e -> { javafx.application.Platform.exit(); });

        VBox content = new VBox(30, title, timerLabel, mazeLevelLabel, resumeButton, exitButton);
        content.setAlignment(Pos.CENTER);

        pauseOverlay = new StackPane(content);
        pauseOverlay.getStyleClass().add("shop-overlay");
        pauseOverlay.setVisible(false);

        pauseOverlay.prefWidthProperty().bind(widthProperty());
        pauseOverlay.prefHeightProperty().bind(heightProperty());

        getChildren().add(pauseOverlay);
    }

    private void updateTimer() {
        int minutes = (int)(timePlayed / 60);
        int seconds = (int)(timePlayed % 60);
        timerLabel.setText(String.format("Time played: %02d:%02d", minutes, seconds));
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
            audio.playLadder();

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), fadeOverlay);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);

            fadeOut.setOnFinished(e -> {
                reset();

                FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), fadeOverlay);
                fadeIn.setFromValue(1);
                fadeIn.setToValue(0);
                fadeIn.play();
            });

            fadeOut.play();
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
        mazeLevelLabel.setText(String.format("Maze %d", maze.getMazeLevel()));

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

        timePlayed += dt;
        updateTimer();

        player.updateAnimation(dt, isMoving);
        if (player.shouldMakeFootstep()) { audio.playFootstep(); }

        collision.checkCoinCollisions();
        coin.updateAnimation(dt);
        coinsLabel.setText("Coins: " + player.getCoins());

        canExit = collision.isPlayerOnExit();
        maze.getExit().updateAnimation(dt, canExit);

        canEnterShop = collision.isPlayerOnShop();
    }

    public void playStartFade() {
        fadeOverlay.setOpacity(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1.5), fadeOverlay);
        fadeIn.setFromValue(1.0);
        fadeIn.setToValue(0.0);
        fadeIn.play();
    }
}
