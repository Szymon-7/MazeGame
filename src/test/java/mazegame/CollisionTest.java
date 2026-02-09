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

    @Test
    void wallBlocksPlayer() {
        Maze m = new Maze();
        Player p = new Player(false);
        CollisionSystem cs = new CollisionSystem(m, p, null);
        m.resetMaze(false);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);
        m.getGrid()[1][1].top = true;
        m.getGrid()[1][1].bottom = true;
        m.getGrid()[1][1].left = true;
        m.getGrid()[1][1].right = true;

        assertTrue(cs.canMove(0, -13.5));
        assertFalse(cs.canMove(0, -13.6));
    }
}
