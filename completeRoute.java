/*
 Project B — Goal Set #4: Find Lowest Cost Path using Backtracking
 Authors: William Yang, Mohammed Uddin.

 Purpose:
   - This is the completeRoute class required by Goal Set 4.
*/

public class completeRoute {
    private int cost;
    private String mapString;
    //private boolean hasValue = false;

    // Previous implementation
    /*
    public void setTrue() {
        hasValue = true;
    }

    public boolean hasPath() {
        return hasValue;
    }

    public void setCost(int i) {
        cost = i;
    }

    public void setPath(String s) {
        mapString = s;
    }*/

    /**
     * Constructor for completeRoute class.
     * 
     * @param i
     * @param s
     */
    public completeRoute(int i, String s) {
        this.cost = i;
        this.mapString = s;
    }

    /**
     * Returns the cost of a route.
     * 
     * @return int
     */
    public int getCost() {
        return cost;
    }

    /**
     * Returns the path, which includes the steps taken to reach the end.
     * 
     * @return
     */
    public String getPath() {
        return mapString;
    }
}
