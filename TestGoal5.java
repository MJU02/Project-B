/*
 Project B — Goal Set #5: GUI
 Authors: Mohammed Uddin, William Yang

 Purpose:
   - Load a weighted maze file into our existing finderMap (Goal 3 structure).
   - Display it as a grid of small JavaFX Labels.
   - On "Solve", refer to the knightsTour-style solver in backtrack.java
     which numbers the successful path and erases dead ends.
   - Toggle between a COSTS view (x/#/!/0..F) and a STEPS view (numbered path).
   - Show total cost reported by backtrack.getFinalCost() preferably on the bottom of the window. 


 Goal 5 checklist:
   1) Results on a GUI 
   2) Small labels on a grid; labels change text/color
   3) Runnable Jar instructions below 
   4) Jar can be submitted when fully tested 


*/

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Path;

public class TestGoal5 extends Application {

    // Model
    private finderMap fm;
    private int totalCost = 0;
    private boolean solved = false;

    // UI
    private GridPane grid;
    private Label status;
    private TextField fileField;
    private RadioButton viewCosts, viewSteps;
    private ToggleGroup viewToggle;

    // Cache start/end for coloring
    private int startR = -1, startC = -1;
    private int endR   = -1, endC   = -1;

    @Override
    public void start(Stage stage) {
        // --- Top controls ---
        fileField = new TextField();
        fileField.setPromptText("Path to data file (e.g., datafile.txt)");
        var args = getParameters().getRaw();
        if (!args.isEmpty()) fileField.setText(args.get(0));

        Button loadBtn  = new Button("Load");
        Button solveBtn = new Button("Solve");
        Button resetBtn = new Button("Reset");

        viewToggle = new ToggleGroup();
        viewCosts = new RadioButton("Costs");
        viewSteps = new RadioButton("Steps");
        viewCosts.setToggleGroup(viewToggle);
        viewSteps.setToggleGroup(viewToggle);
        viewCosts.setSelected(true);

        HBox top = new HBox(8,
            new Label("File:"), fileField, loadBtn, solveBtn, resetBtn,
            new Separator(), new Label("View:"), viewCosts, viewSteps
        );
        top.setPadding(new Insets(10));
        top.setAlignment(Pos.CENTER_LEFT);

        // --- Grid + status ---
        grid = new GridPane();
        grid.setHgap(2);
        grid.setVgap(2);
        grid.setPadding(new Insets(10));
        grid.setAlignment(Pos.CENTER);

        status = new Label("Load a file to begin.");
        HBox bottom = new HBox(status);
        bottom.setPadding(new Insets(8, 10, 12, 10));

        BorderPane root = new BorderPane(grid, top, null, bottom, null);

        // Wire actions
        loadBtn.setOnAction(e -> loadFile());
        solveBtn.setOnAction(e -> runBacktrack());
        resetBtn.setOnAction(e -> resetSteps());
        viewToggle.selectedToggleProperty().addListener((obs, o, n) -> render());

        // Optional auto-load from arg
        if (!fileField.getText().isBlank()) loadFile();

        Scene scene = new Scene(root, 900, 600);
        stage.setTitle("Project B — Goal 5 (GUI) — Mohammed Uddin & William Yang");
        stage.setScene(scene);
        stage.show();
    }

    private void loadFile() {
        try {
            String p = fileField.getText().trim();
            if (p.isEmpty()) {
                status.setText("Enter a data file path.");
                return;
            }
            String contents = Files.readString(Path.of(p));
            fm = new finderMap(contents);

            int[] s = fm.getStartOrEnd('s'); startR = s[0]; startC = s[1];
            int[] e = fm.getStartOrEnd('e'); endR   = e[0]; endC   = e[1];

            solved = false; totalCost = 0;
            render();
            status.setText("Loaded: " + p + " — switch view or press Solve.");
        } catch (Exception ex) {
            ex.printStackTrace();
            status.setText("Error loading file: " + ex.getMessage());
            grid.getChildren().clear();
        }
    }

    private void runBacktrack() {
        if (fm == null) { status.setText("Load a file first."); return; }
        try {
            // Clean slate and set start step
            fm.clearAllVisited();
            fm.setVisited(startR, startC, 1);

            // Call our existing solver (knightsTour-style backtracking lives in backtrack.java)
            solved = backtrack.solve(fm);
            totalCost = solved ? backtrack.getFinalCost() : 0;

            if (solved) {
                status.setText("Solved. Total cost = " + totalCost + ". Switch to Steps view to see the path.");
            } else {
                status.setText("No path found from # to !");
            }
            render();
        } catch (Throwable t) {
            t.printStackTrace();
            status.setText("Solver error: " + t.getMessage());
        }
    }

    private void resetSteps() {
        if (fm == null) return;
        fm.clearAllVisited();
        solved = false;
        totalCost = 0;
        render();
        status.setText("Steps cleared.");
    }

    private void render() {
        grid.getChildren().clear();
        if (fm == null) return;

        boolean showCosts = viewCosts.isSelected();
        int rows = countRows();
        int cols = countCols();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Label cell = new Label();
                cell.setMinSize(34, 28);
                cell.setPrefSize(34, 28);
                cell.setAlignment(Pos.CENTER);
                cell.setStyle("-fx-font-family: 'Consolas', 'Monospaced'; -fx-font-size: 12; -fx-border-color: #222; -fx-border-width: 0.5;");

                if (showCosts) setCostCellAppearance(cell, r, c);
                else           setStepCellAppearance(cell, r, c);

                grid.add(cell, c, r);
            }
        }
    }

    // COSTS view cell
    private void setCostCellAppearance(Label cell, int r, int c) {
        try {
            int cost = fm.getCost(r, c);
            if (cost < 0) {
                cell.setText("x");
                cell.setStyle(cell.getStyle() + "; -fx-background-color: #222; -fx-text-fill: #ddd;");
                return;
            }
            if (r == startR && c == startC) {
                cell.setText("#");
                cell.setStyle(cell.getStyle() + "; -fx-background-color: #0e3a8a; -fx-text-fill: white;");
                return;
            }
            if (r == endR && c == endC) {
                cell.setText("!");
                cell.setStyle(cell.getStyle() + "; -fx-background-color: #065f46; -fx-text-fill: white;");
                return;
            }
            cell.setText(toHexDigit(cost));
            cell.setStyle(cell.getStyle() + "; -fx-background-color: #f3f4f6; -fx-text-fill: #111;");
        } catch (IndexOutOfBoundsException ex) {
            cell.setText("?");
            cell.setStyle(cell.getStyle() + "; -fx-background-color: #fee2e2; -fx-text-fill: #111;");
        }
    }

    // STEPS view cell
    private void setStepCellAppearance(Label cell, int r, int c) {
        try {
            int cost = fm.getCost(r, c);
            if (cost < 0) {
                cell.setText("xx");
                cell.setStyle(cell.getStyle() + "; -fx-background-color: #222; -fx-text-fill: #ddd;");
                return;
            }
            int k = fm.getVisited(r, c);
            if (k == 0) {
                if (r == startR && c == startC) {
                    cell.setText("#");
                    cell.setStyle(cell.getStyle() + "; -fx-background-color: #0e3a8a; -fx-text-fill: white;");
                } else if (r == endR && c == endC) {
                    cell.setText("!");
                    cell.setStyle(cell.getStyle() + "; -fx-background-color: #065f46; -fx-text-fill: white;");
                } else {
                    cell.setText(".");
                    cell.setStyle(cell.getStyle() + "; -fx-background-color: #f8fafc; -fx-text-fill: #444;");
                }
            } else {
                cell.setText(Integer.toString(k));
                cell.setStyle(cell.getStyle() + "; -fx-background-color: #fde68a; -fx-text-fill: #111; -fx-font-weight: bold;");
            }
        } catch (IndexOutOfBoundsException ex) {
            cell.setText("?");
            cell.setStyle(cell.getStyle() + "; -fx-background-color: #fee2e2; -fx-text-fill: #111;");
        }
    }

    // Helpers
    private int countRows() {
        String costs = fm.renderCosts();
        int rows = 1;
        for (int i = 0; i < costs.length(); i++) if (costs.charAt(i) == '\n') rows++;
        return rows;
    }

    private int countCols() {
        String costs = fm.renderCosts();
        int idx = costs.indexOf('\n');
        return (idx < 0) ? costs.length() : idx;
    }

    private String toHexDigit(int v) {
        if (v >= 0 && v <= 9) return Integer.toString(v);
        return "" + (char)('A' + (v - 10));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
