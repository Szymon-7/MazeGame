import java.util.*;

public class Maze {

    private int rows = 3;
    private int cols = 3;
    private Cell[][] grid;

    private int mazeLevel = -1;
    private int cellSize = 50;
    private int wallThickness = 5;

    private Shop shop;

    private Exit exit;

    private Random random = new Random();

    public Maze() {}

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Cell[][] getGrid() { return grid; }
    public int getCellSize() { return cellSize; }
    public int getWallThickness() { return wallThickness; }
    public Shop getShop() { return shop; }
    public Exit getExit() { return exit; }
    public double getCenter() { return (rows * cellSize + wallThickness) / 2; }

    public void resetMaze() {
        mazeLevel++;
        rows = 3 + mazeLevel * 6;
        cols = 3 + mazeLevel * 6;

        grid = new Cell[rows][cols];

        initGrid();
        generateMazeDFS(grid[0][0]);

        generateCoins((rows * cols) / 6);
        placeShop();
        placeExit();
    }

    private void initGrid() {
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                grid[row][col] = new Cell(row, col);
            }
        }
    }

    private void generateMazeDFS(Cell cell) {
        cell.visited = true;

        for (Cell neighbor : getShuffledNeighbors(cell)) {
            if (!neighbor.visited) {
                removeWall(cell, neighbor);
                generateMazeDFS(neighbor);
            }
        }
    }

    private List<Cell> getShuffledNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();

        if (cell.row > 0) neighbors.add(grid[cell.row - 1][cell.col]);            // Above neighbor
        if (cell.row < rows - 1) neighbors.add(grid[cell.row + 1][cell.col]);     // Below neighbor
        if (cell.col > 0) neighbors.add(grid[cell.row][cell.col - 1]);            // Left neighbor
        if (cell.col < cols - 1) neighbors.add(grid[cell.row][cell.col + 1]);     // Right neighbor

        Collections.shuffle(neighbors);
        return neighbors;
    }

    private void removeWall(Cell current, Cell next) {
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

    private void generateCoins(int maxCoins) {
        int numOfCoins = random.nextInt(maxCoins + 1);    // Random num between 0 and max (coins)
        int row, col;

        for(int i = 0; i < numOfCoins; i++) {
            do {
                row = random.nextInt(rows);   // Random num between 0 and 14 (rows & cols)
                col = random.nextInt(cols); 
            } while (grid[row][col].hasCoin == true);   // No repeats/overlap

            grid[row][col].hasCoin = true;
        }
    }

    private void placeShop() {
        int r, c;

        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (grid[r][c].hasCoin); // avoid coin overlap

        shop = new Shop(r, c);
        grid[r][c].hasShop = true;
    }

    private void placeExit() {
        int r, c;

        do {
            r = random.nextInt(rows);
            c = random.nextInt(cols);
        } while (grid[r][c].hasCoin || grid[r][c].hasShop);

        exit = new Exit(r, c);
        grid[r][c].hasExit = true;
    }
}
