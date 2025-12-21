import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.input.KeyCombination;

public class Main extends Application {

    private MazeGame game;

    @Override
    public void start(Stage stage) {

        game = new MazeGame();
        game.setVisible(false);

        StackPane root = new StackPane();
        root.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        StackPane.setAlignment(game, Pos.CENTER);

        // TITLE SCREEN
        Text title = new Text("MAZE RUNNER");
        title.setFill(Color.WHITE);
        title.setStyle("-fx-font-size: 64px; -fx-font-weight: bold;");

        Button playButton = new Button("Play");
        playButton.setStyle("-fx-font-size: 24px;");

        VBox menu = new VBox(30, title, playButton);
        menu.setAlignment(Pos.CENTER);

        root.getChildren().addAll(game, menu);

        Scene scene = new Scene(root, 750 + game.getWallThickness(), 750 + game.getWallThickness(), Color.BLACK);

        stage.setScene(scene);
        stage.setTitle("Maze Runner");
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        // START/PLAY
        playButton.setOnAction(e -> {
            menu.setVisible(false);
            menu.setManaged(false);

            game.setVisible(true);
            game.startGameLoop();
            game.requestFocus();
        });

        // INPUT
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W, UP -> game.setMoveUp(true);
                case A, LEFT -> game.setMoveLeft(true);
                case S, DOWN -> game.setMoveDown(true);
                case D, RIGHT -> game.setMoveRight(true);
                case E -> game.interact();
                case ESCAPE -> {
                    if (game.isInShop()) {
                        game.toggleShop();
                    }
                } 
                case F11 -> stage.setFullScreen(!stage.isFullScreen());
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W, UP -> game.setMoveUp(false);
                case A, LEFT -> game.setMoveLeft(false);
                case S, DOWN -> game.setMoveDown(false);
                case D, RIGHT -> game.setMoveRight(false);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
