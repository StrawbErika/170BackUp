import java.util.ArrayList;

public class State {
    public static final String BOX = "b";
    public static final String BOX_STORAGE = "B";
    public static final String FLOOR = "e";
    public static final String STORAGE = "s";
    public static final String WALL = "w";
    public static final String NONE = "x";
    public static final String KEEPER = "k";
    public static final String KEEPER_STORAGE = "K";

    private String[][] state;
    private State parentState;
    private String action;
    private ArrayList<String> actionsNeeded;

    private Coordinates keeperPosition;
    private Coordinates parentKeeperPosition;

//parentState.state -> to get the board of the parentState
    public State(String[][] state, State parentState, String action) {
        this.parentState = parentState;
        this.state = state; //board
        this.action = action;

        if (parentState!=null){
          this.actionsNeeded = new ArrayList(parentState.getActionsNeeded());
          actionsNeeded.add(action); // add the action for this state
        }
        else{
          this.actionsNeeded = new ArrayList();
        }

        // find keeper position and save it
        for (int i = 0; i < Game.ROWS; i++) {
            for (int j = 0; j < Game.COLS; j++) {
                if (
                    this.state[i][j].equals(State.KEEPER) ||
                    this.state[i][j].equals(State.KEEPER_STORAGE)
                ) {
                    this.keeperPosition = new Coordinates(i, j); //position of keeper
                }
            }
        }

        if(action!=null){
          if(action.equals("up")){
            System.out.println("up!");
            this.moveUp();
          }
          else if(action.equals("down")){
            System.out.println("down!");
            this.moveDown();
          }
          else if(action.equals("left")){
            System.out.println("left!");
            this.moveLeft();
          }
          else if(action.equals("right")){
            System.out.println("right!");
            this.moveRight();
          }
        }
    }

    public State (State parentState, String action){
      this(parentState.getState(), parentState, action);
    }

    public State(String[][] state) {
      this(state, null, null); //call constructor
    }

    public String getValue(int i, int j) {
        return this.state[i][j];
    }

    public ArrayList<String> getActionsNeeded(){
      return this.actionsNeeded;
    }

    public State getParentState(){
      return this.parentState;
    }

    public String[][] getState(){
      return this.state;
    }

    public void move(int y, int x) {
        // get value and StateStateycoordinates of the destination grid, and the next after it
        Coordinates currentCoordinates = new Coordinates(keeperPosition.getY(), keeperPosition.getX());
        String currentValue = state[currentCoordinates.getY()][currentCoordinates.getX()];

        Coordinates nextCoordinates = new Coordinates(keeperPosition.getY() + y, keeperPosition.getX() + x);
        String nextValue = state[nextCoordinates.getY()][nextCoordinates.getX()];

// is y or x != 0? Yes y or x * 2, No retain y or x
        Coordinates nextNextCoordinates = new Coordinates(keeperPosition.getY() + (y != 0 ? y * 2 : y), keeperPosition.getX() + (x != 0 ? x * 2 : x));
        String nextNextValue;
        try {
          nextNextValue = state[nextNextCoordinates.getY()][nextNextCoordinates.getX()];
        }
        catch(Exception e) {
          nextNextValue = null;
        }

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

    public ArrayList<String> getPossibleActions(){
      ArrayList<String> possibleActions = new ArrayList<String>();

      if(canMove(-1,0)){
        System.out.println("up!!!!!!!!!!!!!!!!!!!!!!!!!");
        possibleActions.add("up");
      }
      if(canMove(1,0)){
        System.out.println("do!!!!!!!!!!!!!!!!!!!!!!!!!");
        possibleActions.add("down");
      }
      if(canMove(0,-1)){
        System.out.println("le!!!!!!!!!!!!!!!!!!!!!!!!!");
        possibleActions.add("left");
      }
      if(canMove(0,1)){
        System.out.println("ri!!!!!!!!!!!!!!!!!!!!!!!!!");
        possibleActions.add("right");
      }

      return possibleActions;
    }

    public boolean canMove(int y, int x){
      // get value and coordinates of the destination grid, and the next after it
      Coordinates currentCoordinates = new Coordinates(keeperPosition.getY(), keeperPosition.getX());
      String currentValue = state[currentCoordinates.getY()][currentCoordinates.getX()];
      System.out.println("keeperPosition: " + keeperPosition.getY() + " " + keeperPosition.getX());

      Coordinates nextCoordinates = new Coordinates(keeperPosition.getY() + y, keeperPosition.getX() + x);
      String nextValue = state[nextCoordinates.getY()][nextCoordinates.getX()];

      System.out.println("NextValue: " + nextValue);
// is y or x != 0? Yes y or x * 2, No retain y or x
      Coordinates nextNextCoordinates = new Coordinates(keeperPosition.getY() + (y != 0 ? y * 2 : y), keeperPosition.getX() + (x != 0 ? x * 2 : x));
      String nextNextValue;
      try {
        nextNextValue = state[nextNextCoordinates.getY()][nextNextCoordinates.getX()];
      }
      catch(Exception e) {
        nextNextValue = null;
      }

      System.out.println("nextNextValue: " + nextNextValue);


      if(nextValue.equals(State.WALL)){
        return false;
      }
      else if(nextNextValue != null){
        if((nextValue.equals(State.BOX) && nextNextValue.equals(State.WALL)) ||
          (nextValue.equals(State.BOX_STORAGE) && nextNextValue.equals(State.WALL))){ // state box box
          return false;
        }
        else if((nextValue.equals(State.BOX_STORAGE) && nextNextValue.equals(State.BOX)) ||
              (nextValue.equals(State.BOX) && nextNextValue.equals(State.BOX)) ||
              (nextValue.equals(State.BOX_STORAGE) && nextNextValue.equals(State.BOX_STORAGE))){
            return false;
        }
        else{
          return true;
        }
      }
      else if(nextNextValue == null){
        return false;
      }
      else{
        return true;
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

    public State result(State currentState, String action){
      State result = new State(currentState, action);
      return result;
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
              if(this.state[i][j].equals(State.FLOOR)){
                out += " ";
              }
              else{
                out += this.state[i][j];
              }
            }
            out += "\n";
        }

        return out;
    }

    public int pathCost(){
      return this.actionsNeeded.size();
    }

    public Coordinates getKeeper(){
      return this.keeperPosition;
    }
}

/*
PathCost(path) function: A function that will return the cost of input path.
A path/solution in this problem basically consists of the sequence of moves
made by the keeper. For this particular problem and game, the pathcost would
be the number of moves the keeper had made since the initial state.
*/
