package easel.ui.layouts;

import java.util.Objects;

public class GridLocation
{
   public int row;
   public int col;

   public GridLocation(int row, int col) {
     this.row = row;
     this.col = col;
   }

   public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     GridLocation that = (GridLocation)o;
     return (this.row == that.row && this.col == that.col);
   }

   public int hashCode() {
     return Objects.hash(new Object[] { Integer.valueOf(this.row), Integer.valueOf(this.col) });
   }
}
