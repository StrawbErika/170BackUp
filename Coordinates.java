public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int y, int x) {
        this.x = x;
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public int getX() {
        return this.x;
    }

    public String toString() {
        return "{" + this.y + ", " + this.x + "}";
    }
}