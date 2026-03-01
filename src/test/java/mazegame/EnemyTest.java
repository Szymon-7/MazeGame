package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    void enemyCountTest() {
        Maze m = new Maze();
        m.resetMaze(false);
        m.resetMaze(false);
        for (int i = 0; i < 9; i ++) {
            for (int j = 0; j < 9; j++) {
                Cell c = m.getGrid()[i][j];
                c.top = false;
                c.bottom = false;
                c.left = false;
                c.right = false;
            }
        }
        CollisionSystem cs = new CollisionSystem(m, null, null);
        Enemy e = new Enemy(100, 100);

        e.update(1, cs);
        assertTrue(e.getX() == 140 ^ e.getX() == 60 ^ e.getY() == 140 ^ e.getY() == 60);
    }
}
