

/**
 * Copyright (c) 2012 Kyle Hughart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Credit for algorithm goes to:
 *
 * Kocsis L. & Szepesvari C. (September 2006). Bandit based Monte-Carlo
 * Planning. Unpublished paper presented European Conference on Machine
 * Learning, Berlin, Germany.
 *
 * Chaslot, Guillaume et al. (October, 2008). Monte-Carlo Tree Search: A New
 * Framework for Game AI. Unpublished paper presented at the Fourth Artificial
 * Intelligence and Interactive Digital Entertainment Conference, Maastricht,
 * The Netherlands.
 */
/**
 * A version of node representing the standard MCTS decision tree. Creates 
 * children based on possible moves left in the board, and uses the UCT formula
 * to make selection decisions.
 *
 * @author Robert Arrington
 * @author Steven Bogaerts
 * @author Clay Langley
 */
import java.util.ArrayList;

public class xMCTSPruningChoiceNode extends xMCTSPruningNode
{

   /**
    * Instantiates an MCTSNode and initializes ranking variables.
    *
    * @param nodeGameState the state this node represents
	* @param nodeDeck the order of cards used to create this node
	* @param constant the Cp value used for UCT calculation
	* @param nodeCanDraw the cards still available to be drawn from this node's state
    */
   public xMCTSPruningChoiceNode(xMCTSStringGameState nodeGameState, Card[] nodeDeck, float constant, boolean[] nodeCanDraw)
   {
	   super(nodeGameState, nodeDeck, constant, nodeCanDraw);
   }

   /**
    * Expands the tree by adding a set of child nodes to this node.
    *
    * @param possibleMoves The list of move to be added as child nodes to this
    * node.
    */
   public synchronized void choiceExpand(ArrayList<xMCTSStringGameState> possibleMoves)
   {
	   
      if (!expanded)
      {
         expanded = true;
         nextMoves = new ArrayList<xMCTSPruningNode>();
         for (xMCTSStringGameState s : possibleMoves) {
            Card[] deckCopy = java.util.Arrays.copyOf(nodeDeck, nodeDeck.length);
            boolean[] canDrawCopy = java.util.Arrays.copyOf(nodeCanDraw, nodeCanDraw.length);
            nextMoves.add(new xMCTSPruningChanceNode(s, deckCopy, constant, canDrawCopy));
         }
      }
   }

   /**
    * Uses an Upper Confidence Bounds formula to select the best node for the
    * "selection" phase of a single MCTS game simulation. The UCB formula is
    * used to balance the value of exploring relatively unexplored nodes against
    * the value of exploring nodes that are highly ranked thus far. Function
    * assumes alternating turns between two opposing players in a \ zero-sum
    * game.
    *
    * @param myTurn Whether or not it is the turn of the MCTSPlayer that
    * contains this node.
    * @return The best selection from this node.
    */
   public xMCTSPruningNode bestSelection()
   {
      //the randomizer is a tiny random number added for tie-breaking
	  
      float bias, randomizer;
      float max = Float.NEGATIVE_INFINITY;
      int maxIndex = 0;
      float C = constant;
      
      for (int i = 0; i < nextMoves.size(); i++) {
         xMCTSPruningNode curChild = nextMoves.get(i);
         float nodeScore = (float) curChild.getScore() / ((float) (curChild.getTimesVisited() + Float.MIN_VALUE));
         bias = 2 * C * (float) (Math.sqrt(Math.log((float) this.getTimesVisited()) / ((float) curChild.getTimesVisited() + Float.MIN_VALUE)));
         randomizer = Float.MIN_VALUE * r.nextInt(nextMoves.size() * nextMoves.size());
         float biasedScore = nodeScore + randomizer + (bias);
         
         if (biasedScore > max) {
            max = biasedScore;
            maxIndex = i;
         }
      }
      
      if (!nextMoves.get(maxIndex).choiceNode()) //if the child is a chance node...
  	    return nextMoves.get(maxIndex);
	  else {
		  throw new NullPointerException("choice bestSelect has a choice child");
	  }
   }
   

   /**
    * Chooses the best available move (node) following this node.
    *
    * @return the best available move (node) following this node.
    */
   public xMCTSPruningNode bestMove()
   {
      float max = Float.NEGATIVE_INFINITY;
      int maxIndex = r.nextInt(nextMoves.size());
      float randomizer;
      for (int i = 0; i < nextMoves.size(); i++) {
         xMCTSPruningNode node = nextMoves.get(i);
         float nodeScore = (float) node.getScore() / ((float) (node.getTimesVisited() + Float.MIN_VALUE));
         randomizer = Float.MIN_VALUE * r.nextInt(nextMoves.size() * nextMoves.size());
         if (nodeScore + randomizer > max) {
            max = nodeScore + randomizer;
            maxIndex = i;
         }
      }
      if (!nextMoves.get(maxIndex).choiceNode()) //if the child is a chance node...
    	    return nextMoves.get(maxIndex);
	  else {
		  throw new NullPointerException("choice bestMove has a choice child");
	  }
   }
   
   public void createChildNode(xMCTSStringGameState s, Card[] deck, boolean[] canDraw) { 
	   Card[] deckCopy = java.util.Arrays.copyOf(deck, deck.length);
       boolean[] canDrawCopy = java.util.Arrays.copyOf(canDraw, canDraw.length);
	   nextMoves.add(new xMCTSPruningChanceNode(s, deckCopy, constant, canDrawCopy));
   }
   
   /**
    * Whether or not this node is a Choice Node.
    *
    * @return true, as this is a Choice node
    */
     public boolean choiceNode() {
    	 return true;
     }
}
