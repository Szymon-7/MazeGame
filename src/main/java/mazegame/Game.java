package mazegame;

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

import java.util.ArrayList;
import java.util.List;

public class Game extends Pane {
    private Canvas canvas;
    private GraphicsContext gc;
    private Maze maze;
    private Player player;
    private Coin coin;
    private List<Enemy> enemies = new ArrayList<>();
    private Renderer renderer;
    private CollisionSystem collision;
    private AudioManager audio;

    private boolean moveUp, moveDown, moveLeft, moveRight;

    private boolean paused = false;
    private boolean isGameOver = false;
    private StackPane pauseOverlay;
    private StackPane gameOverOverlay;
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
    private Button buyHealthButton;
    private Button buyPickaxeButton;
    private Button buyBagButton;
    private Label coinsLabel;

    public Game(double width, double height) {
        maze = new Maze();

        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        player = new Player(true);

        coin = new Coin();

        renderer = new Renderer(this);

        audio = new AudioManager();

        collision = new CollisionSystem(maze, player, audio);

        getChildren().add(canvas);
        initShopUI();
        initPauseUI();
        initGameOverUI();

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
    public List<Enemy> getEnemies() { return enemies; }
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

        buyLanternButton = createShopButton("Lantern " + player.getLanternLevel() + " Upgrade (+Vision)", 5, e -> {
            if (player.getCoins() >= 5) {
                player.addCoins(-5);
                player.upgradeLantern();
                buyLanternButton.setText("Lantern " + player.getLanternLevel() + " Upgrade (+Vision) - 5 Coins");
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        buySpeedButton = createShopButton("Shoes " + player.getSpeedLevel() + " Upgrade (+Speed)", 5, e -> {
            if (player.getCoins() >= 5) {
                player.addCoins(-5);
                player.upgradeSpeed();
                buySpeedButton.setText("Shoes " + player.getSpeedLevel() + " Upgrade (+Speed) - 5 Coins");
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        buyHealthButton = createShopButton("Health " + player.getMaxHealthLevel() + " Upgrade (+Health)", 5, e -> {
            if (player.getCoins() >= 5) {
                player.addCoins(-5);
                player.upgradeMaxHealth();
                buyHealthButton.setText("Health " + player.getMaxHealthLevel() + " Upgrade (+Health) - 5 Coins");
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        buyPickaxeButton = createShopButton("Pickaxes: " + player.getPickaxes() + " (Break walls - 1 use)", 10, e -> {
            if (player.getCoins() >= 10 && player.addPickaxe()) {
                player.addCoins(-10);
                buyPickaxeButton.setText("Pickaxes: " + player.getPickaxes() + " (Break walls - 1 use) - 10 Coins");
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        buyBagButton = createShopButton("Bag " + player.getBagLevel() + " Upgrade (Hold more pickaxes)", 10, e -> {
            if (player.getCoins() >= 10) {
                player.addCoins(-10);
                player.upgradeBag();
                buyBagButton.setText("Bag " + player.getBagLevel() + " Upgrade (Hold more pickaxes) - 10 Coins");
                coinsLabel.setText("Coins: " + player.getCoins());
                audio.playShopBuy();
            }
            else { audio.playShopErr(); }
        });

        VBox content = new VBox(25, title, coinsLabel, exitHint, buyLanternButton, buySpeedButton, buyHealthButton, buyPickaxeButton, buyBagButton);
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

    private void initGameOverUI() {
        Label title = new Label("GAME OVER");
        title.getStyleClass().add("pause-title");

        Label msg = new Label("You died in the depths...");
        msg.getStyleClass().add("maze-level-label");

        Button retryButton = new Button("Return to Level 0");
        retryButton.getStyleClass().add("shop-button");
        retryButton.setOnAction(e -> fullReset());

        Button exitButton = new Button("Quit");
        exitButton.getStyleClass().add("shop-button");
        exitButton.setOnAction(e -> { javafx.application.Platform.exit(); });

        VBox content = new VBox(30, title, msg, retryButton, exitButton);
        content.setAlignment(Pos.CENTER);

        gameOverOverlay = new StackPane(content);
        gameOverOverlay.getStyleClass().add("shop-overlay");
        gameOverOverlay.setVisible(false);

        gameOverOverlay.prefWidthProperty().bind(widthProperty());
        gameOverOverlay.prefHeightProperty().bind(heightProperty());

        getChildren().add(gameOverOverlay);
    }

    public void fullReset() {
        maze.resetLevel();
        player.resetHealth();
        player.resetStats();
        
        refreshShopUI();
        
        isGameOver = false;
        gameOverOverlay.setVisible(false);
        timePlayed = 0;
        
        reset();
        
        audio.playStart();
        playStartFade();
    }

    private void refreshShopUI() {
        coinsLabel.setText("Coins: " + player.getCoins());
        buyLanternButton.setText("Lantern " + player.getLanternLevel() + " Upgrade (+Vision) - 5 Coins");
        buySpeedButton.setText("Shoes " + player.getSpeedLevel() + " Upgrade (+Speed) - 5 Coins");
        buyHealthButton.setText("Health " + player.getMaxHealthLevel() + " Upgrade (+Health) - 5 Coins");
        buyPickaxeButton.setText("Pickaxes: " + player.getPickaxes() + " (Break walls - 1 use) - 10 Coins");
        buyBagButton.setText("Bag " + player.getBagLevel() + " Upgrade (Hold more pickaxes) - 10 Coins");
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

        if (canEnterShop) {
            toggleShop();
        }
    }

    public void reset() {

        maze.resetMaze(true);

        player.setX(maze.getCenter() - player.getSize() / 2);
        player.setY(maze.getCenter() - player.getSize() / 2);
        player.resetHealth();
        moveUp = moveDown = moveLeft = moveRight = false;
        shopOverlay.setVisible(false);

        player.moveDown(0); // Set sprite to down on reset
        mazeLevelLabel.setText(String.format("Maze %d", maze.getMazeLevel()));

        spawnEnemies();

        inShop = false;
        canEnterShop = false;
        canExit = false;

        lastTime = 0;
    }

    private void spawnEnemies() {
        enemies.clear();
        int count = maze.getMazeLevel() + 1; // 1 enemy on lvl 0, 2 on lvl 1, etc.
        
        for (int i = 0; i < count; i++) {
            // Find a random cell that isn't the center (where player starts)
            int r, c;
            do {
                r = (int)(Math.random() * maze.getRows());
                c = (int)(Math.random() * maze.getCols());
            } while (r == maze.getRows()/2 && c == maze.getCols()/2);
            
            enemies.add(new Enemy(c * maze.getCellSize() + 10, r * maze.getCellSize() + 10));
        }
    }

    public void toggleShop() {
        if (inShop) { 
            inShop = false;
            shopOverlay.setVisible(false);
        }
        else if (canEnterShop) { 
            refreshShopUI(); // Update UI before showing
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

        if (inShop || paused || isGameOver) return;

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

        for (Enemy enemy : enemies) {
            enemy.update(dt, collision);
            
            if (collision.checkEnemyCollision(enemy)) {
                // Player hit, take damage and check game over
                player.takeDamage(1);
                if (player.getHealth() <= 0) {
                    isGameOver = true;
                    gameOverOverlay.setVisible(true);
                    return;
                }
            }
        }

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
