package def;

import wMCTSChanceNode;
import wMCTSNode;

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
 * MCTSNode contains tracks scoring information for each gamestate in a link
 * tree for a Monte Carlo Tree Search
 *
 * @author Kyle
 */
import java.util.ArrayList;

public class wMCTSChoiceNode extends wMCTSNode
{

   /**
    * Instantiates an MCTSNode and initializes ranking variables.
    *
    * @param nodeGameState The GameState this node will keep score for.
    */
   public wMCTSChoiceNode(xMCTSStringGameState nodeGameState, Card[] nodeDeck, int verbosity, float constant)
   {
	   super(nodeGameState, nodeDeck, verbosity, constant);
   }

   /**
    * Expands the tree by adding a set of child nodes to this node.
    *
    * @param possibleMoves The list of move to be added as child nodes to this
    * node.
    */
   public synchronized void expand(ArrayList<xMCTSStringGameState> possibleMoves, String tab, boolean print)
   {
	   if (verbosity > 0)
		   System.out.println(tab + "Choice expanding");
	   
      if (!expanded)
      {
         expanded = true;
         //nextMoves = new ArrayList<wMCTSChanceNode>();
         Card[] deckCopy = java.util.Arrays.copyOf(nodeDeck, nodeDeck.length);
         nextMoves = new ArrayList<wMCTSNode>();
         for (xMCTSStringGameState s : possibleMoves) {
            nextMoves.add(new wMCTSChanceNode(s, deckCopy, verbosity, constant));
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
   public wMCTSNode bestSelection(boolean myTurn, String tab)
   {
      //the randomizer is a tiny random number added for tie-breaking
	  if (verbosity > 2)
		  System.out.println(tab + "Choice bestSelection");
	  
      float bias;
      float max = -Float.MAX_VALUE;
      float C = constant;
      if (verbosity > 2)
    	  System.out.println(tab + "Current's times visited: " + this.getTimesVisited() +  " Current's score: " + this.getScore());
      
      ArrayList<wMCTSNode> bestPlays = new ArrayList<wMCTSNode>();
      for (int i = 0; i < nextMoves.size(); i++) {
         wMCTSNode curChild = nextMoves.get(i);
         if (verbosity > 2)
        	 System.out.println(tab + "This child is node " + curChild.toString() + 
       		  "\n" + tab + "This child's times visited: " + curChild.getTimesVisited() + " This child's score: " + curChild.getScore());
         
         float nodeScore = (float) curChild.getScore() / ((float) (curChild.getTimesVisited() + Float.MIN_VALUE));
         bias = 2 * C * (float) (Math.sqrt(Math.log((float) this.getTimesVisited()) / ((float) curChild.getTimesVisited() + Float.MIN_VALUE)));
         float biasedScore = nodeScore + (bias);
         if (verbosity > 2)
        	 System.out.println(tab + "This child has a UCT value of " + biasedScore + "\n");
         if (biasedScore >= max) {
        	 if (biasedScore > max) {
        		 bestPlays.clear();
        		 max = biasedScore;
        	 }
        	 bestPlays.add(nextMoves.get(i));
         }
      }
      wMCTSNode chosenPlay = bestPlays.get(r.nextInt(bestPlays.size()));
      if (verbosity > 2) {
    	System.out.println(tab + "Choice selected node: " + chosenPlay);
      	System.out.println(tab + "Node " + chosenPlay.toString() + " was selected from bestSelection");
      }
      return chosenPlay;
   }
   

   /**
    * Chooses the best available move (node) following this node.
    *
    * @return the best available move (node) following this node.
    */
   public wMCTSNode bestMove()
   {
      float max = -Float.MAX_VALUE;
      int maxIndex = r.nextInt(nextMoves.size());
      float randomizer;
      for (int i = 0; i < nextMoves.size(); i++) {
         wMCTSNode node = nextMoves.get(i);
         float nodeScore = (float) node.getScore() / ((float) (node.getTimesVisited() + Float.MIN_VALUE));
         randomizer = Float.MIN_VALUE * r.nextInt(nextMoves.size() * nextMoves.size());
         if (nodeScore + randomizer > max) {
            max = nodeScore + randomizer;
            maxIndex = i;
         }
      }
      return nextMoves.get(maxIndex);
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
