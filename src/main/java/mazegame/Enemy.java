package mazegame;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.Random;

public class Enemy {
    private double x, y;
    private final int size = 18;
    private double speed = 40;
    
    // Movement vector
    private double dx = 0;
    private double dy = 0;

    private int lastMoveDir = -1;
    private Random random = new Random();
    private double changeDirectionTimer = 0;

    private int cycleCount = 0;
    private int consecutiveTurns = 0;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        this.lastMoveDir = random.nextInt(4);
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public int getSize() { return size; }
    public int getCycleCount() { return cycleCount; }

    private void pickNewDirection(CollisionSystem collision) {
        // 30% idle chance
        if (random.nextDouble() < 0.3 && (dx != 0 || dy != 0)) {
            dx = 0;
            dy = 0;
            changeDirectionTimer = 1.0 + random.nextDouble() * 1.0;
            return;
        }

        // 0:Up, 1:Down, 2:Left, 3:Right
        double[][] vectors = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

        // Determine the priority based on current direction
        int[] priority;
        
        if (cycleCount >= 2) {
            priority = new int[]{0, 1, 2, 3};
            for (int i = 0; i < 4; i++) {
                int swapIdx = random.nextInt(4);
                int temp = priority[swapIdx];
                priority[swapIdx] = priority[i];
                priority[i] = temp;
            }
            cycleCount = 0;
            consecutiveTurns = 0;
        } else {
            switch (lastMoveDir) {
                case 0: priority = new int[]{3, 0, 2, 1}; break; // Facing Up: Right is Right(3), Straight is Up(0)...
                case 1: priority = new int[]{2, 1, 3, 0}; break; // Facing Down: Right is Left(2), Straight is Down(1)...
                case 2: priority = new int[]{0, 2, 1, 3}; break; // Facing Left: Right is Up(0), Straight is Left(2)...
                case 3: priority = new int[]{1, 3, 0, 2}; break; // Facing Right: Right is Down(1), Straight is Right(3)...
                default: priority = new int[]{0, 1, 2, 3}; // Start out random
            }
        }

        for (int dirIndex : priority) {
            double testDx = vectors[dirIndex][0] * speed * 0.2;
            double testDy = vectors[dirIndex][1] * speed * 0.2;

            if (collision.canMove(x, y, size, testDx, testDy)) {
                if (dirIndex != lastMoveDir && lastMoveDir != -1) {
                    consecutiveTurns++;
                    if (consecutiveTurns >= 4) {
                        cycleCount++;
                        consecutiveTurns = 0;
                    }
                } else {
                    consecutiveTurns = 0;
                }

                dx = vectors[dirIndex][0];
                dy = vectors[dirIndex][1];
                lastMoveDir = dirIndex;

                changeDirectionTimer = 5.0;
                return;
            }
        }

        dx = 0;
        dy = 0;
        changeDirectionTimer = 0;
    }

    public void update(double dt, CollisionSystem collision) {
        changeDirectionTimer -= dt;

        // If we are idle just count down the timer and stop here
        if (changeDirectionTimer <= 0) {
            pickNewDirection(collision);
        }

        // Attempt movement
        if (dx != 0 || dy != 0) {
            double moveDist = speed * dt;

            if (collision.canMove(x, y, size, dx * moveDist, dy * moveDist)) {
                x += dx * moveDist;
                y += dy * moveDist;
            } else {
                pickNewDirection(collision);

                if (dx != 0 || dy != 0) {
                    moveDist = speed * dt;
                    if (collision.canMove(x, y, size, dx * moveDist, dy * moveDist)) {
                        x += dx * moveDist;
                        y += dy * moveDist;
                    }
                }
            }
        }
    }


    public void render(GraphicsContext gc, double offsetX, double offsetY) {
        gc.setFill(Color.RED);
        gc.fillOval(x + offsetX, y + offsetY, size, size);
        
        // temp visuals
        gc.setFill(Color.BLACK);
        gc.fillOval(x + offsetX + 4, y + offsetY + 4, 3, 3);
        gc.fillOval(x + offsetX + 11, y + offsetY + 4, 3, 3);
    }
}
