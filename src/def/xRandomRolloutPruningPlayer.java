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
import java.util.Arrays;
import java.util.Random;

public class xRandomRolloutPruningPlayer extends xMCTSPPSPruningPlayer
{

   public xRandomRolloutPruningPlayer(float AMERICANC, float AMERITISHC, float BRITISHC, float OTHERC)
   {
      super();
      //The constant used for UCT calculating
      this.AMERICANC = AMERICANC;
      this.AMERITISHC = AMERITISHC;
      this.BRITISHC = BRITISHC;
      this.OTHERC = OTHERC;
   }

   /**
    * Performs a simulation or "rollout" for the "simulation" phase of the
    * runTrial function. This can be written to contain game-specific heuristics
    * or "finishing move" detection if desired. It is called recursively until the board is filled.
    *
    * @param state the state to be simulated from.
    * @param randomize whether or not to begin simulating by randomly drawing a card
    * @param simDeck the deck to be used for simulating, determines draw orders without affecting the player or node decks
    * @param simCanDraw the canDraw boolean array to be used for simulating, determines which cards are still available to be drawn
    * @return the resulting value of the simulated end game state.
    */
   @Override
   protected float simulateFrom(xMCTSStringGameState state, boolean randomize, Card[] simDeck,  boolean[] simCanDraw)
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
    	  //randomize is always true after the first call to simulateFrom. 
         return simulateFrom(getRandomMoveFrom(state, simDeck, randomize, simCanDraw), true, simDeck, simCanDraw);
      }
   }

   /**
    * Gets a random move from a given state.
    *
    * @param gameState a game state from which a random child state is desired.
    * @param simDeck the deck to be used for simulating, determines draw orders without affecting the player or node decks
    * @param randomize whether or not to begin simulating by randomly drawing a card
    * @param simCanDraw the canDraw boolean array to be used for simulating, determines which cards are still available to be drawn
    * @return a random child state of the given state.
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
      
      //calculate the highest scoring possible move from this state, then return that as the selected move.
      return g.getBestSimMove(simState, simCanDraw).get(0);
   }
   
   /**
    * Complete game setup steps that only need to be done once per point system.
    * 
    * @param pointSystem an array of scores for each of the possible hands in poker.
    * @param millis the time allotted to this method
    */
   public void setPointSystem(PokerSquaresPointSystem pointSystem, long millis) {
	   this.pointSystem = pointSystem;
	   g = new xMCTSPruningPPSGame(pointSystem);
	   //if this is the american point system
	   if (Arrays.equals(pointSystem.getScoreTable(), new int[] {0, 2, 5, 10, 15, 20, 25, 50, 75, 100})) {
		   C = AMERICANC;
	   }
	   else if (Arrays.equals(pointSystem.getScoreTable(), new int[] {0, 1, 4, 6, 8, 9, 13, 24, 47, 47})) {
		   C = AMERITISHC;
	   }
	   else if (Arrays.equals(pointSystem.getScoreTable(), new int[] {0, 1, 3, 6, 12, 5, 10, 16, 30, 30})) {
		   C = BRITISHC;
	   }
	   else {
		   C = OTHERC;
	   }
	   System.out.println("C value = " + C);
   }
   
	/**
	 * getName - gets the uniquely identifying name of this Poker Squares player.
	 * @return unique player name
	 */
   public String getName() {
	   return "xRandomRolloutPruningPlayer";
   }
}
