import java.util.*;
// import java.util.ArrayList;

public class BFSAlgo {
  public static Queue<State> frontier;
  public static ArrayList<String> list;

  public State solve(State initialState) {
    frontier = new LinkedList<State>();
    list = new ArrayList<String>(initialState.getPossibleActions());

    for (int i=0; i!=list.size(); i++){
      State state = new State(initialState, list.get(i));
      frontier.add(state);
    }

    State currentState = new State(null, null);
    Iterator iterator = frontier.iterator();
    while(iterator.hasNext()){
      currentState = frontier.remove();
      if(currentState.isWin()){
        return currentState;
      }
      else {
        for (int i=0; i!=list.size(); i++){
          frontier.add(currentState.result(list.get(i)));
        }
      }
    }
    return currentState;
  }
}
