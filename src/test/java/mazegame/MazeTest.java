package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Random;
import java.util.Queue;
import java.util.ArrayDeque;
import java.util.Set;
import java.util.HashSet;

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

    @Test
    void validShopAndExitLargeMaze() {
        Maze m = new Maze();
        for (int i = 0; i < 100; i++) {
            m.resetMaze(false);
        }

        boolean shop = false, exit = false;
        int r = 0, c = 0;
        Cell grid[][] = m.getGrid();

        outer:
        for (r = 0; r < m.getRows(); r++) {
            for (c = 0; c < m.getCols(); c++) {
                if (shop && exit)
                    break outer;
                else if (grid[r][c].hasShop)
                    shop = true;
                else if (grid[r][c].hasExit)
                    exit = true;
            }
        }

        assertTrue(shop);
        assertTrue(exit);
    }

    @Test
    void mazeIsAlwaysSolvable() {
        Maze m = new Maze();
        for (int i = 0; i < 10; i++) {
            m.resetMaze(false);
            
            int startRow = m.getRows() / 2;
            int startCol = m.getCols() / 2;
            Cell start = m.getGrid()[startRow][startCol];
            Exit exit = m.getExit();
            
            assertTrue(isPathAvailable(m, start, exit.row, exit.col), "Maze level " + m.getMazeLevel() + " is unsolvable.");
        }
    }

    @Test
    void shopIsAlwaysReachable() {
        Maze m = new Maze();
        for (int i = 0; i < 10; i++) {
            m.resetMaze(false);
            
            int startRow = m.getRows() / 2;
            int startCol = m.getCols() / 2;
            Cell start = m.getGrid()[startRow][startCol];
            Shop shop = m.getShop();
            
            assertTrue(isPathAvailable(m, start, shop.row, shop.col), "Shop in maze level " + m.getMazeLevel() + " is unreachable.");
        }
    }

    private boolean isPathAvailable(Maze m, Cell start, int targetRow, int targetCol) {
        Queue<Cell> queue = new ArrayDeque<>();
        Set<Cell> visited = new HashSet<>();
        
        queue.add(start);
        visited.add(start);
        
        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            if (current.row == targetRow && current.col == targetCol) 
                return true;

            // Check all 4 directions, respecting the maze's wall booleans
            if (!current.top && current.row > 0) {
                Cell next = m.getGrid()[current.row - 1][current.col];
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
            if (!current.bottom && current.row < m.getRows() - 1) {
                Cell next = m.getGrid()[current.row + 1][current.col];
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
            if (!current.left && current.col > 0) {
                Cell next = m.getGrid()[current.row][current.col - 1];
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
            if (!current.right && current.col < m.getCols() - 1) {
                Cell next = m.getGrid()[current.row][current.col + 1];
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }
}
