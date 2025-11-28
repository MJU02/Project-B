import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestGoal1 {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java TestGoal1 datafile.txt");
            //return;
        }

        //String contents = Files.readString(Path.of(args[0]));
        String contents = Files.readString(Path.of("datafile.txt"));
        finderMap fm = new finderMap(contents);

        System.out.println("=== Loaded Map ===");
        System.out.println(fm.getString());

        // Find and visit up to 3 'p' cells (inline scan, no helper methods)
        int visitedCount = 0;
        String[] lines = contents.split("\n", -1);

        
        outer: // label to break from nested loop
        for (int r = 0; r < lines.length; r++) {
            for (int c = 0; c < lines[r].length(); c++) {
                if (lines[r].charAt(c) == 'p') {
                    boolean ok = fm.setVisited(r, c);
                    System.out.println("visit p @ (" + r + "," + c + ") -> " + ok);
                    if (++visitedCount == 3) break outer;
                }
            }
        }

        // Try to visit the first 'x' we see (should fail)
        boolean triedWall = false;
        for (int r = 0; r < lines.length && !triedWall; r++) {
            for (int c = 0; c < lines[r].length() && !triedWall; c++) {
                if (lines[r].charAt(c) == 'x') {
                    boolean ok = fm.setVisited(r, c);
                    System.out.println("visit x @ (" + r + "," + c + ") -> " + ok + "  (expected false)");
                    triedWall = true;
                }
            }
        }

        System.out.println("\n=== Current State (visited shown as 'v') ===");
        System.out.println(fm.getString());
    }
}

