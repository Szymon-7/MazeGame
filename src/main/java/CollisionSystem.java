public class CollisionSystem {

    private final Maze maze;
    private final Player player;

    public CollisionSystem(Maze maze, Player player) {
        this.maze = maze;
        this.player = player;
    }

    public boolean canMove(double dx, double dy) {
        double nextX = player.getX() + dx;
        double nextY = player.getY() + dy;

        double left = nextX;
        double right = nextX + player.getSize();
        double top = nextY;
        double bottom = nextY + player.getSize();

        int minRow = (int)(top / maze.getCellSize());
        int maxRow = (int)((bottom - 0.001) / maze.getCellSize());
        int minCol = (int)(left / maze.getCellSize());
        int maxCol = (int)((right - 0.001) / maze.getCellSize());

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {
                Cell cell = maze.getGrid()[row][col];

                double cellLeft = col * maze.getCellSize();
                double cellRight = cellLeft + maze.getCellSize();
                double cellTop = row * maze.getCellSize();
                double cellBottom = cellTop + maze.getCellSize();

                if (checkWallCollision(cell, top, bottom, left, right, cellTop, cellBottom, cellLeft, cellRight)) {
                    return false;
                }

                if (checkCornerCollision(row, col, top, left, maze.getCellSize())) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean checkWallCollision(Cell cell, double top, double bottom, double left, double right,
                                       double cellTop, double cellBottom, double cellLeft, double cellRight) {

        if (cell.top && top < cellTop + maze.getWallThickness() - 1 && bottom > cellTop)
            return true;
        if (cell.bottom && bottom > cellBottom && top < cellBottom)
            return true;
        if (cell.left && left < cellLeft + maze.getWallThickness() - 1 && right > cellLeft)
            return true;
        if (cell.right && right > cellRight && left < cellRight)
            return true;

        return false;
    }

    private boolean checkCornerCollision(int row, int col, double top, double left, int cellSize) {
        if (row > 0 && col > 0) {
            Cell above = maze.getGrid()[row - 1][col];
            Cell leftCell = maze.getGrid()[row][col - 1];
            Cell corner = maze.getGrid()[row - 1][col - 1];

            boolean topWall = above.bottom;
            boolean leftWall = leftCell.right;
            boolean cornerWall = corner.bottom || corner.right;

            boolean touchingCorner = top < (row * cellSize) + maze.getWallThickness() - 1 && left < (col * cellSize) + maze.getWallThickness() - 1;

            return (topWall && leftWall || cornerWall) && touchingCorner;
        }
        return false;
    }

    public void checkCoinCollisions() {
        double playerLeft = player.getX();
        double playerRight = player.getX() + player.getSize();
        double playerTop = player.getY();
        double playerBottom = player.getY() + player.getSize();

        for (int row = 0; row < maze.getRows(); row++) {
            for (int col = 0; col < maze.getCols(); col++) {
                Cell cell = maze.getGrid()[row][col];
                if (!cell.hasCoin) continue;

                // Coin position & radius
                double coinX = col * maze.getCellSize() + maze.getCellSize() / 3.0;
                double coinY = row * maze.getCellSize() + maze.getCellSize() / 3.0;
                double coinRadius = maze.getCellSize() / 6.0;

                // Closest point on player to coin center
                double closestX = Math.max(playerLeft, Math.min(coinX + coinRadius, playerRight));
                double closestY = Math.max(playerTop, Math.min(coinY + coinRadius, playerBottom));

                // Distance from coin center
                double dx = (coinX + coinRadius) - closestX;
                double dy = (coinY + coinRadius) - closestY;

                // Coin pickup with efficient formula
                if (dx * dx + dy * dy < coinRadius * coinRadius) {
                    cell.hasCoin = false;
                    player.addCoins(1);
                }
            }
        }
    }

    public boolean isPlayerOnShop() {

        if (maze.getShop() == null) return false;

        int playerRow = (int)((player.getY() + player.getSize() / 2) / maze.getCellSize());
        int playerCol = (int)((player.getX() + player.getSize() / 2) / maze.getCellSize());

        return playerRow == maze.getShop().row && playerCol == maze.getShop().col;
    }

    public boolean isPlayerOnExit() {

        if (maze.getExit() == null) return false;

        int playerRow = (int)((player.getY() + player.getSize() / 2) / maze.getCellSize());
        int playerCol = (int)((player.getX() + player.getSize() / 2) / maze.getCellSize());

        return playerRow == maze.getExit().row && playerCol == maze.getExit().col;
    }
}
