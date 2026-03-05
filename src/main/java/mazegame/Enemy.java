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
    
    private Random random = new Random();
    private double changeDirectionTimer = 0;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        pickNewDirection();
    }

    public double getX() { return x; }
    public double getY() { return y; }
    public double getDx() { return dx; }
    public double getDy() { return dy; }
    public int getSize() { return size; }

    private void pickNewDirection() {
        int oldDir = -1;
        if (dy == -1) oldDir = 0;
        else if (dy == 1) oldDir = 1;
        else if (dx == -1) oldDir = 2;
        else if (dx == 1) oldDir = 3;

        int dir;
        do {
            dir = random.nextInt(4);
        } while (dir == oldDir && oldDir != -1); // Try to pick different direction if blocked

        dx = 0;
        dy = 0;
        switch(dir) {
            case 0: dy = -1; break; // Up
            case 1: dy = 1;  break; // Down
            case 2: dx = -1; break; // Left
            case 3: dx = 1;  break; // Right
        }
        // Change direction every 3-6 seconds naturally, or immediately on wall hit
        changeDirectionTimer = 3.0 + random.nextDouble() * 3.0;
    }

    public void update(double dt, CollisionSystem collision) {
        changeDirectionTimer -= dt;
        
        double moveDist = speed * dt;
        double nextX = dx * moveDist;
        double nextY = dy * moveDist;

        if (collision.canMove(x, y, size, nextX, nextY)) {
            x += nextX;
            y += nextY;
        } else {
            pickNewDirection(); // Hit a wall, turn immediately
        }

        if (changeDirectionTimer <= 0) {
            pickNewDirection();
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
