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
 * An MCTSPlayer with a purely random rollout for hte simulation phase. This
 * player employs no game-specific heuritics, nor does it take special action
 * for moves that are garaunteed to win or lose.
 *
 * @author Kyle
 */
import java.util.ArrayList;
import java.util.Random;

public class xRandomRolloutPruningPlayer extends xMCTSPPSPruningPlayer
{

   public xRandomRolloutPruningPlayer(float C)
   {
      super();
      //The constant used for UCT calculating
      this.C = C;
   }

   /**
    * Simulates a random play-through from a given state and returns the result.
    *
    * @param state The state to be simulated from.
    * @return the game status at the end of the simulation.
    */
   @Override
   protected float simulateFrom(xMCTSStringGameState state, boolean randomize, Card[] simDeck,  boolean[] simCanDraw) // simDeck
   {
      xMCTSGame.status s = g.gameStatus(state);
      if (s != xMCTSGame.status.ONGOING) {
    	  for (int i = 0; i < 5; i++) {
	            for (int j = 0; j < 5; j++) {
	            	int pos = i * 10 + j * 2;
	            	String temp = state.toString().substring(pos, pos + 2);
	            	grid[i][j] = Card.getCard(temp);
	            }
    	  }
        	 
    	 float score = pointSystem.getScore(grid);
         return score;
      } else {
         return simulateFrom(getRandomMoveFrom(state, simDeck, randomize, simCanDraw), true, simDeck, simCanDraw); // simDeck
      }
   }

   /**
    * Gets a random move from a given state.
    *
    * @param gameState a game state from which a random child state is desired.
    * @return a random child state of the passed state.
    */
   private xMCTSStringGameState getRandomMoveFrom(xMCTSStringGameState gameState, Card[] simDeck, boolean randomize, boolean[] simCanDraw)
   {
      Random random = new Random();
      int totalSimPlays = gameState.numPlays;
      xMCTSStringGameState simState;
      if (randomize) {
	      int simCard = random.nextInt(NUM_CARDS - totalSimPlays) + totalSimPlays; //a randomly selected card
	      Card card = simDeck[simCard]; //Match deck to event
	      simDeck[simCard] = simDeck[totalSimPlays];
		  simDeck[totalSimPlays] = card;
		  
		  simCanDraw[card.getRank() + (card.getSuit() * xCard.NUM_RANKS)] = false; //match canDraw to event
		  
		  simState = new xMCTSStringGameState(gameState.toString().substring(0, gameState.toString().length() -2) + card.toString(), gameState.expectedValue, totalSimPlays);
      }
      else
    	  simState = gameState;
      
      ArrayList<xMCTSStringGameState> moves = g.getBestSimMove(simState, simCanDraw);
	  int chosenMoveIndex = random.nextInt(moves.size()); //the new board state
      return moves.get(chosenMoveIndex);
   }
   
   public void setPointSystem(PokerSquaresPointSystem pointSystem, long millis) {
	   this.pointSystem = pointSystem;
	   g = new xMCTSPruningPPSGame(pointSystem);
	   System.gc();
   }
   
   public String getName() {
	   return "xRandomRolloutPruningPlayer" + C;
   }
}
