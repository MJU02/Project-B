import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/*
 Project B — Goal Set #2: Floodfill
 Authors: William Yang, Mohammed Uddin.

 Purpose:
   - Load a weighted maze from file.
   - Use the flood fill algorithm to solve a maze (assuming it is solvable).
*/

public class floodfill {
    /**
     * Initiates the flood fill algorithm.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String contents = Files.readString(Path.of("datafile.txt"));
        finderMap fm = new finderMap(contents);

        System.out.println("=== Loaded Map ===");
        System.out.println(fm.getString());

        String[] lines = contents.split("\n", -1);

        floodfill_dfs(fm, fm.getStartOrEnd('s')[0], fm.getStartOrEnd('s')[1]);
    }

    /**
     * Uses the flood fill algorithm to solve the maze.
     * Recurses through every adjacent position from the start until the end point is reached.
     * 
     * @param fm
     * @param x
     * @param y
     */
    public static void floodfill_dfs(finderMap fm, int x, int y) {
        System.out.println("Current position: " + x + "," + y);
        if(fm.getVisited(x, y)) return;
        if((x == fm.getStartOrEnd('e')[0] && y == fm.getStartOrEnd('e')[1])) {
            // Terminates program when floodfill finds the exit
            System.out.println("=== Completed Map ===");
            System.out.println(fm.getString());
            System.exit(0);
        }
        fm.setVisited(x, y);

        if(fm.getPath(x+1, y)) {
            floodfill_dfs(fm, x+1, y);
        }
        if(fm.getPath(x-1, y)) {
            floodfill_dfs(fm, x-1, y);
        }
        if(fm.getPath(x, y+1)) {
            floodfill_dfs(fm, x, y+1);
        }
        if(fm.getPath(x, y-1)) {
            floodfill_dfs(fm, x, y-1);
        }
    }
}
