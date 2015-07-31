package def;
import java.util.ArrayList;
import java.util.Random;

/**
 * Nodes make up the tree of possible states in a game. Moving down the tree is equivalent to progressing further
 * in a game, in this case Poker Squares. There are chance nodes, which represent events the player has no control
 * over, like drawing a card, and choice nodes, which represent a player's potential decisions. General node contains
 * information common to both chance and choice nodes
 * 
 * @author Robert Arrington
 * @author Steven Bogaerts
 * @author Clay Langley
 */
public abstract class xMCTSPruningNode {

	
	public ArrayList<xMCTSPruningNode> nextMoves;
	protected xMCTSStringGameState nodeGameState;
	protected int timesVisited;
	protected float score;
	protected final Random r;
	protected boolean expanded = false;
	public Card[] nodeDeck;
	protected float constant;
	protected boolean[] nodeCanDraw;
	   
	/**
	 * instantiates a node
	 * 
	 * @param nodeGameState the state this node represents
	 * @param nodeDeck the order of cards used to create this node
	 * @param constant the Cp value used for UCT calculation
	 * @param nodeCanDraw the cards still available to be drawn from this node's state
	 */
	public xMCTSPruningNode(xMCTSStringGameState nodeGameState, Card[] nodeDeck, float constant, boolean[] nodeCanDraw)
	   {
	      this.nodeGameState = nodeGameState;
	      this.nodeDeck = nodeDeck;
	      timesVisited = 0;
	      score = 0;
	      nextMoves = new ArrayList<xMCTSPruningNode>();
	      r = new Random();
	      this.constant = constant;
	      this.nodeCanDraw = nodeCanDraw;
	   }
	
	/**
	 * utilizes a formula depending on whether the node is chance or choice to select a child
	 * @return the child chosen by the formula
	 */
	public abstract xMCTSPruningNode bestSelection();
	
		/**
	    * Returns the number of times this node has been visited.
	    *
	    * @return The number of times this node has been visited.
	    */
	public int getTimesVisited()
	   {
	      return timesVisited;
	   }
	
	/**
	 * return this node's strongest child as determined by MCTS
	 * @return this node's child with the best Q/N value
	 */
	public abstract xMCTSPruningNode bestMove();
	
	   /**
	    * Gets the GameState for this node.
	    *
	    * @return the GameState for this node.
	    */
	   public xMCTSStringGameState getState()
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
	   
	   /**
	    * Sets this node's score
	    * 
	    * @param score the score to adjust this node's score to
	    */
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
	   public xMCTSPruningNode findChildNode(xMCTSStringGameState s)
	   {
	      for (xMCTSPruningNode x : nextMoves) {
	         if (x.getState().equals(s)) {
	            return x;
	         }
	      }
	      throw new NullPointerException("findChildNode wants to return null");
	   }
	   
	   /**
	    * Creates a child of the current Node that has a given GameState by adding it into nextMoves.
	    *
	    * @param s is the state to be created.
	    * @param deck is the order of cards used to get to this node
	    * @param canDraw the cards still possible to be drawn from this node's state
	    */
	   public abstract void createChildNode(xMCTSStringGameState s, Card[] deck, boolean[] canDraw);
	   
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
	   public xMCTSPruningNode getRandomChild()
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
	   
	   public abstract boolean choiceNode();
}
