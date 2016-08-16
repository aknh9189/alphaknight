import java.util.Arrays;

/**
 * Created by aknh9189 on 8/12/16.
 */

public class Board {
    public static int[][] grid = new int[8][8];
    public Board()
    {
        int ffff[] = {0,0};
        int tooo[] = {3,3};
        Board.move(ffff,tooo);
        System.out.println(Arrays.deepToString(grid));
    }


    public static void move(int moveTo[], int location[]) {
        grid[moveTo[0]][moveTo[1]] = grid[location[0]][location[1]];
        grid[location[0]][location[1]] = 0;
    }

    public static void main(String[] args) {
        for (int x = 0; x <= grid[0].length - 1; x++) {
            grid[0][x] = 1;
            grid[1][x] = 1;
            grid[6][x] = 2;
            grid[7][x] = 2;
        }
        Board boardb = new Board();
    }
}