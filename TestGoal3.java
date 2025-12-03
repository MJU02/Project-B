/*
 Project B — Goal Set #3: Counting moves and backtracking
 Authors: Mohammed Uddin, William Yang

 Purpose:
   - Load a weighted maze from file.
   - Use finderMap (Goal 3 version) to hold costs and numbered steps.
   - Run the knightsTour-style backtracking solver (backtrack3).
   - Print: original cost map, numbered solution (dead ends erased), and total cost.

 Which Goal #3 items this file demonstrates:
   (1) Shows the cleaned, numbered path after true backtracking.
   (3) Displays step numbers and total cost of the successful route.
   (6) Acts as prjBtestGoal3.jar entry point for console output.

 Usage:
   javac finderMap.java backtrack3.java TestGoal3.java
   java TestGoal3 datafile3.txt
   # or as a jar:
   jar cfe prjBtestGoal3.jar TestGoal3 *.class
   java -jar prjBtestGoal3.jar datafile3.txt
*/

import java.nio.file.Files;
import java.nio.file.Path;

public class TestGoal3 {
    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: java TestGoal3 datafile3.txt");
            return;
        }

        // Load file and build Goal-3 finderMap
        String contents = Files.readString(Path.of(args[0]));
        finderMap fm = new finderMap(contents);

        // Show original cost/hex layout
        System.out.println("== Cost Map ==");
        System.out.println(fm.renderCosts());

        // Run knightsTour-style recursive backtracking
        boolean ok = backtrack.solve(fm);

        if (!ok) {
            System.out.println("\nNo path found from # to ! (check map).");
            return;
        }

        // Print numbered solution (dead ends erased) and total cost
        System.out.println("\n== Steps (dead ends erased) ==");
        System.out.println(fm.renderSteps());

        System.out.println("\nTotal cost = " + backtrack.getFinalCost());
    }
}
