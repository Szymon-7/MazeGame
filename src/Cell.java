public class Cell {
   boolean top = true, bottom = true, left = true, right = true;
   boolean visited = false;
   boolean visible = false;

   int row, col;

   public Cell(int row, int col) {
      this.row = row;
      this.col = col;
   }
}
