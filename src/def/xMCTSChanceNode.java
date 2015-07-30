package def;

import xMCTSChoiceNode;
import xMCTSNode;

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

public class xMCTSChanceNode extends xMCTSNode
{

   /**
    * Instantiates an MCTSNode and initializes ranking variables.
    *
    * @param nodeGameState The GameState this node will keep score for.
    */
   public xMCTSChanceNode(xMCTSStringGameState nodeGameState, Card[] nodeDeck, int verbosity, float constant)
   {
	   super(nodeGameState, nodeDeck, verbosity, constant);
   }

   /**
    * Expands the tree by adding a set of child nodes to this node based on the possible draws left in the deck
    *
    * @param possibleMoves The list of moves available, used to determine number of plays
    */
   public synchronized void expand(ArrayList<xMCTSStringGameState> possibleMoves, String tab, boolean print)
   {
	  if (verbosity > 0)
		  System.out.println(tab + "Chance expanding");
	  
	  if (!expanded)
      {
		 String childStateStringRep;
   	     int numPlays = 25 - possibleMoves.size();
         expanded = true;
         nextMoves = new ArrayList<xMCTSNode>();
         Card[] currChildDeck;
         Card temp;
         if (verbosity > 4) {
        	 System.out.println(tab + "This node's deck: " + java.util.Arrays.toString(nodeDeck));
        	 System.out.print(tab + "possible draws: \t         ");
         }
         for (int i = numPlays; i < nodeDeck.length; i++) {
        	 currChildDeck = java.util.Arrays.copyOf(nodeDeck, nodeDeck.length);
        	 childStateStringRep = nodeGameState.toString().substring(0, nodeGameState.toString().length() - 2);
        	 childStateStringRep += currChildDeck[i].toString();
        	 if (verbosity > 4)
        		 System.out.print(currChildDeck[i].toString() + " ");
        	 
        	 // swap i into the "already played" section
        	 temp = currChildDeck[i];
        	 currChildDeck[i] = currChildDeck[numPlays];
        	 currChildDeck[numPlays] = temp;
        	 nextMoves.add(new xMCTSChoiceNode(new xMCTSStringGameState(childStateStringRep, 0.0, 0), currChildDeck, verbosity, constant)); //the non-pruning player does not need to track numPlays or expectedValues in the string state
         }
         if (verbosity > 4)
        	 System.out.println();
      }
   }

   /**
    * Randomly selects a child of this node
    *
    * @param myTurn Whether or not it is the turn of the MCTSPlayer that
    * contains this node.
    * @return A random child of this node
    */
   public xMCTSNode bestSelection(boolean myTurn, String tab)
   {
	  if (verbosity > 2) 
		  System.out.println(tab + "Chance bestSelection");
	  
	  xMCTSNode temp = getRandomChild();
	  if (verbosity > 2)
		  System.out.println(tab + "Chance selected node: " + temp);
	  
      return temp;
   }

   /**
    * Chooses the best available move (node) following this node.
    *
    * @return the best available move (node) following this node.
    */
   public xMCTSNode bestMove() //ChanceNode should never need to call this 
   {
      return getRandomChild();
   }
   
   /**
    * Whether or not this node is a Choice Node.
    *
    * @return false, as this is a Chance node
    */
   public boolean choiceNode() {
    	 return false;
   }
}
