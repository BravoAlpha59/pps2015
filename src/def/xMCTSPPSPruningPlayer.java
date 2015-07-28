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
 * MCTSPPSPruningPlayer is an implementation of a Parameterized Poker Squares player
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
   protected float C; //constant used in the UCT formula
   protected long totalTrials = 0;
   protected long totalPruning = 0;
   protected long totalSingleGameTrials;
   protected float averageTimeRemaining = 0;
   protected long numGamesPlayed = 0;
   
   protected boolean[] gameCanDraw;
   
   //Created outside of getPlay for debugging
   int remainingPlays = 0;
   long millisPerPlay = 0; 
   long startTime = 0;
   long endTime = 0;
   Card getPlayCard = Card.getCard(null);
   long millisRemainingFromGetPlayStart = 0;
   int getPlayTrials;
   
   /**
    * Gets the current state of the game.
    *
    * @return the current state of the game.
    */
   public xMCTSStringGameState getCurrentState()
   {
      return currentState;
   }

   /**
    * Instantiates the player.
    *
    * @param g The Game being played.
    * @param player1 Whether or not this player is player 1.
    * @param thinkTime How many milliseconds this player is allowed to think per
    * turn (Longer think time yields better simulations.
    */
   public xMCTSPPSPruningPlayer()
   {
   }

   public void updateGameState(xMCTSStringGameState s)
   {
      try {
    	  curNode = curNode.findChildNode(s);
      }
      catch (Exception e) {
    	  System.out.println("GameCanDraw = " + java.util.Arrays.toString(gameCanDraw));
    	  System.out.println("curNodeCanDraw = " + java.util.Arrays.toString(curNode.nodeCanDraw));
    	  System.out.println("GameDeck = " + java.util.Arrays.toString(gameDeck));
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
    * a move.
    */
   public int[] getPlay(Card card, long millisRemaining)
   {
	   // Update simDeck so that the given card is moved to the end of the already played section. (numPlays... careful for off-by-1)
	   // The root node of the tree that we give to MCTS is a ChoiceNode with the Card passed in at the end of that state.
	   //     That ChoiceNode has a cardDeck which is java.util.copyOf(simDeck, simDeck.length) 
	   // Then we do MCTS with that ChoiceNode at the root.
	   // Return the desired move.
	   
	 getPlayTrials = 0;
	 getPlayCard = card;

	 
	// match gameDeck to actual play event; in this way, all indices forward from the card contain a list of unplayed cards
	  	int cardIndex = numPlays;
		 while (!card.equals(gameDeck[cardIndex])) {
			 cardIndex++;
		 }
		 gameDeck[cardIndex] = gameDeck[numPlays];
		 gameDeck[numPlays] = card; 
		 
	//match gameCanDraw to the drawn card
		 gameCanDraw[(card.getRank() + (card.getSuit() * xCard.NUM_RANKS))] = false;
		 
	 String activeState = currentState.toString().substring(0, currentState.toString().length() - 2);
	 activeState += card.toString();
	 
	 if (numPlays == 0) {
		 xMCTSStringGameState startingGameState = new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays); //Draw first card, curNode starts as chance
  		 curNode.createChildNode(startingGameState, gameDeck, gameCanDraw);
  		 updateGameState(startingGameState); //update, curNode is now choice
  		 
  		activeState = (card.toString() + activeState.substring(2, activeState.length())); //play first card at first position, curNode is a choice node
  		xMCTSStringGameState firstMoveGameState = new xMCTSStringGameState(activeState, g.getExpectedBoardScore(activeState, numPlays + 1, gameCanDraw, 2), numPlays + 1);
  		curNode.createChildNode(firstMoveGameState, gameDeck, gameCanDraw);
  		updateGameState(firstMoveGameState); //update, curNode is once again chance
	 } 	 
	 
	 else if (numPlays == 1) {
		 //Change from chance node to choice node
		 xMCTSStringGameState startingGameState = new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays); //Draw first card, curNode starts as chance
  		 curNode.createChildNode(startingGameState, gameDeck, gameCanDraw);
  		 updateGameState(startingGameState); //update, curNode is now choice

		 //Create child for first possible relevant move (next to first card)
		 String secondMoveState = (activeState.substring(0, 2) + card.toString() + activeState.substring(4, activeState.length()));
		 xMCTSStringGameState playNextToFirst = new xMCTSStringGameState(secondMoveState, g.getExpectedBoardScore(secondMoveState, numPlays + 1, gameCanDraw, 2), numPlays + 1); 

		 //Create child for second possible relevant move (away from first card)
		 secondMoveState = (activeState.substring(0, 12) + card.toString() + activeState.substring(14, activeState.length()));
		 xMCTSStringGameState playByItself = new xMCTSStringGameState(secondMoveState, g.getExpectedBoardScore(secondMoveState, numPlays + 1, gameCanDraw, 5), numPlays + 1);

		 //Only create the child with the best expected value
		 if (playNextToFirst.expectedValue > playByItself.expectedValue) {
			 curNode.createChildNode(playNextToFirst, gameDeck, gameCanDraw);
			 updateGameState(playNextToFirst); //update, curNode is once again chance
		 }
		 else {
			 curNode.createChildNode(playByItself, gameDeck, gameCanDraw);
			 updateGameState(playByItself); //update, curNode is once again chance
		 }
		 ((xMCTSPruningChanceNode) curNode).chanceExpand();
	 }
	 
  	 else if (numPlays < 23) {
  		 remainingPlays = (NUM_POS - numPlays); // ignores triviality of last few plays to keep a conservative margin for game completion
  	     millisPerPlay = (millisRemaining) / (remainingPlays - 1); // dividing time evenly with future getPlay() calls
  	     startTime = System.currentTimeMillis();
  	     endTime = startTime + millisPerPlay;
  		 millisRemainingFromGetPlayStart = millisRemaining;
  		 
  		 //Change from chance node to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 
  		 while (System.currentTimeMillis() < endTime)
  		 {
  			 runTrial(curNode);
  			 getPlayTrials++;
  		 }

  		 if (g.gameStatus(currentState) == xMCTSGame.status.ONGOING) {
  			 xMCTSPruningNode best = curNode.bestMove();
  			 currentState = best.getState();
  			 curNode = best;
  		 }
  	 }

  	 else if (numPlays == 23) {
  		 //Change from chance node to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 if (curNode.nextMoves.size() == 1) {//only one move in nextMoves, just return that one
  	  		 xMCTSPruningNode best = curNode.bestMove();
  	  		 currentState = best.getState();
  	  		 curNode = best;
  		 }
  		 else {//neither move was pruned
  		 String[] possibleMoves = new String[2];
  		 int index = 0;

  		 int gridSize = SIZE*2; //Size is the size of the actual game grid... with 2 characters per card, size*2 is the grid size of string representations
  		 for (int row = 0; row < gridSize; row += 2) {//for each position in this state being checked
  			 for (int col = 0; col < gridSize; col += 2) {
  				 int pos = row * SIZE + col;
  				 if (currentState.toString().charAt(pos) == '_') {
  					 possibleMoves[index] = currentState.toString().substring(0, pos)
  							 				+ card.toString()
  							 				+ currentState.toString().substring(pos + 2, currentState.toString().length());
  					 index++;
  					 if (index == 2)
  					 	 break;
  				 }
  			 }
  		 }

  		 //Create child for first possible move (first blank space found)
  		 xMCTSStringGameState playInFirstOpen = new xMCTSStringGameState(possibleMoves[0], g.getExpectedBoardScore(possibleMoves[0], numPlays + 1, gameCanDraw, 5), numPlays + 1); 

  		 //Create child for second possible move (second blank space found)
  		 xMCTSStringGameState playInSecondOpen = new xMCTSStringGameState(possibleMoves[1], g.getExpectedBoardScore(possibleMoves[1], numPlays + 1, gameCanDraw, 5), numPlays + 1);

  		 //Only create the child with the best expected value
  		 if (playInFirstOpen.expectedValue > playInSecondOpen.expectedValue) {
  			 updateGameState(playInFirstOpen); //update, curNode is once again chance
  		 }
  		 else {
  			 updateGameState(playInSecondOpen); //update, curNode is once again chance
  		 }
  		 }
  	 }

  	 else if (numPlays == 24) {
  		 //Chance to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 ArrayList<xMCTSStringGameState> possibleMoves = g.getPossibleMoves(curNode.getState(), curNode.nodeCanDraw);
  		 ((xMCTSPruningChoiceNode) curNode).choiceExpand(possibleMoves);
  		 xMCTSPruningNode best = curNode.bestMove();
  		 currentState = best.getState();
  		 curNode = best;
  	 }

	 numPlays++;
	 //return an array holding the row and column of the move that results in the bestMove node
	 int[] temp = curNode.bestMoveLocation(card);
      
      totalSingleGameTrials += getPlayTrials;
      if (numPlays == 25) {
    	  System.out.println(); //blank line
    	  float timeRemaining = (millisRemaining);
    	  System.out.println("Time remaining this game (before print statements) = " + timeRemaining);
    	  numGamesPlayed++;
    	  System.out.println("Total games played = " + numGamesPlayed);
    	  averageTimeRemaining = ((averageTimeRemaining * (numGamesPlayed - 1)) + timeRemaining)/numGamesPlayed;
    	  System.out.println("Average time remaining = " + averageTimeRemaining);
    	  System.out.println("Total trials this game: " + totalSingleGameTrials);
      	  totalTrials += totalSingleGameTrials;
      	  totalPruning += g.totalChildrenPruned;
      	  System.out.println("Total trials overall  : " + totalTrials);
      	  System.out.println("Total children pruned this game : " + g.totalChildrenPruned);
      	  System.out.println("Total children pruned overall   : " + totalPruning);
      	  //System.out.println("average pruned before division = " + g.totalPercentPruned);
      	  //System.out.println("total times pruning was considered = " + g.timesCalled);
      	  g.totalPercentPruned /= g.timesCalled;
      	  System.out.println("Average percent pruned          : " + (1.0 - g.totalPercentPruned));
      	  System.out.println("Total moves looked at throughout the game = " + g.overAllMoves);
      	  
      }
      
	  return temp;		
   }

   /**
    * Plays a single simulated game, and encompasses the four stages of an MCTS
    * simulation (selection, expansion, simulation, and backpropogation).
    * Selection: Pick a node to simulate from by recursively applying UCB.
    * Expansion: Add a new set of nodes to the link tree as children of the
    * selected node. Simulation: Pick one of those nodes and simulate a game
    * from it. Backpropogation: Rank all nodes selected during the selection
    * step based on simulation outcome.
    *
    * @param node The node to begin running the trial from.
    * @param myTurn Whether it is this players turn or not.
    * @return The status of the trial.
    */
   private float runTrial(xMCTSPruningNode node)	{
	   float returnScore;
	   node.visit();
	   if (g.gameStatus(node.getState()) == xMCTSGame.status.ONGOING) {
		   if (!node.isLeaf()) {
			   //selection

			   xMCTSPruningNode nextChild = node.bestSelection();
			   returnScore = runTrial(nextChild);
		   } else {

			   if (node.choiceNode()) {
				   //expansion
				   ArrayList<xMCTSStringGameState> possibleMoves = g.getPossibleMoves(node.getState(), node.nodeCanDraw);
				  
				   ((xMCTSPruningChoiceNode) node).choiceExpand(possibleMoves); //to be in this if statement it must be a choice node

				   //simulation
				   returnScore = simulateFrom(node.getState(), false, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), java.util.Arrays.copyOf(node.nodeCanDraw, node.nodeCanDraw.length)); // copy(node.getDeck)
			   }
			   else {
				   //expansion
				   ((xMCTSPruningChanceNode) node).chanceExpand();//to be in this else it must be a chance node

				   //simulation
				   returnScore = simulateFrom(node.getState(), true, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), java.util.Arrays.copyOf(node.nodeCanDraw, node.nodeCanDraw.length)); // copy(node.getDeck)
			   }

		   }
		   //backpropogation
		   node.setScore(node.getScore() + returnScore);
	   }
	   else {
		   for (int i = 0; i < 5; i++) {//row
			   for (int j = 0; j < 5; j++) {//column
				   int position = i * 10 + j * 2;
				   String temp = node.getState().toString().substring(position, position + 2);
				   grid[i][j] = Card.getCard(temp);
			   }
		   }
		   returnScore = pointSystem.getScore(grid);
		   
	   }
	   return returnScore;
   }

   /**
    * Performs a simulation or "rollout" for the "simulation" phase of the
    * runTrial function. This can be written to contain game-specific heuristics
    * or "finishing move" detection if desired.
    *
    * @param state the state to be simulated from.
    * @return the resulting status of the simulation.
    */
   protected abstract float simulateFrom(xMCTSStringGameState state, boolean randomize, Card[] simDeck,  boolean[] simCanDraw);
   
   //Code added from PokerSquaresPlayer
   public abstract void setPointSystem(PokerSquaresPointSystem pointSystem, long millis);
	
	/**
	 * init - initializes the player before each game
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
	    
	    //reset printed values. Can be removed when done with testing entirely.
	    g.resetTrackingValues();
	    currentState = g.getStartingState();
	    curNode = new xMCTSPruningChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), C, java.util.Arrays.copyOf(gameCanDraw, gameCanDraw.length));
	    totalSingleGameTrials = 0;
	}
	
	
	/**
	 * getName - gets the uniquely identifying name of the Poker Squares player.  The name should be 20 characters or less.
	 * @return unique player name
	 */
	public abstract String getName();
}
