import java.util.*;
// import java.util.ArrayList;

public class DFSAlgo {
  public static Stack<State> frontier;
  public static ArrayList<String> list;
  public static ArrayList<State> exploredList;

  public static State solve(State initialState) {
    frontier = new Stack<State>();
    exploredList = new ArrayList<State>();
    list = new ArrayList<String>(initialState.getPossibleActions());

    for (int i=0; i!=list.size(); i++){
      State state = new State(initialState, list.get(i));
      frontier.push(state);
    }

    State currentState = null;
    while(frontier.size()>0){
      currentState = frontier.pop();
      list= currentState.getPossibleActions();
      if(currentState.isWin()){
        return currentState;
      }
      else {
        for (int i=0; i!=list.size(); i++){
          State newState = currentState.result(currentState, list.get(i));
          // String [][] board = newState.getState();
          boolean inList = false;

          for(int j = 0; j!=exploredList.size(); j++){
            if(newState.toString().equals(exploredList.get(j).toString())){
              inList = true;
              break;
            }
          }

          if(!inList){
            exploredList.add(newState);
            frontier.push(newState);
          }
        }
      }
    }
    return currentState;
  }
}
