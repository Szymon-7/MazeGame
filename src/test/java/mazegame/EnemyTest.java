package mazegame;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EnemyTest {

    @Test
    void enemyMoveTest() {
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

    @Test
    void enemyMoveBlockedTest() {
        Maze m = new Maze();
        m.resetMaze(false);

        CollisionSystem cs = new CollisionSystem(m, null, null);
        Enemy e = new Enemy(66, 66);
        Cell c = m.getGrid()[1][1];
        c.left = true;
        c.right = true;
        c.top = true;
        c.bottom = true;

        e.update(2, cs);
        assertTrue(e.getX() == 66 && e.getY() == 66); // Same spot
    }

    @Test
    void enemyChangesDirectionOnTimer() {
        Maze m = new Maze();
        m.resetMaze(false);
        CollisionSystem cs = new CollisionSystem(m, null, null);
        Enemy e = new Enemy(75, 75);
        
        double startDx = e.getDx();
        double startDy = e.getDy();
        
        e.update(100.0, cs); 
        
        assertFalse(e.getDx() == startDx && e.getDy() == startDy, "Direction should change after timer expired");
    }
}
