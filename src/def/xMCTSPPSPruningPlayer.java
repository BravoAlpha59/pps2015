package def;
import java.util.ArrayList;
import java.util.Random;


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
 * MCTSPlayer is an implementation of Player that makes moves using UCT (A Monte
 * Carlo Tree Search that uses an Upper Confidence Bounds formula).
 *
 * @author Kyle
 */
public abstract class xMCTSPPSPruningPlayer implements PokerSquaresPlayer
{

  protected xMCTSPruningPPSGame g;
  protected boolean player1;
   public xMCTSStringGameState currentState;
   public xMCTSPruningNode curNode;
   
   //fields added from GreedyMCPlayer
   private final int SIZE = 5; // number of rows/columns in square grid
   private final int NUM_POS = SIZE * SIZE; // number of positions in square grid
   protected final int NUM_CARDS = Card.NUM_CARDS; // number of cards in deck
   protected Random random = new Random(); // pseudorandom number generator for Monte Carlo simulation 
   private int[] plays = new int[NUM_POS]; // positions of plays so far (index 0 through numPlays - 1) recorded as integers using row-major indices.
   // row-major indices: play (r, c) is recorded as a single integer r * SIZE + c (See http://en.wikipedia.org/wiki/Row-major_order)
   // From plays index [numPlays] onward, we maintain a list of yet unplayed positions.
   public int numPlays = 0; // number of Cards played into the grid so far
   protected PokerSquaresPointSystem pointSystem; // point system
   protected Card[][] grid = new Card[SIZE][SIZE]; // grid with Card objects or null (for empty positions)
   protected Card[] gameDeck = Card.getAllCards(); // a list of all Cards. As we learn the index of cards in the play deck,
	                                             // we swap each dealt card to its correct index.  Thus, from index numPlays 
												 // onward, we maintain a list of undealt cards for MC simulation.
   protected int verbosity; //Verbosity level of output. 0 is no output, 1 is select/expand/simulate/backpropogate, 2 is decision and trial statistics,
   								//3 is node statistics, 4 is simulation, 5 looks at card decks and draws
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
   
   //long possibleMovesStartTime;
   //long possibleMovesEndTime;
   //long simulateStartTime;
   //long simulateEndTime;
   
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
	  this.verbosity = 0;
   }

   public void updateGameState(xMCTSStringGameState s)
   {
//      if (curNode.isLeaf()) {
//   		 if (verbosity > 1) 
//   			 System.out.println("expanding curNode in updateGameState");
//         ((xMCTSPruningChanceNode) curNode).chanceExpand("", true);//updateGameState should only ever be called from a chance node, to update to a drawn card
//      }
      try {
    	//  System.out.println("This node's children:");
    	//  for (xMCTSPruningNode child : curNode.nextMoves) {
    	//	  System.out.println(child.getState().toString());
    	//  }
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
	 //long timeSpentThisCall = System.currentTimeMillis();

	 
	// match gameDeck to actual play event; in this way, all indices forward from the card contain a list of unplayed cards
	  	int cardIndex = numPlays;
		 while (!card.equals(gameDeck[cardIndex])) {
			 cardIndex++;
			 //System.out.println("Card index = " + cardIndex);
		 }
		 gameDeck[cardIndex] = gameDeck[numPlays];
		 gameDeck[numPlays] = card; 
		 //System.out.println("gameDeck -" + java.util.Arrays.toString(gameDeck));
		 
	//match gameCanDraw to the drawn card
		 gameCanDraw[(card.getRank() + (card.getSuit() * xCard.NUM_RANKS))] = false;
		 
    if (verbosity > 4)
    	System.out.println("gameDeck: " + java.util.Arrays.toString(gameDeck));
	 String activeState = currentState.toString().substring(0, currentState.toString().length() - 2);
	 activeState += card.toString();
	 
//	 System.out.println("curNode = " + curNode.getState().toString());
	 
	 if (numPlays == 0) {
//		 System.out.println("curNode = " + curNode.getState().toString());
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
//		 for (int i = 0; i < 5; i++) {
//			 for (int j = 0; j < 5; j++) {
//				 int pos = i * 10 + j * 2;
//				 String temp = secondMoveState.toString().substring(pos, pos + 2);
//				 grid[i][j] = Card.getCard(temp);
//			 }
//		 }
//		 pointSystem.printGrid(grid);
//		 System.out.println();

		 //Create child for second possible relevant move (away from first card)
		 secondMoveState = (activeState.substring(0, 12) + card.toString() + activeState.substring(14, activeState.length()));
		 xMCTSStringGameState playByItself = new xMCTSStringGameState(secondMoveState, g.getExpectedBoardScore(secondMoveState, numPlays + 1, gameCanDraw, 5), numPlays + 1);
//		 for (int i = 0; i < 5; i++) {
//			 for (int j = 0; j < 5; j++) {
//				 int pos = i * 10 + j * 2;
//				 String temp = secondMoveState.toString().substring(pos, pos + 2);
//				 grid[i][j] = Card.getCard(temp);
//			 }
//		 }
//		 pointSystem.printGrid(grid);
//		 System.out.println();

		 //Only create the child with the best expected value
		 if (playNextToFirst.expectedValue > playByItself.expectedValue) {
			 //System.out.println("Played this card next to first");
			 curNode.createChildNode(playNextToFirst, gameDeck, gameCanDraw);
			 updateGameState(playNextToFirst); //update, curNode is once again chance
		 }
		 else {
			 //System.out.println("Played this card by itself");
			 curNode.createChildNode(playByItself, gameDeck, gameCanDraw);
			 updateGameState(playByItself); //update, curNode is once again chance
		 }
		 ((xMCTSPruningChanceNode) curNode).chanceExpand("", true);
	 }
	 
  	 else if (numPlays < 23) {
  		 remainingPlays = (NUM_POS - numPlays); // ignores triviality of last few plays to keep a conservative margin for game completion
  	     millisPerPlay = (millisRemaining - 10) / (remainingPlays - 2); // dividing time evenly with future getPlay() calls
  	     startTime = System.currentTimeMillis();
  	     endTime = startTime + millisPerPlay;
  		 millisRemainingFromGetPlayStart = millisRemaining;
  		 
  		 if (verbosity > 1) {
  		 	 System.out.println();
  			 System.out.println("Starting getPlay with card: " + card.toString());
  		 	 System.out.println("numPlays from getPlay start = " + numPlays);
  		 	 System.out.println("milliseconds remaining in the game = " + millisRemaining);
  	  		 System.out.println("remainingPlays = " + remainingPlays);
  	  		 System.out.println("millisPerPlay = " + millisPerPlay);
  	  		 System.out.println("curNode's numPlays = " + curNode.nodeGameState.numPlays);
  	     }
  		 
  		 //Change from chance node to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 
  		 //      Thread a = new Thread(){
  		 //         @Override
  		 //        public void run()
  		 //        {
  		 //           int extraTrials = 0;
  		 //            while (System.currentTimeMillis() < endTime) //(trials < 4000)
  		 //            {
  		 //               runTrial(curNode, true);
  		 //               extraTrials++;
  		 //            }           
  		 //            System.out.println("Thread a Ran " + extraTrials + " trials in " + (System.currentTimeMillis() - startTime)  + "ms.");
  		 //        }
  		 //      };
  		 //      a.start();
  		 //      
  		 //      
  		 //      Thread b = new Thread(){
  		 //         @Override
  		 //        public void run()
  		 //        {
  		 //           int extraTrials = 0;
  		 //            while (System.currentTimeMillis() < endTime) //(trials < 4000)
  		 //            {
  		 //               runTrial(curNode, true);
  		 //               extraTrials++;
  		 //            }           
  		 //            System.out.println("Thread b Ran " + extraTrials + " trials in " + (System.currentTimeMillis() - startTime)  + "ms.");
  		 //        }
  		 //      };
  		 //      b.start();
  		 //      
  		 //      Thread c = new Thread(){
  		 //         @Override
  		 //        public void run()
  		 //        {
  		 //           int extraTrials = 0;
  		 //            while (System.currentTimeMilstatelis() < endTime) //(trials < 4000)
  		 //            {
  		 //               runTrial(curNode, true);
  		 //               extraTrials++;
  		 //            }           
  		 //            System.out.println("Thread c Ran " + extraTrials + " trials in " + (System.currentTimeMillis() - startTime)  + "ms.");
  		 //        }
  		 //      };
  		 //      c.start();    

  		//long beginTrialTime = -69;
  		//long endTrialTime = -178;
  		 while (System.currentTimeMillis() < endTime) //|| a.isAlive() || b.isAlive() || c.isAlive()) //(trials < 4000)
  		 {
  			 if (verbosity > 1) 
  				 System.out.println("Running trial with node " + curNode.toString());
  			 
  			 //beginTrialTime = System.currentTimeMillis();
  			 
  			 runTrial(curNode, true, 0);
  			 getPlayTrials++;
  			 
  			// endTrialTime = System.currentTimeMillis();
  			// if ((endTrialTime - beginTrialTime) > 100) {
  			 //	System.out.println("MS taken for that trial = " + (endTrialTime - beginTrialTime));
  			 //	System.out.println("Total time left for trials = " + (endTime - System.currentTimeMillis()));
  			// 	System.out.println("getPlayTrials = " + getPlayTrials + "\n");
  	 		//}

  		 }

  		 if (g.gameStatus(currentState) == xMCTSGame.status.ONGOING) {
  			 //System.out.println("curNode = " + curNode.getState().toString());
  			 xMCTSPruningNode best = curNode.bestMove();
  			 currentState = best.getState();
  			 curNode = best;
  			 //System.out.println("curNode = " + curNode.getState().toString());
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
  			// System.out.println("two children remaining");
  		 String[] possibleMoves = new String[2];
  		 int index = 0;
  		// System.out.println("curNode = " + curNode.getState());
  		 //System.out.println(curNode.getState().expectedValue);
  		// System.out.println("curNode's children:");
  		// for (int i = 0; i < curNode.nextMoves.size(); i++) {
  		//	 System.out.println(curNode.nextMoves.get(i));
  		//	 System.out.println(curNode.nextMoves.get(i).getState().expectedValue);
  		// }

  		 int gridSize = SIZE*2; //Size is the size of the actual game grid... with 2 characters per card, size*2 is the grid size of string representations
  		 for (int row = 0; row < gridSize; row += 2) {//for each position in this state being checked
  			 for (int col = 0; col < gridSize; col += 2) {
  				 int pos = row * SIZE + col;
  				 if (currentState.toString().charAt(pos) == '_') {
  					 possibleMoves[index] = currentState.toString().substring(0, pos)
  							 + card.toString()
  							 + currentState.toString().substring(pos + 2,
  									 currentState.toString().length());
  					 index++;
  					 if (index == 2)
  					 	 break;
  				 }
  			 }
  		 }

  		 //Create child for first possible move (first blank space found)
  		 xMCTSStringGameState playInFirstOpen = new xMCTSStringGameState(possibleMoves[0], g.getExpectedBoardScore(possibleMoves[0], numPlays + 1, gameCanDraw, 5), numPlays + 1); 
//  		 for (int i = 0; i < 5; i++) {
//  			 for (int j = 0; j < 5; j++) {
//  				 int pos = i * 10 + j * 2;
//  				 String temp = possibleMoves[0].toString().substring(pos, pos + 2);
//  				 grid[i][j] = Card.getCard(temp);
//  			 }
//  		 }
  		// System.out.println("First Open");
  		// System.out.println("Expected value = " + playInFirstOpen.expectedValue);
  		// pointSystem.printGrid(grid);
  		// System.out.println();

  		 //Create child for second possible move (second blank space found)
  		 xMCTSStringGameState playInSecondOpen = new xMCTSStringGameState(possibleMoves[1], g.getExpectedBoardScore(possibleMoves[1], numPlays + 1, gameCanDraw, 5), numPlays + 1);
//  		 for (int i = 0; i < 5; i++) {
//  			 for (int j = 0; j < 5; j++) {
//  				 int pos = i * 10 + j * 2;
//  				 String temp = possibleMoves[1].toString().substring(pos, pos + 2);
//  				 grid[i][j] = Card.getCard(temp);
//  			 }
//  		 }
  		// System.out.println("Second Open");
  		// System.out.println("Expected value = " + playInSecondOpen.expectedValue);
  		// pointSystem.printGrid(grid);
  		// System.out.println();

  		 //Only create the child with the best expected value
  		 if (playInFirstOpen.expectedValue > playInSecondOpen.expectedValue) {
  			 //System.out.println("Played this card in first open");
  			 updateGameState(playInFirstOpen); //update, curNode is once again chance
  		 }
  		 else {
  			// System.out.println("Played this card in second open");
  			 updateGameState(playInSecondOpen); //update, curNode is once again chance
  		 }
  		 }
  	 }

  	 else if (numPlays == 24) {
  		 //System.out.println("activeState 24th move  = " + activeState);
  		 //System.out.println("currentState 24th move = " + currentState.toString());
  		 //Chance to choice node
  		 updateGameState(new xMCTSStringGameState(activeState, currentState.expectedValue, numPlays));
  		 //System.out.println("curNode = " + curNode.getState().toString());
  		 ArrayList<xMCTSStringGameState> possibleMoves = g.getPossibleMoves(curNode.getState(), curNode.nodeCanDraw);
  		 //System.out.println("possibleMoves size= " + possibleMoves.size());
  		// System.out.println("is curNode expanded? " + curNode.expanded);
  		 ((xMCTSPruningChoiceNode) curNode).choiceExpand(possibleMoves, currentState.toString(), false);
  		// System.out.println("is curNode expanded? " + curNode.expanded);
//
//
//  		 System.out.println("num next moves = " + curNode.nextMoves.size());
  		 xMCTSPruningNode best = curNode.bestMove();
  		 currentState = best.getState();
  		 curNode = best;
  		 //System.out.println("curNode = " + curNode.getState().toString());
  	 }
	 
//	 System.out.println("Total getPlay Trials = " + getPlayTrials);
//		 for (int i = 0; i < 5; i++) {//row
//  			 for (int j = 0; j < 5; j++) {//column
//  				 int position = i * 10 + j * 2;
//  				 String tempp = currentState.toString().substring(position, position + 2);
//  				 grid[i][j] = Card.getCard(tempp);
//  			 }
//  		 }
//  		 pointSystem.printGrid(grid);

	 numPlays++;
	 //return an array holding the row and column of the move that results in the bestMove node
	 int[] temp = curNode.bestMoveLocation(card);
	 if (verbosity > 1) {
		 System.out.println("Done thinking. Chosen Row: " + temp[0] + " Column: " + temp[1]);
	 }
      
	 long timeAtEndOfCall = System.currentTimeMillis();
	 //System.out.println("MS spent in this call to getPlay = " + (timeAtEndOfCall - timeSpentThisCall));
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
   private float runTrial(xMCTSPruningNode node, boolean myTurn, int level)	{
	   // long runTrialStartTime = System.currentTimeMillis();
	   String tab = "";
	   for (int i = 0; i < level; i++) {
		   tab += "\t";
	   }
	   float returnScore;
	   node.visit();
	   if (g.gameStatus(node.getState()) == xMCTSGame.status.ONGOING) {
		   if (!node.isLeaf()) {
			   //selection
			   if (verbosity > 0)
				   System.out.println(tab + "Selecting");

			   xMCTSPruningNode nextChild = node.bestSelection(myTurn, tab);
			  // long recCallStartTime = System.currentTimeMillis();
			   returnScore = runTrial(nextChild, myTurn, level + 1);
			   //long recCallEndTime = System.currentTimeMillis();
			   //if ((recCallEndTime - recCallStartTime) > 100) {
				//   System.out.println("Time in recursive calls to runTrial = " + (recCallEndTime - recCallStartTime));
				//   System.out.println("that call's level = " + (level + 1));
				//   System.out.println("that trial started at node " + nextChild);
			   //}
		   } else {

			   if (verbosity > 1)
				   System.out.println(tab + "Node being simulated from: " + node.getState());

			   //         int simPlays = node.getState().numPlays;
			   //System.out.println(tab + "numPlays from this trial's node = " + node.getState().numPlays);
			   if (node.choiceNode()) {
				   //expansion
				   if (verbosity > 0)
					   System.out.println(tab + "Choice Expanding");

				   //possibleMovesStartTime = System.currentTimeMillis();
				   ArrayList<xMCTSStringGameState> possibleMoves = g.getPossibleMoves(node.getState(), node.nodeCanDraw);
				  // possibleMovesEndTime = System.currentTimeMillis();
				   //if ((possibleMovesEndTime - possibleMovesStartTime) > 100) {
					//   System.out.println("Time in this call to getPossibleMoves = " + (possibleMovesEndTime - possibleMovesStartTime));
					//   System.out.println("node simulated from = " + node.getState().toString());
				   //}

				   //if (curNode.getState().numPlays == 21)
				   //	System.out.println(tab + "this getPossibleMoves run time = " + (possibleMovesEndTime - possibleMovesStartTime));
				   //long expandStartTime = System.currentTimeMillis();
				   ((xMCTSPruningChoiceNode) node).choiceExpand(possibleMoves, tab, false); //to be in this if statement it must be a choice node
				   //long expandEndTime = System.currentTimeMillis();
				   //if ((expandEndTime - expandStartTime) > 100) {
					//   System.out.println("Time in this call to choiceExpand = " + (expandEndTime - expandStartTime));
					//   System.out.println("node being expanded = " + node.getState().toString());
				   //}

				   //simulation
				   if (verbosity > 0)
					   System.out.println(tab + "Simulating");

				   //simulateStartTime = System.currentTimeMillis();
				   returnScore = simulateFrom(node.getState(), false, tab, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), java.util.Arrays.copyOf(node.nodeCanDraw, node.nodeCanDraw.length)); // copy(node.getDeck)
				   //simulateEndTime = System.currentTimeMillis();
				   //if ((simulateEndTime - simulateStartTime) > 100) {
					//   System.out.println("Time in this originally choice simulation = " + (simulateEndTime - simulateStartTime));
					//   System.out.println("node simulated from = " + node.getState().toString());
				  // }
			   }
			   else {
				   //expansion
				   if (verbosity > 0)
					   System.out.println(tab + "Chance Expanding");
				   //long expandStartTime = System.currentTimeMillis();
				   ((xMCTSPruningChanceNode) node).chanceExpand(tab, false);//to be in this else it must be a chance node
				   //long expandEndTime = System.currentTimeMillis();
				   //if ((expandEndTime - expandStartTime) > 100) {
					//   System.out.println("Time in this call to chanceExpand = " + (expandEndTime - expandStartTime));
					//   System.out.println("node expanded = " + node.getState().toString());
				   //}

				   //simulation
				   if (verbosity > 0)
					   System.out.println(tab + "Simulating");

				   //simulateStartTime = System.currentTimeMillis();
				   returnScore = simulateFrom(node.getState(), true, tab, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), java.util.Arrays.copyOf(node.nodeCanDraw, node.nodeCanDraw.length)); // copy(node.getDeck)
				   //simulateEndTime = System.currentTimeMillis();
				   //if ((simulateEndTime - simulateStartTime) > 100) {
				//	   System.out.println("Time in this originally chance simulation = " + (simulateEndTime - simulateStartTime));
					//   System.out.println("node simulated from = " + node.getState().toString());
				   //}
			   }

		   }
		   //backpropogation
		   node.setScore(node.getScore() + returnScore);
		   if (verbosity > 0)
			   System.out.println(tab + "Backpropogating from node " + node.toString());

		   if (verbosity > 2)
			   System.out.println(tab + "Adding score of " + returnScore + " to this node. Score is now " + node.getScore());

		   //if (curNode.getState().numPlays == 21)
		   //	System.out.println(tab + "this trial's run time = " + (runTrialEndTime - runTrialStartTime));
	   }
	   else {
		   for (int i = 0; i < 5; i++) {//row
			   for (int j = 0; j < 5; j++) {//column
				   int position = i * 10 + j * 2;
				   String temp = node.getState().toString().substring(position, position + 2);
				   grid[i][j] = Card.getCard(temp);
			   }
		   }
		   //System.out.println("Finished game");
		   //pointSystem.printGrid(grid);
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
   protected abstract float simulateFrom(xMCTSStringGameState state, boolean randomize, String tab, Card[] simDeck,  boolean[] simCanDraw);
   
   //Code added from PokerSquaresPlayer
   public abstract void setPointSystem(PokerSquaresPointSystem pointSystem, long millis);
	
	/**
	 * init - initializes the player before each game
	 */
	public void init() {
		//System.out.println("initializing");
		// clear grid
		for (int row = 0; row < SIZE; row++)
			for (int col = 0; col < SIZE; col++)
				grid[row][col] = null;
		// reset numPlays
		numPlays = 0;
		// (re)initialize list of play positions (row-major ordering)
		for (int i = 0; i < NUM_POS; i++)
			plays[i] = i;
//		curNode = rootNode ; //Set curNode equal to the starting node so the same tree is used over multiple games
	    player1 = true;
	    //gameTree = new MCTSNode(curState);
	    //curNode = gameTree;
	    
	    gameCanDraw = new boolean[] {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
				true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true}; 
	    
	    g.resetTrackingValues();
	    currentState = g.getStartingState();
	    curNode = new xMCTSPruningChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), verbosity, C, java.util.Arrays.copyOf(gameCanDraw, gameCanDraw.length));
	    totalSingleGameTrials = 0;
	}
	
	
	/**
	 * getName - gets the uniquely identifying name of the Poker Squares player.  The name should be 20 characters or less.
	 * @return unique player name
	 */
	public abstract String getName();
}
