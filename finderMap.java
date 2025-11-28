/**
 * Authors: Mohammed, William
 * 
 * 
 * finderMap.java
 * 
 * A class representing a maze map for pathfinding.
 * 
 * Symbols per spec:
 *   '#' = beginning (walkable)
 *   '!' = end (walkable)
 *   'x' = unvisitable (wall)
 *   'p' = pathway (walkable)
 * 
 *
*/
public class finderMap {

    // Required data structures
    private final char[][] map;       // contains the raw map symbols
    private final boolean[][] visited; // visited flags

    private final int rows;
    private final int cols;

    /**
     * constructor(String map)
     * Accepts the entire map as a single String that may contain '\n' for new lines.
     * Lines must be rectangular (all the same length).
     */
public finderMap(String mapString) {
    if (mapString == null) {
        throw new IllegalArgumentException("Map string cannot be null.");
    }

    // Normalize Windows/mac line endings to '\n'
    //ran into an issue initially where my test files had \r\n endings
    String normalized = mapString.replace("\r\n", "\n").replace("\r", "\n");
    String[] lines = normalized.split("\n", -1);

    // If the file ends with a newline, drop the trailing empty row
    if (lines.length > 1 && lines[lines.length - 1].isEmpty()) {
        lines = java.util.Arrays.copyOf(lines, lines.length - 1);
    }

    if (lines.length == 0) {
        throw new IllegalArgumentException("Map string is empty.");
    }

    int width = lines[0].length();
    if (width == 0) {
        throw new IllegalArgumentException("First map line is empty.");
    }

    // Enforce a rectangular map
    for (int i = 1; i < lines.length; i++) {
        if (lines[i].length() != width) {
            throw new IllegalArgumentException(
                "Non-rectangular map: line " + (i + 1) + " has different length."
            );
        }
    }

    this.rows = lines.length;
    this.cols = width;
    this.map = new char[rows][cols];
    this.visited = new boolean[rows][cols];

    for (int r = 0; r < rows; r++) {
        char[] rowChars = lines[r].toCharArray();
        System.arraycopy(rowChars, 0, this.map[r], 0, cols);
    }
}
    public int[] getStartOrEnd(char c) {
        int[] position = {0,0};
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[i].length; ++j) {
                if (c == 's') {
                    if(map[i][j] == '#') {
                        position[0] = i;
                        position[1] = j;
                        return position;
                    }
                } else if (c == 'e') {
                    if(map[i][j] == '!') {
                        position[0] = i;
                        position[1] = j;
                        return position;
                    }
                }
            }
        }
        return position;
    }

    // ** Spec methods of goal 1 ** 

    // Returns true if (row,col) is a walkable spot per spec (p, #, or !)
    public boolean getPath(int row, int col) {
        if (!inBounds(row, col)) return false;
        char ch = map[row][col];
        return ch == 'p' || ch == '#' || ch == '!';
    }

    /** Returns whether (row,col) has been visited. Out-of-bounds returns false. */
    public boolean getVisited(int row, int col) {
        if (!inBounds(row, col)) return false;
        return visited[row][col];
    }

    /**
     * Attempts to mark (row,col) as visited.
     * Only marks if the cell is on an allowable pathway (getPath == true).
     * Returns true if visited was set, false otherwise.
     */
    public boolean setVisited(int row, int col) {
        if (!inBounds(row, col)) return false;
        if (!getPath(row, col)) return false;
        visited[row][col] = true;
        return true;
    }

    /**
     * Represents the current state of the maze including visited/unvisited.
     * Convention here:
     *   - Show 'v' where a walkable cell has been visited.
     *   - Show original symbols for all other cells (#, !, x, p).
     *
     * (We leave the underlying map untouched; this is a rendered view.)
     */
    public String getString() {
        StringBuilder sb = new StringBuilder(rows * (cols + 1));
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (visited[r][c] && getPath(r, c)) {
                    sb.append('v');
                } else {
                    sb.append(map[r][c]);
                }
            }
            if (r < rows - 1) sb.append('\n');
        }
        return sb.toString();
    }

    // ---- small internal helper ----
    // Checks if (r,c) is within map bounds
    private boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }
}
