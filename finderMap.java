/**
 * Authors: Mohammed Uddin, William Yang
 *
 * finderMap.java — Project B, Goal Set #3 (Counting moves and backtracking)
 *
 * Purpose (our words):
 *   - Parse a weighted maze where walkable cells carry hexadecimal costs (0–F).
 *   - Keep two integer grids:
 *       * cost[r][c] => cost to step on that cell, -1 means blocked (wall)
 *       * step[r][c] => visit order number along the successful path (0 = unvisited)
 *   - Expose helpers to query costs, mark/clear step numbers, and render console views
 *     for both the original cost map and the numbered steps.
 *
 * File format (rectangular, all rows equal width):
 *   x!xxxxx
 *   X3x888#
 *   X218xxx
 *   xxxxxxx
 *
 * Legend:
 *   # = start         (walkable, cost 0)
 *   ! = end           (walkable, cost 0)
 *   x or X = blocked  (unvisitable, cost -1)
 *   0..9, A..F = walkable with cost (hex) 0..15
 *
 * Which Goal #3 items this file covers:
 *   (1) Numbered array of steps & backtracking support:
 *       - Provides step[r][c] to hold the visit order (backtracking will clear these via clearVisitedAt).
 *       - Actual recursion/backtracking logic lives in the solver (Goal 3 driver), not here.
 *   (2) Data structure revision from boolean -> int costs + step numbers:
 *       - Implements int[][] cost (with -1 for blocked) and int[][] step (0 = unvisited).
 *       - Adds getCost(r,c), getVisited(r,c), setVisited(r,c,visitNum).
 *   (3) Track steps and (solver will) track total cost:
 *       - step[r][c] supplies the step numbering. Summing costs happens in the solver.
 *   (4) New data-file format with hex costs:
 *       - Constructor parses '#', '!', 'x/X', and hex 0–F as required.
 *   (5) Termination on reaching end and (6) printing numbered solution:
 *       - Handled by the Goal 3 solver/test files. This class provides the data + renderers.
 */

public class finderMap {

    // Raw symbols as provided (for reference / cost rendering)
    private final char[][] map;

    // Goal 3 data
    private final int[][] cost;  // -1 = blocked; else 0..15
    private final int[][] step;  // 0 = not visited; else visit order number

    private final int rows;
    private final int cols;

    /**
     * constructor(String mapString)
     *
     * Responsibility:
     *   - Normalize line endings (\r\n, \r) to \n so files from different OSes load the same.
     *   - Split into rows; enforce rectangular shape (all rows same width).
     *   - Copy raw symbols into map[r][c].
     *   - Parse symbols into cost[r][c]:
     *       x/X  -> -1 (blocked)
     *       #/!  -> 0  (start/end; both walkable with zero cost)
     *       0..9,A..F -> 0..15 (hex cost)
     *   - Initialize step[r][c] = 0 (unvisited). The solver will write step numbers during traversal.
     *
     * Error cases we handle up front:
     *   - null input
     *   - empty map
     *   - empty first line
     *   - non-rectangular input
     *   - invalid symbol (anything not x/X/#/!/0..9/A..F)
     */
    public finderMap(String mapString) {
        if (mapString == null) {
            throw new IllegalArgumentException("Map string cannot be null.");
        }

        // Normalize Windows/Mac endings to '\n' and split.
        String normalized = mapString.replace("\r\n", "\n").replace("\r", "\n");
        String[] lines = normalized.split("\n", -1);

        // If the file ends with a newline, drop the trailing empty row.
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

        this.map  = new char[rows][cols];
        this.cost = new int[rows][cols];
        this.step = new int[rows][cols];

        // Parse characters into cost grid and copy raw map.
        for (int r = 0; r < rows; r++) {
            char[] rowChars = lines[r].toCharArray();
            System.arraycopy(rowChars, 0, this.map[r], 0, cols);

            for (int c = 0; c < cols; c++) {
                char ch = rowChars[c];
                if (ch == 'x' || ch == 'X') {
                    cost[r][c] = -1; // blocked
                } else if (ch == '#') {
                    cost[r][c] = 0;  // start
                } else if (ch == '!') {
                    cost[r][c] = 0;  // end
                } else {
                    int v = hexValue(ch); // A..F or 0..9 => 0..15
                    if (v < 0 || v > 15) {
                        throw new IllegalArgumentException(
                            "Invalid map symbol '" + ch + "' at (" + r + "," + c + ")"
                        );
                    }
                    cost[r][c] = v;
                }
                step[r][c] = 0; // unvisited
            }
        }
    }

    /**
     * Find the position of start or end.
     * c == 's' -> start '#'
     * c == 'e' -> end '!'
     * Returns {row, col}. If not found, returns {0, 0}.
     */
    public int[] getStartOrEnd(char c) {
        int[] position = {0, 0};
        for (int i = 0; i < map.length; ++i) {
            for (int j = 0; j < map[i].length; ++j) {
                if (c == 's') {
                    if (map[i][j] == '#') {
                        position[0] = i;
                        position[1] = j;
                        return position;
                    }
                } else if (c == 'e') {
                    if (map[i][j] == '!') {
                        position[0] = i;
                        position[1] = j;
                        return position;
                    }
                }
            }
        }
        return position;
    }

    // ===== Goal 3 API =====

    /** Returns true if (row,col) is walkable (i.e., cost >= 0). */
    public boolean getPath(int row, int col) {
        if (!inBounds(row, col)) return false;
        return cost[row][col] >= 0;
    }

    /** Cost of stepping on (row,col); -1 means blocked. */
    public int getCost(int row, int col) {
        checkBounds(row, col);
        return cost[row][col];
    }

    /** Visit order number at (row,col); 0 means not visited. */
    public int getVisited(int row, int col) {
        checkBounds(row, col);
        return step[row][col];
    }

    /**
     * Mark (row,col) with the given visit number.
     * Only succeeds on walkable squares (cost >= 0). Returns true if set, false otherwise.
     */
    public boolean setVisited(int row, int col, int visitNum) {
        checkBounds(row, col);
        if (cost[row][col] < 0) return false; // cannot visit blocked
        step[row][col] = visitNum;
        return true;
    }

    /** Clear the visit mark at (row,col) (set back to 0). */
    public void clearVisitedAt(int row, int col) {
        checkBounds(row, col);
        step[row][col] = 0;
    }

    /** Clear all visit marks. */
    public void clearAllVisited() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                step[r][c] = 0;
            }
        }
    }

    // ===== Renderers =====

    /** Render the original cost map as given (x/#/!/hex). */
    public String renderCosts() {
        StringBuilder sb = new StringBuilder(rows * (cols + 1));
        for (int r = 0; r < rows; r++) {
            sb.append(map[r]);
            if (r < rows - 1) sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * Render the steps as a numbered array (console-friendly).
     * - Blocked cells -> " xx"
     * - Unvisited walkable -> "  ."
     * - Visited cells -> right-aligned step number in width 3
     */
    public String renderSteps() {
        StringBuilder sb = new StringBuilder(rows * cols * 3 + rows);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cost[r][c] < 0) {
                    sb.append(" xx");
                } else {
                    int k = step[r][c];
                    if (k == 0) sb.append("  .");
                    else sb.append(String.format("%3d", k));
                }
            }
            if (r < rows - 1) sb.append('\n');
        }
        return sb.toString();
    }

    /**
     * For Goal 3, getString() mirrors the numbered-step view (dead ends should be erased by the solver).
     */
    public String getString() {
        return renderSteps();
    }

    // ===== Bounds + utilities =====

    private boolean inBounds(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    private void checkBounds(int r, int c) {
        if (!inBounds(r, c)) {
            throw new IndexOutOfBoundsException("Out of bounds: (" + r + "," + c + ")");
        }
    }

    private static int hexValue(char ch) {
        if (ch >= '0' && ch <= '9') return ch - '0';
        char u = Character.toUpperCase(ch);
        if (u >= 'A' && u <= 'F') return 10 + (u - 'A');
        return -1;
    }
}
