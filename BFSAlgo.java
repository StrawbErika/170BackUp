import java.util.*;
// import java.util.ArrayList;

public class BFSAlgo {
  public static Queue<State> frontier;
  public static ArrayList<String> list;
  public static ArrayList<State> exploredList;

  public static State solve(State initialState) {
    frontier = new LinkedList<State>();
    exploredList = new ArrayList<State>();
    list = new ArrayList<String>(initialState.getPossibleActions());

    for (int i=0; i!=list.size(); i++){
      State state = new State(initialState, list.get(i));
      frontier.add(state);
    }

    State currentState = null;
    while(frontier.size()>0){
      currentState = frontier.remove();
      list= currentState.getPossibleActions();
      if(currentState.isWin()){
        return currentState;
      }
      else {
        for (int i=0; i!=list.size(); i++){
          State newState = currentState.result(currentState, list.get(i));
          if(exploredList.contains(newState) == false){
            exploredList.add(newState);
            frontier.add(newState);
          }
        }
      }
    }
    return currentState;
  }
}
