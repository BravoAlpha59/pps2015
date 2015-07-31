package def;

import java.util.ArrayList;


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
 * An implementation of a Parameterized Poker Squares player
 * that focuses on using an MCTS algorithm for decision making in the game. It also
 * uses domain knowledge to evaluate possible nodes and prune undesireable moves.
 *
 * @author Robert Arrington
 * @author Steven Bogaerts
 * @author Clay Langley
 */
public abstract class xMCTSPPSPruningPlayer implements PokerSquaresPlayer
{

  protected xMCTSPruningPPSGame g; //Representation of the game itself
   public xMCTSStringGameState currentState; //the current state of the game board
   public xMCTSPruningNode curNode; //the node that represents the current game board
   private final int SIZE = 5; // number of rows/columns in a Poker Squares grid
   private final int NUM_POS = SIZE * SIZE; // number of positions in a Poker Squares grid
   protected final int NUM_CARDS = Card.NUM_CARDS; // number of cards in a standard deck
   public int numPlays = 0; // number of Cards played into the grid so far
   protected PokerSquaresPointSystem pointSystem; // point system
   protected Card[][] grid = new Card[SIZE][SIZE]; // grid with Card objects or null (for empty positions)
   protected Card[] gameDeck = Card.getAllCards(); // a list of all Cards. As we learn the index of cards in the play deck,
	                                             // we swap each dealt card to its correct index.  Thus, from index numPlays 
												 // onward, we maintain a list of undealt cards for MC simulation.
   protected boolean[] gameCanDraw; //an array of booleans meant to make it easier to check which cards can still be drawn or not. 
   //Using this array allows you to not have to iterate through the deck whenever determining whether or not a specific card is still available
   
   //Cps for various scoring systems
   protected float C; //constant currently being used in the UCT formula
   protected float AMERICANC; //Cp for the American system
   protected float AMERITISHC; //Cp for the Ameritish system
   protected float BRITISHC; //Cp for the British system
   protected float HIGHCARDC; //Cp for High Card Single Hand system
   protected float ONEPAIRC; //Cp for One Pair Single Hand system
   protected float TWOPAIRC; //Cp for Two Pair Single Hand system
   protected float THREEKINDC; //Cp for Three Kind Single Hand system
   protected float STRAIGHTC; //Cp for Straight Single Hand system
   protected float FLUSHC; //Cp for Flush Single Hand system
   protected float FULLHOUSEC; //Cp for Full House Single Hand system
   protected float FOURKINDC; //Cp for Four Kind Single Hand system
   protected float STRAIGHTFLUSHC; //Cp for Straight Flush Single Hand system
   protected float ROYALC; //Cp for Royal Flush Single Hand system
   protected float HYPERCORNERC; //Cp for Hyper Corner systems 
   protected float OTHERC; //Cp for randomized systems
   
   
   //tracking variables for debugging
//   protected long totalTrials = 0; 
//   protected long totalPruning = 0;
//   protected long totalSingleGameTrials;
//   protected float averageTimeRemaining = 0;
//   protected long numGamesPlayed = 0;
//   Card getPlayCard = Card.getCard(null);
//   int getPlayTrials;
   
   //Created outside of getPlay for further debugging
   int remainingPlays = 0;
   long millisPerPlay = 0; 
   long startTime = 0;
   long endTime = 0;
   long millisRemainingFromGetPlayStart = 0;
   

   /**
    * Instantiates the player. Nothing happens here because all game creation and setup is done in either init or setPointSystem
    *
    */
   public xMCTSPPSPruningPlayer()
   {
   }

   /**
    * mutates curNode and currentState into the child of curNode that matches String S
    * 
    * @param s the state representing the child that curNode should be updated to.
    */
   public void updateGameState(xMCTSStringGameState s)
   {
      try {
    	  curNode = curNode.findChildNode(s);
      }
      catch (Exception e) {//When there is a problem with node creation, this is typically the first place that problem comes up
    	  System.out.println("GameCanDraw    = " + java.util.Arrays.toString(gameCanDraw));
    	  System.out.println("curNodeCanDraw = " + java.util.Arrays.toString(curNode.nodeCanDraw));
    	  System.out.println("GameDeck    = " + java.util.Arrays.toString(gameDeck));
    	  System.out.println("curNodeDeck = " + java.util.Arrays.toString(curNode.nodeDeck));
    	  System.out.println("curNode = " + curNode.getState().toString());
    	  System.out.println("curNode's children:");
    	  for (int i = 0; i < curNode.nextMoves.size(); i++) {
    		  System.out.println(curNode.nextMoves.get(i));
    	  }
    	  System.out.println("\nupdateGameState is looking for node: " + s.toString());
    	  throw new NullPointerException("findChildNode tried to return null");
      }
      
      currentState = s;
   }

   /**
    * Simulates possible games until allowed think time runs out, and then makes
    * a move by returning the position of that move on the board
    * 
    * @param card the Card object "drawn" by the PokerSquares code.
    * @param millisRemaining the time left to play the game
    * @return an array of two integers, representing the row and the column of the game grid location of the chosen move
    */
   public int[] getPlay(Card card, long millisRemaining)
   {
	 //tracking variables
	 //getPlayTrials = 0;
	 //getPlayCard = card;
	   
	// match gameDeck to actual play event; in this way, all indices forward from the card contain a list of unplayed cards
	  	int cardIndex = numPlays;
		 while (!card.equals(gameDeck[cardIndex])) {
			 cardIndex++;
		 }
		 gameDeck[cardIndex] = gameDeck[numPlays];
		 gameDeck[numPlays] = card; 
		 
	//turn off the index of the drawn card in canDraw
		 gameCanDraw[(card.getRank() + (card.getSuit() * xCard.NUM_RANKS))] = false;
	
	 //Create a string representation of the game board, with the newly drawn card at the end	 
	 String activeState = currentState.toString().substring(0, currentState.toString().length() - 2);
	 activeState += card.toString();
	 
	 //Hard coding for first move of the game
	 if (numPlays == 0) {
		 
		 //Create a state object corresponding to the drawn card
		 xMCTSStringGameState startingGameState = new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays); 
		 //Because there have been no calls to expand yet, a node representing the gameState must be manually created
  		 curNode.createChildNode(startingGameState, gameDeck, gameCanDraw);
  		//Update to the created state, curNode is now choice
  		 updateGameState(startingGameState); 
  		 
  		//Play the drawn card at the first position in the board, creating a string, state, and node to represent that change
  		activeState = (card.toString() + activeState.substring(2, activeState.length())); 
  		xMCTSStringGameState firstMoveGameState = new xMCTSStringGameState(activeState, g.getExpectedBoardScore(activeState, numPlays + 1, gameCanDraw), numPlays + 1);
  		curNode.createChildNode(firstMoveGameState, gameDeck, gameCanDraw);
  		//Update, curNode is once again chance
  		updateGameState(firstMoveGameState); 
	 } 	 
	 
	 //Hard coding for second move of the game
	 else if (numPlays == 1) {
		
		 //As before, create a state and node representation of the drawn card.
		 xMCTSStringGameState startingGameState = new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays); 
  		 curNode.createChildNode(startingGameState, gameDeck, gameCanDraw);
  		 //Update, curNode is now choice
  		 updateGameState(startingGameState); 

		 //Create a state for the first possible relevant move (next to first card)
		 String secondMoveState = (activeState.substring(0, 2) + card.toString() + activeState.substring(4, activeState.length()));
		 xMCTSStringGameState playNextToFirst = new xMCTSStringGameState(secondMoveState, g.getExpectedBoardScore(secondMoveState, numPlays + 1, gameCanDraw), numPlays + 1); 

		 //Create a state for the second possible relevant move (away from first card)
		 secondMoveState = (activeState.substring(0, 12) + card.toString() + activeState.substring(14, activeState.length()));
		 xMCTSStringGameState playByItself = new xMCTSStringGameState(secondMoveState, g.getExpectedBoardScore(secondMoveState, numPlays + 1, gameCanDraw), numPlays + 1);

		 //Only create the child representation of the state with the best expected value
		 if (playNextToFirst.expectedValue > playByItself.expectedValue) {
			 curNode.createChildNode(playNextToFirst, gameDeck, gameCanDraw);
			 updateGameState(playNextToFirst); //update, curNode is once again chance
		 }
		 else {
			 curNode.createChildNode(playByItself, gameDeck, gameCanDraw);
			 updateGameState(playByItself); //update, curNode is once again chance
		 }
		 //Now entering MCTS, so expand the created chance node
		 ((xMCTSPruningChanceNode) curNode).chanceExpand();
	 }
	 
	 //divide up the remaining time among the remaining moves, then run MCTS as many times as possible
  	 else if (numPlays < 23) {
  		 remainingPlays = (NUM_POS - numPlays);
  	     millisPerPlay = (millisRemaining) / (remainingPlays - 1); // dividing time evenly with future getPlay() calls. 
  	     //Because the final two moves of the game do not run MCTS, one move's worth of time is added back for the MCTS moves to utilize,
  	     //while the other is kept out of the equation as a buffer to avoid timeouts.
  	     startTime = System.currentTimeMillis();
  	     endTime = startTime + millisPerPlay;
  		 millisRemainingFromGetPlayStart = millisRemaining;
  		 
  		 //Change from chance node to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 
  		 while (System.currentTimeMillis() < endTime)
  		 {
  			 //keep running trials until the given time is used up
  			 runTrial(curNode);
  			 //getPlayTrials++;
  		 }

  		 //After running as many trials as possible, determine the move with the highest Q/N value. Then, update the
  		 //curNode and currentState pointers to reflect making that move
  		 if (g.gameStatus(currentState) == xMCTSGame.status.ONGOING) {
  			 xMCTSPruningNode best = curNode.bestMove();
  			 currentState = best.getState();
  			 curNode = best;
  		 }
  	 }

	 //Hard coding for the penultimate move of the game
  	 else if (numPlays == 23) {
  		 //Change from chance node to choice node. Because MCTS has already been run, this node has already been expanded
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		//only one move in nextMoves, the other was pruned, so just return the single move.
  		 if (curNode.nextMoves.size() == 1) {
  	  		 xMCTSPruningNode best = curNode.bestMove();
  	  		 currentState = best.getState();
  	  		 curNode = best;
  		 }
  		 else {//neither move was pruned, so a move must be chosen

  		 //Create a state for the first possible move (first blank space found)
  		 xMCTSStringGameState playInFirstOpen = curNode.nextMoves.get(0).getState(); 

  		 //Create a state for the second possible move (second blank space found)
  		 xMCTSStringGameState playInSecondOpen = curNode.nextMoves.get(1).getState();

  		 //Update to the child with the best estimated value
  		 if (playInFirstOpen.expectedValue > playInSecondOpen.expectedValue) {
  			 updateGameState(playInFirstOpen); //Update, curNode is once again chance
  		 }
  		 else {
  			 updateGameState(playInSecondOpen); //Update, curNode is once again chance
  		 }
  		 }
  	 }

	 //Hard coding for the final move of the game
  	 else if (numPlays == 24) {
  		 //Chance to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 ArrayList<xMCTSStringGameState> possibleMoves = g.getPossibleMoves(curNode.getState(), curNode.nodeCanDraw);
  		 ((xMCTSPruningChoiceNode) curNode).choiceExpand(possibleMoves);
  		 //There should only be one possible move remaining. Determine that move and update to its state
  		 xMCTSPruningNode best = curNode.bestMove();
  		 currentState = best.getState();
  		 curNode = best;
  	 }

	 //update numPlays to reflect that a move has been chosen.
	 numPlays++;
	 //return an array holding the row and column of the move that resulted in this new state
	 int[] temp = curNode.bestMoveLocation(card);
      
	 //all the tracking information for debugging, only displayed on the final move of each game. Commented out so as to not interfere with
	 //the tournament, but left in as an indicator of what we found useful to track per game
//      totalSingleGameTrials += getPlayTrials;
//      if (numPlays == 25) {
//    	  System.out.println(); //blank line
//    	  float timeRemaining = (millisRemaining);
//    	  System.out.println("Time remaining this game (before print statements) = " + timeRemaining);
//    	  numGamesPlayed++;
//    	  System.out.println("Total games played = " + numGamesPlayed);
//    	  averageTimeRemaining = ((averageTimeRemaining * (numGamesPlayed - 1)) + timeRemaining)/numGamesPlayed;
//    	  System.out.println("Average time remaining = " + averageTimeRemaining);
//    	  System.out.println("Total trials this game: " + totalSingleGameTrials);
//      	  totalTrials += totalSingleGameTrials;
//      	  totalPruning += g.totalChildrenPruned;
//      	  System.out.println("Total trials overall  : " + totalTrials);
//      	  System.out.println("Total children pruned this game : " + g.totalChildrenPruned);
//      	  System.out.println("Total children pruned overall   : " + totalPruning);
//      	  g.totalPercentPruned /= g.timesCalled;
//      	  System.out.println("Average percent pruned          : " + (1.0 - g.totalPercentPruned));
//      	  System.out.println("Total moves looked at throughout the game = " + g.overAllMoves);
//      	  
//      }
      
	  return temp;		
   }

   /**
    * Plays a single simulated game, and encompasses the four stages of an MCTS
    * simulation (selection, expansion, simulation, and backpropogation).
    * Selection: Pick a node to simulate from by recursively applying UCB.
    * Expansion: Add a new set of nodes to the link tree as children of the
    * selected node. 
    * Simulation: Pick one of those nodes and simulate a game from it. 
    * Backpropogation: Rank all nodes selected during the selection
    * step based on simulation outcome.
    *
    * @param node The node to begin running the trial from.
    * @return The score calculated by the simulation step of this trial.
    */
   private float runTrial(xMCTSPruningNode node)	{
	   float returnScore;
	   node.visit();
	   //if runTrial is called on a node representing an unfinished game state
	   if (g.gameStatus(node.getState()) == xMCTSGame.status.ONGOING) {
		   if (!node.isLeaf()) {
			   //selection. proceed further down the tree. The path chosen is determined by the UCT equation

			   xMCTSPruningNode nextChild = node.bestSelection();
			   returnScore = runTrial(nextChild);
		   } else {

			   if (node.choiceNode()) {
				   //expansion. Create a child node for each possible move location remaining on the board.
				   ArrayList<xMCTSStringGameState> possibleMoves = g.getPossibleMoves(node.getState(), node.nodeCanDraw);
				  
				   ((xMCTSPruningChoiceNode) node).choiceExpand(possibleMoves); //to be in this if statement it must be a choice node

				   //simulation. Play out a game to the end, then use that end score as the Q value for this trial. Because this is a choice node,
				   //you have already randomly drawn a card, and therefore want randomize to begin as false.
				   returnScore = simulateFrom(node.getState(), false, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), java.util.Arrays.copyOf(node.nodeCanDraw, node.nodeCanDraw.length)); // copy(node.getDeck)
			   }
			   else {
				   //expansion. Create a child node for each card still possible to draw
				   ((xMCTSPruningChanceNode) node).chanceExpand();//to be in this else it must be a chance node

				   //simulation. Play out a game to the end, then use that end score as the Q value for this trial. Because this is a chance node,
				   //you need to begin simulation by randomly drawing a card, so randomize begins as true.
				   returnScore = simulateFrom(node.getState(), true, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), java.util.Arrays.copyOf(node.nodeCanDraw, node.nodeCanDraw.length)); // copy(node.getDeck)
			   }

		   }
		   //backpropogation. Return the simulation score and update all nodes in this recursive sequence.
		   node.setScore(node.getScore() + returnScore);
	   }
	   else {//if runTrial is called on a node representing a completed game
		   for (int i = 0; i < 5; i++) {//row
			   for (int j = 0; j < 5; j++) {//column
				   int position = i * 10 + j * 2;
				   String temp = node.getState().toString().substring(position, position + 2);
				   grid[i][j] = Card.getCard(temp);
			   }
		   }
		   returnScore = pointSystem.getScore(grid); //no need to attempt to select, expand, or simulate, just return the completed board's score.
		   
	   }
	   return returnScore;
   }

   /**
    * Performs a simulation or "rollout" for the "simulation" phase of the
    * runTrial function. This can be written to contain game-specific heuristics
    * or "finishing move" detection if desired.
    *
    * @param state the state to be simulated from.
    * @param randomize whether or not to begin simulating by randomly drawing a card
    * @param simDeck the deck to be used for simulating, determines draw orders without affecting the player or node decks
    * @param simCanDraw the canDraw boolean array to be used for simulating, determines which cards are still available to be drawn
    * @return the resulting value of the simulated end game state.
    */
   protected abstract float simulateFrom(xMCTSStringGameState state, boolean randomize, Card[] simDeck,  boolean[] simCanDraw);
   
   /**
    * Complete game setup steps that only need to be done once per point system.
    * 
    * @param pointSystem an array of scores for each of the possible hands in poker.
    * @param millis the time allotted to this method
    */
   public abstract void setPointSystem(PokerSquaresPointSystem pointSystem, long millis);
	
	/**
	 * initializes the player before each game
	 */
	public void init() {
		// clear grid
		for (int row = 0; row < SIZE; row++)
			for (int col = 0; col < SIZE; col++)
				grid[row][col] = null;
		// reset numPlays
		numPlays = 0;
		//reset the deck
	    gameCanDraw = new boolean[] {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}; 
	    
	    //reset printed values. Not removed so it can serve as an example of what we tracked.
	    //g.resetTrackingValues();
	    //totalSingleGameTrials = 0;
	    
	    //reset currentState to a blank board
	    currentState = g.getStartingState();
	    //reset curNode to a node representing the blank board
	    curNode = new xMCTSPruningChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), C, java.util.Arrays.copyOf(gameCanDraw, gameCanDraw.length));
	}
	
	
	/**
	 * getName - gets the uniquely identifying name of this Poker Squares player.
	 * @return unique player name
	 */
	public abstract String getName();
}
