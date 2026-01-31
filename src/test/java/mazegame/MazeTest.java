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
}
