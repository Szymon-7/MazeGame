import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MazeGame game = new MazeGame();
        StackPane root = new StackPane();
        root.getChildren().add(game);
        StackPane.setAlignment(game, Pos.CENTER);

        Scene scene = new Scene(root, 750 + game.getWallThickness(), 750 + game.getWallThickness());
        scene.setFill(Color.BLACK);
        stage.setScene(scene);
        stage.setTitle("Maze Runner");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();

        game.startGameLoop(); // starts AnimationTimer or input handling

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W, UP -> game.setMoveUp(true);
                case A, LEFT -> game.setMoveLeft(true);
                case S, DOWN -> game.setMoveDown(true);
                case D, RIGHT -> game.setMoveRight(true);
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
