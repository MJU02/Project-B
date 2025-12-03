/*
 Project B — Goal Set #3: Counting moves and backtracking
 Authors: William Yang,Mohammed Uddin

 Purpose:
   - Depth-first recursive search from start (#) to end (!).
   - Write step numbers during traversal; erase them on dead ends (true backtracking).
   - Accumulate total path cost for the successful route only.

 Which Goal #3 items this file addresses:
   (1) Backtracking + numbered array + erasing wrong paths
       -> Implemented below with knightsTour-style recursion.
   (1b) Recursive algorithm
       -> dfs(...) is recursive and does the backtrack.
   (3) Keep track of sum of costs and step numbers
       -> runningCost accumulates via finderMap.getCost(...); step numbers written/erased.
   (5) Terminate when end is reached
       -> base case: when current cell == end, record total cost and return true.

 Notes about the context of this file:
   - finderMap (Goal 3 version) provides the data structure and helpers:
       getPath / getCost / getVisited / setVisited / clearVisitedAt / clearAllVisited / getStartOrEnd
   - Movement is 4-directional (up, right, down, left)



   Key:
        • # = beginning.
        • ! = end.
        • x = unvisitable. (e.g., a wall for a maze, a sidewalk for a GPS.)
        • { i16: 0 <= i <= F } = pathway with associated cost of 0 to 15 units.
*/

public class backtrack {

    // Results after solve
    private static int finalCost = 0;
    private static boolean solved = false;

    /** Run the Goal 3 solver on the provided map. */
    public static boolean solve(finderMap fm) {
        solved = false;
        finalCost = 0;

        // Locate start/end
        int[] s = fm.getStartOrEnd('s'); // '#'
        int[] e = fm.getStartOrEnd('e'); // '!'

        // Reset any previous markings; start at step 1
        fm.clearAllVisited();
        fm.setVisited(s[0], s[1], 1);

        // Start DFS from '#'; start cost is 0 (per spec: # and ! have cost 0)
        boolean ok = dfs(fm, s[0], s[1], 1, 0, e[0], e[1]);
        solved = ok;
        return ok;
    }

    public static int  getFinalCost() { 
        return finalCost; 
    }
    public static boolean isSolved()  { 
        return solved; 
    }

    /**
     * DFS with true backtracking (knightsTour-style):
     * - Write a step number before recursing.
     * - If the branch fails, ERASE that step number (clearVisitedAt) to hide dead ends.
     *
     * Base case:
     *   if (r,c) == end, capture total cost and bubble up success.
     */
    private static boolean dfs(finderMap fm, int r, int c, int stepNum, int runningCost, int er, int ec) {

        // (5) Terminate when the end is reached
        if (r == er && c == ec) {
            finalCost = runningCost; // end tile cost is 0 per spec, so nothing to add here
            return true;
        }

        // Explore neighbors in fixed order to keep output deterministic
        int[] dr = {-1, 0, 1, 0};
        int[] dc = { 0, 1, 0,-1};

        for (int i = 0; i < 4; i++) {
            int nr = r + dr[i], nc = c + dc[i];

            // Bounds + walkable
            if (!inBounds(fm, nr, nc)) continue;
            if (!fm.getPath(nr, nc))    continue;              // blocked cell
            if (fm.getVisited(nr, nc) != 0) continue;          // already on current path

            // Prepare next step/cost
            int nextStep = stepNum + 1;
            int nextCost = runningCost + fm.getCost(nr, nc);

            // (1) knightsTour-style: write the move number before recursing
            fm.setVisited(nr, nc, nextStep);

            // Recurse
            if (dfs(fm, nr, nc, nextStep, nextCost, er, ec)) {
                return true; // propagate success up the call stack
            }

            // (1) knightsTour-style backtracking: erase the wrong route
            fm.clearVisitedAt(nr, nc);
        }

        // No neighbor led to the end from this cell
        return false;
    }

    // Minimal bounds check using finderMap's getCost to validate indices
    private static boolean inBounds(finderMap fm, int r, int c) {
        try { fm.getCost(r, c); return true; }
        catch (IndexOutOfBoundsException ex) { return false; }
    }
}
