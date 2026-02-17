package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {

    @Test
    void playerMovesUp() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.resetMaze(false);

        p.setX(10);
        p.setY(10);
        p.moveUp(5);

        assertEquals(5, p.getY());
    }

    @Test
    void playerMovesDown() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.resetMaze(false);

        p.setX(10);
        p.setY(10);
        p.moveDown(5);

        assertEquals(15, p.getY());
    }

    @Test
    void playerMovesLeft() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.resetMaze(false);

        p.setX(10);
        p.setY(10);
        p.moveLeft(5);

        assertEquals(5, p.getX());
    }

    @Test
    void playerMovesRight() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.resetMaze(false);

        p.setX(10);
        p.setY(10);
        p.moveRight(5);

        assertEquals(15, p.getX());
    }

    @Test
    void playerCoordinatesClamp() {
        Player p = new Player(false);
        Maze m = new Maze();
        m.resetMaze(false);

        p.setX(10);
        p.setY(20);
        assertEquals(p.getX(), 10);
        assertEquals(p.getY(), 20);
        p.setX(-10);
        p.setY(-20);
        assertEquals(p.getX(), 10);
        assertEquals(p.getY(), 20);
    }

    @Test
    void addCoinsSingle() {
        Player p = new Player(false);

        assertEquals(0, p.getCoins());

        p.addCoins(1);

        assertEquals(1, p.getCoins());
    }

    @Test
    void addCoinsMulti() {
        Player p = new Player(false);

        assertEquals(0, p.getCoins());

        p.addCoins(1);
        p.addCoins(1);
        p.addCoins(1);

        assertEquals(3, p.getCoins());
    }

    @Test
    void addCoinsGreaterAmounts() {
        Player p = new Player(false);

        assertEquals(0, p.getCoins());

        p.addCoins(5);
        p.addCoins(10);
        p.addCoins(15);

        assertEquals(30, p.getCoins());
    }

    @Test
    void addCoinsSubtract() {
        Player p = new Player(false);

        assertEquals(0, p.getCoins());

        p.addCoins(10);
        p.addCoins(-5);

        assertEquals(5, p.getCoins());
    }

    @Test
    void addPickaxeRespectsBagLimit() {
        Player p = new Player(false);

        assertTrue(p.addPickaxe());
        assertFalse(p.addPickaxe());
    }

    @Test
    void upgradeLanternCorrectly() {
        Player p = new Player(false);

        assertEquals(1, p.getLanternLevel());
        p.upgradeLantern();
        assertEquals(2, p.getLanternLevel());
    }

    @Test
    void upgradeSpeedCorrectly() {
        Player p = new Player(false);

        assertEquals(1, p.getSpeedLevel());
        p.upgradeSpeed();
        assertEquals(2, p.getSpeedLevel());
    }

    @Test
    void upgradeBagCorrectly() {
        Player p = new Player(false);

        assertEquals(1, p.getBagLevel());
        p.upgradeBag();
        assertEquals(2, p.getBagLevel());
    }

    @Test
    void addPickaxeRespectsBagUpgrade() {
        Player p = new Player(false);

        p.upgradeBag();
        assertTrue(p.addPickaxe());
        assertTrue(p.addPickaxe());
        assertFalse(p.addPickaxe());
    }

    @Test
    void playerKeepsCoinsOnMazeReset() {
        Maze m = new Maze();
        Player p = new Player(false);
        m.resetMaze(false);
        assertEquals(p.getCoins(), 0);
        p.addCoins(5);
        assertEquals(p.getCoins(), 5);
        m.resetMaze(false);
        assertEquals(p.getCoins(), 5);
    }
}
