import java.util.*;
// import java.util.ArrayList;

public class BFSAlgo {
  public static Queue<State> frontier;
  public static ArrayList<String> list;

  public static State solve(State initialState) {
    frontier = new LinkedList<State>();
    list = new ArrayList<String>(initialState.getPossibleActions());

    for (int i=0; i!=list.size(); i++){
      State state = new State(initialState, list.get(i));
      frontier.add(state);
    }
    State currentState = null;
    while(frontier.size()>0){
      currentState = frontier.remove();
      System.out.println("================================================================================================");
      System.out.println(currentState);
      list= currentState.getPossibleActions();
      System.out.println(list);
      if(currentState.isWin()){
        return currentState;
      }
      else {
        for (int i=0; i!=list.size(); i++){
          System.out.println(list.get(i));
          State newState = currentState.result(currentState, list.get(i));
          System.out.println(newState);
          frontier.add(newState);
        }
      }
      System.out.println("================================================================================================");
    }
    return currentState;
  }
}
