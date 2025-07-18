import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MazeGame game = new MazeGame();
        StackPane root = new StackPane();
        root.getChildren().add(game);
        StackPane.setAlignment(game, Pos.CENTER);

        Scene scene = new Scene(root, 750 + game.wallThickness, 750 + game.wallThickness);
        stage.setScene(scene);
        stage.setTitle("Maze Runner");
        stage.setFullScreen(true);
        stage.show();

        game.startGameLoop(); // starts AnimationTimer or input handling

        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W -> game.setMoveUp(true);
                case A -> game.setMoveLeft(true);
                case S -> game.setMoveDown(true);
                case D -> game.setMoveRight(true);
                case UP -> game.setMoveUp(true);
                case LEFT -> game.setMoveLeft(true);
                case RIGHT -> game.setMoveRight(true);
                case DOWN -> game.setMoveDown(true);
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W -> game.setMoveUp(false);
                case A -> game.setMoveLeft(false);
                case S -> game.setMoveDown(false);
                case D -> game.setMoveRight(false);
                case UP -> game.setMoveUp(false);
                case LEFT -> game.setMoveLeft(false);
                case RIGHT -> game.setMoveRight(false);
                case DOWN -> game.setMoveDown(false);
            }
        });
    }

    public static void main(String[] args) {
        launch();
    }
}
