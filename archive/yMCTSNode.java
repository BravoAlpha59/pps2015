package archive;
import Card;
import xMCTSGameState;

import java.util.ArrayList;
import java.util.Random;


public abstract class yMCTSNode {

	//public ArrayList<? extends yMCTSNode> nextMoves;
	public ArrayList<yMCTSNode> nextMoves;
	protected xMCTSGameState nodeGameState;
	protected int timesVisited;
	protected float score;
	protected final Random r;
	protected boolean expanded = false;
	public Card[] nodeDeck;
	protected int verbosity;
	protected float constant;
	   
	public yMCTSNode(xMCTSGameState nodeGameState, Card[] nodeDeck, int verbosity, float constant)
	   {
	      this.nodeGameState = nodeGameState;
	      this.nodeDeck = nodeDeck;
	      timesVisited = 0;
	      score = 0;
	      nextMoves = null;
	      r = new Random();
	      this.verbosity = verbosity;
	      this.constant = constant;
	   }
	
	public abstract void expand(ArrayList<? extends xMCTSGameState> possibleMoves, String tab, boolean print); //Have expand take a game instead? then it can create its own arrayList using the game
	
	
	public abstract yMCTSNode bestSelection(boolean myTurn, String tab);
	
	/**
	    * Returns the number of times this node has been visited.
	    *
	    * @return The number of times this node has been visited.
	    */
	public int getTimesVisited()
	   {
	      return timesVisited;
	   }
	
	public abstract yMCTSNode bestMove();
	
	   /**
	    * Gets the GameState for this node.
	    *
	    * @return the GameState for this node.
	    */
	   public xMCTSGameState getState()
	   {
	      return nodeGameState;
	   }
	   
	   /**
	    * Returns the string representation of the GameState of this node.
	    *
	    * @return the string representation of the GameState of this node.
	    */
	   @Override
	   public String toString()
	   {
	      return nodeGameState.toString();
	   }
	   
	   /**
	    * Increases the number of visits recorded to this node by one.
	    */
	   public synchronized void visit()
	   {
	      timesVisited++;
	   }
	   
	   public void setScore(float score) {
		   this.score = score;
	   }

	   /**
	    * Gets the score for this node.
	    *
	    * @return the score for this node.
	    */
	   public float getScore()
	   {
	      return score;
	   }
	   
	   /**
	    * Finds a child of the current Node that has a given GameState.
	    *
	    * @param s is the state to be searched for.
	    * @return matching node if found, null otherwise.
	    */
	   public yMCTSNode findChildNode(xMCTSGameState s)
	   {
	      for (yMCTSNode x : nextMoves) {
	         if (x.getState().equals(s)) {
	            return x;
	         }
	      }
	      throw new NullPointerException("findChildNode wants to return null");
	   }
	   
	   /**
	    * Returns whether this node is a leaf (has no child nodes).
	    *
	    * @return whether this node is a leaf (has no child nodes).
	    */
	   public boolean isLeaf()
	   {
	      return nextMoves == null || nextMoves.isEmpty();
	   }

	   /**
	    * Returns a random child node of this node.
	    *
	    * @return a random child node of this node.
	    */
	   public yMCTSNode getRandomChild()
	   {
	      int rand = r.nextInt(nextMoves.size());
	      return nextMoves.get(rand);
	   }
	   
	   /**
	    * Searches through the String state for the card that resulted in this node's state, then calculates its location in row/column form
	    * 
	    * @param card the card to check the String state for
	    * @return the row and column representing the location of the passed card in the String state
	    */
	   public int[] bestMoveLocation(Card card) {
		   int temp = nodeGameState.toString().indexOf(card.toString());
		   if (temp != -1) {
			   temp /= 2;
			   int[] retVal = {temp/5, temp%5};
			   return retVal;
		   }
		   return null;
	   }
	   
	   public yMCTSNode mostVisitedChild() {
		   int mostVisited = -1, mostVisitedIndex = 0;
		   for (int i = 0; i < nextMoves.size(); i++) {
		         if (nextMoves.get(i).timesVisited > mostVisited) {
		             mostVisited = nextMoves.get(i).timesVisited;
		             mostVisitedIndex = i;
		          }
		   }
		   return nextMoves.get(mostVisitedIndex);
	   }
	   
	   public abstract boolean choiceNode();
}
