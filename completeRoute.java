public class completeRoute {
    private int cost;
    private String mapString;
    private boolean hasValue = false;

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
    }

    public int getCost() {
        return cost;
    }

    public String getPath() {
        return mapString;
    }
}
