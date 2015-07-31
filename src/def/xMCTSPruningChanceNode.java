package def;


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
 * A version of Node meant to emulate the uncertainty of drawing cards. Creates children
 * based on cards left in the deck and does not use UCT for selection.
 *
 * @author Robert Arrington
 * @author Steven Bogaerts
 * @author Clay Langley
 */

public class xMCTSPruningChanceNode extends xMCTSPruningNode
{

   /**
    * Instantiates an MCTSChanceNode and initializes ranking variables.
    *
    *  @param nodeGameState the state this node represents
	 * @param nodeDeck the order of cards used to create this node
	 * @param constant the Cp value used for UCT calculation
	 * @param nodeCanDraw the cards still available to be drawn from this node's state
    * 
    */
   public xMCTSPruningChanceNode(xMCTSStringGameState nodeGameState, Card[] nodeDeck, float constant, boolean[] nodeCanDraw)
   {
	   super(nodeGameState, nodeDeck, constant, nodeCanDraw);
   }

   /**
    * Expands the tree by adding a set of child nodes to this node based on the possible draws left in the deck.
    */
   public synchronized void chanceExpand()
   {
	  
	  if (!expanded)
      {
		 int numPlays = nodeGameState.numPlays;
		 String childStateStringRep;
         expanded = true;
         Card[] currChildDeck;
         boolean[] currChildCanDraw;
         Card temp;
         for (int i = numPlays; i < nodeDeck.length; i++) {
        	 currChildDeck = java.util.Arrays.copyOf(nodeDeck, nodeDeck.length);
        	 currChildCanDraw = java.util.Arrays.copyOf(nodeCanDraw, nodeCanDraw.length);
        	 childStateStringRep = nodeGameState.toString().substring(0, nodeGameState.toString().length() - 2);
        	 childStateStringRep += currChildDeck[i].toString();
        	 
        	 // swap i into the "already played" section
        	 temp = currChildDeck[i];
        	 currChildDeck[i] = currChildDeck[numPlays];
        	 currChildDeck[numPlays] = temp;
        	 
        	 //turn off i in canDraw for this child
        	 currChildCanDraw[(temp.getRank() + (temp.getSuit() * xCard.NUM_RANKS))] = false;
        	 
        	 nextMoves.add(new xMCTSPruningChoiceNode(new xMCTSStringGameState(childStateStringRep, nodeGameState.expectedValue, numPlays), currChildDeck, constant, currChildCanDraw));
         }
      }
   }

   /**
    * Randomly selects a child of this node
    *
    * @param myTurn Whether or not it is the turn of the MCTSPlayer that
    * contains this node.
    * @return A random child of this node
    */
   public xMCTSPruningNode bestSelection()
   {
	  
	  xMCTSPruningNode temp = getRandomChild();
	  
	  if (temp.choiceNode()) //if temp is a choice node...
		  return temp;
	  else {
		  throw new NullPointerException("chance bestSelect has a chance child");
	  }
   }
   
   /**
    * This method should never be called from a chance node, as you cannot decide which card you
    * will draw next. The method was kept as a means of testing to ensure that nothing ever
    * attempts to call it.
    *
    * @return the best available move (node) following this node.
    */
   public xMCTSPruningNode bestMove() //ChanceNode should never need to call this 
   {
	   System.out.println("This is being called from a chance node");
      return getRandomChild();
   }
   
   /**
    * Create an individual node based on the provided information. This is meant
    * to be used with hard coded moves only, as MCTS uses the expand method to
    * create multiple nodes at once instead.
    * 
    * @param s the state of the node to be created
    * @param deck the ordering of the cards for the node being created
    * @param canDraw the cards still possible to draw in the node being created
    */
   public void createChildNode(xMCTSStringGameState s, Card[] deck, boolean[] canDraw) { 
	   Card[] deckCopy = java.util.Arrays.copyOf(deck, deck.length);
       boolean[] canDrawCopy = java.util.Arrays.copyOf(canDraw, canDraw.length);
	   nextMoves.add(new xMCTSPruningChoiceNode(s, deckCopy, constant, canDrawCopy));
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
