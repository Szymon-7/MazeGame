package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionTest {

    @Test
    void playerCanMoveUp() {
        Maze m = new Maze();
        Player p = new Player(false);
        CollisionSystem cs = new CollisionSystem(m, p, null);
        m.resetMaze(false);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);

        assertTrue(cs.canMove(0, -1));
    }

    @Test
    void playerCanMoveDown() {
        Maze m = new Maze();
        Player p = new Player(false);
        CollisionSystem cs = new CollisionSystem(m, p, null);
        m.resetMaze(false);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);

        assertTrue(cs.canMove(0, 1));
    }

    @Test
    void playerCanMoveLeft() {
        Maze m = new Maze();
        Player p = new Player(false);
        CollisionSystem cs = new CollisionSystem(m, p, null);
        m.resetMaze(false);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);

        assertTrue(cs.canMove(-1, 0));
    }

    @Test
    void playerCanMoveRight() {
        Maze m = new Maze();
        Player p = new Player(false);
        CollisionSystem cs = new CollisionSystem(m, p, null);
        m.resetMaze(false);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);

        assertTrue(cs.canMove(1, 0));
    }
}
