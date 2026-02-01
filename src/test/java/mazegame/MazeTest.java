package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

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
        m.resetMaze(false);
        m.resetMaze(false);

        assertEquals(9, m.getRows());
        assertEquals(9, m.getCols());
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
}
