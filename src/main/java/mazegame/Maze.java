package mazegame;

import java.util.*;

public class Maze {

    public static Maze instance;
    private int rows = 3;
    private int cols = 3;
    private Cell[][] grid;
    private Cell[] neighborBuffer = new Cell[4];

    private int mazeLevel = -1;
    private int cellSize = 50;
    private int wallThickness = 5;

    private Shop shop;

    private Exit exit;

    private Random random = new Random();
    public void setRandomSeed(Random r) { random = r; }

    public Maze() {
        instance = this;
    }


    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell[][] getGrid() { return grid; }
    public int getMazeLevel() { return mazeLevel; }
    public int getCellSize() { return cellSize; }
    public int getWallThickness() { return wallThickness; }
    public Shop getShop() { return shop; }
    public Exit getExit() { return exit; }
    public double getCenter() { return ((double)rows * cellSize) / 2; }

    public void resetMaze(boolean loadSprites) {
        mazeLevel++;
        rows = 3 + mazeLevel * 6;
        cols = 3 + mazeLevel * 6;

        grid = new Cell[rows][cols];

        initGrid();
        generateMazeDFS(grid[0][0]);

        placeShop(loadSprites);
        placeExit(loadSprites);

        // Coins last to fill remaining 84% space
        generateCoins((rows * cols) / 6);
    }

    private void initGrid() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }

    private void generateMazeDFS(Cell startCell) {
        // ArrayDeque for stack, more performant and flexible
        Deque<Cell> stack = new ArrayDeque<>();

        startCell.visited = true;
        stack.push(startCell);

        while (!stack.isEmpty()) {
            Cell current = stack.peek();
            int count = getUnvisitedNeighbors(current);

            if (count > 0) {
                // Pick one random unvisited neighbor
                Cell next = neighborBuffer[random.nextInt(count)]; 

                removeWall(current, next);
                next.visited = true;

                // Push to stack to continue exploring from this new cell
                stack.push(next);
            } else {
                // Dead end found, backtrack by popping
                stack.pop();
            }
        }
    }

    private int getUnvisitedNeighbors(Cell cell) {
        int count = 0;

        // Check up
        if (cell.row > 0 && !grid[cell.row - 1][cell.col].visited)
        neighborBuffer[count++] = grid[cell.row - 1][cell.col];
        // Check down
        if (cell.row < rows - 1 && !grid[cell.row + 1][cell.col].visited)
        neighborBuffer[count++] = grid[cell.row + 1][cell.col];
        // Check left
        if (cell.col > 0 && !grid[cell.row][cell.col - 1].visited)
        neighborBuffer[count++] = grid[cell.row][cell.col - 1];
        // Check right
        if (cell.col < cols - 1 && !grid[cell.row][cell.col + 1].visited)
        neighborBuffer[count++] = grid[cell.row][cell.col + 1];

        return count; // How many slots in neighborBuffer are valid
    }

    public void removeWall(Cell current, Cell next) {
        if (next.row < current.row) {           // Next is above
            current.top = false;
            next.bottom = false;
        } else if (next.row > current.row) {    // Next is below
            current.bottom = false;
            next.top = false;
        } else if (next.col < current.col) {    // Next is left
            current.left = false;
            next.right = false;
        } else if (next.col > current.col) {    // Next is right
            current.right = false;
            next.left = false;
        }
    }

    // The 3 functions below are BOGO but are actually more efficient than an O(n) Collections shuffle
    // The max total coins in the maze are always (rows * cols / 6) ~ 16.67% of the cells, meaning the maze is 83.33% empty
    // The average tries to find an empty spot per coin is 1/0.8333 ~ 1.2 in this case at the maximum
    // The approximate complexity is therefore (0.1667 * 1.2) ~ 0.2N
    // On average the O(0.2N) BOGO approach is actually 5x better than the shuffle approach all because the coins are capped to (rows * cols / 6)
    // For example to place 100 coins it would take about 120 guesses VS. 600 shuffle operations
    // Of course it is random so it is not 100% stable and could take much longer theoretically
    private void placeShop(boolean loadSprites) { 
        // Grid is empty, first try
        int r = random.nextInt(rows);
        int c = random.nextInt(cols);

        shop = new Shop(r, c, loadSprites);
        grid[r][c].hasShop = true;
    }

    private void placeExit(boolean loadSprites) {
        int r, c;

        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (grid[r][c].hasShop); // Avoid shop

        exit = new Exit(r, c, loadSprites);
        grid[r][c].hasExit = true;
    }

    private void generateCoins(int maxCoins) {
        int numOfCoins = random.nextInt(maxCoins + 1);    // Random num between 0 and max (coins)
        int row, col;

        for(int i = 0; i < numOfCoins; i++) {
            do {
                row = random.nextInt(rows);   // Random num between rows & cols
                col = random.nextInt(cols);
            } while (grid[row][col].hasCoin || grid[row][col].hasShop || grid[row][col].hasExit);

            grid[row][col].hasCoin = true;
        }
    }
}
