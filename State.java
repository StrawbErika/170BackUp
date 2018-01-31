public class State {
    public static final String BOX = "b";
    public static final String BOX_STORAGE = "B";
    public static final String FLOOR = "e";
    public static final String KEEPER = "k";
    public static final String STORAGE = "s";
    public static final String WALL = "w";
    public static final String NONE = "x";
    public static final String KEEPER_STORAGE = "K";

    private String[][] state;

    private Coordinates keeperPosition;

    public State(String[][] state) {
        this.state = state;

        // find keeper position and save it
        for (int i = 0; i < Game.ROWS; i++) {
            for (int j = 0; j < Game.COLS; j++) {
                if (
                    this.state[i][j].equals(State.KEEPER) ||
                    this.state[i][j].equals(State.KEEPER_STORAGE)
                ) {
                    this.keeperPosition = new Coordinates(i, j);
                }
            }
        }
    }

    public String getValue(int i, int j) {
        return this.state[i][j];
    }

    public void move(int y, int x) {
        // get value and coordinates of the destination grid, and the next after it
        Coordinates currentCoordinates = new Coordinates(keeperPosition.getY(), keeperPosition.getX());
        String currentValue = state[currentCoordinates.getY()][currentCoordinates.getX()];


        Coordinates nextCoordinates = new Coordinates(keeperPosition.getY() + y, keeperPosition.getX() + x);
        String nextValue = state[nextCoordinates.getY()][nextCoordinates.getX()];

// is y or x != 0? Yes y or x * 2, No retain y or x
        Coordinates nextNextCoordinates = new Coordinates(keeperPosition.getY() + (y != 0 ? y * 2 : y), keeperPosition.getX() + (x != 0 ? x * 2 : x));
        String nextNextValue = state[nextNextCoordinates.getY()][nextNextCoordinates.getX()];

// if floor next -> if K now then s-> else floor
        if (nextValue.equals(State.FLOOR)) {
            // update value of destination
            state[nextCoordinates.getY()][nextCoordinates.getX()] = State.KEEPER;

            // update value of source
            if (currentValue.equals(State.KEEPER_STORAGE)) {
                state[currentCoordinates.getY()][currentCoordinates.getX()] = State.STORAGE;
            }
            else {
                state[currentCoordinates.getY()][currentCoordinates.getX()] = State.FLOOR;
            }

            this.keeperPosition = nextCoordinates;
        }

//if storage next then K -> if K then s -> else floor
        else if (nextValue.equals(State.STORAGE)) {
            // update value of destination
            state[nextCoordinates.getY()][nextCoordinates.getX()] = State.KEEPER_STORAGE;

            // update value of source
            if (currentValue.equals(State.KEEPER_STORAGE)) {
                state[currentCoordinates.getY()][currentCoordinates.getX()] = State.STORAGE;
            }
            else {
                state[currentCoordinates.getY()][currentCoordinates.getX()] = State.FLOOR;
            }

            this.keeperPosition = nextCoordinates;
        }

// if B or b -> if next floor/s
        else if (nextValue.equals(State.BOX) || nextValue.equals(State.BOX_STORAGE)) {
            if (nextNextValue.equals(State.FLOOR) || nextNextValue.equals(State.STORAGE)) {

// if next next floor then b -> else s then B
                // update next of destination
                if (nextNextValue.equals(State.FLOOR)) {
                    state[nextNextCoordinates.getY()][nextNextCoordinates.getX()] = State.BOX;
                }
                else if(nextNextValue.equals(State.STORAGE)) {
                    state[nextNextCoordinates.getY()][nextNextCoordinates.getX()] = State.BOX_STORAGE;
                }

// if next b then k -> else B then K
                // update value of destination
                if (nextValue.equals(State.BOX)) {
                    state[nextCoordinates.getY()][nextCoordinates.getX()] = State.KEEPER;
                }
                else if (nextValue.equals(State.BOX_STORAGE)) {
                    state[nextCoordinates.getY()][nextCoordinates.getX()] = State.KEEPER_STORAGE;
                }

//if K then s -> else floor
                // update value of source
                if (currentValue.equals(State.KEEPER_STORAGE)) {
                    state[currentCoordinates.getY()][currentCoordinates.getX()] = State.STORAGE;
                }
                else {
                    state[currentCoordinates.getY()][currentCoordinates.getX()] = State.FLOOR;
                }

                this.keeperPosition = nextCoordinates;
            }
        }
    }

    public void moveUp() {
        this.move(-1, 0);
    }

    public void moveDown() {
        this.move(1, 0);
    }

    public void moveLeft() {
        this.move(0, -1);
    }

    public void moveRight() {
        this.move(0, 1);
    }

    public boolean isWin() {
        // it's win if there's no Game.BOX existing
        for(int i = 0; i < Game.ROWS; i++) {
            for(int j = 0; j < Game.COLS; j++) {
                String currentValue = this.state[i][j];

                if (currentValue.equals(State.BOX)) {
                    return false;
                }
            }
        }

        return true;
    }

    public String toString() {
        String out = "";

        for(int i = 0; i < this.state.length; i++) {
            for(int j = 0; j < this.state.length; j++) {
                out += this.state[i][j];
            }
            out += "\n";
        }

        return out;
    }
}
