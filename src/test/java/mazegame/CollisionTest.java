package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

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
    void wallBlocksPlayerUp() {
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

        assertTrue(cs.canMove(0, -12.5));
        assertFalse(cs.canMove(0, -12.6));
    }

    @Test
    void wallBlocksPlayerDown() {
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

        assertTrue(cs.canMove(0, 12.5));
        assertFalse(cs.canMove(0, 12.6));
    }

    @Test
    void wallBlocksPlayerLeft() {
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

        assertTrue(cs.canMove(-12.5, 0));
        assertFalse(cs.canMove(-12.6, 0));
    }

    @Test
    void wallBlocksPlayerRight() {
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

        assertTrue(cs.canMove(12.5, 0));
        assertFalse(cs.canMove(12.6, 0));
    }

    @Test
    void playerCoinCollision() {
        Maze m = new Maze();
        Player p = new Player(false);
        CollisionSystem cs = new CollisionSystem(m, p, null);
        m.resetMaze(false);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);

        assertEquals(p.getCoins(), 0);
        m.getGrid()[1][1].hasCoin = true;
        cs.checkCoinCollisions();
        assertEquals(p.getCoins(), 1);
    }

    @Test
    void playerShopCollision() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.setRandomSeed(new Random(1));
        CollisionSystem cs = new CollisionSystem(m, p, null);
        p.setX(m.getCenter() - p.getSize() / 2);
        p.setY(m.getCenter() - p.getSize() / 2);

        assertFalse(cs.isPlayerOnShop());
        m.resetMaze(false); // Shop generates at 1,1 in this seed
        assertTrue(cs.isPlayerOnShop());
    }

    @Test
    void playerExitCollision() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.setRandomSeed(new Random(1));
        CollisionSystem cs = new CollisionSystem(m, p, null);
        p.setX(m.getCenter() - m.getCellSize() - p.getSize() / 2); // Middle of cell 0,1
        p.setY(m.getCenter() - p.getSize() / 2);

        assertFalse(cs.isPlayerOnExit());
        m.resetMaze(false); // Exit generates at 0,1 in this seed
        assertTrue(cs.isPlayerOnExit());
    }
}
