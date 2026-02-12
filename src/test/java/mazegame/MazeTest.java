package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;

public class MazeTest {
    @Test
    void starterMazeSize() {
        Maze m = new Maze();
        m.resetMaze(false);

        assertEquals(3, m.getRows());
        assertEquals(3, m.getCols());
    }

    @Test
    void secondMazeSize() {
        Maze m = new Maze();
        for (int i = 0; i < 2; i++)
            m.resetMaze(false);

        assertEquals(9, m.getRows());
        assertEquals(9, m.getCols());
    }

    @Test
    void thirdMazeSize() {
        Maze m = new Maze();
        for (int i = 0; i < 3; i++)
            m.resetMaze(false);

        assertEquals(15, m.getRows());
        assertEquals(15, m.getCols());
    }

    @Test
    void forthMazeSize() {
        Maze m = new Maze();
        for (int i = 0; i < 4; i++)
            m.resetMaze(false);

        assertEquals(21, m.getRows());
        assertEquals(21, m.getCols());
    }

    @Test
    void fiftyMazeSize() {
        Maze m = new Maze();
        for (int i = 0; i < 50; i++)
            m.resetMaze(false);

        assertEquals(297, m.getRows());
        assertEquals(297, m.getCols());
    }

    @Test
    void largeMazeSize() {
        Maze m = new Maze();
        for (int i = 0; i < 100; i++)
            m.resetMaze(false);

        assertEquals(597, m.getRows());
        assertEquals(597, m.getCols());
    }

    @Test
    void removeWallUp() {
        Maze m = new Maze();
        m.resetMaze(false);
        m.removeWall(m.getGrid()[1][1], m.getGrid()[0][1]);
        assertFalse(m.getGrid()[1][1].top);
        assertFalse(m.getGrid()[0][1].bottom);
    }

    @Test
    void removeWallDown() {
        Maze m = new Maze();
        m.resetMaze(false);
        m.removeWall(m.getGrid()[1][1], m.getGrid()[2][1]);
        assertFalse(m.getGrid()[1][1].bottom);
        assertFalse(m.getGrid()[2][1].top);
    }

    @Test
    void removeWallLeft() {
        Maze m = new Maze();
        m.resetMaze(false);
        m.removeWall(m.getGrid()[1][1], m.getGrid()[1][0]);
        assertFalse(m.getGrid()[1][1].left);
        assertFalse(m.getGrid()[1][0].right);
    }

    @Test
    void removeWallRight() {
        Maze m = new Maze();
        m.resetMaze(false);
        m.removeWall(m.getGrid()[1][1], m.getGrid()[1][2]);
        assertFalse(m.getGrid()[1][1].right);
        assertFalse(m.getGrid()[1][2].left);
    }

    @Test
    void checkStartMazeCoins() {
        Maze m = new Maze();
        m.setRandomSeed(new Random(1)); 
        m.resetMaze(false);
        boolean hasCoin = false;

        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (m.getGrid()[r][c].hasCoin) {
                    hasCoin = true;
                }
            }
        }
        assertTrue(hasCoin);
    }

    @Test
    void checkSecondMazeCoins() {
        Maze m = new Maze();
        m.setRandomSeed(new Random(1));
        for (int i = 0; i < 2; i++) {
            m.resetMaze(false);
        }

        int coinsNum = 0;
        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (m.getGrid()[r][c].hasCoin) {
                    coinsNum++;
                }
            }
        }
        assertEquals(coinsNum, 12); // From seed always true
    }

    @Test
    void checkThirdMazeCoins() {
        Maze m = new Maze();
        m.setRandomSeed(new Random(1));
        for (int i = 0; i < 3; i++) {
            m.resetMaze(false);
        }

        int coinsNum = 0;
        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (m.getGrid()[r][c].hasCoin) {
                    coinsNum++;
                }
            }
        }
        assertEquals(coinsNum, 31); // From seed always true
    }

    @Test
    void checkLargeMazeCoins() {
        Maze m = new Maze();
        m.setRandomSeed(new Random(1));
        for (int i = 0; i < 100; i++) {
            m.resetMaze(false);
        }

        int coinsNum = 0;
        for (int r = 0; r < m.getRows(); r++) {
            for (int c = 0; c < m.getCols(); c++) {
                if (m.getGrid()[r][c].hasCoin) {
                    coinsNum++;
                }
            }
        }
        assertEquals(coinsNum, 14255); // From seed always true
    }
}
