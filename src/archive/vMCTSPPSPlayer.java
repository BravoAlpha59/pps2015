package archive;
import Card;
import PokerSquaresPlayer;
import PokerSquaresPointSystem;
import xMCTSGame;
import xMCTSGameState;
import xMCTSPPSGame;
import xMCTSStringGameState;

import java.util.Map;
import java.util.Random;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import xMCTSGame.status;


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
 * vMCTS player is almost identical to xMCTS player, however it holds theoretical improvements to 
 * functionality that we can use to test against the code as it currently stands
 *
 *Currently testing: multithreading with each thread making their own copy of the root node
 */
public abstract class vMCTSPPSPlayer implements PokerSquaresPlayer
{

  protected xMCTSGame g;
  protected boolean player1;
   protected xMCTSGameState currentState;
   private vMCTSNode curNode;
   private vMCTSNode aCurNode;
   private vMCTSNode bCurNode;
   private vMCTSNode cCurNode;
   private vMCTSNode rootNode;
   private vMCTSNode aRootNode;
   private vMCTSNode bRootNode;
   private vMCTSNode cRootNode;
   
   //fields added from GreedyMCPlayer
   private final int SIZE = 5; // number of rows/columns in square grid
   private final int NUM_POS = SIZE * SIZE; // number of positions in square grid
   protected final int NUM_CARDS = Card.NUM_CARDS; // number of cards in deck
   protected Random random = new Random(); // pseudorandom number generator for Monte Carlo simulation 
   private int[] plays = new int[NUM_POS]; // positions of plays so far (index 0 through numPlays - 1) recorded as integers using row-major indices.
   // row-major indices: play (r, c) is recorded as a single integer r * SIZE + c (See http://en.wikipedia.org/wiki/Row-major_order)
   // From plays index [numPlays] onward, we maintain a list of yet unplayed positions.
   protected int numPlays = 0; // number of Cards played into the grid so far
   protected PokerSquaresPointSystem pointSystem; // point system
   protected Card[][] grid = new Card[SIZE][SIZE]; // grid with Card objects or null (for empty positions)
   protected Card[] gameDeck = Card.getAllCards(); // a list of all Cards. As we learn the index of cards in the play deck,
	                                             // we swap each dealt card to its correct index.  Thus, from index numPlays 
												 // onward, we maintain a list of undealt cards for MC simulation.
   protected int verbosity; //Verbosity level of output. 0 is no output, 1 is select/expand/simulate/backpropogate, 2 is decision and trial statistics,
   								//3 is node statistics, 4 is simulation, 5 looks at card decks and draws
   protected float C; //constant used in the UCT formula
   protected int totalTrials = 0;
   protected int totalSingleGameTrials;
   protected HashMap<String, int[]> stateToCountArray = new HashMap<String, int[]>(10000);
   protected double overallUniquePercentage;
   
   
   /**
    * Gets the current state of the game.
    *
    * @return the current state of the game.
    */
   public xMCTSGameState getCurrentState()
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
   public vMCTSPPSPlayer()
   {
	  this.verbosity = 0;
      init();
	   
   }

   public void updateGameState(xMCTSGameState s)
   {
      if (curNode.isLeaf()) {
         curNode.expand(g.getPossibleMoves(curNode.getState()), "", true);
      }
      for (int i = 0; i < curNode.nextMoves.size(); i++) {
      }
      try {
    	  curNode = curNode.findChildNode(s);
      }
      catch (Exception e) {
    	  System.out.println("updateGameState: " + java.util.Arrays.toString(curNode.nodeDeck));
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

	   if (verbosity > 1) {
		   System.out.println("Starting getPlay with card: " + card.toString());
		   System.out.println("numPlays = " + numPlays);
	   }
	   int parentTrials = 0;
	   AtomicInteger aTrials = new AtomicInteger(0);
	   AtomicInteger bTrials = new AtomicInteger(0);
	   AtomicInteger cTrials = new AtomicInteger(0); 

	   // match simDeck to actual play event; in this way, all indices forward from the card contain a list of
	   int cardIndex = numPlays;
	   while (!card.equals(gameDeck[cardIndex]))
		   cardIndex++;
	   gameDeck[cardIndex] = gameDeck[numPlays];
	   gameDeck[numPlays] = card; 
	   if (verbosity > 4)
		   System.out.println("gameDeck: " + java.util.Arrays.toString(gameDeck));
	   String activeState = currentState.toString().substring(0, currentState.toString().length() - 2);
	   activeState += card.toString();
	   //Change from chance node to choice node
	   updateGameState(new xMCTSStringGameState(activeState));
	   aCurNode = aCurNode.findChildNode(curNode.getState());
	   bCurNode = bCurNode.findChildNode(curNode.getState());
	   cCurNode = cCurNode.findChildNode(curNode.getState());

	   if (numPlays == 0)
			curNode.expand(g.getPossibleMoves(curNode.getState()), "", false);
	   
	   if (numPlays < 24) {
		   int remainingPlays = NUM_POS - numPlays; // ignores triviality of last play to keep a conservative margin for game completion
		   long millisPerPlay = millisRemaining / remainingPlays; // dividing time evenly with future getPlay() calls
		   final long startTime = System.currentTimeMillis();
		   long endTime = startTime + millisPerPlay;
		   Thread a = new Thread(){
			   @Override
			   public void run()
			   {
				   while (System.currentTimeMillis() < endTime) //(trials < 4000)
				   {
					   runTrial(aCurNode, true, 0);
					   aTrials.getAndIncrement();
				   }           
				   if (verbosity > 1)
					   System.out.println("Thread a Ran " + aTrials + " trials in " + (System.currentTimeMillis() - startTime)  + "ms.");

			   }
		   };
		   a.start();


		   Thread b = new Thread(){
			   @Override
			   public void run()
			   {
				   while (System.currentTimeMillis() < endTime) //(trials < 4000)
				   {
					   runTrial(bCurNode, true, 0);
					   bTrials.getAndIncrement();
				   }           
				   if (verbosity > 1)
					   System.out.println("Thread b Ran " + bTrials + " trials in " + (System.currentTimeMillis() - startTime)  + "ms.");

			   }
		   };
		   b.start();

		   Thread c = new Thread(){
			   @Override
			   public void run()
			   {
				   while (System.currentTimeMillis() < endTime) //(trials < 4000)
				   {
					   runTrial(cCurNode, true, 0);
					   cTrials.getAndIncrement();
				   }    
				   if (verbosity > 1)
					   System.out.println("Thread c Ran " + cTrials + " trials in " + (System.currentTimeMillis() - startTime)  + "ms.");

			   }
		   };
		   c.start();    

		   while (System.currentTimeMillis() < endTime || a.isAlive() || b.isAlive() || c.isAlive()) //(trials < 4000)
		   {
			   if (verbosity > 1) 
				   System.out.println("Running trial with node " + curNode.toString());

			   runTrial(curNode, true, 0);
			   parentTrials++;
		   }
		   if (verbosity > 1)
			   System.out.println("Parent ran " + parentTrials + " trials in " + (System.currentTimeMillis() - startTime) + "ms.");
	   }

	   if (g.gameStatus(currentState) == xMCTSGame.status.ONGOING) {
		   vMCTSNode best = curNode.bestMove();
		   if (best.getScore() < aCurNode.bestMove().getScore()) {
			   //System.out.println("Choosing child a's move");
			   best = curNode.findChildNode(aCurNode.bestMove().getState());
		   }

		   if (best.getScore() < bCurNode.bestMove().getScore()) {
			   //System.out.println("Choosing child b's move");
			   best = curNode.findChildNode(bCurNode.bestMove().getState());
		   }

		   if (best.getScore() < cCurNode.bestMove().getScore()) {
			   //System.out.println("Choosing child c's move");
			   best = curNode.findChildNode(cCurNode.bestMove().getState());
		   }

		   currentState = best.getState();
		   curNode = best;
	   }

	   aCurNode = aCurNode.findChildNode(curNode.getState());
	   bCurNode = bCurNode.findChildNode(curNode.getState());
	   cCurNode = cCurNode.findChildNode(curNode.getState());

	   // if this is the first call to getPlay, then store curNode, aCurNode, bCurNode, and cCurNode in other variables that we can analyze later.

	   // If this is the last call to getPlay, then analyze the previously stored root nodes for each thread.


	   //return an array holding the row and column of the move that results in the bestMove node
	   int[] temp = curNode.bestMoveLocation(card);
	   if (verbosity > 1) {
		   System.out.println("Done thinking. Chosen Row: " + temp[0] + " Column: " + temp[1]);
	   }
	   numPlays++;

	   if (numPlays == 18) {
		   aRootNode = aCurNode;
		   bRootNode = bCurNode;
		   cRootNode = cCurNode;
		   rootNode = curNode;
		   System.out.println("parent trials  : " + parentTrials);
		   System.out.println("A Thread trials: " + aTrials.intValue());
		   System.out.println("B Thread trials: " + bTrials.intValue());
		   System.out.println("C Thread trials: " + cTrials.intValue());
	   }

	   totalSingleGameTrials += parentTrials;
	   totalSingleGameTrials += aTrials.intValue();
	   totalSingleGameTrials += bTrials.intValue();
	   totalSingleGameTrials += cTrials.intValue();
	   if (numPlays == 25) {
		   preorder(aRootNode, 0);
		   preorder(bRootNode, 1);
		   preorder(cRootNode, 2);
		   preorder(rootNode, 3);

		   String node1Str = rootNode.getState().toString();
		   int[] node1VisitCounts = stateToCountArray.get(node1Str);
		   System.out.println("node1VisitCounts: " + java.util.Arrays.toString(node1VisitCounts));

		   String node2Str = rootNode.getRandomChild().toString();
		   int[] node2VisitCounts = stateToCountArray.get(node2Str);
		   System.out.println("node2VisitCounts: " + java.util.Arrays.toString(node2VisitCounts));



		   overlap();
		   System.out.println("Unique percentage of state visits in this game = " + overallUniquePercentage);
		   System.out.println("Total trials this game: " + totalSingleGameTrials);
		   totalTrials += totalSingleGameTrials;
		   System.out.println("Total trials overall  : " + totalTrials);
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
   private float runTrial(vMCTSNode node, boolean myTurn, int level)
   {
	  String tab = "";
	  for (int i = 0; i < level; i++) {
		  tab += "\t";
	  }
      float returnScore;
      node.visit();
      if (!node.isLeaf()) {
         //selection
    	 if (verbosity > 0)
    		 System.out.println(tab + "Selecting");
    	 
         returnScore = runTrial(node.bestSelection(myTurn, tab), myTurn, level + 1);
      } else {
         //expansion
    	 if (verbosity > 0)
    		 System.out.println(tab + "Expanding");
    	 
         node.expand(g.getPossibleMoves(node.getState()), tab, false);
         //simulation
         if (verbosity > 0)
        	 System.out.println(tab + "Simulating");
         
         if (verbosity > 1)
        	 System.out.println(tab + "Node being simulated from: " + node.getState());
         
         int simPlays = 25 - (g.getPossibleMoves(node.getState()).size());
         if (node.choiceNode())
        	 returnScore = simulateFrom(node.getState(), false, tab, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), simPlays); // copy(node.getDeck)
         else 
        	 returnScore = simulateFrom(node.getState(), true, tab, java.util.Arrays.copyOf(node.nodeDeck, node.nodeDeck.length), simPlays); // copy(node.getDeck)
      }
      //backpropogation
      node.setScore(node.getScore() + returnScore);
      if (verbosity > 0)
      	  System.out.println(tab + "Backpropogating from node " + node.toString());
      
      if (verbosity > 2)
    	  System.out.println(tab + "Adding score of " + returnScore + " to this node. Score is now " + node.getScore());
      
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
   protected abstract float simulateFrom(xMCTSGameState state, boolean randomize, String tab, Card[] simDeck, int totalSimPlays);
   
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
		// (re)initialize list of play positions (row-major ordering)
		for (int i = 0; i < NUM_POS; i++)
			plays[i] = i;
//		curNode = rootNode ; //Set curNode equal to the starting node so the same tree is used over multiple games
		g = new xMCTSPPSGame();
	    player1 = true;
	      currentState = g.getStartingState();
	      //gameTree = new MCTSNode(curState);
	      //curNode = gameTree;
	      
	      curNode = new vMCTSChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), verbosity, C);
	      aCurNode = new vMCTSChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), verbosity, C);
	      bCurNode = new vMCTSChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), verbosity, C);
	      cCurNode = new vMCTSChanceNode(currentState, java.util.Arrays.copyOf(gameDeck, gameDeck.length), verbosity, C);
	      
	      rootNode = curNode;
	      aRootNode = aCurNode;
	      bRootNode = bCurNode;
	      cRootNode = cCurNode;
	      
	      totalSingleGameTrials = 0;
	      overallUniquePercentage = 0;
	}
	
	private void preorder(vMCTSNode root, int index) {
		if (root.equals(null))
			return;
		else {
			visit(root, index);
			if (!root.isLeaf()) {
				for (vMCTSNode child : root.nextMoves) {
					preorder(child, index);
				}
			}
		}
	}
	
	private void visit(vMCTSNode root, int index) {
	   	int[] currStateCount = stateToCountArray.get(root.getState().toString());
	   	if (currStateCount == null) {
	   		// make a new int[] and put the count in the right index in the array
	   		int[] visitedCountArray = {0, 0, 0, 0};
	   		visitedCountArray[index] = root.getTimesVisited();
	   		stateToCountArray.put(root.getState().toString(),  visitedCountArray);
	   	}
	   	else {
	   		currStateCount[index] = root.getTimesVisited();
	   		//or do you have to use the replace method in HashMap also. Probably not, but double check.
	   	}
	}
	
	private void overlap() {
		double sum = 0.0;
		double nodesCounted = 0.0;
		for (Map.Entry<String, int[]> e : stateToCountArray.entrySet()) {
			int[] visitCounts = e.getValue();
			int highestValueIndex = 0;
			int highestValue = visitCounts[highestValueIndex];
			int totalVisits = visitCounts[highestValueIndex];
			for (int i = 1; i < 4; i++) {
				if (highestValue < visitCounts[i]) {
					highestValue = visitCounts[i];
					highestValueIndex = i;
				}
				totalVisits += visitCounts[i];
			}
			if (totalVisits > 10) {
				double uniquePercentage = ((double) visitCounts[highestValueIndex]) / totalVisits;  // high means no overlap (unique). Near 25% means tons of overlap.
				sum += uniquePercentage;
				nodesCounted++;
			}
		}
		overallUniquePercentage = sum / nodesCounted;
	}
	
	
	
	/**
	 * getName - gets the uniquely identifying name of the Poker Squares player.  The name should be 20 characters or less.
	 * @return unique player name
	 */
	public abstract String getName();
}
