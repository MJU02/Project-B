import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/*
 Project B — Goal Set #4: Find Lowest Cost Path using Backtracking
 Authors: William Yang, Mohammed Uddin.

 Purpose:
   - Load a weighted maze from file.
   - Use the flood fill algorithm with backtracking to find all possible route.
   - Out of all the possible route, return the lowest cost route.
*/

public class TestGoal4 {
    //private static completeRoute route = new completeRoute();
    private final static ArrayList<completeRoute> routes = new ArrayList<>();

    /**
     * Main function for running goal set 4.
     * Finds the lowest cost path of any maze.
     * 
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        String contents = Files.readString(Path.of("datafile2.txt"));
        finderMap fm = new finderMap(contents);

        System.out.println("=== Loaded Map ===");
        System.out.println(fm.getString());

        //String[] lines = contents.split("\n", -1);

        floodfill_dfs(fm, fm.getStartOrEnd('s')[0], fm.getStartOrEnd('s')[1], 0, 1);

        if (!routes.isEmpty()) {
            int lowest = 0;
            for (int i = 0; i < routes.size(); ++i) {
                if (routes.get(i).getCost() < routes.get(lowest).getCost()) lowest = i;
            }
            System.out.println("\n== Lowest Cost Path ==");

            System.out.println(routes.get(lowest).getPath());

            System.out.println("\nTotal cost = " + routes.get(lowest).getCost());
        } else {
            System.out.println("\nNo path found from # to ! (check map).");
        }
        /*
        if (route.hasPath()) {
            System.out.println("\n== Lowest Cost Path ==");

            System.out.println(route.getPath());

            System.out.println("\nTotal cost = " + route.getCost());
        } else {
            System.out.println("\nNo path found from # to ! (check map).");
        }
        */
    }

    /**
     * Uses the floodfill algorithm with backtracking to find all possible routes of a maze.
     * Adds all possible routes along with their costs to an arrayList of routes.
     * 
     * @param fm
     * @param x
     * @param y
     * @param cost
     * @param step
     */
    public static void floodfill_dfs(finderMap fm, int x, int y, int cost, int step) {
        // System.out.println("Current position: " + x + "," + y);

        // Goal Set 4: Bounds + walkable
        if (!inBounds(fm, x, y)) return;
        if (!fm.getPath(x, y)) return;             // blocked cell
        if(fm.getVisited(x, y) != 0) return;  // Already walked here

        fm.setVisited(x, y, step);

        if((x == fm.getStartOrEnd('e')[0] && y == fm.getStartOrEnd('e')[1])) {
            // Optimal Implementation without unnecessary objects
            /*
            if (route.hasPath() == false) {
                route.setTrue();
                route.setCost(cost);
                route.setPath(fm.getString());
            } else {
                if (cost < route.getCost()) {
                    route.setCost(cost);
                    route.setPath(fm.getString());
                }
            }*/

            // Implementation required by Goal Set 4
            routes.add(new completeRoute(cost,fm.getString()));
            // System.exit(0); Only used for Goal set 2
        }

        cost += fm.getCost(x, y);
        step += 1;

        if(fm.getPath(x+1, y)) {
            floodfill_dfs(fm, x+1, y, cost, step);
        }
        if(fm.getPath(x-1, y)) {
            floodfill_dfs(fm, x-1, y, cost, step);
        }
        if(fm.getPath(x, y+1)) {
            floodfill_dfs(fm, x, y+1, cost, step);
        }
        if(fm.getPath(x, y-1)) {
            floodfill_dfs(fm, x, y-1, cost, step);
        }

        fm.clearVisitedAt(x, y);
    }

    private static boolean inBounds(finderMap fm, int r, int c) {
        try { fm.getCost(r, c); return true; }
        catch (IndexOutOfBoundsException ex) { return false; }
    }
}
